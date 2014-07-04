
package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenAnswer;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.ScaleQuestion;

/**
* Class for a dialog, to view the questions in the log
*
* @author domme
*
*/
public class ViewQuestionDialog extends Dialog {

    private Context mContext;
    private Answer mAnswer;

    public ViewQuestionDialog(Context context, Answer answer) {
        super(context);
        mContext = context;
        mAnswer = answer;
    }

    public AlertDialog makeDialog() {

        AlertDialog.Builder viewQuestionDialog = new AlertDialog.Builder(mContext);

        if (mAnswer.getQuestion() instanceof OpenQuestion) {
            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.fragment_qsort_question_open, null);

            TextView questionTextView = (TextView)
                    v.findViewById(R.id.fragment_qsort_question_open_field);
            
            questionTextView.setText(((OpenAnswer) mAnswer).getQuestion().getText());

            EditText answerEditText = (EditText)
                    v.findViewById(R.id.fragment_qsort_question_open_answerfield);
            answerEditText.setEnabled(false);
            if(!((OpenAnswer) mAnswer).getAnswer().toString().isEmpty())
            answerEditText.setText(((OpenAnswer) mAnswer).getAnswer());
            else
                answerEditText.setHint(mContext.getString(R.string.loggable_not_answer_openquestion));

            viewQuestionDialog.setView(v);

        }
        else if (mAnswer.getQuestion() instanceof ClosedQuestion) {
            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);
            TextView questionText = new TextView(mContext);
            questionText.setText(((ClosedQuestion) mAnswer.getQuestion()).getText());
            ll.addView(questionText);
            ll.addView(((ClosedQuestion) mAnswer.getQuestion())
                    .getQuestionAnswerDisplayView(mContext, mAnswer));
            viewQuestionDialog.setView(ll);
        }
        else if (mAnswer.getQuestion() instanceof ScaleQuestion) {
            LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout ll = new LinearLayout(mContext);
            ll.setLayoutParams(pl);

            ScrollView scrollView = new ScrollView(mContext);
            LayoutParams ps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            scrollView.setLayoutParams(ps);

            View questionView = ((ScaleQuestion) mAnswer.getQuestion())
                    .getQuestionAnswerDisplayView(mContext, mAnswer);

            scrollView.addView(questionView);
            ll.addView(scrollView);
            viewQuestionDialog.setView(ll);
        }
        return viewQuestionDialog.create();
    }
}
