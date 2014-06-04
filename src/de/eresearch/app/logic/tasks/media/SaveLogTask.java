
package de.eresearch.app.logic.tasks.media;

import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.logic.model.Log;

/**
 * Task to save a log to the database.
 * 
 * @deprecated Unused
 */
@Deprecated
public class SaveLogTask extends AsyncTask<Void, Void, Void> {

    public final CountDownLatch signal = new CountDownLatch(1);

    public SaveLogTask(Log l) {
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

}
