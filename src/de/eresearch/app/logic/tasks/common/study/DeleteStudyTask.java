package de.eresearch.app.logic.tasks.common.study;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;


/** Starts the process to delete a study from the database.
 * 
 * 
 */
public class DeleteStudyTask extends AsyncTask<Object, Object, Boolean> {
   
   private Context context;
   private Callbacks callback;
   private int studyId;
   private StudyHelper studyHelper;
   public final CountDownLatch signal = new CountDownLatch(1);
   private String LOG_TAG="DeleteStudyTask";
   
    public DeleteStudyTask(Context context, Callbacks callback, int studyId){
        this.context = context;
        this.callback = callback;
        this.studyId = studyId;  
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        
        Boolean back = false;
       try {    
            studyHelper = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_STUDY);
            back = studyHelper.deleteById(studyId);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }        
        Log.d(LOG_TAG, "run  doInBackground() back="+back.toString()); 
        return back;
    }
    
    public interface Callbacks {
        public void onDeleteStudyTask();
    }
    
    protected void onPostExecute(Boolean result) {
        callback.onDeleteStudyTask();
        signal.countDown();  
    }

}