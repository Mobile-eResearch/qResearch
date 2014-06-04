
package de.eresearch.app.gui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesActivity;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;
import de.eresearch.app.logic.tasks.io.GetImportableStudiesTask;
import de.eresearch.app.logic.tasks.io.ImportStudyTask;

/**
 * @author thg
 */
@SuppressLint("DefaultLocale")
public class StudyImportDialog extends DialogFragment implements de.eresearch.app.logic.tasks.io.
        GetImportableStudiesTask.Callbacks,
        de.eresearch.app.logic.tasks.io.ImportStudyTask.Callbacks, GetStudiesListTask.Callbacks {

    private TextView mPath;
    private ListView mStudiesListView;
    private List<String> mStudies;
    private ArrayAdapter<String> mAdapter;
    private String mNewStudyName;
    private List<Study> mStudiesInAppList;
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
        // use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_study_import, null);

        // get the important views
        mPath = (TextView) v.findViewById(R.id.path_to_studies_textview);
        mPath.setText(getString(R.string.import_dir, ""));
        mStudiesListView = (ListView) v.findViewById(R.id.importable_studies_listview);

        // execute the GetImportableStudiesTask
        GetImportableStudiesTask t = new GetImportableStudiesTask(getActivity(), this);
        t.execute();

        GetStudiesListTask t2 = new GetStudiesListTask(this, getActivity());
        t2.execute();

        // initialize the ArrayAdapter
        mStudies = new ArrayList<String>();
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, mStudies);
        mStudiesListView.setAdapter(mAdapter);
        mStudiesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        builder.setTitle(getString(R.string.study_import_title));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setNeutralButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onOKClick();
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
    public void onStudyImported(int importedStudyId, Throwable thr) {
        
        Log.d("TEST","onStudyImported called in StudyImportDialog");
        mListener.onImportCallback();
        
        Toast.makeText(myContext,
                myContext.getString(R.string.study_import) + " " + mNewStudyName, Toast.LENGTH_SHORT)
                .show();
        
       
        
    }

    @Override
    public void onImportableStudiesGotten(List<String> importableStudies, Throwable thr) {
        mStudies.addAll(importableStudies);
        
        // if no studies to import, only show a toast message
        if (mStudies.size() <= 0) {
            Toast.makeText(myContext,
                    myContext.getString(R.string.no_study_to_import), Toast.LENGTH_SHORT)
                    .show();
            this.dismiss();
        }

        // update the ListView
        mAdapter.notifyDataSetChanged();

    }

    private void onOKClick() {
        String name = mStudies.get(mStudiesListView.getCheckedItemPosition());

        // if name already exists, ask for new one
        if (testNameExists(name)) {

            StudyImportNewNameDialog d = new StudyImportNewNameDialog();
            d.setOldStudyName(name);
            d.setOldStudies(mStudiesInAppList);
            d.setContext(myContext);
            d.show(getFragmentManager(), "StudyImportNewName");
            
            return;

        } else {

            mNewStudyName = name;

            Log.d("TEST", "run Import Task");
            // else import with old name
            ImportStudyTask t = new ImportStudyTask(getActivity(), this, name, mNewStudyName);
            t.execute();
        }
    }

    @Override
    public void onGetStudiesListTaskUpdate(List<Study> studiesList) {
        this.mStudiesInAppList = studiesList;
    }

    /**
     * Test if study name already exists
     * 
     * @param name a name
     * @return true if study already exists, false if not
     */
    private boolean testNameExists(String name) {

        for (Study s : mStudiesInAppList) {
            if (s.getName().toLowerCase().equals(name.toLowerCase()))
                return true;
        }

        return false;
    }
    
    public void setContext(StudiesActivity myContext) {
        this.myContext = myContext;
    }
}
