package de.eresearch.app.db.tables;

public class QuestionsTable {
    
    public static final String TABLE_QUESTIONS ="Questions";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_QUESTION ="question";
    public static final String COLUMN_IS_AFTER_QSORT ="is_after_qsort";
    public static final String COLUMN_QUESTION_ORDER ="question_order";
    public static final String COLUMN_QUESTIONTYPES_ID ="questiontypes_id";
    public static final String COLUMN_STUDY_ID ="study_id";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_QUESTION,COLUMN_IS_AFTER_QSORT,
        COLUMN_QUESTION_ORDER,COLUMN_QUESTIONTYPES_ID,COLUMN_STUDY_ID};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_QUESTIONS + "(" + COLUMN_ID
        + " integer primary key autoincrement, "
        + COLUMN_QUESTION + " text not null, " 
        + COLUMN_IS_AFTER_QSORT +" interger, "
        + COLUMN_QUESTION_ORDER +" integer, "
        + COLUMN_QUESTIONTYPES_ID +" integer, "
        + COLUMN_STUDY_ID +" integer, "
        + "FOREIGN KEY (" + COLUMN_QUESTIONTYPES_ID + ") REFERENCES "
        + EnumTable.TABLE_ENUM+"(" + EnumTable.COLUMN_ID +"),"
        + "FOREIGN KEY (" + COLUMN_STUDY_ID + ") REFERENCES "
        + StudiesTable.TABLE_STUDIES+"(" + StudiesTable.COLUMN_ID +") ON DELETE CASCADE);";
}
