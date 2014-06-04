
package de.eresearch.app.logic.tasks.media;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.LogHelper;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.Study;

import java.util.concurrent.CountDownLatch;

/**
 * Task to get a AudioLog object from the database.
 * 
 * @author liqquid
 */

public class GetAudioLogTask extends AsyncTask<Void, Void, Log[]> {

    private Callbacks callback;
    private Log[] audioLog;
    private Context context;
    private LogHelper logHelper;
    private int audioLogId;

    /**
     * Creates a new Task.
     * 
     * @param context Context of the activity to get the instance from the
     *            database.
     * @param callback Interface which is executed by the activity.
     * @param audioLogId Id of the requested audioLog
     */
    public GetAudioLogTask(Context context, Callbacks callback, int audioLogId) {
        this.context = context;
        this.callback = callback;
        this.audioLogId = audioLogId;
    }

    /**
     * Opens a connection to the database and requests the audio log from a
     * QSort.
     */
    @Override
    protected Log[] doInBackground(Void... arg0) {
        try {
            logHelper = (LogHelper) DatabaseConnector.getInstance(context).getHelper(
                    DatabaseConnector.TYPE_LOG);
            audioLog = logHelper.getAllByQSortId(audioLogId);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        return audioLog;
    }

    /**
     * Executes the callback interface to transfer the list of answers back to
     * the activity.
     */
    public final CountDownLatch signal = new CountDownLatch(1);

    protected void onPostExecute(Study study) {
        callback.onGetAnswersTask(audioLog);
        signal.countDown();
    }

    public interface Callbacks {
        public void onGetAnswersTask(Log[] audioLog);
    }
}
