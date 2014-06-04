package de.eresearch.app.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.eresearch.app.db.DatabaseOpenHelper;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AbstractObjectHelper;
import de.eresearch.app.db.helper.AnswerHelper;
import de.eresearch.app.db.helper.AudioRecordHelper;
import de.eresearch.app.db.helper.ClosedAnswerHelper;
import de.eresearch.app.db.helper.ClosedQuestionHelper;
import de.eresearch.app.db.helper.ItemHelper;
import de.eresearch.app.db.helper.LogEntryHelper;
import de.eresearch.app.db.helper.LogHelper;
import de.eresearch.app.db.helper.NoteHelper;
import de.eresearch.app.db.helper.OpenAnswerHelper;
import de.eresearch.app.db.helper.OpenQuestionHelper;
import de.eresearch.app.db.helper.PyramidHelper;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.db.helper.QuestionHelper;
import de.eresearch.app.db.helper.ScaleAnswerHelper;
import de.eresearch.app.db.helper.ScaleHelper;
import de.eresearch.app.db.helper.ScaleQuestionHelper;
import de.eresearch.app.db.helper.StudyHelper;

/**
 * Interface to the database
 * @author Jurij Wöhlke
 */
public final class DatabaseConnector{
    
    private static DatabaseConnector dbCInstance; 

    private SQLiteDatabase db;
    private DatabaseOpenHelper dbOpenHelper;
    
    //Cannot change this:
    @SuppressWarnings("rawtypes")
    private AbstractObjectHelper[] objHelper;
    //Number of Helper
    public final static int TYPE_COUNT = 17;
    //Helper constants
    public final static int TYPE_STUDY = 0;
    public final static int TYPE_QSORT = 1;
    public final static int TYPE_QUESTION = 2;
    public final static int TYPE_QUESTION_OPEN = 3;
    public final static int TYPE_QUESTION_CLOSED = 4;
    public final static int TYPE_QUESTION_SCALE = 5;
    public final static int TYPE_SCALE = 6;
    public final static int TYPE_ANSWER = 7;
    public final static int TYPE_ANSWER_OPEN = 8;
    public final static int TYPE_ANSWER_CLOSED = 9;
    public final static int TYPE_ANSWER_SCALE = 10;
    public final static int TYPE_ITEM = 11;
    public final static int TYPE_PYRAMID = 12;
    public final static int TYPE_LOG = 13;
    public final static int TYPE_LOGENTRY = 14;
    public final static int TYPE_AUDIORECORD = 15;
    public final static int TYPE_NOTE = 16;
    
    /**
     * Standard Constructor
     * @param context
     */
    private DatabaseConnector(Context context){
        this.dbOpenHelper = DatabaseOpenHelper.getInstance(context);
        this.objHelper=new AbstractObjectHelper[TYPE_COUNT];
    }
    
    /**
     * returns instance of DatabaseConnector
     * @param context
     * @return DatabaseConnector
     */
    public synchronized static DatabaseConnector getInstance(Context context) {
        if(dbCInstance==null){
            dbCInstance=new DatabaseConnector(context);
        }
        return dbCInstance;
    }
    
    /**
     * opens DB
     * @throws SQLException
     */
    public synchronized void open() throws SQLException {
        Log.d(this.getClass().toString(), "open()");
        db = dbOpenHelper.getWritableDatabase();
    }
    
    /**
     * closes DB
     */
    public synchronized void close(){
        Log.d(this.getClass().toString(), "close()");
        dbOpenHelper.close();
    }
    
    /**
     * returns helper from objHelper or creates it first if not already created.
     * helper is instance of type, defined by param type
     * @param type
     * @return ObjectHelper Object
     * @throws HelperNotFoundException
     */
    @SuppressWarnings("rawtypes")
    public synchronized AbstractObjectHelper getHelper(int type) throws HelperNotFoundException{
        if(objHelper[type]==null){
            createHelper(type);
        }
        if(objHelper[type]!=null && objHelper[type] instanceof AbstractObjectHelper){
            return objHelper[type];
        }else{
            throw new HelperNotFoundException();
        }
    }

    /**
     * Creates Helper and saves it into objHelper in type as position in array
     * @param type
     * @throws HelperNotFoundException
     */
    private void createHelper(int type) throws HelperNotFoundException {
        switch(type){
            case TYPE_STUDY:
                objHelper[type]=new StudyHelper(this);
                break;
            case TYPE_QSORT:
                objHelper[type]=new QSortHelper(this);
                break;
            case TYPE_QUESTION:
                objHelper[type]=new QuestionHelper(this);
                break;
            case TYPE_QUESTION_OPEN:
                objHelper[type]=new OpenQuestionHelper(this);
                break;
            case TYPE_QUESTION_CLOSED:
                objHelper[type]=new ClosedQuestionHelper(this);
                break;
            case TYPE_QUESTION_SCALE:
                objHelper[type]=new ScaleQuestionHelper(this);
                break;
            case TYPE_SCALE:
                objHelper[type]=new ScaleHelper(this);
                break;
            case TYPE_ANSWER:
                objHelper[type]=new AnswerHelper(this);
                break;
            case TYPE_ANSWER_OPEN:
                objHelper[type]=new OpenAnswerHelper(this);
                break;
            case TYPE_ANSWER_CLOSED:
                objHelper[type]=new ClosedAnswerHelper(this);
                break;
            case TYPE_ANSWER_SCALE:
                objHelper[type]=new ScaleAnswerHelper(this);
                break;
            case TYPE_ITEM:
                objHelper[type]=new ItemHelper(this);
                break;
            case TYPE_PYRAMID:
                objHelper[type]=new PyramidHelper(this);
                break;
            case TYPE_LOG:
                objHelper[type]=new LogHelper(this);
                break;
            case TYPE_LOGENTRY:
                objHelper[type]=new LogEntryHelper(this);
                break;
            case TYPE_AUDIORECORD:
                objHelper[type]=new AudioRecordHelper(this);
                break;
            case TYPE_NOTE:
                objHelper[type]=new NoteHelper(this);
                break;
            default:
                throw new HelperNotFoundException();
        }
    }
    
    // #################### getter and setter ####################
    /**
     * returns the database
     * @return db
     */
    public synchronized SQLiteDatabase getDb() {
        if(db==null){
            db=dbOpenHelper.getWritableDatabase();
        }
        return db;
    }
}