package de.eresearch.app.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.tables.StudiesTable;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Study;

/**
 * Study model object related helper
 * @author Jurij Wöhlke
 */
public class StudyHelper extends AbstractObjectHelper<Study> implements IdObjectHelperInterface<Study>{

        
    /**
     * Constructor
     * @param dbconn
     */
    public StudyHelper(final DatabaseConnector dbconn) {
        super(dbconn);
    }

    // ################# AbstractObjectHelper methods #################
    /**
     * saves a study object
     * @param Study
     * @Override
     */
    public Study saveObject(Study study){
        Study res=this.saveMetaData(study);
        try{
            if(res!=null && res.getId()>0){
                //saveItems
                if(study.getItems()!=null && !study.getItems().isEmpty()){
                    ItemHelper itemHelper = (ItemHelper)dbc.getHelper(DatabaseConnector.TYPE_ITEM);
                    Item[] oldItems=itemHelper.getAllByStudyId(res.getId());

                    List<Item> items=study.getItems();
                    if(oldItems!=null && oldItems.length>0){
                        //delete items which do not exist anymore:
                        for(int j=0;j<oldItems.length;j++){
                            boolean stillExists=false;
                            for(int k=0;k<items.size();k++){
                                if(oldItems[j].getId()==items.get(k).getId()){
                                    stillExists=true;
                                }
                            }
                            if(!stillExists && oldItems[j]!=null){
                                itemHelper.deleteObject(oldItems[j]);
                            }
                        }
                    }
                    
                    //update:
                    for(int i=0;i<items.size();i++){
                        Item item=items.get(i);
                        if(item.getStudyID()<=0){
                            item.setStudyID(res.getId());
                        }
                        item=itemHelper.saveObject(item);
                        if(item!=null){
                            items.set(i, item);
                        }else{
                            return null;
                        }
                    }
                    res.setItems(items);
                }
                
                //savePyramids
                if(study.getPyramid()!=null){
                    final PyramidHelper pyramidHelper = (PyramidHelper)dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
                    if(study.getPyramid()!=null && study.getPyramid().getId()<0){
                        study.getPyramid().setId(res.getId());
                    }
                    res.setPyramid(pyramidHelper.saveObject(study.getPyramid()));
                }
                
                //saveQSorts
                if(study.getQSorts()!=null && !study.getQSorts().isEmpty()){
                    QSortHelper qsortHelper = (QSortHelper)dbc.getHelper(DatabaseConnector.TYPE_QSORT);
                    List<QSort> qsorts= study.getQSorts();
                    for(int j=0;j<qsorts.size();j++){
                        QSort qsort=qsorts.get(j);
                        if(qsort.getStudyId()<=0){
                            qsort.setStudyId(res.getId());
                        }
                        qsort=qsortHelper.saveObject(qsort);
                        if(qsort!=null){
                            qsorts.set(j,qsort);
                        }
                    }
                    res.setQSorts(qsorts);
                }
                
                //saveQuestions
                if(study.getAllQuestions()!=null && !study.getAllQuestions().isEmpty()){
                    QuestionHelper questionHelper = (QuestionHelper)dbc.getHelper(DatabaseConnector.TYPE_QUESTION);
                    Question[] oldQuestions=questionHelper.getAllByStudyId(res.getId());                    
                    List<Question> questions= study.getAllQuestions();
                    
                    if(oldQuestions!=null && oldQuestions.length>0){
                        //delete questions which do not exist anymore:
                        for(int j=0;j<oldQuestions.length;j++){
                            boolean stillExists=false;
                            for(int k=0;k<questions.size();k++){
                                if(oldQuestions[j].getId()==questions.get(k).getId()){
                                    stillExists=true;
                                }
                            }
                            if(!stillExists && oldQuestions[j]!=null){
                                questionHelper.deleteObject(oldQuestions[j]);
                            }
                        }
                    }
                    //save/update questions
                    for(int m=0;m<questions.size();m++){
                        Question question=questions.get(m);
                        if(question.getStudyId()<=0){
                            question.setStudyId(res.getId());
                        }
                        question=questionHelper.saveObject(question);
                        if(question!=null){
                            questions.set(m,question);
                        }
                    }
                    res.setQuestions(questions);
                }
            }
            return res;
        } catch (HelperNotFoundException e) {
            return null;
        }
    }

    /**
     * returns a fresh study object
     * @param Study
     * @return study
     * @Override
     */
    public Study refreshObject(Study study){
        return getObjectById(study.getId());
    }

