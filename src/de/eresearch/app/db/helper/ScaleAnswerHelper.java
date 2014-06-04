package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.AnswersTable;
import de.eresearch.app.db.tables.ClosedQuestionsAnswersTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.db.tables.ScaleQuestionAnswersTable;
import de.eresearch.app.db.tables.ScaleValuesTable;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Helper for ScaleAnswer model object
 * @author Jurij Wöhlke
 */
public class ScaleAnswerHelper extends AbstractObjectHelper<ScaleAnswer> implements IdObjectHelperInterface<ScaleAnswer>,QSortObjectHelperInterface<ScaleAnswer>{

    /**
     * standard constructor
     * @param dbconn
     */
    public ScaleAnswerHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# QSortObjectHelperInterface methods #################
    /**
     * returns all ScaleAnswers for this qSortId
     * @param int qsortid
     * @return ScaleAnswer[]
     * @Override
     */
    public ScaleAnswer[] getAllByQSortId(int id){
        ScaleAnswer[] result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QSORT_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_SCALE
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        result=this.getScaleAnswersForCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the ScaleAnswer for this scaleAnswerId
     * @param int scaleAnswerId
     * @return ScaleAnswer scaleAnswer - null if it fails
     * @Override
     */
    public ScaleAnswer getObjectById(int id){
        ScaleAnswer result=null;
        String[] selectionArgs={Integer.toString(id)};
        String statement=
                "SELECT "+AnswersTable.TABLE_ANSWERS+".*,"
                + QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID
                + " FROM "+AnswersTable.TABLE_ANSWERS
                + " INNER JOIN " + QuestionsTable.TABLE_QUESTIONS
                + " ON " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_ID+"="+AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_QUESTION_ID
                + " WHERE " +AnswersTable.TABLE_ANSWERS+"."+AnswersTable.COLUMN_ID+"=?"
                + " AND " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTIONTYPES_ID+"="+EnumTable.ENUM_QUESTION_TYPE_SCALE
                + " ORDER BY " +QuestionsTable.TABLE_QUESTIONS+"."+QuestionsTable.COLUMN_QUESTION_ORDER+" ASC;";
        Cursor cursor=database.rawQuery(statement, selectionArgs);
        ScaleAnswer[] answers=this.getScaleAnswersForCursor(cursor);
        if(answers!=null && answers.length>0){
            result=answers[0];
        }else{
            Log.i("ScaleAnswerHelper", "getObjectById() - scaleAnswer not found");
        }
        cursor.close();
        return result;
    }

    /**
     * deletes the ScaleAnswer in database
     * @param int scaleAnswerId
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        if(id>0){
            int res1=this.database.delete(ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER, ScaleQuestionAnswersTable.COLUMN_ANSWER_ID+"=?", new String[]{Integer.toString(id)});
            int res2=this.database.delete(AnswersTable.TABLE_ANSWERS, AnswersTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
            if(res1>=0 && res2>=0){
                result=true;
            }
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves the ScaleAnswer in database
     * @param ScaleAnswer scaleAnswer
     * @return ScaleAnswer scaleAnswer
     * @Override
     */
    public ScaleAnswer saveObject(ScaleAnswer scaleAnswer) {
        ScaleAnswer result=null;
        if(scaleAnswer!=null){
            int res1=-1;
            int res2=-1;
            
            ContentValues cValuesAnswerTable=new ContentValues();
            cValuesAnswerTable.put(AnswersTable.COLUMN_QSORT_ID,scaleAnswer.getQSortId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_QUESTION_ID,scaleAnswer.getQuestion().getId());
            cValuesAnswerTable.put(AnswersTable.COLUMN_TIME,scaleAnswer.getTime());
            
            //insert:
            if(scaleAnswer.getId()<=0){
                res1=(int) this.database.insert(AnswersTable.TABLE_ANSWERS, null, cValuesAnswerTable);
                if(res1>0){
                    scaleAnswer.setId(res1);
                    res2=1;//not needed in insert but in and after update
                }
            //update:
            }else{
                //delete old chosen "answers" for this answer:
                res1=this.database.delete(ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER, ClosedQuestionsAnswersTable.COLUMN_ANSWER_ID+"=?", new String[]{Integer.toString(scaleAnswer.getId())});
                res2=this.database.update(AnswersTable.TABLE_ANSWERS, cValuesAnswerTable, AnswersTable.COLUMN_ID+"=?",new String[]{Integer.toString(scaleAnswer.getId())});
            }
            if(res1>=0 && res2>=0){
                List<Scale> scales=scaleAnswer.getScales();
                for(int k=0;k<scales.size();k++){
                    int index=scales.get(k).getSelectedValueIndex();
                    if(index>=0){
                        String scaleAnswerValue=scales.get(k).getScaleValues().get(index);
                    
                        Cursor cursor=this.database.query(ScaleValuesTable.TABLE_SCALES_VALUES, ScaleValuesTable.ALL_COLUMNS, ScaleValuesTable.COLUMN_SCALE_VALUE+"=?", new String[]{scaleAnswerValue}, null, null, null);
                        if(cursor!=null && cursor.moveToNext()){
                            int scaleValueId=cursor.getInt(cursor.getColumnIndex(ScaleValuesTable.COLUMN_ID));
                            //save scaleValues:
                            ContentValues cValuesScaleAnswerTable=new ContentValues();
                            cValuesScaleAnswerTable.put(ScaleQuestionAnswersTable.COLUMN_SCALE_VALUE_ID,scaleValueId);
                            cValuesScaleAnswerTable.put(ScaleQuestionAnswersTable.COLUMN_ANSWER_ID,scaleAnswer.getId());
                            long resInsert=this.database.insert(ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER, null, cValuesScaleAnswerTable);
                            if(!(resInsert>0)){
                                Log.e("ScaleAnswerHelper", "saveObject() - Failed to insert into ScaleQuestionAnswerTable");
                                result=null;
                                break;
                            }
                        }else{
                            Log.e("ScaleAnswerHelper", "saveObject() -- ScaleValue for ScaleAnswer not found -- invalid Answer!");
                        }
                        cursor.close();
                    }else{
                        Log.i("ScaleAnswerHelper", "No ScaleAnswer selected");
                    }
                }
                result=scaleAnswer;
            }else{
                Log.e("ScaleAnswerHelper", "saveObject() - insert or update of answerTable failed");
            }
        }
        return result;
    }

    /**
     * reloads a ScaleAnswer from database
     * @param ScaleAnswer scaleAnswer
     * @return ScaleAnswer scaleAnswer
     * @Override
     */
    public ScaleAnswer refreshObject(ScaleAnswer obj) {
        ScaleAnswer result=null;
        if(obj!=null && obj.getId()>0){
            result=this.getObjectById(obj.getId());
        }
        return result;
    }

    /**
     * deletes a ScaleAnswer in database
     * @param ScaleAnswer scaleAnswer
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteObject(ScaleAnswer obj) {
        boolean result=false;
        if(obj!=null && obj.getId()>0){
            result=this.deleteById(obj.getId());
        }
        return result;
    }
    
    // ################# additional methods #################

    /**
     * needs a cursor with all scale answer table columns and with question type
     * @param cursor
     * @return ScaleAnswer[] 
     */
    private ScaleAnswer[] getScaleAnswersForCursor(Cursor cursor){
        ScaleAnswer[] result=null;
        try{
            ScaleQuestionHelper sqh = (ScaleQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_SCALE);
            ScaleHelper scaleHelper=(ScaleHelper)this.dbc.getHelper(DatabaseConnector.TYPE_SCALE);
            if(cursor!=null && cursor.getCount()>0){
                int i=0;
                result = new ScaleAnswer[cursor.getCount()];
                while(cursor.moveToNext() && i<result.length){
                    
                    int id=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_ID));
                    int questionId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QUESTION_ID));
                    int qsortId=cursor.getInt(cursor.getColumnIndex(AnswersTable.COLUMN_QSORT_ID));
                    
                    ScaleQuestion scaleQuestion = sqh.getObjectById(questionId);
                    //no scale question found!
                    if(scaleQuestion==null || scaleQuestion.getId()<=0){
                        Log.e("ScaleAnswerHelper", "getScaleAnswersForCursor() - scaleQuestion from DB is null");
                        result=null;
                        break;
                    }
                    result[i]=new ScaleAnswer(id,scaleQuestion, qsortId);
                    result[i].setTime(cursor.getLong(cursor.getColumnIndex(AnswersTable.COLUMN_TIME)));
                    
                    //get scale question answer
                    String statement=
                            "SELECT "+ScaleValuesTable.TABLE_SCALES_VALUES+"."+ScaleValuesTable.COLUMN_SCALE_ID+","
                            + ScaleValuesTable.TABLE_SCALES_VALUES+"."+ScaleValuesTable.COLUMN_SCALE_VALUE
                            + " FROM "+ ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER
                            + " LEFT JOIN " + ScaleValuesTable.TABLE_SCALES_VALUES
                            + " ON " +ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER+"."+ScaleQuestionAnswersTable.COLUMN_SCALE_VALUE_ID
                            + " = " + ScaleValuesTable.TABLE_SCALES_VALUES+"."+ScaleValuesTable.COLUMN_ID
                            + " WHERE " +ScaleQuestionAnswersTable.TABLE_SCALE_QUESTION_ANSWER+"."+ScaleQuestionAnswersTable.COLUMN_ANSWER_ID+"=?"
                            + " ORDER BY " +ScaleValuesTable.TABLE_SCALES_VALUES+"."+ScaleValuesTable.COLUMN_ORDER + " ASC"
                            + ";";
                    String[] args=new String[]{Integer.toString(id)};
                    Cursor cursor2=this.database.rawQuery(statement,args);
                    if(cursor2.getCount()<=0){
                        Log.e("ScaleAnswerHelper", "getScaleAnswersForCursor() -- Answer not found -- cursor2 is empty\n-- SQL Statement:\n"+statement+"\n params: answer_id:"+id);
                    }
                    List<Scale> answerScales=new ArrayList<Scale>();
                    while(cursor2.moveToNext()){
                        int scaleId=cursor2.getInt(cursor2.getColumnIndex(ScaleValuesTable.COLUMN_SCALE_ID));
                        String scaleValue=cursor2.getString(cursor2.getColumnIndex(ScaleValuesTable.COLUMN_SCALE_VALUE));
                        Scale tmp=scaleHelper.getObjectById(scaleId);
                        boolean success=false;
                        if(tmp!=null){
                            for(int k=0;k<tmp.getScaleValues().size();k++){
                                if(tmp.getScaleValues().get(k).equals(scaleValue)){
                                    tmp.setSelectedValueIndex(k);
                                    success=true;
                                    answerScales.add(tmp);
                                }
                            }
                        }
                        if(tmp==null || !success){
                            if(tmp==null){
                                Log.e("ScaleAnswerHelper", "getScaleAnswersForCursor() - scale from db is null");
                            }
                            if(!success){
                                Log.e("ScaleAnswerHelper", "getScaleAnswersForCursor() - no scaleValue is equal to answer which was saved");
                                Log.e("ScaleAnswerHelper", "getScaleAnswersForCursor() - getScaleValues().size():"+tmp.getScaleValues().size());
                            }
                            return null;
                        }
                    }
                    result[i].setScales(answerScales);
                    
                    cursor2.close();
                    
                    //increment counter
                    i++;
                }
            }else{
                Log.i("ScaleAnswerHelper", "getScaleAnswersForCursor() - cursor is null or empty");
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