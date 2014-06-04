package de.eresearch.app.db.tables;

public class QuestionLoggingTable {
    
    public static final String TABLE_QUESTION_LOGGING ="QuestionLogging";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_DATE_TIME ="date_time";
    public static final String COLUMN_QSORT_ID ="qsort_id";
    public static final String COLUMN_QUESTION_ID ="question_id"; 
    public static final String COLUMN_EVENT ="event"; 
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_DATE_TIME,
        COLUMN_QSORT_ID, COLUMN_QUESTION_ID, COLUMN_EVENT};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_QUESTION_LOGGING + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_DATE_TIME + " text not null, " 
            + COLUMN_QSORT_ID + " integer not null, "
            + COLUMN_QUESTION_ID + " integer not null, "
            + COLUMN_EVENT + " integer not null, "
            + "FOREIGN KEY (" + COLUMN_QSORT_ID + ") REFERENCES "
            + QsortsTable.TABLE_QSORTS +"(" + QsortsTable.COLUMN_ID+ ") ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES "
            + QuestionsTable.TABLE_QUESTIONS+"(" + QuestionsTable.COLUMN_ID + ")"
            + "FOREIGN KEY (" + COLUMN_EVENT + ") REFERENCES "
            + EnumTable.TABLE_ENUM +"(" + EnumTable.COLUMN_ID +"));"; 
    
}
