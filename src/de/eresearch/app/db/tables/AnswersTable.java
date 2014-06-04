package de.eresearch.app.db.tables;

public class AnswersTable {
    
    public static final String TABLE_ANSWERS ="Answers";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_QUESTION_ID="question_id";
    public static final String COLUMN_QSORT_ID ="qsort_id";
    public static final String COLUMN_TIME ="time";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_QUESTION_ID,COLUMN_QSORT_ID,COLUMN_TIME};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_ANSWERS + "(" + COLUMN_ID
        + " integer primary key autoincrement,"
        + COLUMN_QUESTION_ID + " integer, " 
        + COLUMN_QSORT_ID + " integer, " 
        + COLUMN_TIME + " text not null, "
        + "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES "
        + QuestionsTable.TABLE_QUESTIONS+"(" + QuestionsTable.COLUMN_ID +"),"
        + "FOREIGN KEY (" + COLUMN_QSORT_ID + ") REFERENCES "
        + QsortsTable.TABLE_QSORTS+"(" + QsortsTable.COLUMN_ID +") ON DELETE CASCADE"
        + ");";
    

}
