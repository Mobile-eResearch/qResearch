
package de.eresearch.app.logic.tasks.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import de.eresearch.app.R;
import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.AudioRecordHelper;
import de.eresearch.app.db.helper.ItemHelper;
import de.eresearch.app.db.helper.PyramidHelper;
import de.eresearch.app.db.helper.QSortHelper;
import de.eresearch.app.db.helper.QuestionHelper;
import de.eresearch.app.db.helper.ScaleHelper;
import de.eresearch.app.db.helper.StudyHelper;
import de.eresearch.app.io.exception.ItemsNotAvailableException;
import de.eresearch.app.io.imp.ImportHandler;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.LogEntry;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.model.Study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Task to import a study.
 * 
 * @author Moritz
 * @author Jurij Wöhlke
 */
public class ImportStudyTask extends AsyncTask<Void, Void, Throwable> {

    private Context mContext;

    private ImportStudyTask.Callbacks mCallback;

    private String mStudyName;

    private String mNewStudyName;

    private ProgressDialog mPd;

    private int mImportedStudyId;
    
    /** Folder: */
    private File mSDdir, mResearchDir,mPictureDir;
    /** Name for App dir */
    private final String QRESEARCH_APP_DIR = "qResearch";
    /** Name for image dir in app dir on sd */
    private final String QRESEARCH_APP_PICTURE_DIR = "Pictures";

    public static interface Callbacks {
        /**
         * Called by the {@link ImportStudyTask} when the import of a study has
         * finished. As the import of a study can fail (e.g. due to an IO
         * failure), this method provides an error that has (possibly) happened.
         * 
         * @param importedStudy The id of the study that has been imported and
         *            written to the database or <code>-1</code>, when an error
         *            occured
         * @param thr A {@link Throwable} containing an error that has occured
         *            or <code>null</code> when everything worked fine
         */
        public void onStudyImported(int importedStudyId, Throwable thr);
    }

    /**
     * Creates a new {@link ImportStudyTask}.
     * 
     * @param context An android application context
     * @param callback An instance of the task's callback interface
     * @param studyName The name of the study to be imported (on the file
     *            system)
     * @param newStudyName The name of the study to be imported (as then shown
     *            in the app)
     */
    public ImportStudyTask(Context context, ImportStudyTask.Callbacks callback, String studyName,
            String newStudyName) {
        mContext = context;
        mCallback = callback;
        mStudyName = studyName;
        mNewStudyName = newStudyName;

        mImportedStudyId = -1;
        
        mSDdir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        mResearchDir = new File(mSDdir, QRESEARCH_APP_DIR);
        mPictureDir = new File(mResearchDir,QRESEARCH_APP_PICTURE_DIR);
    }

    @Override
    protected void onPreExecute() {
        mPd = new ProgressDialog(mContext);
        mPd.setMessage("Importing...");
        mPd.show();
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        Study imported = null;

        // Import the study
        ImportHandler imph = new ImportHandler();
        try {
            imported = imph.copyStudyToApp(mStudyName, mNewStudyName, mContext);
        } catch (IOException e) {
            return e;
        }

        // Abort when no study has been imported
        if (imported == null) {
            return new NullPointerException();
        }

        try {
            // Write imported study to db
            imported=this.writeStudyToDB(imported);
            // copy files of imported study to app storage
            imported=this.ImportFilesToInternalStorage(imported);
        } catch (HelperNotFoundException e) {
            return e;
        } catch (ItemsNotAvailableException e) {
            return e;
        } catch (IOException e) {
            return e;
        }

        mImportedStudyId = imported.getId();

        return null;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        mPd.dismiss();
        mCallback.onStudyImported(mImportedStudyId, result);
    }
    
