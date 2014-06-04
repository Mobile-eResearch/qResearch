package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditActivity;

/**
 * 
 * @author thg
 *
 */
public class StudyEditDialog extends DialogFragment{

    
    public static final String STUDY_ID = "de.eresearch.app.gui.dialogs.StudyEditDialog.STUDY_ID";
    public static final String STUDY_NAME = "de.eresearch.app.gui.dialogs.StudyEditDialog.STUDY_NAME";
    
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(STUDY_NAME) + ": " + getString(R.string.study_edit_dialog_title));
        builder.setMessage(R.string.study_edit_dialog_message)
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       Intent intent = new Intent(getActivity(), StudyEditActivity.class);
                       intent.putExtra(StudyEditActivity.STUDY_NEW, false);
                       intent.putExtra(StudyEditActivity.STUDY_ID, getArguments().getInt(STUDY_ID));
                       getActivity().startActivityForResult(intent, 100);
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
