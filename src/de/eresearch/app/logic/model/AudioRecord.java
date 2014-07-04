
package de.eresearch.app.logic.model;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;

import de.eresearch.app.gui.LogParentActivity;

import java.io.IOException;

/**
 * @author Tammo -- AudioRecord Objects provide a prepared {@link MediaRecorder}
 *
 * @author domme -- The playing-part of the AudioRecord{@link MediaPlayer}
 */
public class AudioRecord {

    /** the AudioRecord's id */
    private int mID = -1;

    /** Id of the QSort, to which the Record is attached */
    private int mQSortID = -1;

    /** Enum Phase for AudioRecord */
    private Phase mPhase = null;

    /**
     * Path, where the media file is saved. The name of the file must be part of
     * the path (without .3gp)!
     */
    private String mPath = "";

    /** Boolean showing if the recorder is active */
    private boolean mIsRecording = false;

    /** Boolean showing if the player is active */
    private boolean mIsPlaying = false;

    /** Boolean showing if the recorder is paused */
    private boolean mRecPaused = false;

    /** Boolean showing if the player is paused */
    private boolean mPlayPaused = false;

    /** {@link MediaRecorder} of an instance */
    private MediaRecorder mMediaRecorder = null;

    /** {@link MediaPlayer} of an instance */
    private CustomMediaPlayer mMediaPlayer = null;

    /** Number of total audio files, recorded by this instance */
    private int mPartNumber = 1;

    /** Number of tracks already played */
    private int mPlayedTracks = 1;
    
    private boolean firstPlay = true;

    private LogParentActivity mLogActivity;

    /**
     * Creates a new audio record object with the given id. It is important to
     * set the file path by using {@link AudioRecord#setFilePath(String)}! FOR
     * DATABASE: When reconstructing this object, remember to set the number of
     * parts!
     * 
     * @param id The id of this audio record
     */
    public AudioRecord(int id) {
        mID = id;
        mMediaPlayer = new CustomMediaPlayer(this, mPartNumber);
    }

    /**
     * @param id The id of this audio record
     */
    public void setId(int id) {
        mID = id;
    }

    /**
     * @return The id of this audio record
     */
    public int getId() {
        return mID;
    }

    /**
     * @return Id of the QSort, the record is attached to
     */
    public int getQSortId() {
        return mQSortID;
    }

    /**
     * @param id of the QSort, the record is attached to
     */
    public void setQSortId(int id) {
        mQSortID = id;
    }

    /**
     * @return the Phase of this AudioRecord
     */
    public Phase getPhase() {
        return this.mPhase;
    }

    /**
     * @param phase of this AudioRecord
     */
    public void setPhase(Phase phase) {
        this.mPhase = phase;
    }

    /**
     * @param path The absolute path of the corresponding audio file WITH the
     *            file name but without .3gp!
     */
    public void setFilePath(String path) {
        // if (path.length() > 5 && path.substring(path.length() - 4,
        // path.length()).equals(".3gp")) {
        // mPath = (path.substring(0, path.length() - 4)) + "_1";
        // } else {
        mPath = path + "_1";
        // }
    }

    /**
     * @return The absolute path of the corresponding audio file WITH the file
     *         name!
     */
    public String getFilePath() {
        return mPath.substring(0, mPath.length() - 2);
    }

    /**
     * @return The absolute path of the corresponding audio file WITHOUT the
     *         file name!
     */
    public String getOriginFilePath() {
        return mPath;
    }

    /**
     * @param i Number of parts of this {@link AudioRecord}
     */
    public void setPartNumber(int i) {
        mPartNumber = i;
    }

    /**
     * @return Number of parts of this {@link AudioRecord}
     */
    public int getPartNumber() {
        return mPartNumber;
    }

    /**
     * @return Number of parts of already played {@link AudioRecord}
     */
    public int getPlayedTracks() {
        return mPlayedTracks;
    }

