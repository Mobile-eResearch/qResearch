package de.eresearch.app.logic.tasks.common.qsort;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AnswerHelper;
import de.eresearch.app.db.helper.ClosedAnswerHelper;
import de.eresearch.app.db.helper.OpenAnswerHelper;
import de.eresearch.app.db.helper.ScaleAnswerHelper;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.OpenAnswer;


/** 
 * @author thg, marcel L.
 * Task to get the answers for a specified question
 * 
 */

public class GetAnswerByQSortTask extends AsyncTask<Void, Void, Answer[]> {
    
    private Callbacks mCallback;
    private int mQSortId;
    private int mQuestionId;
    private int mAnswerType;
    private Context mContext;
    
    /*
     * 
     */
    
    public static final int ANSWER_OPEN = 0;
    public static final int ANSWER_CLOSED = 1;
    public static final int ANSWER_SCALE = 2;

    
    public GetAnswerByQSortTask(Callbacks callback, Context context, 
            int qSortId, int questionId, int answerType){
        
        // set global attributes
        this.mCallback = callback;
        this.mContext = context;
        this.mQuestionId = questionId;
        this.mQSortId = qSortId;
        this.mAnswerType = answerType;
    }
    
    
    /**
     * Gets an array of specific answer objects identified by the answerType
     * 0 = OpenAnswer, 1 = ClosedAnswer, 2 = ScaleAnswer
     * 
     */
    @Override
    protected Answer[] doInBackground(Void... arg0) {
        try {
            switch (mAnswerType) {
                case 0:
                    OpenAnswerHelper ah = (OpenAnswerHelper) DatabaseConnector
                            .getInstance(mContext)
                            .getHelper(DatabaseConnector.TYPE_ANSWER_OPEN);
                    return ah.getAllByQSortId(mQSortId);

                case 1:
                    ClosedAnswerHelper ca = (ClosedAnswerHelper) DatabaseConnector.getInstance(
                            mContext)
                            .getHelper(DatabaseConnector.TYPE_ANSWER_CLOSED);
                    return ca.getAllByQSortId(mQSortId);

                case 2:
                    ScaleAnswerHelper sa = (ScaleAnswerHelper) DatabaseConnector.getInstance(
                            mContext)
                            .getHelper(DatabaseConnector.TYPE_ANSWER_SCALE);
                    return sa.getAllByQSortId(mQSortId);
            }
            
            
        } catch (HelperNotFoundException e) {
            Log.e("GetAnswerByQSortTask","HelperNotFoundException: "
                    + "AnswerHelper not found, return null");
            e.printStackTrace();
        }
      return null;
    }
    
    @Override
    protected void onPostExecute(Answer[] answers) {

        if (answers != null) {

            for (Answer a : answers) {
                System.out.println(a.getQuestion().getId());
                if (a.getQuestion().getId() == mQuestionId) {
                    mCallback.onGetAnswerByQSortTask(a);

                }
            }
            
        } else {
            mCallback.onGetAnswerByQSortTask(null);

         }

    }
    
    /**
     * Callback interface. All activities witch implements this task 
     * must implement this interface.
     */
    public interface Callbacks {
        public void onGetAnswerByQSortTask(Answer answer);
    }

}
