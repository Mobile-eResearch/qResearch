
package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudyEditQuestionClosedListAdapter;
import de.eresearch.app.gui.dialogs.StudyEditQuestionClosedDialog;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.Phase;

/**
 * Provides Fragment for edit/create closed questions for a study (frame on the
 * right side, next to the {@link StudyEditListFragment})
 */
public class StudyEditQuestionClosedFragment extends Fragment {

    private final String LOG_TAG = "StudyEditQuestionClosedFragment";
    private EditText mQuestionText;
    private StudyEditContainer sec;
    private StudyEditQuestionClosedListAdapter mListAdapter;
    private ImageView mAddButton;
    private Phase mPhase;
    private int mPos = -1;
    private boolean isNew = true;
    private String mQuestionTextS = "";
    private Button mSaveButton, mCancelButton;
    private CheckBox mFreetext, mMultipleChoice;
    private boolean isFreetext, isMultipleChoice;
    private TextView mEmptyNotice;
    private ListView mAnswerList;
    private ClosedQuestion mCq = new ClosedQuestion(-1, -1);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = StudyEditContainer.getStudyEditContainer();
        mCq.setStudyId(sec.getStudy().getId());
        mPhase = (Phase) getArguments().getSerializable("PHASE");
        mPos = getArguments().getInt("POSITION");
        isNew = getArguments().getBoolean("NEWQ");
        if (!isNew)
        {
            mCq = (ClosedQuestion) sec.getStudy().getQuestions(mPhase).get(mPos);
            mQuestionTextS = mCq.getText();
            isFreetext = mCq.hasOpenField();
            isMultipleChoice = mCq.isMultipleChoice();
            Log.i(LOG_TAG, "Initializing StudyEditClosedQuestionFragment. New: " + isNew + " position: " + mPos);
            for (int k = 0; k<mCq.getPossibleAnswers().size(); k++)
            {
                Log.i(LOG_TAG, "Answer: " + mCq.getPossibleAnswers().get(k));
            }
            
        }
        
