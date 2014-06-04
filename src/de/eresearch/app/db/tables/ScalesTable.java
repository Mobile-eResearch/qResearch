package de.eresearch.app.db.tables;

public class ScalesTable {

    public static final String TABLE_SCALES ="Scales";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_SCALE_QUESTION_ID="scale_question_id";
    public static final String COLUMN_WIDTH ="width";
    public static final String COLUMN_ORDER_ID="order_id";
    public static final String COLUMN_POL_LEFT ="pol_left";
    public static final String COLUMN_POL_RIGHT ="pol_right";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_SCALE_QUESTION_ID,COLUMN_WIDTH,COLUMN_ORDER_ID,
        COLUMN_POL_LEFT, COLUMN_POL_RIGHT};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_SCALES + "(" + COLUMN_ID
        + " integer primary key autoincrement,"
        + COLUMN_SCALE_QUESTION_ID + " integer not null, "
        + COLUMN_WIDTH + " integer, " 
        + COLUMN_ORDER_ID + " integer, " 
        + COLUMN_POL_LEFT + " text not null, "
        + COLUMN_POL_RIGHT + " text not null, "
        + "FOREIGN KEY (" + COLUMN_SCALE_QUESTION_ID + ") REFERENCES "
        + ScaleQuestionsTable.TABLE_SCALE_QUESTIONS +"(" + ScaleQuestionsTable.COLUMN_QUESTION_ID +") ON DELETE CASCADE"
        + ");";
}
