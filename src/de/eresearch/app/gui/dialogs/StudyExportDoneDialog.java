package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import de.eresearch.app.R;

/**
 * 
 * @author thg
 *
 */
public class StudyExportDoneDialog extends DialogFragment{

    private String mStudyName, mStudyExportPath;
    
    public void setStudyName(String s){
        mStudyName = s;
    }
    
    public void setStudyPath(String s){
        mStudyExportPath = s;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mStudyName + " erfolgreich exportiert");
        builder.setMessage(mStudyExportPath)
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // TODO: close dialog
                   }
               });   
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
