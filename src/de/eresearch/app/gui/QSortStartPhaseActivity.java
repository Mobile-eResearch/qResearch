
package de.eresearch.app.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.tasks.io.PrepareAudioFileTask;

public class QSortStartPhaseActivity extends QSortParentActivity implements PrepareAudioFileTask.Callbacks{

    public static final String PHASE =
            "de.eresearch.app.gui.QSortStartPhaseActivity.PHASE";
    
    private Intent mIntent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsort_start_phase);
        
        CurrentQSort.resetAudioRecord();
        CurrentQSort.resetTimer();
        
        CurrentQSort.newTimer();
        CurrentQSort.getTimer().start();
        
        mPhase = (Phase) mExtras.getSerializable(PHASE);
        
        // set title of the activity
        setTitle(mPhase.toString(this));
        
        if (CurrentQSort.getInstance().hasAudioRecord())
            new PrepareAudioFileTask(this, this, CurrentQSort.getInstance(), mPhase).execute();
        
        Button start_button = (Button) findViewById(R.id.qsort_start_phase_button_start);

        start_button.setText(R.string.qsort_start_phase_start_button);

        start_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (mPhase) {
                    case QUESTIONS_PRE:
                        mIntent = new Intent(getApplicationContext(), QSortQuestionActivity.class);
                        mIntent.putExtra(QSortQuestionActivity.IS_POST, false);
                        break;
                    case Q_SORT:
                        mIntent = new Intent(getApplicationContext(), QSortActivity.class);
                        break;
                    case QUESTIONS_POST:
                        mIntent = new Intent(getApplicationContext(), QSortQuestionActivity.class);
                        mIntent.putExtra(QSortQuestionActivity.IS_POST, true);
                        break;
                    case INTERVIEW:
                        mIntent = new Intent(getApplicationContext(), InterviewActivity.class);
                        break;
                    default:
                        break;
                }
                    startActivity(mIntent);
                    finish();
            }
        });
    }

    @Override
    public void onAudioPrepared(AudioRecord record) {
        CurrentQSort.setAudioRecord(record);
        CurrentQSort.getAudioRecord().startRecording();
    }

}
