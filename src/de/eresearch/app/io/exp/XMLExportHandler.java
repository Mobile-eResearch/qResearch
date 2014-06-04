
package de.eresearch.app.io.exp;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.AudioRecord;
import de.eresearch.app.logic.model.ClosedAnswer;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Log;
import de.eresearch.app.logic.model.LogEntry;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.model.Study;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author Andrey Bagraev
 * @author Jurij Wöhlke
 */
public class XMLExportHandler {

    private XmlSerializer xmlSerializer=null;
    
    /**
     * Creates the XMLFile for the given Study in the given directory.
     * 
     * @param study
     * @param dir
     */
    public void createXMLFile(Study study, File dir) throws Exception {
        xmlSerializer = Xml.newSerializer();
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);

        /**
         * Starting file
         */
        xmlSerializer.startDocument("UTF-8", true);
        
        this.serializeStudy(study);

        xmlSerializer.endDocument();
        xmlSerializer.flush();
        String output = writer.toString();

        try {

            File export = new File(dir, study.getName() + ".xml");
            android.util.Log.i("XMLExport", "Export Directory: " + dir.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(export);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(output);
            osw.flush();
            osw.close();

        } catch (Exception e) {
            android.util.Log.e("XML Writer", "I/O ERROR");

        }
    }

    /**
     * serialize study
     * @param study
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeStudy(Study study) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "study");
        
        this.serializeMetaData(study);
        this.serializePyramid(study.getPyramid());
        this.serializeAllItems(study);
        this.serializeAllQuestions(study.getAllQuestions());
        this.serializeAllQSorts(study);

        xmlSerializer.endTag("", "study");
    }

    /**
     * serialize study meta data to XmlSerializer
     * @param study
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeMetaData(Study study) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "study_name");
        if (study.getName() != null)
            xmlSerializer.text(study.getName());
        xmlSerializer.endTag("", "study_name");

        xmlSerializer.startTag("", "study_author");
        if (study.getAuthor() != null)
            xmlSerializer.text(study.getAuthor());
        xmlSerializer.endTag("", "study_author");

        xmlSerializer.startTag("", "study_research_question");
        if (study.getResearchQuestion() != null)
            xmlSerializer.text(study.getResearchQuestion());
        xmlSerializer.endTag("", "study_research_question");

        xmlSerializer.startTag("", "study_desc");
        if (study.getDescription() != null)
            xmlSerializer.text(study.getDescription());
        xmlSerializer.endTag("", "study_desc");

        //NEEDED? - check and remove
        xmlSerializer.startTag("", "study_complete");
        xmlSerializer.text(String.valueOf(study.isComplete()));
        xmlSerializer.endTag("", "study_complete");
    }
    
    /**
     * serialize study items
     * @param study
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllItems(Study study) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "items");
        for (int i = 0; i < study.getItems().size(); i++)
        {
            if(study.getItems().get(i) instanceof Picture){
                this.serializePicture((Picture)study.getItems().get(i));
            }else{
                android.util.Log.e("XMLExportHandler", "serializeAllItems() -- wrong instance");
            }
            
        }
        xmlSerializer.endTag("", "items");
    }
    
    /**
     * serializes a item
     * @param item
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializePicture(Picture item) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "picture");
        xmlSerializer.attribute("", "original_id", Integer.toString(item.getId()));
        if (item instanceof Picture)
        {
            Picture picture=(Picture) item;
            String fname = picture.getFilePath(); //TODO current file path??? because of export?
            String[] fnames = fname.split("/");
            fname = fnames[(fnames.length) - 1];
            xmlSerializer.attribute("", "name",fname);
        }
        
        if(item.getColumn()>=0){
            xmlSerializer.attribute("", "column",Integer.toString(item.getColumn()));
        }
        
        if(item.getRow()>=0){
            xmlSerializer.attribute("", "row",Integer.toString(item.getRow()));
        }
        
        if (item.getStatement() != null)
            xmlSerializer.text(item.getStatement());

        xmlSerializer.endTag("", "picture");
    }

    /**
     * serialize pyramid
     * @param pyramid
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializePyramid(Pyramid pyramid) throws IllegalArgumentException, IllegalStateException, IOException {
        if(pyramid!=null){
            xmlSerializer.startTag("", "pyramid");
            
            xmlSerializer.startTag("", "pyramid_pole_left");
            if (pyramid.getPoleLeft() != null)
                xmlSerializer.text(pyramid.getPoleLeft());
            xmlSerializer.endTag("", "pyramid_pole_left");
    
            xmlSerializer.startTag("", "pyramid_pole_right");
            if (pyramid.getPoleRight() != null)
                xmlSerializer.text(pyramid.getPoleRight());
            xmlSerializer.endTag("", "pyramid_pole_right");
    
            xmlSerializer.startTag("", "pyramid_shape");
            if (pyramid.toUniqueString() != null)
                xmlSerializer.text(pyramid.toUniqueString());
            xmlSerializer.endTag("", "pyramid_shape");
            
            xmlSerializer.endTag("", "pyramid");
        }
    }
    
    /* ###############################################################################################################################
     * Serialization of Questions
     * ############################################################################################################################ */
    
