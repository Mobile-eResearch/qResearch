
package de.eresearch.app.logic.model;

import android.content.Context;

import de.eresearch.app.R;

public abstract class Answer extends Loggable {

    /** Answer id */
    private int mId;

    /** Answer's question */
    private Question mQuestion;

    /** Answer's qsortid */
    private int mQSortId;

    /**
     * Creates a new answer with a given id.
     * 
     * @param id The id of this answer
     */
    public Answer(int id) {
        this.mId = id;
        this.mQuestion = null;
        this.mQSortId = -1;
    }

    /**
     * constructor for initialization with qsort id and question id
     * 
     * @param id
     * @param questionId
     * @param qsortId
     */
    public Answer(int id, Question question, int qsortId) {
        this.mId = id;
        this.mQuestion = question;
        this.mQSortId = qsortId;
    }

    /**
     * @param id The id of this answer
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return The id of this answer
     */
    public int getId() {
        return mId;
    }

    /**
     * @return the question id
     */
    public Question getQuestion() {
        return mQuestion;
    }

    /**
     * sets the question id
     * 
     * @param mQuestionId
     */
    public void setQuestion(Question question) {
        this.mQuestion = question;
    }

    /**
     * @return qsort id for this answer
     */
    public int getQSortId() {
        return mQSortId;
    }

    /**
     * sets the qsortid
     * 
     * @param mQSortId
     */
    public void setQSortId(int mQSortId) {
        this.mQSortId = mQSortId;
    }

    @Override
    public String toString(Context context) {
        boolean questionAnswerd = true;
        if (this instanceof OpenAnswer) {
            if (((OpenAnswer) this).getAnswer().toString().isEmpty())
                questionAnswerd = false;
        }
        else if (this instanceof ClosedAnswer) {
            if (!((ClosedAnswer) this).isAnswered())
                questionAnswerd = false;
        }
        else if (this instanceof ScaleAnswer) {
            if (!((ScaleAnswer) this).isAnswered())
                questionAnswerd = false;
        }
        
        String toString="";
        if(questionAnswerd)
            toString = context.getString(R.string.loggable_answer, mQuestion.getText());
        else
            toString = context.getString(R.string.loggable_not_answer, mQuestion.getText());
        
        return toString;
    }

}
