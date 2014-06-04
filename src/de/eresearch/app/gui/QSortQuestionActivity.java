
package de.eresearch.app.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import de.eresearch.app.R;
import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.QuestionHelper;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.tasks.common.qsort.SaveAnswersTask;

import java.util.Arrays;
import java.util.List;

/**
 * Activity to provide UI ask questions in context of one Qsort This activity
 * loads the following fragments: - {@link QSortQuestionStartFragment} (to
 * provide start button) - {@link QSortQuestionNavFragment} (to provide
 * navigation on the bottom of the screen) - {@link QSortQuestionClosedFragment}
 * (to prepare closed questions) - {@link QSortQuestionOpenFragment} (to prepare
 * open questions) - {@link QSortQuestionScaleFragment} (to prepare scale
 * questions)
 */
public class QSortQuestionActivity extends QSortParentActivity implements
        SaveAnswersTask.Callbacks
{

    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private List<Question> mQuestions;
    private QSortQuestionContainer container;
    private QuestionHelper qh;

    public static final String IS_POST =
            "de.eresearch.app.gui.QSortQuestionActivity.IS_POST";

    /**
     * Name of debug log tag.
     */
    private static final String LOG_TAG = "QSortQuestionActivity";

    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "run onCreate");
        setContentView(R.layout.activity_qsort_pre_post);

        try {
            qh = (QuestionHelper) DatabaseConnector.getInstance(this)
                    .getHelper(DatabaseConnector.TYPE_QUESTION);

            Question[] q = qh.getAllByStudyId(CurrentQSort.getInstance().getStudyId());
            mQuestions = Arrays.asList(q);
        } catch (HelperNotFoundException e) {
            Log.e("GetQuestionsTask", "HelperNotFoundException: "
                    + "QuestionHelper not found, return null");
            e.printStackTrace();
        }

        container = QSortQuestionContainer.getQSortQuestionContainer();
        container.setPost(mExtras.getBoolean(IS_POST));
        container.setmExtras(mExtras);
        container.setContainer(mQuestions);

        if (container.isPost()) {
            mPhase = Phase.QUESTIONS_POST;
        } else {
            mPhase = Phase.QUESTIONS_PRE;
        }

        getRightQuestionFragment(false);
    }

    /**
     * Callback method for lifecycle state onStop().
     */
    @Override
    public void onStop() {
        if (CurrentQSort.getInstance() != null) {
            for(int i = 0; i<container.containerList.size(); i++){
                for(int j = 0; j<container.filteredList.size(); j++){
                    if(container.containerList.get(i).getId() == container.filteredList.get(j).getId()){
                        container.containerList.set(i, container.filteredList.get(j));
                    }
                }
            }
            
            SaveAnswersTask.Callbacks callback2 = (SaveAnswersTask.Callbacks) this;
            SaveAnswersTask task2 = new SaveAnswersTask(this, callback2, CurrentQSort.getInstance()
                    .getId(),
                    container.containerList);
            task2.execute();
            
            container.reset();
        }
        super.onStop();
    }

    /**
     * Callback method for lifecycle state onResume().
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveAnswerTask() {
    }

    @Override
    public void onBackPressed() {
        if (container.location == 0) {
            super.onBackPressed();
        } else {
            container.location--;
            getRightQuestionFragment(true);
        }

    }

    private void getRightQuestionFragment(boolean replace) {
        Fragment openAnswerFragment = new QSortQuestionOpenFragment();
        Fragment closedAnswerFragment = new QSortQuestionClosedFragment();
        Fragment scaleAnswerFragment = new QSortQuestionScaleFragment();
        Fragment navFragment = new QSortQuestionNavFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (container.filteredList.get(container.location) instanceof ScaleQuestion ) {

            if (replace) {
                fragmentTransaction.replace(R.id.fragment_qsort_question, scaleAnswerFragment);
                fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            } else {
                fragmentTransaction.add(R.id.fragment_qsort_question, scaleAnswerFragment);
                fragmentTransaction.add(R.id.fragment_qsort_question_nav, navFragment);
            }

        } else if (container.filteredList.get(container.location) instanceof ClosedQuestion ) {
            Log.d("QSortQuestionActivty", "Activity Closed Question");
            if (replace) {
                fragmentTransaction.replace(R.id.fragment_qsort_question, closedAnswerFragment);
                fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            } else {
                fragmentTransaction.add(R.id.fragment_qsort_question, closedAnswerFragment);
                fragmentTransaction.add(R.id.fragment_qsort_question_nav, navFragment);
            }

        } else if (container.filteredList.get(container.location) instanceof OpenQuestion ) {
            if (replace) {
                fragmentTransaction.replace(R.id.fragment_qsort_question, openAnswerFragment);
                fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            } else {
                fragmentTransaction.add(R.id.fragment_qsort_question, openAnswerFragment);
                fragmentTransaction.add(R.id.fragment_qsort_question_nav, navFragment);
            }

        }
        fragmentTransaction.commit();
    }

}
