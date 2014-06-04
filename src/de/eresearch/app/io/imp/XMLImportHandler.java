
package de.eresearch.app.io.imp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Picture;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;
import de.eresearch.app.logic.model.Study;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey Bagraev
 * @author Jurij Wöhlke
 */
public class XMLImportHandler {
    
    private Map<String, String> mStatements = new HashMap<String, String>();
    
    private Study mStudy = new Study(-1);

    /**
     * Parses the XML file and creates study from it. *
     * 
     * @param xml to parse
     * @return
     * @throws XmlPullParserException
     */

    public Study parseXMLFile(File xml) throws XmlPullParserException, IOException {

        android.util.Log.i("XMLParser", "proceeding");
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            /* Preparing input streams */
            FileInputStream fis = new FileInputStream(xml);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(dis, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String tagname = parser.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        /* Metadata */
                        if (tagname.equalsIgnoreCase("study_name"))
                        {
                            mStudy.setName(parser.nextText());
                        }
                        else if (tagname.equalsIgnoreCase("study_author"))
                        {
                            mStudy.setAuthor(parser.nextText());
                        }
                        else if (tagname.equalsIgnoreCase("study_research_question"))
                        {
                            mStudy.setResearchQuestion(parser.nextText());
                        }
                        else if (tagname.equalsIgnoreCase("study_desc"))
                        {
                            mStudy.setDescription(parser.nextText());
                        }
                        else if (tagname.equalsIgnoreCase("study_complete"))
                        {
                            mStudy.setComplete(Boolean.parseBoolean(parser.nextText()));
                        }
                        /* Items */
                        else if (tagname.equalsIgnoreCase("items"))
                        {
                            mStudy.setItems(this.parseItemList(parser));
                        }
                        /* PQMethod */
                        else if (tagname.equalsIgnoreCase("pqmethod_folder"))
                        {
                            // TODO:PQMethod - dont know what should happen here (jw)
                        }
                        /* Questions */
                        else if (tagname.equalsIgnoreCase("questions"))
                        {
                            List<Question> questions=this.parseQuestions(parser);
                            if(questions!=null){
                                this.mStudy.setQuestions(questions);
                            }else{
                                android.util.Log.i("XMLImportHandler","parseXMLFile() -- list of questions from parseQuestions is null");
                            }
                        }
                        /* Pyramid */
                        else if (tagname.equalsIgnoreCase("pyramid"))
                        {
                            this.mStudy.setPyramid(this.parsePyramid(parser));
                        }
                        
                        /*QSorts*/
                        else if (tagname.equalsIgnoreCase("qsorts"))
                        {
                            this.mStudy.setQSorts(this.parseAllQSorts(parser));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
 
        } catch (IOException e){
            e.printStackTrace();
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return mStudy;

    }
    
    /* ###############################################################################################################################
     * Item parsing
     * ############################################################################################################################ */

    /**
     * parses items
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Item> parseItemList(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Item> items=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("items")){
            items=new LinkedList<Item>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseItems() -- not a START_TAG or tagName!='items'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("picture")){
                        items.add(this.parsePicture(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("items")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return items;
    }
    
    private Picture parsePicture(XmlPullParser parser) throws XmlPullParserException, IOException {
        Picture picture=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("picture")){
            String itemName = parser.getAttributeValue(null, "name");
            String itemOriginalId = parser.getAttributeValue(null, "original_id");
            String itemColumn = parser.getAttributeValue(null, "column");
            String itemRow = parser.getAttributeValue(null, "row");
            String itemStatement = (parser.nextText());
            if(itemOriginalId!=null){
                picture=new Picture(-1);
                picture.setId(Integer.parseInt(itemOriginalId));
            }
            if(itemName!=null){
                picture.setFilePath(itemName);
            }
            if(itemColumn!=null && itemRow!=null){
                picture.setPosition(Integer.parseInt(itemRow),Integer.parseInt(itemColumn));
            }
            if(itemStatement!=null){
                picture.setStatement(itemStatement);
            }
            if (itemName != null && itemStatement != null){
                mStatements.put(itemName, itemStatement);
            }
        }else{
            android.util.Log.e("XMLImportHandler", "parsePicture() -- not a START_TAG or tagName!='picture'");
        }
        
        return picture;
    }

    /* ###############################################################################################################################
     * Question parsing
     * ############################################################################################################################ */    

    /**
     * parses questions
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Question> parseQuestions(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Question> questions=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("questions")){
            questions=new LinkedList<Question>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseQuestions() -- not a START_TAG or tagName!='questions'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("open_question")){
                        questions.add(this.parseOpenQuestion(parser));
                    }
                    else if(tagname.equalsIgnoreCase("closed_question")){
                        questions.add(this.parseClosedQuestion(parser));
                    }
                    else if(tagname.equalsIgnoreCase("scale_question")){
                        questions.add(this.parseScaleQuestion(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("questions")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return questions;
    }

    private Question parseOpenQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        OpenQuestion result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("open_question")){
            run=true;
            result=new OpenQuestion(-1,-1);
        }else{
            android.util.Log.e("XMLImportHandler", "parseOpenQuestion() -- not a START_TAG or tagName!='open_question'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("original_question_id")){
                        result.setId(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_text")){
                        result.setText(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("question_is_post")){
                        result.setIsPost(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_order")){
                        result.setOrderNumber(Integer.parseInt(parser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("open_question")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private Question parseClosedQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        ClosedQuestion result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("closed_question")){
            run=true;
            result=new ClosedQuestion(-1,-1);
        }else{
            android.util.Log.e("XMLImportHandler", "parseClosedQuestion() -- not a START_TAG or tagName!='closed_question'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("original_question_id")){
                        result.setId(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_text")){
                        result.setText(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("question_is_post")){
                        result.setIsPost(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_order")){
                        result.setOrderNumber(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("is_multiple_choice")){
                        result.setMultipleChoice(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("has_open_field")){
                        result.setOpenField(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("possible_answers")){
                        List<String> possAnswers=this.parsePossibleAnswers(parser);
                        for(int a=0;a<possAnswers.size();a++){
                            result.addPossibleAnswer(possAnswers.get(a));
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("closed_question")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private List<String> parsePossibleAnswers(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<String> result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("possible_answers")){
            run=true;
            result=new LinkedList<String>();
        }else{
            android.util.Log.e("XMLImportHandler", "parsePossibleAnswers() -- not a START_TAG or tagName!='possible_answers'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("answer")){
                        result.add(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("possible_answers")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private Question parseScaleQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        ScaleQuestion result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("scale_question")){
            run=true;
            result=new ScaleQuestion(-1,-1);
        }else{
            android.util.Log.e("XMLImportHandler", "parseScaleQuestion() -- not a START_TAG or tagName!='scale_question'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("original_question_id")){
                        result.setId(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_text")){
                        result.setText(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("question_is_post")){
                        result.setIsPost(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("question_order")){
                        result.setOrderNumber(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("scales")){
                        result.setScales(this.parseScaleList(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("scale_question")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * Method for parsing the scale
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Scale> parseScaleList(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Scale> result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("scales")){
            run=true;
            result=new LinkedList<Scale>();
        }else{
            android.util.Log.e("XMLImportHandler", "parseScaleList() -- not a START_TAG or tagName!='scales'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("scale")){
                        result.add(this.parseScale(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("scales")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }
    
    /**
     * Method for parsing the scale
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Scale parseScale(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        Scale result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("scale")){
            run=true;
            result=new Scale(-1);
        }else{
            android.util.Log.e("XMLImportHandler", "parseScale() -- not a START_TAG or tagName!='scale'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("original_id")){
                        result.setId(Integer.parseInt(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("pole_left")){
                        result.setPoleLeft(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("pole_right")){
                        result.setPoleRight(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("scale_values")){
                        List<String> svalues=this.parseScaleValues(parser);
                        for(String value:svalues){
                            result.addScaleValue(value);
                        }
                    }else if(tagname.equalsIgnoreCase("selected_value_index")){
                        result.setSelectedValueIndex(Integer.parseInt(parser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("scale")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }
    
    private List<String> parseScaleValues(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<String> result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("scale_values")){
            run=true;
            result=new LinkedList<String>();
        }else{
            android.util.Log.e("XMLImportHandler", "parseScaleValues() -- not a START_TAG or tagName!='scale_values'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("value")){
                        result.add(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("scale_values")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }
    
    /* ###############################################################################################################################
     * Pyramid parsing
     * ############################################################################################################################ */
    private Pyramid parsePyramid(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        Pyramid result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("pyramid")){
            run=true;
            result=new Pyramid(-1);
        }else{
            android.util.Log.e("XMLImportHandler", "parsePyramid() -- not a START_TAG or tagName!='pyramid'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("pyramid_pole_left")){
                        result.setPoleLeft(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("pyramid_pole_right")){
                        result.setPoleRight(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("pyramid_shape")){
                        result.fromUniqueString(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("pyramid")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }
    
    /* ###############################################################################################################################
     * QSorts parsing
     * ############################################################################################################################ */
    
    private List<QSort> parseAllQSorts(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<QSort> qsorts=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("qsorts")){
            qsorts=new LinkedList<QSort>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseAllQSorts() -- not a START_TAG or tagName!='qsorts'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("qsort")){
                        qsorts.add(this.parseQSort(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("qsorts")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return qsorts;
    }
    
    private QSort parseQSort(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        QSort qsort=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("qsort")){
            qsort=new QSort(-1,-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseQSort() -- not a START_TAG or tagName!='qsort'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("qsort_name")){
                        qsort.setName(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("qsort_acronym")){
                        qsort.setAcronym(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("qsort_finished")){
                        qsort.setFinished(Boolean.parseBoolean(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("qsort_starttime")){
                        qsort.setStartTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("qsort_endtime")){
                        qsort.setEndTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("logs")){
                        List<Log> logs=this.parseLogList(parser);
                        for(Log l:logs){
                            qsort.setLog(l.getPhase(), l);
                        }
                    }else if(tagname.equalsIgnoreCase("sorted_items")){
                        qsort.setSortedItems(this.parseSortedItems(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("qsort")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return qsort;
    }

    private List<Log> parseLogList(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Log> logs=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("logs")){
            logs=new LinkedList<Log>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseLogList() -- not a START_TAG or tagName!='logs'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("log")){
                        logs.add(this.parseLog(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("logs")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return logs;
    }

    private Log parseLog(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        Log log=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("log")){
            log=new Log(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseLog() -- not a START_TAG or tagName!='log'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("log_phase")){
                        log.setPhase(Phase.valueOf(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("audio")){
                        log.setAudio(this.parseAudio(parser));
                    }else if(tagname.equalsIgnoreCase("answers")){
                        log.setAnswers(this.parseAllAnswers(parser));
                    }else if(tagname.equalsIgnoreCase("log_entries")){
                        log.setLogEntries(this.parseAllLogEntries(parser));
                    }else if(tagname.equalsIgnoreCase("notes")){
                        log.setNotes(this.parseAllNotes(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("log")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return log;
    }

    private AudioRecord parseAudio(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        AudioRecord audiorecord=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("audio")){
            audiorecord=new AudioRecord(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseAudio() -- not a START_TAG or tagName!='audio'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("audio_path")){
                        audiorecord.setFilePath(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("audio_number_parts")){
                        audiorecord.setPartNumber(Integer.parseInt(parser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("audio")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return audiorecord;
    }

    private List<Answer> parseAllAnswers(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Answer> answers=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("answers")){
            answers=new LinkedList<Answer>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseAllAnswers() -- not a START_TAG or tagName!='answers'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("open_answer")){
                        answers.add(this.parseOpenAnswer(parser));
                    }else if(tagname.equalsIgnoreCase("closed_answer")){
                        answers.add(this.parseClosedAnswer(parser));
                    }else if(tagname.equalsIgnoreCase("scale_answer")){
                        answers.add(this.parseScaleAnswer(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("answers")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return answers;
    }

    private OpenAnswer parseOpenAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        OpenAnswer answer=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("open_answer")){
            answer=new OpenAnswer(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseOpenAnswer() -- not a START_TAG or tagName!='open_answer'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("old_question_id")){
                        answer.setQuestion(new OpenQuestion(Integer.parseInt(parser.nextText()),-1));
                    }else if(tagname.equalsIgnoreCase("time")){
                        answer.setTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("answer_string")){
                        answer.setAnswer(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("open_answer")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return answer;
    }

    private ClosedAnswer parseClosedAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        ClosedAnswer answer=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("closed_answer")){
            answer=new ClosedAnswer(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseClosedAnswer() -- not a START_TAG or tagName!='closed_answer'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("old_question_id")){
                        answer.setQuestion(new ClosedQuestion(Integer.parseInt(parser.nextText()),-1));
                    }else if(tagname.equalsIgnoreCase("time")){
                        answer.setTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("answers_list")){
                        answer.setAnswers(this.parseClosedAnswerValues(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("closed_answer")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return answer;
    }
    
    private List<String> parseClosedAnswerValues(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<String> result=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("answers_list")){
            run=true;
            result=new LinkedList<String>();
        }else{
            android.util.Log.e("XMLImportHandler", "parseClosedAnswerValues() -- not a START_TAG or tagName!='answers_list'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("value")){
                        result.add(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("answers_list")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private ScaleAnswer parseScaleAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        ScaleAnswer answer=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("scale_answer")){
            answer=new ScaleAnswer(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseScaleAnswer() -- not a START_TAG or tagName!='scale_answer'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("old_question_id")){
                        answer.setQuestion(new ScaleQuestion(Integer.parseInt(parser.nextText()),-1));
                    }else if(tagname.equalsIgnoreCase("time")){
                        answer.setTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("scales")){
                        answer.setScales(this.parseScaleList(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("scale_answer")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return answer;
    }

    private List<LogEntry> parseAllLogEntries(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<LogEntry> logEntries=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("log_entries")){
            logEntries=new LinkedList<LogEntry>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseAllLogEntries() -- not a START_TAG or tagName!='log_entries'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("log_entry")){
                        logEntries.add(this.parseLogEntry(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("log_entries")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return logEntries;
    }

    private LogEntry parseLogEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        LogEntry logEntry=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("log_entry")){
            logEntry=new LogEntry(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseLogEntry() -- not a START_TAG or tagName!='log_entry'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("log_entry_timestamp")){
                        logEntry.setTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("log_old_coor")){
                        String[] coors=parser.nextText().split("/");
                        if(coors.length==2){
                            logEntry.setFrom(Integer.parseInt(coors[0]), Integer.parseInt(coors[1]));
                        }else{
                            android.util.Log.e("XMLImportHandler", "parseLogEntry() -- length of splitted string for -OLD- coordinates in logEntry is wrong!");
                        }
                    }else if(tagname.equalsIgnoreCase("log_new_coor")){
                        String[] coors=parser.nextText().split("/");
                        if(coors.length==2){
                            logEntry.setTo(Integer.parseInt(coors[0]), Integer.parseInt(coors[1]));
                        }else{
                            android.util.Log.e("XMLImportHandler", "parseLogEntry() -- length of splitted string for -NEW- coordinates in logEntry is wrong!");
                        }
                    }else if(tagname.equalsIgnoreCase("item_original_id")){
                        logEntry.setItem(new Picture(Integer.parseInt(parser.nextText())));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("log_entry")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return logEntry;
    }

    private List<Note> parseAllNotes(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Note> notes=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("notes")){
            notes=new LinkedList<Note>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseAllNotes() -- not a START_TAG or tagName!='notes'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("note")){
                        notes.add(this.parseNote(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("notes")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return notes;
    }

    private Note parseNote(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        Note note=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("note")){
            note=new Note(-1);
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseNote() -- not a START_TAG or tagName!='note'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("note_timestamp")){
                        note.setTime(Long.parseLong(parser.nextText()));
                    }else if(tagname.equalsIgnoreCase("note_title")){
                        note.setTitle(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("note_text")){
                        note.setText(parser.nextText());
                    }else if(tagname.equalsIgnoreCase("note_phase")){
                        note.setPhase(Phase.valueOf(parser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("note")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return note;
    }

    private List<Item> parseSortedItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean run=false;
        List<Item> items=null;
        
        int eventType = parser.getEventType();
        String tagname = parser.getName();
        
        if(eventType==XmlPullParser.START_TAG && tagname.equalsIgnoreCase("sorted_items")){
            items=new LinkedList<Item>();
            run=true;
        }else{
            android.util.Log.e("XMLImportHandler", "parseSortedItems() -- not a START_TAG or tagName!='sorted_items'");
        }
        
        while (run)
        {
            eventType=parser.next();
            tagname = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("picture")){
                        items.add(this.parsePicture(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equals("sorted_items")){
                        run=false;
                    }
                    break;
                default:
                    break;
            }
        }
        return items;
    }

    /* ###############################################################################################################################
     * additional methods
     * ############################################################################################################################ */
    
    /**
    * Returns parsed item names with corresponding statements in form of
    * Map<String, String>. Should be generally called only after parser call.
    * 
    * @param s not currently used
    * @return
    */
    public Map<String, String> getStatements(String s)
    {
        return mStatements;
    }
}
/*
 * Based on tutorial:
 * http://www.javacodegeeks.com/2010/11/boost-android-xml-parsing-xml-pull.html
 */
