
package de.eresearch.app.logic.tasks.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.io.exception.NameAlreadyInUseException;
import de.eresearch.app.io.exp.ExportHandler;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Study;

import java.io.IOException;

/**
 * Task to export a study.
 * 
 * @author Moritz
 */
public class ExportStudyTask extends AsyncTask<Void, Void, Throwable> {

    private Context mContext;

    private ExportStudyTask.Callbacks mCallback;

    private int mStudyId;

    private ProgressDialog mPd;

    public static interface Callbacks {
        /**
         * Called by the {@link ExportStudyTask} when the export of a study has
         * finished. As the export of a study can fail (e.g. due to an IO
         * failure), this method provides an error that has (possibly) happened.
         * 
         * @param thr A {@link Throwable} containing an error that has occured
         *            or <code>null</code> when everything worked fine
         */
        public void onStudyExported(Throwable thr);
    }

    /**
     * Creates a new {@link ExportStudyTask}.
     * 
     * @param context An android application context
     * @param callback An instance of the task's callback interface
     * @param studyId The id of the study to be exported
     */
    public ExportStudyTask(Context context, ExportStudyTask.Callbacks callback, int studyId) {
        mContext = context;
        mCallback = callback;
        mStudyId = studyId;
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Exporting...");
        mPd.show();
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        // Load full study object from DB
        Study toExport = null;
        try {
            StudyHelper sh = (StudyHelper) DatabaseConnector.getInstance(mContext).getHelper(
                    DatabaseConnector.TYPE_STUDY);
            toExport = sh.getObjectById(mStudyId);
        } catch (HelperNotFoundException e) {
            return e;
        }
        
        // Export the study
        ExportHandler exph = new ExportHandler();
        try {
            exph.exportStudy(toExport);
        } catch (NameAlreadyInUseException e) {
            return e;
        } catch (IOException e) {
            return e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        mPd.dismiss();
        mCallback.onStudyExported(result);
    }

}