    public void setPlayedTracks(int playedTracks) {
        mPlayedTracks = playedTracks;
    }

    // //////////////////// RECORDING PART /////////////////////////

    /**
     * Creates a {@link MediaRecorder} instance and starts recording audio to
     * the file, given by the path. The format is .3gp! If the given path is
     * empty or if the {@link MediaRecorder} is already active, nothing will
     * happen.
     */
    public void startRecording() {
        if (mIsRecording || mPath.isEmpty()) {
            System.out.println("ICH WAR NICHT BEIM ABSPIELEN WEIL DER PFAD LEER IST");
            return;
        }

        mIsRecording = true;

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setOutputFile(mPath + ".3gp");
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaRecorder.start();
    }

    /**
     * Stops recording audio. The {@link MediaRecorder} will be released and set
     * null.
     */
    public void stopRecording() {
        if (!mIsRecording)
            return;

        mIsRecording = false;
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * Pauses the active {@link MediaRecorder}. Attention: Since the android API
     * has no pause-method for the {@link MediaRecorder}, we are stopping the
     * record and starting a new one (little workaround)! This means, we got 1+n
     * parts of our complete audio record. Note, that you don't have to bother
     * with this, because everything is handled here!
     */
    public void pauseRecording() {
        if (mRecPaused)
            return;

        mRecPaused = true;
        stopRecording();
    }

    /**
     * Starts recording audio again, after the {@link MediaRecorder} was paused.
     */
    public void resumeRecording() {
        if (!mRecPaused)
            return;

        mRecPaused = false;
        mPartNumber++;
        mPath = mPath.substring(0, mPath.length() - 1) + mPartNumber;
        startRecording();
    }

    // //////////////////////// PLAING PART /////////////////////////////////

    /**
     * @return MediaPlayer of this {@link AudioRecord}
     */
    public CustomMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mPlayedTracks++;
            if (mPlayedTracks <= mPartNumber){
                sysOut("partEnde_onCompletion");
                playAudio(false,true);
            }
            else{
                sysOut("gesamtEnde_onCompletion");
                firstPlay = true;
                mPlayedTracks = 1;
                mMediaPlayer.seekTo(0);
                mLogActivity.setEndOfPlay(true);
                mLogActivity.setPlay(false);
                mLogActivity.refreshViews();
            }
        }
    };

    public void playAudio(boolean pressedPlayButton, boolean onCompletionListener) {
        if (onCompletionListener || firstPlay ) {
            firstPlay = false;
            if (mPlayedTracks == mPartNumber && pressedPlayButton)
                mPlayedTracks = 1;
            if (!mPath.isEmpty()) {
                try {
                    mMediaPlayer = new CustomMediaPlayer(this, mPartNumber);
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                    mMediaPlayer.setDataSource(mPath.substring(0,
                            mPath.length() - 1)
                            + mPlayedTracks + ".3gp");
                    mMediaPlayer.prepare();
                    mMediaPlayer.getDuration();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            mMediaPlayer.start();
        sysOut("after_playAudio(pressedPlayButton "+pressedPlayButton+ " onCompletionListener "+ onCompletionListener);
    }

    public void pauseAudio() {
        sysOut("before_pauseAudio()");
        mMediaPlayer.pause();
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
        sysOut("after_pauseAudio()");
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
    
    public void seekTo(int msec){
        mMediaPlayer.seekTo(msec);
    }
    
    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }
    
    public void releasePlayer(){
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
    
    public boolean getIsPlayingAudio(){
        return mMediaPlayer.isPlaying();
    }
    
    public void setLogActivity(LogParentActivity mLog){
        mLogActivity = mLog;
    }
    
    public void sysOut(String methodName){
        System.out.println("TRACK: "+methodName +" - PlayedTracks:"+ mPlayedTracks +" - CurrentPosition:"+mMediaPlayer.getCurrentPosition());
    }
}
