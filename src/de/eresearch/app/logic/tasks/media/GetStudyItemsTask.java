/** All tasks which deal with media data.
 * 
 */

package de.eresearch.app.logic.tasks.media;

import android.os.AsyncTask;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.ItemHelper;
import de.eresearch.app.logic.model.Picture;

/**
 * Get all pictures associated with a study.
 */
public class GetStudyItemsTask extends AsyncTask<Void, Void, List<Picture>> {
    public final CountDownLatch signal = new CountDownLatch(1);
    private Context context;
    private Callbacks callback; // TODO Warum nirgendwo genutzt?!?!?!?!
    private int studyId;
    private ItemHelper pch;
    private Picture[] pic;
    private List<Picture> pictures;

    /**
     * Creates a new task.
     * 
     * @param s the study object
     */
    public GetStudyItemsTask(Context context, Callbacks callback, int studyId) {
        this.context = context;
        this.callback = callback;
        this.studyId = studyId;

    }

    /**
     * Opens a connection to the database and requests the list of all picture .
     */

    @Override
    protected List<Picture> doInBackground(Void... arg0) {
        // TODO Auto-generated method stub

        try {
            pch = (ItemHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_ITEM);
            pic = new Picture[pch.getAllByStudyId(studyId).length];
            for(int i = 0; i<pch.getAllByStudyId(studyId).length;i++){
                pic[i] =(Picture) pch.getAllByStudyId(studyId)[i];
            }

        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        if (pic == null) {
            pictures = new ArrayList<Picture>();

        } else {
            pictures = Arrays.asList(pic);
        }
        return pictures;
    }

    protected void onPostExecute(List<Picture> pictures) {
        // Why is the callback expecting a picture object instead of a list ?
        // callback.onGetStudyItemsTask(pictures);
        signal.countDown();
    }

    // TODO change callback interface method
    public interface Callbacks {
        public void onGetStudyItemsTask(Picture picture);
    }

}
