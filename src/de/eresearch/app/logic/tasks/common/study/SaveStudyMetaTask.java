/** All tasks which contains function of a study.
 * 
 */

package de.eresearch.app.logic.tasks.common.study;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.logic.model.Study;

import java.util.concurrent.CountDownLatch;

/**
 * Task to save the meta data from a study
 */
public class SaveStudyMetaTask extends AsyncTask<Void, Void, Study> {

    private Context context;
    private Study study;
    private StudyHelper sh;
    private Callbacks callback;

    // signal for the testcase that the async task finished
    public final CountDownLatch signal = new CountDownLatch(1);

    public SaveStudyMetaTask(Context context, Study study, Callbacks callback) {
        this.context = context;
        this.study = study;
        this.callback = callback;
    }

    @Override
    protected Study doInBackground(Void... params) {
        try {
            sh = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(
                    DatabaseConnector.TYPE_STUDY);
            study = sh.saveMetaData(study);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return study;
    }

    protected void onPostExecute(Study study) {
        callback.onSaveStudyMeta(study);
    }

    public interface Callbacks {
        public void onSaveStudyMeta(Study study);
    }

}
