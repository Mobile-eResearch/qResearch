
package de.eresearch.app.io.exp;

import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.ClosedAnswer;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.model.Phase;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SPSSExportHandler {

    public SPSSExportHandler() {

    }

    /**
     * Creates SPSS-files for each QSort of the given Study in separated
     * SPSS-dirs.
     * 
     * @author Jan
     * @param study
     * @param qsortDirs List of all qsort-directories. The order of dirs and the
     *            order of the studie's qsorts are the same.
     */
    public void createSPSSFiles(Study study, List<File> qsortDirs) {

        // There is nothing to be exported
        if (study == null || study.getQSorts() == null || qsortDirs == null) {
            return;
        }

        if (!study.getQSorts().isEmpty() && !qsortDirs.isEmpty()) {
            createQuestionFile(study, qsortDirs);
        }

    }

    private void createQuestionFile(Study study, List<File> qsortDirs) {

        List<QSort> qsorts = study.getQSorts();
        List<Question> questions = study.getAllQuestions();

        // There is nothing to be exported
        if (questions == null) {
            return;
        }

        // Iteration through QSorts
        for (int currentQSortNumber = 0; currentQSortNumber < qsorts.size(); currentQSortNumber++) {

            QSort currentQSort = qsorts.get(currentQSortNumber);
            File currentDir = qsortDirs.get(currentQSortNumber);

            File file = new File(currentDir, currentQSort.getName() + ".txt");

            try {
                if (!file.exists())
                    file.createNewFile();

                PrintWriter fileOutput = new PrintWriter(file);

                String firstRow = "Frage;Antwort";
                fileOutput.println(firstRow);

                // Iteration through Questions
                for (int currentQuestionNumber = 0; currentQuestionNumber < questions.size(); currentQuestionNumber++) {

                    String noAnswer = "NO_ANSWER";

                    String qString = questions.get(currentQuestionNumber).getText();
                    String aString = "UNSPECIFIED_ANSWER";

                    Question question = questions.get(currentQuestionNumber);
                    Log preLog = currentQSort.getLog(Phase.QUESTIONS_PRE);
                    Log postLog = currentQSort.getLog(Phase.QUESTIONS_POST);

                    if (preLog == null || postLog == null) {
                        return;
                    }

                    List<Answer> preAnswers = preLog.getAnswers();
                    List<Answer> postAnswers = postLog.getAnswers();

                    List<Answer> answers = new ArrayList<Answer>();
                    if (preAnswers != null)
                        answers.addAll(preAnswers);
                    if (postAnswers != null)
                        answers.addAll(postAnswers);

                    Answer answer;

                    if (answers.size() < currentQuestionNumber + 1) {
                        answer = null;
                    } else {
                        answer = answers.get(currentQuestionNumber);
                    }

                    if (question == null) {
                        continue;
                    }

                    if (answer == null) {
                        aString = noAnswer;
                    }
                    else {
                        // OpenAnswer
                        if (answer instanceof OpenAnswer) {
                            aString = ((OpenAnswer) answer).getAnswer();
                        }
                        // ClosedAnswer
                        else if (answer instanceof ClosedAnswer) {

                            List<String> answerList = ((ClosedAnswer) answer).getAnswers();
                            if (answerList == null || answerList.size() == 0) {
                                aString = noAnswer;
                            }
                            else {
                                aString = answerList.toString();
                            }
                        }
                        // ScaleAnswer
                        else if (answer instanceof ScaleAnswer) {
                            List<Scale> scales = ((ScaleAnswer) answer).getScales();

                            if (scales == null || scales.size() == 0) {
                                aString = noAnswer;
                            } else {
                                aString = "";
                                for (Scale s : scales) {
                                    aString = aString + String.valueOf(s.getSelectedValueIndex());
                                }
                            }

                        }
                    }

                    fileOutput.println(qString + ";" + aString);

                }

                fileOutput.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
