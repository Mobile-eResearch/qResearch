
package de.eresearch.app.logic.tasks.common.questions;

import android.os.AsyncTask;

import de.eresearch.app.logic.model.Question;

/**
 * Task to save a new or edited question to the database.
 * 
 * @deprecated Unused
 */
@Deprecated
public class SaveQuestionTask extends AsyncTask<Void, Void, Void> {

    public SaveQuestionTask(Question q) {
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

}
