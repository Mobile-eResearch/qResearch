
package de.eresearch.app.gui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesMergeActivity;
import de.eresearch.app.gui.StudiesMergeListFragment;
import de.eresearch.app.logic.model.Study;

/**
 * @author mn
 */
public class StudiesMergeListFragmentArrayAdapter extends ArrayAdapter<Study>  {

    private final Context context;
    private final List<Study> studies;
    private List<Integer>  disabledStudies;
    private List<Integer> selectedStudies;
    private  Study selected =null;
    private StudiesMergeListFragment fragment;
    
    public StudiesMergeListFragmentArrayAdapter(Context context, int resource, List<Study> studies, StudiesMergeListFragment fragment) {
        super(context, resource, studies);
        this.context = context;
        this.studies = studies;
        this.fragment = fragment;
        this.disabledStudies = new ArrayList<Integer>();
        this.selectedStudies = new ArrayList<Integer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_studies_merge_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        CheckBox cb = (CheckBox) rowView.findViewById(R.id.checkBox1);
        cb.setTag(position);
        if(selectedStudies.contains((Integer) position)){
            cb.setChecked(true);
        }
        textView.setText(studies.get(position).toString());        
        if (!studies.get(position).isComplete()) {
            imageView.setVisibility(View.VISIBLE);
            textView.setTextColor(Color.GRAY);
            cb.setEnabled(false);
        }
        else if (this.selected != null) {
            Study studyB = studies.get(position);
            if (! StudiesMergeActivity.compareStudies(this.selected, studyB)) {
                textView.setTextColor(Color.GRAY);
                cb.setEnabled(false);
                disabledStudies.add((Integer) position);
            }
            else{
                if(disabledStudies.contains((Integer) position)){
                    disabledStudies.remove((Integer) position);
                }
            }
        }
        else{
            disabledStudies.clear();
        }
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                fragment.onCheckBoxChange((Integer)buttonView.getTag());
                if ( isChecked )
                {
                    selectedStudies.add((Integer) buttonView.getTag());
                }
                else{
                    selectedStudies.remove((Integer) buttonView.getTag());
                }
            }
        });
        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        if (studies.get(position) != null && studies.get(position).isComplete() == true) {
            if(disabledStudies.size() < 1 || !disabledStudies.contains((Integer)position)){
                return true;
            }
        }
        return false;
    }
    
    public void setSelectedStudy(Study study){
        this.selected=study;
    }
    

}
