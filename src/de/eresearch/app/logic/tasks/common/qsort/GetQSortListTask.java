
package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.logic.model.QSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task to get a list of all qsorts from a study.
 */
public class GetQSortListTask extends AsyncTask<Void, Void, List<QSort>> {
    
    private Context mContext;
    private int mStudyId;
    private Callbacks mCallback;
    private QSortHelper qh;
    private List<QSort> qsorts;
    private QSort[] qsorts1;

    public GetQSortListTask(int studyId, Context context, Callbacks callback) {
        mStudyId = studyId;
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected List<QSort> doInBackground(Void... params) {
        try {
            DatabaseConnector.getInstance(mContext).open();
            qh = (QSortHelper) DatabaseConnector.getInstance(mContext).getHelper(DatabaseConnector.TYPE_QSORT);
            qsorts1 = qh.getAllByStudyId(mStudyId);
        } catch (HelperNotFoundException e) {
                  e.printStackTrace();
        }
        //temp bugfix
        if(qsorts1 == null){
            qsorts = new ArrayList<QSort>();
            
        }else{
        qsorts = Arrays.asList(qsorts1);
        }

        return qsorts;
    }
    
    protected void onPostExecute(List<QSort> qsortsList){
        mCallback.onGetQSortListTaskUpdate(qsortsList);
    }
    
    
    public interface Callbacks {
        public void onGetQSortListTaskUpdate(List<QSort> qsortsList);
    }

}
