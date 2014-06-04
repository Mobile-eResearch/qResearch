
package de.eresearch.app.gui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import de.eresearch.app.gui.QSortView;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class QSortsPyramidDialog extends DialogFragment {

    private QSort mQSort;
    private Context mContext;
    private ArrayList<Picture> mPictureList;
    private Point mSize;
    private int mHeight;
    private Pyramid mPyramid;
    private LinearLayout mLayout;

    public static final String QSORT_ID = "de.eresearch.app.gui.dialogs.QSortsPyramidDialog.QSORT_ID";

    public QSortsPyramidDialog(Context context, QSort qsort, Point size, int height, Pyramid pyramid) {
        mQSort = qsort;
        mContext = context;
        mSize = size;
        mPictureList = new ArrayList<Picture>();
        mHeight = height;
        mPyramid = pyramid;

        for (Item item : mQSort.getSortedItems()) {
            mPictureList.add((Picture) item);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        
        mLayout = new LinearLayout(mContext);       
        Log.d("TEST",""+ mSize.x + "," + mSize.y);
        
        QSortView q = new QSortView(mContext);
        q.onCreate(mSize, mPyramid, mHeight, mPictureList, true);
        mLayout.addView(q.getContent());
        builder.setView(mLayout);
        return builder.create();
    }
   
}
