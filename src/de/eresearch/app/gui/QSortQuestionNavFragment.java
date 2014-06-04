
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import de.eresearch.app.R;
import de.eresearch.app.R.drawable;
import de.eresearch.app.logic.model.ClosedAnswer;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.ScaleAnswer;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Provides Fragment navigation in QSort questions In context of
 * {@link QSortQuestionActivity})
 * 
 * @author Henrik
 */
public class QSortQuestionNavFragment extends Fragment {

    private QSortQuestionContainer container;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ImageButton imageButton;
    private int buttonCounter = -1;
    private HorizontalScrollView scroll;
    private LinearLayout linear;


    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        container = QSortQuestionContainer.getQSortQuestionContainer();

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        float dp = 1100f;
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);

        RelativeLayout layout3 = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(pixels,
                LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layout3.setLayoutParams(param);

        RelativeLayout layout4 = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        param2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layout4.setLayoutParams(param2);

        imageButton = new ImageButton(getActivity());
        imageButton.setImageResource(drawable.arrow_right);
        LayoutParams param3 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        imageButton.setLayoutParams(param3);

        View rootView = inflater.inflate(R.layout.fragment_qsort_question_nav, c, false);
        
        scroll = new HorizontalScrollView(getActivity());
        scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        
        fragmentManager = getFragmentManager();

//        View questionView = fragmentManager.findFragmentById(R.id.fragment_qsort_question)
//                .getView();
//        
//        fragmentManager = getFragmentManager();
//        Fragment nav = fragmentManager.findFragmentById(R.id.fragment_qsort_question_nav);
//        final FragmentTransaction ft = fragmentManager.beginTransaction();
//        ft.detach(nav);
//        ft.attach(nav);
//        ft.commit();

        linear = replaceNavBarButtons();
        
        scroll.addView(linear);
        layout3.addView(scroll);
        layout4.addView(imageButton);
        
        if (container.filteredList.size() == 1) {
            ((QSortQuestionActivity) getActivity()).setStateOfPhase(true);
        }

        ((ViewGroup) rootView).addView(layout3);
        ((ViewGroup) rootView).addView(layout4);

        return rootView;
    }

    private synchronized void replaceWithFragment(CharSequence charSequence) {

        if (container.filteredList.get(Integer.valueOf(
                charSequence.toString().trim()) - 1) instanceof ScaleQuestion) {
            Fragment scaleAnswerFragment = new QSortQuestionScaleFragment();
            Fragment navFragment = new QSortQuestionNavFragment();
            Log.d("before", Integer.toString(container.location));
            container.location = (Integer.valueOf(
                    charSequence.toString().trim()) - 1);
            Log.d("after", Integer.toString(container.location));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_qsort_question,
                    scaleAnswerFragment);
            fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            fragmentTransaction.commit();

        } else if (container.filteredList.get(Integer.valueOf(
                charSequence.toString().trim()) - 1) instanceof ClosedQuestion) {
            Fragment closedAnswerFragment = new QSortQuestionClosedFragment();
            Fragment navFragment = new QSortQuestionNavFragment();
            Log.d("before", Integer.toString(container.location));
            container.location = (Integer.valueOf(
                    charSequence.toString().trim()) - 1);
            Log.d("after", Integer.toString(container.location));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_qsort_question,
                    closedAnswerFragment);
            fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            fragmentTransaction.commit();

        } else if (container.filteredList.get(Integer.valueOf(
                charSequence.toString().trim()) - 1) instanceof OpenQuestion) {
            Fragment openAnswerFragment = new QSortQuestionOpenFragment();
            Fragment navFragment = new QSortQuestionNavFragment();
            Log.d("before", Integer.toString(container.location));
            container.location = (Integer.valueOf(
                    charSequence.toString().trim()) - 1);
            Log.d("after", Integer.toString(container.location));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_qsort_question,
                    openAnswerFragment);
            fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
            fragmentTransaction.commit();
        }

        isLastQuestion();

    }

    /**
     * Callback for fragment lifecycle onAttach().to start the questions of a
     * QSort
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * Callback for fragment lifecycle onDetach()
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void isLastQuestion() {
        if (container.location + 1 == container.filteredList.size()) {
            ((QSortQuestionActivity) getActivity()).setStateOfPhase(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addListenerOnButton();

    }

    private void addListenerOnButton() {

        imageButton.setOnClickListener((new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                fragmentManager = getFragmentManager();

                Log.d("Location", Integer.toString(container.location));
                Log.d("containerList", Integer.toString(container.filteredList.size()));

                if (container.location + 1 < container.filteredList.size()) {
                    Log.d("TRUE",
                            Integer.toString(container.location + 1) + "<"
                                    + Integer.toString(container.filteredList.size()));

                    if (container.filteredList.get(container.location + 1) instanceof ScaleQuestion)
                    {
                        Fragment scaleAnswerFragment = new QSortQuestionScaleFragment();
                        Fragment navFragment = new QSortQuestionNavFragment();
                        container.location++;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_qsort_question,
                                scaleAnswerFragment);
                        fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
                        fragmentTransaction.commit();

                    } else if (container.filteredList.get(container.location + 1) instanceof ClosedQuestion
                    ) {
                        Fragment closedAnswerFragment = new QSortQuestionClosedFragment();
                        Fragment navFragment = new QSortQuestionNavFragment();
                        container.location++;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_qsort_question,
                                closedAnswerFragment);
                        fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
                        fragmentTransaction.commit();

                    } else if (container.filteredList.get(container.location + 1) instanceof OpenQuestion
                    ) {
                        Fragment openAnswerFragment = new QSortQuestionOpenFragment();
                        Fragment navFragment = new QSortQuestionNavFragment();
                        container.location++;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_qsort_question,
                                openAnswerFragment);
                        fragmentTransaction.replace(R.id.fragment_qsort_question_nav, navFragment);
                        fragmentTransaction.commit();

                    }

                }

                isLastQuestion();

            }
        }));

    }

    private synchronized LinearLayout replaceNavBarButtons() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        layout.setGravity(1);
        layout.setOrientation(0);

        for (Question q : container.filteredList) {
            final Button t1 = new Button(getActivity());
            buttonCounter++;
            t1.setText(Integer.toString(buttonCounter + 1));
            t1.setId(buttonCounter+100000);
            t1.setBackgroundResource(android.R.drawable.btn_default);
            if (q instanceof OpenQuestion) {
                if (q.getAnswer() != null) {
                    t1.setBackgroundColor(Color.RED);
                }
            } else if (q instanceof ClosedQuestion) {
                ClosedAnswer closedA = (ClosedAnswer) q.getAnswer();
                if (closedA != null) {
                    if (closedA.isAnswered()) {
                        t1.setBackgroundColor(Color.RED);
                    }
                }

            } else if (q instanceof ScaleQuestion) {
                ScaleAnswer scaleA = (ScaleAnswer) q.getAnswer();
                if (scaleA != null) {
                    if (scaleA.isAnswered()) {
                        t1.setBackgroundColor(Color.RED);
                    }
                }

            }
            t1.setOnClickListener((new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    replaceWithFragment(t1.getText());
                }
            }));

            layout.addView(t1);

        }
        buttonCounter = 0;
        return layout;
    }

}
