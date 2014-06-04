
package de.eresearch.app.logic.tasks.media;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.NoteHelper;
import de.eresearch.app.logic.model.Note;

import java.util.concurrent.CountDownLatch;

/**
 * Task to save a note to the database.
 * 
 * @author Henrik
 */
public class SaveNoteTask extends AsyncTask<Void, Void, Note> {

    private Context context;
    private NoteHelper noteHelper;
    private Note note;
    private Callbacks callback;

    // signal for the testcase that the async task finished
    public final CountDownLatch signal = new CountDownLatch(1);

    public SaveNoteTask(Callbacks callback, Context context, Note n) {
        this.context = context;
        this.note = n;
        this.callback = callback;
    }

    @Override
    protected Note doInBackground(Void... params) {
        try {
            noteHelper = (NoteHelper) DatabaseConnector.getInstance(context)
                    .getHelper(DatabaseConnector.TYPE_NOTE);
            note = noteHelper.saveObject(note);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return note;
    }

    protected void onPostExecute(Note note) {
        callback.onSaveNoteTaskUpdate(note);
        signal.countDown();
    }

    public static interface Callbacks {
        public void onSaveNoteTaskUpdate(Note note);
    }

}
