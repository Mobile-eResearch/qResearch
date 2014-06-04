/** All tasks which handles the Qsort functions.
 * 
 */

package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.R;
import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.QSort;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Starts the process to delete a QSort from the database.
 */
public class DeleteQSortTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private Callbacks mCallback;
    private int mQsortId;
    private QSort mQSort;
    private QSortHelper qsortHelper;
    public final CountDownLatch signal = new CountDownLatch(1);

    /**
     * Creates a new task.
     * 
     * @param qSortId id from the qsort which should be deleted.
     */
    public DeleteQSortTask(Context context, Callbacks callback, QSort qsort) {
        mContext = context;
        mQSort = qsort;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!mQSort.getLogs().isEmpty()){
        for (Log l:mQSort.getLogs()){
            if (l.getAudio() != null){
                //Workaround cause the path in a qsort object is wrong
                String internalDir = mContext.getFilesDir().toString();
                String audioDir = mContext.getResources().getString(R.string.audiorecord_private_root);
                String path = internalDir +"/"+ audioDir + "/"+ mQSort.getId() + "/" + l.getPhase().name()+ "_"+ l.getAudio().getPartNumber() + ".3gp";

                File file = new File(path);
                file.delete();
                System.out.println("Deleting: "+ path);
            }
          }
        }

        Boolean back = false;
        try {
            qsortHelper = (QSortHelper) DatabaseConnector.getInstance(mContext).getHelper(
                    DatabaseConnector.TYPE_QSORT);
            back = qsortHelper.deleteById(mQSort.getId());
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        return back;
    }

    public interface Callbacks {
        public void onDeleteQSortTask();

    }

    protected void onPostExecute(Boolean result) {
        mCallback.onDeleteQSortTask();
        signal.countDown();
    }

}
