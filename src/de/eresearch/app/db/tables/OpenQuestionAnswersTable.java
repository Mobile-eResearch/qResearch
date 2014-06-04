package de.eresearch.app.db.tables;

public class OpenQuestionAnswersTable {
    
    public static final String TABLE_OPEN_QUESTION_ANSWERS_TABLE ="OpenQuestionAnswers";
    
    public static final String COLUMN_ANSWER_ID="answer_id";
    public static final String COLUMN_ANSWER ="answer";
    
    public static final String[] ALL_COLUMNS={COLUMN_ANSWER_ID,COLUMN_ANSWER};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_OPEN_QUESTION_ANSWERS_TABLE + "("
        + COLUMN_ANSWER_ID + " integer primary key, " 
        + COLUMN_ANSWER + " text not null, " 
        + "FOREIGN KEY (" + COLUMN_ANSWER_ID + ") REFERENCES "
        + AnswersTable.TABLE_ANSWERS +"(" + AnswersTable.COLUMN_ID +") ON DELETE CASCADE);";
}
