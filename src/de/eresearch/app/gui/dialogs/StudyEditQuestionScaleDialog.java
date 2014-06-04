
package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;

public class StudyEditQuestionScaleDialog extends DialogFragment {

    private ScaleQuestion mSq;
    private boolean isNewS = true;
    private String mScalePoleLeft, mScalePoleRight;
    private EditText mTextLeftPole, mTextRightPole;
    private int mPos = -1;
    private Callbacks mCallbacks;
    private Scale mScale = new Scale(-1);
    private int mScaleCount = 5;    
    private List<EditText> mScaleValues = new ArrayList<EditText>();    
    private LinearLayout mLinearLayout;

    private EditText editText(int id) {
        EditText editText = new EditText(getActivity());
        editText.setId(id);
        CharSequence hint = getString(R.string.study_edit_scaled_questions_scale_grades_hint);
        editText.setHint(hint + " " + (id+1));
        editText.setSingleLine();
        editText.setGravity(Gravity.CENTER);        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(450,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 15, 10, 0);
        editText.setLayoutParams(params);
        mScaleValues.add(editText);
        editText.setText("");
        return editText;
    }

    public interface Callbacks {
        public void onSaveScaleClick();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_study_edit_scale_question_answer, null);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.scales_container_layout);
        mTextLeftPole = (EditText) v.findViewById(R.id.question_scalenpole_left);
        mTextLeftPole.addTextChangedListener(new TextWatcher() {

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
                mScalePoleLeft = mTextLeftPole.getText().toString();
                Log.d("ScaleDialog", "Setting Pole");
            }

        });

        mTextRightPole = (EditText) v.findViewById(R.id.question_scalenpole_right);
        mTextRightPole.addTextChangedListener(new TextWatcher() {

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
                mScalePoleRight = mTextRightPole.getText().toString();
                Log.d("ScaleDialog", "Setting Pole");
            }

        });

        if (!isNewS)
        {
            mScale = mSq.getScales().get(mPos);
            mScaleCount = mScale.getScaleValues().size();
            mScalePoleLeft = mScale.getPoleLeft();
            mScalePoleRight = mScale.getPoleRight();
            mTextLeftPole.setText(mScalePoleLeft);
            mTextRightPole.setText(mScalePoleRight);
            for (int i = 0; i < mScaleCount; i++)
            {
                mLinearLayout.addView(editText(i));
                mScaleValues.get(i).setText(mScale.getScaleValues().get(i));
            }
        }
        else
        {
            for (int i = 0; i < mScaleCount; i++)
            {
                mLinearLayout.addView(editText(i));
            }
            mTextLeftPole.setText("");
            mTextRightPole.setText("");
        }

        // mLinearLayout.addView(editText());

        builder.setView(v)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mScale.setPoleLeft(mScalePoleLeft);
                        mScale.setPoleRight(mScalePoleRight);
                        if (isNewS)
                        {

                            for (int i = 0; i < mScaleCount; i++)
                            {
                                mScale.addScaleValue(mScaleValues.get(i).getText().toString());
                            }

                            mSq.addScale(mScale);
                        }
                        else
                        {

                            for (int i = 0; i < mScaleCount; i++)
                            {
                                mScale.getScaleValues().set(i,
                                        mScaleValues.get(i).getText().toString());
                            }

                            mSq.getScales().set(mPos, mScale);
                        }
                        mCallbacks.onSaveScaleClick();
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

    public void setPoles(String left, String right)
    {
        mScalePoleLeft = left;
        mScalePoleRight = right;
    }

    public void setQuestion(ScaleQuestion s)
    {
        mSq = s;
    }

    public void setNew(boolean b)
    {
        isNewS = b;
    }

    public void setPos(int pos)
    {
        mPos = pos;
    }

    public void setScaleCount(int count)
    {
        mScaleCount = count;
    }

}
