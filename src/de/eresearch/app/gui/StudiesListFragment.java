
package de.eresearch.app.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import de.eresearch.app.R;
import de.eresearch.app.gui.adapter.StudiesListFragmentArrayAdapter;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;

/**
 * Fragment for list of studies on the left side of startup screen.
 * 
 * @author thg
 */
@SuppressLint("DefaultLocale")
public class StudiesListFragment extends Fragment implements GetStudiesListTask.Callbacks {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private StudiesListFragmentArrayAdapter adapter;

    private InputMethodManager mImm;

    private ListView mStudiesList;
    private ImageView mSearchStartButton, mSearchEndButton;
    private EditText mSearch;
    private RelativeLayout mHeadText, mHeadSearch;
    private TextView mHeadTitle;
    
    private List<Study> studies;
    private List<Study> savedStudies;

    /**
     * Callback is called on action onItemClick(). If the fragment is not
     * attached to a activity this callback is a dummy callback with no action.
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

        View rootView = inflater.inflate(R.layout.fragment_studies_list,
                container, false);
        View searchView = inflater.inflate(R.layout.search_studies_list_fragment,
                container, false);
        
        mHeadTitle = (TextView) rootView.findViewById(R.id.TextViewHeader);
        mHeadText = (RelativeLayout) rootView.findViewById(R.id.StudiesListHeaderText);
        mHeadSearch = (RelativeLayout) searchView.findViewById(R.id.StudiesListHeaderSearch);
        mStudiesList = (ListView) rootView.findViewById(R.id.ListViewStudiesList);
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
            }

        });

        mSearchStartButton = (ImageView) rootView.findViewById(R.id.imageViewSearch);
        mSearchStartButton.setOnClickListener(new OnClickListener() {
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
        mSearchEndButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide keyboard
                mImm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);

                ViewGroup vg = (ViewGroup) mHeadSearch.getParent();
                vg.removeView(mHeadSearch);
                vg.addView(mHeadText);

                studies = savedStudies;
                savedStudies = null;
                adapter.clear();
                adapter.addAll(studies);

                /*
                 * set activated position in StudiesListFragment after search to
                 * displayed study in StudieDetailFragment
                 */
                // get DetailFragment
                StudiesDetailFragment fragment = (StudiesDetailFragment) getFragmentManager().
                        findFragmentById(R.id.studies_detail_container);
                // only if there is a fragment
                if (fragment != null) {
                    Study s = fragment.getLoadedStudy();
                    if (s != null) {
                        // set selection in list
                        int count = 0;
                        for (Study i : studies) {
                            if (s.getId() == i.getId() && s.getName().equals(i.getName()))
                                break;
                            count++;
                        }
                        setActivatedPosition(count);
                    }
                }
                // ------------------------------------------------------
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

        ViewGroup vg = (ViewGroup) mHeadSearch.getParent();

        if (vg != null && vg.indexOfChild(mHeadSearch) != -1) {
            vg.removeView(mHeadSearch);
            vg.addView(mHeadText);

            studies = savedStudies;
            savedStudies = null;
        }

    }

    public void onResume() {
        super.onResume();
        
        
        // Bugfix
        mHeadTitle.setText(getResources().getString(R.string.studies_list_head_title));
        // clear the ListView data container
        studies = new ArrayList<Study>();

        // get the studies out of the database
        GetStudiesListTask t = new GetStudiesListTask(this, this.getActivity());
        t.execute();

        // add the adapter (with data) to the ListView
        adapter = new StudiesListFragmentArrayAdapter(getActivity(),
                R.layout.list_studies_list_fragment, studies);

        mStudiesList.setAdapter(adapter);

        // define action: OnItemClick
        mStudiesList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // save the position
                mActivatedPosition = position;
                // call the methode in the activity
                mCallbacks.onItemSelected(studies.get(position).getId());
            }

        });
    }

    /**
     * Callback method from GetStudiesListTask
     * 
     * @param studiesList A List of Study objects
     */
    @Override
    public void onGetStudiesListTaskUpdate(List<Study> studiesList) {

        // update the data conteiner
        this.studies = studiesList;

        // add the new data to the ListView
        adapter.clear();
        adapter.addAll(studiesList);

        // activate selection in ListView
        setActivateOnItemClick(true);
        // set selection to previous selected item
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            setActivatedPosition(mActivatedPosition);
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
     * Callback for fragment lifecycle onAttach(). Method should check if
     * activity (this fragmenet is attached to) implements the callback method
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
     * Callback for fragment lifecycle onViewCreated() Restore the previously
     * serialized activated item position.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        } else if (mActivatedPosition != ListView.INVALID_POSITION) {
            setActivatedPosition(mActivatedPosition);
        }
    }

    /**
     * Callback for fragment lifecycle onSaveInstanceState() Serialize and
     * persist the activated item position.
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

    @SuppressLint("DefaultLocale")
    private void search(String search) {

        // update the search data container
        if (savedStudies != null) {
            studies = savedStudies;
        }

        // create a new list
        List<Study> sl = new ArrayList<Study>();

        // add all studies with name like search string to the new list
        for (Study s : studies) {
            if (s.getName().toLowerCase().contains(search.toLowerCase()))
                sl.add(s);
        }

        // save the previous list
        savedStudies = studies;

        // set the ListView data container to the search result
        studies = sl;

        // update the adapter (the ListView)
        adapter.clear();
        adapter.addAll(studies);
    }

}
