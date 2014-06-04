
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.StudyMergeDialog;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.SaveFullStudyTask;

/**
 * Activity to provide UI for merging studies. This activity consists of
 * {@link StudieMergeDetailFragment} and {@link StudieMergeListFragment}.
 * MockUp:
 * http://eresearch.informatik.uni-bremen.de/mockup/#studiedatenansicht_page
 */
public class StudiesMergeActivity extends Activity implements
        StudiesMergeListFragment.Callbacks, StudyMergeDialog.StudyMergeDialogListener,
        SaveFullStudyTask.Callbacks {

    private StudiesMergeListFragment fragment;
    private List<Integer> selectedItems = new ArrayList<Integer>();
    private String title;

    /**
     * Callback method for lifecycle state onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studies);
        fragment = new StudiesMergeListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.studies_list, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.merge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Callback method for lifecycle state onPause().
     */
    @Override
    public void onPause() {
        super.onPause();
        // TODO Auto-generated method stub
    }

    /**
     * Callback method for click on a list element in @link
     * StudiesMergeListFragment
     * 
     * @param studyId Study ID
     */
    @Override
    public void onItemSelected(int id) {
        Bundle arguments = new Bundle();
        arguments.putInt(StudyViewMetaDataFragment.STUDY_ID, id);
        StudyViewMetaDataFragment fragment2 = new StudyViewMetaDataFragment();
        fragment2.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .replace(R.id.studies_detail_container, fragment2).commit();
    }

    @Override
    public void onCheckBoxChange(int id) {

        if (!selectedItems.contains(id)) {
            selectedItems.add(id);
            fragment.onUpdate(id, selectedItems);
        }
        else {
            selectedItems.remove((Integer) id);
            fragment.onUpdate(id, selectedItems);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.selectedItems.size() > 1) {
            merge();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.merge_not_enough_studies_selected)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        // this.event = event;
        // this.keyCode = keyCode;
        // StudyMergeLeaveDialog dialog = new StudyMergeLeaveDialog();
        // dialog.show(getFragmentManager(), null);
        return true;
    }

    private void merge() {
        StudyMergeDialog dialog = new StudyMergeDialog();
        dialog.show(getFragmentManager(), null);
    }

    /**
     * compares two Studies if they are mergeable
     * 
     * @param studyA
     * @param studyB
     * @return true if mergeable
     */
    public static boolean compareStudies(Study studyA, Study studyB) {
        String researchQuestionA = studyA.getResearchQuestion();
        Pyramid pyramidA = studyA.getPyramid();
        List<Question> questionsA = studyA.getAllQuestions();
        if (!researchQuestionA.equals(studyB.getResearchQuestion()) ||
                !comparePyramid(pyramidA, studyB.getPyramid()) ||
                !compareQuestions(questionsA, studyB.getAllQuestions()) ||
                !compareItems(studyA.getItems(), studyB.getItems())) {
            return false;
        }
        return true;

    }

    /**
     * compares two Pyramids
     * 
     * @param pyramidA
     * @param pyramidB
     * @return true if equal
     */
    private static boolean comparePyramid(Pyramid pyramidA, Pyramid pyramidB) {
        if (pyramidA == null || pyramidB == null) {
            return false;
        }
        if (!pyramidA.getPoleLeft().equals(pyramidB.getPoleLeft()) ||
                !pyramidA.getPoleRight().equals(pyramidB.getPoleRight())) {
            return false;
        }
        if (pyramidA.getSize() != pyramidB.getSize() || pyramidA.getWidth() != pyramidB.getWidth()) {
            return false;
        }
        return true;
    }

    /**
     * compares two List of Questions
     * 
     * @param questionA
     * @param questionB
     * @return true if contains same Questions
     */
    private static boolean compareQuestions(List<Question> questionA, List<Question> questionB) {
        if (questionA == null && questionB == null) {
            return true;
        }
        if (questionA == null || questionB == null) {
            return false;
        }
        int size = questionA.size();
        if (size != questionB.size()) {
            return false;
        }
        List<String> questionStringA = new ArrayList<String>();
        List<String> questionStringB = new ArrayList<String>();
        for (int y = 0; y < size; y++) {
            questionStringA.add(questionA.get(y).getText());
            questionStringB.add(questionB.get(y).getText());
        }
        for (int i = 0; i < size; i++) {
            if (!questionStringA.contains(questionStringB.get(i))) {
                
                return false;
            }
        }
        return true;
    }

    /**
     * compares a List of Items
     * 
     * @param itemsA
     * @param itemsB
     * @return true if contains same Items
     */
    private static boolean compareItems(List<Item> itemsA, List<Item> itemsB) {
        int size = itemsA.size();
        if (size != itemsB.size()) {
            return false;
        }
        List<String> itemsStringA = new ArrayList<String>();
        List<String> itemsStringB = new ArrayList<String>();
        for (int y = 0; y < size; y++) {
            itemsStringA.add(itemsA.get(y).toString());
            itemsStringB.add(itemsA.get(y).toString());
        }
        // compares fileNames of the Items
        for (int i = 0; i < size; i++) {
            if (!itemsStringA.contains(itemsStringB.get(i))) {
                return false;
            }
        }
        return true;

    }

    @Override
    public void onStudyMergeDialogPositiveClick(StudyMergeDialog dialog) {
        this.title = dialog.getTitle();
        Study studyA = fragment.studies.get(selectedItems.get(0));
        Study study = new Study(-1);
        study.setName(this.title);
        study.setComplete(true);
        study.setDescription(studyA.getDescription());
        List<Item> items = studyA.getItems();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            items.get(i).setId(-1);
        }
        study.setItems(items);
        Pyramid pyramid = studyA.getPyramid();
        pyramid.setId(-1);
        study.setPyramid(pyramid);
        study.setQuestions(studyA.getAllQuestions());
        study.setResearchQuestion(studyA.getResearchQuestion());
        study.setAuthor(studyA.getAuthor());
        if (studyA.getQuestions(Phase.QUESTIONS_POST) != null
                || studyA.getQuestions(Phase.QUESTIONS_PRE) != null) {
            List<Question> questionsList = studyA.getQuestions(Phase.QUESTIONS_POST);
            questionsList.addAll(studyA.getQuestions(Phase.QUESTIONS_PRE));
            for(int i=0; i<questionsList.size() ;i++){
                questionsList.get(i).setId(-1);
                questionsList.get(i).setStudyId(-1);   
            }            
            study.setQuestions(questionsList);
        }
              List<QSort> qsortList = studyA.getQSorts();
        for (int i = 0; i < selectedItems.size(); i++) {
            Study studyB = fragment.studies.get(selectedItems.get(i));
            qsortList.addAll(studyB.getQSorts());
        }
        if (qsortList != null && qsortList.size() > 0) {
            for (int i = 0; i < qsortList.size(); i++) {
                qsortList.get(i).setStudyId(-1);
                qsortList.get(i).setId(-1);
                List<Log> logList= qsortList.get(i).getLogs();
                for(int y=0;y<logList.size();y++){
                    logList.get(y).setQSortId(-1);
                }
            }
        }
        study.setQSorts(qsortList);
        SaveFullStudyTask t = new SaveFullStudyTask(this, study, this);
        t.execute();
    }

    @Override
    public void onSaveFullStudy(int studyId) {

        this.finish();
    }

}
