package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.AudioRecordsTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.RecordsTable;
import de.eresearch.app.logic.model.AudioRecord;

/**
 * Helper for AudioRecord model helper
 * @author Jurij Wöhlke
 */
public class AudioRecordHelper extends AbstractObjectHelper<AudioRecord> implements IdObjectHelperInterface<AudioRecord>,QSortObjectHelperInterface<AudioRecord> {

    /**
     * standard constructor
     * @param dbconn
     */
    public AudioRecordHelper(final DatabaseConnector dbconn) {
        super(dbconn);
    }
    
    // ################# QSortIdObjectHelperInterface methods #################

    /**
     * returns all AudioRecords for a QSort
     * @param int QSortId
     * @return AudioRecord[]
     * @Override
     */
    public AudioRecord[] getAllByQSortId(final int identifier) {
        AudioRecord[] result=null;
        final String[] whereArgs={Integer.toString(identifier)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*, "+AudioRecordsTable.TABLE_AUDIORECORDS+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+AudioRecordsTable.TABLE_AUDIORECORDS
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+AudioRecordsTable.TABLE_AUDIORECORDS+"."+AudioRecordsTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_QSORT_ID+"=?"
                + ";";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        result=getAudioRecordsByCursor(cursor);
        cursor.close();
        return result;
    }

    // ################# AbstractObjectHelper methods #################
    
    /**
     * saves an AudioRecord
     * @param audiorecord
     * @return AudioRecord
     * @Override
     */
    public AudioRecord saveObject(AudioRecord audiorecord){
        AudioRecord result=null;
        
        //Values for insert/update in RecordsTable
        ContentValues cvaluesRecord=new ContentValues();

        cvaluesRecord.put(RecordsTable.COLUMN_QSORT_ID, audiorecord.getQSortId());
        cvaluesRecord.put(RecordsTable.COLUMN_DATATYPE_OF_RECORD, EnumTable.ENUM_RECORD_DATATYPE_AUDIO);
        cvaluesRecord.put(RecordsTable.COLUMN_TYPE_OF_RECORD, EnumTable.getQPhaseIntForModelEnum(audiorecord.getPhase()));
        
        //Values for insert/update in AudioRecordsTable
        ContentValues cvaluesAudio=new ContentValues();
        cvaluesAudio.put(AudioRecordsTable.COLUMN_PATH, audiorecord.getFilePath());
        cvaluesAudio.put(AudioRecordsTable.COLUMN_PARTNUMBER, audiorecord.getPartNumber());
        
        //Insert:
        if(audiorecord.getId()<=0){
            long identifier1=database.insert(RecordsTable.TABLE_RECORDS, null, cvaluesRecord);
            if(identifier1>0){
                cvaluesAudio.put(AudioRecordsTable.COLUMN_RECORD_ID, identifier1);
                long identifier2=database.insert(AudioRecordsTable.TABLE_AUDIORECORDS, null, cvaluesAudio);
                if(identifier1==identifier2){
                    audiorecord.setId((int)identifier1);
                    result=audiorecord;
                }
            }else{
                //remove record entry
                this.deleteById((int)identifier1);
            }
        //Update:
        }else{
            cvaluesAudio.put(AudioRecordsTable.COLUMN_RECORD_ID, audiorecord.getId());
            String[] whereArgs={Integer.toString(audiorecord.getId())};
            int res1=database.update(RecordsTable.TABLE_RECORDS, cvaluesRecord, RecordsTable.COLUMN_ID+"=?", whereArgs);
            int res2=database.update(AudioRecordsTable.TABLE_AUDIORECORDS, cvaluesAudio, AudioRecordsTable.COLUMN_RECORD_ID+"=?", whereArgs);
            if(res1>0 && res2>0){
                result=audiorecord;
            }
        }
        return result;
    }

    /**
     * refresh AudioRecord
     * @param audiorecord
     * @return AudioRecord
     * @Override
     */
    public AudioRecord refreshObject(AudioRecord audiorecord){
        return this.getObjectById(audiorecord.getId());
    }

    /**
     * deletes AudioRecord in database
     * @param audiorecord
     * @return boolean - true if success - else false
     * @Override 
     */
    public boolean deleteObject(AudioRecord audiorecord){
        return this.deleteById(audiorecord.getId());
    }
    
    // ################# IdObjectHelperInterface methods #################
    
    /**
     * returns AudioRecord for given Id
     * @param int audioRecordId
     * @return AudioRecord audioRecord
     * @Override
     */
    public AudioRecord getObjectById(int identifier) {
        AudioRecord result=null;
        final String[] whereArgs={Integer.toString(identifier)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*, "+AudioRecordsTable.TABLE_AUDIORECORDS+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+AudioRecordsTable.TABLE_AUDIORECORDS
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+AudioRecordsTable.TABLE_AUDIORECORDS+"."+AudioRecordsTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"=?"
                + ";";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        AudioRecord[] records=getAudioRecordsByCursor(cursor);
        if(records!=null && records.length>0){
            result=records[0];
        }
        cursor.close();
        return result;
    }

    /**
     * deletes AudioRecord from database for given Id
     * @param int audioRecordId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(final int identifier) {
        boolean result=false;
        final String[] whereArgs={Integer.toString(identifier)};
        final int res2=this.database.delete(AudioRecordsTable.TABLE_AUDIORECORDS, AudioRecordsTable.COLUMN_RECORD_ID+"=?", whereArgs);
        final int res1=this.database.delete(RecordsTable.TABLE_RECORDS, RecordsTable.COLUMN_ID+"=?", whereArgs);
        if(res1 >= 0 && res2 >= 0){
            result=true;
        }
        return result;
    }
    
 // ################# additional methods #################
    /**
     * returns all AudioRecords for a QSort and filters it by enumPhase
     * @param int QSortId
     * @param int enumPhase
     * @return AudioRecord[]
     */
    public AudioRecord[] getAllByQSortIdAndFilterByPhase(final int identifier, final int enumPhase) {
        AudioRecord[] result=null;
        final String[] whereArgs={Integer.toString(identifier),Integer.toString(enumPhase)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*,"+AudioRecordsTable.TABLE_AUDIORECORDS+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+AudioRecordsTable.TABLE_AUDIORECORDS
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+AudioRecordsTable.TABLE_AUDIORECORDS+"."+AudioRecordsTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_QSORT_ID+"=?"
                + " AND "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_TYPE_OF_RECORD+"=?"
                + ";";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        result=getAudioRecordsByCursor(cursor);
        cursor.close();
        return result;
    }
    
    /**
     * returns an array of AudioRecords for this cursor
     * the method needs all columns from AudioRecordsTable
     * and RecordsTable with join on records id in cursor
     * @param Cursor cursor
     * @return AudioRecord[] audioRecords
     */
    private AudioRecord[] getAudioRecordsByCursor(Cursor cursor){
        AudioRecord[] result=null;
        if(cursor.getCount()>0){
            int i=0;
            result=new AudioRecord[cursor.getCount()];
            while(cursor.moveToNext() && i<result.length){
                result[i]=new AudioRecord(cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_ID)));
                result[i].setQSortId(cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_QSORT_ID)));
                int phase=cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_TYPE_OF_RECORD));
                result[i].setPhase(EnumTable.getModelPhaseForQPhaseEnumId(phase));
                result[i].setFilePath(cursor.getString(cursor.getColumnIndex(AudioRecordsTable.COLUMN_PATH)));
                result[i].setPartNumber(cursor.getInt(cursor.getColumnIndex(AudioRecordsTable.COLUMN_PARTNUMBER)));
                i++;
            }
        }
        return result;
    }
    
    // ################# getter/setter #################
    
    @Override
    public DatabaseConnector getDbc() {
        return this.dbc;
    }

    @Override
    public void setDbc(final DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}
