package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.db.tables.ScaleQuestionsTable;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Helper for ScaleQuestion model object
 * @author Jurij Wöhlke
 */
public class ScaleQuestionHelper extends AbstractObjectHelper<ScaleQuestion> implements IdObjectHelperInterface<ScaleQuestion>,StudyObjectHelperInterface<ScaleQuestion>{

    /**
     * standard constructor
     * @param dbconn
     */
    public ScaleQuestionHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all ScaleQuestions for this study id
     * @param int studyid
     * @return ScaleQuestion[]
     * @Override
     */
    public ScaleQuestion[] getAllByStudyId(int id) {
        ScaleQuestion[] result=null;
        if(id>0){
            String statement = "SELECT * FROM " + QuestionsTable.TABLE_QUESTIONS
                    + " INNER JOIN " + ScaleQuestionsTable.TABLE_SCALE_QUESTIONS
                    + " ON " + QuestionsTable.COLUMN_ID+"="+ScaleQuestionsTable.COLUMN_QUESTION_ID
                    + " WHERE " + QuestionsTable.COLUMN_STUDY_ID + "=?";
            Cursor cursor=this.database.rawQuery(statement, new String[]{Integer.toString(id)});
            result=this.fromCursorToScaleQuestions(cursor);
            cursor.close();
        }
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the ScaleQuestion for this scale question id
     * @param int scaleQuestionId
     * @return ScaleQuestion
     * @Override
     */
    public ScaleQuestion getObjectById(int id) {
        ScaleQuestion result=null;
        if(id>0){
            String statement = "SELECT * FROM " + QuestionsTable.TABLE_QUESTIONS
                    + " INNER JOIN " + ScaleQuestionsTable.TABLE_SCALE_QUESTIONS
                    + " ON " + QuestionsTable.COLUMN_ID+"="+ScaleQuestionsTable.COLUMN_QUESTION_ID
                    + " WHERE " + QuestionsTable.COLUMN_ID + "=?";
            Cursor cursor=this.database.rawQuery(statement, new String[]{Integer.toString(id)});
            ScaleQuestion[] tmp=this.fromCursorToScaleQuestions(cursor);            
            if(tmp!=null && tmp.length>0 && tmp[0]!=null){
                result=tmp[0];
            }
            cursor.close();
        }
        return result;
    }

    /**
     * deletes the ScaleQuestion with this id
     * @param int scaleQuestionId
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=true;
        try {
            if(id>0){
                int res1=this.database.delete(ScaleQuestionsTable.TABLE_SCALE_QUESTIONS, ScaleQuestionsTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(id)});
                if(res1<0){
                    result=false;
                }
                
                ScaleQuestion questionToDelete=this.getObjectById(id);
                if(questionToDelete!=null){
                    List<Scale> scales=questionToDelete.getScales();
                    ScaleHelper scaleHelper=(ScaleHelper)this.dbc.getHelper(DatabaseConnector.TYPE_SCALE);
                    for(int i=0;i<scales.size();i++){
                        if(!(scaleHelper.deleteObject(scales.get(i)) && result)){
                            result=false;
                        }
                    }
                }
                
                int res2=this.database.delete(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
                if(res2<0){
                    result=false;
                }
            }else{
                result=false;
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            result=false;
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves a ScaleQuestion
     * @param ScaleQuestion scaleQuestion
     * @return ScaleQuestion - null if save fail
     * @Override
     */
    public ScaleQuestion saveObject(ScaleQuestion obj){
        ScaleQuestion result=null;
        try{
            if(obj!=null){
                ScaleHelper scaleHelper=(ScaleHelper)this.dbc.getHelper(DatabaseConnector.TYPE_SCALE);
                
                ContentValues cValuesQuestionsTable=new ContentValues();
                cValuesQuestionsTable.put(QuestionsTable.COLUMN_IS_AFTER_QSORT, Boolean.toString(obj.isPost()));
                cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTION,obj.getText());
                cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTION_ORDER,obj.getOrderNumber());
                cValuesQuestionsTable.put(QuestionsTable.COLUMN_QUESTIONTYPES_ID,EnumTable.ENUM_QUESTION_TYPE_SCALE);
                cValuesQuestionsTable.put(QuestionsTable.COLUMN_STUDY_ID,obj.getStudyId());
                
                ContentValues cValuesScale=new ContentValues();
                //DEPRECATED: cValuesScale.put(ScaleQuestionsTable.COLUMN_SCALE_ID,scale.getId());
                
                //insert
                if(obj.getId()<=0){
                    //insert into table questions
                    long res1=this.database.insert(QuestionsTable.TABLE_QUESTIONS, null, cValuesQuestionsTable);
                    //insert into table ScaleQuestions
                    if(res1>0){
                        //set id in obj:
                        obj.setId((int) res1);
                        //set question id in scale question row:
                        cValuesScale.put(ScaleQuestionsTable.COLUMN_QUESTION_ID,obj.getId());
                        //insert row
                        long res2=this.database.insert(ScaleQuestionsTable.TABLE_SCALE_QUESTIONS, null, cValuesScale);
                        if(res2==res1){
                            result=obj;
                        }
                    }
                //update
                }else{
                    int res=this.database.update(QuestionsTable.TABLE_QUESTIONS, cValuesQuestionsTable, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(obj.getId())});
                    if(res>=0){
                        /* removed beacuse of DEPRECATED COLUMN in SCALEQUESTIONSTable:
                        this.database.update(ScaleQuestionsTable.TABLE_SCALE_QUESTIONS, cValuesScale, ScaleQuestionsTable.COLUMN_QUESTION_ID+"=?", new String[]{Integer.toString(obj.getId())});
                        if(res>=0){*/
                            result=obj;
                        /*}*/
                    }
                }
                
                //save scales
                if(result!=null && result.getId()>0){
                    //delete old scales
                    Scale[] oldScales=scaleHelper.getAllByScaleQuestionId(result.getId());
                    List<Scale> scales=obj.getScales();
                    if(oldScales!=null && oldScales.length>0){
                        //delete scales which do not exist anymore:
                        for(int j=0;j<oldScales.length;j++){
                            boolean stillExists=false;
                            for(int k=0;k<scales.size();k++){
                                if(oldScales[j].getId()==scales.get(k).getId()){
                                    stillExists=true;
                                }
                            }
                            if(!stillExists && oldScales[j]!=null){
                                scaleHelper.deleteObject(oldScales[j]);
                            }
                        }
                    }
                    //add/update/save scales:
                    Scale scale=null;
                    if(scales!=null && scales.size()>0){
                        for(int k=0;k<scales.size();k++){
                            scale=scales.get(k);
                            if(scale.getQuestionId()<=0){
                                scale.setQuestionId(result.getId());
                            }
                            scale.setDBInternalOrderOfScales(k);
                            scale=scaleHelper.saveObject(scale);
                            if(scale==null){
                                Log.e("ScaleQuestionHelper", "saveObject() -- failed to save scale!");
                            }
                        }
                    }
                }else{
                    Log.e("ScaleQuestionHelper", "saveObject() -- failed to save question -- result is empty and scales will not be saved");
                }
            }
        }catch(HelperNotFoundException e){
            e.printStackTrace();
            result=null;
        }
        return result;
    }

    /**
     * returns a new ScaleQuestion object
     * @param ScaleQuestion scaleQuestion
     * @return ScaleQuestion
     * @Override
     */
    public ScaleQuestion refreshObject(ScaleQuestion obj) {
        if(obj!=null && obj.getId()>0){
            return this.getObjectById(obj.getId());
        }else{
            return null;
        }
    }

    /**
     * deletes this ScaleQuestion from database
     * @param ScaleQuestion scaleQuestion
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteObject(ScaleQuestion obj) {
        if(obj!=null && obj.getId()>0){
            return this.deleteById(obj.getId());
        }else{
            return false;
        }
    }
    
    // ################# additional methods #################
    /**
     * returns an array of full ScaleQuestions for a Cursor
     * needs all columns from QuestionsTable and ScaleQuestionsTable
     * @param cursor
     * @return ScaleQuestion[] - null if fails
     */
    public ScaleQuestion[] fromCursorToScaleQuestions(Cursor cursor){
        ScaleQuestion[] result=null;
        try {
            ScaleHelper scaleHelper=(ScaleHelper)this.dbc.getHelper(DatabaseConnector.TYPE_SCALE);
            if(cursor!=null && cursor.getCount()>0){
                int i=0;
                result = new ScaleQuestion[cursor.getCount()];
                while(cursor.moveToNext() && i<result.length){
                    
                    int id=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ID));
                    int studyid=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_STUDY_ID));
                    result[i]=new ScaleQuestion(id, studyid);
    
                    result[i].setOrderNumber(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION_ORDER)));
                    result[i].setText(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                    result[i].setIsPost(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_IS_AFTER_QSORT))));
                    
                    //DEPRECATED: Scale scale=scaleHelper.getObjectById(cursor.getInt(cursor.getColumnIndex(ScaleQuestionsTable.COLUMN_SCALE_ID)));
                    
                    Scale[] scales=scaleHelper.getAllByScaleQuestionId(id);
                    
                    if(scales!=null && scales.length>0){
                        for(int k=0;k<scales.length;k++){
                            result[i].addScale(scales[k]);
                        }
                    }else{
                        Log.d("ScaleQuestionHelper", "fromCursorToScaleQuestions() - Scale is null");
                        result=null;
                        break;
                    }
                    
                    //increment counter
                    i++;
                }
                
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
