
package de.eresearch.app.logic.model;

public class OpenAnswer extends Answer {
    
    /** Answer to the referencing OpenQuestion*/
    private String oAnswer;

    /**
     * Creates a new open answer with the given id.
     * 
     * @param id The id of this answer
     */
    public OpenAnswer(int id) {
        super(id);
    }
    
    /**
     * Creates a new open answer with the given id.
     * 
     * @param id The id of this answer
     * @param id The id of the question
     * @param id The id of the qsort
     */
    public OpenAnswer(int id,OpenQuestion question,int qsortId) {
        super(id, question, qsortId);
    }

    /**
     * @return the answer provided by the user
     */
    public String getAnswer() {
        return oAnswer;
    }

    /**
     * @param answer the answer provided by the user
     */
    public void setAnswer(String answer) {
        oAnswer = answer;
    }
}
