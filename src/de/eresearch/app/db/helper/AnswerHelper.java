package de.eresearch.app.db.helper;

import android.database.Cursor;
import android.util.Log;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.AnswersTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.ClosedAnswer;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.ScaleAnswer;

/**
 * Helper for all Answer model related objects
 * (Answer, ScaleAnswer, ClosedAnswer, OpenAnswer)
 * @author Jurij Wöhlke
 */
public class AnswerHelper extends AbstractObjectHelper<Answer> implements IdObjectHelperInterface<Answer>,QSortObjectHelperInterface<Answer>{

    /**
     * standard constructor
     * @param dbconn
     */
    public AnswerHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# QSortObjectHelperInterface methods #################
    /**
     * returns all Answers for this qsortId
     * @param int qsortId
     * @return Answer[] answers
     * @Override
     */
    public Answer[] getAllByQSortId(int id) {
        Answer[] result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+ AnswersTable.COLUMN_QSORT_ID+"=?"
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+ QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        result=this.getAnswersForCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the answer for this id
     * @param int answerId
     * @return Answer answer
     * @Override
     */
    public Answer getObjectById(int id) {
        Answer result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"." + QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE "+AnswersTable.TABLE_ANSWERS+"."+ AnswersTable.COLUMN_ID+"=?"
                + ";";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        Answer[] arrAnswer=this.getAnswersForCursor(cursor);
        if(arrAnswer!=null && arrAnswer.length>0){
            result=arrAnswer[0];
        }
        cursor.close();
        return result;
    }

    /**
     * delete the answer for this id from database
     * @param int answerId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        Object answer=this.getObjectById(id);
        try{
            if(answer!=null){
                if(answer instanceof OpenAnswer){
                    OpenAnswerHelper oah=(OpenAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_OPEN);
                    result=oah.deleteById(id);
                }else if(answer instanceof ClosedAnswer){
                    ClosedAnswerHelper cah=(ClosedAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_CLOSED);
                    result=cah.deleteById(id);
                }else if(answer instanceof ScaleAnswer){
                    ScaleAnswerHelper sah=(ScaleAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_SCALE);
                    result=sah.deleteById(id);
                }else{
                    Log.e("AnswerHelper", "deleteById() -Unknown Type of Answer");
                }
            }
        }catch(HelperNotFoundException e){
            e.printStackTrace();
            result=false;
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves an answer in database
     * @param Answer answer
     * @return Answer answer - returns null if fails
     * @Override
     */
    public Answer saveObject(Answer obj) {
        Answer result=null;
        try{
            if(obj instanceof OpenAnswer){
                OpenAnswerHelper oah=(OpenAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_OPEN);
                result=oah.saveObject((OpenAnswer)obj);
            }else if(obj instanceof ClosedAnswer){
                ClosedAnswerHelper cah=(ClosedAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_CLOSED);
                result=cah.saveObject((ClosedAnswer)obj);
            }else if(obj instanceof ScaleAnswer){
                ScaleAnswerHelper sah=(ScaleAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_SCALE);
                result=sah.saveObject((ScaleAnswer)obj);
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            result=null;
        }
        return result;
    }

    /**
     * reloads the answer from database
     * @param Answer answer
     * @return Answer answer
     * @Override
     */
    public Answer refreshObject(Answer obj) {
        if(obj!=null && obj.getId()>0){
            return this.getObjectById(obj.getId());
        }else{
            return null;
        }
    }

    /**
     * deletes the answer from database
     * @param Answer answer
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(Answer obj) {
        if(obj!=null && obj.getId()>0){
            return this.deleteById(obj.getId());
        }else{
            return false;
        }
    }

    // ################# additional methods #################

    /**
     * needs a cursor with all answer table columns and with question type
     * @param cursor
     * @return Answer[]
     */
    private Answer[] getAnswersForCursor(Cursor cursor) {
        Answer[] result=null;
        if(cursor.getCount()>0){
            try{
                result= new Answer[cursor.getCount()];
                int i=0;
                while(cursor.moveToNext()){
                    int id=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_ID));
                    int typeid=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTIONTYPES_ID));
                    if(typeid==EnumTable.ENUM_QUESTION_TYPE_OPEN){
                        OpenAnswerHelper oqh=(OpenAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_OPEN);
                        result[i]=oqh.getObjectById(id);
                    }else if(typeid==EnumTable.ENUM_QUESTION_TYPE_CLOSED){
                        ClosedAnswerHelper cqh=(ClosedAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_CLOSED);
                        result[i]=cqh.getObjectById(id);
                    }else if(typeid==EnumTable.ENUM_QUESTION_TYPE_SCALE){
                        ScaleAnswerHelper sqh=(ScaleAnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER_SCALE);
                        result[i]=sqh.getObjectById(id);
                    }
                    i++;
                }
            } catch (HelperNotFoundException e) {
                e.printStackTrace();
                result=null;
            }
        }
        return result;

    }
    
    /**
     * returns an array of answer for a certain qsort and a phase 
     * @param qSortId
     * @param phase
     * @return Answer
     */
    public Answer[] getAllByQSortIdAndPhase(int qSortId, Phase phase){
        Answer[] result=null;
        
        String isPost=null;
        if(phase==Phase.QUESTIONS_POST){
            isPost=Boolean.toString(true);
        }else if(phase==Phase.QUESTIONS_PRE){
            isPost=Boolean.toString(false);
        }
        
        if(isPost!=null){
            String[] selectionArgs={Integer.toString(qSortId),isPost};
            String statement=
                    "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                    + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                    + " FROM "+AnswersTable.TABLE_ANSWERS
                    + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                    + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                    + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+ AnswersTable.COLUMN_QSORT_ID+"=?"
                    + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_IS_AFTER_QSORT+"=?"
                    + ";";
            Cursor cursor=database.rawQuery(statement, selectionArgs);
            result=this.getAnswersForCursor(cursor);
            cursor.close();
        }else{
            Log.e("AnswerHelper", "getAllByQSortIdAndPhase() - Invalid Phase");
        }
        return result;
    }
    
    /**
     * returns a the answer for a certain question in the qsort
     * @param qSortId
     * @param questionId
     * @return Answer
     */
    public Answer getByQSortIdAndQuestionId(int qSortId, int questionId){
        Answer result=null;
        String[] selectionArgs={Integer.toString(qSortId),Integer.toString(questionId)};
        String statement="SELECT * FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+ AnswersTable.COLUMN_QSORT_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"=?"
                + ";";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        Answer[] arrAnswer=this.getAnswersForCursor(cursor);
        if(arrAnswer!=null && arrAnswer.length>0){
            result=arrAnswer[0];
        }
        cursor.close();
        return result;
    }
    
    /**
     * Save the answer to a question
     * 
     * @param qSortId the current QSort
     * @param questionId the Question
     * @return answerId if successful else -1;
     */
    public int saveByQuestionAndQSortId(Question q, int qSortId){
        int result=-1;
        if(q!=null && q.getAnswer()!=null && q.getId()>0 && qSortId>0){
            Answer answer=q.getAnswer();
            //set qsortid if not set:
            if(answer.getQSortId()<=0){
                answer.setQSortId(qSortId);
            }
            //set question if not set:
            if(answer.getQuestion()==null){
                Question tmp=q;
                tmp.setAnswer(null);
                answer.setQuestion(tmp);
            }
            //save answer:
            answer=this.saveObject(answer);
            if(answer!=null && answer.getId()>0){
                result=answer.getId();
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
