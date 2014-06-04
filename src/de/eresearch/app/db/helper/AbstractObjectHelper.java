package de.eresearch.app.db.helper;

import android.database.sqlite.SQLiteDatabase;

import de.eresearch.app.db.DatabaseConnector;

/**
 * Abstract Class for all helper to inherits
 * @author Jurij Wöhlke
 * @param <T>
 */
public abstract class AbstractObjectHelper<T> {
    
    protected DatabaseConnector dbc;
    protected SQLiteDatabase database;
    
    /**
     * standard constructor which sets DatabaseConnector and Database
     * @param dbconn
     */
    public AbstractObjectHelper(DatabaseConnector dbconn){
        this.dbc=dbconn;
        this.database=this.dbc.getDb();
    }
    
    /**
     * Saves Object back to db and returns it after that
     * @param <T>
     * @return <T>
     */
    public abstract T saveObject(T obj);
    
    /**
     * Refreshs Object with db and returns it after that
     * @param <T>
     * @return <T>
     */
    public abstract T refreshObject(T obj);
    
    /**
     * deletes Object from DB and returns boolean if it was a success
     * @param <T>
     * @return boolean
     */
    public abstract boolean deleteObject(T obj);
    
    /**
     * getDBConnector
     * @return DatabaseConnector
     */
    public abstract DatabaseConnector getDbc();
    /**
     * set DBConnector
     * @param dbconn
     */
    public abstract void setDbc(DatabaseConnector dbconn);
}
