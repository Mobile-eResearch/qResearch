
package de.eresearch.app.gui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import de.eresearch.app.R;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetStudyMetaTask;

public class StudyViewMetaDataFragment extends Fragment implements GetStudyMetaTask.Callbacks {

    public static final String STUDY_ID = "de.eresearch.app.gui.study_ID";

    private static TextView mTitle;
    private static TextView mAuthor;
    private static TextView mDescription;
    private static TextView mQuestion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_studies_merge_detail,
                container, false);
        mTitle = (EditText) rootView.findViewById(R.id.meta_name);
        mAuthor = (EditText) rootView.findViewById(R.id.meta_author);
        mDescription = (EditText) rootView.findViewById(R.id.meta_desc);
        mQuestion = (EditText) rootView.findViewById(R.id.meta_question);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        GetStudyMetaTask t = new GetStudyMetaTask(this, this.getActivity(),
                getArguments().getInt(STUDY_ID));
        t.execute();
    }

    @Override
    public void onGetStudyMetaTaskTaskUpdate(Study study) {
        mTitle.setText(study.getName());
        mTitle.setEnabled(false);
        mTitle.setTextColor(Color.BLACK);
        mAuthor.setText(study.getAuthor());
        mAuthor.setEnabled(false);
        mAuthor.setTextColor(Color.BLACK);
        mDescription.setText(study.getDescription());
        mDescription.setEnabled(false);
        mDescription.setTextColor(Color.BLACK);
        mQuestion.setText(study.getResearchQuestion());
        mQuestion.setEnabled(false);
        mQuestion.setTextColor(Color.BLACK);
    }
}
