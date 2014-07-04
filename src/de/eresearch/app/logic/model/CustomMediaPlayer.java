
package de.eresearch.app.logic.model;

import android.media.MediaPlayer;

import java.io.IOException;

/**
* Model of a custom MediaPlayer. Important to override some methods of MediaPlayer
*
* @author domme
*
*/
public class CustomMediaPlayer extends MediaPlayer {

    private AudioRecord mAudioRecord;

    private long[] mLengthOfParts;

    public CustomMediaPlayer(AudioRecord audioRecord, int partNumber) {
        mAudioRecord = audioRecord;
        mLengthOfParts = new long[partNumber];
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        mAudioRecord.sysOut("before_seektTo(" + msec + ")");
        int seekTo = msec;
        for (int i = 1; i <= mLengthOfParts.length; i++) {
            if (seekTo >= mLengthOfParts[i - 1]) {
                seekTo = (int) (seekTo - mLengthOfParts[i - 1]);
                mAudioRecord.setPlayedTracks(i + 1);
            }
            else {
                mAudioRecord.setPlayedTracks(i);
                break;
            }
        }

        if (!mAudioRecord.getOriginFilePath().isEmpty()) {
            try {
                super.reset();
                super.setDataSource(mAudioRecord.getOriginFilePath().substring(0,
                        mAudioRecord.getOriginFilePath().length() - 1)
                        + mAudioRecord.getPlayedTracks() + ".3gp");
                super.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mAudioRecord.sysOut("after_seektTo(" + msec + ")");
        super.seekTo(seekTo);
    }

    @Override
    public int getCurrentPosition() {
        int position = 0;
        if (mAudioRecord != null) {
            for (int i = 1; i < mAudioRecord.getPlayedTracks(); i++) {
                position += mLengthOfParts[i - 1];
            }
            position += super.getCurrentPosition();
        }
        return position;
    }

    public int getSuperCurrentPosition() {
        return super.getCurrentPosition();
    }

    public int getSuperDuration() {
        return super.getDuration();
    }

    @Override
    public int getDuration() {
        mAudioRecord.sysOut("before_getDuration");
        int duration = 0;
        int currentPosition = this.getCurrentPosition();

        if (!mAudioRecord.getOriginFilePath().isEmpty()) {
            try {
                for (int i = 1; i <= mAudioRecord.getPartNumber(); i++) {
                    super.reset();
                    super.setDataSource(mAudioRecord.getOriginFilePath().substring(0,
                            mAudioRecord.getOriginFilePath().length() - 1)
                            + i + ".3gp");
                    super.prepare();
                    duration += super.getDuration();
                    mLengthOfParts[i - 1] = super.getDuration();
                    System.out.println("TRACK - lengthOfPart" + (i - 1) + " = "
                            + mLengthOfParts[i - 1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mAudioRecord.getPlayedTracks() > 1) {
            currentPosition = 0;
            for (int i = 0; i <= mAudioRecord.getPlayedTracks() - 2; i++) {
                currentPosition += mLengthOfParts[i];
            }
            currentPosition = currentPosition + 1;
        }
        this.seekTo(currentPosition);
        mAudioRecord.sysOut("after_getDuration()");
        return duration;
    }

}
