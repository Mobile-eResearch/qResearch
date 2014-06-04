
package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Pyramid;
import de.eresearch.app.logic.tasks.misc.LoadPyramidEditViewTask;

/**
 * Provides Fragment for edit/create a pyramid for a study (frame on the right
 * side, next to the {@link StudyEditListFragment})
 * 
 * @author thg
 */
public class StudyEditPyramidFragment extends Fragment {

    /**
     * Name of debug log tag. *
     */
    private static final String LOG_TAG = "StudyEditPyramidFragment";

    private StudyEditContainer sec;

    /**
     * The Pyramid
     */
    private Pyramid mPyramid;

    // Declared the Views we need
    private EditText mDist;
    private EditText mCountField;
    private EditText mCountColumns;
    private TextView mIsValid;
    private EditText mLeftPole;
    private EditText mRightPole;
    private TextView mPoleTitle;
    private TextView mDistTitle;
    private FrameLayout mPyramidFrame;
    private View mPyramidView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sec = StudyEditContainer.getStudyEditContainer();
        mPyramid = sec.getStudy().getPyramid();
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "run onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_study_edit_pyramid,
                container, false);

        // set all the views
        mPyramidFrame = (FrameLayout) rootView.findViewById(R.id.study_edit_pyramid_container);
        mDist = (EditText) rootView.findViewById(R.id.editText_distributing);
        mCountField = (EditText) rootView.findViewById(R.id.text_field_count_value);
        mCountColumns = (EditText) rootView.findViewById(R.id.text_column_count_value);
        mIsValid = (TextView) rootView.findViewById(R.id.text_isValid);
        mLeftPole = (EditText) rootView.findViewById(R.id.editText_left_pole);
        mRightPole = (EditText) rootView.findViewById(R.id.editText_right_pole);
        mDistTitle = (TextView) rootView.findViewById(R.id.text_distributing_title);
        mPoleTitle = (TextView) rootView.findViewById(R.id.text_distributing_poles_title);

        // fill out TextViews and EditViews
        mLeftPole.setText(mPyramid.getPoleLeft());
        mRightPole.setText(mPyramid.getPoleRight());
        mCountColumns.setText("" + mPyramid.getWidth());
        mCountField.setText("" + mPyramid.getSize());
        mDist.setText(mPyramid.toPQString());

        mLeftPole.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPyramid.setPoleLeft(mLeftPole.getText().toString());
                sec.isChanged = true;
                setStatus();

            }

        });

        mRightPole.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPyramid.setPoleRight(mRightPole.getText().toString());
                sec.isChanged = true;
                setStatus();

            }

        });

        // setup pyramid view

        LoadPyramidEditViewTask.Callbacks cb = new LoadPyramidEditViewTask.Callbacks() {
            @Override
            public void onPyramidEditViewLoaded(View editView) {
                mPyramidView = editView;
                mPyramidView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mDist.setText(mPyramid.toPQString());
                        mCountField.setText("" + mPyramid.getSize());
                        mCountColumns.setText("" + mPyramid.getWidth());
                        sec.isChanged = true;
                        setStatus();
                    }
                });

                // maybe replace the PyramidView
                if (((ViewGroup) mPyramidView.getParent()) != null)
                    ((ViewGroup) mPyramidView.getParent()).removeView(mPyramidView);
                mPyramidFrame.addView(mPyramidView);
            }
        };

        new LoadPyramidEditViewTask(getActivity(), cb, mPyramid).execute();

        // set the "edit status" (the red markers)
        setStatus();

        return rootView;
    }

    public void onPause() {
        super.onPause();
        sec.getStudy().setPyramid(mPyramid);
    }

    /**
     * set status fields in activity
     */
    private void setStatus() {

        boolean valid = true;

        if (mPyramid.isValid()) {
            mDist.setTextColor(getActivity().getResources().getColor(android.R.color.black));
            mDistTitle.setTextColor(getActivity().getResources().getColor(android.R.color.black));
        }
        else {
            valid = false;
            mIsValid.setText("False");
            mDist.setTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
            mDistTitle.setTextColor(getActivity().getResources().getColor(
                    android.R.color.holo_red_dark));
        }

        if (mPyramid.getPoleLeft() == null
                || mPyramid.getPoleLeft().equals("")
                || mPyramid.getPoleRight() == null
                || mPyramid.getPoleRight().equals("")) {
            valid = false;
            mPoleTitle.setTextColor(getActivity().getResources().getColor(
                    android.R.color.holo_red_dark));
        }
        else
            mPoleTitle.setTextColor(getActivity().getResources().getColor(android.R.color.black));

        if (!valid) {
            mIsValid.setVisibility(View.VISIBLE);
            mIsValid.setHeight(20);
            mIsValid.setTextColor(getActivity().getResources().getColor(
                    android.R.color.holo_red_dark));
            mIsValid.setText(getString(R.string.setallfields));
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Pyramid, false);
        }
        else {
            mIsValid.setVisibility(View.INVISIBLE);
            mIsValid.setHeight(0);
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Pyramid, true);
        }

        if (sec.getStudy().getItems().size() < sec.getStudy().getPyramid().getSize())
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Items, false);
        else
            ((StudyEditListFragment) getFragmentManager().findFragmentById(
                    R.id.fragment_study_edit_list))
                    .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Items, true);

    }

}
