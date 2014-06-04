package de.eresearch.app.db.tables;

public class CQPossibleAnswersTable {
    
    public static final String TABLE_CQ_POSSIBLE_ANSWERS ="CqPossibleAnswers";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_QUESTION_ID="questions_id";
    public static final String COLUMN_ORDER ="_order";
    public static final String COLUMN_ANSWER ="answer";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_QUESTION_ID,COLUMN_ORDER,
        COLUMN_ANSWER};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_CQ_POSSIBLE_ANSWERS + "(" + COLUMN_ID
        + " integer primary key autoincrement,"
        + COLUMN_QUESTION_ID + " integer not null," 
        + COLUMN_ORDER + " integer," 
        + COLUMN_ANSWER + " text not null," 
        + "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES "
        + ClosedQuestionsTable.TABLE_CLOSED_QUESTIONS +"(" + ClosedQuestionsTable.COLUMN_QUESTION_ID +") ON DELETE CASCADE);";
}
