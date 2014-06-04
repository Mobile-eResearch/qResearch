/** All tasks which handles the pyramid structure.
 * 
 */

package de.eresearch.app.logic.tasks.common.pyramid;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.PyramidHelper;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask.Callbacks;

/** Gets the pyramid from a study.
 * 
 */
public class GetStudyPyramidTask extends AsyncTask<Void, Void, Pyramid> {
    
    
    private Context context;
    private Callbacks callback;
    public final CountDownLatch signal = new CountDownLatch(1);
    private int studyId;
    private PyramidHelper ph;
    private Pyramid[] pyramids;
    
    /** Creates a new task.
     * 
     * @param s the study object 
     */
    public GetStudyPyramidTask(Context context, Callbacks callback, int studyId){
        this.context = context;
        this.callback = callback;
        this.studyId = studyId;
        
    }

    @Override
    protected Pyramid doInBackground(Void... arg0) {
        try {
            ph = (PyramidHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_PYRAMID);
            pyramids = ph.getAllByStudyId(studyId);
        } catch (HelperNotFoundException e) {
               e.printStackTrace();
        }
        
        if(pyramids == null){
            Pyramid p = new Pyramid(studyId);
            return p;
        }else{
            //why array ?
        return pyramids[0];
        }
    }
  
    
    protected void onPostExecute(Pyramid pyramid) {
        callback.onGetStudiesListTaskUpdate(pyramid);
        signal.countDown();
       
    }
    
    //TODO change callback interface method 
    public interface Callbacks {
        public void onGetStudiesListTaskUpdate(Pyramid pyramid);
    }

  

}