    private Study writeStudyToDB(Study imported) throws HelperNotFoundException {
        //create study:
        StudyHelper sh = (StudyHelper) DatabaseConnector.getInstance(mContext).getHelper(
                DatabaseConnector.TYPE_STUDY);
        imported.setId(sh.saveMetaData(imported).getId());
        
        //save questions and backup old ids!
        List<Question> questions=imported.getAllQuestions();
        HashMap<String,String> oldNewQuestionIds=new HashMap<String,String>();
        HashMap<String,String> oldNewScaleIds=new HashMap<String,String>();
        
        QuestionHelper qh = (QuestionHelper) DatabaseConnector.getInstance(mContext).getHelper(
                DatabaseConnector.TYPE_QUESTION);
        ScaleHelper scalehelper = (ScaleHelper) DatabaseConnector.getInstance(mContext).getHelper(
                DatabaseConnector.TYPE_SCALE);
        
        //save questions
        for(int k=0;k<questions.size();k++){
            Question q=questions.get(k);
            q.setStudyId(imported.getId());
            
            //get old question id
            int oldId=q.getId();
            
            //if scalequestion save scale and backup and map scaleIds
            if(q instanceof ScaleQuestion){
                ScaleQuestion qtmp=(ScaleQuestion) q;
                List<Scale> scales=qtmp.getScales();
                
                qtmp.setId(-1);
                qtmp.setScales(new LinkedList<Scale>());
                qtmp=(ScaleQuestion)qh.saveObject((ScaleQuestion)qtmp);
                int newId=qtmp.getId();
                
                //backup and map new id to old id
                oldNewQuestionIds.put(Integer.toString(oldId), Integer.toString(newId));
                
                //save scales:
                for(int c=0;c<scales.size();c++){
                    Scale s=scales.get(c);
                    s.setQuestionId(newId);
                    
                    int oldSId=s.getId();
                    s.setId(-1);
                    s=scalehelper.saveObject(s);
                    int newSId=s.getId();
                    oldNewScaleIds.put(Integer.toString(oldSId), Integer.toString(newSId));
                }
                q=qtmp;
            }else{
                q.setId(-1);
                q=qh.saveObject(q);
                int newId=q.getId();
                //backup and map new id to old id
                oldNewQuestionIds.put(Integer.toString(oldId), Integer.toString(newId));
            }
        }
        
        //save items and backup old ids
        List<Item> items=imported.getItems();
        HashMap<String,String> oldNewItemIds=new HashMap<String,String>();
        
        ItemHelper ih = (ItemHelper) DatabaseConnector.getInstance(mContext).getHelper(
                DatabaseConnector.TYPE_ITEM);
        for(Item i:items){
            //set study id
            i.setStudyID(imported.getId());
            
            //save item
            int oldId=i.getId();
            i.setId(-1);
            i=ih.saveObject(i);
            int newId=i.getId();
            //backup and map new id to old one
            oldNewItemIds.put(Integer.toString(oldId), Integer.toString(newId));
        }
        
        //save pyramid
        PyramidHelper ph = (PyramidHelper) DatabaseConnector.getInstance(mContext).getHelper(
                DatabaseConnector.TYPE_PYRAMID);
        Pyramid pyramid=imported.getPyramid();
        //set pyramidid to study id
        pyramid.setId(imported.getId());
        //save pyramid:
        imported.setPyramid(ph.saveObject(pyramid));
        
        //update qsorts and write them to db
        imported=this.updateAndWriteQSortsToDB(imported,oldNewQuestionIds,oldNewScaleIds,oldNewItemIds);
        
        //return study
        return imported;
    }

    private Study updateAndWriteQSortsToDB(Study imported, HashMap<String, String> oldNewQuestionIds, HashMap<String, String> oldNewScaleIds, HashMap<String, String> oldNewItemIds) throws HelperNotFoundException {
        List<QSort> qsorts=imported.getQSorts();
        QSortHelper qSh = (QSortHelper) DatabaseConnector.getInstance(mContext).getHelper(DatabaseConnector.TYPE_QSORT);
        
        for(QSort qs:qsorts){
            //update studyid in qsort
            qs.setStudyId(imported.getId());

            //update logs:
            List<Log> logs=qs.getLogs();
            for(int u=0;u<logs.size();u++){
                Log l=logs.get(u);
                //update answers
                List<Answer> answers=l.getAnswers();
                for(int g=0;g<answers.size();g++){
                    Answer a=answers.get(g);
                    
                    //update scales in answer
                    if(a instanceof ScaleAnswer){
                        ScaleAnswer sa=(ScaleAnswer) a;
                        List<Scale> scales=((ScaleAnswer) a).getScales();
                        for(int s=0;s<scales.size();s++){
                            Scale scale=scales.get(s);
                            String newId=oldNewScaleIds.get(Integer.toString(scale.getId()));
                            if(newId!=null){
                                scale.setId(Integer.parseInt(newId));
                            }else{
                                android.util.Log.e("ImportStudyTask", "updateAndWriteQSortsToDB() -- no new scale id found for old id -- oldId:"+scale.getId()+" -- newId:"+newId);
                            }
                        }
                        a=sa;
                    }
                    //get old question id
                    int qId=a.getQuestion().getId();
                    String newId=oldNewQuestionIds.get(Integer.toString(qId));
                    if(newId!=null){
                        //set updated question
                        a.setQuestion(imported.getQuestionById(Integer.parseInt(newId)));
                    }else{
                        android.util.Log.e("ImportStudyTask", "updateAndWriteQSortsToDB() -- no new question id found for old id -- oldId:"+qId+" -- newId:"+newId);
                    }
                }
                
                //update logEntries
                List<LogEntry> logentries=l.getLogEntries();
                for(int k=0;k<logentries.size();k++){
                    LogEntry entry=logentries.get(k);
                    //get old item id
                    int oldItemId=entry.getItem().getId();
                    //set updated question
                    entry.setItem(imported.getItemById(Integer.parseInt(oldNewItemIds.get(Integer.toString(oldItemId)))));
                }
            }

            //update sorted items
            List<Item> sortedItems=qs.getSortedItems();
            if(sortedItems!=null){
                for(int i=0;i<sortedItems.size();i++){
                    Item item=sortedItems.get(i);
                    int newId=Integer.parseInt(oldNewItemIds.get(Integer.toString(item.getId())));
                    item.setId(imported.getItemById(newId).getId());
                }
            }
            
            //save qsort
            qSh.saveObject(qs);
        }
        return imported;
    }
    
