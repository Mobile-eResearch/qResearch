package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import de.eresearch.app.R;

public class NotesBackDialog extends DialogFragment {
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(getActivity());
        exitDialog.setTitle(R.string.notes_activity_exitdialog_title);
        exitDialog.setMessage(R.string.notes_activity_exitdialog_message);
        exitDialog.setPositiveButton(
                R.string.notes_activity_exitdialog_button_exit, 
                new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        exitDialog.setNegativeButton
            (R.string.notes_activity_exitdialog_button_back, null);
        
        return exitDialog.create();
    }

}
