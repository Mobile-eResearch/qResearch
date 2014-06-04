/** Contains all tasks which handle the questions.
 * 
 */

package de.eresearch.app.logic.tasks.common.questions;

import android.os.AsyncTask;

import de.eresearch.app.logic.model.Question;

/**
 * Task to delete a question from a study.
 * 
 * @deprecated Unused
 */
@Deprecated
public class DeleteQuestionTask extends AsyncTask<Void, Void, Void> {

    public DeleteQuestionTask(Question q) {
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

}
