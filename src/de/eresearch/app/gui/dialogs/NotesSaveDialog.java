package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import de.eresearch.app.R;
import de.eresearch.app.gui.NotesDetailFragment;


/**
 * Class for the QSort-Finish-Dialog
 * 
 * @author Marcel
 *
 */
public class NotesSaveDialog  extends DialogFragment{
    
    Context mContext;
    
    int mNoteId;


    Callbacks mListener;
    
    public interface Callbacks {
        public void onSaveDialogOKClick(int noteId, String title, String text);
    }
    
    // Source: http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement Callbacks");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.note_save_dialog_message)
        .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onSaveDialogOKClick(getArguments().getInt(NotesDetailFragment.NOTE_ID), getArguments().getString(NotesDetailFragment.NOTE_TITLE),
                        getArguments().getString(NotesDetailFragment.NOTE_TEXT));
                
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
