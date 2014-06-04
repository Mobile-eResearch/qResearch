
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudyEditQuestionScaleListAdapter;
import de.eresearch.app.gui.dialogs.StudyEditQuestionScaleDialog;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Provides Fragment for edit/create closed questions for a study (frame on the
 * right side, next to the {@link StudyEditListFragment})
 */
public class StudyEditQuestionScaleFragment extends Fragment {

    private final String LOG_TAG = "StudyEditQuestionScaleFragment";
    private EditText mQuestionText;
    private StudyEditContainer sec;
    private StudyEditQuestionScaleListAdapter mListAdapter;
    private ImageView mAddButton;
    private Phase mPhase;
    private int mPos = -1;
    private boolean isNew = true;
    private String mQuestionTextS = "";
    private Button mSaveButton, mCancelButton;
    private TextView mEmptyNotice;
    private ListView mAnswerList;
    private ScaleQuestion mSq = new ScaleQuestion(-1, -1);
    private Spinner mSpinner;
    private int mScaleCount = 5;
    // private String[] mSpinnerContents = {
    // "5", "6", "7", "8", "9", "10"
    // };
    // FIXME: Workaround for Bug: 1988
    private String[] mSpinnerContents = {
            "5", "6", "7", "8"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = StudyEditContainer.getStudyEditContainer();
        mSq.setStudyId(sec.getStudy().getId());
        mPhase = (Phase) getArguments().getSerializable("PHASE");
        mPos = getArguments().getInt("POSITION");
        isNew = getArguments().getBoolean("NEWQ");
        if (!isNew)
        {
            mSq = (ScaleQuestion) sec.getStudy().getQuestions(mPhase).get(mPos);
            mQuestionTextS = mSq.getText();
            if (mSq.getScales().size() != 0)
            {
                mScaleCount = mSq.getScales().get(0).getScaleValues().size();
                Log.i(LOG_TAG, "Starting scale size: " + mScaleCount);
            }             

        }

        mListAdapter = new StudyEditQuestionScaleListAdapter(getActivity(),
                R.layout.fragment_study_edit_question_scale_row,
                (ArrayList<Scale>) mSq.getScales(), this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_edit_question_scale,
                container, false);

        Log.i("ClosedQuestion Bundle: ", "Phase: " + mPhase.toString() + " " + "isNew: " + isNew
                + " " + "position: " + mPos);

        mQuestionText = (EditText) rootView.findViewById(R.id.question_text);

        mAnswerList = (ListView) rootView.findViewById(R.id.scale_question_answers);
        mAnswerList.setAdapter(mListAdapter);

        mSpinner = (Spinner) rootView.findViewById(R.id.spinner);
        setSpinner();

        mSaveButton = (Button) rootView.findViewById(R.id.closed_button_ok);
        mCancelButton = (Button) rootView.findViewById(R.id.closed_button_cancel);
        mAddButton = (ImageView) rootView.findViewById(R.id.fragment_study_question_addButton);
        mEmptyNotice = (TextView) rootView.findViewById(R.id.empty_notice);

        if (!isNew)
        {
            mQuestionText.setText(mQuestionTextS);
            mQuestionText.setSelection(mQuestionTextS.length());

        }

        mAddButton.setOnClickListener(clickAddButton);
        mSaveButton.setOnClickListener(clickSaveButton);
        mCancelButton.setOnClickListener(clickCancelButton);
        mQuestionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mQuestionTextS = mQuestionText.getText().toString();
            }

        });

        mAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                    int position, long id) {
                Log.d(LOG_TAG, "Click to edit");
                StudyEditQuestionScaleDialog dialog = new StudyEditQuestionScaleDialog();
                dialog.setQuestion(mSq);
                dialog.setNew(false);
                dialog.setPos(position);
                dialog.show(getFragmentManager(), "StudyEditQuestionScaledDialog");
            }
        });

        status();
        return rootView;
    }

    OnClickListener clickSaveButton = new OnClickListener() {

        @Override
        public void onClick(View v) {

            sec.isChanged = true;
            mSq.setText(mQuestionTextS);
            switch (mPhase) {
                case QUESTIONS_PRE:
                    mSq.setIsPost(false);
                    break;
                case QUESTIONS_POST:
                    mSq.setIsPost(true);
                    break;
                default:
                    Log.e("StudyEditQuestionOpenFragment", "UNKNOWN PHASE IN QUESTION");
                    break;
            }

            if (isNew)
            {
                mSq.setId(-1);
                mSq.setOrderNumber(sec.getStudy().getQuestions(mPhase).size());
                sec.getStudy().addQuestion(mPhase, mSq);
            }
            else
            {
                int index = mPos;
                // mSq.setOrderNumber(mPos);
                sec.getStudy().getQuestions(mPhase).remove(index);
                sec.getStudy().getQuestions(mPhase).add(index, mSq);
            }

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

    public void onListDeleteClick(Scale answer) {
        Log.d(LOG_TAG, "REMOVING");
        int index = mSq.getScales().indexOf(answer);
        Log.d(LOG_TAG, "index: " + index);
        mSq.getScales().remove(index);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();

    }

    public void onListUpClick(Scale answer) {

        int index = mSq.getScales().indexOf(answer);
        mSq.getScales().remove(index);
        mSq.getScales().add(index - 1, answer);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();

    }

    public void onListDownClick(Scale answer) {

        int index = mSq.getScales().indexOf(answer);
        mSq.getScales().remove(index);
        mSq.getScales().add(index + 1, answer);
        mListAdapter.notifyDataSetChanged();
        sec.isChanged = true;
        status();
    }

    OnClickListener clickAddButton = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "ADD ANSWER");
            StudyEditQuestionScaleDialog dialog = new StudyEditQuestionScaleDialog();
            dialog.setQuestion(mSq);
            dialog.setNew(true);
            dialog.setScaleCount(mScaleCount);
            dialog.show(getFragmentManager(), "StudyEditQuestionScaleDialog");
        }

    };

    private void status() {
        Log.d(LOG_TAG, "Status change");
        Log.d(LOG_TAG, "Scales count: " + mSq.getScales().size());

        if (mSq.getScales().size() < 1)
        {
            mEmptyNotice.setVisibility(View.VISIBLE);
        }
        else
        {
            mEmptyNotice.setVisibility(View.INVISIBLE);
        }
    }

    public void setSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mSpinnerContents);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        Log.i(LOG_TAG, "Current mScaleCount: " + mScaleCount);
        mSpinner.setSelection(dataAdapter.getPosition(Integer.toString(mScaleCount)));
    }

    class CustomOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            mScaleCount = Integer.parseInt((String) parent.getItemAtPosition(pos));
            for (int i = 0; i < mSq.getScales().size(); i++)
            {
                if (mSq.getScales().get(i).getScaleValues().size() < mScaleCount)
                {
                    while (mSq.getScales().get(i).getScaleValues().size() < mScaleCount)
                    {

                        mSq.getScales().get(i).addScaleValue("");
                    }
                }
                if (mSq.getScales().get(i).getScaleValues().size() > mScaleCount)
                {

                    while (mSq.getScales().get(i).getScaleValues().size() > mScaleCount)
                    {

                        mSq.getScales().get(i).getScaleValues()
                                .remove(mSq.getScales().get(i).getScaleValues().size() - 1);
                    }
                }
            }
            Log.d(LOG_TAG, "scale size: " + mScaleCount);
            status();
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    void notifyNewAnswer()
    {
        status();
        mListAdapter.notifyDataSetChanged();
    }

}
