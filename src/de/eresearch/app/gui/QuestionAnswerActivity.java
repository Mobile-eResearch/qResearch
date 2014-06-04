package de.eresearch.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.tasks.common.qsort.GetAnswerByQSortTask;

/** 
 * Activity to provide UI for viewing answered questions. This activity consists of 
 * {@link QuestionAnswerDetailFragment} and {@link QuestionAnswerListFragmentPresort}.
 *
 * MockUp: http://eresearch.informatik.uni-bremen.de/mockup/#studiedatenansicht_page
 */
public class QuestionAnswerActivity extends Activity implements QuestionAnswerListFragment.Callbacks{
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        // load UI
        setContentView(R.layout.activity_question_answer);   
       
    }
    
    
    /**
     * Callback method for lifecycle state onPause().
     */
    @Override
    public void onPause(){
        super.onPause();
        // TODO Auto-generated method stub
    }

    /**
     * Callback method for click on a list element in @link QuestionAnswerListFragment
     * @param id Question ID
     */
    public void onItemSelected(Question q) {
        Bundle arguments = new Bundle();
        int qsortId = getIntent().getExtras().getInt(QSortsDetailFragment.QSORT_ID);
        arguments.putInt(QuestionAnswerDetailFragment.QUESTION_ID, q.getId());
        arguments.putString(QuestionAnswerDetailFragment.QUESTION_TEXT, q.getText());
        System.out.println("QUstion id:" + q.getId());
        arguments.putInt(QSortsDetailFragment.QSORT_ID, qsortId);

        if (q instanceof OpenQuestion) {
            System.out.println("OPENQUESTION");
            QuestionAnswerDetailFragment fragment = new QuestionAnswerDetailFragment();
            arguments.putInt(QuestionAnswerListFragment.TYPE, GetAnswerByQSortTask.ANSWER_OPEN);
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.question_answer_detail_container, fragment).commit();
        } else if (q instanceof ClosedQuestion) {
            System.out.println("CLOSEQUESTION");

            QuestionAnswerViewDetailFragment fragment = new QuestionAnswerViewDetailFragment();
            arguments.putInt(QuestionAnswerListFragment.TYPE, GetAnswerByQSortTask.ANSWER_CLOSED);
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.question_answer_detail_container, fragment).commit();

        } else if (q instanceof ScaleQuestion){
            System.out.println("SCLAEQUESTION");

            QuestionAnswerViewDetailFragment fragment = new QuestionAnswerViewDetailFragment();
            arguments.putInt(QuestionAnswerListFragment.TYPE, GetAnswerByQSortTask.ANSWER_SCALE);
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.question_answer_detail_container, fragment).commit();

        }
    }
    /**
     * Callback method for lifecycle state onResume().
     */
    public void onResume() {
        super.onResume();
        // List items should be given the
        // 'activated' state when touched.
        ((QuestionAnswerListFragment) getFragmentManager().
                findFragmentById(R.id.question_answer_list)).
                setActivateOnItemClick(true);
    }
}

