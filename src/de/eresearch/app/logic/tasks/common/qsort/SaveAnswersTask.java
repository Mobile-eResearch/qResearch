
package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.util.List;
import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AnswerHelper;
import de.eresearch.app.logic.model.Question;


public class SaveAnswersTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private Callbacks mCallback;
    private List<Question> mQuestions;
    private int mQSortId;
  
    public SaveAnswersTask(Context context, Callbacks callback, int qSortId
            , List<Question> questions) {
        
        this.mContext = context;
        this.mCallback = callback;
        this.mQuestions = questions;
        this.mQSortId = qSortId;
    }

    @Override
    protected Boolean doInBackground(Void ...arg0){
        
        try {
            AnswerHelper ah = (AnswerHelper) DatabaseConnector.getInstance(mContext)
                    .getHelper(DatabaseConnector.TYPE_ANSWER);
            
            // Add answer for all questions in List
            for (Question question: mQuestions){
                
                // but only when answer != null
                if (question.getAnswer() != null)
                    ah.saveByQuestionAndQSortId(question, mQSortId);
            }
            return true;
        } catch (HelperNotFoundException e) {
            Log.e("SaveAnswersTask","HelperNotFoundException: "
                    + "AnswerHelper not found, return null");
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    protected void onPostExecute(Boolean status){
        if (status)
            mCallback.onSaveAnswerTask();
    }

    public interface Callbacks {
        public void onSaveAnswerTask();
    }

}
