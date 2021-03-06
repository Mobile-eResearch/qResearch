
package de.eresearch.app.logic.tasks.media;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AudioRecordHelper;
import de.eresearch.app.logic.model.AudioRecord;

import java.util.concurrent.CountDownLatch;

/**
 * Task to save a recorded audio
 * @author Henrik
 */

public class SaveAudioTask extends AsyncTask<Void, Void, AudioRecord> {

    private Context context;
    private AudioRecord aR;
    private AudioRecordHelper audioRecordHelper;
    private Callbacks callback;
    
    // signal for the testcase that the async task finished
    public final CountDownLatch signal = new CountDownLatch(1);

    public SaveAudioTask(Callbacks callback, Context context, AudioRecord aR) {
        this.context = context;
        this.aR = aR;
        this.callback = callback;
    }

    @Override
    protected AudioRecord doInBackground(Void... params) {
        try {
            audioRecordHelper = (AudioRecordHelper) DatabaseConnector.getInstance(context)
                    .getHelper(DatabaseConnector.TYPE_AUDIORECORD);
            aR = audioRecordHelper.saveObject(aR);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        signal.countDown();
        return aR;
    }

    protected void onPostExecute(AudioRecord aR){
        callback.onSaveAudioTaskUpdate(aR);
        signal.countDown();
    }

    public static interface Callbacks {
        public void onSaveAudioTaskUpdate(AudioRecord aR);
    }
}
