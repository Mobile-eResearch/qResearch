package de.eresearch.app.db.tables;

public class ClosedQuestionsTable {
    
    public static final String TABLE_CLOSED_QUESTIONS ="ClosedQuestions";
    
    public static final String COLUMN_QUESTION_ID ="question_id";
    public static final String COLUMN_HAS_OPEN_ANSWER ="has_open_answer";
    public static final String COLUMN_IS_MULTIPLE_CHOICE ="is_multiple_choice";
    
    public static final String[] ALL_COLUMNS={COLUMN_QUESTION_ID,COLUMN_HAS_OPEN_ANSWER,
        COLUMN_IS_MULTIPLE_CHOICE};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_CLOSED_QUESTIONS + "(" + COLUMN_QUESTION_ID
        + " integer primary key, "
        + COLUMN_HAS_OPEN_ANSWER + " integer not null, " 
        + COLUMN_IS_MULTIPLE_CHOICE + " integer not null, " 
        + "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES "
        + QuestionsTable.TABLE_QUESTIONS +"(" + QuestionsTable.COLUMN_ID + ") ON DELETE CASCADE"
        + ");";
}
