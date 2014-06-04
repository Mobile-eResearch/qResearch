
package de.eresearch.app.logic.model;

import java.util.ArrayList;
import java.util.List;

public class Log {
    
    private List<Note> mNotes;
    private List<Answer> mAnswers;
    private List<LogEntry> mEntries;
    private AudioRecord mAudioRecord;
    private int mQSortId;
    private Phase mPhase;
    
    
    public Log (int qSortId){
        mQSortId = qSortId;
        mNotes = new ArrayList<Note>();
        mAnswers = new ArrayList<Answer>();
        mEntries = new ArrayList<LogEntry>();
        
    }

    /**
     * @return A List of all notes stored in this log
     */
    public List<Note> getNotes() {
        return mNotes;
    }
    
    /**
     * Overwrites complete list
     * @param notes
     */
    public void setNotes(List<Note> notes){
        this.mNotes=notes;
    }

    /**
     * @param note A single note to be added to this log
     */
    public void addNote(Note note) {
        mNotes.add(note);
    }

    /**
     * @param noteId The id of the note to be removed from this log
     */
    public void removeNote(int noteId) {
        for(Note n:mNotes){
            if(n.getId() == noteId){
                mNotes.remove(n);
                break;
            }
        }
        
    }

    /**
     * @return A List of all log entries stored in this log
     */
    public List<LogEntry> getLogEntries() {
        return mEntries;
    }
    
    /**
     * Overwrites complete list
     * @param logEntries
     */
    public void setLogEntries(List<LogEntry> logEntries){
        this.mEntries=logEntries;
    }

    /**
     * @param note A single log entry to be added to this log
     */
    public void addLogEntry(LogEntry entry) {
        mEntries.add(entry);
    }

    /**
     * @param noteId The id of the log entry to be removed from this log
     */
    public void removeLogEntry(int entryId) {
        for (LogEntry l: mEntries){
            if(l.getId() == entryId){
                mEntries.remove(l);
                break;
            }
        }
    }

    /**
     * @return A List of all answers stored in this log
     */
    public List<Answer> getAnswers() {
        return mAnswers;
    }
    
    /**
     * Overwrites complete list
     * @param answers
     */
    public void setAnswers(List<Answer> answers){
        this.mAnswers=answers;
    }

    /**
     * @param note A single answer to be added to this log
     */
    public void addAnswer(Answer answer) {
        mAnswers.add(answer);
    }

    /**
     * @param noteId The id of the answer to be removed from this log
     */
    public void removeAnswer(int answerId) {
        for (Answer a: mAnswers){
            if (a.getId() == answerId){
                mAnswers.remove(a);
            }
        }
    }

    /**
     * @return The audio record attached to this log or <code>null</code>, when
     *         none was attached
     */
    public AudioRecord getAudio() {
        return mAudioRecord;
    }

    /**
     * @param audio The audio record to be attached to this log
     */
    public void setAudio(AudioRecord audio) {
        mAudioRecord = audio;
    }
    
    public Phase getPhase(){
        return mPhase;
    }
    
    public void setPhase(Phase phase){
        mPhase = phase;
    }
    
    
    public int getQSortId(){
        return mQSortId;
    }
    
    public void setQSortId(int id){
        mQSortId = id;
    }
    

}
