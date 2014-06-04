
package de.eresearch.app.io.exp;

import android.os.Environment;
import android.util.Log;

import de.eresearch.app.io.exception.ItemsNotAvailableException;
import de.eresearch.app.io.exception.NameAlreadyInUseException;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * The ExportHandler is used to export studies({@link Study}) from the
 * application and its database to the external storage of the android device.
 * 
 * @author Tammo
 */
public class ExportHandler {
    /** Study to be exported */
    private Study mStudy;

    /**
     * Directories for ExternalStorage, qResearch folder, Export folder, Study
     * folder, PQMethod folder, Items folder, Pyramids folder
     */
    private File mSDdir, mResearchDir, mExportDir, mStudyDir, mPQDir, mSortsDir,
            mItemsDir, mFullPyrDir;

    /** List of QSort directories */
    private List<File> mQSortDirList;

    /** Name for PQMethod dir */
    private final String PQMETHOD_DIRECTORY = "PQMethod";

    /** Name for Item dir */
    private final String ITEM_DIRECTORY = "Items";

    /** Name for Pyramide dir */
    private final String FULL_PYRAMIDE_DIRECTORY = "ErgebnisBilder";

    /** Name for Export dir */
    private final String EXPORT_DIRECTORY = "Export";

    /** Name for App dir */
    private final String QRESEARCH_APP_DIR = "qResearch";

    /** Name for QSorts dir */
    private final String QSORTS_DIR = "QSorts";

    /**
     * Constructor for ExportHandler, creates dirs for App and Export, if these
     * are not yet created.
     */
    public ExportHandler() {
        mSDdir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());

        mResearchDir = new File(mSDdir, QRESEARCH_APP_DIR);
        if (!mResearchDir.exists())
            mResearchDir.mkdirs();

        mExportDir = new File(mResearchDir, EXPORT_DIRECTORY);
        if (!mExportDir.exists())
            mExportDir.mkdirs();
    }

    /**
     * Exports the given study to the export dir on the external storage. If a
     * {@link Study} with the same name has already been exported, a
     * {@link NameAlreadyInUseException} is thrown.
     * 
     * @param Study to be exported
     * @return true if the {@link Study} was successfully exported, else false
     * @throws NameAlreadyInUseException
     * @throws IOException
     */
    public boolean exportStudy(Study study) throws NameAlreadyInUseException, IOException {
        if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())))
            return false;

        mStudy = study;
        createDirs();
        getAllItems();

        if (!mStudy.getQSorts().isEmpty()) {
            mSortsDir = new File(mStudyDir, QSORTS_DIR);
            mSortsDir.mkdirs();
            
            createAndFillQSortDir();

            PQMethodExportHandler pq = new PQMethodExportHandler(mStudy);
            pq.exportPQFiles(mPQDir);
        }

        XMLExportHandler xmlh = new XMLExportHandler();
        try {
            xmlh.createXMLFile(mStudy, mStudyDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SPSSExportHandler spssh = new SPSSExportHandler();
        spssh.createSPSSFiles(mStudy, mQSortDirList);

        // TODO ResultPictures still need to be exported

        return true;
    }

    /**
     * Creates and fills directories for each {@link QSort} with audio files.
     */
    private void createAndFillQSortDir() {
        List<QSort> sorts = mStudy.getQSorts();
        mQSortDirList = new ArrayList<File>();

        if (sorts == null){
            return;
        }

        for (QSort s : sorts) {
            File sortDir = new File(mSortsDir, s.getName());
            sortDir.mkdirs();
            mQSortDirList.add(sortDir);

            if ((s.getLog(Phase.QUESTIONS_PRE) != null)
                    && (s.getLog(Phase.QUESTIONS_PRE).getAudio() != null)){
                getAudioForPhase(Phase.QUESTIONS_PRE,
                        s.getLog(Phase.QUESTIONS_PRE).getAudio(), sortDir);
            }

            if ((s.getLog(Phase.Q_SORT) != null)
                    && (s.getLog(Phase.Q_SORT).getAudio() != null)){
                getAudioForPhase(Phase.Q_SORT, s.getLog(Phase.Q_SORT)
                        .getAudio(), sortDir);
            }

            if ((s.getLog(Phase.QUESTIONS_POST) != null)
                    && (s.getLog(Phase.QUESTIONS_POST).getAudio() != null)){
                getAudioForPhase(Phase.QUESTIONS_POST,
                        s.getLog(Phase.QUESTIONS_POST).getAudio(), sortDir);
            }

            if ((s.getLog(Phase.INTERVIEW) != null)
                    && (s.getLog(Phase.INTERVIEW).getAudio() != null)){
                getAudioForPhase(Phase.INTERVIEW, s.getLog(Phase.INTERVIEW)
                        .getAudio(), sortDir);
            }

        }
    }

    /**
     * Copies audio files to the qsort dir given as parameter. The audio file's
     * name is its phase and its partnumber.
     * 
     * @param phase of the audio file
     * @param rec
     * @param sortDir qsort's dir
     */
    private void getAudioForPhase(Phase phase, AudioRecord rec, File sortDir) {
        String phaseName;

        switch (phase) {
            case QUESTIONS_PRE:
                phaseName = "AudioQuestionsPre_";
                break;
            case Q_SORT:
                phaseName = "AudioQSort_";
                break;
            case QUESTIONS_POST:
                phaseName = "AudioQuestionsPost_";
                break;
            case INTERVIEW:
                phaseName = "AudioInterview_";
                break;
            default:
                phaseName = "AudioFile_";
        }

        for (int i = 1; i <= rec.getPartNumber(); i++) {
            File src = new File(rec.getFilePath() +"_"+ i + ".3gp");
            File dst = new File(sortDir, phaseName + i + ".3gp");

            if (!src.exists()){
                break;
            }

            try {
                copy(src, dst);
            } catch (ItemsNotAvailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copies all items to the item folder
     * 
     * @throws IOException
     */
    private void getAllItems() throws IOException {
        List<Item> items = mStudy.getItems();

        if (items.size() <= 0 || !(items.get(0) instanceof Picture))
            return; // add code for different types of item!

        for (Item i : items) {

            Picture p = (Picture) i;
            File src = new File(p.getFilePath());
            File dst = new File(mItemsDir.getAbsolutePath() + "/"
                    + src.getName());
            try {
                copy(src, dst);
            } catch (ItemsNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates directories for the study, pqmethod, items and pyramids
     */
    private void createDirs() {
        mStudyDir = new File(mExportDir, mStudy.getName());
        mStudyDir.mkdirs();
        mPQDir = new File(mStudyDir, PQMETHOD_DIRECTORY);
        mPQDir.mkdirs();
        mItemsDir = new File(mStudyDir, ITEM_DIRECTORY);
        mItemsDir.mkdirs();
       // mFullPyrDir = new File(mStudyDir, FULL_PYRAMIDE_DIRECTORY);
       // mFullPyrDir.mkdirs();
    }

    /**
     * Copies a source file to a destination file. Source:
     * http://examples.javacodegeeks.com/core-java/io/file/4-ways-to-copy-file-
     * in-java/
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
