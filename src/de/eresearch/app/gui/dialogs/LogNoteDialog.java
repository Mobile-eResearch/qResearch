
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import de.eresearch.app.R;
import de.eresearch.app.gui.LogParentActivity;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.tasks.media.SaveNoteTask;
import de.eresearch.app.logic.tasks.media.SaveNoteTask.Callbacks;
import de.eresearch.app.util.InputFilterMinMax;

/**
* Class for the note-dialog in the log
*
* @author domme
*
*/
public class LogNoteDialog extends Dialog {

    private Context mContext;

    private EditText mTitleEditText;

    private EditText mNoteEditText;

    private EditText mTimeMinutesEditText;

    private int mMaxMinutes;

    private int mCurrentMinutes;

    private EditText mTimeSecondsEditText;

    private int mMaxSeconds;

    private int mCurrentSeconds;

    private ImageButton mPauseImageButton;

    private AlertDialog mAlertDialog;

    private Phase mPhase;

    private int mQSortID;

    private boolean mNewNote;

    private Note mNote;

    public LogNoteDialog(Context context, Phase phase, int qSortID, boolean newNote, Note note,
            int maxMinutes, int currentMinutes, int maxSeconds, int currentSeconds) {
        super(context);

        mPhase = phase;

        mQSortID = qSortID;

        mNewNote = newNote;

        mContext = context;

        mNote = note;

        mMaxMinutes = maxMinutes;

        mMaxSeconds = maxSeconds;

        mCurrentMinutes = currentMinutes;

        mCurrentSeconds = currentSeconds;
    }

    public Dialog makeLogNoteDialog() {

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_log_note, null);

        mTitleEditText = (EditText)
                v.findViewById(R.id.log_dialog_note_title);

        mNoteEditText = (EditText)
                v.findViewById(R.id.log_dialog_note_note);

        mTimeMinutesEditText = (EditText)
                v.findViewById(R.id.log_dialog_note_time_minutes);

        mTimeMinutesEditText.setOnKeyListener(mOnKeyListener);

        mTimeMinutesEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, mMaxMinutes)
        });

        mTimeSecondsEditText = (EditText)
                v.findViewById(R.id.log_dialog_note_time_seconds);

        mTimeSecondsEditText.setOnKeyListener(mOnKeyListener);

        mPauseImageButton = (ImageButton)
                v.findViewById(R.id.log_dialog_note_pause);

        if (!((LogParentActivity) mContext).getHasAudio()) {
            mPauseImageButton.setVisibility(View.INVISIBLE);
            mPauseImageButton.setLayoutParams(new LayoutParams(0, 0));
        }

        ((LogParentActivity) mContext).setPauseButtonDialog(mPauseImageButton);

        if (((LogParentActivity) mContext).getPlayPause())
            mPauseImageButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        mPauseImageButton.setOnClickListener(((LogParentActivity) mContext).
                getOnClickPausePlayListener());

        if (!mNewNote) {
            mTitleEditText.setText(mNote.getTitle());
            mNoteEditText.setText(mNote.getText());
            mTimeMinutesEditText.setText(mCurrentMinutes+"");
            mTimeSecondsEditText.setText(mCurrentSeconds+"");
        } else {
            mTimeMinutesEditText.setHint(mCurrentMinutes+"");
            mTimeSecondsEditText.setHint(mCurrentSeconds+"");
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setTitle(R.string.qsort_note_dialog_title);
        alertDialogBuilder.setView(v).setPositiveButton(
                R.string.qsort_note_dialog_ok,new OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNote();
                        
                    }
                });
        alertDialogBuilder.setView(v).setNegativeButton(R.string.qsort_note_dialog_cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        mAlertDialog = alertDialogBuilder.create();
        
        return mAlertDialog;
    }

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if(!mTimeMinutesEditText.getText().toString().isEmpty()){
            if (Integer.parseInt(mTimeMinutesEditText.getText().toString())== mMaxMinutes)
                mTimeSecondsEditText.setFilters(new InputFilter[] {
                        new InputFilterMinMax(0, mMaxSeconds)
                });
            else
                mTimeSecondsEditText.setFilters(new InputFilter[] {
                        new InputFilterMinMax(0, 60)
                });
            }
            return false;
        }
    };

    /**
     * Methode to save the note in a QSort
     */
    private void saveNote() {
        if (mNoteEditText.getText().toString().isEmpty()) {
            Toast.makeText(mContext, R.string.qsort_note_dialog_error,
                    Toast.LENGTH_LONG).show();
        }
        else {

            if (mNewNote) {
                Note note = new Note(-1);
                note.setPhase(mPhase);
                note.setQSortId(mQSortID);

                if (mTitleEditText.getText().toString().isEmpty())
                    note.setTitle((mTitleEditText.getHint().toString()));
                else
                    note.setTitle((mTitleEditText.getText().toString()));

                note.setText(mNoteEditText.getText().toString());

                long minutes = 0;
                long seconds = 0;

                if (!mTimeMinutesEditText.getText().toString().isEmpty())
                    minutes = Integer.parseInt(mTimeMinutesEditText.getText().toString());
                else if (!mTimeMinutesEditText.getHint().toString().isEmpty())
                    minutes = Integer.parseInt(mTimeMinutesEditText.getHint().toString());
                
                if (!mTimeSecondsEditText.getText().toString().isEmpty())
                    seconds = Integer.parseInt(mTimeSecondsEditText.getText().toString());
                else if (!mTimeSecondsEditText.getHint().toString().isEmpty())
                    seconds = Integer.parseInt(mTimeSecondsEditText.getHint().toString());

                note.setTime(minutes*60*1000+seconds*1000 );
                SaveNoteTask saveNoteTask = new SaveNoteTask((Callbacks) mContext, mContext, note);
                saveNoteTask.execute();
            }
            else {
                if (!mTitleEditText.getText().toString().equals(mNote.getTitle()) ||
                        !mNoteEditText.getText().toString().equals(mNote.getText())) {
                    mNote.setTitle(mTitleEditText.getText().toString());
                    mNote.setText(mNoteEditText.getText().toString());
                    SaveNoteTask saveNoteTask = new SaveNoteTask((Callbacks) mContext, mContext,
                            mNote);
                    saveNoteTask.execute();

                }
            }
            mAlertDialog.dismiss();
            ((LogParentActivity) mContext).setPauseButtonDialog(null);
        }
    }
}
