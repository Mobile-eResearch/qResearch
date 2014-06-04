package de.eresearch.app.logic.tasks.common.pyramid;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.PyramidHelper;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.tasks.common.study.SaveStudyMetaTask.Callbacks;

/** Saves a created pyramid for a study to the database.
 *
 */
public class SaveStudyPyramidTask extends AsyncTask<Void, Void, Void>{
    
    private Context context;
    private Pyramid pyramid;
    private PyramidHelper ph;
    
    public SaveStudyPyramidTask(Context context, Pyramid pyramid){
        this.context = context;
        this.pyramid = pyramid;
        }
       
    /**
     *  Saves the pyramid object to the database.
     */
 
    @Override
    protected Void doInBackground(Void... params) {
        try {
            ph = (PyramidHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_PYRAMID);
            ph.saveObject(pyramid);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return null;
    }

}
