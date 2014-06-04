
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.gui.QSortParentActivity;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask.Callbacks;
import de.eresearch.app.logic.tasks.media.SaveAudioTask;

/**
 * Class for the QSort-Finish-Dialog
 * 
 * @author domme
 */
public class QSortFinishDialog extends Dialog implements SaveAudioTask.Callbacks,
        SaveQSortTask.Callbacks {

    Context mContext;

    public QSortFinishDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Methode to create the QSortFinishDialog
     * 
     * @return Return the QSortFinishDialog
     */
    public AlertDialog qsortFinishDialog() {
        final Callbacks callback = (Callbacks) this;
        
        AlertDialog.Builder qsortFinishDialog = new AlertDialog.Builder(mContext);
        qsortFinishDialog.setTitle(R.string.qsortparentactivity_qsortfinishdialog_title);
        qsortFinishDialog.setPositiveButton(
                R.string.qsortparentactivity_qsortfinishdialog_button_ok,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("QSortFinishDialog", "name=" + CurrentQSort.getInstance().getName());
                        CurrentQSort.getInstance().setFinished(true);
                        CurrentQSort.getInstance().setEndTime(System.currentTimeMillis());
                        SaveQSortTask saveQSortTask = new SaveQSortTask(mContext,
                                callback, CurrentQSort.getInstance());
                        saveQSortTask.execute();

                    }
                });
        qsortFinishDialog.setNegativeButton
                (R.string.qsortparentactivity_qsortfinishdialog_button_cancel, null);

        return qsortFinishDialog.create();
    }

    @Override
    public void onSaveAudioTaskUpdate(AudioRecord aR) {
        CurrentQSort.resetInstance();
        CurrentQSort.resetAudioRecord();
        CurrentQSort.resetTimer();
        ((QSortParentActivity) mContext).finish();
    }

    @Override
    public void onSaveQSortTask(int id) {
        if(CurrentQSort.getInstance().hasAudioRecord()){
        CurrentQSort.getAudioRecord().stopRecording();
        SaveAudioTask saveAudioTask = new SaveAudioTask
                ((de.eresearch.app.logic.tasks.media.
                        SaveAudioTask.Callbacks) this, mContext,
                        CurrentQSort.getAudioRecord());
        saveAudioTask.execute();}
        else{
            CurrentQSort.resetInstance();
            CurrentQSort.resetAudioRecord();
            CurrentQSort.resetTimer();
            ((QSortParentActivity) mContext).finish();
        }
    }
}
