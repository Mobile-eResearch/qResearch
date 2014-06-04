
package de.eresearch.app.gui.view;

import android.content.Context;
import android.widget.GridLayout;

import de.eresearch.app.gui.QSortView;
import de.eresearch.app.logic.model.Item;

import java.util.List;

/**
 * @deprecated Use {@link QSortView} instead!
 */
@Deprecated
public class SortedPyramidView extends GridLayout {

    private List<Item> mItemList;

    private int mWidth, mHeight;

    public SortedPyramidView(Context context) {
        this(context, null);
    }

    public SortedPyramidView(Context context, List<Item> items) {
        super(context);

        mItemList = items;

        mWidth = 0;
        mHeight = 0;

        // Determine the needed size of the grid
        for (Item i : mItemList) {
            if (i.getRow() >= mHeight) {
                mHeight = i.getRow() + 1;
            }
            if (i.getColumn() >= mWidth) {
                mWidth = i.getColumn() + 1;
            }
        }

        setRowCount(mHeight);
        setColumnCount(mWidth);

        for (Item i : mItemList) {
            addView(i.getView(context), (i.getRow() * mWidth + i.getColumn()));
        }
    }

}
