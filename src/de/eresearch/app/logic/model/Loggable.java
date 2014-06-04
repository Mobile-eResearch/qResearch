
package de.eresearch.app.logic.model;

import android.content.Context;

import java.util.concurrent.TimeUnit;



public abstract class Loggable implements Comparable<Loggable> {

    /** time that has passed */
    private long mTime;

    /**
     * @return The time that has passed in milliseconds
     */
    public long getTime() {
        return mTime;
    }

    /**
     * @param time The time that has passed in milliseconds
     */
    public void setTime(long time) {
        mTime = time;
    }

    public String getTimeString() {
        System.out.println(mTime);
        return String.format("%02d:%02d", 
                TimeUnit.MILLISECONDS.toMinutes(mTime),
                TimeUnit.MILLISECONDS.toSeconds(mTime) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTime))
            );
    }

    @Override
    public int compareTo(Loggable another) {
        return (int) (mTime - another.mTime);
    }

    /**
     * Returns a string representation of a {@link Loggable} to display it on
     * the log screen.
     * 
     * @param context An android application context
     * @return The string
     */
    public abstract String toString(Context context);

}
