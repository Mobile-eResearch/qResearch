
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudiesActivity;
import de.eresearch.app.gui.adapter.AppSettingDialogArrayAdapter;
import de.eresearch.app.gui.adapter.AppSettingDialogArrayAdapterRowItem;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class for the AppSettingDialog
 * @author domme, jan
 *
 */
public class AppSettingDialog extends Dialog{
    
    private Context context;
    private Locale locale;
    private int[] language;
    private String[] local;
    private int[] images; 
    private Editor edit;
    private final static String chargercode = "code";
    private final static String lang = "lang";
    
    private SharedPreferences settings;
    private SharedPreferences settings2;
    
    public AppSettingDialog(Context context) {
        super(context);
        this.context = context;
        
        settings2 = context.getSharedPreferences(lang,
                Context.MODE_PRIVATE);
        
        settings = context.getSharedPreferences(chargercode,
                Context.MODE_PRIVATE);
        
        //Set the current language as the selected language in spinner
        if(context.getResources().getConfiguration().
                locale.getLanguage().equals("de")){
            language = new int[]{R.string.dialog_appsettings_language_german, 
                    R.string.dialog_appsettings_language_english};
            images  = new int[] {R.drawable.german, R.drawable.english};
            local = new String [] {"de","en"};
            
        }
        else{
            language = new int[]{R.string.dialog_appsettings_language_english, 
                    R.string.dialog_appsettings_language_german};
            images  = new int[] {R.drawable.english, R.drawable.german};
            local = new String [] {"en","de"};
            }
    }
    
    /**
     * Return the AppSettingDialog
     * @return
     */
    public AlertDialog appSettingDialog() {
        AlertDialog.Builder appSettingDialog = 
                new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) 
                context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.dialog_app_stettings, null);
        
        //Find spinner and give him the adapter
        final Spinner spinner = (Spinner) 
                v.findViewById(R.id.dialog_appsettings_language_spinner);
        spinner.setAdapter(makeAdapter());
        
        //Set hint of charger code
        final TextView chargerCodeTextView = (TextView) 
                v.findViewById(R.id.dialog_appsettings_edittext_code_title);
        
        edit = settings.edit();
        
        chargerCodeTextView.setText(settings.getString(chargercode, ""));
        chargerCodeTextView.setHint(getCodeOfDeviceOwner(context));
        
        // Set layout of the AppSettingDialog
        appSettingDialog.setView(v);
        appSettingDialog.setTitle(R.string.dialog_appsettings_dialog_title);
        appSettingDialog.setNegativeButton
            (R.string.dialog_appsettings_button_cancle, null);
        appSettingDialog.setPositiveButton
            (R.string.dialog_appsettings_button_save, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSettingDialogArrayAdapterRowItem item = 
                        (AppSettingDialogArrayAdapterRowItem) 
                            spinner.getSelectedItem();
                
                if(item.getLocal().equals(local[0]) && chargerCodeTextView.getText().toString().trim().isEmpty())
                    dialog.dismiss();
                else if (!item.getLocal().equals(local[0]) && chargerCodeTextView.getText().toString().trim().isEmpty()) {    
                    changeLanguage(item);
                    
                    //start new application
                    ((StudiesActivity) (context)).finish();
                    ((StudiesActivity) (context)).
                    startActivity(((StudiesActivity) (context)).getIntent());
                } else if (!chargerCodeTextView.getText().toString().isEmpty() && !item.getLocal().equals(local[0])){
                    
                    edit.putString(chargercode, chargerCodeTextView.getText().toString().trim());
                    edit.commit();
                    
                    changeLanguage(item);
                    
                    //start new application
                    ((StudiesActivity) (context)).finish();
                    ((StudiesActivity) (context)).
                    startActivity(((StudiesActivity) (context)).getIntent());
                } else {
                    edit.putString(chargercode, chargerCodeTextView.getText().toString().trim());
                    edit.commit();
                }
            }
        });
        
        return appSettingDialog.create();
    }
    
    /**
     * Change the langugage of the application
     * @param item selected item from AppSettingDialogArrayAdapter
     */
    private void changeLanguage(AppSettingDialogArrayAdapterRowItem item){
       String langLoc = item.getLocal();
        
        Editor edit2 = settings2.edit();
        edit2.putString(lang, langLoc);
        edit2.commit();
        
        locale = new Locale(item.getLocal());
        Resources resources = context.getResources();
        DisplayMetrics displaymetric = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, displaymetric);
    }
    
    /**
     * Make the AppSettingDialogArraAdapter for the Dialog
     * @return
     */
    private AppSettingDialogArrayAdapter makeAdapter(){
       // Make a list of AppSettingDialogArrayAdapterRowItem's
        ArrayList<AppSettingDialogArrayAdapterRowItem> rowItems = 
                new ArrayList<AppSettingDialogArrayAdapterRowItem>();
        for (int i = 0; i < language.length; i++) {
            AppSettingDialogArrayAdapterRowItem item = 
                    new AppSettingDialogArrayAdapterRowItem
                        (images[i], language[i], local[i]);
            rowItems.add(item);
        }
        
        // Create AppSettingDialogArrayAdapter
        AppSettingDialogArrayAdapter adapter = 
                new AppSettingDialogArrayAdapter(context, 
                        R.layout.dialog_app_settings_spinner_row, rowItems);
        
        // Set viewresource for dropdownmenu of the spinner
        adapter.setDropDownViewResource
        (R.layout.dialog_app_settings_spinner_row);
        
        return adapter;
    }
    
    /**
     * return the name/code from the owner of the device
     * 
     * @return
     */
    public static String getCodeOfDeviceOwner(Context context){
        
        String name = "";
        
        Cursor c = context.getContentResolver().
                query(ContactsContract.Profile.CONTENT_URI, 
                        null, null, null, null);
        int count = c.getCount();
        String[] columnNames = c.getColumnNames();
        c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {
            for (int j = 0; j < columnNames.length; j++) {
                String columnName = columnNames[j];
                String columnValue = c.getString(c.getColumnIndex(columnName));
                
                if(columnName.equals("display_name"))
                    name = columnValue;
            }
        }   
        return name;
    }
    
}
