
package de.eresearch.app.logic.model;

public class OpenQuestion extends Question {

    /**
     * Creates a new open question with the given id.
     * 
     * @param id the id of this question
     * @param studyid of this question
     */
    public OpenQuestion(int id, int studyid) {
        super(id, studyid);
    }

    @Override
    public boolean isConsistent() {
        if (this.getText().length() < 1)
        {
            return false;
        }
        return true;
    }

}
