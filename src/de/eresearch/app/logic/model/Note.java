
package de.eresearch.app.logic.model;

import android.content.Context;

import de.eresearch.app.R;

/**
 * @author Tammo
 */
public class Note extends Loggable {

    /** The Note's id */
    private int mID = -1;

    /** id of the QSort, the Note is attached to */
    private int mQSortID = -1;

    /** Enum Phase for Note */
    private Phase mPhase = null;

    /** Note's title */
    private String mTitle;

    /** Note's text */
    private String mText;

    /**
     * Creates a new note with the given id.
     * 
     * @param id The id of this note
     */
    public Note(int id) {
        mID = id;
    }

    /**
     * @param id of the QSort for this note
     */
    public void setQSortId(int id) {
        mQSortID = id;
    }

    /**
     * The id is <code>-1</code>, if it's not set yet
     * 
     * @return QSortID for this note
     */
    public int getQSortId() {
        return mQSortID;
    }

    /**
     * @param id of this note
     */
    public void setId(int id) {
        mID = id;
    }

    /**
     * The id is <code>-1</code>, if it's not set yet
     * 
     * @return The id of this note
     */
    public int getId() {
        return mID;
    }

    public Phase getPhase() {
        return this.mPhase;
    }

    public void setPhase(Phase phase) {
        this.mPhase = phase;
    }

    /**
     * The title is empty, if it's not set yet
     * 
     * @return The title of this note
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title The title of this note
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * The text is empty, if it's not set yet
     * 
     * @return the text of this note
     */
    public String getText() {
        return mText;
    }

    /**
     * @param text The text of this note
     */
    public void setText(String text) {
        mText = text;
    }

    @Override
    public String toString(Context context) {
        return context.getString(R.string.loggable_note, mTitle);
    }
    
    
    public String toString(){
        return mTitle;
    }

}
