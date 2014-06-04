package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.PicturesTable;
import de.eresearch.app.db.tables.QsortItems;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Picture;

/**
 * Helper for Item model object
 * @author Jurij Wöhlke
 */
public class ItemHelper extends AbstractObjectHelper<Item> implements IdObjectHelperInterface<Item>,StudyObjectHelperInterface<Item>{
    
    /**
     * constructor
     * @param dbconn
     */
    public ItemHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# StudyObjectHelperInterface methods #################
    /**
     * returns all items which belong to the study
     * @param int identifier
     * @return Item[]
     * @Override 
     */
    public Item[] getAllByStudyId(int id) {
        String query="SELECT"
                +" "+PicturesTable.TABLE_PICTURES+"."+PicturesTable.COLUMN_ITEM_ID+","
                +" "+QsortItems.TABLE_QSORT_ITEMS+"."+QsortItems.COLUMN_STATEMENT+","
                +" "+QsortItems.TABLE_QSORT_ITEMS+"."+QsortItems.COLUMN_STUDY_ID+","
                +" "+PicturesTable.TABLE_PICTURES+"."+PicturesTable.COLUMN_PATH
                +" FROM "+PicturesTable.TABLE_PICTURES
                +" INNER JOIN "+ QsortItems.TABLE_QSORT_ITEMS
                +" ON "+PicturesTable.COLUMN_ITEM_ID+"="+QsortItems.COLUMN_ID
                +" WHERE "+QsortItems.COLUMN_STUDY_ID+"=?"
                +";";
        String[] arg={Integer.toString(id)};
        Cursor cursor=this.database.rawQuery(query,arg);
        Item[] result=null;
        if(cursor.getCount()>0){
            result=new Item[cursor.getCount()];
            int i=0;
            Picture tmp;
            while(cursor.moveToNext()){
                int itemId=cursor.getInt(cursor.getColumnIndex(PicturesTable.COLUMN_ITEM_ID));
                String path=cursor.getString(cursor.getColumnIndex(PicturesTable.COLUMN_PATH));
                String statement=cursor.getString(cursor.getColumnIndex(QsortItems.COLUMN_STATEMENT));
                tmp=new Picture(itemId);
                tmp.setFilePath(path);
                tmp.setStatement(statement);
                result[i]=(Item) tmp;
                i++;
            }
        }
        cursor.close();
        return result;
    }
    
    // ################# AbstractObjectHelper methods #################
    /**
     * saves an item and returns it again if successful
     * needs studyId in item!
     * @param Item
     * @return Item
     * @Override
     */
    public Item saveObject(Item item){
        Item res=null;
        if(item instanceof Picture){
            Picture picture=(Picture) item;
            
            //insert:
            if(picture.getId()<=0){
                //QSortItems:
                ContentValues cItemValues=new ContentValues();
                cItemValues.put(QsortItems.COLUMN_TYPE,EnumTable.ENUM_ITEM_TYPE_PICTURE);
                cItemValues.put(QsortItems.COLUMN_STUDY_ID,picture.getStudyID());
                cItemValues.put(QsortItems.COLUMN_STATEMENT,picture.getStatement());
                long newId = this.database.insert(QsortItems.TABLE_QSORT_ITEMS, null, cItemValues);
                
                //Picture:
                ContentValues cPictureValues=new ContentValues();
                cPictureValues.put(PicturesTable.COLUMN_PATH,picture.getFilePath());
                cPictureValues.put(PicturesTable.COLUMN_ITEM_ID,newId);
                long newId2 = this.database.insert(PicturesTable.TABLE_PICTURES, null, cPictureValues);
                
                if(newId2==newId){
                    picture.setId((int)newId);
                    res = (Item) picture;
                }
            //update:
            }else{
                //Picture Table update:
                ContentValues cPictureValues=new ContentValues();
                cPictureValues.put(PicturesTable.COLUMN_PATH,picture.getFilePath());
                String[] whereargs={Integer.toString(picture.getId())};
                int updateRes=this.database.update(PicturesTable.TABLE_PICTURES, cPictureValues,PicturesTable.COLUMN_ITEM_ID+"=?",whereargs);
                
                //QSortItems:
                ContentValues cItemValues=new ContentValues();
                cItemValues.put(QsortItems.COLUMN_TYPE,EnumTable.ENUM_ITEM_TYPE_PICTURE);
                cItemValues.put(QsortItems.COLUMN_STUDY_ID,picture.getStudyID());
                cItemValues.put(QsortItems.COLUMN_STATEMENT,picture.getStatement());
                String[] whereargs2={Integer.toString(picture.getId())};
                int updateRes2=this.database.update(QsortItems.TABLE_QSORT_ITEMS, cItemValues,QsortItems.COLUMN_ID+"=?",whereargs2);
                
                if(updateRes>0 && updateRes2==updateRes){
                    res = (Item) picture;
                }
            }
        }
        return res;
    }    

    /**
     * reloads the item from database
     * @param Item item
     * @return Item item
     * @Override
     */
    public Item refreshObject(Item item){
        return this.getObjectById(item.getId());
    }

    /**
     * deletes the item from database
     * @param Item item
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(Item item){
        return this.deleteById(item.getId());
    }

    // ################# IdObjectHelperInterface methods #################
    /**
     * loads the item for this id from database
     * @param int itemId
     * @Override
     */
    public Item getObjectById(int id) {
        Item result=null;
        String[] selectionArgs={Integer.toString(id)};
        Cursor cursor=this.database.query(PicturesTable.TABLE_PICTURES, PicturesTable.ALL_COLUMNS, PicturesTable.COLUMN_ITEM_ID+"=?",selectionArgs, null, null, null);
        if(cursor.moveToNext()){
            Picture pic=new Picture(cursor.getInt(cursor.getColumnIndex(PicturesTable.COLUMN_ITEM_ID)));
            pic.setFilePath(cursor.getString(cursor.getColumnIndex(PicturesTable.COLUMN_PATH)));
            Cursor cursor2=this.database.query(QsortItems.TABLE_QSORT_ITEMS, QsortItems.ALL_COLUMNS, QsortItems.COLUMN_ID+"=?",selectionArgs, null, null, null);
            if(cursor2.moveToNext()){
                if(pic.getId()==cursor2.getInt(cursor2.getColumnIndex(QsortItems.COLUMN_ID))){
                    pic.setStatement(cursor2.getString(cursor2.getColumnIndex(QsortItems.COLUMN_STATEMENT)));
                    pic.setStudyID(cursor2.getInt(cursor2.getColumnIndex(QsortItems.COLUMN_STUDY_ID)));
                    result=(Item) pic;
                }
            }
            cursor2.close();
        }
        cursor.close();
        return result;
    }

    /**
     * delete the item for this id 
     * @param int itemId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(int id) {
        String[] args={Integer.toString(id)};
        int res1=this.database.delete(PicturesTable.TABLE_PICTURES, PicturesTable.COLUMN_ITEM_ID+"=?", args);
        int res2=this.database.delete(QsortItems.TABLE_QSORT_ITEMS, QsortItems.COLUMN_ID+"=?", args);
        if(res1>=0 && res2>=0){
            return true;
        }
        return false;
    }
    
    // ################# Other methods #################

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
