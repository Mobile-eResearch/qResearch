
package de.eresearch.app.logic.tasks.io;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;

import java.io.File;

/**
 * Task to get an prepared (file path is set) {@link AudioRecord} for a specific
 * phase of a QSort.
 * 
 * @author Moritz
 */
public class PrepareAudioFileTask extends AsyncTask<Void, Void, AudioRecord> {

    private Context mContext;

    private PrepareAudioFileTask.Callbacks mCallback;

    private QSort mQsort;

    private Phase mPhase;

    public static interface Callbacks {
        /**
         * Called by the {@link PrepareAudioFileTask} when the
         * {@link AudioRecord} object has been prepared. The object that is
         * provided contains a valid file path.
         * 
         * @param record The {@link AudioRecord} object
         */
        public void onAudioPrepared(AudioRecord record);
    }

    /**
     * Creates a new {@link PrepareAudioFileTask}.
     * @param context An android application context
     * @param callback An instance of the task's callback interface
     * @param qsort The qsort (must have a valid id obtained from the database)
     * @param phase The phase
     */
    public PrepareAudioFileTask(Context context, PrepareAudioFileTask.Callbacks callback,
            QSort qsort, Phase phase) {
        mContext = context;
        mCallback = callback;
        mQsort = qsort;
        mPhase = phase;
    }

    @Override
    protected AudioRecord doInBackground(Void... params) {
        String audioDir = mContext.getResources().getString(R.string.audiorecord_private_root);

        // Get private internal storage directory
        File internalDir = mContext.getFilesDir();

        // The root directory where all private audio data lies
        File audioRoot = new File(internalDir, audioDir);

        File audioQsortRoot = new File(audioRoot, Integer.toString(mQsort.getId()));

        // create directories when they don't exist
        audioQsortRoot.mkdirs();

        AudioRecord ar = new AudioRecord(-1);
        ar.setFilePath(audioQsortRoot +"/"+mPhase.name());
        ar.setPhase(mPhase);
        ar.setQSortId(mQsort.getId());

        return ar;
    }

    @Override
    protected void onPostExecute(AudioRecord result) {
        mCallback.onAudioPrepared(result);
    }

}
