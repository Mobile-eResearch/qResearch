package de.eresearch.app.logic.tasks.common.study;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.logic.model.Study;

import java.util.concurrent.CountDownLatch;

/*
 *@author liqquid
 */

public class GetFullStudyTask extends AsyncTask<Void, Void, Study> {
    
    private Context context;
    private Callbacks callback;
    private int studyId;
    private StudyHelper studyHelper;
    private Study study;
    
    /**
     * Constructor for a new Task
     * 
     * @param context Context of the activity to get the instance from the
     *            database.
     * @param callback Interface which is executed by the activity.
     * @param studyId Id of the requested study
     */

    public GetFullStudyTask(Context context, Callbacks callback, int studyId){
        this.context = context;
        this.callback = callback;
        this.studyId = studyId;        
    }
    
    /**
     * Opens a connection to the database and requests a full study.
     */
    @Override
    protected Study doInBackground(Void... arg0) {
        try {
            studyHelper = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_STUDY);
            study = studyHelper.getObjectById(studyId);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        return study;
    }
    
    /**
     * Executes the callback interface to transfer the list of studies back to
     * the activity.
     */
    public final CountDownLatch signal = new CountDownLatch(1);
    protected void onPostExecute(Study study){
        callback.onGetFullStudyTask(study);
        signal.countDown();
    }
    
    public interface Callbacks {
        public void onGetFullStudyTask(Study study);
    }
}
