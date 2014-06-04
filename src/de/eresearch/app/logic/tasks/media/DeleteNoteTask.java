package de.eresearch.app.logic.tasks.media;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.NoteHelper;
import de.eresearch.app.db.helper.StudyHelper;


/** Starts the process to delete a study from the database.
 *  @author Marcel L.
 * 
 */
public class DeleteNoteTask extends AsyncTask<Object, Object, Boolean> {
   
   private Context context;
   private Callbacks callback;
   private int noteId;
   private NoteHelper noteHelper;
   public final CountDownLatch signal = new CountDownLatch(1);
   private String LOG_TAG="DeleteStudyTask";
   
    public DeleteNoteTask(Context context, Callbacks callback, int noteId){
        this.context = context;
        this.callback = callback;
        this.noteId = noteId;  
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        
        Boolean back = false;
       try {    
            noteHelper = (NoteHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_NOTE);
            back = noteHelper.deleteById(noteId);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }        
        Log.d(LOG_TAG, "run  doInBackground() back="+back.toString()); 
        return back;
    }
    
    public interface Callbacks {
        public void onDeleteNoteTask();
    }
    
    protected void onPostExecute(Boolean result) {
        callback.onDeleteNoteTask();
        signal.countDown();  
    }

}
