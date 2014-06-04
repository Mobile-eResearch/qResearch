
package de.eresearch.app.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
 * Provides Fragment to prepare scale questions in context of a survey In
 * context of {@link QSortQuestionActivity})
 * 
 * @author Henrik
 */
public class QSortQuestionScaleFragment extends Fragment {

    private QSortQuestionContainer container;
    private TextView text;

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        container = QSortQuestionContainer.getQSortQuestionContainer();

        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_qsort_question_scale, c, false);
        
        ScaleQuestion question = (ScaleQuestion) container.filteredList.get(container.location);

        rootView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View v) {
                container.filteredList.set(container.location,
                        container.filteredList.get(container.location));
            }

            @Override
            public void onViewAttachedToWindow(View v) {

            }
        });
        View view = question.getQuestionAnswerEditView(getActivity());

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        float dp = 852f;
        float fpixels = metrics.density * dp;
        int width = (int) (fpixels + 0.5f);

        float dp2 = 159f;
        float fpixels2 = metrics.density * dp2;
        int height = (int) (fpixels2 + 0.5f);

        text = new TextView(getActivity());
        text.setTextSize(32);
        text.setWidth(width);
        text.setHeight(height);
        text.setGravity(Gravity.CENTER);

//        float dp3 = metrics.widthPixels / (metrics.densityDpi / 160f);
//        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT);
//        rootView.setLayoutParams(param);
        
//        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//                (int) dp3);
        
//        view.setLayoutParams(param2);

        rootView.addView(text);
        rootView.addView(view);

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

        text.setText(container.filteredList.get(container.location).getText());
    }

    public void onResume() {
        super.onResume();
    }

}
