package de.eresearch.app.logic.tasks.common.questions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QuestionHelper;
import de.eresearch.app.logic.model.Question;

import java.util.Arrays;
import java.util.List;


/** 
 * Task to get all questions for a study.
 * 
 * @author thg
 * 
 */
public class GetQuestionsTask extends AsyncTask<Void, Integer, Question []> {
    
    private Callbacks mCallback;
    private Context mContext;
    private int mStudyId;
    
    public GetQuestionsTask(Context context, Callbacks callback, int studyid){

        
        // set global attributes
        this.mCallback = callback;
        this.mContext = context;

        this.mStudyId = studyid;
    }

    @Override
    protected Question [] doInBackground(Void... param) {
        QuestionHelper qh;
        try {
            qh = (QuestionHelper) DatabaseConnector.getInstance(mContext)
                    .getHelper(DatabaseConnector.TYPE_QUESTION);

            Question [] q = qh.getAllByStudyId(mStudyId);
            return q;
        } catch (HelperNotFoundException e) {
            Log.e("GetQuestionsTask","HelperNotFoundException: "
                    + "QuestionHelper not found, return null");
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onPostExecute(Question [] questions){

        if(questions != null){
             mCallback.onGetQuestionTaskUpdate(Arrays.asList(questions));
        } else {
            mCallback.onGetQuestionTaskUpdate(Arrays.asList(new Question [] {}));
        }
       
    }
    
    /**
     * Callback interface. All Activities witch implements this task 
     * must implement this interface.
     */
    public interface Callbacks{
        public void onGetQuestionTaskUpdate(List<Question> questions); 
    }

}
