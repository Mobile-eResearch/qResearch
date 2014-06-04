package de.eresearch.app.gui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.eresearch.app.R;

import java.util.List;

/**
 * Custom ArrayAdapter for AppSettingDialog
 * @author domme
 *
 */
public class AppSettingDialogArrayAdapter extends 
    ArrayAdapter<AppSettingDialogArrayAdapterRowItem>{
    
    private Context context;
    private List<AppSettingDialogArrayAdapterRowItem> objects;
    
    /**
     * Constructor of the AppSettingDialogArrayAdapter
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public AppSettingDialogArrayAdapter(Context context,int textViewResourceId,
            List<AppSettingDialogArrayAdapterRowItem> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }
    
    /**
     * Make the dropdown-list of the spinner in the AppSettingDialog
     */
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        return getView(position, convertView, parent);
    }
    
    /**
     * Make the items in the list of the dropdown
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.dialog_app_settings_spinner_row, 
                parent, false);
        
        
        // Cast object to AppSettingDialogArrayAdapterRowItem
        AppSettingDialogArrayAdapterRowItem item = 
                (AppSettingDialogArrayAdapterRowItem) objects.get(position);
        
        // Get the text and image view
        TextView textView = (TextView)row.
                findViewById(R.id.dialog_app_settings_spinner_text);
        ImageView imageView = (ImageView)row.
                findViewById(R.id.dialog_app_settings_spinner_image);
         
        
        // Set text and image
        textView.setText(item.getTextResourceId());
        imageView.setImageResource(item.getImageResourceId());
 
        return row;
       
    }
}
