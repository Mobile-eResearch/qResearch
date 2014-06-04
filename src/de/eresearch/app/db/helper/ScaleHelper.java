package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.ScaleValuesTable;
import de.eresearch.app.db.tables.ScalesTable;
import de.eresearch.app.logic.model.Scale;

/**
 * Helper for Scale model object
 * @author Jurij Wöhlke
 */
public class ScaleHelper extends AbstractObjectHelper<Scale> implements IdObjectHelperInterface<Scale>{

    /**
     * standard constructor
     * @param dbconn
     */
    public ScaleHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }
    
    // ################# IdObjectHelperInterface methods #################
    /**
     * returns the scale for this id
     * @param int id
     * @return Scale scale
     * @Override
     */
    public Scale getObjectById(int id) {
        Scale result=null;
        if(id>0){
            Cursor cursor=this.database.query(ScalesTable.TABLE_SCALES, ScalesTable.ALL_COLUMNS, ScalesTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)}, null, null, ScalesTable.COLUMN_ORDER_ID+" ASC");
            Scale[] scales=this.fromCursorToScale(cursor);
            if(scales!=null && scales.length>0){
                result=scales[0];
            }
            cursor.close();
        }
        return result;
    }

    /**
     * deletes the scale for this scale id
     * @param int id
     * @return boolean - true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        boolean result=false;
        int res1=this.database.delete(ScaleValuesTable.TABLE_SCALES_VALUES, ScaleValuesTable.COLUMN_SCALE_ID+"=?", new String[]{Integer.toString(id)});
        int res2=this.database.delete(ScalesTable.TABLE_SCALES, ScalesTable.COLUMN_ID+"=?", new String[]{Integer.toString(id)});
        
        if(res1>=0 && res2>=0){
            result=true;
        }
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves a Scale in database
     * @param Scale scale
     * @return Scale scale - null if fails
     * @Override
     */
    public Scale saveObject(Scale obj) {
        Scale result=null;
        if(obj!=null){
            ContentValues cValues=new ContentValues();
            cValues.put(ScalesTable.COLUMN_SCALE_QUESTION_ID,obj.getQuestionId());
            cValues.put(ScalesTable.COLUMN_ORDER_ID,obj.getDBInternalOrderOfScales());
            cValues.put(ScalesTable.COLUMN_POL_LEFT,obj.getPoleLeft());
            cValues.put(ScalesTable.COLUMN_POL_RIGHT,obj.getPoleRight());
            
            //insert
            if(obj.getId()<=0){
                long res=this.database.insert(ScalesTable.TABLE_SCALES, null, cValues);
                obj.setId((int) res);
                result=obj;
            //update
            }else{
                //remove scaleValues:
                int res1=this.database.delete(ScaleValuesTable.TABLE_SCALES_VALUES, ScaleValuesTable.COLUMN_SCALE_ID+"=?", new String[]{Integer.toString(obj.getId())});
                if(res1>=0){
                    int res2=this.database.update(ScalesTable.TABLE_SCALES, cValues, ScalesTable.COLUMN_ID+"=?", new String[]{Integer.toString(obj.getId())});
                    if(res2>=0){
                        result=obj;
                    }
                }
            }
            
            if(obj.getId()>0){
                List<String> scaleValues=obj.getScaleValues();
                if(scaleValues!=null && scaleValues.size()>0){
                    for(int i=0;i<scaleValues.size();i++){
                        String scaleValue=scaleValues.get(i);
                        
                        ContentValues cScaleValues=new ContentValues();
                        cScaleValues.put(ScaleValuesTable.COLUMN_SCALE_ID,obj.getId());
                        cScaleValues.put(ScaleValuesTable.COLUMN_ORDER,i);
                        cScaleValues.put(ScaleValuesTable.COLUMN_SCALE_VALUE,scaleValue);
                        
                        this.database.insert(ScaleValuesTable.TABLE_SCALES_VALUES, null, cScaleValues);
                    }
                }
            }
        }
        return result;
    }

    /**
     * reloads the scale from database
     * @param Scale scale
     * @return Scale scale - null if it fails
     * @Override
     */
    public Scale refreshObject(Scale obj) {
        if(obj!=null && obj.getId()>0){
            return this.getObjectById(obj.getId());
        }else{
            return null;
        }
    }

    /**
     * deletes a scale in database
     * @param Scale scale
     * @return boolean - if success then true else false
     * @Override
     */
    public boolean deleteObject(Scale obj) {
        if(obj!=null && obj.getId()>0){
            return this.deleteById(obj.getId());
        }else{
            return false;
        }
    }

    // ################# additional methods #################
    /**
     * returns an array of full Scale objects for cursor
     * the cursor needs all columns from ScalesTable
     * @param Cursor cursor
     * @return Scale[] scale
     */
    public Scale[] fromCursorToScale(Cursor cursor){
        Scale[] result=null;
        if(cursor!=null && cursor.getCount()>0){
            result=new Scale[cursor.getCount()];
            int i=0;
            while(cursor.moveToNext() && i<result.length){
                result[i] = new Scale(cursor.getInt(cursor.getColumnIndex(ScalesTable.COLUMN_ID)));
                result[i].setQuestionId(cursor.getInt(cursor.getColumnIndex(ScalesTable.COLUMN_SCALE_QUESTION_ID)));
                result[i].setDBInternalOrderOfScales(cursor.getInt(cursor.getColumnIndex(ScalesTable.COLUMN_ORDER_ID)));
                result[i].setPoleLeft(cursor.getString(cursor.getColumnIndex(ScalesTable.COLUMN_POL_LEFT)));
                result[i].setPoleRight(cursor.getString(cursor.getColumnIndex(ScalesTable.COLUMN_POL_RIGHT)));
                
                String[] selArgs={Integer.toString(result[i].getId())};
                Cursor cursor2=this.database.query(ScaleValuesTable.TABLE_SCALES_VALUES, ScaleValuesTable.ALL_COLUMNS, ScaleValuesTable.COLUMN_SCALE_ID+"=?", selArgs, null, null, ScaleValuesTable.COLUMN_ORDER+" ASC");
                while(cursor2.moveToNext()){
                    result[i].addScaleValue(cursor2.getString(cursor2.getColumnIndex(ScaleValuesTable.COLUMN_SCALE_VALUE)));
                }
                cursor2.close();
                
                //increment
                i++;
            }
        }else{
            Log.d("ScaleHelper", "fromCursorToScale() -- Cursor is empty -- no scale in cursor");
        }
        
        return result;
    }
    
    public Scale[] getAllByScaleQuestionId(int scaleQuestionId) {
        Scale[] result=null;
        if(scaleQuestionId>0){
            Cursor cursor=this.database.query(ScalesTable.TABLE_SCALES, ScalesTable.ALL_COLUMNS, ScalesTable.COLUMN_SCALE_QUESTION_ID+"=?", new String[]{Integer.toString(scaleQuestionId)}, null, null, ScalesTable.COLUMN_ORDER_ID+" ASC");
            Scale[] scales=this.fromCursorToScale(cursor);
            if(scales!=null && scales.length>0){
                result=scales;
            }
            cursor.close();
        }
        return result;   
    }
    
    // ################# getter and setter ################# 
    @Override
    public DatabaseConnector getDbc() {
        return dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}
