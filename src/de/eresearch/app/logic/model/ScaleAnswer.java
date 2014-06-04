
package de.eresearch.app.logic.model;

import java.util.List;

public class ScaleAnswer extends Answer {
    /**
     * List of scales refers to this answer
     */
    private List<Scale> scaleList;

    /**
     * Creates a new scale answer with the given id.
     * 
     * @param id The id of this answer
     */
    public ScaleAnswer(int id) {
        super(id);
    }
    
    /**
     * Creates a new scale answer with the given id.
     * 
     * @param id The id of this answer
     * @param id The id of the question
     * @param id The id of the qsort
     */
    public ScaleAnswer(int id,ScaleQuestion question,int qsortId) {
       super(id, question, qsortId);
    }

    /**
     * Returns a list with all the scales of the corresponding question.
     * 
     * @return The list
     */
    public List<Scale> getScales() {
        return scaleList;
    }

    /**
     * @param scales The scales, see {@link ScaleAnswer#getScales()}
     */
    public void setScales(List<Scale> scales) {
        scaleList = scales;
    }
    
    /**
     * Check if Question is Answered
     * @return
     */
    public boolean isAnswered(){
        for (Scale s: scaleList)
            if (s.getSelectedValueIndex() >= 0)
                return true;
        return false;
    }
}
