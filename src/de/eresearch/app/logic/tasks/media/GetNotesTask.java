
package de.eresearch.app.logic.tasks.media;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.NoteHelper;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;

import java.util.concurrent.CountDownLatch;

/**
 * Task to get all notes from a qsort.
 * @author Henrik
 */
public class GetNotesTask extends AsyncTask<Void, Void, Note[]> {

    private QSort qSort;
    private int qSortId;
    private Context context;
    private NoteHelper noteHelper;
    private Callbacks callback;

    private Note[] noteList;

    // signal for the testcase that the async task finished
    public final CountDownLatch signal = new CountDownLatch(1);

    public GetNotesTask(Callbacks callback, Context context, int qSortId) {
        this.context = context;
        this.qSortId = qSortId;
        this.callback = callback;
    }

    @Override
    protected Note[] doInBackground(Void... params) {
        try {
            noteHelper = (NoteHelper) DatabaseConnector.getInstance(context).getHelper(
                    DatabaseConnector.TYPE_NOTE);
            
            //test data
//            Note n = new Note(-1);
//            n.setQSortId(qSortId);
//            n.setText("jfada");
//            n.setPhase(Phase.INTERVIEW);
//            long t = 100;
//            n.setTime(t);
//            n.setTitle("fdafd");
//            Note n2 = noteHelper.saveObject(n);
            
            noteList = noteHelper.getAllByQSortId(qSortId);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (noteList == null){
            noteList = new Note[0];
            return noteList;
        }else{
        
        
        return noteList;
        }
    }

    protected void onPostExecute(Note[] noteList) {
        callback.onGetNotesTaskUpdate(noteList);
        signal.countDown();
    }

    public static interface Callbacks {
        public void onGetNotesTaskUpdate(Note[] noteList);
    }

}
