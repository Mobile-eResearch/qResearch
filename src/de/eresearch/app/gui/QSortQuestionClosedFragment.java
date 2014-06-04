package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.ClosedQuestion;

/**
 * Provides Fragment to prepare closed questions in context of a survey
 * 
 * In context of {@link QSortQuestionActivity})
 * @author Henrik
 */
public class QSortQuestionClosedFragment extends Fragment {

    private QSortQuestionContainer container;
    private TextView text;

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        container = QSortQuestionContainer.getQSortQuestionContainer();

        View rootView = inflater.inflate(R.layout.fragment_qsort_question_closed, c, false);
        ClosedQuestion question = (ClosedQuestion) container.filteredList.get(container.location);

        rootView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            
            @Override
            public void onViewDetachedFromWindow(View v) {
                container.filteredList.set(container.location, container.filteredList.get(container.location)); 
            }
            
            @Override
            public void onViewAttachedToWindow(View v) {
                
            }
        });

        ((ViewGroup) rootView).addView(question.getQuestionAnswerEditView(getActivity()));

        return rootView;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        text = (TextView) getActivity().findViewById(R.id.fragment_qsort_question_closed_field);
        text.setText(container.filteredList.get(container.location).getText());
    }

    public void onResume() {
        super.onResume();
    }

    

}
