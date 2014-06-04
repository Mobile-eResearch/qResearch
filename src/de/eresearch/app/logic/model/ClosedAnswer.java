                                                                                               
package de.eresearch.app.logic.model;

import java.util.ArrayList;
import java.util.List;

public class ClosedAnswer extends Answer {
        
    /** List of answers which refers to this answer*/
    private List<String> mAnswersList;

    /**
     * Creates a new closed answer with the given id.
     * 
     * @param id The id of this closed answer
     */
    public ClosedAnswer(int id) {
        super(id);
        this.mAnswersList = new ArrayList<String>();
    }
    
    /**
     * Creates a new closed answer with the given id.
     * 
     * @param id The id of this answer
     * @param id The id of the question
     * @param id The id of the qsort
     */
    public ClosedAnswer(int id,ClosedQuestion question,int qsortId) {
        super(id, question, qsortId);
    }

    /**
     * Returns a list of answers (as strings) that have been selected by
     * answering the question. If the corresponding {@link ClosedQuestion} does
     * not allow multiple answers, the returned list will always have the size
     * 1. If no answers have been selected, an empty list will be returned.
     * 
     * @return A list containing the selected answers
     */
    public List<String> getAnswers() {
        ClosedQuestion question=(ClosedQuestion)this.getQuestion();
        if(question.isMultipleChoice() == false){
            if (mAnswersList.isEmpty())
                return mAnswersList;
            return mAnswersList.subList(0, 1);
        }
        return mAnswersList;
    }

    /**
     * @param answers A list with all the answers selected in the corresponding
     *            {@link ClosedQuestion}, as strings
     */
    public void setAnswers(List<String> answers) {
        mAnswersList = answers;
    }
    
    /**
     * Add a answer to the list of answers
     * @param s a answer as String
     */
    public void addAnswer(String s){
        mAnswersList.add(s);
    }
   
    /**
     * Remove a answer from the list of answers
     * @param s a answer as String
     */
    public void removeAnswer(String s){
        String result = null;
        for (String e: mAnswersList){
            if (e.equals(s))
                result = e;
        }
        if (result != null)
            mAnswersList.remove(result);
    }
    
    /**
     * Clear the answer list
     */
    public void clear(){
        mAnswersList.clear();
    }
    
    /**
     * Check if Question is Answered
     * @return
     */
    public boolean isAnswered(){
        if (mAnswersList.size() > 0)
            return true;
       return false;
    }
}
