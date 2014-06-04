package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.CQPossibleAnswersTable;
import de.eresearch.app.db.tables.ClosedQuestionsTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.ClosedQuestion;

/**
 * Helper for ClosedQuestion model object
 * @author Jurij Wöhlke
 */
public class ClosedQuestionHelper extends AbstractObjectHelper<ClosedQuestion> implements IdObjectHelperInterface<ClosedQuestion>,StudyObjectHelperInterface<ClosedQuestion>{

    /**
     * standard constructor
     * @param dbconn
     */
    public ClosedQuestionHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all ClosedQuestion for this study id
     * @param int studyId
     * @return ClosedQuestion[] closedQuestions
     * @Override
     */
    public ClosedQuestion[] getAllByStudyId(int id) {
        ClosedQuestion[] result=null;
        
        String statement = "SELECT * FROM " + QuestionsTable.TABLE_QUESTIONS
                + " INNER JOIN " + ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS
                + " ON " + QuestionsTable.COLUMN_ID+"="+ClosedQuestionsTable.COLUMN_QUESTION_ID
                + " WHERE " + QuestionsTable.COLUMN_STUDY_ID + "=?";
        Cursor cursor=this.database.rawQuery(statement, new String[]{Integer.toString(id)});
        result=this.fromCursorToClosedQuestions(cursor);
        cursor.close();

        return result;
    }
    