    /**
     * serializes all questions in study
     * @param study
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllQuestions(List<Question> questions) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "questions");
        if(questions!=null){
            for (int i = 0; i < questions.size() ; i++)
            {
                this.serializeQuestion(xmlSerializer,questions.get(i));
            }
        }
        xmlSerializer.endTag("", "questions");
    }

    /**
     * serializes a single questions
     * @param xmlSerializer
     * @param question
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeQuestion(XmlSerializer xmlSerializer, Question question) throws IllegalArgumentException, IllegalStateException, IOException {
        if(question instanceof OpenQuestion){
            this.serializeOpenQuestion((OpenQuestion) question);
        }else if(question instanceof ClosedQuestion){
            this.serializeClosedQuestion((ClosedQuestion) question);
        }else if(question instanceof ScaleQuestion){
            this.serializeScaleQuestion((ScaleQuestion) question);
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * serializes question meta informations
     * @param question
     * @throws IOException 
     * @throws IllegalStateException 
     * @throws IllegalArgumentException 
     */
    private void serializeQuestionMetaData(Question question) throws IllegalArgumentException, IllegalStateException, IOException{
        //serialize old id for answers later!
        xmlSerializer.startTag("", "original_question_id");
        xmlSerializer.text(Integer.toString(question.getId()));
        xmlSerializer.endTag("", "original_question_id");
        
        //serialize question text
        xmlSerializer.startTag("", "question_text");
        xmlSerializer.text(question.getText());
        xmlSerializer.endTag("", "question_text");

        //serialize isPost
        xmlSerializer.startTag("", "question_is_post");
        xmlSerializer.text(Boolean.toString(question.isPost()));
        xmlSerializer.endTag("", "question_is_post");
        
        //serialize order
        xmlSerializer.startTag("", "question_order");
        xmlSerializer.text(Integer.toString(question.getOrderNumber()));
        xmlSerializer.endTag("", "question_order");
    }

