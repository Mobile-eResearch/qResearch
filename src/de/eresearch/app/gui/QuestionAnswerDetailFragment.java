
package de.eresearch.app.gui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.tasks.common.qsort.GetAnswerByQSortTask;
import de.eresearch.app.logic.tasks.common.study.GetStudyMetaTask;

/**
 * Provides Fragment for question answers (frame on the right side, next to the
 * {@link QuestionAnswerListFragmentPresort})
 */
public class QuestionAnswerDetailFragment extends Fragment implements GetAnswerByQSortTask.Callbacks {
    
   
    public static final String QUESTION_ID = "de.eresearch.app.gui.QuestionAnswerDetailFragment.question_id"; 
    public static final String QUESTION_TEXT = "de.eresearch.app.gui.QuestionAnswerDetailFragment.question_text"; 
    public static final String ANSWER_TEXT = "de.eresearch.app.gui.QuestionAnswerDetailFragment.answer_text"; 

    
    

    private Answer mAnswer;
    private Question mQuestion;
    private int mQSortId; 
    private int type;
    private String questionText;
    private static TextView mQuestionTitle;
    private static TextView mAnswerTitle;
    private TextView mQuestionText;
    private TextView mAnswerText;
    private OpenAnswer mOpenAnswer;

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.fragment_question_answer_detail,
                container, false);

        mQuestionTitle = (TextView) rootView.findViewById(R.id.question_title);
        mAnswerTitle = (TextView) rootView.findViewById(R.id.answer_header);
        mQuestionText = (TextView) rootView.findViewById(R.id.question_text);
        mAnswerText = (TextView) rootView.findViewById(R.id.answer_text);
        
        
        
       

        return rootView;

    }
    
    

    
    @Override
    public void onStart() {
        super.onStart();
        
      Bundle extras = getActivity().getIntent().getExtras();
      type = getArguments().getInt(QuestionAnswerListFragment.TYPE);
      questionText = getArguments().getString(QuestionAnswerDetailFragment.QUESTION_TEXT);
      int qsortId = extras.getInt(QSortsDetailFragment.QSORT_ID);
      GetAnswerByQSortTask t = new GetAnswerByQSortTask(this, this.getActivity(), qsortId,
              getArguments().getInt(QUESTION_ID),type);
      t.execute();
        

    }




    @Override
    public void onGetAnswerByQSortTask(Answer answer) {
        if (answer != null){
         mOpenAnswer = (OpenAnswer) answer;
         mAnswerText.setText(mOpenAnswer.getAnswer());
         mQuestionText.setText(mOpenAnswer.getQuestion().getText());
        }
        else {
            
            //TODO Find a better way for this case
            mAnswerText.setText(this.getString(R.string.dummy_answer_text));
            mQuestionText.setText(questionText);
        }
    }
}
