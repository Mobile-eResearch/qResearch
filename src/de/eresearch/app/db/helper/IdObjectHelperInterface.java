package de.eresearch.app.db.helper;

/**
 * Interface for model objects with identifier
 * @author Jurij Wöhlke
 * @param <T>
 */
public interface IdObjectHelperInterface<T> {
    
    /**
     * returns object for id
     * @param id
     * @return <T>
     */
    public T getObjectById(int id);
    
    /**
     * returns true if row/object was successfully deleted
     * @param id
     * @return boolean
     */
    public boolean deleteById(int id);
}