    /**
     * serializes an open question
     * @param openQuestion
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeOpenQuestion(OpenQuestion openQuestion) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "open_question");
        
        this.serializeQuestionMetaData(openQuestion);
        
        xmlSerializer.endTag("", "open_question");
    }
    
    /**
     * serializes a closed question
     * @param closedQuestion
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeClosedQuestion(ClosedQuestion closedQuestion) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "closed_question");
        
        this.serializeQuestionMetaData(closedQuestion);
        
        // serialize is multiple choice
        xmlSerializer.startTag("", "is_multiple_choice");
        xmlSerializer.text(Boolean.toString(closedQuestion.isMultipleChoice()));
        xmlSerializer.endTag("", "is_multiple_choice");
        
        // serialize has open field
        xmlSerializer.startTag("", "has_open_field");
        xmlSerializer.text(Boolean.toString(closedQuestion.hasOpenField()));
        xmlSerializer.endTag("", "has_open_field");

        //serialize possible answers
        xmlSerializer.startTag("", "possible_answers");
        List<String> possAnswers=closedQuestion.getPossibleAnswers();
        for(int k=0; k<possAnswers.size();k++){
            xmlSerializer.startTag("", "answer");
            xmlSerializer.text(possAnswers.get(k));
            xmlSerializer.endTag("", "answer");
        }
        xmlSerializer.endTag("", "possible_answers");
        
        xmlSerializer.endTag("", "closed_question");
    }
    
    /**
     * serializes a scale question
     * @param scaleQuestion
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeScaleQuestion(ScaleQuestion scaleQuestion) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "scale_question");
        
        this.serializeQuestionMetaData(scaleQuestion);

        this.serializeScaleList(scaleQuestion.getScales());
        
        xmlSerializer.endTag("", "scale_question");
    }

    /**
     * serializes a list of scales
     * @param scales
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeScaleList(List<Scale> scales) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "scales");
        
        for(int j=0; j<scales.size();j++){
            this.serializeScale(scales.get(j));
        }
        xmlSerializer.endTag("", "scales");
    }

    /**
     * serializes a scale
     * @param scale
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeScale(Scale scale) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "scale");
        
        //serialize original scale id
        xmlSerializer.startTag("", "original_id");
        xmlSerializer.text(Integer.toString(scale.getId()));
        xmlSerializer.endTag("", "original_id");
        
        //serialize left pole
        xmlSerializer.startTag("", "pole_left");
        xmlSerializer.text(scale.getPoleLeft());
        xmlSerializer.endTag("", "pole_left");
        
        //serialize right pole
        xmlSerializer.startTag("", "pole_right");
        xmlSerializer.text(scale.getPoleRight());
        xmlSerializer.endTag("", "pole_right");
        
        //serialize scale values
        xmlSerializer.startTag("", "scale_values");
        List<String> scaleValues=scale.getScaleValues();
        for(int k=0; k<scaleValues.size();k++){
            xmlSerializer.startTag("", "value");
            xmlSerializer.text(scaleValues.get(k));
            xmlSerializer.endTag("", "value");
        }
        xmlSerializer.endTag("", "scale_values");
        
        //if selectedValueIndes>=0 serialize it!
        if(scale.getSelectedValueIndex()>=0){
            xmlSerializer.startTag("", "selected_value_index");
            xmlSerializer.text(Integer.toString(scale.getSelectedValueIndex()));
            xmlSerializer.endTag("", "selected_value_index");
        }
        
        xmlSerializer.endTag("", "scale");
    }

    /* ###############################################################################################################################
     * Serialization of QSorts
     * ############################################################################################################################ */
    
