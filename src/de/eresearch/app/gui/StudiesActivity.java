
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.AppExitDialog;
import de.eresearch.app.gui.dialogs.AppSettingDialog;
import de.eresearch.app.gui.dialogs.StudyFromTemplateDialog;
import de.eresearch.app.gui.dialogs.StudyImportDialog;
import de.eresearch.app.logic.tasks.common.study.DeleteStudyTask;

import java.io.File;
import java.util.Locale;

/**
 * Activity to provide startup screen. This activity consists of
 * {@link StudiesDetailFragment} and {@link StudiesListFragment}. MockUp:
 * http://eresearch.informatik.uni-bremen.de/mockup/#startmen_page
 * 
 * @author thg
 */
public class StudiesActivity extends Activity implements StudiesListFragment.Callbacks,
        de.eresearch.app.logic.tasks.common.study.DeleteStudyTask.Callbacks,
        de.eresearch.app.gui.dialogs.StudyDeleteDialog.Callbacks,
        de.eresearch.app.gui.dialogs.StudyImportDialog.Callbacks,
        de.eresearch.app.gui.dialogs.StudyImportNewNameDialog.Callbacks {

    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load UI
        setContentView(R.layout.activity_studies);

        // load list fragment (the left side of UI)
        StudiesListFragment fragment = new StudiesListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.studies_list, fragment).commit();

    }

    /**
     * Callback method for lifecycle state onResume().
     */
    public void onResume() {
        super.onResume();
        // List items should be given the
        // 'activated' state when touched.
        ((StudiesListFragment) getFragmentManager().
                findFragmentById(R.id.studies_list)).
                setActivateOnItemClick(true);
    }

    /**
     * Callback method for click on a list element in @link StudiesListFragment
     * 
     * @param id Study ID
     */
    @Override
    public void onItemSelected(int id) {
        Bundle arguments = new Bundle();

        // create the argument STUDY_ID
        arguments.putInt(StudiesDetailFragment.STUDY_ID, id);

        // create a new StudieDetailFragment
        StudiesDetailFragment fragment = new StudiesDetailFragment();

        // set the argument
        fragment.setArguments(arguments);

        // load the fragments
        getFragmentManager().beginTransaction()
                .replace(R.id.studies_detail_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.studies, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onActionAddClick();
                break;
            case R.id.action_template:
                onActionTemplateClick();
                break;
            case R.id.action_merge:
                onActionMergeClick();
                break;
            case R.id.action_import:
                onActionImportClick();
                break;
            case R.id.action_settings:
                onActionSettingsClick();
                break;
            case R.id.action_finish:
                onActionExitClick();
                break;
            case R.id.action_imprint:
                onActionImpressumClick();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * on click button: create a new study
     */
    private void onActionAddClick() {
        Intent intent = new Intent(this, StudyEditActivity.class);
        intent.putExtra(StudyEditActivity.STUDY_NEW, true);
        startActivityForResult(intent, 100);
    }

    /**
     * on click button: create a new study from template
     */
    private void onActionTemplateClick() {

        new StudyFromTemplateDialog(this);

    }

    /**
     * on click button: merge studies
     */
    private void onActionMergeClick() {
        Intent intent = new Intent(this, StudiesMergeActivity.class);
        this.startActivity(intent);
    }

    private void onActionImportClick() {
        StudyImportDialog dialog = new StudyImportDialog();
        dialog.setContext(this);
        dialog.show(getFragmentManager(), "StudyImportDialog");
    }

    /**
     * on click button: open settings dialog
     */
    private void onActionSettingsClick() {
        new AppSettingDialog(this).appSettingDialog().show();
    }

    /**
     * on click button: exit app
     */
    private void onActionExitClick() {
        AppExitDialog.exitDialog(this).show();
    }

    /**
     * on click button: open impress
     */
    private void onActionImpressumClick() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_studies_imprint);
        dialog.setTitle(R.string.action_imprint);
        dialog.setCancelable(true);

        TextView text = (TextView) dialog.findViewById(R.id.TextView01);

        text.setText(R.string.text_imprint);

        // Button to show open source software
        Button ossButton = (Button) dialog.findViewById(R.id.impressButtonOss);
        ossButton.setText("Open Source Software");
        ossButton.setClickable(true);

        ossButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView wv = new WebView(StudiesActivity.this);
                wv.loadUrl("file:///android_asset/open-source.html");

                AlertDialog.Builder builder = new AlertDialog.Builder(StudiesActivity.this);
                builder.setView(wv);
                AlertDialog ossDialog = builder.create();
                ossDialog.setCanceledOnTouchOutside(true);
                ossDialog.show();
            }
        });

        dialog.show();
    }

    /**
     * Callback for DeleteStudyTask runs when task finished
     */
    @Override
    public void onDeleteStudyTask() {
        // refresh StudiesListFragment
        FragmentManager fm = getFragmentManager();
        StudiesListFragment newFragment = new StudiesListFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.studies_list, newFragment);
        ft.remove(fm.findFragmentById(R.id.studies_detail_container));
        ft.commit();
    }

    /**
     * on click button: OK in StudyDeleteDialog
     */
    @Override
    public void onDeleteDialogOKClick(int id) {
        DeleteStudyTask t = new DeleteStudyTask(this, this, id);
        t.execute();
    }

    /**
     * on click the back-button
     */
    @Override
    public void onBackPressed() {
        AppExitDialog.exitDialog(this).show();
    }

    /**
     * Change the language of the application
     * 
     * @param item selected item from AppSettingDialogArrayAdapter
     */
    private void changeLanguage() {
        String langApp = this.getResources().getConfiguration().
                locale.getLanguage();

        SharedPreferences settings = this.getSharedPreferences("lang",
                Context.MODE_PRIVATE);
        Editor edit = settings.edit();

        String langXml = settings.getString("lang", "");

        if (!langXml.equals(langApp) && !langXml.equals("")) {
            Locale locale = new Locale(langXml);
            Resources resources = this.getResources();
            DisplayMetrics displaymetric = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displaymetric);

            edit.putString("lang", langXml);
            edit.commit();
        } else {
            edit.putString("lang", langApp);
            edit.commit();
        }

    }

    public void onStart() {
        changeLanguage();

        File appFolder = new File(Environment.getExternalStorageDirectory().toString() + "/qResearch/Pictures");

        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        MediaScannerConnection.scanFile(this, new String[] {
                appFolder.toString()
        }, null, null);

        super.onStart();
    }

    /**
     * Callback on study import
     */
    @Override
    public void onImportCallback() {
        // recreate the StudiesListFragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.detach(((StudiesListFragment) getFragmentManager().
                findFragmentById(R.id.studies_list)));
        ft.attach(((StudiesListFragment) getFragmentManager().
                findFragmentById(R.id.studies_list)));
        ft.commit();
    }
}
