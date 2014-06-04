package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.OperatorsTable;

/**
 * Helper for operator names and ids
 * @author Jurij Wöhlke
 */
public class OperatorHelper{
    
    private static OperatorHelper instance; 
    
    protected DatabaseConnector dbc;
    protected SQLiteDatabase database;
    
    /**
     * standard constructor
     * @param dbconn
     */
    public OperatorHelper(final DatabaseConnector dbconn){
        this.dbc=dbconn;
        this.database=this.dbc.getDb();
    }
    
    /**
     * returns instance of DatabaseConnector
     * @param context
     * @return DatabaseConnector
     */
    public synchronized static OperatorHelper getInstance(DatabaseConnector dbc) {
        if(instance==null){
            instance=new OperatorHelper(dbc);
        }
        return instance;
    }
    
    /**
     * returns the operator name for this id
     * @param int operatorId
     * @return String
     */
    public String getOperatorById(final int id){
        String res=null;
        String[] whereArgs={Integer.toString(id)};
        Cursor cursor=this.database.query(OperatorsTable.TABLE_OPERATORS, OperatorsTable.ALL_COLUMNS, OperatorsTable.COLUMN_ID+"=?", whereArgs, null, null, null);
        if(cursor.moveToNext()){
            res=cursor.getString(cursor.getColumnIndex(OperatorsTable.COLUMN_TOKEN));
        }
        cursor.close();
        return res;
    }
    
    /**
     * returns the operatorId for this token/operator name
     * @param String token
     * @return int operatorId
     */
    public int getOperatorIdByString(final String token){
        int res=-1;
        String[] whereArgs={token};
        Cursor cursor=this.database.query(OperatorsTable.TABLE_OPERATORS, OperatorsTable.ALL_COLUMNS, OperatorsTable.COLUMN_TOKEN+"=?", whereArgs, null, null, null);
        if(cursor.moveToNext()){
            res=cursor.getInt(cursor.getColumnIndex(OperatorsTable.COLUMN_ID));
        }
        cursor.close();
        return res;
    }
    
    /**
     * adds an operator to database
     * @param String token
     * @return int operatorId
     */
    public int addOperator(String token){
        int res=-1;
        ContentValues cvalues= new ContentValues();
        cvalues.put(OperatorsTable.COLUMN_TOKEN, token);
        res=(int)this.database.insert(OperatorsTable.TABLE_OPERATORS, null, cvalues);
        return res;
    }
}
