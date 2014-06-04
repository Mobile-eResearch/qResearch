package de.eresearch.app.db.tables;

public class OperatorsTable {

    public static final String TABLE_OPERATORS ="Operator";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_TOKEN ="token";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_TOKEN};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_OPERATORS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TOKEN + " text not null );";
    
}