    /**
     * deletes study from database
     * @param Study
     * @return boolean
     * @Override
     */
    public boolean deleteObject(Study study){
        if(study.getId()>0){
            return this.deleteById(study.getId());
        }else{
            return false;
        }
    }
    
    // ################# IdObjectHelperInterface methods #################
    
    /**
     * if there is a Study with this identifier, this function returns it. Otherwise it returns null
     * @param int identifier
     * @return Study
     * @Override
     */
    public Study getObjectById(final int identifier) {
        Study study=null;
        final Cursor cursor=this.database.query(StudiesTable.TABLE_STUDIES, StudiesTable.ALL_COLUMNS, StudiesTable.COLUMN_ID+"="+identifier, null, null, null, null);
        if(cursor.moveToFirst()){
            study=new Study(cursor.getInt(cursor.getColumnIndex(StudiesTable.COLUMN_ID)));
            study.setName(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_NAME)));
            study.setDescription(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_DESCRIPTION)));
            study.setResearchQuestion(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_RESEARCH_QUESTION)));
            study.setAuthor(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_AUTHOR)));
            study.setComplete(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_IS_COMPLETE))));
            //TODO: lastEdited and created for study?
            
            //setItems
            try {
                ItemHelper itemHelper = (ItemHelper)dbc.getHelper(DatabaseConnector.TYPE_ITEM);
                final Item[] items=itemHelper.getAllByStudyId(study.getId());
                if(items!=null && items.length>0){
                    for(final Item item : items)
                        study.addItem(item);
                }
            } catch (HelperNotFoundException e) {
                return null;
            }
            
            //setPyramids
            try {
                final PyramidHelper pyramidHelper = (PyramidHelper)dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
                study.setPyramid(pyramidHelper.getObjectById(study.getId()));
            } catch (HelperNotFoundException e) {
                return null;
            }
            
            //setQSorts
            try {
                QSortHelper qsortHelper = (QSortHelper)dbc.getHelper(DatabaseConnector.TYPE_QSORT);
                final QSort[] qsorts=qsortHelper.getAllByStudyId(study.getId());
                if(qsorts!=null && qsorts.length>0){
                    for(final QSort qsort : qsorts)
                        study.addQSort(qsort);
                }
            } catch (HelperNotFoundException e) {
                return null;
            }
            
            //setQuestions
            try {
                QuestionHelper questionHelper = (QuestionHelper)dbc.getHelper(DatabaseConnector.TYPE_QUESTION);
                final Question[] questions=questionHelper.getAllByStudyId(study.getId());
                if(questions!=null && questions.length>0){
                    for(final Question question : questions){
                        if(question!=null){
                            if(question.isPost()){
                                study.addQuestion(Phase.QUESTIONS_POST,question);
                            }else{
                                study.addQuestion(Phase.QUESTIONS_PRE,question);
                            }
                        }else{
                            Log.e("StudyHelper", "Question is null!");
                        }
                    }
                }
            } catch (HelperNotFoundException e) {
                return null;
            }
            //TODO: and load other if needed
            
        }
        cursor.close();
        return study;
    }

    /**
     * deletes a study from database and returns true if it was successful
     * @param int identifier
     * @return boolean
     * @Override
     */
    public boolean deleteById(final int identifier) {
        boolean result=true;
        try {
            final Study study = this.getObjectById(identifier);
            
            if(study!=null){
                //deleteQSorts
                final QSortHelper qsortHelper = (QSortHelper)dbc.getHelper(DatabaseConnector.TYPE_QSORT);
                final List<QSort> qsorts=study.getQSorts();
                for(final QSort qsort:qsorts){
                    if(!(qsortHelper.deleteObject(qsort) && result)){
                        Log.e("StudyHelper", "qsort delete failed");
                        result=false;
                    }
                }
                
                //deletePyramids
                final PyramidHelper pyramidHelper = (PyramidHelper)dbc.getHelper(DatabaseConnector.TYPE_PYRAMID);
                if(!(pyramidHelper.deleteObject(study.getPyramid()) && result)){
                    result=false;
                }
                
                //deleteItems
                ItemHelper itemHelper = (ItemHelper)dbc.getHelper(DatabaseConnector.TYPE_ITEM);
                List<Item> items=study.getItems();
                for(final Item item : items){
                    if(!(itemHelper.deleteObject(item) && result)){
                        result=false;
                    }
                }
                
                //deleteQuestions
                QuestionHelper questionHelper = (QuestionHelper)dbc.getHelper(DatabaseConnector.TYPE_QUESTION);
                List<Question> questions=study.getAllQuestions();
                for(final Question question : questions){
                    if(!(questionHelper.deleteObject(question) && result)){
                        result=false;
                    }
                }
            }else{
                result=false;
            }
            
            int res=this.database.delete(StudiesTable.TABLE_STUDIES, StudiesTable.COLUMN_ID+"="+identifier, null);
            if(result && res>=0)
                result=true;
        } catch (HelperNotFoundException e) {
            result = false;
        }
        return result;
    }
    
    // ################# additional methods ################# 
    /**
     * returns an array of Study objects with id and names
     * @return Study[]
     */
    public Study[] getAllStudyWithIdAndNames(){
        String[] columns={StudiesTable.COLUMN_ID,StudiesTable.COLUMN_NAME,StudiesTable.COLUMN_IS_COMPLETE};
        Cursor cursor=this.database.query(StudiesTable.TABLE_STUDIES,columns,null, null, null, null, null);
        Study[] result=null;
        if(cursor.getCount()>0){
            result=new Study[cursor.getCount()];
            int i=0;
            Study study;
            while(i<cursor.getCount()){
                if(cursor.moveToNext()){
                    study=new Study(cursor.getInt(cursor.getColumnIndex(StudiesTable.COLUMN_ID)));
                    study.setName(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_NAME)));
                    study.setComplete(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_IS_COMPLETE))));
                    result[i]=study;
                }else{
                    break;
                }
                i++;
            }
        }
        cursor.close();
        return result;
    }
    
    /**
     * searches in study names for this keyword and returns study objects with id and name
     * @param keyword
     * @return Study[]
     */
    public Study[] searchStudyNames(final String keyword){
        final String[] columns={StudiesTable.COLUMN_ID,StudiesTable.COLUMN_NAME,StudiesTable.COLUMN_IS_COMPLETE};
        final Cursor cursor=this.database.query(StudiesTable.TABLE_STUDIES,columns,StudiesTable.COLUMN_NAME+" like '%"+keyword+"%'",null, null, null, null);
        //iterate through cursor
        Study[] result=null;
        if(cursor.getCount()>0){
            result=new Study[cursor.getCount()];
            int i=0;
            Study study;
            while(i<cursor.getCount()){
                if(cursor.moveToNext()){
                    study=new Study(cursor.getInt(cursor.getColumnIndex(StudiesTable.COLUMN_ID)));
                    study.setName(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_NAME)));
                    study.setComplete(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(StudiesTable.COLUMN_IS_COMPLETE))));
                    result[i]=study;
                }else{
                    result=null;
                    break;
                }
                i++;
            }
        }
        cursor.close();
        return result;
    }
    
    /**
     * saves the MetaData for a study
     * @param study
     * @return study
     */
    public Study saveMetaData(Study study){
        if(study!=null){
            ContentValues cvalues=new ContentValues();
            cvalues.put(StudiesTable.COLUMN_NAME,study.getName());
            cvalues.put(StudiesTable.COLUMN_RESEARCH_QUESTION,study.getResearchQuestion());
            cvalues.put(StudiesTable.COLUMN_DESCRIPTION,study.getDescription());
            cvalues.put(StudiesTable.COLUMN_AUTHOR,study.getAuthor());
            cvalues.put(StudiesTable.COLUMN_LAST_EDITED,"datetime()");
            cvalues.put(StudiesTable.COLUMN_IS_COMPLETE,Boolean.toString(study.isComplete()));
            //TODO:other values?
            if(study.getId()<=0){
                long newId = this.database.insert(StudiesTable.TABLE_STUDIES, null, cvalues);
                if(newId>0){
                    study.setId((int)newId);
                    return study;
                }else{
                    return null;
                }
            }else{
                String[] whereArgs=new String[1];
                whereArgs[0]=Integer.toString(study.getId());
                this.database.update(StudiesTable.TABLE_STUDIES, cvalues, StudiesTable.COLUMN_ID+"=?",whereArgs);
                return study;
            }
        }
        return null;
    }
    
    
    // ################# getter and setter ################# 
    @Override
    public DatabaseConnector getDbc() {
        return dbc;
    }

    @Override
    public void setDbc(DatabaseConnector dbconn) {
        this.dbc=dbconn;
    }
}
