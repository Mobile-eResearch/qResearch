package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesActivity;

/**
 * Class for the exit-dialog of the application
 * 
 * @author domme
 *
 */
public class AppExitDialog  {

    /**
     * Make exit-dialog of the application
     * @param context
     * @return
     */
    public static AlertDialog exitDialog(final Context context){
        
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(context);
        exitDialog.setTitle(R.string.studies_activity_exitdialog_title);
        exitDialog.setPositiveButton(
                R.string.studies_activity_exitdialog_button_quit, 
                new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((StudiesActivity)(context)).finish();
            }
        });
        exitDialog.setNegativeButton
            (R.string.studies_activity_exitdialog_button_back, null);
        
        return exitDialog.create();
    }
}
