
package de.eresearch.app.logic.tasks.common.qsort;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.LogHelper;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.Loggable;
import de.eresearch.app.logic.model.Phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Moritz
 */
public class GetLoggablesTask extends AsyncTask<Void, Void, List<Loggable>> {

    private Context mContext;

    private GetLoggablesTask.Callbacks mCallbacks;

    private int mQsortId;

    private ProgressDialog mPd;

    private Phase mPhase;

    public static interface Callbacks {
        /**
         * @param loggables A list of all loggables attached to a qsort, sorted
         *            by their time stamp
         */
        public abstract void onLoggablesGotten(List<Loggable> loggables);
    }


    /**
     * Creates a new {@link GetLoggablesTask}. This task my be used to obtain
     * all events that are logged during a QSort and that may be displayed on
     * the log screen, dependent on the phase.
     * 
     * @param context An android application context
     * @param callbacks An implementation of the internal callback interface
     * @param qsortId The id of the QSort
     * @param phase The phase
     */
    public GetLoggablesTask(Context context, GetLoggablesTask.Callbacks callbacks, int qsortId,
            Phase phase) {
        mContext = context;
        mCallbacks = callbacks;
        mQsortId = qsortId;
        mPhase = phase;
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Loading...");
        mPd.show();
    }
    
    @Override
    protected List<Loggable> doInBackground(Void... params) {
        LogHelper logh = null;

        try {
            logh = (LogHelper) DatabaseConnector.getInstance(mContext).getHelper(
                    DatabaseConnector.TYPE_LOG);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }

        List<Loggable> loggables = new ArrayList<Loggable>();

        System.out.println(loggables.size());
        

        // Get the required log object
        Log requiredLog = logh.getByPhaseAndQSortId(mPhase, mQsortId);

        System.out.println(requiredLog == null ? "null :o" : "not null :o");
        
        // If requiredLog is null however, there doesn't seem to be a log that
        // matches the given phase. An empty list will be returned.
        if (requiredLog == null) {
            return loggables;
        }

        System.out.println("answers: " + requiredLog.getAnswers().size());
        System.out.println("log entries: " + requiredLog.getLogEntries().size());
        System.out.println("notes: " + requiredLog.getNotes().size());
        
        // get the loggables from the log and put them into the list
        loggables.addAll(requiredLog.getAnswers());
        loggables.addAll(requiredLog.getLogEntries());
        loggables.addAll(requiredLog.getNotes());

        System.out.println("total: " + loggables.size());
        
        // sort the list by the time stamp of the loggables
        if(loggables != null & loggables.size()>0)
        Collections.sort(loggables);

        System.out.println("total sorted: " + loggables.size());
        
        return loggables;
    }

    @Override
    protected void onPostExecute(List<Loggable> result) {
        mCallbacks.onLoggablesGotten(result);
        mPd.dismiss();
    }

}
