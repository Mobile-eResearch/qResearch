package de.eresearch.app.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Study;

/**
 * 
 * @author thg
 *
 */
public class StudiesListFragmentArrayAdapter extends ArrayAdapter<Study> {

    private final Context context;
    private final List<Study> studies;
    
    public StudiesListFragmentArrayAdapter(Context context, int resource, List<Study> studies) {
        super(context, resource, studies);
        this.context = context;
        this.studies = studies;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     
      View rowView = inflater.inflate(R.layout.list_studies_list_fragment, parent, false);
      TextView textView = (TextView) rowView.findViewById(R.id.label);
      ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
       if (!studies.get(position).isComplete())
          imageView.setVisibility(View.VISIBLE);
      textView.setText(studies.get(position).toString());
      
      return rowView;
    }
    
}
