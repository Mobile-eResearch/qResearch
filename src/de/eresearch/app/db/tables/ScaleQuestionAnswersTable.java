package de.eresearch.app.db.tables;

public class ScaleQuestionAnswersTable {
    
    public static final String TABLE_SCALE_QUESTION_ANSWER ="ScaleQuestionAnswers";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_ANSWER_ID="answer_id";
    public static final String COLUMN_SCALE_VALUE_ID ="scale_value_id";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_ANSWER_ID,COLUMN_SCALE_VALUE_ID};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_SCALE_QUESTION_ANSWER + "(" + COLUMN_ID
        + " integer primary key autoincrement, "
        + COLUMN_ANSWER_ID + " integer not null," 
        + COLUMN_SCALE_VALUE_ID + " integer not null," 
        + "FOREIGN KEY (" + COLUMN_ANSWER_ID + ") REFERENCES "
        + AnswersTable.TABLE_ANSWERS +"(" + AnswersTable.COLUMN_ID +") ON DELETE CASCADE,"
        + "FOREIGN KEY (" + COLUMN_SCALE_VALUE_ID + ") REFERENCES "
        + ScaleValuesTable.TABLE_SCALES_VALUES +"(" + ScaleValuesTable.COLUMN_ID +"));";
    
}
