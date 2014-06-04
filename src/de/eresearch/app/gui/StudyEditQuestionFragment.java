
package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudyEditQuestionListAdapter;
import de.eresearch.app.gui.dialogs.StudyEditQuestionSelectorDialog;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Provides Fragment for edit/create questions for a study (frame on the right
 * side, next to the {@link StudyEditListFragment})
 */
public class StudyEditQuestionFragment extends Fragment {
    private StudyEditContainer sec;
    private StudyEditQuestionListAdapter mListAdapterPre, mListAdapterPost;
    private ImageView mAddButtonPre, mAddButtonPost;
    private ListView mQuestionListPre, mQuestionListPost;
    private static final String LOG_TAG = "StudyEditQuestionFragment";
    private TextView mPreSort, mPostSort;
    private boolean mIsCorrect = true;
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = StudyEditContainer.getStudyEditContainer();
        mListAdapterPre = new StudyEditQuestionListAdapter(getActivity(),
                R.layout.fragment_study_edit_question_row, sec.getStudy().getQuestions(
                        Phase.QUESTIONS_PRE), this, Phase.QUESTIONS_PRE);

        mListAdapterPost = new StudyEditQuestionListAdapter(getActivity(),
                R.layout.fragment_study_edit_question_row, sec.getStudy().getQuestions(
                        Phase.QUESTIONS_POST), this, Phase.QUESTIONS_POST);
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_study_edit_question,
                container, false);
        Log.d(LOG_TAG, "Question count: " + sec.getStudy().getAllQuestions().size());
        mAddButtonPre = (ImageView) rootView
                .findViewById(R.id.fragment_study_question_addButton_pre);
        mAddButtonPre.setOnClickListener(clickAddButtonPre);

        mAddButtonPost = (ImageView) rootView
                .findViewById(R.id.fragment_study_question_addButton_post);
        mAddButtonPost.setOnClickListener(clickAddButtonPost);

        mQuestionListPre = (ListView) rootView.findViewById(R.id.questions_presort);
        mQuestionListPre.setAdapter(mListAdapterPre);

        mQuestionListPost = (ListView) rootView.findViewById(R.id.questions_postsort);
        mQuestionListPost.setAdapter(mListAdapterPost);

        mPreSort = (TextView) rootView.findViewById(R.id.presort_notice);
        mPostSort = (TextView) rootView.findViewById(R.id.postsort_notice);

        mQuestionListPre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {             
                Question q = sec.getStudy().getQuestions(Phase.QUESTIONS_PRE).get(position);
                if (q instanceof OpenQuestion)
                {
                    Log.d(LOG_TAG, "Instance of Open Question");
                    StudyEditQuestionOpenFragment fragment = new StudyEditQuestionOpenFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_PRE);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"OpenQuestionFragment")
                            .commit();

                }
                else if (q instanceof ClosedQuestion)
                {
                    Log.d(LOG_TAG, "Instance of Closed Question");
                    StudyEditQuestionClosedFragment fragment = new StudyEditQuestionClosedFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_PRE);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"ClosedQuestionFragment")
                            .commit();
                }
                else
                {
                    Log.d(LOG_TAG, "Instance of Skale Question");
                    StudyEditQuestionScaleFragment fragment = new StudyEditQuestionScaleFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_PRE);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"ScaleQuestionFragment")
                            .commit();
                }

            }

        });
        
        mQuestionListPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {             
                Question q = sec.getStudy().getQuestions(Phase.QUESTIONS_POST).get(position);
                if (q instanceof OpenQuestion)
                {
                    Log.d(LOG_TAG, "Instance of Open Question");
                    StudyEditQuestionOpenFragment fragment = new StudyEditQuestionOpenFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_POST);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"OpenQuestionFragment")
                            .commit();

                }
                else if (q instanceof ClosedQuestion)
                {
                    Log.d(LOG_TAG, "Instance of Closed Question");
                    StudyEditQuestionClosedFragment fragment = new StudyEditQuestionClosedFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_POST);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"ClosedQuestionFragment")
                            .commit();
                }
                else
                {
                    Log.d(LOG_TAG, "Instance of Skale Question");
                    StudyEditQuestionScaleFragment fragment = new StudyEditQuestionScaleFragment();                    ;                    
                    Bundle b = new Bundle();
                    b.putSerializable("PHASE", Phase.QUESTIONS_POST);
                    b.putInt("POSITION", position);
                    b.putBoolean("NEWQ", false);
                    fragment.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.activity_study_edit_right_pane, fragment,"ScaleQuestionFragment")
                            .commit();
                }

            }

        });
        
        status();
        return rootView;
    }
    
   
    private void status() {
        if (sec.getStudy().getQuestions(Phase.QUESTIONS_PRE).size() < 1)
        {
            mPreSort.setVisibility(View.VISIBLE);
        }
        else
        {
            mPreSort.setVisibility(View.INVISIBLE);
        }

        if (sec.getStudy().getQuestions(Phase.QUESTIONS_POST).size() < 1)
        {
            mPostSort.setVisibility(View.VISIBLE);
        }
        else
        {
            mPostSort.setVisibility(View.INVISIBLE);
        }
        
        
        setListViewStatus();
        
    }

    OnClickListener clickAddButtonPre = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "ADD PRE");
            StudyEditQuestionSelectorDialog dialog = new StudyEditQuestionSelectorDialog();
            Bundle b = new Bundle();
            b.putSerializable("PHASE", Phase.QUESTIONS_PRE);
            b.putBoolean("NEWQ", true);
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "StudyEditQuestionSelectorDialog");
        }

    };

    OnClickListener clickAddButtonPost = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "ADD POST");
            StudyEditQuestionSelectorDialog dialog = new StudyEditQuestionSelectorDialog();
            Bundle b = new Bundle();
            b.putSerializable("PHASE", Phase.QUESTIONS_POST);
            b.putBoolean("NEWQ", true);
            dialog.setArguments(b);
            dialog.show(getFragmentManager(), "StudyEditQuestionSelectorDialog");
        }

    };

    public void onListDeleteClick(Question q, Phase p) {
        Log.d(LOG_TAG, "REMOVING");
        int index = sec.getStudy().getQuestions(p).indexOf(q);
        sec.getStudy().getQuestions(p).remove(index);         
        for (int i = index; i<sec.getStudy().getQuestions(p).size(); i++)
        {            
            sec.getStudy().getQuestions(p).get(i).setOrderNumber(i);            
        }
        if (p == Phase.QUESTIONS_POST)
        {
            mListAdapterPost.notifyDataSetChanged();
        }
        else
        {
            mListAdapterPre.notifyDataSetChanged();
        }
        sec.isChanged = true;
        status();

    }

    public void onListUpClick(Question q, Phase p) {

        int index = sec.getStudy().getQuestions(p).indexOf(q);
        sec.getStudy().getQuestions(p).remove(index);
        q.setOrderNumber(index - 1);                    
        sec.getStudy().getQuestions(p).add(index - 1, q);
        sec.getStudy().getQuestions(p).get(index).setOrderNumber(index);
        if (p == Phase.QUESTIONS_POST)
        {
            mListAdapterPost.notifyDataSetChanged();
        }
        else
        {
            mListAdapterPre.notifyDataSetChanged();
        }
        sec.isChanged = true;
        status();

    }

    public void onListDownClick(Question q, Phase p) {

        int index = sec.getStudy().getQuestions(p).indexOf(q);
        sec.getStudy().getQuestions(p).remove(index);
        q.setOrderNumber(index + 1);       
        sec.getStudy().getQuestions(p).add(index + 1, q);
        sec.getStudy().getQuestions(p).get(index).setOrderNumber(index);
        if (p == Phase.QUESTIONS_POST)
        {
            mListAdapterPost.notifyDataSetChanged();
        }
        else
        {
            mListAdapterPre.notifyDataSetChanged();
        }
        sec.isChanged = true;
        status();
    }
    
    
    public void setListViewStatus(){
        List<Question> l = new ArrayList<Question>(); 
        l.addAll(sec.getStudy().getQuestions(Phase.QUESTIONS_PRE));
        l.addAll(sec.getStudy().getQuestions(Phase.QUESTIONS_POST));
        
        if (l.size() == 0)
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Questions, true);
        
        for(Question q: l){
            if (!q.isConsistent())
            {
                ((StudyEditListFragment) getFragmentManager().findFragmentById(
                        R.id.fragment_study_edit_list))
                        .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Questions, false);
                break;
            }
            else
                ((StudyEditListFragment) getFragmentManager().findFragmentById(
                        R.id.fragment_study_edit_list))
                        .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Questions, true);
                
        }
    }
    


}
