package de.eresearch.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.QSortCancelDialog;
import de.eresearch.app.gui.dialogs.QSortFinishDialog;
import de.eresearch.app.gui.dialogs.QSortFinishPhaseDialog;
import de.eresearch.app.gui.dialogs.QSortNoteDialog;
import de.eresearch.app.gui.dialogs.QSortPauseDialog;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.tasks.media.SaveNoteTask;

/** 
 * Activity to provide the actionbar and the behavior for
 * the entire qsort
 * 
 * @author Dominic
 */

public class QSortParentActivity extends Activity implements SaveNoteTask.Callbacks{
    
    /**
     * The phase in which is the qsort currently
     */
    protected Phase mPhase;
    
    /**
     * The state if the phase can be finished
     */
    protected boolean mFinish;
    
    /**
     * The extras from the intent
     */
    protected Bundle mExtras;
    
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mExtras= getIntent().getExtras();
        ActivityCompat.invalidateOptionsMenu(QSortParentActivity.this);
    }
    
    /**
     * Make the actionbar and give them the right layout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qsort, menu);
        return true;
    }

    /**
     * Methode to change the icons in the actionbar dynamicly
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //if the qsort is in the phase interview, we disable the cancel icon
        if (mPhase == Phase.INTERVIEW)
            menu.getItem(3).setVisible(false);
        
        //if we can continue to the next phase
        if (mFinish || mPhase == Phase.INTERVIEW)
            menu.getItem(4).setIcon(R.drawable.ic_action_finished);
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    /**
     * Behavior of the icons in the actionbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.action_help:
                onClickHelp();
              break;
            case R.id.action_notice:
                new QSortNoteDialog(this, mPhase);
              break;
            case R.id.action_pause:
                // TODO: pause the recording
                new QSortPauseDialog(this).qsortPauseDialog().show();
                break;
            case R.id.action_cancle:
                new QSortCancelDialog(this).qsortCancelDialog().show();
                break;
            case R.id.action_finish:
                onClickFinish();
                break;
            default:
              break;
            }
        return super.onOptionsItemSelected(item);
    }
    
    private void onClickHelp(){
     // TODO Help Overlay
        switch(mPhase){
            case QUESTIONS_PRE:
                break;
            case Q_SORT:
                break;
            case QUESTIONS_POST:
                break;
            case INTERVIEW:
                break;
            default:
                break;
                }
    }
    
    private void onClickFinish(){
        switch(mPhase){
            //if we are in the phase interview, we can finish the qsort
            case INTERVIEW:
                new QSortFinishDialog(this).
                    qsortFinishDialog().show();
                    Log.d("QSortParentActivity","in case INTERVIEW");
                break;
            //TODO this case should be delete, if we have more than the phase Q_Sort    
            //if we are in the others phases, we can continue to the next phase    
            default:
                //if the mFinish flag is true, we can continue to the next phase
                if(mFinish)
                    new QSortFinishPhaseDialog(this).
                        qsortFinishPhaseDialog(mPhase).show();
                //new QSortFinishDialog(this, mQSort).
                //qsortFinishDialog().show();
                //else we make a toast
                else{
                    switch (mPhase) {
                        case Q_SORT:
                            Toast.makeText(this, 
                                    R.string.qsortparentactivity_toast_unfinished_qsort, 
                                    Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(this, 
                                    R.string.qsortparentactivity_toast_unfinished_questions, 
                                    Toast.LENGTH_LONG).show();
                            
                            break;
                    }
                }
                break;
        }
    }
    
    /**
     * Set the state of phase, if it is finish or not
     * 
     * @param finish The state, if it is finish
     */
    public void setStateOfPhase(boolean finish){
        mFinish = finish;
        invalidateOptionsMenu();
    }
    

    @Override
    public void onSaveNoteTaskUpdate(Note note) {
        Toast.makeText(this, getString(R.string.qsortparentactivity_toast_save_note) 
               +" " + note.getTitle(), Toast.LENGTH_SHORT).show();
    }
 
    @Override
    public void onBackPressed() {
        new QSortCancelDialog(this).qsortCancelDialog().show();
    }
}
