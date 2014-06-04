
package de.eresearch.app.db.tables;

public class QsortItems {
    public static final String TABLE_QSORT_ITEMS = "QsortItems";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATEMENT ="statement";
    public static final String COLUMN_STUDY_ID ="study_id";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_TYPE, COLUMN_STATEMENT, COLUMN_STUDY_ID
    };

    public static final String TABLE_CREATE = "create table "
            + TABLE_QSORT_ITEMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TYPE + " integer not null, " 
            + COLUMN_STATEMENT + " text," 
            + COLUMN_STUDY_ID + " integer not null,"
            +"FOREIGN KEY(" + COLUMN_TYPE+ ") REFERENCES "
            + EnumTable.TABLE_ENUM + "(" + EnumTable.COLUMN_ID +")"
            + "FOREIGN KEY (" + COLUMN_STUDY_ID + ") REFERENCES "
            + StudiesTable.TABLE_STUDIES +"(" + StudiesTable.COLUMN_ID+") ON DELETE CASCADE);";
}
