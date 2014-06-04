package de.eresearch.app.db.tables;

public class ScaleValuesTable {
    
    public static final String TABLE_SCALES_VALUES ="ScaleValues";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_SCALE_ID="scale_id";
    public static final String COLUMN_ORDER ="_order";
    public static final String COLUMN_SCALE_VALUE ="scale_value";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_SCALE_ID,COLUMN_ORDER,COLUMN_SCALE_VALUE};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_SCALES_VALUES + "(" + COLUMN_ID
        + " integer primary key autoincrement,"
        + COLUMN_SCALE_ID + " integer, " 
        + COLUMN_ORDER + " integer, " 
        + COLUMN_SCALE_VALUE + " text not null, "
        + "FOREIGN KEY (" + COLUMN_SCALE_ID + ") REFERENCES "
        + ScalesTable.TABLE_SCALES +"(" + ScalesTable.COLUMN_ID +") ON DELETE CASCADE);";
}
