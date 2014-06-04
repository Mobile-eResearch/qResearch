package de.eresearch.app.db.helper;

/**
 * Interface for all Helper for model object
 * which have a QSort id as a foreign key
 * @author Jurij Wöhlke
 * @param <T>
 */
public interface QSortObjectHelperInterface<T> {

    /**
     * returns an array of the object associatet with this qsort id
     * for example all Answers for this QSortId in class AnswerHelper
     * @param id
     * @return <T>[]
     */
     public T[] getAllByQSortId(int id);
}
