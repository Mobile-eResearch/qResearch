package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.AnswersTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.OpenQuestionAnswersTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.OpenQuestion;

/**
 * Helper for OpenAnswer model object
 * @author Jurij Wöhlke
 */
public class OpenAnswerHelper extends AbstractObjectHelper<OpenAnswer> implements IdObjectHelperInterface<OpenAnswer>,QSortObjectHelperInterface<OpenAnswer>{

    /**
     * standard constructor
     * @param dbconn
     */
    public OpenAnswerHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# QSortObjectHelperInterface methods #################
    /**
     * returns all OpenAnswers for this QSortId
     * @param int qSortId
     * @return OpenAnswer[] openAnswers - null if it fails
     * @Override
     */
    public OpenAnswer[] getAllByQSortId(int id) {
        OpenAnswer[] result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QSORT_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_OPEN
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        result=this.getOpenAnswersForCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the OpenAnswer for this id
     * @param int openAnswerId
     * @return OpenAnswer openAnswer
     * @Override
     */
    public OpenAnswer getObjectById(int id) {
        OpenAnswer result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+ AnswersTable.COLUMN_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_OPEN
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        OpenAnswer[] answers=this.getOpenAnswersForCursor(cursor);
        if(answers!=null && answers.length>0){
            result=answers[0];
        }else{
            Log.e("OpenAnswerHelper", "getObjectById() - no openAnswer found in db for id:"+id);
        }
        cursor.close();
        return result;
    }

    /**
     * delete OpenAnswer in database
     * @param int openAnswerId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        if(id>0){
            int res1=this.database.delete(OpenQuestionAnswersTable.TABLE_OPEN_QUESTION_ANSWERS_TABLE, OpenQuestionAnswersTable.COLUMN_ANSWER_ID+"=?", new String[]{Integer.toString(id)});
            int res2=this.database.delete(AnswersTable.TABLE_ANSWERS, AnswersTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
            if(res1>=0 && res2>=0){
                result=true;
            }else{
                Log.e("OpenAnswerHelper", "deleteById() - delete of openQuestion failed");
            }
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves the OpenAnswer object in database
     * @param OpenAnswer openAnswer
     * @return OpenAnswer openAnswer
     * @Override
     */
    public OpenAnswer saveObject(OpenAnswer openAnswer) {
        OpenAnswer result=null;
        if(openAnswer!=null){
            ContentValues cValuesAnswerTable=new ContentValues();
            cValuesAnswerTable.put(AnswersTable.COLUMN_QSORT_ID,openAnswer.getQSortId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_QUESTION_ID,openAnswer.getQuestion().getId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_TIME,openAnswer.getTime());
            
            ContentValues cValuesOpenAnswerTable=new ContentValues();
            cValuesOpenAnswerTable.put(OpenQuestionAnswersTable.COLUMN_ANSWER,openAnswer.getAnswer());
            
            //insert:
            if(openAnswer.getId()<=0){
                int res1=(int) this.database.insert(AnswersTable.TABLE_ANSWERS, null, cValuesAnswerTable);
                if(res1>0){
                    openAnswer.setId(res1);
                    cValuesOpenAnswerTable.put(OpenQuestionAnswersTable.COLUMN_ANSWER_ID,openAnswer.getId());
                    int res2=(int) this.database.insert(OpenQuestionAnswersTable.TABLE_OPEN_QUESTION_ANSWERS_TABLE, null, cValuesOpenAnswerTable);
                    if(res2==res1){
                        result=openAnswer;
                    }else{
                        Log.e("OpenAnswerHelper", "saveObject() - insert failed ids not equal, but should be. Id1:"+res1+" Id2"+res2);
                    }
                }else{
                    Log.e("OpenAnswerHelper", "saveObject() - insert into AnswersTable failed");
                }
            //update:
            }else{
                cValuesOpenAnswerTable.put(OpenQuestionAnswersTable.COLUMN_ANSWER_ID,openAnswer.getId());
                int res1=this.database.update(AnswersTable.TABLE_ANSWERS, cValuesAnswerTable, AnswersTable.COLUMN_ID+"=?",new String[]{Integer.toString(openAnswer.getId())});
                int res2=this.database.update(OpenQuestionAnswersTable.TABLE_OPEN_QUESTION_ANSWERS_TABLE, cValuesOpenAnswerTable, OpenQuestionAnswersTable.COLUMN_ANSWER_ID+"=?",new String[]{Integer.toString(openAnswer.getId())});
                if(res1>=0 && res2>=0){
                    result=openAnswer;
                }else{
                    Log.e("OpenAnswerHelper", "saveObject() - update failed");
                }
            }
        }
        return result;
    }

    /**
     * reloads the OpenAnswer from database
     * @param OpenAnswer openAnswer
     * @return OpenAnswer openAnswer
     * @Override
     */
    public OpenAnswer refreshObject(OpenAnswer obj) {
        OpenAnswer result=null;
        if(obj!=null && obj.getId()>0){
            result=this.getObjectById(obj.getId());
        }
        return result;
    }

    /**
     * deletes the OpenAnswer from database
     * @param OpenAnswer openAnswer
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(OpenAnswer obj) {
        boolean result=false;
        if(obj!=null && obj.getId()>0){
            result=this.deleteById(obj.getId());
        }
        return result;
    }
    
    // ################# additional methods #################

    /**
     * needs a cursor with all open answer table columns and with question type
     * @param cursor
     * @return OpenAnswer[]
     */
    private OpenAnswer[] getOpenAnswersForCursor(Cursor cursor) {
        OpenAnswer[] result=null;
        try{
            OpenQuestionHelper oqh = (OpenQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_OPEN);
            if(cursor!=null && cursor.getCount()>0){
                int i=0;
                result = new OpenAnswer[cursor.getCount()];
                while(cursor.moveToNext() && i<result.length){
                    
                    int id=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_ID));
                    int questionId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QUESTION_ID));
                    int qsortId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QSORT_ID));
                    
                    OpenQuestion openQuestion = oqh.getObjectById(questionId);
                    //no open question found!
                    if(openQuestion==null || openQuestion.getId()<=0){
                        Log.e("OpenAnswerHelper", "getOpenAnswersForCursor() - openQuestion from db is empty");
                        result=null;
                        break;
                    }
                    
                    result[i]=new OpenAnswer(id,openQuestion, qsortId);
                    result[i].setTime(cursor.getLong(cursor.getColumnIndex(AnswersTable.COLUMN_TIME)));
                    
                    //get open question answer
                    String[] args=new String[]{Integer.toString(id)};
                    Cursor cursor2=this.database.query(OpenQuestionAnswersTable.TABLE_OPEN_QUESTION_ANSWERS_TABLE, OpenQuestionAnswersTable.ALL_COLUMNS, OpenQuestionAnswersTable.COLUMN_ANSWER_ID+"=?", args, null, null, null);
                    if(cursor2.moveToNext()){
                        String answer=cursor2.getString(cursor2.getColumnIndex(OpenQuestionAnswersTable.COLUMN_ANSWER));
                        result[i].setAnswer(answer);
                    }
                    cursor2.close();
                    
                    //increment counter
                    i++;
                }
            }else{
                Log.i("OpenAnswerHelper", "getOpenAnswersForCursor() - cursor is empty or null");
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
