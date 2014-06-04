
package de.eresearch.app.gui;

import android.os.Bundle;

import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.tasks.common.questions.GetQuestionsTask.Callbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class for QSort questions
 * 
 * @author Henrik
 */

public class QSortQuestionContainer implements Callbacks {

    public List<Question> containerList;
    private static QSortQuestionContainer ref;
    public int location;
    private boolean isPost;
    private Bundle mExtras;
    public List<Question> filteredList;

    private QSortQuestionContainer() {
        location = 0;
    }

    public static synchronized QSortQuestionContainer getQSortQuestionContainer()
    {
        if (ref == null) {
            ref = new QSortQuestionContainer();
        }
        return ref;
    }

    public void setContainer(List<Question> cList) {
        containerList = new ArrayList<Question>();
        if (cList != null) {
            containerList = cList;
        }
        setfilteredLocations();
    }

    private void setfilteredLocations() {
        filteredList = new ArrayList<Question>();
        for (Question q : containerList) {
            if (q.isPost() == this.isPost) {
                filteredList.add(q);
            }
        }

    }

    public List<Question> getContainer() {
        return containerList;
    }

    @Override
    public void onGetQuestionTaskUpdate(List<Question> questions) {
        this.containerList = questions;

    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean isPost) {
        this.isPost = isPost;
    }

    public Bundle getmExtras() {
        return mExtras;
    }

    public void setmExtras(Bundle mExtras) {
        this.mExtras = mExtras;
    }
    
    public void reset(){
        containerList = new ArrayList<Question>();
        location = 0;
    }

}