    /**
     * serializes all qsorts for study
     * @param study
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllQSorts(Study study) throws IllegalArgumentException, IllegalStateException, IOException{
        xmlSerializer.startTag("", "qsorts");
        for (int i = 0; i < study.getQSorts().size(); i++)
        {
            this.serializeQSort(study.getQSorts().get(i));
        }
        xmlSerializer.endTag("", "qsorts");
    }

    private void serializeQSort(QSort qSort) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "qsort");
        
        if(qSort!=null){
            this.serializeQSortMetaData(qSort);
            this.serializeAllLogs(qSort.getLogs());  
            this.serializeAllSortedItems(qSort.getSortedItems());
        }
        xmlSerializer.endTag("", "qsort");
    }

    /**
     * serialize meta information for a qsort
     * @param qSort
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeQSortMetaData(QSort qSort) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "qsort_name");
        if (qSort.getName() != null)
            xmlSerializer.text(qSort.getName());
        xmlSerializer.endTag("", "qsort_name");

        xmlSerializer.startTag("", "qsort_acronym");
        if (qSort.getAcronym() != null)
            xmlSerializer.text(qSort.getAcronym());
        xmlSerializer.endTag("", "qsort_acronym");

        xmlSerializer.startTag("", "qsort_finished");
        xmlSerializer.text(Boolean.toString(qSort.isFinished()));
        xmlSerializer.endTag("", "qsort_finished");

        xmlSerializer.startTag("", "qsort_starttime");
        xmlSerializer.text(Long.toString(qSort.getStartTime()));
        xmlSerializer.endTag("", "qsort_starttime");

        xmlSerializer.startTag("", "qsort_endtime");
        xmlSerializer.text(Long.toString(qSort.getEndTime()));
        xmlSerializer.endTag("", "qsort_endtime");   
    }
    
    /**
     * serialize a list of logs
     * @param logs
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllLogs(List<Log> logs) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "logs");
        for (int j = 0; j < logs.size(); j++)
        {
            this.serializeLog(logs.get(j));
        }
        xmlSerializer.endTag("", "logs");
    }
    
    /**
     * serialize a single log
     * @param log
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeLog(Log log) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "log");
        
        xmlSerializer.startTag("", "log_phase");
        xmlSerializer.text(log.getPhase().toString());
        xmlSerializer.endTag("", "log_phase");
        
        if(log.getAudio()!=null){
            this.serializeAudio(log.getAudio());
        }
        this.serializeAllAnswers(log.getAnswers());
        this.serializeAllLogEntries(log.getLogEntries());
        this.serializeAllNotes(log.getNotes());

        xmlSerializer.endTag("", "log");    
    }

    /**
     * serialize audio record
     * @param audio
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAudio(AudioRecord audio) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "audio");
        
        xmlSerializer.startTag("", "audio_path");
        xmlSerializer.text(audio.getFilePath()); //TODO current file path??? because of export?
        xmlSerializer.endTag("", "audio_path");
        
        xmlSerializer.startTag("", "audio_number_parts");
        xmlSerializer.text(Integer.toString(audio.getPartNumber()));
        xmlSerializer.endTag("", "audio_number_parts");
        
        xmlSerializer.endTag("", "audio");
    }

    /**
     * serialize a list of answers
     * @param answers
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllAnswers(List<Answer> answers) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "answers");
        for (int k = 0; k < answers.size(); k++)
        {
            this.serializeAnswer(answers.get(k));
        }
        xmlSerializer.endTag("", "answers");
    }

    /**
     * serialize a single answer
     * @param answer
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAnswer(Answer answer) throws IllegalArgumentException, IllegalStateException, IOException {
        /* answer type*/
        if(answer instanceof OpenAnswer){
            this.serializeOpenAnswer((OpenAnswer)answer);
        }else if(answer instanceof ClosedAnswer){
            this.serializeClosedAnswer((ClosedAnswer)answer);
        }else if(answer instanceof ScaleAnswer){
            this.serializeScaleAnswer((ScaleAnswer)answer);
        }else{
            android.util.Log.e("XMLExportHandler", "createXMLFile() - invalid instance of answer");
        }
    }

    /**
     * serialize answer meta information
     * @param answer
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAnswerMetaData(Answer answer) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "old_question_id");
        xmlSerializer.text(Integer.toString(answer.getQuestion().getId()));
        xmlSerializer.endTag("", "old_question_id");
        
        xmlSerializer.startTag("", "time");
        xmlSerializer.text(Long.toString(answer.getTime()));
        xmlSerializer.endTag("", "time");
    }

    /**
     * serialize a single openanswer
     * @param answer
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeOpenAnswer(OpenAnswer answer) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "open_answer");
        this.serializeAnswerMetaData(answer);
        
        //serialize answer string for open answer
        xmlSerializer.startTag("","answer_string");
        xmlSerializer.text(answer.getAnswer());
        xmlSerializer.endTag("","answer_string");
        
        xmlSerializer.endTag("", "open_answer");
    }

    /**
     * serialize a single closed answer
     * @param answer
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeClosedAnswer(ClosedAnswer answer) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "closed_answer");
        this.serializeAnswerMetaData(answer);
        
        //serialize answers
        xmlSerializer.startTag("", "answers_list");
        List<String> answers=answer.getAnswers();
        for(int k=0; k<answers.size();k++){
            xmlSerializer.startTag("", "value");
            xmlSerializer.text(answers.get(k));
            xmlSerializer.endTag("", "value");
        }
        xmlSerializer.endTag("", "answers_list");
        
        xmlSerializer.endTag("", "closed_answer");
    }

    /**
     * serialize a single scale answer
     * @param answer
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeScaleAnswer(ScaleAnswer answer) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "scale_answer");
        
        this.serializeAnswerMetaData(answer);
        this.serializeScaleList(answer.getScales());
        
        xmlSerializer.endTag("", "scale_answer");
    }
    
    /**
     * serializes a list of log entries
     * @param logEntries
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllLogEntries(List<LogEntry> logEntries) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "log_entries");
        for (int k = 0; k < logEntries.size(); k++)
        {
            this.serializeLogEntry(logEntries.get(k));
        }
        xmlSerializer.endTag("", "log_entries");
    }

    /**
     * serialize a single log entry
     * @param le
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeLogEntry(LogEntry le) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "log_entry");

        xmlSerializer.startTag("", "log_entry_timestamp");
        xmlSerializer.text(Long.toString(le.getTime()));
        xmlSerializer.endTag("", "log_entry_timestamp");

        xmlSerializer.startTag("", "log_old_coor");
        xmlSerializer.text(le.getFromX()+"/"+le.getFromY());
        xmlSerializer.endTag("", "log_old_coor");

        xmlSerializer.startTag("", "log_new_coor");
        xmlSerializer.text(le.getToX()+"/"+le.getToY());
        xmlSerializer.endTag("", "log_new_coor");

        xmlSerializer.startTag("", "item_original_id");
        xmlSerializer.text(Integer.toString(le.getItem().getId()));
        xmlSerializer.endTag("", "item_original_id");

        xmlSerializer.endTag("", "log_entry");
    }
    
    /**
     * serializes a list of notes
     * @param notes
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllNotes(List<Note> notes) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "notes");
        for (int k = 0; k < notes.size(); k++)
        {
            this.serializeNote(notes.get(k));
        }
        xmlSerializer.endTag("", "notes");
    }

    /**
     * serializes a single note
     * @param note
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeNote(Note note) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "note");
        
        xmlSerializer.startTag("", "note_timestamp");
        xmlSerializer.text(Long.toString(note.getTime()));
        xmlSerializer.endTag("", "note_timestamp");

        xmlSerializer.startTag("", "note_title");
        xmlSerializer.text(note.getTitle());
        xmlSerializer.endTag("", "note_title");

        xmlSerializer.startTag("", "note_text");
        xmlSerializer.text(note.getText());
        xmlSerializer.endTag("", "note_text");

        xmlSerializer.startTag("", "note_phase");
        xmlSerializer.text(note.getPhase().toString());
        xmlSerializer.endTag("", "note_phase");
        
        xmlSerializer.endTag("", "note");
    }

    /**
     * serializes a list of sorted items
     * @param sortedItems
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    private void serializeAllSortedItems(List<Item> sortedItems) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag("", "sorted_items");
        
        for(int i=0;i<sortedItems.size();i++){
            if(sortedItems.get(i) instanceof Picture){
                this.serializePicture((Picture)sortedItems.get(i));
            }else{
                android.util.Log.e("XMLExportHandler", "serializeAllSortedItems() -- wrong instance");
            }
        }
        
        xmlSerializer.endTag("", "sorted_items");
    }
}
