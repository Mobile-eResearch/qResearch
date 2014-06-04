package de.eresearch.app.db.helper;

import java.util.Arrays;
import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.EnumTable;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.LogEntry;
import de.eresearch.app.logic.model.Phase;

/**
 * Helper for Log model object
 * @author Jurij Wöhlke
 */
public class LogHelper extends AbstractObjectHelper<Log> implements QSortObjectHelperInterface<Log>{

    /**
     * standard constructor
     * @param dbconn
     */
    public LogHelper(final DatabaseConnector dbconn) {
        super(dbconn);
    }

    /**
     * returns the log as an array with one element
     * @param int qsortId
     * @return Log - empty if not exists
     * @Override
     */
    public Log[] getAllByQSortId(final int identifier) {
        Log[] result=new Log[Phase.values().length];
        int i=0;
        for(Phase phase:Phase.values()){
            result[i]=this.getByPhaseAndQSortId(phase, identifier);
            
            //increment counter;
            i++;
        }
        return result;
    }
    
    /**
     * loads and returns a log object for a phase and a qsortid
     * @param phase
     * @param qsortId
     * @return Log
     */
    public Log getByPhaseAndQSortId(Phase phase, int qsortId){
        try{
            Log result=new Log(qsortId);
            result.setPhase(phase);
            
            //IF QSORT THEN ADD SORTLOGGING: 
            if(phase==Phase.Q_SORT){
                final LogEntryHelper logEntry = (LogEntryHelper)this.dbc.getHelper(DatabaseConnector.TYPE_LOGENTRY);
                LogEntry[] logentries=logEntry.getAllByQSortId(qsortId);
                if(logentries!=null && logentries.length>0){
                    for(final LogEntry tmpLogEnt:logentries){
                        result.addLogEntry(tmpLogEnt);
                    }
                }
            }
            //ADD NOTES:
            final NoteHelper noteHelper = (NoteHelper)this.dbc.getHelper(DatabaseConnector.TYPE_NOTE);
            Note[] notes=noteHelper.getAllByQSortIdAndFilterByPhase(qsortId, EnumTable.getQPhaseIntForModelEnum(phase));
            if(notes!=null && notes.length>0){
                for(final Note tmpNote:notes){
                    result.addNote(tmpNote);
                }
            }
            
            //ADD AUDIORECORD:
            final AudioRecordHelper audioRecordHelper = (AudioRecordHelper)this.dbc.getHelper(DatabaseConnector.TYPE_AUDIORECORD);
            AudioRecord[] audio=audioRecordHelper.getAllByQSortIdAndFilterByPhase(qsortId, EnumTable.getQPhaseIntForModelEnum(phase));
            if(audio!=null && audio.length>=1){
                result.setAudio(audio[0]);
            }

            //Add Answers
            if(phase==Phase.QUESTIONS_POST || phase==Phase.QUESTIONS_PRE){
                AnswerHelper answerHelper = (AnswerHelper)this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER);
                Answer[] answers=answerHelper.getAllByQSortIdAndPhase(result.getQSortId(), phase);
                if(answers!=null){
                    result.setAnswers(Arrays.asList(answers));
                }
            }
            
            return result;
        }catch(HelperNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * saves a log object
     * @param Log obj
     * @return Log - null if fails
     * @Override
     */
    public Log saveObject(final Log log) {
        Log result=log;
        try {
            if(result!=null){
                //save Answers
                List<Answer> answers=log.getAnswers();
                if(answers!=null && answers.size()>0){
                    final AnswerHelper answerHelper=(AnswerHelper) this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER);
                    for(int i=0;i<answers.size();i++){
                        Answer answer=answers.get(i);
                        answer.setQSortId(log.getQSortId());
                        answer=answerHelper.saveObject(answer);
                        if(answer!=null){
                            answers.set(i,answer);
                        }
                    }
                }
                
                //save AUDIO
                AudioRecord audio=log.getAudio();
                if(audio!=null){
                    final AudioRecordHelper audioHelper=(AudioRecordHelper) this.dbc.getHelper(DatabaseConnector.TYPE_AUDIORECORD);
                    audio.setQSortId(log.getQSortId());
                    audio.setPhase(log.getPhase());
                    audio=audioHelper.saveObject(audio);
                    if(audio!=null){
                        result.setAudio(audio);
                    }
                }
                
                //save Notes
                List<Note> notes=log.getNotes();
                if(notes!=null && notes.size()>0){
                    final NoteHelper noteHelper=(NoteHelper) this.dbc.getHelper(DatabaseConnector.TYPE_NOTE);
                    for(int k=0;k<notes.size();k++){
                        Note note=notes.get(k);
                        note.setQSortId(log.getQSortId());
                        note.setPhase(log.getPhase());
                        note=noteHelper.saveObject(note);
                        if(note!=null){
                            notes.set(k,note);
                        }
                    }
                }
                
                //save SortLogging:
                List<LogEntry> logEntries=log.getLogEntries();
                if(logEntries!=null && logEntries.size()>0){
                    final LogEntryHelper logEntryHelper=(LogEntryHelper) this.dbc.getHelper(DatabaseConnector.TYPE_LOGENTRY);
                    for(int j=0; j<logEntries.size();j++){
                        LogEntry logEntry=logEntries.get(j);
                        if(logEntry.getQSortId()<=0){
                            logEntry.setQSortId(log.getQSortId());
                        }
                        logEntry=logEntryHelper.saveObject(logEntry);
                        if(logEntry!=null){
                            logEntries.set(j,logEntry);
                        }
                    }
                }
            }            
        } catch (HelperNotFoundException e) {
            return null;
        }
        return result;
    }

