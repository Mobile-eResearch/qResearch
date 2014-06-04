
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.widget.Toast;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesActivity;

import de.eresearch.app.logic.tasks.io.ExportStudyTask;

/**
 * @author thg
 */
public class StudyExportDialog extends DialogFragment implements
        de.eresearch.app.logic.tasks.io.ExportStudyTask.Callbacks {

    private int mStudyID;
    private String mStudyName;

    private StudiesActivity myContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // setup export task
        final ExportStudyTask t = new ExportStudyTask(getActivity(), this, mStudyID);

        builder.setTitle("Export" + ": " + mStudyName);
        builder.setMessage(getString(R.string.study_export_message))
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // execute the Task
                        t.execute();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStudyExported(Throwable thr) {

        // TODO: if Exception -> then Toast

        String path = myContext.getString(R.string.export_dir, mStudyName);

        // if path string empty (maybe not nes
        String message;

        message = mStudyName + " " + myContext.getString(R.string.study_exported_to) + " "
                + path;

        Toast.makeText(myContext,
                message, Toast.LENGTH_SHORT).show();
    }

    // --- GETTER AND SETTER ------------------------------
    public int getStudyID() {
        return mStudyID;
    }

    public void setStudyID(int mStudyID) {
        this.mStudyID = mStudyID;
    }

    public String getStudyName() {
        return mStudyName;
    }

    public void setStudyName(String mStudyName) {
        this.mStudyName = mStudyName;
    }

    public void setContext(StudiesActivity myContext) {
        this.myContext = myContext;
    }

}
