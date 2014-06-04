
package de.eresearch.app.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.app.DialogFragment;
import java.util.List;
import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.StudyEditQuestionClosedDialog;
import de.eresearch.app.gui.dialogs.StudyEditQuestionScaleDialog;
import de.eresearch.app.gui.dialogs.StudyEditSaveDialog;
import de.eresearch.app.gui.dialogs.StudyEditOnBackPressedDialog;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask;
import de.eresearch.app.logic.tasks.common.study.GetStudiesListTask;
import de.eresearch.app.logic.tasks.common.study.SaveFullStudyTask;
import de.eresearch.app.gui.dialogs.StudyEditQuestionSelectorDialog;
import de.eresearch.app.logic.model.Phase;

/**
 * Activity to edit/create a study. This activity consists of
 * {@link StudyEditListFragment}, {@link StudyEditMetaDataFragment},
 * {@link StudyEditPyramidFragment}, {@link StudyEditQuestionFragment} and
 * {@link StudyEditItemsFragment_OLD} MockUp:
 * http://eresearch.informatik.uni-bremen.
 * de/mockup/#studiebearbeitenmetadaten_page
 * 
 * @author thg
 */
@SuppressLint("DefaultLocale")
public class StudyEditActivity extends Activity implements
        StudyEditSaveDialog.Callbacks, SaveFullStudyTask.Callbacks,
        StudyEditOnBackPressedDialog.Callbacks, GetFullStudyTask.Callbacks,
        StudyEditQuestionSelectorDialog.Callbacks, StudyEditQuestionClosedDialog.Callbacks,
        StudyEditQuestionScaleDialog.Callbacks,
        GetStudiesListTask.Callbacks {

    /**
     * Data descriptor for the study ID. Only exists if this is a study edit
     * action
     */
    public static final String STUDY_ID = "de.eresearch.app.gui.StudyEditActivity.STUDY_ID";

    /**
     * Data descriptor for Intent. Should be an Boolean value: true if this
     * activity is used to create a new study, false if this Activity is used to
     * edit a exist study
     */
    public static final String STUDY_NEW = "de.eresearch.app.gui.StudyEditActivity.STUDY_NEW";

    public static final String STUDY_PYRAMID_NEW = "de.eresearch.app.gui.StudyEditActivity.STUDY_PYRAMID_NEW";

    public static final String STUDY_TEMPLATE = "de.eresearch.app.gui.StudyEditActivity.STUDY_TEMPLATE";

    /**
     * Enumeration for {@link StudyEditListFragment}) entries. *
     */
    public static enum EditCategory {
        MetaData, Pyramid, Items, Question
    }

    /**
     * The container stores a study object for all the edit fragments
     */
    private StudyEditContainer sec;

    /**
     * The container stores a study object for all the edit fragments
     */
    private boolean isTemplate = false;

    /**
     * Name of debug log tag.
     */
    private static final String LOG_TAG = "StudyEditActivity";

    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction;
    StudyEditMetaDataFragment studyEMDF = new StudyEditMetaDataFragment();

    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "run onCreate");
        setContentView(R.layout.activity_study_edit);
        sec = StudyEditContainer.getStudyEditContainer();

        if (!(getIntent().getExtras().getBoolean(STUDY_NEW)))
            sec.newStudy = false;
        if (!sec.newStudy && !sec.loaded) {
            Log.d(LOG_TAG, "run GetFullStudyTask");
            if (getIntent().getExtras().getBoolean(STUDY_TEMPLATE)) {
                this.isTemplate = true;

            }
            else {
                this.isTemplate = false;
            }
            // get the full study and setup the container
            GetFullStudyTask t = new GetFullStudyTask(this, this,
                    getIntent().getExtras().getInt(StudyEditActivity.STUDY_ID));
            t.execute();
            sec.newStudy = false;
            sec.loaded = true;

            getIntent().putExtra(STUDY_NEW, false);
        } else
        {
            ((StudyEditListFragment) getFragmentManager().
                    findFragmentById(R.id.fragment_study_edit_list)).
                    setActivateOnItemClick(true);
            StudyEditMetaDataFragment fragment = new StudyEditMetaDataFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_study_edit_right_pane, fragment).commit();
        }

        // get all studies and add a list to the container
        // we need this to disallow studies with the same name
        GetStudiesListTask t2 = new GetStudiesListTask(this, this);
        t2.execute();

    }

    /**
     * Callback method for lifecycle state onStop().
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "run onStop()");

    }

    /**
     * Callback method for lifecycle state onResume().
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonClicked(View v) {
    }

    /**
     * This method inflates save button on menu bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.studyedit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                onMenuSaveClick();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * This method calls the save dialog when save button is clicked.
     */
    private void onMenuSaveClick() {

        // test for study name
        if (sec.getStudy().getName() == null || sec.getStudy().getName().equals(""))
        {
            Toast.makeText(this,
                    getString(R.string.study_edit_no_name_error), Toast.LENGTH_SHORT).show();
        }
        else if (sec.checkDuplicateStudyName(sec.getStudy().getName()))
        {
            Toast.makeText(this,
                    getString(R.string.study_edit_duplicate_name_error), Toast.LENGTH_SHORT).show();
        }
        else
        {

            StudyEditSaveDialog dialog = new StudyEditSaveDialog();
            dialog.show(getFragmentManager(), "StudyEditSaveDialog");
        }
    }

    /**
     * Callback from StudyEditSaveDialog. Pay attention to populate Study object
     * (i.e. by calling {@link StudyEditMetaDataFragment#process()} method)
     * before actual saving shall take place.
     */

    @Override
    public void onSaveClick(DialogFragment dialog) {

        // test for study name
        if (sec.getStudy().getName() == null || sec.getStudy().getName().equals("")) {
            Toast.makeText(this,
                    getString(R.string.study_edit_no_name_error), Toast.LENGTH_SHORT).show();
        }
        else if (sec.checkDuplicateStudyName(sec.getStudy().getName()))
        {
            Toast.makeText(this,
                    getString(R.string.study_edit_duplicate_name_error), Toast.LENGTH_SHORT).show();
        } else {

            // only save study when changed
            if (sec.isChanged) {
                SaveFullStudyTask t = new SaveFullStudyTask(this, sec.getStudy(), this);
                t.execute();
            }
            sec.reset();

            // leave activity
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    public void onNeutralClick(DialogFragment dialog) {
        if (dialog instanceof StudyEditOnBackPressedDialog)
        {
            sec.reset();
            // leave activity
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }

    /**
     * 
     */
    public void onBackPressed() {
        if (sec.isChanged)
        {
            StudyEditOnBackPressedDialog dialog = new StudyEditOnBackPressedDialog();
            dialog.show(getFragmentManager(), "StudyEditOnBackPressedDialog");
        }
        else
        {
            sec.reset();
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }

    }

    /**
     * Callback for SaveFullStudyTask
     */
    @Override
    public void onSaveFullStudy(int studyId) {
        Log.d(LOG_TAG, "run onSaveFullStudy()");
        sec.reset();
    }

    /**
     * Callback for GetFullStudyTask
     */
    @Override
    public void onGetFullStudyTask(Study study) {
        sec = StudyEditContainer.getStudyEditContainer();
        if (this.isTemplate == true) {
            Study newStudy = new Study(-1);
            newStudy.setName(getString(R.string.action_study_template_name) + " " + study.getName());
            newStudy.setAuthor(study.getAuthor());
            newStudy.setDescription(study.getDescription());
            newStudy.setResearchQuestion(study.getResearchQuestion());
            newStudy.setQSorts(null);
            List<Question> questions =study.getAllQuestions();
            for(int i =0 ; i<questions.size(); i++){
                questions.get(i).setId(-1);
                questions.get(i).setStudyId(-1);
            }
            newStudy.setQuestions(questions);
            Pyramid p = study.getPyramid();
            p.setId(-1);
            newStudy.setPyramid(p);
            List<Item> items = study.getItems();
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setId(-1);
            }
            newStudy.setItems(items);
            sec.setStudy(newStudy);
        }
        if (this.isTemplate == true) {
            sec.isChanged = true;
        }
        else{
            sec.setStudy(study);
        }
        sec.newStudy = false;
        ((StudyEditListFragment) getFragmentManager().
                findFragmentById(R.id.fragment_study_edit_list)).
                setActivateOnItemClick(true);

        StudyEditMetaDataFragment fragment = new StudyEditMetaDataFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_study_edit_right_pane, fragment).commit();

    }

    @Override
    public void onAbortClick(DialogFragment dialog) {
    }

    @Override
    public void onOpenTypeClick(DialogFragment dialog, Phase p, boolean isNew) {
        Log.d(LOG_TAG, p.toString());
        StudyEditQuestionOpenFragment fragment = new StudyEditQuestionOpenFragment();
        Bundle b = new Bundle();
        b.putSerializable("PHASE", p);
        b.putBoolean("NEWQ", isNew);
        fragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_study_edit_right_pane, fragment, "OpenQuestionFragment")
                .commit();

    }

    @Override
    public void onClosedTypeClick(DialogFragment dialog, Phase p, boolean isNew) {
        Log.d(LOG_TAG, p.toString());
        StudyEditQuestionClosedFragment fragment = new StudyEditQuestionClosedFragment();
        Bundle b = new Bundle();
        b.putSerializable("PHASE", p);
        b.putBoolean("NEWQ", isNew);
        fragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_study_edit_right_pane, fragment, "ClosedQuestionFragment")
                .commit();

    }

    @Override
    public void onScaleTypeClick(DialogFragment dialog, Phase p, boolean isNew) {
        Log.d(LOG_TAG, p.toString());
        StudyEditQuestionScaleFragment fragment = new StudyEditQuestionScaleFragment();
        Bundle b = new Bundle();
        b.putSerializable("PHASE", p);
        b.putBoolean("NEWQ", isNew);
        fragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_study_edit_right_pane, fragment, "ScaleQuestionFragment")
                .commit();

    }

    @Override
    public void onSaveAnswerClick() {
        // Log.d(LOG_TAG, "Click");
        StudyEditQuestionClosedFragment ff = (StudyEditQuestionClosedFragment) getFragmentManager()
                .findFragmentByTag("ClosedQuestionFragment");
        ff.notifyNewAnswer();

    }

    @Override
    public void onSaveScaleClick() {
        StudyEditQuestionScaleFragment ff = (StudyEditQuestionScaleFragment) getFragmentManager()
                .findFragmentByTag("ScaleQuestionFragment");
        ff.notifyNewAnswer();

    }

    @SuppressLint("DefaultLocale")
	@Override
    public void onGetStudiesListTaskUpdate(List<Study> studiesList) {
        // only get the names out of the studies and save this to the container
        sec.getStudies().clear();

        for (Study s : studiesList)
            sec.getStudies().add(s.getName());

        int loc = -1, count = 0;
        for (String s : sec.getStudies()){
            if (s.toLowerCase().equals(sec.getStudy().getName().toLowerCase()))
                loc = count;
            count++;
        }

        if (loc != -1)
            sec.getStudies().remove(loc);

    }

}
