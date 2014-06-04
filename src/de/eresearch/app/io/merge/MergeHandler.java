
package de.eresearch.app.io.merge;

import de.eresearch.app.logic.model.Study;

import java.util.Collection;

/**
 * The MergeHandler is used to merge {@link Study}-objects.
 */
public class MergeHandler {
    
    /**
     * Collection of studies to merge
     */
    private Collection<Study> mStudies;
    
    /**
     * Name of the new study
     */
    private String mName;
    
    /**
     * Study to fill
     */
    private Study mStudy;
    
    public MergeHandler() {

    }
    
    /**
     * Merges a {@link Collection} of {@link Study}-objects and creates a new {@link Study} with the name given by the parameters.
     * 
     * @param studies to be merged
     * @param name of the Study
     * @return true if the merge was successful, else false
     */
    public boolean createMergedStudy(Collection<Study> studies, String name) throws UnknownError{
        return false;
    }
    
    /**
     * Merges mStudies's QSorts in mStudy 
     * 
     * @throws UnmergeableException
     */
    private void mergeQSorts() throws UnmergeableException{
        
    }
    
    /**
     * Merges mStudies's Questions in mStudy
     * 
     * @throws UnmergeableException
     */
    private void mergeQuestions() throws UnmergeableException{
        
    }
    
    /**
     * Merges mStudies's Items in mStudy
     * 
     * @throws UnmergeableException
     */
    private void mergeItems() throws UnmergeableException{
        
    }

}
