package de.eresearch.app.gui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.QSortsPyramidDialog;
import de.eresearch.app.logic.model.Loggable;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.QSort;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.qsort.GetLoggablesTask;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortTask;
import de.eresearch.app.logic.tasks.common.study.GetFullStudyTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Marcel L.
 * Provides Fragment for studies detail view (frame on the right side, next to 
 * the {@link StudiesListFragment})
 *
 */
public class QSortsDetailFragment extends Fragment implements GetQSortTask.Callbacks, GetFullStudyTask.Callbacks, GetLoggablesTask.Callbacks{
    
    public static final String QSORT_ID = "de.eresearch.app.gui.qsort_id"; 
    public static final String IS_POSTSORT = "de.eresearch.app.gui.is_postsort"; 

    public static final String STUDY_ID = "de.eresearch.app.gui.study_id";
    
    private QSort mQSort;
    private Study mStudy;
    private int mStudyId;
    private TextView mProband;
    private TextView mDuration;
    private TextView mResearcher;
    private ListView menu_logs;
    private ListView menu_questions;
    private ListView menu_notes;
    private List<Question> questions;
    private boolean hasPost;
    private boolean hasPre;
    private boolean hasInterview;
    private boolean hasPyramid;
    private boolean hasQSort;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onStart(){
        super.onStart();
        
        int qsortId = getArguments().getInt(QSORT_ID);
        
        GetQSortTask t = new GetQSortTask(this.getActivity(),qsortId , 
                this);
        t.execute();
        
        
        Bundle extras = getActivity().getIntent().getExtras();
        mStudyId = extras.getInt(QSortsActivity.STUDY_ID);
        
        GetFullStudyTask t2 = new GetFullStudyTask(this.getActivity(),this, mStudyId);
        t2.execute();
        
        GetLoggablesTask t3 = new GetLoggablesTask(this.getActivity(), this, qsortId, Phase.INTERVIEW);
        t3.execute();
        

    }
    
    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qsorts_detail,
                container, false);


        menu_logs = (ListView) rootView.findViewById(R.id.qsortsDetailMenuLogs);  
        menu_logs.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              
              switch (position) {
                  case 0: onDistributionPyramidClick(); break;
                  case 1: onQuestionsPreSortLogClick(); break;
                  case 2: onQSortLogClick(); break;
                  case 3: onQuestionsPostSortLogClick(); break;
                  case 4: onInterviewLogClick(); break;
                  default: break;
              }
            }
        }
                );


        
        menu_questions = (ListView) rootView.findViewById(R.id.qsortsDetailMenuQuestions);
        menu_questions.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              
              switch (position) {
                  case 0: onQuestionsPreSortClick(); break;
                  case 1: onQuestionsPostSortClick(); break;
                  default: break;
              }
            }
        }
                );
        
        menu_notes = (ListView) rootView.findViewById(R.id.qsortsDetailMenuNotes);
        
        menu_notes.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              
               switch (position){
                   case 0: onNotesClick(); break;
                  
                   default: break;
               }
           }
        }
                );
        
        updateListViewHeight(menu_logs);
        updateListViewHeight(menu_questions);
        updateListViewHeight(menu_notes);
        mProband = (TextView) rootView.findViewById(R.id.text_proband);
        mDuration =  (TextView) rootView.findViewById(R.id.text_time);
        mResearcher =  (TextView) rootView.findViewById(R.id.text_researcher);
        return rootView;
    }
    
    private void onDistributionPyramidClick(){
        if (hasPyramid){

        
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point(); 
        display.getSize(size);
        
        size.y = size.y/2;
        size.x = (int) (size.x/2);
        
        QSortsPyramidDialog dialog = new QSortsPyramidDialog(this.getActivity(), mQSort,size , 0, mStudy.getPyramid());
        dialog.show(getFragmentManager(), "QSortPyraView");
        }
    }

    private void onQuestionsPreSortLogClick(){
        
        if (hasPre){
           
        Intent intent = new Intent(getActivity(), QSortLogActivity.class);
        intent.putExtra(LogParentActivity.QSORT_ID, mQSort.getId());
        intent.putExtra(LogParentActivity.PHASE, Phase.QUESTIONS_PRE);
        intent.putExtra(LogParentActivity.AUDIORECORD, mQSort.hasAudioRecord());
        startActivity(intent);
        }
        
    }
    
    private void onQSortLogClick(){
        
        if (hasQSort){

        Intent intent = new Intent(getActivity(), QSortLogActivity.class);
        intent.putExtra(LogParentActivity.QSORT_ID, mQSort.getId());
        intent.putExtra(LogParentActivity.PHASE, Phase.Q_SORT);
        intent.putExtra(LogParentActivity.AUDIORECORD, mQSort.hasAudioRecord());
        startActivity(intent);
        }
        
    }
    
    private void onQuestionsPostSortLogClick(){
        if (hasPost){

        Intent intent = new Intent(getActivity(), QSortLogActivity.class);
        intent.putExtra(LogParentActivity.QSORT_ID, mQSort.getId());
        intent.putExtra(LogParentActivity.PHASE, Phase.QUESTIONS_POST);
        intent.putExtra(LogParentActivity.AUDIORECORD, mQSort.hasAudioRecord());
        startActivity(intent);
        }
    }
    
    private void onInterviewLogClick(){
        if (hasInterview){

        Intent intent = new Intent(getActivity(), QSortLogActivity.class);
        intent.putExtra(LogParentActivity.QSORT_ID, mQSort.getId());
        intent.putExtra(LogParentActivity.PHASE, Phase.INTERVIEW);
        intent.putExtra(LogParentActivity.AUDIORECORD, mQSort.hasAudioRecord());
        startActivity(intent);
        }
    }
    
    private void onQuestionsPreSortClick(){
        if(hasPre){
        Intent intent = new Intent(getActivity(), QuestionAnswerActivity.class);
        intent.putExtra(QSortsDetailFragment.QSORT_ID, mQSort.getId());
        intent.putExtra(QSortsActivity.STUDY_ID, getActivity().getIntent().getExtras().getInt(QSortsActivity.STUDY_ID));
        intent.putExtra("QSortName", mQSort.getName());
        intent.putExtra(QSortsDetailFragment.IS_POSTSORT, false);
        startActivity(intent);
        }
        
      
    }
    
    private void onQuestionsPostSortClick(){
        if(hasPost){
        Intent intent = new Intent(getActivity(), QuestionAnswerActivity.class);
        intent.putExtra(QSortsDetailFragment.QSORT_ID, mQSort.getId());
        intent.putExtra(QSortsActivity.STUDY_ID, getActivity().getIntent().getExtras().getInt(QSortsActivity.STUDY_ID));
        intent.putExtra("QSortName", mQSort.getName());
        intent.putExtra(QSortsDetailFragment.IS_POSTSORT, true);
        startActivity(intent);        
        }
    }
    
    
    private void onNotesClick(){
        Intent intent = new Intent(getActivity(), NotesActivity.class);
        intent.putExtra(NotesActivity.QSORT_ID, mQSort.getId());
        intent.putExtra(NotesActivity.QSORT_NAME, mQSort.getName());
        intent.putExtra(NotesActivity.STUDY_NAME, getActivity().getIntent().getExtras().getString("studyName"));

        
        startActivity(intent);  
        
    }
    
   
    

    
    @Override
    public void onGetQSortTaskUpdate(QSort qsort) {
        mQSort = qsort;
        mDuration.setText(getQSortDuration(mQSort));
        mProband.setText(mQSort.getName());
        mResearcher.setText(mQSort.getAcronym());
        
    }
    /**Method to calculate the duration of a QSort.
     * 
     * @param q Current QSort
     * @return duration of QSort
     */
    
    private String getQSortDuration(QSort q){
        long startTime = q.getStartTime();
        long endTime = q.getEndTime();
        
        SimpleDateFormat start = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat end = new SimpleDateFormat("dd.MM.yyyy HH:mm");


        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        String startTimeString = start.format(startDate);
        String endTimeString = end.format(endDate);
        
        
        String duration = startTimeString + " - " + endTimeString;
        return duration;
    }
    
    /**
     * BUFGIX for listview in scrollview. A ListView in a ScrollView is bugged and only shows one row. This method fixes that issue.
     * @param myListView
     */
    
    public static void updateListViewHeight(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {            
                 return;
        }
       //get listview height
       int totalHeight = 0;
       int adapterCount = myListAdapter.getCount();
       for (int size = 0; size < adapterCount ; size++) {
           View listItem = myListAdapter.getView(size, null, myListView);
           listItem.measure(0, 0);
           totalHeight += listItem.getMeasuredHeight();
       }
       //Change Height of ListView 
       ViewGroup.LayoutParams params = myListView.getLayoutParams();
       params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount - 1));
       myListView.setLayoutParams(params);
   }
   
    public QSort getLoadedStudy(){
        return mQSort;
    }

    @Override
    public void onGetFullStudyTask(Study study) {
        mStudy = study;
        questions = mStudy.getAllQuestions();
        disableListItems();
    }
    
    
    private void disableListItems() {
        hasPyramid = true;
        hasQSort = true;
        if (mQSort == null ||  mQSort.getSortedItems() == null || mQSort.getSortedItems().isEmpty()){
            menu_logs.getChildAt(0).setEnabled(false);
            hasPyramid = false;
        }
        
        if (mQSort == null || mQSort.getLog(Phase.Q_SORT).getLogEntries().isEmpty()){
            menu_logs.getChildAt(2).setEnabled(false);
            hasQSort = false;
        }
        
        menu_logs.getChildAt(3).setEnabled(false);
        menu_questions.getChildAt(1).setEnabled(false);
        menu_logs.getChildAt(1).setEnabled(false);
        menu_questions.getChildAt(0).setEnabled(false);
        
        for (Question q: questions){
            if (q.isPost()){
                menu_logs.getChildAt(3).setEnabled(true);
                menu_questions.getChildAt(1).setEnabled(true);
                hasPost = true;
                
            }
            if(!q.isPost()){
                menu_logs.getChildAt(1).setEnabled(true);
                menu_questions.getChildAt(0).setEnabled(true);
                hasPre = true;
                
            }
        }

    }

    @Override
    public void onLoggablesGotten(List<Loggable> loggables) {
        hasInterview = true;
        if (loggables.isEmpty() && mQSort.getLog(Phase.INTERVIEW).getAudio() == null){
            menu_logs.getChildAt(4).setEnabled(false);
            hasInterview = false;
        }
    }
}
