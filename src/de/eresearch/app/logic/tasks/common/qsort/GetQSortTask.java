
package de.eresearch.app.logic.tasks.common.qsort;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.logic.model.QSort;

/**
 * Task to get all data from a Qsort by id from the database.
 */
public class GetQSortTask extends AsyncTask<Void, Void, QSort> {
    
    private int mQSortId;
    private Context mContext;
    private Callbacks mCallback;
    private QSortHelper qh;
    private QSort mQSort;
    private ProgressDialog mPd;

    public GetQSortTask(Context context, int qsortId, Callbacks callback) {
        mContext = context;
        mQSortId = qsortId;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Loading...");
        mPd.show();
    }

    @Override
    protected QSort doInBackground(Void... arg0) {
        try {
            qh = (QSortHelper) DatabaseConnector.getInstance(mContext).getHelper(DatabaseConnector.TYPE_QSORT);
            mQSort = qh.getObjectById(mQSortId);

        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if(mQSort == null){
            mQSort = new QSort(-1,-1);
        }
        return mQSort;
    }
    
    protected void onPostExecute(QSort qsort){
        mCallback.onGetQSortTaskUpdate(qsort);
        mPd.dismiss();
    }
    
    public interface Callbacks{
        public void onGetQSortTaskUpdate(QSort qsort);
        
    }

}