    private Study ImportFilesToInternalStorage(Study imported) throws HelperNotFoundException, ItemsNotAvailableException, IOException {
        // ################################### copy audio ###################################
        AudioRecordHelper arh = (AudioRecordHelper) DatabaseConnector.getInstance(mContext).getHelper(DatabaseConnector.TYPE_AUDIORECORD);
        
        String audioDir = mContext.getResources().getString(R.string.audiorecord_private_root);

        // Get private internal storage directory
        File internalDir = mContext.getFilesDir();

        // The root directory where all private audio data lies
        File audioRoot = new File(internalDir, audioDir);

        //get qsorts:
        List<QSort> qsorts=imported.getQSorts();
        for(int q=0;q<qsorts.size();q++){
            QSort qsort=qsorts.get(q);
            //make file/folder for this qsort:
            File audioQsortRoot = new File(audioRoot, Integer.toString(qsort.getId()));

            // create directories when they don't exist
            audioQsortRoot.mkdirs();
            
            //copy files to folder:
            List<Log> logs=qsort.getLogs();
            for(int l=0;l<logs.size();l++){
                Log log=logs.get(l);
                AudioRecord tmp=log.getAudio();
                for (int i = 1; i <= tmp.getPartNumber(); i++) {
                    File src=new File(tmp.getFilePath()+"_"+i+".3gp");
                    File dst=new File(audioQsortRoot + "/" + src.getName());
                    this.copy(src, dst);
                }
                
                //update filepaths:
                File tmpFilePath=new File(tmp.getFilePath());
                tmp.setFilePath(audioQsortRoot + "/" +tmpFilePath.getName());
                
                arh.saveObject(tmp);
            }
        }
        // ################################### copy images ###################################
        ItemHelper ih = (ItemHelper) DatabaseConnector.getInstance(mContext).getHelper(DatabaseConnector.TYPE_ITEM);
        
        List<Item> items=imported.getItems();
        for(int i=0;i<items.size();i++){
            if(items.get(i) instanceof Picture){
                Picture p=(Picture) items.get(i);
                
                File src=new File(p.getFilePath());
                File dst=new File(mPictureDir+"/"+src.getName());
                
                if(!src.exists()){
                    throw new ItemsNotAvailableException();
                }
                
                //is true if a file in dst exists with the same name and the same size: 
                boolean sameFileExists=false;
                //if exists:
                if(dst.exists()){
                    //if not the same size, then change filename:
                    if(dst.length()!=src.length()){
                        dst=new File(mPictureDir+"/"+"ImpStudy"+imported.getId()+"_"+src.getName());
                    }else{
                        //no overwrite file:
                        sameFileExists=true;
                        android.util.Log.i("ImportStudyTask","ImportFilesToInternalStorage() -- File with same size and filename already exists -- no overwrite");
                    }
                }
                
                //if a file on dst exists with the same size and the same name, then no overwrite, else copy:
                if(!sameFileExists){
                    this.copy(src, dst);
                }
                
                //refresh filePath:
                p.setFilePath(dst.getAbsolutePath());
                
                //save item in db:
                ih.saveObject(p);
            }
        }
        return imported;
    }
    
    /**
     * Copies a source file to a destination file. Source:
     * http://examples.javacodegeeks.com/core-java/io/file/4-ways-to-copy-file-
     * in-java/
     * 
     * copied from ExportHandler:
     * 
     * @param src file
     * @param dst file
     * @throws IOException
     */
    public void copy(File src, File dst) throws ItemsNotAvailableException,
            IOException {
        if (!src.exists())
            throw new ItemsNotAvailableException();

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        try {
            inputChannel = new FileInputStream(src).getChannel();
            outputChannel = new FileOutputStream(dst).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }

    }
}
