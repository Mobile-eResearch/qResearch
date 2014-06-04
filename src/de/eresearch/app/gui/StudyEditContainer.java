
package de.eresearch.app.gui;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.model.Study;

/**
 * @author thg
 */
@SuppressLint("DefaultLocale")
public class StudyEditContainer {

    private Study mStudy;
    public Boolean newStudy = true;
    public Boolean loaded = false;
    public boolean metaDataComplete = false;
    public boolean itemsComplete = false;
    public boolean pyramidComplete = false;
    public boolean questionsComplete = true;
    public boolean isChanged = false;
    private List<String> studies;

    private StudyEditContainer()
    {
        mStudy = new Study(-1);
        mStudy.setName("");
        mStudy.setAuthor("");
        mStudy.setResearchQuestion("");
        mStudy.setDescription("");
        mStudy.setPyramid(new Pyramid(-1));
        newStudy = true;
        loaded = false;
        metaDataComplete = false;
        itemsComplete = false;
        pyramidComplete = false;
        questionsComplete = true;
        studies = new ArrayList<String>();

    }

    public static synchronized StudyEditContainer getStudyEditContainer()
    {
        if (ref == null) {
            ref = new StudyEditContainer();
            Log.d("StudyEditContainer", "create new Study");
        }

        Log.d("StudyEditContainer", "Container requested");

        return ref;
    }

    private static StudyEditContainer ref;

    public Study getStudy() {
        // Log.d("StudyEditContainer", "getStudy(): studyID=" + mStudy.getId() +
        // " Status(MPI)=" + metaDataComplete + "/" + pyramidComplete + "/" +
        // itemsComplete);
        return mStudy;
    }

    public void setStudy(Study study) {
        reset();
        mStudy = study;

        // Update the status of Study in the container
        if (study.getName() != null && !study.getName().isEmpty())
            metaDataComplete = true;

        if (study.getItems() != null && study.getPyramid() != null &&
                study.getItems().size() == study.getPyramid().getSize())
            itemsComplete = true;

        if (study.getPyramid() != null
                && study.getPyramid().getPoleLeft() != null
                && study.getPyramid().getPoleRight() != null
                && !study.getPyramid().getPoleLeft().isEmpty()
                && !study.getPyramid().getPoleRight().isEmpty()
                && study.getPyramid().isValid())
            pyramidComplete = true;

        List<Question> l = new ArrayList<Question>();
        l.addAll(study.getQuestions(Phase.QUESTIONS_PRE));
        l.addAll(study.getQuestions(Phase.QUESTIONS_POST));

        for (Question q : l) {
            if (!q.isConsistent())
            {
                questionsComplete = false;
                break;
            }
        }

    }

    public void reset() {
        Log.d("StudyEditContainer", "run reset");
        mStudy = new Study(-1);
        mStudy.setId(-1);
        mStudy.setName("");
        mStudy.setAuthor("");
        mStudy.setResearchQuestion("");
        mStudy.setDescription("");
        mStudy.setPyramid(new Pyramid(-1));
        mStudy.getPyramid().setPoleLeft("");
        mStudy.getPyramid().setPoleRight("");
        newStudy = true;
        loaded = false;
        isChanged = false;
        metaDataComplete = false;
        itemsComplete = false;
        pyramidComplete = false;
        questionsComplete = true;
        studies.clear();
        Log.d("StudyEditContainer", "Container cleared");
    }

    public List<String> getStudies() {
        return studies;
    }

    /**
     * Check if a study name is already in use
     * 
     * @param name
     * @return true if study name is already in use, false if not
     */
    @SuppressLint("DefaultLocale")
    public boolean checkDuplicateStudyName(String name) {
        // if the study name is already in use
        for (String s : studies)
            if (s.toLowerCase().equals(name.toLowerCase()))
                return true;

        return false;
    }

}
