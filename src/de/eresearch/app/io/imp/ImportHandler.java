
package de.eresearch.app.io.imp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.SaveFullStudyTask.Callbacks;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *         The ImportHandler is used to import studies({@link Study})
 *         and templates of studies from the external storage of the android
 *         device to the internal storage folder and the database of the Qapp
 *         application.
 *         
 *         @author liqquid
 *         @author Jurij Wöhlke
 */
public class ImportHandler implements Callbacks {

    public ImportHandler() {

    }

    /**
     * This method provides a String[] of studies or templates, on the external
     * storage, which can be imported to the application.
     * 
     * @return Returns a String[] with the names of available studies or
     *         templates
     */
    public String[] getNamesOfAvailableStudies() {
        // The created Files are the directorys containing the studies
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), "qResearch");
        File importDirectory = new File(mainDirectory, "Export");
        // Adds the names of the Files to a String array and returns it
        String[] fileNames = importDirectory.list();
        return fileNames;
    }

    /**
     * This method get's the chosen {@link Study} by it's studyName (String)
     * from the external storage and copy it to the application and it's
     * database.
     * 
     * @param String The name (String) of the {@link Study} which should be
     *            imported
     * @param String The new Name of the Study which should be displayed in the
     *            app
     * @return true if the {@link Study} was successfully imported.
     * @throws IOException
     */
    public Study copyStudyToApp(String studyName, String newStudyName, Context context)
            throws IOException {
        // Gives the new studyName the value of the old, if the new is empty
        if (newStudyName.isEmpty()) {
            newStudyName = studyName;
        }
        // Createse Files representing the directorys an the external storage
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), "qResearch");
        File importDirectory = new File(mainDirectory, "Export");
        File studyFolder = new File(importDirectory, studyName);
        File xmlFile = new File(studyFolder, studyName + ".xml");
        // Should create study Object from xml file
        Study studyObject = createStudyFromXML(xmlFile, newStudyName);
        if (studyObject == null) {
            throw new IOException();
        }
        // writes template study to db even without items
        File itemFolder = new File(studyFolder, "Items");
        if (!itemFolder.exists()) {
            return studyObject;
        }
        
        // ############################################## Items ##############################################
        // gets Items from folder
        String[] listOfItems = itemFolder.list();
        List<Item> importedItems=studyObject.getItems();
        for (int i = 0; i < listOfItems.length; i++) {
            // This need to be changed if something else than a picture should be imported
            File itemFile = new File(itemFolder, listOfItems[i]);
            String absoluteItemFilePath = itemFile.getAbsolutePath();
            for(int p=0;p<importedItems.size();p++){
                Picture pictureItem=(Picture) importedItems.get(p);
                String[] oldFilePathSplitted=pictureItem.getFilePath().split("/");
                if(oldFilePathSplitted.length>0){
                    //compare old and new filenames:
                    if(itemFile.getName().equals(oldFilePathSplitted[oldFilePathSplitted.length-1])){
                        pictureItem.setFilePath(absoluteItemFilePath);
                        // adds item to study
                        importedItems.set(p, pictureItem);
                    }
                }
            }
        }
        //save list:
        studyObject.setItems(importedItems);
        
     // ############################################## QSorts ##############################################
        // writes template study to db with items but without qsorts
        File qsortFolder = new File(studyFolder, "QSorts");
        if (!qsortFolder.exists()) {
            return studyObject;
        }
        // gets list of qsorts from the qsort folder
        String[] listOfQsorts = qsortFolder.list();
        for (int i = 0; i < listOfQsorts.length; i++) {
            // ignores PQmethod folder
            if (listOfQsorts[i].equals("PQMethod")) {
                // Do nothing
            } else {
                File qSort = new File(qsortFolder, listOfQsorts[i]);
                
                //load qsorts from xmlimport and refresh filepaths:
                List <QSort> qSortsXMLImp=studyObject.getQSorts();
                
                for(int q=0;q<qSortsXMLImp.size();q++){
                    QSort tmpQSort=qSortsXMLImp.get(q);
                    
                    //if qsortname is the same as qsort folder name 
                    if(tmpQSort.getName().equals(qSort.getName())){                        
                        //get audio record file names:
                        String[] audioFileNames = qSort.list();
                        
                        //iterate through files
                        for(int a=0;a<audioFileNames.length;a++){
                            //if is valid audio file and has filename with "_1":
                            if(audioFileNames[a].length()>1 && audioFileNames[a].contains(".3gp") && audioFileNames[a].contains("_1")){
                                File audio=new File(qSort,audioFileNames[a]);
                                String casename=audioFileNames[a].substring(0,audioFileNames[a].indexOf("_1"));
                                if(casename.equals("AudioQuestionsPre")){
                                    if(tmpQSort.getLog(Phase.QUESTIONS_PRE)!=null
                                            && tmpQSort.getLog(Phase.QUESTIONS_PRE).getAudio()!=null
                                            && tmpQSort.getLog(Phase.QUESTIONS_PRE).getAudio().getFilePath()!=null){
                                        //get new filename:
                                        String newpath=audio.getAbsolutePath();
                                        newpath=newpath.substring(0,newpath.indexOf("_1.3gp"));
                                        tmpQSort.getLog(Phase.QUESTIONS_PRE).getAudio().setFilePath(newpath);
                                    }
                                }else if(casename.equals("AudioQSort")){
                                    if(tmpQSort.getLog(Phase.Q_SORT)!=null
                                            && tmpQSort.getLog(Phase.Q_SORT).getAudio()!=null
                                            && tmpQSort.getLog(Phase.Q_SORT).getAudio().getFilePath()!=null){
                                        //get new filename:
                                        String newpath=audio.getAbsolutePath();
                                        newpath=newpath.substring(0,newpath.indexOf("_1.3gp"));
                                        tmpQSort.getLog(Phase.Q_SORT).getAudio().setFilePath(newpath);
                                    }
                                }else if(casename.equals("AudioQuestionsPost")){
                                    if(tmpQSort.getLog(Phase.QUESTIONS_POST)!=null
                                            && tmpQSort.getLog(Phase.QUESTIONS_POST).getAudio()!=null
                                            && tmpQSort.getLog(Phase.QUESTIONS_POST).getAudio().getFilePath()!=null){
                                        //get new filename:
                                        String newpath=audio.getAbsolutePath();
                                        newpath=newpath.substring(0,newpath.indexOf("_1.3gp"));
                                        tmpQSort.getLog(Phase.QUESTIONS_POST).getAudio().setFilePath(newpath);
                                    }
                                }else if(casename.equals("AudioInterview")){
                                    if(tmpQSort.getLog(Phase.INTERVIEW)!=null
                                            && tmpQSort.getLog(Phase.INTERVIEW).getAudio()!=null
                                            && tmpQSort.getLog(Phase.INTERVIEW).getAudio().getFilePath()!=null){
                                        //get new filename:
                                        String newpath=audio.getAbsolutePath();
                                        newpath=newpath.substring(0,newpath.indexOf("_1.3gp"));
                                        tmpQSort.getLog(Phase.INTERVIEW).getAudio().setFilePath(newpath);
                                    }
                                }else{
                                    Log.e("ImportHandler", "copyStudyToApp() -- invalid AudioFilename: "+audioFileNames[a]);
                                }
                            }
                        }
                    }
                    qSortsXMLImp.set(q, tmpQSort);
                }
                studyObject.setQSorts(qSortsXMLImp);
            }
        }
        // adds full imported study to db
        return studyObject;
    }

    /**
     * @param xmlFile the xml file from which the study should be created
     * @param newStudyName the possibly new study name. Needs to be checked if
     *            differs from the xml ones
     * @return a study created from xml
     */
    private Study createStudyFromXML(File xmlFile, String newStudyName) {
        XMLImportHandler xmlh = new XMLImportHandler();
        Study s = null;
        try {
            s = xmlh.parseXMLFile(xmlFile);
            s.setName(newStudyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public void onSaveFullStudy(int studyId) {
        // Seems like this doesn'T need to do anything
    }
}
