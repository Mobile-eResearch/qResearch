package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import de.eresearch.app.R;

public class StudyMergeDialog extends DialogFragment   {

    private EditText mTitle;
    private String title;
    StudyMergeDialogListener mListener;
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =  getActivity().getLayoutInflater();
        View view = (View) inflater.inflate(R.layout.dialog_study_merge, null);
        builder.setView(view);
        builder.setTitle("Studie zusammenführen"); //TODO strings
        mTitle = (EditText) view.findViewById(R.id.meta_name);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
             
            }
            
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
        builder.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                title = mTitle.getText().toString(); 
                mListener.onStudyMergeDialogPositiveClick(StudyMergeDialog.this);
            }
        });
        return builder.create();
    }
    
    public String getTitle(){
        return this.title;
    }
      
    
    
    public interface StudyMergeDialogListener {
        public void onStudyMergeDialogPositiveClick(StudyMergeDialog studyMergeDialog);
    }
    
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (StudyMergeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement StudyMergeDialogListener");
        }
    }
    
}
