
package de.eresearch.app.logic.tasks.misc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import de.eresearch.app.logic.model.Pyramid;

/**
 * Task to load a pyramid's edit view.
 * 
 * @author Moritz
 */
public class LoadPyramidEditViewTask extends AsyncTask<Void, Void, View> {

    private Context mContext;

    private LoadPyramidEditViewTask.Callbacks mCallback;

    private Pyramid mPyramid;

    private ProgressDialog mPd;

    public static interface Callbacks {
        /**
         * Called by the {@link LoadPyramidEditViewTask} when it has finished.
         * Provides the edit view that has been loaded from a pyramid.
         * 
         * @param editView The {@link View} object
         */
        public void onPyramidEditViewLoaded(View editView);
    }

    /**
     * Creates a new {@link LoadPyramidEditViewTask}.
     * 
     * @param context An android application context
     * @param callback An instance of the task's callback interface
     * @param pyramid The {@link Pyramid} to load the view from
     */
    public LoadPyramidEditViewTask(Context context, LoadPyramidEditViewTask.Callbacks callback,
            Pyramid pyramid) {
        mContext = context;
        mCallback = callback;
        mPyramid = pyramid;
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Loading...");

        // only show the progress dialog when the view is not cached and the
        // call might take a bit longer
        if (!mPyramid.isEditViewCached()) {
            mPd.show();
        }
    }

    @Override
    protected View doInBackground(Void... params) {
        return mPyramid.getEditView(mContext);
    }

    @Override
    protected void onPostExecute(View result) {
        mPd.dismiss();
        mCallback.onPyramidEditViewLoaded(result);
    }

}
