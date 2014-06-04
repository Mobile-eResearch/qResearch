
package de.eresearch.app.gui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.eresearch.app.R;
import de.eresearch.app.gui.dialogs.QSortStartDialog;
import de.eresearch.app.gui.dialogs.StudyDeleteDialog;
import de.eresearch.app.gui.dialogs.StudyEditDialog;
import de.eresearch.app.gui.dialogs.StudyExportDialog;
import de.eresearch.app.logic.model.Study;
import de.eresearch.app.logic.tasks.common.study.GetStudyMetaTask;

/**
 * Provides Fragment for studies detail view (frame on the right side, next to
 * the {@link StudiesListFragment})
 * 
 * @author thg
 */
public class StudiesDetailFragment extends Fragment implements GetStudyMetaTask.Callbacks
{
    public static final String STUDY_ID = "de.eresearch.app.gui.studie_id";

    private Study mStudy;

    private static TextView mTitle;
    private static TextView mAuthor;
    private TextView mDescription;
    private TextView mQuestion;
    private ListView mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_studies_detail,
                container, false);

        mMenu = (ListView) rootView.findViewById(R.id.studiesDetailMenu);
        mMenu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        onQSortStartClick();
                        break;
                    case 1:
                        onQSortViewClick();
                        break;
                    case 2:
                        onStudyEditClick();
                        break;
                    case 3:
                        onStudyExportClick();
                        break;
                    case 4:
                        onStudyDeleteClick();
                        break;
                    default:
                        break;
                }
            }
        }
                );

        mTitle = (TextView) rootView.findViewById(R.id.text_name);
        mAuthor = (TextView) rootView.findViewById(R.id.text_author);
        mDescription = (TextView) rootView.findViewById(R.id.text_description);
        mQuestion = (TextView) rootView.findViewById(R.id.text_question);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        GetStudyMetaTask t = new GetStudyMetaTask(this, this.getActivity(),
                getArguments().getInt(STUDY_ID));
        t.execute();
    }

    /**
     * Callback form StudyMetaDataTask
     */
    @Override
    public void onGetStudyMetaTaskTaskUpdate(Study study) {
        mStudy = study;

        // if there are qsorts disable edit button
        if (!study.getQSorts().isEmpty())
            mMenu.getChildAt(2).setEnabled(false);
        else
            mMenu.getChildAt(2).setEnabled(true);

        // set the TextViews
        mTitle.setText(study.getName());
        mAuthor.setText(mStudy.getAuthor());
        mDescription.setText(mStudy.getDescription());
        mQuestion.setText(mStudy.getResearchQuestion());
    }

    /**
     * on click button: start a QSort
     */
    private void onQSortStartClick() {
        // If study complete, you can start a QSort
        if (mStudy.isComplete()) {
            QSortStartDialog dialog = new QSortStartDialog(getActivity(), mStudy.getId());
            dialog.qsortFinishPhaseDialog().show();
        }
        // if not complete, make toast!
        else
            Toast.makeText(getActivity(), R.string.error_study_unfinished,
                    Toast.LENGTH_LONG).show();
    }

    /**
     * on click button: view QSorts
     */
    private void onQSortViewClick() {
        Intent intent = new Intent(getActivity(), QSortsActivity.class);
        intent.putExtra(QSortsActivity.STUDY_ID, mStudy.getId());
        intent.putExtra("studyName", mStudy.getName());
        startActivity(intent);
    }

    /**
     * on click button: edit study
     */
    private void onStudyEditClick() {

        // TODO: better error handling
        if (mStudy == null) {
            Toast.makeText(getActivity(),
                    "Error", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // if there are QSorts show error as toast
        if (!mStudy.getQSorts().isEmpty()) {
            Toast.makeText(getActivity(),
                    getString(R.string.error_there_are_qsorts), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        StudyEditDialog dialog = new StudyEditDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(StudyEditDialog.STUDY_ID, mStudy.getId());
        arguments.putString(StudyEditDialog.STUDY_NAME, mStudy.getName());

        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "StudyEditDialog");

    }

    /**
     * on click button: export study
     */
    private void onStudyExportClick() {
        StudyExportDialog dialog = new StudyExportDialog();
        dialog.setStudyID(mStudy.getId());
        dialog.setStudyName(mStudy.toString());
        dialog.setContext((StudiesActivity) this.getActivity());
        dialog.show(getFragmentManager(), "StudyExportDialog");
    }

    /**
     * on click button: delete study
     */
    private void onStudyDeleteClick() {

        // TODO: better error handling
        if (mStudy == null) {
            Toast.makeText(getActivity(),
                    "Error", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        StudyDeleteDialog dialog = new StudyDeleteDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(StudyDeleteDialog.STUDY_ID, mStudy.getId());
        arguments.putString(StudyDeleteDialog.STUDY_NAME, mStudy.getName());

        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "StudyDeleteDialog");
    }

    public Study getLoadedStudy() {
        return mStudy;
    }

}
