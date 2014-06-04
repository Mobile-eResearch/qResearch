package de.eresearch.app.db.tables;

public class FilledPyramidCellsTable {

    public static final String TABLE_FILLED_PYRAMIDE ="FilledPyramidCells";
        
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_QSORT_ID ="qsort_id";
    public static final String COLUMN_ITEM_ID ="item_id"; 
    public static final String COLUMN_X_COORD ="x_coord"; 
    public static final String COLUMN_Y_COORD ="y_coord"; 
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_QSORT_ID, COLUMN_ITEM_ID,
        COLUMN_X_COORD, COLUMN_Y_COORD};

    public static final String TABLE_CREATE = "create table "
            + TABLE_FILLED_PYRAMIDE + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_QSORT_ID + " integer not null, " 
            + COLUMN_ITEM_ID + " integer not null, " 
            + COLUMN_X_COORD + " integer not null, " 
            + COLUMN_Y_COORD + " integer not null, " 
            + "FOREIGN KEY (" + COLUMN_QSORT_ID + ") REFERENCES "
            + QsortsTable.TABLE_QSORTS+"(" + QsortsTable.COLUMN_ID +") ON DELETE CASCADE,"
            + "FOREIGN KEY ("+ COLUMN_ITEM_ID +") REFERENCES "
            + QsortItems.TABLE_QSORT_ITEMS+ "(" + QsortItems.COLUMN_ID +"));";
}
