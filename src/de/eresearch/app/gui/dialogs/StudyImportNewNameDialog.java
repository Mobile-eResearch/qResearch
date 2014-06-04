
package de.eresearch.app.gui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesActivity;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.io.ImportStudyTask;

/**
 * @author thg
 */
@SuppressLint("DefaultLocale")
public class StudyImportNewNameDialog extends DialogFragment implements
        de.eresearch.app.logic.tasks.io.ImportStudyTask.Callbacks {

    private String mNewStudyName;
    private String mOldStudyName;
    private List<Study> mOldStudies;
    private EditText mNewStudyNameEditText;
    private StudiesActivity myContext;
    
    Callbacks mListener;
    
    public interface Callbacks {
        public void onImportCallback();
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
        
        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_study_import_new_name, null);
        mNewStudyNameEditText = (EditText) v.findViewById(R.id.editText_new_name);
        
        builder.setTitle(getString(R.string.study_import_name_exists_title));
        
        builder.setMessage(getString(R.string.study_import_name_exists_message));
        builder.setView(v)         
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onOKClick();
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

    private void onOKClick() {
        
        mNewStudyName = mNewStudyNameEditText.getText().toString();

        if (!testNameExists(mNewStudyName)) {
            Toast.makeText(myContext,
                    getString(R.string.study_import) + " " + mNewStudyName, Toast.LENGTH_SHORT)
                    .show();
        }
        else{
            Toast.makeText(myContext,
                    getString(R.string.study_import_name_exists_error), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ImportStudyTask t = new ImportStudyTask(getActivity(),this, mOldStudyName, mNewStudyName);
        t.execute();
    }

    @Override
    public void onStudyImported(int importedStudyId, Throwable thr) {
        mListener.onImportCallback();
    }
    
    
    /**
     * Test if study name already exists
     * 
     * @param name a name
     * @return true if study already exists, false if not
     */
    private boolean testNameExists(String name){
        
        for (Study s: mOldStudies){
            if (s.getName().toLowerCase().equals(name.toLowerCase()))
                return true;
        }
        
        return false;
    }

    public String getOldStudyName() {
        return mOldStudyName;
    }

    public void setOldStudyName(String mOldStudyName) {
        this.mOldStudyName = mOldStudyName;
    }

    public String getNewStudyName() {
        return mNewStudyName;
    }

    public void setNewStudyName(String mNewStudyName) {
        this.mNewStudyName = mNewStudyName;
    }

    public List<Study> getOldStudies() {
        return mOldStudies;
    }

    public void setOldStudies(List<Study> mOldStudies) {
        this.mOldStudies = mOldStudies;
    }
    
    public void setContext(StudiesActivity myContext) {
        this.myContext = myContext;
    }
}
