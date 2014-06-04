package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.AudioRecordsTable;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.db.tables.NotesTable;
import de.eresearch.app.db.tables.RecordsTable;
import de.eresearch.app.logic.model.Note;

/**
 * Helper for Note model object
 * @author Jurij Wöhlke
 */
public class NoteHelper extends AbstractObjectHelper<Note> implements IdObjectHelperInterface<Note>,QSortObjectHelperInterface<Note>{
    
    /**
     * standard constructor
     * @param dbconn
     */
    public NoteHelper(DatabaseConnector dbconn) {
        super(dbconn);
    }
    
    // ################# QSortIdObjectHelperInterface methods #################
    /**
     * returns an array of all notes for this qsort
     * @param int qSortId
     * @return Note note
     * @Override
     */
    public Note[] getAllByQSortId(int identifier) {
        Note[] result=null;
        final String[] whereArgs={Integer.toString(identifier)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*, "+NotesTable.TABLE_NOTES+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+NotesTable.TABLE_NOTES
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+NotesTable.TABLE_NOTES+"."+NotesTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_QSORT_ID+"=?"
                + "";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        result=getNotesForCursor(cursor);
        cursor.close();
        return result;

    }
    
    // ################# IdObjectHelperInterface methods #################

    /**
     * returns the Note for this id
     * @param int nodeId
     * @return Note note
     * @Override
     */
    public Note getObjectById(final int identifier) {
        Note result=null;
        final String[] whereArgs={Integer.toString(identifier)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*, "+NotesTable.TABLE_NOTES+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+NotesTable.TABLE_NOTES
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+NotesTable.TABLE_NOTES+"."+NotesTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"=?"
                + "";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        Note[] notes=getNotesForCursor(cursor);
        if(notes!=null && notes.length>0){
            result=notes[0];
        }
        cursor.close();
        return result;
    }

    /**
     * deletes the Note from database by id
     * @param int noteId
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteById(final int identifier) {
        boolean result=false;
        final String[] whereArgs={Integer.toString(identifier)};
        final int res2=this.database.delete(NotesTable.TABLE_NOTES, NotesTable.COLUMN_RECORD_ID+"=?", whereArgs);
        final int res1=this.database.delete(RecordsTable.TABLE_RECORDS, RecordsTable.COLUMN_ID+"=?", whereArgs);
        if(res1 >= 0 && res2 >= 0){
            result=true;
        }
        return result;
    }
    
    // ################# AbstractObjectHelper methods #################

    /**
     * saves the Note in database 
     * @param Note note
     * @return Note note
     * @Override
     */
    public Note saveObject(final Note note) {
        Note result=null;
        
        // #### Values for insert/update in RecordsTable
        ContentValues cvaluesRecord=new ContentValues();

        cvaluesRecord.put(RecordsTable.COLUMN_QSORT_ID, note.getQSortId());
        cvaluesRecord.put(RecordsTable.COLUMN_DATATYPE_OF_RECORD, EnumTable.ENUM_RECORD_DATATYPE_NOTE);
        cvaluesRecord.put(RecordsTable.COLUMN_TYPE_OF_RECORD, EnumTable.getQPhaseIntForModelEnum(note.getPhase()));
        
        // #### Values for insert/update in AudioRecordsTable
        ContentValues cvaluesNote=new ContentValues();
        cvaluesNote.put(NotesTable.COLUMN_NAME, note.getTitle());
        cvaluesNote.put(NotesTable.COLUMN_TEXT, note.getText());
        cvaluesNote.put(NotesTable.COLUMN_TIME, Long.toString(note.getTime()));
        //cvaluesNote.put(NotesTable.COLUMN_QUESTION_ID, note.);
        
        //Insert:
        if(note.getId()<=0){
            long identifier1=database.insert(RecordsTable.TABLE_RECORDS, null, cvaluesRecord);
            if(identifier1>0){
                cvaluesNote.put(AudioRecordsTable.COLUMN_RECORD_ID, identifier1);
                long identifier2=database.insert(NotesTable.TABLE_NOTES, null, cvaluesNote);
                if(identifier1==identifier2){
                    note.setId((int)identifier1);
                    result=note;
                }
            }else{
                //remove record entry
                this.deleteById((int)identifier1);
            }
        //Update:
        }else{
            cvaluesNote.put(AudioRecordsTable.COLUMN_RECORD_ID, note.getId());
            String[] whereArgs={Integer.toString(note.getId())};
            int res1=database.update(RecordsTable.TABLE_RECORDS, cvaluesRecord, RecordsTable.COLUMN_ID+"=?", whereArgs);
            int res2=database.update(NotesTable.TABLE_NOTES, cvaluesNote, AudioRecordsTable.COLUMN_RECORD_ID+"=?", whereArgs);
            if(res1>=0 && res2>=0){
                result=note;
            }
        }
        return result;
    }

    /**
     * reloads the Note object from database
     * @param Note note
     * @return Note note
     * @Override
     */
    public Note refreshObject(final Note note) {
        return this.getObjectById(note.getId());
    }
    
    /**
     * delete the note object from database
     * @param Note note
     * @return boolean - returns true if success else false
     * @Override
     */
    public boolean deleteObject(final Note note) {
        return this.deleteById(note.getId());
    }
    
    // ################# additional methods #################
    /**
     * returns all Notes for qsort id and filters it by enum phase
     * @param identifier
     * @param enumPhase
     * @return Note[]
     */
    public Note[] getAllByQSortIdAndFilterByPhase(int identifier,int enumPhase) {
        Note[] result=null;
        final String[] whereArgs={Integer.toString(identifier),Integer.toString(enumPhase)};
        String query="SELECT "+RecordsTable.TABLE_RECORDS+".*, "+NotesTable.TABLE_NOTES+".*"
                + " FROM "+RecordsTable.TABLE_RECORDS
                + " INNER JOIN "+NotesTable.TABLE_NOTES
                + " ON "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_ID+"="+NotesTable.TABLE_NOTES+"."+NotesTable.COLUMN_RECORD_ID
                + " WHERE "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_QSORT_ID+"=?"
                + " AND "+RecordsTable.TABLE_RECORDS+"."+RecordsTable.COLUMN_TYPE_OF_RECORD+"=?"
                + ";";
        Cursor cursor=this.database.rawQuery(query, whereArgs);
        result=getNotesForCursor(cursor);
        cursor.close();
        return result;
    }
    
    /**
     * returns an array of Note for this cursor
     * in the cursor this method needs all columns from
     * RecordsTable and NotesTable with a join on recordsId
     * @param Cursor cursor
     * @return Note[] notes
     */
    private Note[] getNotesForCursor(Cursor cursor){
        Note[] result=null;
        if(cursor.getCount()>0){
            int i=0;
            result=new Note[cursor.getCount()];
            while(cursor.moveToNext() && i<result.length){
                result[i]=new Note(cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_ID)));
                result[i].setQSortId(cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_QSORT_ID)));
                int phase=cursor.getInt(cursor.getColumnIndex(RecordsTable.COLUMN_TYPE_OF_RECORD));
                result[i].setPhase(EnumTable.getModelPhaseForQPhaseEnumId(phase));
                result[i].setTitle(cursor.getString(cursor.getColumnIndex(NotesTable.COLUMN_NAME)));
                result[i].setText(cursor.getString(cursor.getColumnIndex(NotesTable.COLUMN_TEXT)));
                result[i].setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(NotesTable.COLUMN_TIME))));
                i++;
            }
        }
        return result;
    }

    // ################# getter/setter methods #################
    @Override
    public DatabaseConnector getDbc() {
        return this.dbc;
    }

    @Override
    public void setDbc(final DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}
