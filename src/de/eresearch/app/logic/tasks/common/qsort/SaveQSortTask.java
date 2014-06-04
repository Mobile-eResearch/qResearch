
package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.logic.model.QSort;

/**
 * Task to save a completed Qsort. Returns the id for the QSort or
 * <code> -1 </code>, if something failed!
 * 
 * @author Tammo
 */
public class SaveQSortTask extends AsyncTask<Void, Void, QSort> {

    private Context mContext;

    private Callbacks mCallback;
    public final CountDownLatch signal = new CountDownLatch(1);
    private QSort mQSort;


    public SaveQSortTask(Context context, Callbacks callback, QSort qsort) {
        mContext = context;
        mCallback = callback;
        mQSort = qsort;
    }

    @Override
    protected QSort doInBackground(Void ...arg0) {
        QSort sort = null;
        try {
            QSortHelper qsh = (QSortHelper) DatabaseConnector.getInstance(mContext).getHelper(
                    DatabaseConnector.TYPE_QSORT);

            sort = qsh.saveObject(mQSort);

        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }

        return sort;
    }

    protected void onPostExecute(QSort sort) {
        if (sort == null) {
            mCallback.onSaveQSortTask(-1);
        } else {
            mCallback.onSaveQSortTask(sort.getId());
        }
        signal.countDown();
    }

    public interface Callbacks {
        public void onSaveQSortTask(int id);
    }

}
