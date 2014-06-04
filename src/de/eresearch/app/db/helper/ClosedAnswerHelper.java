package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.AnswersTable;
import de.eresearch.app.db.tables.CQPossibleAnswersTable;
import de.eresearch.app.db.tables.ClosedQuestionsAnswersTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.ClosedAnswer;
import de.eresearch.app.logic.model.ClosedQuestion;

/**
 * Helper for ClosedAnswer model object
 * @author Jurij Wöhlke
 */
public class ClosedAnswerHelper extends AbstractObjectHelper<ClosedAnswer> implements IdObjectHelperInterface<ClosedAnswer>,QSortObjectHelperInterface<ClosedAnswer>{

    /**
     * standard constructor
     * @param dbconn
     */
    public ClosedAnswerHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# QSortObjectHelperInterface methods #################
    /**
     * returns all ClosedAnswers for this qsort
     * @param int qSortId
     * @return ClosedAnswer[] closedAnswers
     * @Override
     */
    public ClosedAnswer[] getAllByQSortId(int id) {
        ClosedAnswer[] result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QSORT_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_CLOSED
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        result=this.getClosedAnswersForCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    
    /**
     * saves a ClosedAnswer in database
     * @param ClosedAnswer closedAnswers
     * @return ClosedAnswer closedAnswers
     * @Override
     */
    public ClosedAnswer saveObject(ClosedAnswer closedAnswer){
        ClosedAnswer result=null;
        if(closedAnswer!=null){
            int res1=-1;
            int res2=-1;
            
            ContentValues cValuesAnswerTable=new ContentValues();
            cValuesAnswerTable.put(AnswersTable.COLUMN_QSORT_ID,closedAnswer.getQSortId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_QUESTION_ID,closedAnswer.getQuestion().getId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_TIME,closedAnswer.getTime());
            
            //insert:
            if(closedAnswer.getId()<=0){
                res1=1;//not needed in this case, but has to be bigger than 0 (see update case)
                res2=(int) this.database.insert(AnswersTable.TABLE_ANSWERS, null, cValuesAnswerTable);
                if(res2>0){
                    closedAnswer.setId(res2);
                }
            //update:
            }else{
                //delete old chosen "answers" for this answer:
                res1=this.database.delete(ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS, ClosedQuestionsAnswersTable.COLUMN_ANSWER_ID+"=?", new String[]{Integer.toString(closedAnswer.getId())});
                if(res1>=0){
                    //update answer
                    res2=this.database.update(AnswersTable.TABLE_ANSWERS, cValuesAnswerTable, AnswersTable.COLUMN_ID+"=?",new String[]{Integer.toString(closedAnswer.getId())});
                }
            }
            //insert single answers:
            if(res1>=0 && res2>=0){
                result=closedAnswer;
                
                List<String> answers=closedAnswer.getAnswers();
                for(String answer:answers){
                    //insert values:
                    ContentValues cValuesClosedAnswerTable=new ContentValues();
                    cValuesClosedAnswerTable.put(ClosedQuestionsAnswersTable.COLUMN_ANSWER_ID,closedAnswer.getId());
                    
                    // search for answer in ClosedQuestion Possible Answers:
                    Cursor cursor=this.database.query(CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS, CQPossibleAnswersTable.ALL_COLUMNS, CQPossibleAnswersTable.COLUMN_ANSWER+" LIKE ?", new String[]{answer}, null, null, null);
                    
                    // if found entry, then closed answer:
                    if(cursor.moveToNext()){
                        cValuesClosedAnswerTable.put(ClosedQuestionsAnswersTable.COLUMN_CQ_ANSWER_ID,cursor.getInt(cursor.getColumnIndex(CQPossibleAnswersTable.COLUMN_ID)));
                    // if not, then open answer:
                    }else{
                        cValuesClosedAnswerTable.put(ClosedQuestionsAnswersTable.COLUMN_OPEN_ANSWER,answer);
                    }
                    
                    //save:
                    long res3=this.database.insert(ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS, null, cValuesClosedAnswerTable);
                    
                    // return null if insert fails:
                    if(!(res3>0)){
                        result=null;
                    }
                    
                    cursor.close();
                }
            }else{
                Log.e("ClosedAnswerHelper", "saveObject() - closedAnswer insert or updae of AnswersTable failed");
            }
            
        }
        return result;
    }

    /**
     * reloads a ClosedAnswer from database
     * @param ClosedAnswer closedAnswers
     * @return ClosedAnswer closedAnswers
     * @Override
     */
    public ClosedAnswer refreshObject(ClosedAnswer closedAnswer){
        ClosedAnswer result=null;
        if(closedAnswer!=null && closedAnswer.getId()>0){
            result=this.getObjectById(closedAnswer.getId());
        }
        return result;
    }

    /**
     * deletes a ClosedAnswer from database
     * @param ClosedAnswer closedAnswers
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(ClosedAnswer closedAnswer){
        boolean result=false;
        if(closedAnswer!=null && closedAnswer.getId()>0){
            result=this.deleteById(closedAnswer.getId());
        }
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * loads a ClosedAnswer by id
     * @param int closedAnswerId
     * @return ClosedAnswer closedAnswers
     * @Override
     */
    public ClosedAnswer getObjectById(int id) {
        ClosedAnswer result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_CLOSED
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        ClosedAnswer[] answers=this.getClosedAnswersForCursor(cursor);
        if(answers!=null && answers.length>0){
            result=answers[0];
        }else{
            Log.i("ClosedAnswerHelper", "getObjectById() - closedAnswer not found");
        }
        cursor.close();
        return result;
    }
    
    /**
     * deletes a ClosedAnswer from database by id
     * @param int closedAnswerId
     * @return boolean - returns true if sucess else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        if(id>0){
            int res1=this.database.delete(ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS, ClosedQuestionsAnswersTable.COLUMN_ANSWER_ID+"=?", new String[]{Integer.toString(id)});
            int res2=this.database.delete(AnswersTable.TABLE_ANSWERS, AnswersTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
            if(res1>=0 && res2>=0){
                result=true;
            }else{
                Log.e("ClosedAnswerHelper", "deleteById() - delete of closedAnswer failed (id:"+id+")");
            }
        }
        return result;
    }
    
    // ################# additional methods #################

    /**
     * needs a cursor with all closed answer table columns and with question type
     * @param cursor
     * @return ClosedAnswer[]
     */
    private ClosedAnswer[] getClosedAnswersForCursor(Cursor cursor) {
        ClosedAnswer[] result=null;
        try{
            ClosedQuestionHelper cqh = (ClosedQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_CLOSED);
            if(cursor!=null && cursor.getCount()>0){
                int i=0;
                result = new ClosedAnswer[cursor.getCount()];
                while(cursor.moveToNext() && i<result.length){
                    
                    int id=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_ID));
                    int questionId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QUESTION_ID));
                    int qsortId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QSORT_ID));
                    
                    ClosedQuestion closedQuestion = cqh.getObjectById(questionId);
                    //no closed question found!
                    if(closedQuestion==null || closedQuestion.getId()<=0){
                        Log.e("ClosedAnswerHelper", "getClosedAnswersForCursor() - no closedQuestion found in db");
                        result=null;
                        break;
                    }
                    
                    result[i]=new ClosedAnswer(id,closedQuestion, qsortId);
                    result[i].setTime(cursor.getLong(cursor.getColumnIndex(AnswersTable.COLUMN_TIME)));

                    //get closed questions answers
                    String statement=
                            "SELECT * FROM "+ ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS
                            + " LEFT JOIN " + CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS
                            + " ON " +ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS+"."+ClosedQuestionsAnswersTable.COLUMN_CQ_ANSWER_ID
                            + " = " +CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS+"."+CQPossibleAnswersTable.COLUMN_ID
                            + " WHERE " +ClosedQuestionsAnswersTable.TABLE_CLOSED_QUESTIONS_ANSWERS+"."+ ClosedQuestionsAnswersTable.COLUMN_ANSWER_ID+"=?"
                            + " ORDER BY " +CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS+"."+CQPossibleAnswersTable.COLUMN_ORDER + " ASC"
                            + ";";
                    String[] args=new String[]{Integer.toString(id)};
                    Cursor cursor2=this.database.rawQuery(statement,args);
                    List<String> answers=new ArrayList<String>();
                    while(cursor2.moveToNext()){
                        if(!cursor2.isNull(cursor2.getColumnIndex(CQPossibleAnswersTable.COLUMN_ANSWER))){
                            answers.add(cursor2.getString(cursor2.getColumnIndex(CQPossibleAnswersTable.COLUMN_ANSWER)));
                        }else if(!cursor2.isNull(cursor2.getColumnIndex(ClosedQuestionsAnswersTable.COLUMN_OPEN_ANSWER))){
                            answers.add(cursor2.getString(cursor2.getColumnIndex(ClosedQuestionsAnswersTable.COLUMN_OPEN_ANSWER)));
                        }else{
                            //if no answer in row, then something is wrong!
                            Log.e("ClosedAnswerHelper", "getClosedAnswersForCursor() - no answer found in row, something is definitely wrong");
                            answers=null;
                            break;
                        }
                    }
                    cursor2.close();
                    
                    result[i].setAnswers(answers);
                    //increment counter
                    i++;
                }
            }else{
                Log.i("ClosedAnswerHelper", "getClosedAnswersForCursor() - cursor is null or empty");
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            result=null;
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
