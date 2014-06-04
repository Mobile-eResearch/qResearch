
package de.eresearch.app.logic.tasks.common.study;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.logic.model.Study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Task to get all Studies out of the database.
 */
public class GetStudiesListTask extends AsyncTask<Void, Void, List<Study>> {

    public final CountDownLatch signal = new CountDownLatch(1);
    private Callbacks callback;
    private Context context;
    private StudyHelper sh;
    private List<Study> studies;
    private Study[] studies1;

    /**
     * Constructor for a new Task
     * 
     * @param callback Interface which is executed by the activity.
     * @param context Context of the activity to get the instance from the
     *            database.
     */

    public GetStudiesListTask(Callbacks callback, Context context) {
        this.callback = callback;
        this.context = context;

    }

    /**
     * Executes the callback interface to transfer the list of studies back to
     * the activity.
     */

    protected void onPostExecute(List<Study> studiesList) {
        callback.onGetStudiesListTaskUpdate(studiesList);
        signal.countDown();
    }

    /**
     * Opens a connection to the database and requests the list of all studies.
     */

    @Override
    protected List<Study> doInBackground(Void... params) {
        try {
            DatabaseConnector.getInstance(context).open();
            sh = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_STUDY);
            
            studies1 = sh.getAllStudyWithIdAndNames();
        } catch (HelperNotFoundException e) {
                  e.printStackTrace();
        }
        //temp bugfix
        if(studies1 == null){
            studies = new ArrayList<Study>();
            return studies;
        }else
        {
            studies = Arrays.asList(studies1);
        }
        
        
        
        

        return studies;
    }

    public interface Callbacks {
        public void onGetStudiesListTaskUpdate(List<Study> studiesList);
    }
}
