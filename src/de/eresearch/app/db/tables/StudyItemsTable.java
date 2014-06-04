package de.eresearch.app.db.tables;

/**
 * deprecated because not needed anymore and easier to implement without additional referencing
 * - jwoehlke
 */
@Deprecated
public class StudyItemsTable {
    
    public static final String TABLE_STUDY_ITEMS ="StudyItems";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_STATEMENT ="statement";
    public static final String COLUMN_STUDY_ID ="study_id";
    public static final String COLUMN_ITEM_ID ="item_id";
    public static final String COLUMN_ORDER ="_order";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_STATEMENT,
        COLUMN_STUDY_ID, COLUMN_ITEM_ID, COLUMN_ORDER };
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_STUDY_ITEMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_STATEMENT + " text," 
            + COLUMN_STUDY_ID + " integer not null,"
            + COLUMN_ITEM_ID + " integer not null,"
            + COLUMN_ORDER + " integer,"
            + "FOREIGN KEY (" + COLUMN_STUDY_ID + ") REFERENCES "
            + StudiesTable.TABLE_STUDIES +"(" + StudiesTable.COLUMN_ID
            + "), FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES "
            + QsortItems.TABLE_QSORT_ITEMS+"(" + QsortItems.COLUMN_ID + "));";
}
