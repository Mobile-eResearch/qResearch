
package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;

public class StudyEditQuestionSelectorDialog extends DialogFragment {

    private Callbacks mCallbacks;
    private Phase mPhase;
    private boolean isNewQ;

    public interface Callbacks {
        public void onOpenTypeClick(DialogFragment dialog, Phase p, boolean isNew);

        public void onClosedTypeClick(DialogFragment dialog, Phase p, boolean isNew);

        public void onScaleTypeClick(DialogFragment dialog, Phase p, boolean isNew);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPhase = (Phase) getArguments().getSerializable("PHASE");
        isNewQ = getArguments().getBoolean("NEWQ");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.study_questions_type_message)
                .setNegativeButton(R.string.study_edit_question_type_open,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mCallbacks.onOpenTypeClick(StudyEditQuestionSelectorDialog.this,
                                        mPhase, isNewQ);
                            }
                        })
                .setNeutralButton(R.string.study_edit_question_type_closed,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mCallbacks.onClosedTypeClick(StudyEditQuestionSelectorDialog.this,
                                        mPhase, isNewQ);

                            }
                        })
                .setPositiveButton(R.string.study_edit_question_type_scale,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mCallbacks.onScaleTypeClick(StudyEditQuestionSelectorDialog.this,
                                        mPhase, isNewQ);

                            }
                        })

        ;

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "callback in StudyEdit is not implemented");
        }
    }

}
