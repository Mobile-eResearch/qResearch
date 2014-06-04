
package de.eresearch.app.gui.dialogs;


import de.eresearch.app.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.List;

import de.eresearch.app.gui.StudyEditActivity;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;

public class StudyFromTemplateDialog extends Dialog implements GetStudiesListTask.Callbacks{

    
     
    private ArrayAdapter<String> adapter;
    private List<Study> studies;
    private String[] names;
    private Context context;

    public StudyFromTemplateDialog(Context context) {
        super(context);
        this.context = context;
        GetStudiesListTask task = new GetStudiesListTask(this, context);
        task.execute();
    }

    @Override
    public void onGetStudiesListTaskUpdate(List<Study> studiesList) {
        this.studies = studiesList;
        int arrayLength = studies.size();
        this.names = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            this.names[i] = studies.get(i).getName();
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View) inflater.inflate(android.R.layout.select_dialog_item, null);
        alertDialog.setView(view);
        alertDialog.setTitle(R.string.study_from_template_dialog_title); 
        if (arrayLength == 0) {
            alertDialog.setMessage(R.string.study_from_template_no_template); 
        }
        else {
            adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, names);
            alertDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Study s =studies.get(id);
                    Intent intent = new Intent(context, StudyEditActivity.class);
                    intent.putExtra(StudyEditActivity.STUDY_NEW, false);
                    intent.putExtra(StudyEditActivity.STUDY_ID, s.getId());
                    intent.putExtra(StudyEditActivity.STUDY_TEMPLATE, true);
                    context.startActivity(intent); 
                }
            });
        }
        alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        alertDialog.show();
        
    }


}
