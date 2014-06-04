package de.eresearch.app.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.QSortsDeleteDialog;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.tasks.common.qsort.DeleteQSortTask;

/** 
 * Activity to provide UI for listing and viewing qsorts. This activity consists of 
 * {@link QSortsDetailFragment} and {@link QSortsListFragment}.
 *
 * MockUp: http://eresearch.informatik.uni-bremen.de/mockup/#studiedatenansicht_page
 */
public class QSortsActivity extends Activity implements QSortsListFragment.Callbacks, DeleteQSortTask.Callbacks, de.eresearch.app.gui.dialogs.QSortsDeleteDialog.Callbacks{
    
    public static final String STUDY_ID = "de.eresearch.app.gui.qsorts_id"; 
    private int studyId;
    private String selectedQSortName;
    private int selectedQSortId;
    private boolean isInitiated;
    private QSort mQSort;

  
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isInitiated = false;
        setContentView(R.layout.activity_qsorts);
    }
    
    /**
     * Callback method for lifecycle state onPause().
     */
    @Override
    public void onPause(){
        super.onPause();
        // TODO Auto-generated method stub
    }
    
    /**
     * Callback method for lifecycle state onResume().
     */
    public void onResume(){
        super.onResume();
        // List items should be given the
        // 'activated' state when touched.
        ((QSortsListFragment) getFragmentManager().
                findFragmentById(R.id.qsorts_list)).
                setActivateOnItemClick(true);
    }

    /**
     * Callback method for click on a list element in @link QSortListFragment
     * @param id QSort ID
     */
    public void onItemSelected(int id) {
        isInitiated = true;
        Bundle arguments = new Bundle();
        arguments.putInt(QSortsDetailFragment.QSORT_ID, id);
        arguments.putInt(QSortsDetailFragment.STUDY_ID, studyId);
        QSortsDetailFragment fragment = new QSortsDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .replace(R.id.qsorts_detail_container, fragment).commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qsorts_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete:
                onClickDelete();
                break;
            default:
                break;
        }
        return true;
    }
    
    private void onClickDelete(){
        if(isInitiated == true){
        QSortsDeleteDialog dialog = new QSortsDeleteDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(QSortsDeleteDialog.QSORT_ID, selectedQSortId);
        arguments.putString(QSortsDeleteDialog.QSORT_NAME, selectedQSortName);

        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "QSortsDeleteDialog");
        }
    }
    
    /**
     * Method to rebuild the fragments after a QSort is deleted
     */
    
    public void onDeleteQSortTask() {
        isInitiated = false;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        QSortsListFragment ql = (QSortsListFragment) fm.findFragmentById(R.id.qsorts_list);
        ql.onResume();
        ft.remove(fm.findFragmentById(R.id.qsorts_detail_container));
        ft.commit();
        
    }
    
    
    
    
    public void setSelectedQSort(QSort qsort){
        mQSort = qsort;
        selectedQSortId = qsort.getId();
        selectedQSortName = qsort.getName();
    }

    @Override
    public void onDeleteDialogOKClick(int id) {
        DeleteQSortTask t = new DeleteQSortTask(this, this, mQSort);
        t.execute();

    }

}


