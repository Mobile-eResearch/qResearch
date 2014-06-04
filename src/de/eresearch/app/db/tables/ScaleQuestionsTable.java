package de.eresearch.app.db.tables;

public class ScaleQuestionsTable {
    
    public static final String TABLE_SCALE_QUESTIONS ="ScaleQuestions";
    
    public static final String COLUMN_QUESTION_ID ="question_id";
    @Deprecated
    public static final String COLUMN_SCALE_ID ="scale_id";
    
    public static final String[] ALL_COLUMNS={COLUMN_QUESTION_ID/*,COLUMN_SCALE_ID*/};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_SCALE_QUESTIONS + "(" + COLUMN_QUESTION_ID
        + " integer primary key, "
        //+ COLUMN_SCALE_ID + " integer, " 
        + "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES "
        + QuestionsTable.TABLE_QUESTIONS +"(" + QuestionsTable.COLUMN_ID +") ON DELETE CASCADE"
        /*+ "FOREIGN KEY (" + COLUMN_SCALE_ID + ") REFERENCES "
        + ScalesTable.TABLE_SCALES +"(" + ScalesTable.COLUMN_ID +")"*/
        +");";

}
