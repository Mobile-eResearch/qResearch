package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.QSortsListFragmentArrayAdapter;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortListTask;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Marcel L.
 * Fragment for list of studies on the left side of startup screen.
 */
public class QSortsListFragment extends Fragment  implements GetQSortListTask.Callbacks {
    

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    /**
     * The current activated item position.
     */
    protected int mActivatedPosition = ListView.INVALID_POSITION;
    
    
    /**
     * Callback is called on action onItemClick(). 
     * 
     * If the fragment is not 
     * attached to a activity this callback is a dummy callback 
     * with no action.
     */
    private Callbacks mCallbacks = sDummyCallbacks; 
    
    private int mStudyId;
    private QSortsListFragmentArrayAdapter adapter;
    private View mHeadText;
    private ListView mQSortsList;
    private List<QSort> qsorts;
    private String mStudyName;
    private RelativeLayout mHeadSearch;
    private EditText mSearch;
    private InputMethodManager mImm;
    private ImageView mSearchStartButton, mSearchEndButton;
    

    private List<QSort> savedQSorts;


    
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
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_qsorts_list,
                container, false);
        
        View searchView = inflater.inflate(R.layout.search_qsorts_list_fragment,
                container, false);
        
        mHeadText = (RelativeLayout) rootView.findViewById(R.id.QSortsListHeaderText);
        mHeadSearch = (RelativeLayout) searchView.findViewById(R.id.QSortsListHeaderSearch);
        mQSortsList = (ListView) rootView.findViewById(R.id.ListViewQSortsList);
        
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        
        mSearch = (EditText) searchView.findViewById(R.id.EditTextSearch);
        mSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(mSearch.getText().toString());
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        
        mSearchStartButton = (ImageView) rootView.findViewById(R.id.imageViewSearch);
        mSearchStartButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
               
                ViewGroup vg = (ViewGroup) mHeadText.getParent();
                vg.removeView(mHeadText);
                vg.addView(mHeadSearch);
                mSearch.requestFocus();
                search(mSearch.getText().toString());
                
                // show keyboard
                mImm.showSoftInput(mSearch, InputMethodManager.SHOW_IMPLICIT);
                
            }
            
        });
        
        mSearchEndButton = (ImageView) searchView.findViewById(R.id.imageViewCancleSearch);
        mSearchEndButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
               
                // hide keyboard
                mImm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                
                ViewGroup vg = (ViewGroup) mHeadSearch.getParent();
                vg.removeView(mHeadSearch);
                vg.addView(mHeadText);
                
                qsorts = savedQSorts;
                savedQSorts=null;
                adapter.clear();
                adapter.addAll(qsorts);
             
                
                
            }
            
        });
        
        mHeadText = (RelativeLayout) rootView.findViewById(R.id.QSortsListHeaderText);
        mHeadSearch = (RelativeLayout) searchView.findViewById(R.id.QSortsListHeaderSearch);
        mQSortsList = (ListView) rootView.findViewById(R.id.ListViewQSortsList);
        
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        
        mSearch = (EditText) searchView.findViewById(R.id.EditTextSearch);
        mSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(mSearch.getText().toString());
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        
        mSearchStartButton = (ImageView) rootView.findViewById(R.id.imageViewSearch);
        mSearchStartButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
               
                ViewGroup vg = (ViewGroup) mHeadText.getParent();
                vg.removeView(mHeadText);
                vg.addView(mHeadSearch);
                mSearch.requestFocus();
                search(mSearch.getText().toString());
                
                // show keyboard
                mImm.showSoftInput(mSearch, InputMethodManager.SHOW_IMPLICIT);
                
            }
            
        });
        
        mSearchEndButton = (ImageView) searchView.findViewById(R.id.imageViewCancleSearch);
        mSearchEndButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
               
                // hide keyboard
                mImm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                
                ViewGroup vg = (ViewGroup) mHeadSearch.getParent();
                vg.removeView(mHeadSearch);
                vg.addView(mHeadText);
                
                qsorts = savedQSorts;
                savedQSorts=null;
                adapter.clear();
                adapter.addAll(qsorts);
             
                /*
                 * set activated position in QSortsListFragment after
                 * search to displayed qsort in QSortsDetailFragment
                 */
                // get DetailFragment
                QSort s = ((QSortsDetailFragment) getFragmentManager().
                        findFragmentById(R.id.qsorts_detail_container)).getLoadedStudy();
                
                
                if (s!=null){       
                    // set selection in list
                    int count = 0;
                    for (QSort i: qsorts){
                        if (s.getId() == i.getId() && s.getName().equals(i.getName()))
                            break;
                        count++;
                    }
                    setActivatedPosition(count);
                }
                // ------------------------------------------------------
                
                
            }
            
        });
        
        return rootView;

    }
    
    
    public void onResume(){
        super.onResume();
        QSortsActivity q = (QSortsActivity) getActivity();
        qsorts = new ArrayList<QSort>();
        Bundle extras = getActivity().getIntent().getExtras();
        mStudyId = extras.getInt(QSortsActivity.STUDY_ID);
        mStudyName = extras.getString("studyName");
        GetQSortListTask t = new GetQSortListTask(mStudyId, this.getActivity(),this);
        t.execute();
        
        adapter = new QSortsListFragmentArrayAdapter(getActivity(),
                R.layout.list_qsorts_list_fragment,qsorts);
        mQSortsList.setAdapter(adapter);
        getActivity().setTitle(mStudyName);
        mQSortsList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(qsorts.get(position).getId());
                QSortsActivity q = (QSortsActivity) getActivity();
                q.setSelectedQSort(qsorts.get(position));
            }
        });
        Log.d("QSortsListFragment","");
        
    }
    
    
    @Override
    public void onStart(){
        super.onStart();
      
    }
    
    
    @Override
    public void onPause(){
        super.onPause();
        
        ViewGroup vg = (ViewGroup) mHeadSearch.getParent();
        
        if (vg!=null && vg.indexOfChild(mHeadSearch) != -1){
        vg.removeView(mHeadSearch);
        vg.addView(mHeadText);
        
        qsorts=savedQSorts;
        savedQSorts=null;
        }
      
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
//    @Override
//    public void onListItemClick(ListView listView, View view, int position,
//            long id) {
//        super.onListItemClick(listView, view, position, id);
//        mCallbacks.onItemSelected(qsorts.get(position).getId());
//    }
    
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
        mQSortsList.setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mQSortsList.setItemChecked(mActivatedPosition, false);
        } else {
            mQSortsList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onGetQSortListTaskUpdate(List<QSort> qsortsList) {
        System.out.print("TASKUPDATE");
        qsorts = qsortsList;
        adapter.clear();
        adapter.addAll(qsorts);
    }
    
    /**Search method for the Listview
     * 
     * @param search
     */
    
    private void search(String search){
        
        if (savedQSorts!=null){
            qsorts = savedQSorts;
        }
        
        List<QSort> sl = new ArrayList<QSort>();
        
        for (QSort q: qsorts){
            if (q.getName().toLowerCase().contains(search.toLowerCase()))
                sl.add(q);
        }
        
        savedQSorts = qsorts;
        qsorts = sl;
        
        adapter.clear();
        adapter.addAll(qsorts);
    }
    
    
    
   
}
