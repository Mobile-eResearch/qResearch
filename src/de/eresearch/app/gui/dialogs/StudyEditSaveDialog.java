package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import de.eresearch.app.R;

/**
 * 
 * @author a1o
 *
 */
public class StudyEditSaveDialog extends DialogFragment {

    public static final String STUDY_ID = "de.eresearch.app.gui.dialogs.StudyEditSaveDialog.STUDY_ID";
    
    private Callbacks mCallbacks;
        
    public interface Callbacks {
        public void onSaveClick(DialogFragment dialog);
        public void onAbortClick(DialogFragment dialog);
    }    
  
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.study_save_metadata_message)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { 
                        mCallbacks.onSaveClick(StudyEditSaveDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCallbacks.onAbortClick(StudyEditSaveDialog.this);

                    }
                });

        return builder.create();
    }
    
      
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "callback in StudyEdit is not implemented");
        }
    }
 
}
