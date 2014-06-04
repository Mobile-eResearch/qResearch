
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.Question;

/**
 * Provides Fragment to prepare open questions in context of a survey In context
 * of {@link QSortQuestionActivity})
 * 
 * @author Henrik
 */
public class QSortQuestionOpenFragment extends Fragment {

    private TextView text;
    private EditText editText;
    private QSortQuestionContainer container;

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qsort_question_open, container, false);
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
        text = (TextView) getActivity().findViewById(R.id.fragment_qsort_question_open_field);
        editText = (EditText) getActivity().findViewById(
                R.id.fragment_qsort_question_open_answerfield);
        container = QSortQuestionContainer.getQSortQuestionContainer();
        text.setText(container.filteredList.get(container.location).getText());

        if (container.filteredList.get(container.location).getAnswer() != null) {
            OpenAnswer answer = (OpenAnswer) container.filteredList.get(container.location)
                    .getAnswer();
            editText.setText(answer.getAnswer());
        }
        
        //save initial the empty answer for log
        OpenAnswer openAnswer = null;
        openAnswer = new OpenAnswer(-1);
        openAnswer.setAnswer("");
        openAnswer.setTime(CurrentQSort.getTimer().getTime());
        Question saveQuestion = container.filteredList.get(container.location);
        saveQuestion.setAnswer(openAnswer);
        container.filteredList.set(container.location, saveQuestion);
        
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                OpenAnswer openAnswer = null;
                String answerText = s.toString();
                View parent = getActivity().findViewById(R.id.fragment_qsort_question_nav);
                View child = parent.findViewById(container.location + 100000);

                if (!answerText.trim().isEmpty()) {
                    openAnswer = new OpenAnswer(-1);
                    openAnswer.setAnswer(answerText);
                    openAnswer.setTime(CurrentQSort.getTimer().getTime());
                    child.setBackgroundColor(Color.RED);
                } else {
                    child.setBackgroundResource(android.R.drawable.btn_default);
                }
                Question saveQuestion = container.filteredList.get(container.location);
                saveQuestion.setAnswer(openAnswer);
                container.filteredList.set(container.location, saveQuestion);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

    }

    public void onResume() {
        super.onResume();
    }

}
