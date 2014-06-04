package de.eresearch.app.db.helper;

import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Helper for all Question model objects
 * @author Jurij Wöhlke
 */
public class QuestionHelper extends AbstractObjectHelper<Question> implements IdObjectHelperInterface<Question>,StudyObjectHelperInterface<Question>{

    /**
     * standard constructor
     * @param dbconn
     */
    public QuestionHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all questions for this study id
     * @param int studyId
     * @return Question[]
     * @Override
     */
    public Question[] getAllByStudyId(int id) {
        Question[] result=null;
        String[] whereArgs={Integer.toString(id)};
        Cursor cursor=database.query(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.ALL_COLUMNS, QuestionsTable.COLUMN_STUDY_ID+"=?", whereArgs, null, null, QuestionsTable.COLUMN_QUESTION_ORDER+" ASC");
        result=this.getQuestionsForCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the question for this question id
     * @param int questionId
     * @return Question
     * @Override
     */
    public Question getObjectById(int id) {
        Question result=null;
        String[] whereArgs={Integer.toString(id)};
        Cursor cursor=database.query(QuestionsTable.TABLE_QUESTIONS, QuestionsTable.ALL_COLUMNS, QuestionsTable.COLUMN_ID+"=?", whereArgs, null, null, null);
        Question[] questions=this.getQuestionsForCursor(cursor);
        if(questions!=null && questions.length>0){
            result = questions[0];
        }
        cursor.close();
        return result;
    }

    /**
     * deletes question in database for this question id
     * @param int questionId
     * @return boolean - if success then returns true else false
     * @Override
     */
    public boolean deleteById(int id) {
        Question question=this.getObjectById(id);
        if(question!=null && question.getId()>0){
            return this.deleteObject(question);
        }else{
            return false;
        }
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves the question in database
     * @param Question question - needs to be valid question of type scale, open or closed
     * @return Question question - null if saving fails
     * @Override
     */
    public Question saveObject(Question obj) {
        Question result=null;
        try{
            if(obj instanceof OpenQuestion){
                OpenQuestionHelper oqh=(OpenQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_OPEN);
                result=oqh.saveObject((OpenQuestion)obj);
            }else if(obj instanceof ClosedQuestion){
                ClosedQuestionHelper cqh=(ClosedQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_CLOSED);
                result=cqh.saveObject((ClosedQuestion)obj);
            }else if(obj instanceof ScaleQuestion){
                ScaleQuestionHelper sqh=(ScaleQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_SCALE);
                result=sqh.saveObject((ScaleQuestion)obj);
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            result=null;
        }
        return result;
    }

    /**
     * reloads the question from database
     * @param Question question
     * @return Question question
     * @Override
     */
    public Question refreshObject(Question obj) {
        if(obj!=null && obj.getId()>0){
            return this.getObjectById(obj.getId());
        }else{
            return null;
        }
    }

    /**
     * deletes the question from database
     * @param Question question
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(Question obj) {
        boolean result=false;
        try{
            if(obj instanceof OpenQuestion){
                OpenQuestionHelper oqh=(OpenQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_OPEN);
                result=oqh.deleteObject((OpenQuestion)obj);
            }else if(obj instanceof ClosedQuestion){
                ClosedQuestionHelper cqh=(ClosedQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_CLOSED);
                result=cqh.deleteObject((ClosedQuestion)obj);
            }else if(obj instanceof ScaleQuestion){
                ScaleQuestionHelper sqh=(ScaleQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_SCALE);
                result=sqh.deleteObject((ScaleQuestion)obj);
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            result=false;
        }
        return result;
    }
    // ################# additional methods #################
    /**
     * returns an array of full questions for this cursor
     * needed is a cursor with all columns from QuestionsTable
     * @param Cursor cursor
     * @return Question[] questions
     */
    public Question[] getQuestionsForCursor(Cursor cursor){
        Question[] result=null;
        if(cursor.getCount()>0){
            try{
                result= new Question[cursor.getCount()];
                int i=0;
                while(cursor.moveToNext()){
                    int id=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_ID));
                    int typeid=cursor.getInt(cursor.getColumnIndex(QuestionsTable.COLUMN_QUESTIONTYPES_ID));
                    if(typeid==EnumTable.ENUM_QUESTION_TYPE_OPEN){
                        OpenQuestionHelper oqh=(OpenQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_OPEN);
                        result[i]=oqh.getObjectById(id);
                    }else if(typeid==EnumTable.ENUM_QUESTION_TYPE_CLOSED){
                        ClosedQuestionHelper cqh=(ClosedQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_CLOSED);
                        result[i]=cqh.getObjectById(id);
                    }else if(typeid==EnumTable.ENUM_QUESTION_TYPE_SCALE){
                        ScaleQuestionHelper sqh=(ScaleQuestionHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QUESTION_SCALE);
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
