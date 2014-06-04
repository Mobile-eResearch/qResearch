
package de.eresearch.app.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.gui.QSortStartPhaseActivity;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask;
import de.eresearch.app.logic.tasks.common.qsort.SaveQSortTask.Callbacks;
import de.eresearch.app.logic.tasks.common.questions.GetQuestionsTask;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Class for the start-dialog, to start a QSort
 * 
 * @author domme
 *
 */
public class QSortStartDialog extends Dialog implements GetQuestionsTask.Callbacks,
        SaveQSortTask.Callbacks {

    public QSortStartDialog(Context context, int studyID) {
        super(context);
        mContext = context;
        mStudyID = studyID;
    }

    private List<Question> mQuestionList;
    private SharedPreferences sharedPreferences;
    private final static String chargercode = "code";
    private String mQSortName;
    private CheckBox mCheckboxAudioRecord;
    private EditText mEdittextIntervieweeId;
    private Context mContext;
    private int mStudyID;

    public AlertDialog qsortFinishPhaseDialog() {
        AlertDialog.Builder qsortStartDialog =
                new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_qsort_start, null);

        
        CurrentQSort.newInstance(mStudyID);

        final Callbacks callbacks = (Callbacks) this;

        GetQuestionsTask task = new GetQuestionsTask(mContext.getApplicationContext(), this,
                mStudyID);
        task.execute();

        mCheckboxAudioRecord = (CheckBox) v
                .findViewById(R.id.dialog_qsort_start_checkbox_record);
        mEdittextIntervieweeId = (EditText) v
                .findViewById(R.id.dialog_qsort_start_edittext_interviewee_identifier);

        // make default name for the qsort
        makeQSortName();

        mEdittextIntervieweeId.setHint(mQSortName);

        qsortStartDialog.setTitle(R.string.studies_activity_qsortstartdialog_title);

        qsortStartDialog.setPositiveButton(R.string.studies_activity_qsortstartdialog_button_start,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // if the EditText-field for the interviewee_id is
                        // empty, we set
                        // the hint of the EditText-field as interviewee-ID
                        if (mEdittextIntervieweeId.getText().toString().isEmpty())
                            CurrentQSort.getInstance().setName(mQSortName);
                        else
                            CurrentQSort.getInstance().setName(
                                    mEdittextIntervieweeId.getText().toString());

                        System.out.println(mCheckboxAudioRecord.isChecked());
                        CurrentQSort.getInstance().setAudioRecord(mCheckboxAudioRecord.isChecked());

                        CurrentQSort.getInstance().setStudyId(mStudyID);
                        CurrentQSort.getInstance().
                                setAcronym(sharedPreferences.getString(chargercode, ""));
                        
                        if(CurrentQSort.getInstance().getAcronym() == ""){
                            String name = AppSettingDialog.getCodeOfDeviceOwner(getContext());
                            CurrentQSort.getInstance().setAcronym(name);
                        }
                        CurrentQSort.getInstance().setStartTime(System.currentTimeMillis());

                        SaveQSortTask saveQSortTask = new SaveQSortTask(
                                mContext, callbacks,
                                CurrentQSort.getInstance());
                        saveQSortTask.execute();

                    }
                });
        qsortStartDialog.setNegativeButton(
                R.string.studies_activity_qsortstartdialog_button_cancel, null);
        qsortStartDialog.setView(v);

        return qsortStartDialog.create();
    }

    @Override
    public void onGetQuestionTaskUpdate(List<Question> questions) {
        this.mQuestionList = questions;

    }

    private void makeQSortName() {
        sharedPreferences = mContext.getSharedPreferences(chargercode,
                Context.MODE_PRIVATE);
        mQSortName = sharedPreferences.getString(chargercode, "");
        if (mQSortName.isEmpty() || mQSortName.equals(""))
        mQSortName = AppSettingDialog.getCodeOfDeviceOwner(mContext)+ " ";
        else
            mQSortName += " ";
        mQSortName += (new Timestamp(new Date().getTime())).toString();

        mQSortName = (String) mQSortName.subSequence(0, (mQSortName.length() - 4));
    }

    @Override
    public void onSaveQSortTask(int id) {
        CurrentQSort.getInstance().setId(id);

        Phase nextPhase;

        Intent intent = new Intent(mContext, QSortStartPhaseActivity.class);
        boolean check = false;
        if (mQuestionList != null) {

            for (Question q : mQuestionList) {
                if (!q.isPost()) {
                    check = true;
                    break;
                }
            }
        }
        if (check)
            nextPhase = Phase.QUESTIONS_PRE;
        else
            nextPhase = Phase.Q_SORT;

        intent.putExtra(QSortStartPhaseActivity.PHASE, nextPhase);

        ((Activity) mContext).startActivityForResult(intent, 100);

    }
}
