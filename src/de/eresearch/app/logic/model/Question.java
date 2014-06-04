
package de.eresearch.app.logic.model;

/**
 * @author Tammo
 */
public abstract class Question {

    /** Question's Id */
    private int mId;

    /** Question's text */
    private String mText;
    
    /** Question's order number*/
    private int mOrderNumber;
    
    /** Question's studyid*/
    private int mStudyId;
    
    /** if true, then this question is post qsorting*/
    private boolean mIsPost;
    
    /**
     * The answer
     * 
     * (null if there is no answer)
     */
    protected Answer mAnswer;
   

    /**
     * Creates a new question with the given id.
     * 
     * @param id The id of this question
     * @param id The id of this study
     */
    public Question(int id, int studyid) {
        this.mId = id;
        this.mStudyId=studyid;
    }

    /**
     * @param id The id of this log entry
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return the id of this question
     */
    public int getId() {
        return mId;
    }
    
    /**
     * returns studyid for question
     * @return int studyId
     */
    public int getStudyId() {
        return mStudyId;
    }

    /**
     * sets the studyid for this questions
     * @param mStudyId
     */
    public void setStudyId(int mStudyId) {
        this.mStudyId = mStudyId;
    }
    
    /**
     * returns orderNumber for question
     * @return int orderNumber
     */
    public int getOrderNumber() {
        return mOrderNumber;
    }

    /**
     * sets orderNumber for question
     * @param mOrderNumber
     */
    public void setOrderNumber(int mOrderNumber) {
        this.mOrderNumber = mOrderNumber;
    }

    /**
     * returns true if question is after qsorting
     * @return boolean isPost
     */
    public boolean isPost() {
        return mIsPost;
    }

    /**
     * set true if this question is post sorting else false
     * @param mIsPost
     */
    public void setIsPost(boolean mIsPost) {
        this.mIsPost = mIsPost;
    }

    /**
     * @param text The text of this question
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * @return the text of this question
     */
    public String getText() {
        return mText;
    }

    @Override
    public String toString() {
        return mText;
    }

    public Answer getAnswer() {
        return mAnswer;
    }

    public void setAnswer(Answer mAnswer) {
        this.mAnswer = mAnswer;
    }
    public abstract boolean isConsistent();
}
