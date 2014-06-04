package de.eresearch.app.gui;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortTask;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask;

import java.util.ArrayList;

/** 
 * Activity to provide UI to lead an interview
 * 
 */
public class InterviewActivity extends QSortParentActivity implements GetFullStudyTask.Callbacks{
    
    private QSortView mQSortView;
    
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        mPhase = Phase.INTERVIEW;
        //setContentView(R.layout.activity_qsort);
        
        
        GetFullStudyTask getfullstudytask = new GetFullStudyTask(this, this,CurrentQSort.getInstance().getStudyId());
        getfullstudytask.execute();
        
    }
    
    /**
     * Callback method for GetFullStudyTask to update the activity
     */
    
    public void onGetFullStudyTask(Study study){
        mQSortView = new QSortView(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        //for navigation bar height
        Resources resources = getResources();
        int navBarHeight = 0;
        int navBarID = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (navBarID > 0)
            navBarHeight = resources.getDimensionPixelSize(navBarID);
        //for action bar height
        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
             actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        ArrayList<Picture> pictureList = new ArrayList<Picture>();
        
        for(Item item: CurrentQSort.getInstance().getSortedItems()){
            Log.d("InterviewActivity","size=" + CurrentQSort.getInstance().getSortedItems().size());
            pictureList.add((Picture)item);
        }
        
        mQSortView = new QSortView(this);
        mQSortView.onCreate(size, study.getPyramid(), actionBarHeight+navBarHeight,
                pictureList, true);
        mQSortView.onCreate(size, study.getPyramid(), actionBarHeight+navBarHeight, pictureList, true);
        setContentView(mQSortView.getContent());
    }   
    
    /**
     * Callback method for lifecycle state onPause().
     */
    @Override
    public void onPause(){
        super.onPause();
        // TODO Auto-generated method stub
    }

}

