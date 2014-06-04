
package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.regex.Pattern;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.tables.SortLoggingTable;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.LogEntry;

/**
 * Helper for LogEntry model object
 * 
 * @author Marcel Neumann
 * @author Jurij Wöhlke (edited + parts of doc)
 */
public class LogEntryHelper extends AbstractObjectHelper<LogEntry> implements
        IdObjectHelperInterface<LogEntry>, QSortObjectHelperInterface<LogEntry> {

    /**
     * standard constructor
     * 
     * @param dbconn
     */
    public LogEntryHelper(final DatabaseConnector dbconn) {
        super(dbconn);
    }

    /**
     * Returns all LogEntries with the given QSort Id
     * 
     * @param int qsort id
     * @return LogEntry[] logEntry
     * @Override
     */
    public LogEntry[] getAllByQSortId(int id) {
        LogEntry[] result = null;
        Cursor cursor = this.database.query(SortLoggingTable.TABLE_SORT_LOGGING,
                SortLoggingTable.ALL_COLUMNS, SortLoggingTable.COLUMN_QSORT_ID + "=" + id, null,
                null, null, null);
        if (cursor.getCount() > 0) {
            result = new LogEntry[cursor.getCount()];
            int i = 0;
            while (i < cursor.getCount()) {
                if (cursor.moveToNext()) {
                    LogEntry log = this.cursorToLogEntry(cursor);
                    result[i] = log;
                } else {
                    break;
                }
                i++;
            }
        }
        cursor.close();
        return result;
    }

    /**
     * Returns a LogEntry with the given id
     * 
     * @param int LogEntry id
     * @return LogEntry logEntry
     * @Override
     */
    public LogEntry getObjectById(int id) {
        if (id > 0) {
            Cursor cursor = this.database.query(SortLoggingTable.TABLE_SORT_LOGGING,
                    SortLoggingTable.ALL_COLUMNS, SortLoggingTable.COLUMN_ID + "=" + id, null,
                    null, null, null);
            cursor.moveToFirst();
            LogEntry result = cursorToLogEntry(cursor);
            cursor.close();
            return result;
        }
        else {
            return null;
        }

    }

    /**
     * Translates a Cursor to an LogEntry
     * 
     * @param cursor cursor
     * @return LogEntry logEntry
     */
    private LogEntry cursorToLogEntry(Cursor cursor) {
        if (cursor!=null) {
            LogEntry logEntry = new LogEntry(cursor.getInt(cursor
                    .getColumnIndex(SortLoggingTable.COLUMN_ID)));
            final String time = cursor.getString(cursor
                    .getColumnIndex(SortLoggingTable.COLUMN_TIME));
            final long timeInLong = Long.parseLong(time);
            logEntry.setTime(timeInLong);
            final int qsortIdentifier = cursor.getInt(cursor
                    .getColumnIndex(SortLoggingTable.COLUMN_QSORT_ID));
            logEntry.setQSortId(qsortIdentifier);
            int itemId = cursor.getInt(cursor.getColumnIndex(SortLoggingTable.COLUMN_ITEM_ID));
            ItemHelper itemHelper = new ItemHelper(this.dbc);
            Item item = itemHelper.getObjectById(itemId);
            logEntry.setItem(item);
            final String toXandYString = cursor.getString(cursor
                    .getColumnIndex(SortLoggingTable.COLUMN_POSITION_AFTER));
            String[] splitToXAndY = toXandYString.split(Pattern.quote("/"));
            int toX = Integer.parseInt(splitToXAndY[0]);
            int toY = Integer.parseInt(splitToXAndY[1]);
            logEntry.setTo(toY, toX);
            final String fromXandYString = cursor.getString(cursor
                    .getColumnIndex(SortLoggingTable.COLUMN_POSITION_BEFORE));
            String[] splitFromXAndY = fromXandYString.split(Pattern.quote("/"));
            int fromX = Integer.parseInt(splitFromXAndY[0]);
            int fromY = Integer.parseInt(splitFromXAndY[1]);
            logEntry.setFrom(fromX, fromY);
            return logEntry;
        } else {
            return null;
        }
    }

    /**
     * Deletes a LogEntry
     * 
     * @param int LogEntry id
     * @return LogEntry
     * @Override
     */
    public boolean deleteById(int id) {
        int res = this.database.delete(SortLoggingTable.TABLE_SORT_LOGGING,
                SortLoggingTable.COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        if (res >= 0)
            return true;
        else
            return false;
    }

    /**
     * Saves a LogEntry to the database and returns the saved LogEntry Object
     * 
     * @param LogEntry
     * @return LogEntry
     * @Override
     */
    public LogEntry saveObject(LogEntry obj) {
        if (obj != null) {
            ContentValues cvalues = new ContentValues();
            cvalues.put(SortLoggingTable.COLUMN_QSORT_ID, obj.getQSortId());
            cvalues.put(SortLoggingTable.COLUMN_ITEM_ID, obj.getItem().getId());
            cvalues.put(SortLoggingTable.COLUMN_POSITION_BEFORE,
                    obj.getFromX() + "/" + obj.getFromY());
            cvalues.put(SortLoggingTable.COLUMN_POSITION_AFTER, obj.getToX() + "/" + obj.getToY());
            final String timestamp = String.valueOf(obj.getTime());
            cvalues.put(SortLoggingTable.COLUMN_TIME, timestamp);
            if (obj.getId() <= 0) {
                long newId = this.database.insert(SortLoggingTable.TABLE_SORT_LOGGING, null,
                        cvalues);
                return this.getObjectById((int) newId);
            } else {
                String[] whereArgs = new String[1];
                whereArgs[0] = Integer.toString(obj.getId());
                this.database.update(SortLoggingTable.TABLE_SORT_LOGGING, cvalues,
                        SortLoggingTable.COLUMN_ID + "=?", whereArgs);
                return obj;
            }
        }
        return null;
    }

    /**
     * Refresh an LogEntry Object and return the refreshed object
     * 
     * @param LogEntry obj
     * @retur LogEntry obj
     * @Override
     */
    public LogEntry refreshObject(LogEntry obj) {
        if (obj.getId() > 0) {
            LogEntry oldLogEntry = this.getObjectById(obj.getId());
            oldLogEntry.setFrom(obj.getFromX(), obj.getFromY());
            oldLogEntry.setItem(obj.getItem());
            oldLogEntry.setTime(obj.getTime());
            oldLogEntry.setTo(obj.getToX(), obj.getToY());
            return this.saveObject(oldLogEntry);
        }
        else {
            return null;
        }
    }

    /**
     * deletes an LogEntry from the database
     * 
     * @param LogEntry obj
     * @return int id
     * @Override
     */
    public boolean deleteObject(LogEntry obj) {
        return this.deleteById(obj.getId());
    }

    @Override
    public DatabaseConnector getDbc() {
        return dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc = dbconn;
    }
}
