package de.eresearch.app.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.NotesBackDialog;
import de.eresearch.app.gui.dialogs.NotesDeleteDialog;
import de.eresearch.app.gui.dialogs.NotesSaveDialog;
import de.eresearch.app.gui.dialogs.QSortCancelDialog;
import de.eresearch.app.gui.dialogs.QSortsDeleteDialog;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.tasks.common.qsort.DeleteQSortTask;
import de.eresearch.app.logic.tasks.media.DeleteNoteTask;
import de.eresearch.app.logic.tasks.media.GetNotesTask;
import de.eresearch.app.logic.tasks.media.SaveNoteTask;

/** 
 * Activity to provide UI for viewing notes. This activity consists of 
 * {@link NotesDetailFragment} and {@link NotesListFragment}.
 *
 * MockUp: http://eresearch.informatik.uni-bremen.de/mockup/#studiedatenansicht_page
 */
public class NotesActivity extends Activity implements NotesListFragment.Callbacks, GetNotesTask.Callbacks, de.eresearch.app.gui.dialogs.NotesDeleteDialog.Callbacks, 
DeleteNoteTask.Callbacks, de.eresearch.app.gui.dialogs.NotesSaveDialog.Callbacks, de.eresearch.app.logic.tasks.media.SaveNoteTask.Callbacks{
    
    
    private int mQSortId;
    private Note[] mNotes;
    private int mSelectedNoteId;
    private boolean isInitiated;
    private boolean isEdited;
    private String mQSortName;
    public static final String QSORT_ID = "de.eresearch.app.gui.NotesActivity.qsort_id"; 
    public static final String QSORT_NAME = "de.eresearch.app.gui.NotesActivity.qsort_name"; 
    public static final String STUDY_NAME = "de.eresearch.app.gui.NotesActivity.study_name"; 



    
    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        
        
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
        
        Bundle extras = getIntent().getExtras();
        mQSortId = extras.getInt(NotesActivity.QSORT_ID);
        mQSortName = extras.getString(NotesActivity.QSORT_NAME);
        
        GetNotesTask t = new GetNotesTask(this, this, mQSortId);
        t.execute();
        // List items should be given the
        // 'activated' state when touched.
        ((NotesListFragment) getFragmentManager().
                findFragmentById(R.id.notes_list)).
                setActivateOnItemClick(true);
    }

    /**
     * Callback method for click on a list element in @link NotesListFragment
     * @param id Note ID
     */
    public void onItemSelected(int id) {
        isInitiated = true;
        Bundle arguments = new Bundle();

        for(Note n: mNotes){
            if(n.getId() == id){
                //TODO: CHANGE TO TIME
                arguments.putInt(NotesDetailFragment.NOTE_ID, id);
                arguments.putString(NotesDetailFragment.NOTE_TITLE, n.getTitle());
                arguments.putString(NotesDetailFragment.NOTE_TEXT, n.getText());
                arguments.putString(NotesDetailFragment.QSORT_NAME, mQSortName);
                break;

            }
        }
       
        NotesDetailFragment fragment = new NotesDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .replace(R.id.notes_detail_container, fragment).commit();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_activity, menu);
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
            case R.id.action_save:
                onClickSave();
            default:
                break;
        }
        return true;
    }
    
    private void onClickDelete(){
        if(isInitiated){

        NotesDeleteDialog dialog = new NotesDeleteDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(NotesDeleteDialog.NOTE_ID, mSelectedNoteId);

        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "NotesDeleteDialog");
        }
    }
    
    private void onClickSave(){
        if(isInitiated){

        FragmentManager fm = getFragmentManager();
        
        NotesDetailFragment nf = (NotesDetailFragment) fm.findFragmentById(R.id.notes_detail_container);
        
        
        NotesSaveDialog dialog = new NotesSaveDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(NotesDetailFragment.NOTE_ID, mSelectedNoteId);
        arguments.putString(NotesDetailFragment.NOTE_TEXT, nf.getNoteText());
        arguments.putString(NotesDetailFragment.NOTE_TITLE, nf.getNoteTitle());
        

        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "NotesSaveDialog");
        
        }
        
        
    }
    
    
    public void onDeleteNoteTask() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        NotesListFragment ql = (NotesListFragment) fm.findFragmentById(R.id.notes_list);
        ql.onResume();
        ft.remove(fm.findFragmentById(R.id.notes_detail_container));
        ft.commit();
    }

    @Override
    public void onGetNotesTaskUpdate(Note[] noteList) {
        // TODO Auto-generated method stub
        mNotes = noteList;

    }
    
    
    public void setSelectedNoteId(int id){
        mSelectedNoteId = id;
    }

    @Override
    public void onDeleteDialogOKClick(int noteId) {
        DeleteNoteTask t = new DeleteNoteTask(this, this, noteId);
        t.execute();
        
    }

    @Override
    public void onSaveNoteTaskUpdate(Note note) {
        FragmentManager fm = getFragmentManager();
        NotesListFragment ql = (NotesListFragment) fm.findFragmentById(R.id.notes_list);
        ql.onResume();
        isEdited = false;
    }

    @Override
    public void onSaveDialogOKClick(int id, String newTitle, String newText) {
        for (Note n : mNotes) {
            if (n.getId() == id) {
                System.out.println("SAVENOTETASK:" + n.getQSortId() +"/n " + newTitle + "text: " + newText);
                n.setText(newText);
                n.setTitle(newTitle);
                SaveNoteTask t = new SaveNoteTask(this, this, n);
                t.execute();
            }

        }
        
        

    }
    
    public void onBackPressed() {
        if(isEdited){
        NotesBackDialog dialog = new NotesBackDialog();
        dialog.show(getFragmentManager(), "NoteBackDialog");
        }else{
            finish();
        }
    }
    
    public void isEdited(boolean bol){
        isEdited = bol;
    }

}

