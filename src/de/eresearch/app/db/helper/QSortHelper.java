package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.QsortsTable;
import de.eresearch.app.db.tables.RecordsTable;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;

/**
 * Helper for QSort model objects
 * @author Jurij Wöhlke
 */
public class QSortHelper extends AbstractObjectHelper<QSort> implements IdObjectHelperInterface<QSort>,StudyObjectHelperInterface<QSort>{

    /**
     * standard constructor
     * @param dbconn
     */
    public QSortHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all QSorts for this StudyId
     * @param studyId
     * @return QSort[]
     * @Override
     */
    public QSort[] getAllByStudyId(final int identifier) {
        String [] selArgs={Integer.toString(identifier)};
        Cursor cursor=database.query(QsortsTable.TABLE_QSORTS, QsortsTable.ALL_COLUMNS, QsortsTable.COLUMN_STUDY_ID+"=?",selArgs, null, null, QsortsTable.COLUMN_ID);
        QSort[] result=this.getFullQSortsforQSortTableCursor(cursor, identifier);
        cursor.close();
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns a QSort for QSortId
     * @param qsortId
     * @return QSort
     * @Override
     */
    public QSort getObjectById(final int identifier) {
        QSort result=null;
        String [] selArgs={Integer.toString(identifier)};
        Cursor cursor=database.query(QsortsTable.TABLE_QSORTS, QsortsTable.ALL_COLUMNS, QsortsTable.COLUMN_ID+"=?",selArgs, null, null, null);
        QSort[] resultSet=this.getFullQSortsforQSortTableCursor(cursor, identifier);
        if(resultSet!=null && resultSet.length>0){
            result=resultSet[0];
        }
        cursor.close();
        return result;
    }

    /**
     * deletes QSort for QSortId
     * @param qsortId
     * @return boolean true if success
     * @Override
     */
    public boolean deleteById(final int identifier) {
        boolean result=true;
        try {
            //delete Logs
            LogHelper logHelper=(LogHelper) this.dbc.getHelper(DatabaseConnector.TYPE_LOG);
            Log[] logs=logHelper.getAllByQSortId(identifier);
            
            if(logs!=null && logs.length>0){
                for(Log log:logs){
                    if(!(logHelper.deleteObject(log) && result)){
                        result=false;
                    }
                }
            }
            
            //delete pyramid cells
            PyramidHelper pyramidHelper=(PyramidHelper) dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
            if(!(pyramidHelper.deletePyramidCells(identifier) && result)){
                result=false;
            }
            
        } catch (HelperNotFoundException e) {
            result=false;
        }
        
        if(result){
            String[] whereArgs={Integer.toString(identifier)};
            int res=this.database.delete(QsortsTable.TABLE_QSORTS, QsortsTable.COLUMN_ID+"=?",whereArgs);
            if(!(res>=0)){
               result=false;
            }
        }else{
            android.util.Log.e("QSortHelper", "Failed to delete QSort");
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves a QSort - it can be an update or insert depending on the QSortId
     * @param QSort qsort
     * @return QSort
     * @Override
     */
    public QSort saveObject(QSort qsort) {
        QSort result=null;
        try{
            LogHelper logHelper=(LogHelper) this.dbc.getHelper(DatabaseConnector.TYPE_LOG);
            PyramidHelper pyramidHelper=(PyramidHelper) this.dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
            
            //Data:
            ContentValues cValues=new ContentValues();
            cValues.put(QsortsTable.COLUMN_NAME, qsort.getName());
            cValues.put(QsortsTable.COLUMN_STUDY_ID, qsort.getStudyId());
            if(qsort.getAcronym()!=null && !qsort.getAcronym().equals("")){
                OperatorHelper oh=OperatorHelper.getInstance(dbc);
                int operatorId=oh.getOperatorIdByString(qsort.getAcronym());
                if(operatorId<=0){
                    operatorId=oh.addOperator(qsort.getAcronym());
                }
                cValues.put(QsortsTable.COLUMN_OPERATOR_ID, operatorId);
            }
            cValues.put(QsortsTable.COLUMN_FINISHED, Boolean.toString(qsort.isFinished()));
            cValues.put(QsortsTable.COLUMN_START, qsort.getStartTime());
            cValues.put(QsortsTable.COLUMN_END, qsort.getEndTime());
            
            //insert
            if(qsort.getId()<=0){
                final long res=this.database.insert(QsortsTable.TABLE_QSORTS, null, cValues);
                if(res>0){
                    qsort.setId((int) res);
                    for(final Phase phase : Phase.values()){
                        Log log=qsort.getLog(phase);
                        if(log!=null){
                            log.setQSortId(qsort.getId());
                            log=logHelper.saveObject(log);
                            if(log!=null){
                                qsort.setLog(phase, log);
                            }
                        }
                    }
                    
                    //save pyramid cells
                    List<Item> sortedItems=qsort.getSortedItems();
                    if(sortedItems!=null && sortedItems.size()>0){
                        Item[] sortedItemsArray = new Item[sortedItems.size()];
                        sortedItemsArray=sortedItems.toArray(sortedItemsArray);
                        pyramidHelper.savePyramidCells(qsort.getId(), sortedItemsArray );
                    }
                    result=qsort;
                }
            //update
            }else if(qsort.getId()>0){
                String[] whereArgs={Integer.toString(qsort.getId())};
                final long res=this.database.update(QsortsTable.TABLE_QSORTS, cValues, QsortsTable.COLUMN_ID+"=?", whereArgs);
                if(res>=0){
                    for(final Phase phase : Phase.values()){
                        Log log=qsort.getLog(phase);
                        log.setQSortId(qsort.getId());
                        if(log!=null){
                            if(log.getQSortId()<0 ){
                                log.setQSortId(qsort.getId());
                            }
                            log=logHelper.saveObject(log);
                            if(log!=null){
                                qsort.setLog(phase, log);
                            }
                        }
                    }
                    //save pyramid cells
                    List<Item> sortedItems=qsort.getSortedItems();
                    if(sortedItems!=null && sortedItems.size()>0){
                        Item[] itemstmp=new Item[sortedItems.size()];
                        itemstmp=sortedItems.toArray(itemstmp);
                        pyramidHelper.savePyramidCells(qsort.getId(), itemstmp);
                    }
                    result=qsort;
                }
            }
        } catch (HelperNotFoundException e) {
            result=null;
        }
        return result;
    }

    /**
     * Refreshs a QSort with data from database
     * @param QSort obj
     * @return QSort
     * @Override
     */
    public QSort refreshObject(final QSort obj) {
        return this.getObjectById(obj.getId());
    }

    /**
     * deletes a QSort for QSortId in obj
     * @param QSort obj
     * @return boolean true if success
     * @Override
     */
    public boolean deleteObject(final QSort obj) {
        return this.deleteById(obj.getId());
    }

    // ################# Other methods #################
    /**
     * returns studyid for qsortid
     * @param id
     * @return int id -> returns -1 if fails
     */
    public int getStudyIdByQSortId(final int identifier) {
        final String[] columns={QsortsTable.COLUMN_ID, QsortsTable.COLUMN_STUDY_ID};
        final String[] selArgs={Integer.toString(identifier)};
        Cursor cursor=this.database.query(QsortsTable.TABLE_QSORTS, columns, QsortsTable.COLUMN_ID+"=?" ,selArgs, null, null, null);
        int result=-1;
        if(cursor.moveToNext()){
            result=cursor.getInt(cursor.getColumnIndex(QsortsTable.COLUMN_STUDY_ID));
        }
        cursor.close();
        return result;
    }
    
    /**
     * returns an array of qsorts for cursor and the qsort identifier
     * to return a full qsort it loads different things from other helper
     * @param Cursor cursor
     * @param int identifier studyid
     * @return QSort[] qsorts
     */
    private QSort[] getFullQSortsforQSortTableCursor(Cursor cursor, int identifier){
        QSort[] result=null;
        try{
            if(cursor.getCount()>0){
                result=new QSort[cursor.getCount()];
                int i=0;
                while(cursor.moveToNext()){
                    int qid=cursor.getInt(cursor.getColumnIndex(QsortsTable.COLUMN_ID));
                    int studyid=cursor.getInt(cursor.getColumnIndex(QsortsTable.COLUMN_STUDY_ID));
                    result[i]=new QSort(qid, studyid);
                    result[i].setName(cursor.getString(cursor.getColumnIndex(QsortsTable.COLUMN_NAME)));
                    result[i].setStartTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(QsortsTable.COLUMN_START))));
                    result[i].setEndTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(QsortsTable.COLUMN_END))));
                    String finished=cursor.getString(cursor.getColumnIndex(QsortsTable.COLUMN_FINISHED));
                    result[i].setFinished(Boolean.parseBoolean(finished));
                    OperatorHelper ohelper=OperatorHelper.getInstance(dbc);
                    int operator=cursor.getInt(cursor.getColumnIndex(QsortsTable.COLUMN_OPERATOR_ID));
                    if(operator>0){
                        result[i].setAcronym(ohelper.getOperatorById(operator));
                    }
                    LogHelper logHelper=(LogHelper) dbc.getHelper(DatabaseConnector.TYPE_LOG);
                    Log[] logs=logHelper.getAllByQSortId(qid);
                    if(logs!=null && logs.length>0){
                        for(Log log:logs){
                            if(log!=null){
                                result[i].setLog(log.getPhase(), log);
                            }
                        }
                    }
                    
                    //get pyramid cells:
                    PyramidHelper pyramidHelper=(PyramidHelper) dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
                    Item[] pyramid=pyramidHelper.getPyramidCells(qid);
                    if(pyramid!=null && pyramid.length>0){
                        List<Item> sortedItems=Arrays.asList(pyramid);
                        result[i].setSortedItems(sortedItems);
                    }
                    
                    result[i].setAudioRecord(hasQSortAudioRecord(qid));
            
                    /* TODO:set QSortattr:
                     *      - qsort start
                     *      - qsort end
                     */
                    
                    //increment counter
                    i++;
                }
            }
        } catch (HelperNotFoundException e) {
            result=null;
        }
        return result;
    }
    
    
    /**
     * returns all true if there qsorts for study, and false if not
     * @param studyId
     * @return QSort[]
     */
    public Boolean checkForQSortsByStudyId(final int identifier) {
        String [] selArgs={Integer.toString(identifier)};
        String sql = "SELECT 1 FROM "+ QsortsTable.TABLE_QSORTS + " WHERE " + QsortsTable.COLUMN_STUDY_ID + "=?";
        Cursor cursor=database.rawQuery(sql, selArgs);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    
    /**
     * returns true if qsort has at least one audio record, else false
     * @param qsortid
     * @return true if qsort has at least one audio record, else false
     */
    public boolean hasQSortAudioRecord(int qsortid){
        boolean result=false;
        Cursor cursor=this.database.query(RecordsTable.TABLE_RECORDS,
                new String[]{RecordsTable.COLUMN_ID},
                RecordsTable.COLUMN_QSORT_ID+"=? AND "+RecordsTable.COLUMN_DATATYPE_OF_RECORD+"=?",
                new String[]{Integer.toString(qsortid),Integer.toString(EnumTable.ENUM_RECORD_DATATYPE_AUDIO)},
                null, null, RecordsTable.COLUMN_ID+" ASC");
        if(cursor.getCount()>0){
            result=true;
        }
        cursor.close();
        return result;
    }
    
    // ################### Helper Getter and Setter: ###################
    @Override
    public DatabaseConnector getDbc() {
        return this.dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}