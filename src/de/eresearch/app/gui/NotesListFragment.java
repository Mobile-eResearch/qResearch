package de.eresearch.app.gui;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.QSortsListFragmentArrayAdapter;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortListTask;
import de.eresearch.app.logic.tasks.media.GetNotesTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Fragment to list all notes of a category. Categories can be 
 * Qsort, Postsort, Presort, Interview
 */
public class NotesListFragment extends ListFragment implements GetNotesTask.Callbacks {
    
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    /**
     * The current activated item position.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    /**
     * Callback is called on action onItemClick(). 
     * 
     * If the fragment is not 
     * attached to a activity this callback is a dummy callback 
     * with no action.
     */
    private Callbacks mCallbacks = sDummyCallbacks; 
    
    private List<Note> notes;
    private String mTitle;
    private String mText;
    private int time;
    private int mQSortId;
    private ArrayAdapter<Note> adapter;
    private ListView mNotesList;
    private View mHeadText;
    private String mQSortName;
    
    /**
     * Callback for fragment lifecycle onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_notes_list,
                container, false);
        
        mHeadText = (RelativeLayout) rootView.findViewById(R.id.NotesListHeaderText);
        mNotesList = (ListView) rootView.findViewById(android.R.id.list);
        
        
        return rootView;
    }
    
    public void onResume(){
        super.onResume();
        notes = new ArrayList<Note>();
        Bundle extras = getActivity().getIntent().getExtras();
        mQSortId = extras.getInt(NotesActivity.QSORT_ID);
        mQSortName = extras.getString(NotesActivity.QSORT_NAME);
        String studyName = extras.getString(NotesActivity.STUDY_NAME);
       // mTitle = getArguments().getString(NotesDetailFragment.NOTE_TITLE);
        GetNotesTask t = new GetNotesTask(this, this.getActivity(), mQSortId);
        t.execute();
        
        adapter = new ArrayAdapter<Note>(this.getActivity(),R.layout.list_notes_list_fragment,R.id.label_notes, notes);
        mNotesList.setAdapter(adapter);
        if(mTitle != null){
        getActivity().setTitle(studyName + " - " + mQSortName + " - " + mTitle);
        }else{
            getActivity().setTitle(studyName + " - " + mQSortName);

        }
        mNotesList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(notes.get(position).getId());
                NotesActivity q = (NotesActivity) getActivity();
                q.setSelectedNoteId(notes.get(position).getId());
                
            }
        });
        Log.d("QSortsListFragment","");
        
    }
    
    
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         * @param np A NotePlace type; used to distinguish item clicked
         * @param noteId A Note id
         */
        public void onItemSelected(int noteId);
    }
    
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };
    
    /**
     * Callback for fragment lifecycle onAttach().
     * Method should check if activity (this fragmenet is attached to) 
     * implements the callback method
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }
    
    /**
     * Callback for fragment lifecycle onDetach()
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * Callback run on item click.
     * This method calls the activity callback method.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position,
            long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        // mCallback.onBlahBlub
    }
    
    /**
     * Callback for fragment lifecycle onViewCreated()
     * 
     * Restore the previously serialized activated item position.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }
    }
    
    /**
     * Callback for fragment lifecycle onSaveInstanceState()
     * 
     * Serialize and persist the activated item position.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onGetNotesTaskUpdate(Note[] noteList) {
        
        //test data
//        Note n = new Note(-1);
//        n.setText("fdsfdf");
//        n.setTitle("2323");
//        Note[] notes1 = new Note[1];
//        notes1[0] = n;
        
        
        System.out.println("ONGETNOTESTASKUPDATE");
        notes = Arrays.asList(noteList);
        adapter.clear();
        adapter.addAll(notes);
    }
}
