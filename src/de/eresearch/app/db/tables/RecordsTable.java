
package de.eresearch.app.db.tables;

public class RecordsTable {

    public static final String TABLE_RECORDS = "Records";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QSORT_ID = "qsort_id";
    public static final String COLUMN_DATATYPE_OF_RECORD ="datatype_of_record";
    public static final String COLUMN_TYPE_OF_RECORD ="type_of_record";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_QSORT_ID, COLUMN_DATATYPE_OF_RECORD, COLUMN_TYPE_OF_RECORD
    };

    public static final String TABLE_CREATE = "create table "
            + TABLE_RECORDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_QSORT_ID + " integer not null, " 
            + COLUMN_DATATYPE_OF_RECORD + " integer not null, " 
            + COLUMN_TYPE_OF_RECORD + " integer not null, "  
            + "FOREIGN KEY (" + COLUMN_QSORT_ID + ") REFERENCES "
            + QsortsTable.TABLE_QSORTS+"(" + QsortsTable.COLUMN_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_DATATYPE_OF_RECORD + ") REFERENCES "
            + EnumTable.TABLE_ENUM +"(" + EnumTable.COLUMN_ID 
            + "), FOREIGN KEY (" + COLUMN_TYPE_OF_RECORD + ") REFERENCES "
            + EnumTable.TABLE_ENUM +"(" + EnumTable.COLUMN_ID +"));";
}
