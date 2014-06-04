package de.eresearch.app.db.tables;

public class NotesTable {
    
    public static final String TABLE_NOTES ="Notes";
    
    public static final String COLUMN_RECORD_ID ="record_id";
    public static final String COLUMN_QUESTION_ID ="question_id";
    public static final String COLUMN_NAME ="name";
    public static final String COLUMN_TEXT ="text";
    public static final String COLUMN_TIME ="time";
    
    public static final String[] ALL_COLUMNS={COLUMN_RECORD_ID,COLUMN_QUESTION_ID, COLUMN_NAME, COLUMN_TEXT,
        COLUMN_TIME};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_NOTES + "(" + COLUMN_RECORD_ID
            + " integer primary key not null, "
            + COLUMN_QUESTION_ID + " integer, " 
            + COLUMN_NAME +" text, "	
            + COLUMN_TEXT + " text, "
            + COLUMN_TIME + " text, "
            + "FOREIGN KEY (" + COLUMN_RECORD_ID + ") REFERENCES "
            + RecordsTable.TABLE_RECORDS+"(" + RecordsTable.COLUMN_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY(" + COLUMN_QUESTION_ID + ") REFERENCES "
            + QuestionsTable.TABLE_QUESTIONS + "(" + QuestionsTable.COLUMN_ID +"));";    
}
