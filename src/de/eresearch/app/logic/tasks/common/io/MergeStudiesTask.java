package de.eresearch.app.logic.tasks.common.io;

import android.os.AsyncTask;

import de.eresearch.app.logic.model.Study;

import java.util.Collection;

/** Task to merge studies.
 *
 */
public class MergeStudiesTask extends AsyncTask {
    
    /** Creates a new task.
     * 
     * @param selectedStudies all studies which should be merged.
     * @param newName new name of the created study.
     */
    public MergeStudiesTask(Collection<Study> selectedStudies, String newName){
        
    }

    @Override
    protected Object doInBackground(Object... params) {
        // TODO Auto-generated method stub
        return null;
    }

}
