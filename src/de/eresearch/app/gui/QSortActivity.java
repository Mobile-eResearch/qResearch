package de.eresearch.app.gui;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.LogEntry;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask;

import java.util.ArrayList;
import java.util.List;

/** 
 * Activity to provide UI for the sort part of a QSort
 * @author Dominic
 */
public class QSortActivity extends QSortParentActivity implements GetFullStudyTask.Callbacks{
    

    
    private QSortView qsortview;
    
    
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        mPhase = Phase.Q_SORT;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsort);
        
        GetFullStudyTask getfullstudytask = new GetFullStudyTask(this, this,CurrentQSort.getInstance().getStudyId());
        getfullstudytask.execute();
        
    }
    
    
    /**
     * Methode to insert a logEntry in the log
     * 
     * 
     * @param oldCol
     * @param oldRow
     * @param newCol
     * @param newRow
     * @param time
     * @param object
     */
    public void insertLogEntry(int oldCol, int oldRow, int newCol, int newRow, long time, Object object){
        
        //LogEntry logEntry = new LogEntry(CurrentQSort.getInstance().getLog(mPhase).getLogEntries().size()+1);
        LogEntry logEntry = new LogEntry(-1);
        logEntry.setFrom(oldCol, oldRow);
        logEntry.setTo(newCol, newRow);
        logEntry.setTime(time);
        logEntry.setItem((Item) object);
        logEntry.setQSortId(CurrentQSort.getInstance().getId());
        
        CurrentQSort.getInstance().getLog(mPhase).addLogEntry(logEntry);
    }
    
    /**
     * Method to set the sorted items in die QSort
     * 
     * @param pictures ArrayList of Picture with the sorted items
     */
    public void setSortedItems(ArrayList<Picture> pictures){
        //cast ArrayList of pictures to List of items
        List<Item> itemsAsList = new ArrayList<Item>();
        for(Picture picture: pictures){
            itemsAsList.add((Item) picture);
        }
        
        CurrentQSort.getInstance().setSortedItems(itemsAsList);
    }
    
    /**
     * Methode to set
     * 
     * @param study
     */
    private void infalteQSortView(Study study){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //17% window height
        
        //for status bar height
        Resources resources = getResources();
        int statBarHeight = 0;
        int statBarID = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (statBarID > 0)
            statBarHeight = resources.getDimensionPixelSize(statBarID);
        //for action bar height
        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
             actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        
        //Cast list of items to ArrayList of pictures
        ArrayList<Picture> list = new ArrayList<Picture>();
        for(Item item: study.getItems()){
            list.add((Picture)item);
        }
        
        qsortview = new QSortView(this);
        qsortview.onCreate(size, study.getPyramid(), actionBarHeight+statBarHeight,
                list);
        setContentView(qsortview.getContent());
    }
    
    /**
     * Callback method for GetFullStudyTask to update the activity
     */
    @Override
    public void onGetFullStudyTask(Study study){
        this.infalteQSortView(study);
    }
  
    
}

