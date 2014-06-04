
package de.eresearch.app.logic.model;

import de.eresearch.app.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that represents a single study.
 */
public class Study {

    /** The id of this study */
    private int mId;

    /** The name of this study */
    private String mName;

    /** The author of this study */
    private String mAuthor;

    /** The research question of this study */
    private String mResearchQuestion;

    /** A description of the study */
    private String mDescription;

    /** Items of the study */
    private List<Item> mItems;

    /** QSorts of the study */
    private List<QSort> mQSorts;

    /** Questions (pre) of the study */
    private List<Question> mQuestionsPre;

    /** Questions (post) of the study */
    private List<Question> mQuestionsPost;

    /** Initial Pyramid for QSorts of this study */
    private Pyramid mPyramid;

    /** Whether the study is complete (ready for QSorting) or not */
    private boolean mComplete;

    /**
     * Creates a new study with the given id.
     * 
     * @param id The id of the study
     */
    public Study(int id) {
        mId = id;

        mComplete = false;

        mItems = new ArrayList<Item>();
        mQSorts = new ArrayList<QSort>();

        mQuestionsPre = new ArrayList<Question>();
        mQuestionsPost = new ArrayList<Question>();
    }

    /**
     * Sets the id of this study. Also updates all study ids of the attached
     * items.
     * 
     * @param id The id of this study
     */
    public void setId(int id) {
        mId = id;

        for (Item i : mItems) {
            i.setStudyID(mId);
        }
    }

    /**
     * @return The id of this study
     */
    public int getId() {
        return mId;
    }

    /**
     * @param name The name of this study
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return The name of this study
     */
    public String getName() {
        return mName;
    }

    /**
     * @param author The author of this study
     */
    public void setAuthor(String author) {
        mAuthor = author;
    }

    /**
     * @return the author of this study
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @param researchQuestion The research question of this study
     */
    public void setResearchQuestion(String researchQuestion) {
        mResearchQuestion = researchQuestion;
    }

    /**
     * @return The research question of this study
     */
    public String getResearchQuestion() {
        return mResearchQuestion;
    }

    /**
     * @param description The description of this study
     */
    public void setDescription(String description) {
        mDescription = description;
    }

    /**
     * @return The description of this study
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Attaches an {@link Item} to this study. If this study already contains an
     * item with the same id, the item will be added another time. The study id
     * of the item will be set to the id of this study.
     * 
     * @param item The item to be attached to this study
     */
    public void addItem(Item item) {
        item.setStudyID(mId);
        mItems.add(item);
    }

    /**
     * Removes an {@link Item} (or multiple items) from this study. If there is
     * no item that matches the given id, nothing will happen. If multiple items
     * match the given id, all of them will be removed.
     * 
     * @param itemId The id of the item that should be removed
     */
    public void removeItem(int itemId) {
        Item item = null;

        for (Iterator<Item> it = mItems.iterator(); it.hasNext(); item = it.next()) {
            if (item.getId() == itemId) {
                it.remove();
            }
        }
    }

    /**
     * Returns a mutable {@link List} of all the {@link Item} objects attached
     * to this study. The order of items in the returned list complies with the
     * order in which they have been added to the study. The returned list can
     * contain multiple items with the same id.
     * 
     * @return The list of items
     */
    public List<Item> getItems() {
        return mItems;
    }

    /**
     * returns the item with the itemid
     * 
     * @param itemId
     * @return Item
     */
    public Item getItemById(int itemId) {
        for (Item i : mItems) {
            if (i != null && i.getId() == itemId) {
                return i;
            }
        }
        return null;
    }

    /**
     * overwrites complete list
     * 
     * @param items
     */
    public void setItems(List<Item> items) {
        this.mItems = items;
    }

    /**
     * Attaches a {@link QSort} to this study. If this study already contains a
     * QSort with the same id, the QSort will be added another time.
     * 
     * @param qsort The QSort to be attached to this study
     */
    public void addQSort(QSort qsort) {
        mQSorts.add(qsort);
    }

