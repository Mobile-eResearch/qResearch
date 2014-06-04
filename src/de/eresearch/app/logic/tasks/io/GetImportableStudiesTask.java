
package de.eresearch.app.logic.tasks.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.io.imp.ImportHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task to get a list (of names) of all studies that lay on the file system that
 * can be imported.
 * 
 * @author Moritz
 */
public class GetImportableStudiesTask extends AsyncTask<Void, Void, Throwable> {

    private Context mContext;

    private GetImportableStudiesTask.Callbacks mCallback;

    private ProgressDialog mPd;

    private List<String> mImportableStudies;

    public static interface Callbacks {
        /**
         * Called by the {@link GetImportableStudiesTask} when it detected all
         * studies that can be imported. As the check of importable studies can
         * fail (e.g. due to an IO failure), this method provides an error that
         * has (possibly) happened.
         * 
         * @param importableStudies A list of all importable studies or
         *            <code>null</code>, when an error occured
         * @param thr A {@link Throwable} containing an error that has occured
         *            or <code>null</code> when everything worked fine
         */
        public void onImportableStudiesGotten(List<String> importableStudies, Throwable thr);
    }

    /**
     * Creates a new {@link GetImportableStudiesTask}.
     * 
     * @param context An android application context
     * @param callback An instance of the task's callback interface
     */
    public GetImportableStudiesTask(Context context, GetImportableStudiesTask.Callbacks callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Exporting...");
        mPd.show();
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        Throwable thr = null;

        ImportHandler imph = new ImportHandler();

        String[] studies = imph.getNamesOfAvailableStudies();
        
        /*
         * imph.getNamesOfAvailableStudies(true) can be null, so we need
         * to test this. If it is null we deliver an empty list.
         */
        if (studies != null)
            mImportableStudies = Arrays.asList(studies);
        else 
            mImportableStudies = new ArrayList<String>();
        
        return thr;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        mPd.dismiss();
        mCallback.onImportableStudiesGotten(mImportableStudies, result);
    }

}
