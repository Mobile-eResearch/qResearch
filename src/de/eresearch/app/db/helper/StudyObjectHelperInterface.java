package de.eresearch.app.db.helper;

/**
 * Interface to implement getAllByStudyId in 
 * ObjectHelper which have a StudyId as foreign key
 * @author Jurij Wöhlke
 * @param <T>
 */
public interface StudyObjectHelperInterface<T> {

    /**
     * returns an array of the object associated with this study id
     * for example all QSorts for this StudyId in class QSortHelper
     * @param id
     * @return <T>[]
     */
    public T[] getAllByStudyId(int id);
}
