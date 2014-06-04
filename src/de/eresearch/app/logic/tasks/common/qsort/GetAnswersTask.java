package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AnswerHelper;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.Study;

import java.util.concurrent.CountDownLatch;

/** Task to get all answers from a QSort.
 * @author liqquid
 */

public class GetAnswersTask extends AsyncTask<Void, Void, Answer[]> {
    
    private Callbacks callback;
    private Answer[] answerList;
    private Context context;
    private AnswerHelper answerHelper;
    private int qSortId;
    private boolean presort;
    
  //TODO CHECK IF BOOLEAN PRESORT IS REALLY REQUIRED
    /** Creates a new Task.
     * 
     * @param context Context of the activity to get the instance from the
     *            database.
     * @param callback Interface which is executed by the activity.
     * @param qSortId id of the qsort
     * @param presort identifier if pre or post questions
     */
    public GetAnswersTask(Callbacks callback, Context context, int qSortId, boolean presort){
        this.context = context;
        this.callback = callback;
        this.qSortId = qSortId;
        this.presort = presort;
    }

    /**
     * Opens a connection to the database and requests a all answers from a QSort.
     */
    @Override
    protected Answer[] doInBackground(Void... arg0) {
        try {
            answerHelper = (AnswerHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_ANSWER);
            answerList = answerHelper.getAllByQSortId(qSortId);
        } catch (HelperNotFoundException e) {
            e.printStackTrace();
        }
        return answerList;
    }
    
    /**
     * Executes the callback interface to transfer the list of answers back to
     * the activity.
     */
    public final CountDownLatch signal = new CountDownLatch(1);
    protected void onPostExecute(Study study){
        callback.onGetAnswersTask(answerList);
        signal.countDown();
    }
    
    public interface Callbacks {
        public void onGetAnswersTask(Answer[] answerList);
    }

}
