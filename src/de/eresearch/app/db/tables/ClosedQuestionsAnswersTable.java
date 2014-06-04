package de.eresearch.app.db.tables;

public class ClosedQuestionsAnswersTable {
    
    public static final String TABLE_CLOSED_QUESTIONS_ANSWERS ="closed_questions_answers";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_ANSWER_ID="answer_id";
    public static final String COLUMN_CQ_ANSWER_ID ="cq_answer_id";
    public static final String COLUMN_OPEN_ANSWER ="open_answer";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_ANSWER_ID,COLUMN_CQ_ANSWER_ID,COLUMN_OPEN_ANSWER};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_CLOSED_QUESTIONS_ANSWERS + "(" + COLUMN_ID
        + " integer primary key autoincrement, "
        + COLUMN_ANSWER_ID + " integer, " 
        + COLUMN_CQ_ANSWER_ID + " integer, " 
        + COLUMN_OPEN_ANSWER + " text, " 
        + "FOREIGN KEY (" + COLUMN_ANSWER_ID + ") REFERENCES "
        + AnswersTable.TABLE_ANSWERS +"(" + AnswersTable.COLUMN_ID +") ON DELETE CASCADE,"
        + "FOREIGN KEY (" + COLUMN_CQ_ANSWER_ID + ") REFERENCES "
        + CQPossibleAnswersTable.TABLE_CQ_POSSIBLE_ANSWERS +"(" + CQPossibleAnswersTable.COLUMN_ID +"));";
    
    

}
