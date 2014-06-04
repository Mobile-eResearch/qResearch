package de.eresearch.app.db.tables;

public class QsortsTable {
    
    public static final String TABLE_QSORTS ="Qsorts";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_NAME ="name";
    public static final String COLUMN_STUDY_ID ="study_id";
    public static final String COLUMN_OPERATOR_ID ="operator_id";
    public static final String COLUMN_START ="start";
    public static final String COLUMN_END ="end";
    public static final String COLUMN_QSORT_START ="qsort_start";
    public static final String COLUMN_QSORT_END ="qsort_end";
    public static final String COLUMN_FINISHED ="finished";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_NAME, COLUMN_STUDY_ID, COLUMN_OPERATOR_ID,
        COLUMN_START,COLUMN_END, COLUMN_QSORT_START, COLUMN_QSORT_END, COLUMN_FINISHED};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_QSORTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null unique," 
            + COLUMN_STUDY_ID + " integer not null, "
            + COLUMN_OPERATOR_ID + " integer, "
            + COLUMN_START + " text, "
            + COLUMN_END + " text, "
            + COLUMN_QSORT_START + " text, "
            + COLUMN_QSORT_END + " text, "
            + COLUMN_FINISHED + " text, "
            + "FOREIGN KEY (" + COLUMN_STUDY_ID + ") REFERENCES "
            + StudiesTable.TABLE_STUDIES+"(" + StudiesTable.COLUMN_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_OPERATOR_ID + ") REFERENCES "
            + OperatorsTable.TABLE_OPERATORS+"(" + OperatorsTable.COLUMN_ID +"));";

}
