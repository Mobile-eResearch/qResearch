
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Savepoint;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.tasks.media.SaveNoteTask;
import de.eresearch.app.logic.tasks.media.SaveNoteTask.Callbacks;

/**
* Class for the note-dialog to add a note in the QSort
*
* @author domme
*
*/
public class QSortNoteDialog extends Dialog  implements SaveNoteTask.Callbacks{

    private Context mContext;

    private Phase mPhase;

    private EditText mNoteTitle;

    private EditText mNoteText;

    private AlertDialog alertDialog;

    /**
     * Constructor
     * 
     * @param context Context of the activity
     * @param phase The Phase in which the Q-Sort is
     */
    public QSortNoteDialog(Context context, Phase phase) {
        super(context);

        mPhase = phase;
        mContext = context;

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_qsort_note, null);

        mNoteTitle = (EditText)
                v.findViewById(R.id.qsort_dialog_note_title);
        mNoteTitle.setHint(makeTitle(context, phase));

        mNoteText = (EditText)
                v.findViewById(R.id.qsort_dialog_note_note);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle(R.string.qsort_note_dialog_title);
        alertDialogBuilder.setView(v).setPositiveButton(
                R.string.qsort_note_dialog_ok,null)
                .setNegativeButton(R.string.qsort_note_dialog_cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        ;
        alertDialogBuilder.create();
        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        
        //Override onClickListener. Because, with the origin Listener, 
        // the dialog dismiss after click the button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {            
            @Override
            public void onClick(View v)
            {
                saveNote();
            }
        });
    }

    /**
     * Methode to make the default title of the note
     * 
     * @param context Context from the activity
     * @param phase Phase in which the QSort currently is
     * @return
     */
    private String makeTitle(Context context, Phase phase) { // TODO Missing
                                                             // time
        String noteTitleHint = "";
        switch (phase) {
            case QUESTIONS_PRE:
                noteTitleHint = context.getString(
                        R.string.qsort_note_dialog_title_presort);
                break;
            case Q_SORT:
                noteTitleHint = context.getString(
                        R.string.qsort_note_dialog_title_qsort);
                break;
            case QUESTIONS_POST:
                noteTitleHint = context.getString(
                        R.string.qsort_note_dialog_title_postsort);
                break;
            case INTERVIEW:
                noteTitleHint = context.getString(
                        R.string.qsort_note_dialog_title_interview);
                break;
            default:
                break;
        }
        return noteTitleHint;
    }

    /**
     * Methode to save the note in a QSort
     */
    private void saveNote() {
        if (mNoteText.getText().toString().isEmpty()) {
            Toast.makeText(mContext, R.string.qsort_note_dialog_error,
                    Toast.LENGTH_LONG).show();
        }
        else {
            Note note = new Note(CurrentQSort.getInstance().getStudyId());
            note.setPhase(mPhase);
            note.setQSortId(CurrentQSort.getInstance().getId());
            note.setId(-1);
            if (mNoteTitle.getText().toString().isEmpty())
                note.setTitle((mNoteTitle.getHint().toString()));
            else
                note.setTitle((mNoteTitle.getText().toString()));

            note.setText(mNoteText.getText().toString());
            
            note.setTime(CurrentQSort.getTimer().getTime());

            SaveNoteTask saveNoteTask = new SaveNoteTask((Callbacks) 
                    mContext, mContext, note);
            saveNoteTask.execute();
            alertDialog.dismiss();
        }
    }

    @Override
    public void onSaveNoteTaskUpdate(Note note) {
        
    }

}
