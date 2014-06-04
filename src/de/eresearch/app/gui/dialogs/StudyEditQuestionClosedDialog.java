
package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditItemsFragment;
import de.eresearch.app.gui.StudyEditQuestionClosedFragment;
import de.eresearch.app.logic.model.ClosedQuestion;

public class StudyEditQuestionClosedDialog extends DialogFragment {

    private ClosedQuestion mCq;
    private boolean isNewA = true;
    private String mAnswer = "";
    private EditText mText;
    private int mPos = -1;
    private Callbacks mCallbacks;

    public interface Callbacks {
        public void onSaveAnswerClick();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // isNewA = getArguments().getBoolean("NEWA");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_study_edit_closed_question_answer, null);
        mText = (EditText) v.findViewById(R.id.question_answer);
        mText.addTextChangedListener(new TextWatcher() {

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
                mAnswer = mText.getText().toString();
            }

        });

        if (!isNewA)
        {
            mAnswer = mCq.getPossibleAnswers().get(mPos);
            mText.setText(mAnswer);
            mText.setSelection(mAnswer.length());
        }

        builder.setView(v)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isNewA)
                        {
                            Log.i("ClosedQuestionDialog", "Adding new answer: " + mAnswer);
                            mCq.addPossibleAnswer(mAnswer);
                        }
                        else
                        {
                            Log.i("ClosedQuestionDialog", "Replacing answer at " + mPos
                                    + " position: " + mAnswer);
                            mCq.getPossibleAnswers().set(mPos, mAnswer);
                        }
                        mCallbacks.onSaveAnswerClick();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "callback is not implemented");
        }
    }

    public void setAnswer(String s)
    {
        mAnswer = s;
    }

    public void setQuestion(ClosedQuestion q)
    {
        mCq = q;
    }

    public void setNew(boolean b)
    {
        isNewA = b;
    }

    public void setPos(int pos)
    {
        mPos = pos;
    }

}
