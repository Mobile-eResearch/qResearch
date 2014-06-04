
package de.eresearch.app.logic.model;

public class Timer {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;

    private int mStatus;

    private long mTotal;

    private long mLastStart;

    public Timer() {
        stop();
    }

    public synchronized void start() {
        if (mStatus == RUNNING) {
            throw new IllegalStateException();
        }
        
        mStatus = RUNNING;
        
        mLastStart = System.currentTimeMillis();
    }

    /**
     * Stops and resets
     */
    public synchronized void stop() {
        mStatus = STOPPED;

        mTotal = 0;
    }

    public synchronized void pause() {
        if (mStatus != RUNNING) {
            throw new IllegalStateException();
        }
        
        mStatus = PAUSED;
        
        mTotal += (System.currentTimeMillis() - mLastStart);
    }

    public synchronized void resume() {
        start();
    }

    public long getTime() {
        if (mStatus == RUNNING) {
            return mTotal + (System.currentTimeMillis() - mLastStart);
        }

        return mTotal;
    }

}
