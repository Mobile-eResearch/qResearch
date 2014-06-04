
package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.tasks.common.qsort.GetAnswerByQSortTask;

/**
 * Provides Fragment for question answers (frame on the right side, next to the
 * {@link QuestionAnswerListFragmentPresort})
 */
public class QuestionAnswerViewDetailFragment extends Fragment implements
        GetAnswerByQSortTask.Callbacks {


    private TextView mQuestionText;
    private RelativeLayout layout;
    private int type;
    private String questionText;

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.fragment_question_answer_view_detail,
                container, false);

        layout = (RelativeLayout) rootView.findViewById(R.id.layout1);
        // mAnswerView = rootView.findViewById(R.id.answer_view);
        mQuestionText = (TextView) rootView.findViewById(R.id.question_text);

        // RelativeLayout.LayoutParams params = new
        // RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        // RelativeLayout.LayoutParams.WRAP_CONTENT);
        // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
        // RelativeLayout.TRUE);
        // params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getActivity().getIntent().getExtras();
        // mQSortId = extras.getInt(QSortsDetailFragment.QSORT_ID);
        type = getArguments().getInt(QuestionAnswerListFragment.TYPE);
        questionText = getArguments().getString(QuestionAnswerDetailFragment.QUESTION_TEXT);
        int qsortId = extras.getInt(QSortsDetailFragment.QSORT_ID);
        GetAnswerByQSortTask t = new GetAnswerByQSortTask(this, this.getActivity(), qsortId,
                getArguments().getInt(QuestionAnswerDetailFragment.QUESTION_ID), type);
        t.execute();
    }

    @Override
    public void onGetAnswerByQSortTask(Answer answer) {
        if (answer != null){
        if (type == GetAnswerByQSortTask.ANSWER_CLOSED) {
            ClosedQuestion c = (ClosedQuestion) answer.getQuestion();
            mQuestionText.setText(c.getText());
            layout.addView(c.getQuestionAnswerDisplayView(this.getActivity(), answer));

        }

        if (type == GetAnswerByQSortTask.ANSWER_SCALE) {
            ScaleQuestion c = (ScaleQuestion) answer.getQuestion();
            mQuestionText.setText(c.getText());
            //Log.d("TEST",""+((ScaleAnswer)answer).getScales().get(0).getSelectedValueIndex());
            
            // FIXME: Workaround to view large scale questions in QuestionAnswerFragment
            HorizontalScrollView sv = new HorizontalScrollView(this.getActivity());
            sv.addView(c.getQuestionAnswerDisplayView(this.getActivity(), (ScaleAnswer)answer));
            layout.addView(sv);
        }
        }else {
            mQuestionText.setText(questionText);
        }
    }
}
