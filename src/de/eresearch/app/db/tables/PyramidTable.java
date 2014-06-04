package de.eresearch.app.db.tables;

/**
 * @author Jurij Wöhlke
 */
public class PyramidTable {
   
    public static final String TABLE_PYRAMID ="Pyramid";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_SEGMENTATION ="segmentation";
    public static final String COLUMN_POL_LEFT ="pol_left";
    public static final String COLUMN_POL_RIGHT ="pol_right";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID, COLUMN_SEGMENTATION,
        COLUMN_POL_LEFT, COLUMN_POL_RIGHT};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_PYRAMID + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_SEGMENTATION + " text, "
            + COLUMN_POL_LEFT + " text, "
            + COLUMN_POL_RIGHT +" text );";
    
}
