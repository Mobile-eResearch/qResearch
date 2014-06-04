
package de.eresearch.app.logic.model;

import android.content.Context;

import de.eresearch.app.R;

public class LogEntry extends Loggable {

    private int mLogEntryId;
    private int mYCoord;
    private int mOldYCoord;
    private int mOldXCoord;
    private int mXCoord;
    private int mQSortId;
    private Item mSelectedItem;

    /**
     * Creates a new log entry with the given id.
     * 
     * @param id The id of this log entry
     */
    public LogEntry(int id) {
        mLogEntryId = id;
    }

    /**
     * @param id The id of this log entry
     */
    public void setId(int id) {
        mLogEntryId = id;
    }

    /**
     * @return The id of this log entry
     */
    public int getId() {
        return mLogEntryId;
    }

    /**
     * @return The x-coordinate of the item's old position in the pyramid
     */
    public int getFromX() {
        return mOldXCoord;
    }

    /**
     * @return The y-coordinate of the item's old position in the pyramid
     */
    public int getFromY() {
        return mOldYCoord;
    }

    /**
     * @param x The old x-position of the item
     * @param y The old y-position of the item
     */
    public void setFrom(int x, int y) {
        mOldXCoord = x;
        mOldYCoord = y;
    }

    /**
     * @return The x-coordinate of the item's new position in the pyramid
     */
    public int getToX() {
        return mXCoord;
    }

    /**
     * @return The y-coordinate of the item's new position in the pyramid
     */
    public int getToY() {
        return mYCoord;
    }

    /**
     * @param x The new x-position of the item
     * @param y The new y-position of the item
     */
    public void setTo(int x, int y) {
        mXCoord = x;
        mYCoord = y;

    }

    /**
     * @return The item affected by the move
     */
    public Item getItem() {
        return mSelectedItem;
    }

    /**
     * @param item The item affected by the move
     */
    public void setItem(Item item) {
        mSelectedItem = item;

    }

    public int getQSortId() {
        return mQSortId;
    }

    public void setQSortId(int id) {
        mQSortId = id;
    }

    @Override
    public String toString(Context context) {
        String s;

        if (mOldXCoord < 0) {
            s = context.getString(R.string.loggable_logentry_in, mSelectedItem.toString(),
                    mXCoord + ", " + mYCoord);
        }
        else if (mXCoord < 0) {
            s = context.getString(R.string.loggable_logentry_out, mSelectedItem.toString(),
                    mOldXCoord + ", " + mOldYCoord);
        }
        else {
            s = context.getString(R.string.loggable_logentry, mSelectedItem.toString(),
                    mOldXCoord + ", " + mOldYCoord, mXCoord + ", " + mYCoord);
        }

        return s;
    }
}
