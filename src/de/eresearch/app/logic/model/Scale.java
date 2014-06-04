
package de.eresearch.app.logic.model;

import java.util.ArrayList;
import java.util.List;

public class Scale {

    /** id of the scale */
    private int mid;

    /** left pole of the scale */
    private String leftPole;

    /** right pole of the scale */
    private String rightPole;

    /** List of scaleValues*/
    private List<String> scaleValueList;
    
    /** Index of the selected value */
    private int selected = -1;
    
    /** ID of the Question which has this scale*/
    private int questionId=-1;
    
    /** Only for DB Saving purpose
     * this is for scaleHelper and the scaleQuestionHelper in the database package only!
     */
    private int dbOrder=-1;
    
    /**
     * Creates a new scale with the given id.
     * 
     * @param id The id of the scale
     */
    public Scale(int id) {
        this.mid = id;
        this.scaleValueList=new ArrayList<String>();
    }

    /**
     * @param id The id of the scale
     */
    public void setId(int id) {
        mid = id;
    }

    /**
     * @return The id of the scale
     */
    public int getId() {
        return mid;
    }

    /**
     * @param pole the left pole of the scale as string
     */
    public void setPoleLeft(String pole) {
        this.leftPole = pole;
    }

    /**
     * @return the left pole of the scale as string
     */
    public String getPoleLeft() {
        return this.leftPole;
    }

    /**
     * @param pole the right pole of the scale as string
     */
    public void setPoleRight(String pole) {
        this.rightPole = pole;
    }

    /**
     * @return the right pole of the scale as string
     */
    public String getPoleRight() {
        return this.rightPole;
    }

    /**
     * Adds a value to this scale. If there already is a value that equals the
     * passed one, nothing will happen.
     * 
     * @param value The value
     */
    public void addScaleValue(String value) {
            scaleValueList.add(value);
        
    }

    /**
     * @param value A value do be deleted from this scale
     */
    public void deleteScaleValue(String value) {
            scaleValueList.remove(value);
    }

    /**
     * @return A list of all scale values in the order in which they have been
     *         added
     */
    public List<String> getScaleValues() {
        return scaleValueList;
    }

    /**
     * @param index The list index of the value that is selected
     */
    public void setSelectedValueIndex(int index) {
        selected = index;
    }

    /**
     * @return The list index of the value that is selected (-1 if none is
     *         selected)
     */
    public int getSelectedValueIndex() {
        return selected;
    }

    /**
     * @return the (scale) question id for this scale
     */
    public int getQuestionId() {
        return questionId;
    }

    /**
     * sets the question id for this scale
     * @param questionId
     */
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    /**
     * this is for scaleHelper and the scaleQuestionHelper in the database package only!
     * returns the number of the order for the database
     * @return
     */
    public int getDBInternalOrderOfScales() {
        return dbOrder;
    }

    /**
     * this is for scaleHelper and the scaleQuestionHelper in the database package only!
     * sets the order for the database to save the scales
     * @param order
     */
    public void setDBInternalOrderOfScales(int order) {
        this.dbOrder = order;
    }

}