    /**
     * refreshs log object - not implemented yet
     * @param Log obj
     * @return Log - null if fails
     * @Override
     */
    public Log refreshObject(final Log obj) {
        // TODO : implement dont forget to filter by phase
        return null;
    }

    /**
     * deletes log from database and everything connected to it
     * @param Log log
     * @return bool - returns true or false if it fails
     * @Override
     */
    public boolean deleteObject(Log log) {
        boolean result=true;
        if(log!=null){
            try {
                final AnswerHelper answerHelper=(AnswerHelper) this.dbc.getHelper(DatabaseConnector.TYPE_ANSWER);
                final LogEntryHelper logEntryHelper=(LogEntryHelper) this.dbc.getHelper(DatabaseConnector.TYPE_LOGENTRY);
                final NoteHelper noteHelper=(NoteHelper) this.dbc.getHelper(DatabaseConnector.TYPE_NOTE);
                final AudioRecordHelper audioRecordHelper=(AudioRecordHelper) this.dbc.getHelper(DatabaseConnector.TYPE_AUDIORECORD);
                
                //NOTES
                List<Note> notes=log.getNotes();
                for(Note note:notes){
                    if(!(noteHelper.deleteObject(note) && result)){
                        result=false;
                    }
                }
                
                //AUDIO
                if(log.getAudio()!=null){
                    if(!(audioRecordHelper.deleteObject(log.getAudio()) && result)){
                        result=false;
                    }
                }
                
                //SORTLOGGING
                List<LogEntry> logEntries=log.getLogEntries();
                for(LogEntry logEntry:logEntries){
                    if(!(logEntryHelper.deleteObject(logEntry) && result)){
                        result=false;
                    }
                }
                
                //Answers
                List<Answer> answers=log.getAnswers();
                if(answers!=null && answers.size()>0){
                    for(Answer answer:answers){
                        if(!(answerHelper.deleteObject(answer) && result)){
                            result=false;
                        }
                    }
                }
            //Handle Exceptions:
            } catch (HelperNotFoundException e) {
                result = false;
            }
        }
        return result;
    }

    
    // ################# getter and setter ################# 
    @Override
    public DatabaseConnector getDbc() {
        return this.dbc;
    }

    @Override
    public void setDbc(final DatabaseConnector dbconn) {
        this.dbc=dbconn;    
    }
}