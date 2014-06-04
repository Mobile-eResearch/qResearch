


package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.db.tables.QuestionsTable;
import de.eresearch.app.gui.adapter.StudiesListFragmentArrayAdapter;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.gui.adapter.QuestionAnswerListAdapter;
import de.eresearch.app.logic.tasks.common.qsort.GetAnswersTask;
import de.eresearch.app.logic.tasks.common.questions.GetQuestionsTask;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;

/**
 * Fragment for list answered questions from a Qsort.
 */
public class QuestionAnswerListFragment extends Fragment implements GetQuestionsTask.Callbacks {


    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    public static final String TYPE = "de.eresearch.app.gui.QuestionAnswerListFragment";
    /**
     * The current activated item position.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private int mStudyId;

    private QuestionAnswerListAdapter adapter;

    private ListView mQuestionList;
    private TextView mHeader;

    private RelativeLayout mHeadText;

    private List<Question> questions;
    private boolean isPostSort;

    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Callback for fragment lifecycle onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_question_answer_list,
                container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        isPostSort = (Boolean) extras.get(QSortsDetailFragment.IS_POSTSORT);
//        mHeadText = (RelativeLayout) rootView.findViewById(R.id.QuestionAnswerHeaderText);
        mHeader = (TextView) rootView.findViewById(R.id.TextViewHeader);
        mQuestionList = (ListView) rootView.findViewById(android.R.id.list);
        
        if(isPostSort){
            mHeader.setText("Postsort");
        }else{
            mHeader.setText("Presort");
        }

        

        return rootView;
    }
    
   

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Question q);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Question q) {
        }
    };

   

    public void onResume() {
        super.onResume();

        questions = new ArrayList<Question>();

        Bundle extras = getActivity ().getIntent().getExtras();
        int mStudyId = extras.getInt(QSortsActivity.STUDY_ID);
        int mQSortId = extras.getInt(QSortsDetailFragment.QSORT_ID);

        GetQuestionsTask t = new GetQuestionsTask(this.getActivity(), this, mStudyId);
        t.execute();

        adapter = new QuestionAnswerListAdapter(getActivity(),
                R.layout.list_question_fragment, questions);
        mQuestionList.setAdapter(adapter);

        mQuestionList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(questions.get(position));
            }

        });
        Log.d("QuestionsListFragment", "");

    }

    /**
     * Callback for fragment lifecycle onAttach(). Method should check if
     * activity (this fragmenet is attached to) implements the callback method
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    /**
     * Callback for fragment lifecycle onDetach()
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * Callback for fragment lifecycle onViewCreated() Restore the previously
     * serialized activated item position.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }
    }

    /**
     * Callback for fragment lifecycle onSaveInstanceState() Serialize and
     * persist the activated item position.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mQuestionList.setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mQuestionList.setItemChecked(mActivatedPosition, false);
        } else {
            mQuestionList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * Callback method from GetquestionsTask
     * 
     * @param studiesList A List of Questionsobjects
     */

    @Override
    public void onGetQuestionTaskUpdate(List<Question> questionsList) {
        List<Question> templist = new ArrayList<Question>();

        for (Question q : questionsList) {
            if (q.isPost() == isPostSort) {
                templist.add(q);
            }
        }

        this.questions = templist;
        adapter.clear();
        adapter.addAll(questions);

    }

 

}
