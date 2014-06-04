package de.eresearch.app.db.tables;

public class SortLoggingTable {

    public static final String TABLE_SORT_LOGGING ="SortLogging";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_TIME ="time";
    public static final String COLUMN_QSORT_ID ="qsort_id";
    public static final String COLUMN_ITEM_ID ="item_id";
    public static final String COLUMN_POSITION_BEFORE ="position_before";
    public static final String COLUMN_POSITION_AFTER ="position_after";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_TIME,
        COLUMN_QSORT_ID, COLUMN_ITEM_ID, COLUMN_POSITION_BEFORE,COLUMN_POSITION_AFTER };
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_SORT_LOGGING + "(" + COLUMN_ID
            + " integer primary key autoincrement,"
            + COLUMN_TIME + " text not null," 
            + COLUMN_QSORT_ID + " integer,"
            + COLUMN_ITEM_ID + " integer not null,"
            + COLUMN_POSITION_BEFORE + " text not null,"
            + COLUMN_POSITION_AFTER + " text not null,"
            + "FOREIGN KEY (" + COLUMN_QSORT_ID + ") REFERENCES "
            + QsortsTable.TABLE_QSORTS +"(" + QsortsTable.COLUMN_ID+") ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES "
            + QsortItems.TABLE_QSORT_ITEMS+"(" + QsortItems.COLUMN_ID + ")"
            + ");";
}
