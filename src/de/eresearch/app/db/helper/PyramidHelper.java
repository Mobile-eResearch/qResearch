package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.FilledPyramidCellsTable;
import de.eresearch.app.db.tables.PyramidTable;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;

/**
 * Helper for Pyramid model object
 * @author Jurij Wöhlke
 */
public class PyramidHelper extends AbstractObjectHelper<Pyramid> implements IdObjectHelperInterface<Pyramid>,QSortObjectHelperInterface<Pyramid>,StudyObjectHelperInterface<Pyramid>{

    /**
     * standard constructor
     * @param dbconn
     */
    public PyramidHelper(final DatabaseConnector dbconn) {
        super(dbconn);
    }
    
    // ################# StudyObjectHelperInterface methods #################

    /**
     * returns the pyramid for study id. the result is a
     * one dimensional pyramid array with only one pyramid object
     * - This does not return a filled Pyramid(Just the structure)! -
     * see getObjectById for more Details!
     * @param int studyId
     * @return Pyramid[]
     * @Override
     */
    public Pyramid[] getAllByStudyId(final int studyid) {
        Pyramid[] result=new Pyramid[1];
        result[0]=getObjectById(studyid);
        return result;
    }

    // ################# QSortObjectHelperInterface methods #################
    /**
     * returns the pyramid for qsort id. the result is a
     * one dimensional pyramid array with only one pyramid object
     * @param int qsortId
     * @return Pyramid[] - if fails null
     * @Override
     */
    public Pyramid[] getAllByQSortId(final int qsortId) {
        // TODO implement after QSort implemented
        Pyramid[] result=null;
        try {
            QSortHelper qSortHelper=(QSortHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QSORT);
            result=new Pyramid[1];
            result[0]=getObjectById(qSortHelper.getStudyIdByQSortId(qsortId));
            //TODO: setCells! //this.getPyramidCells(qsortId, result[0].getId());
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * returns a Pyramid Structure for Pyramid/Study-Id
     * - this means not a filled Pyramid for a QSORT! -
     * @param int pyramidId/studyId
     * @return Pyramid
     * @Override
     */
    public Pyramid getObjectById(int id) {
        String[] selArgs={Integer.toString(id)};
        Cursor cursor=this.database.query(PyramidTable.TABLE_PYRAMID, PyramidTable.ALL_COLUMNS,
                PyramidTable.COLUMN_ID+"=?",selArgs, null, null, null);
        Pyramid result=null;
        if(cursor.moveToNext()){
            result=new Pyramid(cursor.getInt(cursor.getColumnIndex(PyramidTable.COLUMN_ID)));
            result.setPoleLeft(cursor.getString(cursor.getColumnIndex(PyramidTable.COLUMN_POL_LEFT)));
            result.setPoleRight(cursor.getString(cursor.getColumnIndex(PyramidTable.COLUMN_POL_RIGHT)));
            String segm=cursor.getString(cursor.getColumnIndex(PyramidTable.COLUMN_SEGMENTATION));
            result.fromUniqueString(segm);
        }
        cursor.close();
        return result;
    }

    /**
     * deletes all pyramids and NOT ONLY the structure
     * @param int - pyramidId/studyId
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=true;
        String[] whereArgs={Integer.toString(id)};
        int tmpres=this.database.delete(PyramidTable.TABLE_PYRAMID, PyramidTable.COLUMN_ID+"=?", whereArgs);
        if(result && tmpres>=0){
            result=true;
        }else{
            result=false;
        }
        try {
            QSortHelper qSortHelper = (QSortHelper)this.dbc.getHelper(DatabaseConnector.TYPE_QSORT);
            QSort[] qsorts=qSortHelper.getAllByStudyId(id);
            if(qsorts!=null){
                for(int i=0;i<qsorts.length;i++){
                    if(!(result && deletePyramidCells(qsorts[i].getId()))){
                        result=false;
                    }
                }
            }else{
                result = false;
            }
        } catch (HelperNotFoundException e) {
            result = false;
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves pyramid structure (inserts or updates)
     * if new pyramid set id to -1
     * else id=pyramidId or studyId 
     * @param Pyramid pyramid
     * @return Pyramid
     * @Override
     */
    public Pyramid saveObject(Pyramid obj) {
        //return:
        Pyramid result=null;
        
        //oldPyramid -> to update if not null
        Pyramid oldPyramid=this.getObjectById(obj.getId());
        
        //input values:
        ContentValues cValues=new ContentValues();
        cValues.put(PyramidTable.COLUMN_POL_LEFT,obj.getPoleLeft());
        cValues.put(PyramidTable.COLUMN_POL_RIGHT,obj.getPoleRight());
        cValues.put(PyramidTable.COLUMN_SEGMENTATION,obj.toUniqueString());
        //TODO: remove: cValues.put(PyramidTable.COLUMN_WIDTH,obj.getWidth());
        //insert or update:
        if(oldPyramid==null){
            cValues.put(PyramidTable.COLUMN_ID,obj.getId());
            long res=this.database.insert(PyramidTable.TABLE_PYRAMID, null, cValues);
            if(res==obj.getId()){
                result=obj;
            }
        }else{
            String[] whereArgs={Integer.toString(obj.getId())};
            long res=this.database.update(PyramidTable.TABLE_PYRAMID, cValues, PyramidTable.COLUMN_ID+"=?", whereArgs);
            if(res>0){
                result=obj;
            }
        }
        return result;
    }

    /**
     * refresh pyramid structure
     * @param Pyramid obj
     * @return Pyramid
     * @Override
     */
    public Pyramid refreshObject(Pyramid obj) {
        return this.getObjectById(obj.getId());
    }

    
    /**
     * deletes all pyramids and NOT ONLY the structure
     * @param Pyramid obj
     * @return boolean - true if success else false
     */
    @Override
    public boolean deleteObject(Pyramid obj) {
        if(obj!=null){
            return this.deleteById(obj.getId());
        }else{
            return false;
        }
    }
    
    // ################# other methods #################
    /**
     * returns the filled pyramid cells
     * @param pyramidId
     * @param qsortId
     * @return Item[] items
     */
    public Item[] getPyramidCells(int qsortId){
        Item[] result=null;
        Cursor cursor=null;
        try {
            if(qsortId>0){
                String[] selectionArgs={Integer.toString(qsortId)};
                cursor=database.query(FilledPyramidCellsTable.TABLE_FILLED_PYRAMIDE, FilledPyramidCellsTable.ALL_COLUMNS, FilledPyramidCellsTable.COLUMN_QSORT_ID+"=?", selectionArgs, null, null, null);
                if(cursor!=null && cursor.getCount()>0){
                    result=new Item[cursor.getCount()];
                    ItemHelper itemHelper = (ItemHelper) this.dbc.getHelper(DatabaseConnector.TYPE_ITEM);
                    int i=0;
                    while(cursor.moveToNext()){
                        Item tmp=itemHelper.getObjectById(cursor.getInt(cursor.getColumnIndex(FilledPyramidCellsTable.COLUMN_ITEM_ID)));
                        if(tmp!=null && tmp.getId()>0){
                            int row = cursor.getInt(cursor.getColumnIndex(FilledPyramidCellsTable.COLUMN_Y_COORD));
                            int column = cursor.getInt(cursor.getColumnIndex(FilledPyramidCellsTable.COLUMN_X_COORD));
                            tmp.setPosition(row, column);
                            result[i]=tmp;
                        }else{
                            result=null;
                            break;
                        }
                        i++;
                    }
                }
            }
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally{
            cursor.close();
        }
        return result;
    }
    
    /**
     * deletes all old PyramidCells for this qsort and add the new ones!
     * @param qsortId
     * @param items
     * @return
     */
    public boolean savePyramidCells(int qsortId,Item[] items){
        boolean result=true;
        if(qsortId>0 && items!=null && items.length>0){
            this.deletePyramidCells(qsortId);
            for(int i=0; i<items.length;i++){
                ContentValues cValues= new ContentValues();
                
                cValues.put(FilledPyramidCellsTable.COLUMN_QSORT_ID, qsortId);
                cValues.put(FilledPyramidCellsTable.COLUMN_ITEM_ID, items[i].getId());
                cValues.put(FilledPyramidCellsTable.COLUMN_Y_COORD, items[i].getRow());
                cValues.put(FilledPyramidCellsTable.COLUMN_X_COORD, items[i].getColumn());
                
                long res=this.database.insert(FilledPyramidCellsTable.TABLE_FILLED_PYRAMIDE, null, cValues);
                if(res<1){
                    result=false;
                }
            }
        }
        return result;
    }
    
    /**
     * returns true if there were rows to be removed
     * @param qsortId
     * @return boolean - returns true if success else false
     */
    public boolean deletePyramidCells(int qsortId){
        boolean result=false;
        String[] whereArgs2={Integer.toString(qsortId)};
        int res=this.database.delete(FilledPyramidCellsTable.TABLE_FILLED_PYRAMIDE, FilledPyramidCellsTable.COLUMN_QSORT_ID+"=?", whereArgs2);
        if(res>=0){
            result=true;
        }
        return result;
    }

    // ################# getter/setter methods #################
    @Override
    public DatabaseConnector getDbc() {
        return this.dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }

}
