package de.eresearch.app.logic.tasks.common.study;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Study;

/** Class to get the meta data from the database for a specific study.
 * 
 */

public class GetStudyMetaTask extends AsyncTask<Void, Void, Study> {
    
    private int studyId;
    private Context context;
    private StudyHelper sh;
    private Study study;
    private QSortHelper qsh;
    private Callbacks callback;
    private ProgressDialog mPd;
    
    //signal for the testcase that the async task finished
    public final CountDownLatch signal = new CountDownLatch(1);
    
    public GetStudyMetaTask(Callbacks callback, Context context, int studyId){
        this.context = context;
        this.studyId = studyId;
        this.callback = callback;
        
    }
    
    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(context);
        mPd.setMessage("Loading...");
        mPd.show();
    }
    
    @Override
    protected Study doInBackground(Void... params) {

        try {
            sh = (StudyHelper) DatabaseConnector.getInstance(context).getHelper(0);
            study = sh.getObjectById(studyId);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // get a qsort helper
        try {
            qsh = (QSortHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_QSORT);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }

        // check for qsorts in study and insert a dummy qsort
        if (qsh.checkForQSortsByStudyId(study.getId()))
            study.addQSort(new QSort(-1,study.getId()));

        return study;
    }

    protected void onPostExecute(Study study){
        callback.onGetStudyMetaTaskTaskUpdate(study);
        signal.countDown();
        mPd.dismiss();
    }

    public static interface Callbacks {
        public void onGetStudyMetaTaskTaskUpdate(Study study);
    }



}