        mListAdapter = new StudyEditQuestionClosedListAdapter (getActivity(),
                R.layout.fragment_study_edit_question_closed_row, (ArrayList<String>) mCq.getPossibleAnswers(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_edit_question_closed,
                container, false);


        Log.i("ClosedQuestion Bundle: ", " Phase: " + mPhase.toString() + " " + "isNew: " + isNew
                + " " + "position: " + mPos);
        
       

        mQuestionText = (EditText) rootView.findViewById(R.id.question_text);
        mFreetext = (CheckBox) rootView.findViewById(R.id.freetext);
        mMultipleChoice = (CheckBox) rootView.findViewById(R.id.multiple_choice);



        mAnswerList = (ListView) rootView.findViewById(R.id.closed_question_answers);
        mAnswerList.setAdapter(mListAdapter);

        mSaveButton = (Button) rootView.findViewById(R.id.closed_button_ok);
        mCancelButton = (Button) rootView.findViewById(R.id.closed_button_cancel);
        mAddButton = (ImageView) rootView.findViewById(R.id.fragment_study_question_addButton);
        mEmptyNotice = (TextView) rootView.findViewById(R.id.empty_notice);
        
        if (!isNew)
        {
            mQuestionText.setText(mQuestionTextS);
            mQuestionText.setSelection(mQuestionTextS.length());
            mFreetext.setChecked(isFreetext);
            mMultipleChoice.setChecked(isMultipleChoice);
        }
        

        mAddButton.setOnClickListener(clickAddButton);
        mSaveButton.setOnClickListener(clickSaveButton);
        mCancelButton.setOnClickListener(clickCancelButton);
        mQuestionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                mQuestionTextS = mQuestionText.getText().toString();    
            }

        });
        
        mFreetext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mFreetext.isChecked()){
                    Log.d(LOG_TAG, "Checked");
                    isFreetext = true;
                }else{
                    Log.d(LOG_TAG, "unChecked");
                    isFreetext = false;
                }
            }
        });
        
        mMultipleChoice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mMultipleChoice.isChecked()){
                    Log.d(LOG_TAG, "Checked");
                    isMultipleChoice = true;
                }else{
                    Log.d(LOG_TAG, "unChecked");
                    isMultipleChoice = false;
                }
            }
        });
        

        mAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {
                Log.d(LOG_TAG, "Click to edit");                
                StudyEditQuestionClosedDialog dialog = new StudyEditQuestionClosedDialog();
                dialog.setQuestion(mCq);
                dialog.setNew(false);
                dialog.setPos(position);
                dialog.show(getFragmentManager(), "StudyEditQuestionClosedDialog");
            }
        });

        status();
        return rootView;
    }  

    OnClickListener clickSaveButton = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.e("ClosedQuestionFragment", "Saving Closed Question");    
            mCq.setText(mQuestionTextS);
            mCq.setMultipleChoice(isMultipleChoice);
            mCq.setOpenField(isFreetext);
            switch (mPhase) {
                case QUESTIONS_PRE:
                    mCq.setIsPost(false);                   
                    break;
                case QUESTIONS_POST:
                    mCq.setIsPost(true);                    
                    break;
                default:
                    Log.e("StudyEditQuestionOpenFragment", "UNKNOWN PHASE IN QUESTION");
                    break;
            }

            if (isNew)
            {      
                mCq.setId(-1);
                mCq.setOrderNumber(sec.getStudy().getQuestions(mPhase).size());
                sec.getStudy().addQuestion(mPhase, mCq);
                sec.isChanged = true;
            }
            else
            {
                int index = mPos;
                //mCq.setOrderNumber(mPos);
                sec.getStudy().getQuestions(mPhase).remove(index);
                sec.getStudy().getQuestions(mPhase).add(index, mCq);
                sec.isChanged = true;
            }

            // Log.i("StudyEditQuestionOpenFragment", "P");
            StudyEditQuestionFragment fragment = new StudyEditQuestionFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_study_edit_right_pane, fragment)
                    .commit();

        }

    };

    OnClickListener clickCancelButton = new OnClickListener() {

        @Override
        public void onClick(View v) {
            StudyEditQuestionFragment fragment = new StudyEditQuestionFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_study_edit_right_pane, fragment)
                    .commit();

        }

    };

   

    public void onListDeleteClick(String answer) {
        Log.d(LOG_TAG, "REMOVING");
        int index = mCq.getPossibleAnswers().indexOf(answer);
        mCq.getPossibleAnswers().remove(index);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();

    }

    public void onListUpClick(String answer) {

        int index = mCq.getPossibleAnswers().indexOf(answer);
        mCq.getPossibleAnswers().remove(index);
        mCq.getPossibleAnswers().add(index - 1, answer);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();

    }

    public void onListDownClick(String answer) {

        int index = mCq.getPossibleAnswers().indexOf(answer);
        mCq.getPossibleAnswers().remove(index);
        mCq.getPossibleAnswers().add(index + 1, answer);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();
    }

    OnClickListener clickAddButton = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "ADD ANSWER");
            StudyEditQuestionClosedDialog dialog = new StudyEditQuestionClosedDialog();
            dialog.setQuestion(mCq);
            dialog.setNew(true);
            dialog.show(getFragmentManager(), "StudyEditQuestionClosedDialog");
        }

    };

   private void status() {
       Log.d(LOG_TAG, "Status change");
       Log.d(LOG_TAG, "Answer count: " + mCq.getPossibleAnswers().size());
       
        if (mCq.getPossibleAnswers().size() < 1)
        {
            mEmptyNotice.setVisibility(View.VISIBLE);
        }
        else
        {
            mEmptyNotice.setVisibility(View.INVISIBLE);
        }
    }

   void notifyNewAnswer()
   {
       status();
       mListAdapter.notifyDataSetChanged();
   }

  
}
