package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask.Callbacks;

/**
 * Class for the QSort-Cancel-Dialog
 * 
 * @author domme
 *
 */
public class QSortCancelDialog extends Dialog implements SaveQSortTask.Callbacks{
    
    Context mContext;
    
    public QSortCancelDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Methode to create the QSortCancelPhaseDialog
     * 
     * @return Return the QSortCancelPhaseDialog
     */
    public AlertDialog qsortCancelDialog(){
        final Callbacks callback = (Callbacks) this;
        
        AlertDialog.Builder qsortCancelDialog = new AlertDialog.Builder(mContext);
        qsortCancelDialog.setTitle(R.string.qsortparentactivity_qsortcanceldialog_title);
        qsortCancelDialog.setPositiveButton(
                R.string.qsortparentactivity_qsortcanceldialog_button_ok, 
                new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CurrentQSort.getInstance().setEndTime(System.currentTimeMillis());
                SaveQSortTask saveQSortTask = new SaveQSortTask(mContext, callback, CurrentQSort.getInstance());
                saveQSortTask.execute();
                
            }
        });
        qsortCancelDialog.setNegativeButton
            (R.string.qsortparentactivity_qsortcanceldialog_button_cancel, null);
        
        return qsortCancelDialog.create();
    }

    @Override
    public void onSaveQSortTask(int id) {
        CurrentQSort.resetInstance();
        CurrentQSort.resetAudioRecord();
        CurrentQSort.resetTimer();
        ((Activity) mContext).finish();
    }
    
}
