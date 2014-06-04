
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
import android.widget.Button;
import android.widget.EditText;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Phase;

/**
 * Provides Fragment for edit/create open questions for a study (frame on the
 * right side, next to the {@link StudyEditListFragment})
 */
public class StudyEditQuestionOpenFragment extends Fragment {

    private EditText mQuestionText;
    private StudyEditContainer sec;
    private Phase mPhase;
    private int mPos = -1;
    private boolean isNew = true;
    private String mQuestionTextS = "";
    private Button mSaveButton, mCancelButton;
    private OpenQuestion mOq = new OpenQuestion(-1, -1);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = StudyEditContainer.getStudyEditContainer();
        mOq.setStudyId(sec.getStudy().getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_edit_question_open,
                container, false);
        mPhase = (Phase) getArguments().getSerializable("PHASE");
        mPos = getArguments().getInt("POSITION");
        isNew = getArguments().getBoolean("NEWQ");
        mQuestionText = (EditText) rootView.findViewById(R.id.question_text);
        Log.i("OpenQuestion Bundle: ", " Phase: " + mPhase.toString() + " " + "isNew: " + isNew
                + " " + "position: " + mPos);
        if (!isNew)
        {
            mQuestionTextS = sec.getStudy().getQuestions(mPhase).get(mPos).getText();
            mQuestionText.setText(mQuestionTextS);
            mQuestionText.setSelection(mQuestionTextS.length());
        }

        mSaveButton = (Button) rootView.findViewById(R.id.button_ok);
        mCancelButton = (Button) rootView.findViewById(R.id.button_cancel);

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

        return rootView;
    }

    OnClickListener clickSaveButton = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // Log.e("OpenQuestionFragment", "Saving Open Question");
            mOq.setText(mQuestionTextS);
            switch (mPhase) {
                case QUESTIONS_PRE:
                    mOq.setIsPost(false);
                    break;
                case QUESTIONS_POST:
                    mOq.setIsPost(true);
                    break;
                default:
                    Log.e("StudyEditQuestionOpenFragment", "UNKNOWN PHASE IN QUESTION");
                    break;
            }

            if (isNew)
            {
                mOq.setId(-1);
                mOq.setOrderNumber(sec.getStudy().getQuestions(mPhase).size());
                sec.getStudy().addQuestion(mPhase, mOq);
                sec.isChanged = true;
            }
            else
            {
                int index = mPos;
                // mOq.setOrderNumber(mPos);
                sec.getStudy().getQuestions(mPhase).remove(index);
                sec.getStudy().getQuestions(mPhase).add(index, mOq);
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

    /*
     * public void setQuestion(Question q) { mQuestion = q; }
     */

}
