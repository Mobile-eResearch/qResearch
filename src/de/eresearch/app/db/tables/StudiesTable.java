package de.eresearch.app.db.tables;


public class StudiesTable {

    public static final String TABLE_STUDIES ="Studies";
    
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_AUTHOR ="author";
    public static final String COLUMN_NAME ="name";
    public static final String COLUMN_RESEARCH_QUESTION ="research_question";
    public static final String COLUMN_DESCRIPTION ="description";
    public static final String COLUMN_PYRAMID_ID ="pyramid_id";
    public static final String COLUMN_NOTE ="note";
    public static final String COLUMN_IS_COMPLETE ="is_complete";
    public static final String COLUMN_LAST_EDITED="last_edited";
    public static final String COLUMN_CREATED = "created";
    
    public static final String[] ALL_COLUMNS={COLUMN_ID,COLUMN_AUTHOR,COLUMN_NAME,COLUMN_RESEARCH_QUESTION,
            COLUMN_DESCRIPTION, COLUMN_PYRAMID_ID, COLUMN_NOTE,COLUMN_IS_COMPLETE, COLUMN_LAST_EDITED, COLUMN_CREATED};
    
    public static final String TABLE_CREATE = "create table "
        + TABLE_STUDIES + "(" + COLUMN_ID
        + " integer primary key autoincrement, "
        + COLUMN_AUTHOR + " text not null, " 
        + COLUMN_NAME + " text not null unique, " 
        + COLUMN_RESEARCH_QUESTION + " text, "
        + COLUMN_DESCRIPTION + " text, "
        + COLUMN_PYRAMID_ID +" integer, "
        + COLUMN_NOTE +" text, "
        + COLUMN_IS_COMPLETE + " text,"
        + COLUMN_LAST_EDITED +" text, "
        + COLUMN_CREATED +" text DEFAULT CURRENT_TIMESTAMP, " 
        + "FOREIGN KEY (" + COLUMN_PYRAMID_ID + ") REFERENCES "
        + PyramidTable.TABLE_PYRAMID+"(" + PyramidTable.COLUMN_ID +"));";
}
