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
import de.eresearch.app.logic.model.QSort;

/**
 * 
 * @author thg
 *
 */
public class QSortsListFragmentArrayAdapter extends ArrayAdapter<QSort> {

    private final Context context;
    private final List<QSort> qsorts;
    
    public QSortsListFragmentArrayAdapter(Context context, int resource, List<QSort> qsorts) {
        super(context, resource, qsorts);
        this.context = context;
        this.qsorts = qsorts;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     
      View rowView = inflater.inflate(R.layout.list_studies_list_fragment, parent, false);
      TextView textView = (TextView) rowView.findViewById(R.id.label);
      ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
       if (!qsorts.get(position).isFinished())
          imageView.setVisibility(View.VISIBLE);
      textView.setText(qsorts.get(position).toString());
      
      return rowView;
    }
    
    

}
