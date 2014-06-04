
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;

public class QSortPauseDialog extends Dialog {

    Context mContext;

    public QSortPauseDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Methode to create the QSortFinishDialog
     * 
     * @return Return the QSortFinishDialog
     */
    public AlertDialog qsortPauseDialog() {
        AlertDialog.Builder qsortPauseDialog = new AlertDialog.Builder(mContext);

        if (CurrentQSort.getInstance().hasAudioRecord())
            CurrentQSort.getAudioRecord().pauseRecording();
        CurrentQSort.getTimer().pause();

        qsortPauseDialog.setTitle(
                R.string.qsort_parent_activity_pause_alertdialog_title);
        qsortPauseDialog.setPositiveButton(
                R.string.qsort_parent_activity_pause_alertdialog_button
                , new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentQSort.getTimer().resume();
                        if (CurrentQSort.getInstance().hasAudioRecord())
                            CurrentQSort.getAudioRecord().resumeRecording();
                    }
                });
        return qsortPauseDialog.create();
    }

}
