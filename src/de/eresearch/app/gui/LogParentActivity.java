
package de.eresearch.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.LogListArrayAdapter;
import de.eresearch.app.gui.dialogs.LogNoteDialog;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Loggable;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.tasks.common.qsort.GetLoggablesTask;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortTask;
import de.eresearch.app.logic.tasks.media.SaveNoteTask;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LogParentActivity extends Activity implements GetLoggablesTask.Callbacks,
        GetQSortTask.Callbacks, SaveNoteTask.Callbacks {

    /**
     * Data descriptor for the qsort ID.
     */
    public static final String QSORT_ID =
            "de.eresearch.app.gui.LogParentActivity.QSORT_ID";

    /**
     * Data descriptor for the phase.
     */
    public static final String PHASE =
            "de.eresearch.app.gui.LogParentActivity.PHASE";

    /**
     * Data descriptor for a flag attribut, that the QSort have a audiorecord
     */
    public static final String AUDIORECORD =
            "de.eresearch.app.gui.LogParentActivity.AUDIORECORD";

    private Bundle mExtras;

    private int mQSortID;

    private Phase mPhase;

    private ListView mListView;

    private SeekBar mSeekBar;

    private ImageButton mPauseButton;

    private ImageButton mPauseButtonDialog;

    private TextView mTimeView;

    private LogListArrayAdapter mAdapter;

    private List<Loggable> mLogEntries;

    private AudioRecord mAudioRecord;

    private boolean mPlay;

    private boolean mHasAudioRecord;

    private long mEndTime;

    private QSort mQSort;

    private boolean mEndOfPlay;

    public static int mHighlightedItem;

    private int mTmpPlayTime;

    public void setPauseButtonDialog(ImageButton pauseButtonDialog) {
        mPauseButtonDialog = pauseButtonDialog;
    }

    public OnClickListener getOnClickPausePlayListener() {
        return mOnClickPausePlayListener;
    }

    public boolean getHasAudio() {
        return mQSort.hasAudioRecord();
    }

    public boolean getPlayPause() {
        return mPlay;
    }

    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mExtras = getIntent().getExtras();
        mQSortID = mExtras.getInt(QSORT_ID);
        mPhase = (Phase) mExtras.getSerializable(PHASE);
        
        setTitle(mPhase.toString(this));

        mHasAudioRecord = mExtras.getBoolean(AUDIORECORD);

        mHighlightedItem = -1;

        mListView = (ListView) findViewById(R.id.log_list);

        mSeekBar = (SeekBar) findViewById(R.id.log_bar);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mTimeView = (TextView) findViewById(R.id.log_chronometer);

        mPauseButton = (ImageButton) findViewById(R.id.log_pause_play_button);
        mPauseButton.setOnClickListener(mOnClickPausePlayListener);

        if (!mHasAudioRecord) {
            mSeekBar.setVisibility(View.INVISIBLE);
            mPauseButton.setVisibility(View.INVISIBLE);
            mTimeView.setVisibility(View.INVISIBLE);
        }

        GetLoggablesTask getLoggablesTask = new GetLoggablesTask(this, this, mQSortID, mPhase);
        getLoggablesTask.execute();

        GetQSortTask getQSortTask = new GetQSortTask(this, mQSortID, this);
        getQSortTask.execute();
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (mAudioRecord != null && mPlay) { // &&mPlay
                refreshViews();
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.log_notice:
                if (mQSort.hasAudioRecord()) {
                    int currentPosition = mAudioRecord.getCurrentPosition();
                    LogNoteDialog logNoteDialog = new LogNoteDialog(
                            this,
                            mPhase,
                            mQSortID,
                            true,
                            null,
                            (int) TimeUnit.MILLISECONDS.toMinutes(mEndTime),
                            (int) TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                            (int) (TimeUnit.MILLISECONDS.toSeconds(mEndTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mEndTime))),
                            (int) (TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                    .toMinutes(currentPosition)))
                            );
                    logNoteDialog.makeLogNoteDialog().show();
                }
                else {
                    LogNoteDialog logNoteDialog = new LogNoteDialog(
                            this,
                            mPhase,
                            mQSortID,
                            true,
                            null,
                            (int) TimeUnit.MILLISECONDS.toMinutes(mEndTime),
                            0,
                            (int) (TimeUnit.MILLISECONDS.toSeconds(mEndTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mEndTime))),
                            0
                            );

                    logNoteDialog.makeLogNoteDialog().show();
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * OnClickListener for play/pause-button
     */
    private OnClickListener mOnClickPausePlayListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mPlay) {
                playAudio(true);
            }
            else {
                playAudio(false);
            }
        }
    };

    /**
     * OnSeekBarChangeListener for the seekbar to change the progress of audio
     * replay
     */
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mAudioRecord != null && fromUser) {
                if(mPlay)
                playAudio(false);
                mHighlightedItem = -1;
                mTmpPlayTime = progress;
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            setPlayTime(mTmpPlayTime);
        }
    };

    /**
     * Set the time of the audio replay and refresh
     * 
     * @param time
     */
    public void setPlayTime(long time) {
        playAudio(true);
        playAudio(false);
        mAudioRecord.sysOut("setPlayTime("+time+")");
        mAudioRecord.seekTo((int) time);

        refreshViews();
    }

    public void refreshViews() {

        if (!mEndOfPlay) {
            int currentPosition = mAudioRecord.getCurrentPosition();

            mSeekBar.setProgress(currentPosition);
            calculateHighlightedItem();
            mTimeView.setText(formatTime(currentPosition) + " / "
                    + formatTime(mEndTime));
        }
        else {
            mTimeView.setText(formatTime(0) + " / " + formatTime(mEndTime));
            mSeekBar.setProgress(0);

            mPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
            if (mPauseButtonDialog != null)
                mPauseButtonDialog.setBackgroundResource(android.R.drawable.ic_media_play);

            mPlay = false;

            mHighlightedItem = -1;

            calculateHighlightedItem();
        }
    }

    private String formatTime(long time) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
                );
    }

    public void playAudio(boolean play) {
        mPlay = play;
        if (play){
            mAudioRecord.playAudio(true, false);
            mEndOfPlay= false;
            mPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            if (mPauseButtonDialog != null)
                mPauseButtonDialog.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
        else{
            mAudioRecord.pauseAudio();
            mPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
            if (mPauseButtonDialog != null)
                mPauseButtonDialog.setBackgroundResource(android.R.drawable.ic_media_play);
            }
        /*
         * mPlay = play; if (mPlay) { if (mFirstPlay && mMediaPlayer != null &&
         * mAudioRecord != null) {
         * System.out.println("TRACK LogParentActivity - startPlaying(), seekTo("
         * +mSeekBar.getProgress()+")"); if(mSeekBar.getProgress()!= 0)
         * mMediaPlayer.seekTo(mSeekBar.getProgress());
         * mAudioRecord.startPlaying(); mFirstPlay = false; } else
         * mAudioRecord.resumePlaying(); } else mAudioRecord.pausePlaying();
         */
    }

    public void calculateHighlightedItem() {
        if (mLogEntries != null && mLogEntries.size() > 0) {
            int oldHightlightedItem = mHighlightedItem;
            for (int i = mHighlightedItem + 1; i < mLogEntries.size(); i++) {
                if (mLogEntries.get(i).getTime() <= mAudioRecord.getCurrentPosition())
                    mHighlightedItem = i;
                else
                    break;
            }

            if (oldHightlightedItem != mHighlightedItem || mHighlightedItem == -1)
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoggablesGotten(List<Loggable> loggables) {
        mLogEntries = loggables;
        mAdapter = new LogListArrayAdapter(this, mEndTime, R.layout.activity_log_list_row,
                mLogEntries);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onGetQSortTaskUpdate(QSort qsort) {
        mQSortID = qsort.getId();
        mQSort = qsort;
        if (qsort.hasAudioRecord() && qsort.getLog(mPhase).getAudio() != null) {
            mAudioRecord = qsort.getLog(mPhase).getAudio();
            mAudioRecord.setLogActivity(this);

            mAudioRecord.playAudio(true, false);

            mEndTime = mAudioRecord.getDuration();
            mSeekBar.setMax((int) mEndTime);

            mRunnable.run();
            refreshViews();
        }
    }

    @Override
    public void onSaveNoteTaskUpdate(Note note) {
        GetLoggablesTask getLoggablesTask = new GetLoggablesTask(this, this, mQSortID, mPhase);
        getLoggablesTask.execute();
    }

    /**
     * @return the mEndOfPlay
     */
    public boolean getEndOfPlay() {
        return mEndOfPlay;
    }

    /**
     * @param mEndOfPlay the mEndOfPlay to set
     */
    public void setEndOfPlay(boolean mEndOfPlay) {
        this.mEndOfPlay = mEndOfPlay;
    }

    public void setPlay(boolean play){
        mPlay = play;
    }
    
    public SeekBar getSeekBar(){
        return mSeekBar;
    }
    @Override
    public void finish() {
        if (mAudioRecord != null) {
            mAudioRecord.releasePlayer();
            mAudioRecord = null;
        }
        super.finish();
    }

}
