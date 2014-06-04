
package de.eresearch.app.logic.model;

import android.content.Context;
import android.view.View;

import de.eresearch.app.util.Util;

public abstract class Item {
    /**
     * ItemID (-1 if not yet set)
     */
    private int mID = -1;

    /**
     * Item's Statement
     */
    private String mStatement;

    /**
     * Row of the pyramide, the item is in (-1 if not yet set)
     */
    private int mRow = -1;

    /**
     * Column of the pyramide, the item is in (-1 if not yet set)
     */
    private int mColumn = -1;

    /**
     * ID of the study, to which the item is attached (-1 if not yet set)
     */
    private int mStudyID = -1;

    /**
     * Creates a new item with the given id.
     * 
     * @param id The id of this item
     */
    public Item(int id) {
        mID = id;
    }

    /**
     * @param id The id of this item
     */
    public void setId(int id) {
        mID = id;
    }

    /**
     * @return The id of this item
     */
    public int getId() {
        return mID;
    }

    /**
     * @param statement the statement of this item
     */
    public void setStatement(String statement) {
        mStatement = statement;
    }

    /**
     * @return The statement of this item
     */
    public String getStatement() {
        return mStatement;
    }

    /**
     * Sets the row that this item is in a pyramid. Counting begins at the
     * uppermost row of the pyramid (row 0). If this item is not in a cell of a
     * pyramid, the row is -1.
     * 
     * @param row The row
     */
    public void setRow(int row) {
        mRow = row;
    }

    /**
     * Sets the column that this item is in a pyramid. Counting begins at the
     * left column of the pyramid (column 0). If this item is not in a cell of a
     * pyramid, the column is -1.
     * 
     * @param row The column
     */
    public void setColumn(int column) {
        mColumn = column;
    }

    /**
     * Sets the position that this item is in a pyramid. If this item is not in
     * a cell of a pyramid, the position is -1,-1.
     * 
     * @param row The column
     * @param column The column
     * @see {@link Item#setRow(int)}
     * @see {@link Item#setColumn(int)}
     */
    public void setPosition(int row, int column) {
        setRow(row);
        setColumn(column);
    }

    /**
     * resets the position of this item, so that it's not in a pyramid anymore.
     * So simply call {@link Item#setPosition(int, int)} with -1,-1.
     */
    public void resetPosition() {
        setPosition(-1, -1);
    }

    /**
     * Returns the row that this item is in a pyramid. Counting begins at the
     * uppermost row of the pyramid (row 0). If this item is not in a cell of a
     * pyramid, the row is -1.
     * 
     * @return row The row
     */
    public int getRow() {
        return mRow;
    }

    /**
     * Returns the column that this item is in a pyramid. Counting begins at the
     * left column of the pyramid (column 0). If this item is not in a cell of a
     * pyramid, the column is -1.
     * 
     * @return row The column
     */
    public int getColumn() {
        return mColumn;
    }

    /**
     * Returns the ID of the {@link Study} to which the {@link Item} is
     * attached. If the Item is not yet attached to a Study, the ID is -1.
     * 
     * @return int StudyID
     */
    public int getStudyID() {
        return mStudyID;
    }

    /**
     * Sets the StudyID for the {@link Item}.
     * 
     * @param studyID
     */
    public void setStudyID(int studyID) {
        mStudyID = studyID;
    }

    /**
     * Returns a {@link View} object that can display this item on the screen.
     * 
     * @return The {@link View} object
     */
    public abstract View getView(Context context);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Item)) {
            return false;
        }

        Item other = (Item) o;

        if (mID != other.mID) {
            return false;
        }

        if (mStudyID != other.mStudyID) {
            return false;
        }

        if (mRow != other.mRow) {
            return false;
        }

        if (mColumn != other.mColumn) {
            return false;
        }

        if (!Util.nullSafeEquals(mStatement, other.mStatement)) {
            return false;
        }

        return true;
    }

}
