package de.eresearch.app.gui;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudiesMergeListFragmentArrayAdapter;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;

/**
 * Fragment for list of studies on the left side of startup screen.
 * 
 * @author mn
 */
public class StudiesMergeListFragment extends Fragment implements GetStudiesListTask.Callbacks, GetFullStudyTask.Callbacks{
    
    
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    
    /**
     * The current activated item position.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private StudiesMergeListFragmentArrayAdapter adapter; 
    private ListView mStudiesList;
    protected List<Study> studies;
    
    /**
     * Callback is called on action onItemClick(). 
     * 
     * If the fragment is not 
     * attached to a activity this callback is a dummy callback 
     * with no action.
     */
    private Callbacks mCallbacks = sDummyCallbacks; 
    
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_studies_list_merge,
                container, false);
        mStudiesList = (ListView) rootView.findViewById(R.id.ListViewStudiesList);
        
        mStudiesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }
    
    @Override
    public void onStart(){
        super.onStart();
      
    }
    
    @Override
    public void onPause(){
        super.onPause();      
    }
    
    
    
    public void onResume(){
        super.onResume();  
        studies = new ArrayList<Study>(); 
        GetStudiesListTask t = new GetStudiesListTask(this,this.getActivity());
        t.execute();
        adapter = new StudiesMergeListFragmentArrayAdapter(getActivity(),
                R.layout.list_studies_merge_list  , studies, this); 
        mStudiesList.setAdapter(adapter);
        mStudiesList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               mActivatedPosition = position;
               mCallbacks.onItemSelected(studies.get(position).getId());
            }
        });
    }

    
    public void onUpdate(int id, List<Integer> selectedStudies){
        if(selectedStudies.size()>0){
            adapter.setSelectedStudy(studies.get(id));  
        }
        else{
            adapter.setSelectedStudy(null);
        }
        adapter.clear();
        adapter.addAll(studies);;
    }
    
    /**
     * Callback method from GetStudiesListTask
     * @param studiesList A List of Study objects
     */
    @Override
    public void onGetStudiesListTaskUpdate(List<Study> studiesList) {   
        this.studies = studiesList;       
        for(int i=0;i<studiesList.size();i++){
            GetFullStudyTask t2 = new GetFullStudyTask(this.getActivity(), this, studies.get(i).getId());
            t2.execute();
        }
        adapter.clear();
        adapter.addAll(studiesList);        
        setActivateOnItemClick(true);
        if (mActivatedPosition != ListView.INVALID_POSITION){
            setActivatedPosition(mActivatedPosition);
        }
        
    }
    
    public void onCheckBoxChange(int checkedId){
        mCallbacks.onCheckBoxChange(checkedId);
    }
    
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
        public void onCheckBoxChange(int id);
    }
    
    
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }

        @Override
        public void onCheckBoxChange(int id) {            
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
        } else if (mActivatedPosition != ListView.INVALID_POSITION){
            setActivatedPosition(mActivatedPosition);
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

        mStudiesList.setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
       
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mStudiesList.setItemChecked(mActivatedPosition, false);
        } else {
            mStudiesList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onGetFullStudyTask(Study study) {
        for(int i=0;i<studies.size();i++){
            if(studies.get(i).getId()==study.getId()){
                studies.set(i, study);
                break;
            }
        }
    }
    
}
