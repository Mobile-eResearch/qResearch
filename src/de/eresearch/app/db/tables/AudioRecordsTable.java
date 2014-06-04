package de.eresearch.app.db.tables;

public class AudioRecordsTable {
    
    public static final String TABLE_AUDIORECORDS ="Audiorecords";
    
    public static final String COLUMN_RECORD_ID ="record_id";
    public static final String COLUMN_PATH ="path";
    public static final String COLUMN_PARTNUMBER ="partnumber";
    
    public static final String[] ALL_COLUMNS={COLUMN_RECORD_ID,COLUMN_PATH,COLUMN_PARTNUMBER};
    
    public static final String TABLE_CREATE = "create table "
            + TABLE_AUDIORECORDS + "(" + COLUMN_RECORD_ID
            + " integer primary key not null, "
            + COLUMN_PATH + " text not null, "
            + COLUMN_PARTNUMBER + " int not null,"
            + "FOREIGN KEY (" + COLUMN_RECORD_ID + ") REFERENCES "
            + RecordsTable.TABLE_RECORDS+"(" + RecordsTable.COLUMN_ID + ") ON DELETE CASCADE"
            + ");";
    
}
