package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.OpenQuestion;

/**
 * Helper for OpenQuestion model object
 * @author Jurij Wöhlke
 */
public class OpenQuestionHelper extends AbstractObjectHelper<OpenQuestion> implements IdObjectHelperInterface<OpenQuestion>,StudyObjectHelperInterface<OpenQuestion>{

    /**
     * standard constructor
     * @param dbconn
     */
    public OpenQuestionHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

 // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all OpenQuestions for this studyId
     * @param int studyId
     * @return OpenQuestion[]
     * @Override
     */
    public OpenQuestion[] getAllByStudyId(int id) {
        OpenQuestion[] result=null;
        Cursor cursor=database.query(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.ALL_COLUMNS, QuestionsTable.COLUMN_STUDY_ID+"=? AND "+QuestionsTable.COLUMN_QUESTIONTYPES_ID, new String[]{Integer.toString(id),Integer.toString(EnumTable.ENUM_QUESTION_TYPE_OPEN)}, null, null, QuestionsTable.COLUMN_QUESTION_ORDER+" ASC");
        result=this.fromCursorToOpenQuestions(cursor);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the OpenQuestion for this id
     * @param int openQuestionId
     * @return OpenQuestion openQuestion
     * @Override
     */
    public OpenQuestion getObjectById(int id) {
        OpenQuestion result=null;
        Cursor cursor=this.database.query(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.ALL_COLUMNS, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)}, null, null, QuestionsTable.COLUMN_QUESTION_ORDER+" ASC");
        OpenQuestion[] tmp=this.fromCursorToOpenQuestions(cursor);
        if(tmp!=null && tmp.length>0){
            result=tmp[0];
        }
        cursor.close();
        return result;
    }

    /**
     * deletes the openQuestion for this id
     * @param int openQuestionId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        long res=this.database.delete(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
        if(res>=0){
            result = true;
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves the openquestion in database
     * @param OpenQuestion openQuestion
     * @return OpenQuestion openQuestion
     * @Override
     */
    public OpenQuestion saveObject(OpenQuestion obj) {
        OpenQuestion result=null;
        if(obj!=null){
            ContentValues cValues=new ContentValues();
            
            cValues.put(QuestionsTable.COLUMN_IS_AFTER_QSORT, Boolean.toString(obj.isPost()));
            cValues.put(QuestionsTable.COLUMN_QUESTION, obj.getText());
            cValues.put(QuestionsTable.COLUMN_STUDY_ID, obj.getStudyId());
            cValues.put(QuestionsTable.COLUMN_QUESTION_ORDER,obj.getOrderNumber());
            cValues.put(QuestionsTable.COLUMN_QUESTIONTYPES_ID,EnumTable.ENUM_QUESTION_TYPE_OPEN);

            //insert
            if(obj.getId()<=0){
                long res=this.database.insert(QuestionsTable.TABLE_QUESTIONS, null, cValues);
                obj.setId((int) res);
                result=obj;
            //update
            }else{
                long res=this.database.update(QuestionsTable.TABLE_QUESTIONS, cValues, QuestionsTable.COLUMN_ID+"=?", new String[]{Integer.toString(obj.getId())});
                if(res>0){
                   result=obj;
                }
            }
        }
        return result;
    }

    /**
     * reloads the OpenQuestion from database
     * @param OpenQuestion openQuestion
     * @return OpenQuestion openQuestion
     * @Override
     */
    public OpenQuestion refreshObject(OpenQuestion obj) {
        OpenQuestion res=null;
        if(obj!=null){
            res = this.getObjectById(obj.getId());
        }
        return res;
    }

    /**
     * deletes the OpenQuestion in database
     * @param OpenQuestion openQuestion
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(OpenQuestion obj) {
        boolean result=false;
        if(obj!=null){
            result=this.deleteById(obj.getId());
        }
        return result;
    }

    // ################# additional methods #################
    /**
     * returns an array of OpenQuestions for this cursor
     * needs all columns in cursor from QuestionsTable
     * @param cursor
     * @return
     */
    public OpenQuestion[] fromCursorToOpenQuestions(Cursor cursor){
        OpenQuestion[] result=null;
        
        if(cursor!=null && cursor.getCount()>0){
            result=new OpenQuestion[cursor.getCount()];
            int i=0;
            while(cursor.moveToNext()){
                int currQuestionId=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ID));
                int currQuestionStudyId=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_STUDY_ID));
                result[i]=new OpenQuestion(currQuestionId,currQuestionStudyId);
                result[i].setText(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                result[i].setOrderNumber(cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTION_ORDER)));
                result[i].setIsPost(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(QuestionsTable.COLUMN_IS_AFTER_QSORT))));
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