 // ################# AbstractObjectHelper methods #################
    /**
     * saves the ClosedQuestion in database
     * @param ClosedQuestion closedQuestion
     * @return ClosedQuestion closedQuestion - null if fails
     * @Override
     */
    public ClosedQuestion saveObject(ClosedQuestion closedQuestion){
        ClosedQuestion result=null;
        if(closedQuestion!=null){
            ContentValues cValuesQuestionsTable=new ContentValues();
            cValuesQuestionsTable.put(QuestionsTable.COLUMN_IS_AFTER_QSORT, Boolean.toString(closedQuestion.isPost()));
            cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTION,closedQuestion.getText());
            cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTION_ORDER,closedQuestion.getOrderNumber());
            cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTIONTYPES_ID,EnumTable.ENUM_QUESTION_TYPE_CLOSED);
            cValuesQuestionsTable.put(QuestionsTable.COLUMN_STUDY_ID,closedQuestion.getStudyId());
            
            ContentValues cValuesClosedQuestionsTable=new ContentValues();
            cValuesClosedQuestionsTable.put(ClosedQuestionsTable.COLUMN_HAS_OPEN_ANSWER,Boolean.toString(closedQuestion.hasOpenField()));
            cValuesClosedQuestionsTable.put(ClosedQuestionsTable.COLUMN_IS_MULTIPLE_CHOICE, Boolean.toString(closedQuestion.isMultipleChoice()) );
            
            //insert
            if(closedQuestion.getId()<=0){
                long res1=this.database.insert(QuestionsTable.TABLE_QUESTIONS, null, cValuesQuestionsTable);
                
                cValuesClosedQuestionsTable.put(ClosedQuestionsTable.COLUMN_QUESTION_ID,res1);
                long res2=this.database.insert(ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS, null, cValuesClosedQuestionsTable);
                
                if(res1==res2){
                    closedQuestion.setId((int) res1);
                    
                    List<String> closedquestionanswers=closedQuestion.getPossibleAnswers();
                    for(int i=0;i<closedquestionanswers.size();i++){
                        String answer=closedquestionanswers.get(i);
                        
                        ContentValues cValuesClosedQuestionsAnswer= new ContentValues();
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_ANSWER, answer);
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_ORDER,i);
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_QUESTION_ID,closedQuestion.getId());
                        
                        this.database.insert(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, null, cValuesClosedQuestionsAnswer);
                    }
                    result=closedQuestion;
                }
            //update
            }else{
                int res1=this.database.update(QuestionsTable.TABLE_QUESTIONS, cValuesQuestionsTable, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(closedQuestion.getId())});

                int res2=this.database.update(ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS, cValuesClosedQuestionsTable, ClosedQuestionsTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(closedQuestion.getId())});

                if(res1>=0 && res2>=0){
                    this.database.delete(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, CQPossibleAnswersTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(closedQuestion.getId())});
                    
                    List<String> closedquestionanswers=closedQuestion.getPossibleAnswers();
                    for(int i=0;i<closedquestionanswers.size();i++){
                        String answer=closedquestionanswers.get(i);
                        
                        ContentValues cValuesClosedQuestionsAnswer= new ContentValues();
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_ANSWER, answer);
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_ORDER,i);
                        cValuesClosedQuestionsAnswer.put(CQPossibleAnswersTable.COLUMN_QUESTION_ID,closedQuestion.getId());
                        
                        this.database.insert(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, null, cValuesClosedQuestionsAnswer);
                    }
                    result=closedQuestion;
                }
            }
        }
        return result;
    }

    /**
     * reloads the ClosedQuestion from database
     * @param ClosedQuestion closedQuestion
     * @return ClosedQuestion closedQuestion
     * @Override
     */
    public ClosedQuestion refreshObject(ClosedQuestion closedQuestion){
        if(closedQuestion!=null && closedQuestion.getId()>0){
            return this.getObjectById(closedQuestion.getId());
        }else{
            return null;
        }
    }

    /**
     * delete this ClosedQuestion from database
     * @param ClosedQuestion closedQuestion
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(ClosedQuestion closedQuestion){
        if(closedQuestion!=null && closedQuestion.getId()>0){
            return this.deleteById(closedQuestion.getId());
        }else{
            return false;
        }
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the ClosedQuestion for this id
     * @param int closedQuestionId
     * @return ClosedQuestion closedQuestion
     * @Override
     */
    public ClosedQuestion getObjectById(int id) {
        ClosedQuestion result=null;
        
        String statement = "SELECT * FROM " + QuestionsTable.TABLE_QUESTIONS
                + " INNER JOIN " + ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS
                + " ON " + QuestionsTable.COLUMN_ID+"="+ClosedQuestionsTable.COLUMN_QUESTION_ID
                + " WHERE " + QuestionsTable.COLUMN_ID + "=?";
        Cursor cursor=this.database.rawQuery(statement, new String[]{Integer.toString(id)});
        
        ClosedQuestion[] tmp=this.fromCursorToClosedQuestions(cursor);
        if(tmp!=null && tmp.length>0 && tmp[0]!=null){
            result=tmp[0];
        }
        cursor.close();
        
        return result;
    }

    /**
     * deletes the ClosedQuestion from database by id
     * @param int closedQuestionId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean res=false;
        
        int res1=this.database.delete(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, CQPossibleAnswersTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(id)});
        int res2=this.database.delete(ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS, ClosedQuestionsTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(id)});
        int res3=this.database.delete(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
        
        if(res1>=0 && res2>=0 && res3>=0){
            res=true;
        }
        return res;
    }
    
    // ################# additional methods #################
    
    /**
     * returns an array of ClosedQuestions for this cursor
     * the cursor needs to have all columns of
     * QuestionsTable and ClosedQuestionsTable with Join On QuestionId
     * @param Cursor cursor
     * @return ClosedQuestion[] closedQuestions
     */
    public ClosedQuestion[] fromCursorToClosedQuestions(Cursor cursor){
        ClosedQuestion[] result=null;
        if(cursor!=null && cursor.getCount()>0){
            int i=0;
            result = new ClosedQuestion[cursor.getCount()];
            while(cursor.moveToNext() && i<result.length){
                
                int id=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ID));
                int studyid=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_STUDY_ID));
                result[i]=new ClosedQuestion(id, studyid);

                result[i].setOrderNumber(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION_ORDER)));
                result[i].setText(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                result[i].setIsPost(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_IS_AFTER_QSORT))));
                
                result[i].setMultipleChoice(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ClosedQuestionsTable.COLUMN_IS_MULTIPLE_CHOICE))));
                result[i].setOpenField(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ClosedQuestionsTable.COLUMN_HAS_OPEN_ANSWER))));
                
                //get closed questions possible answers
                Cursor cursor2=this.database.query(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, CQPossibleAnswersTable.ALL_COLUMNS, CQPossibleAnswersTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(result[i].getId())}, null, null, CQPossibleAnswersTable.COLUMN_ORDER+" ASC");
                while(cursor2.moveToNext()){
                    result[i].addPossibleAnswer(cursor2.getString(cursor2.getColumnIndex(CQPossibleAnswersTable.COLUMN_ANSWER)));
                }
                cursor2.close();
                
                //increment counter
                i++;
            }
        }
        return result;
    }
    
    // ################# getter and setter ################# 
    @Override
    public DatabaseConnector getDbc() {
        return dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}
