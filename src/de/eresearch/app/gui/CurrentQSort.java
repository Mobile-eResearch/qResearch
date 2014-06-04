package de.eresearch.app.gui;

import android.os.Environment;

import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Timer;

/**
 * Singleton for the QSort-object and the AudioRecord-Object
 * 
 * @author domme
 *
 */
public class CurrentQSort {

    private static QSort mQSort = null;
    
    private CurrentQSort(){};
    
    private static AudioRecord mAudioRecord = null;
    
    private static Timer mTimer = null;
    
    public static QSort getInstance(){
        return mQSort;
    }
    
    public static QSort newInstance(int studyID){
        mQSort = new QSort(-1, studyID);
        
        return mQSort;
    }
    
    public static void resetInstance(){
        mQSort= null;
    }
    
    public static AudioRecord getAudioRecord(){
        return mAudioRecord;
    }
    
    public static void setAudioRecord(AudioRecord audioRecord){
        mAudioRecord = audioRecord;
    }
    
    public static AudioRecord newAudioRecord(Phase phase){
        mAudioRecord = new AudioRecord(-1);
        mAudioRecord.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/qResearch"+ phase ); //TODO ordentlichen path!!!
        return mAudioRecord;
    }
    
    public static void resetAudioRecord(){
        mAudioRecord= null;
    }
    
    public static Timer getTimer(){
        return mTimer;
    }
    
    public static Timer newTimer(){
        mTimer = new Timer();
        
        return mTimer;
    }
    
    public static void resetTimer(){
        mTimer=null;
    }
}
