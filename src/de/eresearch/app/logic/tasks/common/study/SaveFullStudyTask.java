/** Class to save a new or edited Study.
 * 
 */

package de.eresearch.app.logic.tasks.common.study;

import android.content.Context;
import android.os.AsyncTask;
import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.logic.model.Study;

/**
 * Saves a study to the database.
 */
public class SaveFullStudyTask extends AsyncTask<Void, Void, Study> {
    public final CountDownLatch signal = new CountDownLatch(1);
    private Context context;
    private Study study;
    private Study savedStudy;
    private Callbacks callback;
    private StudyHelper sh;

    public SaveFullStudyTask(Context context, Study study, Callbacks callback) {
        this.context = context;
        this.study = study;
        this.callback = callback;

    }

    @Override
    protected Study doInBackground(Void... arg0) {
        study.checkComplete();

        try {
            sh = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(
                    DatabaseConnector.TYPE_STUDY);
            savedStudy = sh.saveObject(study);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        return savedStudy;
    }

    protected void onPostExecute(Study study) {
        callback.onSaveFullStudy(study.getId());
        signal.countDown();
    }

    public static interface Callbacks {
        public void onSaveFullStudy(int studyId);
    }

}
