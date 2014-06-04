
package de.eresearch.app.io.exp;

import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Study;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports the {@link Study} in PQMethod files
 * 
 * @author Tammo
 */
public class PQMethodExportHandler {

    /** {@link Study} for which the PQMethod files will be created */
    private Study mStudy;

    /** Name of the {@link Study} */
    private String mStudyName;

    public PQMethodExportHandler(Study study) {
        mStudy = study;
        mStudyName = mStudy.getName().replaceAll(" ", "");
    }

    /**
     * Creates the PQMethod files for the {@link Study}
     * 
     * @param path
     */
    public void exportPQFiles(File path) {
        createDat(path);
        createSta(path);
    }

    /**
     * Creates the .sta file for PQMethod
     * 
     * @param path
     */
    private void createSta(File path) {
        File sta = new File(path, (mStudyName + ".sta"));
        try {
            if (!sta.exists())
                sta.createNewFile();

            PrintWriter staout = new PrintWriter(sta);

            for (Item i : mStudy.getItems()) {
                staout.print(i.getStatement() + "\n");
            }

            staout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the .dat file for PQMethod
     * 
     * @param path
     */
    private void createDat(File path) {
        File dat = new File(path, mStudyName + ".dat");
        try {
            if (!dat.exists())
                dat.createNewFile();

            PrintWriter datout = new PrintWriter(dat);

            // ////////// FIRST ROW ///////////
            String blanksForSorts = " ";
            int sorts = mStudy.getQSorts().size();
            if (!(sorts > 9))
                blanksForSorts += " ";

            String blanksForItems = " ";
            int items = mStudy.getItems().size();
            if (!(items > 9))
                blanksForItems += " ";

            datout.println("  " + 0 + blanksForSorts + sorts + blanksForItems
                    + items + " " + mStudyName);

            // ////////// SECOND ROW ///////////
            Pyramid pyr = mStudy.getPyramid();
            int width = pyr.getWidth();

            String nulls = "";
            for (int i = -6; i < -(width / 2); i++) {
                nulls += "0  ";
            }

            String rows = "";
            for (int i = 0; i <= width; i++) {
                rows += pyr.getHeightAt(i) + "  ";
            }
            //String rows = pyr.toPQString();

            String end = "0  0  0  0  0  0  0";

            datout.println(" " + -(width / 2) + "  " + (width / 2) + "  "
                    + nulls + rows + nulls + end);

            // ////////// THIRD ROW ///////////
            List<QSort> qsortList = mStudy.getQSorts();
            List<Integer> rowNumbers = new ArrayList<Integer>();
            for (int i = -(width / 2); i <= (width / 2); i++) {
                rowNumbers.add(i);
            }

            int n = 1;
            for (QSort s : qsortList) {
                
                if (!s.isFinished())
                    continue;
                
                String blanksForQs = "    ";

                if (!(n > 9))
                    blanksForQs += " ";

                if (s.getSortedItems().get(0).getColumn() <= (width / 2))
                    blanksForQs = blanksForQs.substring(0,
                            blanksForQs.length() - 1);

                String rslt = "";
                List<Item> itemList = s.getSortedItems();

                boolean isFirst = true;
                for (Item i : itemList) {
                    int x = rowNumbers.get(i.getColumn()); //-1 ?
                    if (x >= 0 && !isFirst)
                        rslt += " ";

                    isFirst = false;
                    rslt += x;
                }

                datout.println("QSort" + n + blanksForQs + rslt);

                n++;
            }

            datout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