    /**
     * Removes a {@link QSort} (or multiple QSorts) from this study. If there is
     * no QSort that matches the given id, nothing will happen. If multiple
     * QSorts match the given id, all of them will be removed.
     * 
     * @param qsortId The id of the QSort that should be removed
     */
    public void removeQSort(int qsortId) {
        QSort qsort = null;

        for (Iterator<QSort> it = mQSorts.iterator(); it.hasNext(); qsort = it.next()) {
            if (qsort.getId() == qsortId) {
                it.remove();
            }
        }
    }

    /**
     * Returns a mutable {@link List} of all the {@link QSort} objects attached
     * to this study. The order of QSorts in the returned list complies with the
     * order in which they have been added to the study. The returned list can
     * contain multiple QSorts with the same id.
     * 
     * @return The list of QSorts
     */
    public List<QSort> getQSorts() {
        return mQSorts;
    }

    /**
     * Overwrites complete list
     * 
     * @param qsorts
     */
    public void setQSorts(List<QSort> qsorts) {
        this.mQSorts = qsorts;
    }

    /**
     * Adds a {@link Question} to the specified phase of this study. A question
     * can be added either to the {@link Phase#QUESTIONS_PRE} or the
     * {@link Phase#QUESTIONS_POST} phase. If another phase is passed an
     * {@link IllegalArgumentException} will be thrown. If this study already
     * contains a question with the same id, the question will be added again.
     * 
     * @param phase {@link Phase#QUESTIONS_PRE} or {@link Phase#QUESTIONS_POST}
     * @param question The question to be added
     * @throws IllegalArgumentException when an illegal {@link Phase} has been
     *             passed
     */
    public void addQuestion(Phase phase, Question question) {
        switch (phase) {
            case QUESTIONS_PRE:
                mQuestionsPre.add(question);
                break;

            case QUESTIONS_POST:
                mQuestionsPost.add(question);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Removes a {@link Question} from this study, regardless of which
     * {@link Phase} it belongs to. If there is no question that matches the
     * given id, nothing will happen. If multiple questions match the given id,
     * all of them will be removed.
     * 
     * @param questionId The id of the question that should be removed
     */
    public void removeQuestion(int questionId) {
        Question question = null;

        for (Iterator<Question> it = mQuestionsPre.iterator(); it.hasNext(); question = it.next()) {
            if (question.getId() == questionId) {
                it.remove();
            }
        }

        for (Iterator<Question> it = mQuestionsPost.iterator(); it.hasNext(); question = it.next()) {
            if (question.getId() == questionId) {
                it.remove();
            }
        }
    }

    /**
     * Returns a mutable {@link List} of all the {@link Question} objects
     * attached to a specific {@link Phase} of this study. The phase must be
     * {@link Phase#QUESTIONS_PRE} or {@link Phase#QUESTIONS_POST}, an
     * {@link IllegalStateException} is thrown else. The order of questions in
     * the returned list complies with the order in which they have been added
     * to the study. The returned list can contain multiple questions with the
     * same id.
     * 
     * @param phase {@link Phase#QUESTIONS_PRE} or {@link Phase#QUESTIONS_POST}
     * @return The list of questions
     * @throws IllegalArgumentException when an illegal {@link Phase} has been
     *             passed
     */
    public List<Question> getQuestions(Phase phase) {
        switch (phase) {
            case QUESTIONS_PRE:
                return mQuestionsPre;

            case QUESTIONS_POST:
                return mQuestionsPost;

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * overwrites complete list
     * 
     * @param phase
     * @param questions
     */
    public void setQuestions(Phase phase, List<Question> questions) {
        switch (phase) {
            case QUESTIONS_PRE:
                this.mQuestionsPre = questions;
                break;

            case QUESTIONS_POST:
                this.mQuestionsPost = questions;
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * overwrites both lists (pre and post questions) with these questions
     * 
     * @param phase
     * @param questions
     */
    public void setQuestions(List<Question> questions) {
        List<Question> pre = new ArrayList<Question>();
        List<Question> post = new ArrayList<Question>();

        for (Question question : questions) {
            if (question.isPost()) {
                post.add(question);
            } else {
                pre.add(question);
            }
        }

        setQuestions(Phase.QUESTIONS_POST, post);
        setQuestions(Phase.QUESTIONS_PRE, pre);
    }

    /**
     * Returns a {@link List} of all the {@link Question} objects attached to
     * this study. The order of QSorts in the returned list complies with the
     * order in which they have been added to the study (first pre questions,
     * then post questions). The returned list can contain multiple questions
     * with the same id.
     * 
     * @return The list of questions
     */
    public List<Question> getAllQuestions() {
        List<Question> allQuestions = new ArrayList<Question>();

        allQuestions.addAll(mQuestionsPre);
        allQuestions.addAll(mQuestionsPost);

        return allQuestions;
    }

    /**
     * returns the question with this id
     * 
     * @param id
     * @return question
     */
    public Question getQuestionById(int id) {
        List<Question> allQuestions = this.getAllQuestions();
        for (Question q : allQuestions) {
            if (q != null && q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    /**
     * @param pyramid The pyramid template for this study
     */
    public void setPyramid(Pyramid pyramid) {
        mPyramid = pyramid;
    }

    /**
     * @return The pyramid template for this study
     */
    public Pyramid getPyramid() {
        return mPyramid;
    }

    /**
     * A study is regarded as <i>complete</i>, when the following aspects are
     * well configured: <li>study name</li> <li>pyramid form</li> <li>
     * corresponding amount of items</li>
     * 
     * @param complete <code>true</code>, when this study is complete and QSorts
     *            can be started, <code>false</code> else
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    /**
     * Checks wether this study is complete and ready to run QSorts. A study is
     * regarded as <I>complete</I>, when the following aspects are well
     * configured: <LI>study name</LI> <LI>pyramid form</LI> <LI>corresponding
     * amount of items</LI>. This check is only significant if this study object
     * contains all the needed information: the pyramid, the items and the
     * metadata.
     * 
     * @return <code>true</code>, when this study is complete and QSorts can be
     *         started, <code>false</code> else
     */
    public boolean isComplete() {
        return mComplete;
    }

    /**
     * Checks wether this study is complete and ready to run Q-Sorts. After the
     * call of this method the status can be polled by calling
     * {@link Study#isComplete()}.
     */
    public void checkComplete() {
        mComplete = true;

        if (mName == null || mName.equals("")) {
            mComplete = false;
        }

        if (mPyramid == null || mItems == null) {
            mComplete = false;
        }

        if (!mPyramid.isValid()) {
            mComplete = false;
        }

        if (mPyramid.getSize() != mItems.size()) {
            mComplete = false;
        }

        if (mPyramid.getPoleLeft() == null || mPyramid.getPoleLeft().equals("")
                || mPyramid.getPoleRight() == null || mPyramid.getPoleRight().equals("")) {
            mComplete = false;
        }

        List<Question> l = new ArrayList<Question>();
        l.addAll(getQuestions(Phase.QUESTIONS_PRE));
        l.addAll(getQuestions(Phase.QUESTIONS_POST));        
        for (Question q : l) {
            if (!q.isConsistent())
            {
                mComplete = false;               
                break;
            }            
                
        }
    }    
    
    /**
     * @return The string representation (name) of this study
     */
    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Study)) {
            return false;
        }

        Study other = (Study) o;

        if (mId != other.mId) {
            return false;
        }

        if (mComplete != other.mComplete) {
            return false;
        }

        if (!Util.nullSafeEquals(mName, other.mName)) {
            return false;
        }

        if (!Util.nullSafeEquals(mAuthor, other.mAuthor)) {
            return false;
        }

        if (!Util.nullSafeEquals(mResearchQuestion, other.mResearchQuestion)) {
            return false;
        }

        if (!Util.nullSafeEquals(mDescription, other.mDescription)) {
            return false;
        }

        if (!Util.nullSafeEquals(mItems, other.mItems)) {
            return false;
        }

        if (!Util.nullSafeEquals(mQSorts, other.mQSorts)) {
            return false;
        }

        if (!Util.nullSafeEquals(mQuestionsPre, other.mQuestionsPre)) {
            return false;
        }

        if (!Util.nullSafeEquals(mQuestionsPost, other.mQuestionsPost)) {
            return false;
        }

        if (!Util.nullSafeEquals(mPyramid, other.mPyramid)) {
            return false;
        }

        return true;
    }

}
