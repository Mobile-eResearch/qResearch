
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.gui.QSortParentActivity;
import de.eresearch.app.gui.QSortStartPhaseActivity;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.tasks.common.questions.GetQuestionsTask;
import de.eresearch.app.logic.tasks.common.questions.GetQuestionsTask.Callbacks;
import de.eresearch.app.logic.tasks.media.SaveAudioTask;

import java.util.List;

/**
 * Class for the dialog, to finish a phase in the qsort
 * 
 * @author domme
 */
public class QSortFinishPhaseDialog extends Dialog implements GetQuestionsTask.Callbacks,
        SaveAudioTask.Callbacks {

    static Context mContext;

    private List<Question> mQuestionList;

    private Intent mIntent = null;

    public QSortFinishPhaseDialog(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Methode to create the QSortFinishPhaseDialog
     * 
     * @param phase Current phase, which will be finish
     * @return Return the QSortFinishPhaseDialog
     */
    public AlertDialog qsortFinishPhaseDialog(final Phase phase) {
        GetQuestionsTask task = new GetQuestionsTask(mContext, (Callbacks) this,
                CurrentQSort.getInstance().getStudyId());
        task.execute();

        final Callbacks callback = (Callbacks) this;

        AlertDialog.Builder qsortFinishPhaseDialog = new AlertDialog.Builder(mContext);
        qsortFinishPhaseDialog.setTitle(R.string.qsortparentactivity_qsortphasefinishdialog_title);
        qsortFinishPhaseDialog.setPositiveButton(
                R.string.qsortparentactivity_qsortphasefinishdialog_button_ok,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIntent = new Intent(mContext, QSortStartPhaseActivity.class);
                        boolean check = false;
                        switch (phase) {
                            case QUESTIONS_PRE:
                                mIntent.putExtra(QSortStartPhaseActivity.PHASE, Phase.Q_SORT);
                                break;
                            case Q_SORT:
                                if (mQuestionList != null) {
                                    for (Question q : mQuestionList) {
                                        if (q.isPost()) {
                                            check = true;
                                            break;
                                        }
                                    }
                                }
                                if (check) {
                                    mIntent = new Intent(mContext,
                                            QSortStartPhaseActivity.class);
                                    mIntent.putExtra(QSortStartPhaseActivity.PHASE,
                                            Phase.QUESTIONS_POST);
                                }
                                else {
                                    mIntent = new Intent(mContext, QSortStartPhaseActivity.class);
                                    mIntent.putExtra(QSortStartPhaseActivity.PHASE, Phase.INTERVIEW);
                                }

                                break;
                            case QUESTIONS_POST:
                                mIntent = new Intent(mContext, QSortStartPhaseActivity.class);
                                mIntent.putExtra(QSortStartPhaseActivity.PHASE, Phase.INTERVIEW);
                                break;
                            default:
                                break;
                        }
                        if (CurrentQSort.getInstance().hasAudioRecord()) {
                            CurrentQSort.getAudioRecord().stopRecording();
                            SaveAudioTask saveAudioTask = new SaveAudioTask
                                    ((de.eresearch.app.logic.tasks.media.
                                            SaveAudioTask.Callbacks) callback, mContext,
                                            CurrentQSort.getAudioRecord());
                            saveAudioTask.execute();
                        }
                        else {
                            ((QSortParentActivity) mContext).startActivity(mIntent);
                            ((QSortParentActivity) mContext).finish();
                        }

                    }
                });
        qsortFinishPhaseDialog.setNegativeButton
                (R.string.qsortparentactivity_qsortphasefinishdialog_button_cancel, null);

        return qsortFinishPhaseDialog.create();
    }

    @Override
    public void onGetQuestionTaskUpdate(List<Question> questions) {
        this.mQuestionList = questions;

    }

    @Override
    public void onSaveAudioTaskUpdate(AudioRecord aR) {
        ((QSortParentActivity) mContext).startActivity(mIntent);
        ((QSortParentActivity) mContext).finish();
    }
}
