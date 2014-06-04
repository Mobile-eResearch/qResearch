
package de.eresearch.app.gui;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.eresearch.app.R;

/**
 * 
 * @author thg
 *
 */
public class StudyEditListFragment extends ListFragment {

    private MenuListAdapter mMenu;
    
    private StudyEditContainer sec;
    
    
    public static enum MENU_ENTRY {Meta,Pyramid,Items,Questions};
    
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
     * The previously activated item position. Used for saving purposes.
     */
    //private EditCategory previouslyActivatedPosition;

    /**
     * Left Menu Fragment Entries
     */
    private String[] rowEntries = {
            "1", "2", "3", "4"
    };
    /**
     * Callback is called on action onItemClick(). If the fragment is not
     * attached to a activity this callback is a dummy callback with no action.
     */


    public class MenuListAdapter extends ArrayAdapter<String> {

        Context myContext;

        public MenuListAdapter(Context context, int textViewResourceId,
                String[] objects) {
            super(context, textViewResourceId, objects);
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.fragment_study_edit_list_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.labels);
            label.setText(rowEntries[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_action_warning);
            icon.setVisibility(View.INVISIBLE);

            if (position == 0 && sec.metaDataComplete)
                icon.setVisibility(View.INVISIBLE);
            if (position == 0 && !sec.metaDataComplete)
                icon.setVisibility(View.VISIBLE);

            if (position == 1 && sec.pyramidComplete)
                icon.setVisibility(View.INVISIBLE);
            if (position == 1 && !sec.pyramidComplete)
                icon.setVisibility(View.VISIBLE);
            
            if (position == 2 && sec.itemsComplete)
                icon.setVisibility(View.INVISIBLE);
            if (position == 2 && !sec.itemsComplete)
                icon.setVisibility(View.VISIBLE);
            
            if (position == 3 && sec.questionsComplete)
                icon.setVisibility(View.INVISIBLE);
            if (position == 3 && !sec.questionsComplete)
                icon.setVisibility(View.VISIBLE);
            return row;
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sec = StudyEditContainer.getStudyEditContainer();
        
        Resources res = getResources();
        rowEntries = res.getStringArray(R.array.study_edit_list_rows);
        mMenu = new MenuListAdapter(getActivity(), R.layout.fragment_study_edit_list_row, rowEntries);
        setListAdapter(mMenu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study_edit_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentTransaction ft =
                getActivity().getFragmentManager().beginTransaction();
        switch (position)
        {
            case 0: //MetaData entry
                ft.replace(R.id.activity_study_edit_right_pane, new StudyEditMetaDataFragment());
                ;
                ft.commit();
                setActivatedPosition(0);
                break;
            case 1: //Pyramid entry
                ft.replace(R.id.activity_study_edit_right_pane, new StudyEditPyramidFragment());
                ;
                ft.commit();
                setActivatedPosition(1);
                break;
            case 2: //Item entry
                ft.replace(R.id.activity_study_edit_right_pane, new StudyEditItemsFragment());
                ;
                ft.commit();
                setActivatedPosition(2);
                break;
            case 3: //Question entry
                ft.replace(R.id.activity_study_edit_right_pane, new StudyEditQuestionFragment());
                ;
                ft.commit();
                setActivatedPosition(3);
                break;
            default:
                break;
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
        getListView().setItemChecked(0, true);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }
    
    /**
     * Setup ListView Status
     * 
     * @param status true=icon visible; false=icon invisible
     */
    public void setMenuStatus(MENU_ENTRY e, Boolean status){
        
        switch(e){
            case Meta:
                sec.metaDataComplete = status;
                updateIcon(0,status);
                break;
            case Pyramid: 
                sec.pyramidComplete = status;
                updateIcon(1,status);
                break;
            case Items: 
                sec.itemsComplete = status;
                updateIcon(2,status);
                break;
            case Questions: 
                sec.questionsComplete = status;
                updateIcon(3,status);
                break;
            default: break;
            
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
    
    
    private void updateIcon(int index, boolean status){
        View v = getListView().getChildAt(index - 
            getListView().getFirstVisiblePosition());

        if(v == null)
           return;

        ImageView icon = (ImageView) v.findViewById(R.id.icon);
        if (status)
            icon.setVisibility(View.INVISIBLE);
        else
            icon.setVisibility(View.VISIBLE);
    }
}
