package de.eresearch.app.db.tables;

public class PicturesTable {
    public static final String TABLE_PICTURES = "Pictures";

    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_PATH = "path";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ITEM_ID, COLUMN_PATH
    };

    public static final String TABLE_CREATE = "create table "
            + TABLE_PICTURES + "(" + COLUMN_ITEM_ID
            + " integer primary key not null, "
            + COLUMN_PATH + " text not null ,"
            + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES "
            + QsortItems.TABLE_QSORT_ITEMS+"(" + QsortItems.COLUMN_ID + ") ON DELETE CASCADE);";
}
