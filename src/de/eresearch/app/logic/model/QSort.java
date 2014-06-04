
package de.eresearch.app.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a QSort.
 */
public class QSort {

    private int mId;
    private List<Log> mLogs;
    private List<Item> mPyramid;
    private String mName;
    private String mAcronym;
    private boolean mIsFinished;
    private boolean mAudioRecord;
    private int mStudyId;
    private long mStartTime;
    private long mEndTime;

    /**
     * Creates a new QSort with the given id.
     * 
     * @param id The id of the QSort
     */
    public QSort(int id, int studyId) {
        mId = id;
        mStudyId = studyId;
        mLogs = new ArrayList<Log>();

    }

    /**
     * @param id The id of this QSort
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return The id of this QSort
     */
    public int getId() {
        return mId;
    }

    /**
     * Returns the {@link Log} of a specific phase (pre questions, qsort, post
     * questions or interview) of this QSort.
     * 
     * @param phase The phase to get the log from
     * @return The log
     */
    public Log getLog(Phase phase) {

        for (Log l : mLogs) {
            if (l.getPhase() == phase) {
                return l;
            }
        }
        return new Log(mId);
    }

    public List<Log> getLogs() {
        return mLogs;
    }

    /**
     * Sets the {@link Log} of a specific phase (pre questions, qsort, post
     * questions or interview) of this QSort.
     * 
     * @param phase The phase
     * @param log The log
     */
    public void setLog(Phase phase, Log log) {
        log.setPhase(phase);
        boolean found=false;
        for(int l=0;l<mLogs.size();l++){
            if(mLogs.get(l).getPhase()==log.getPhase()){
                mLogs.set(l, log);
                found=true;
            }
        }
        if(!found){
            mLogs.add(log);
        }
    }

    /**
     * @deprecated Don't use this, replaced by {@link QSort#getSortedItems()}!
     */
    @Deprecated
    public Pyramid getPyramid() {
        return null;
    }

    /**
     * @deprecated Don't use this, replaced by
     *             {@link QSort#setSortedItems(List)}!
     */
    @Deprecated
    public void setPyramid(Pyramid pyramid) {
    }

    /**
     * @return The sorted items of this QSort
     */
    public List<Item> getSortedItems() {
        return mPyramid;
    }

    /**
     * @param pyramid The sorted items for this QSort
     */
    public void setSortedItems(List<Item> pyramid) {
        mPyramid = pyramid;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public int getStudyId() {
        return mStudyId;
    }

    public void setStudyId(int id) {
        mStudyId = id;
    }

    /**
     * @param name The name of the QSort
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return The name of the QSort
     */
    public String getName() {
        return mName;
    }

    /**
     * @param acronym The acronym of the QSort
     */
    public void setAcronym(String acronym) {
        mAcronym = acronym;
    }

    /**
     * @return The acronym of the QSort
     */
    public String getAcronym() {
        return mAcronym;
    }

    /**
     * @param finished <code>true</code> when this QSort has been finished yet,
     *            <code>false</code> else
     */
    public void setFinished(boolean finished) {
        mIsFinished = finished;
    }

    /**
     * @return <code>true</code> when this QSort has been finished yet,
     *         <code>false</code> else
     */
    public boolean isFinished() {
        return mIsFinished;
    }

    public void setAudioRecord(boolean audioRecord) {
        mAudioRecord = audioRecord;
    }

    public boolean hasAudioRecord() {
        return mAudioRecord;
    }

    /**
     * @return The string representation (name) of this QSort
     */
    @Override
    public String toString() {
        return mName;
    }

}
