
package de.eresearch.app.logic.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.gui.QSortQuestionContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tammo
 */
public class ClosedQuestion extends Question {

    /** Defines whether the ClosedQuestion has an open field */
    private boolean mHasOpenField = false;

    /** Defines whether the ClosedQuestion is multiple choice */
    private boolean mIsMutipleChoice = false;

    /** ClosedQuestion's List of possibleAnswers */
    private List<String> mPossibleAnswers;

    /**
     * Creates a new closed question with the given id.
     * 
     * @param id The id of this closed question
     * @param studyid of this question
     */
    public ClosedQuestion(int id, int studyid) {
        super(id, studyid);
        mPossibleAnswers = new ArrayList<String>();
    }

    /**
     * @param openField <code>true</code> when this question should provide a
     *            field for user-defined answers, <code>false</code> else
     */
    public void setOpenField(boolean openField) {
        mHasOpenField = openField;
    }

    /**
     * @return <code>true</code> when this question provides a field for
     *         user-defined answers, <code>false</code> else
     */
    public boolean hasOpenField() {
        return mHasOpenField;
    }

    /**
     * @param multipleChoice <code>true</code> when this question should allow
     *            multiple answers from the list to be selected,
     *            <code>false</code> else
     */
    public void setMultipleChoice(boolean multipleChoice) {
        mIsMutipleChoice = multipleChoice;
    }

    /**
     * @return <code>true</code> when this question allows multiple answers from
     *         the list to be selected, <code>false</code> else
     */
    public boolean isMultipleChoice() {
        return mIsMutipleChoice;
    }

    /**
     * Adds a new possible answer to this question. The answer will be attached
     * to the back of the existing list, so when there already is an answer list
     * <code>[answer1, answer2]</code>, <code>addPossibleAnswer(answer3)</code>
     * will result in a list like <code>[answer1, answer2, answer3]</code>. If
     * there is already an answer in the list that equals the one that is
     * passed, nothing will happen.
     * 
     * @param answer The answer to be added as a string
     */
    public void addPossibleAnswer(String answer) {
        if (!mPossibleAnswers.contains(answer))
            mPossibleAnswers.add(answer);
    }

    /**
     * Deletes a certain answer from the answer list.
     * 
     * @param answer The answer to be deleted
     */
    public void deletePossibleAnswer(String answer) {
        mPossibleAnswers.remove(answer);
    }

    /**
     * Returns a list of all the possible answers of this multiple choice
     * question. The list provides the order in which the elements will be
     * displayed on the screen. Initially that's the order in which the answers
     * have been added using {@link ClosedQuestion#addPossibleAnswer(String)},
     * but the order can be manipulated using {@link ClosedQuestion#up(String)}
     * and {@link ClosedQuestion#down(String)} on certain answers. The list does
     * <B>not</B> contain any duplicates.
     * 
     * @return The list with the possible answers as strings
     */
    public List<String> getPossibleAnswers() {
        return mPossibleAnswers;
    }

    /**
     * Changes the order of the answers in the answer list. The given answer
     * will swap its position with the answer <B>in front of it</B>. E.g. when
     * there is an answer list <code>[answer1, answer2, answer3]</code> the call
     * of <code>up(answer2)</code> will result in a list
     * <code>[answer2, answer1, answer3]</code>. If the answer already is on the
     * first position of the list, nothing will happen.
     * 
     * @param answer The answer to be voted up
     */
    public void up(String answer) {
        int i = mPossibleAnswers.indexOf(answer);

        if (i > 0) {
            mPossibleAnswers.remove(answer);
            mPossibleAnswers.add(i - 1, answer);
        }
    }

    /**
     * Changes the order of the answers in the answer list. The given answer
     * will swap its position with the answer <B>behind of it</B>. E.g. when
     * there is an answer list <code>[answer1, answer2, answer3]</code> the call
     * of <code>down(answer2)</code> will result in a list
     * <code>[answer1, answer3, answer2]</code>. If the answer already is on the
     * last position of the list, nothing will happen.
     * 
     * @param answer The answer to be voted down
     */
    public void down(String answer) {
        int i = mPossibleAnswers.indexOf(answer);

        if (i < mPossibleAnswers.size() - 1) {
            mPossibleAnswers.remove(answer);
            mPossibleAnswers.add(i + 1, answer);
        }
    }

    /**
     * Return a View witch possible answers to the Question. The given answers
     * are saved to the question object (to attribute mAnswer) automatically.
     * 
     * @param context
     * @return View with possible answers
     * @author thg
     */
    public View getQuestionAnswerEditView(Context context) {

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // create the answer object
        if (mAnswer == null) {
            mAnswer = new ClosedAnswer(-1);
            ((ClosedAnswer) mAnswer).setQuestion(this);
            if(CurrentQSort.getTimer()!= null)
                ((ClosedAnswer) mAnswer).setTime(CurrentQSort.getTimer().getTime());
        } else {
            // if we have a existing answer object, we check if
            // it is a ClosedAnswer
            if (!(mAnswer instanceof ClosedAnswer))
                throw new IllegalStateException(
                        "Answer Object is not a ClosedAnswer");
        }

        // is the question multiple choice or not? -> call the helper
        if (mIsMutipleChoice) {
            return getQuestionAnswerEditViewMutipleChoiceHelper(ll, context);
        } else
        {
            return getQuestionAnswerEditViewRadioButtonHelper(ll, context);
        }
    }

    /**
     * Helper for getQuestionAnswerView(Context context) method
     * 
     * @param ll the parent {@link LinearLayout}
     * @param context the Context
     * @return the filled out {@link LinearLayout}
     * @author thg
     */
    private View getQuestionAnswerEditViewMutipleChoiceHelper(LinearLayout ll, final Context context) {
        for (String s : mPossibleAnswers) {
            final CheckBox b = new CheckBox(context);

            // b.setChecked(false);

            for (String a : ((ClosedAnswer) mAnswer).getAnswers())
                if (a.equals(s)) {
                    b.setActivated(true);
                    b.setChecked(true);
                }

            b.setText(s);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if (cb.isActivated()) {
                        cb.setActivated(false);
                        ((ClosedAnswer) mAnswer).removeAnswer(cb.getText().toString());
                        onClickAnswer(context);
                    } else {
                        cb.setActivated(true);
                        ((ClosedAnswer) mAnswer).addAnswer(cb.getText().toString());
                        unClickAnswer(context);
                    }
                }
            });
            ll.addView(b);

        }

        if (mHasOpenField) {
            RelativeLayout rl = new RelativeLayout(context);
            final EditText t = new EditText(context);
            CheckBox cb = new CheckBox(context);

            t.setEnabled(false);

            // Fill out the OpenField (if an open Answer exists)
            List<String> tmp = new ArrayList<String>();
            tmp.addAll(((ClosedAnswer) mAnswer).getAnswers());
            tmp.removeAll(this.getPossibleAnswers());
            if (tmp.size() > 0) {
                t.setText(tmp.get(0));
                t.setEnabled(true);
                cb.setChecked(true);
                cb.setActivated(true);
            }

            RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tParams.addRule(RelativeLayout.RIGHT_OF, 123);

            t.setLayoutParams(tParams);

            t.setInputType(InputType.TYPE_NULL);

            t.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    ((ClosedAnswer) mAnswer).removeAnswer(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((ClosedAnswer) mAnswer).addAnswer(s.toString());
                }
            });

            cb.setId(123);
            cb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if (cb.isActivated()) {
                        cb.setActivated(false);
                        t.setEnabled(false);
                        t.setInputType(InputType.TYPE_NULL);
                        ((ClosedAnswer) mAnswer).removeAnswer(t.getText().toString());
                    } else {
                        cb.setActivated(true);
                        t.setEnabled(true);
                        t.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                        ((ClosedAnswer) mAnswer).addAnswer(t.getText().toString());
                    }

                    if (cb.isChecked()) {
                        unClickAnswer(context);
                    } else {
                        onClickAnswer(context);
                    }
                }
            });
            rl.addView(cb);
            rl.addView(t);
            ll.addView(rl);
        }
        ll.setFocusableInTouchMode(true);

        return ll;
    }

    /**
     * Helper for getQuestionAnswerView(Context context) method
     * 
     * @param ll the parent {@link LinearLayout}
     * @param context the Context
     * @return the filled out {@link LinearLayout}
     * @author thg
     */
    private View getQuestionAnswerEditViewRadioButtonHelper(LinearLayout ll, final Context context) {

        final RadioGroup rg = new RadioGroup(context);
        final EditText t = new EditText(context);
        final RadioButton openRB = new RadioButton(context);
        openRB.setId(123);

        // display all possible answers as a RadioGroup
        for (String s : mPossibleAnswers) {
            RadioButton b = new RadioButton(context);

            b.setText(s);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton b = (RadioButton) v;
                    // clear the RadioGroup
                    rg.clearCheck();
                    // check the clicket RadioButton
                    b.setChecked(true);
                    // add the answer to the list
                    ((ClosedAnswer) mAnswer).clear();
                    ((ClosedAnswer) mAnswer).addAnswer(b.getText().toString());
                    // disable the EditText field
                    t.setEnabled(false);
                    t.setInputType(InputType.TYPE_NULL);
                    // disable the RadioButton in the RelativeLayout
                    openRB.setChecked(false);

                    if (b.isChecked()) {
                        unClickAnswer(context);
                    } else {
                        onClickAnswer(context);
                    }
                }
            });
            rg.addView(b);

            // Check the saved Answer (if there is one)
            for (String a : ((ClosedAnswer) mAnswer).getAnswers())
                if (a.equals(s)) {
                    b.setChecked(true);
                }
        }
        ll.addView(rg);

        /*
         * if there should be a open field create RelativeLayout and add this to
         * the parent LinearLayout. Also set needed onClickListener
         */
        if (mHasOpenField) {
            RelativeLayout rl = new RelativeLayout(context);

            // the EditText field must be right of the RadioButton
            RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tParams.addRule(RelativeLayout.RIGHT_OF, 123);
            t.setLayoutParams(tParams);
            t.setEnabled(false);

            // Fill out the OpenField (if an open Answer exists)
            List<String> tmp = new ArrayList<String>();
            tmp.addAll(((ClosedAnswer) mAnswer).getAnswers());
            tmp.removeAll(this.getPossibleAnswers());
            if (tmp.size() > 0) {
                t.setText(tmp.get(0));
                t.setEnabled(true);
                openRB.setChecked(true);
            }

            t.setInputType(InputType.TYPE_NULL);
            // if we change the EditText text the answer will set in mAnswer
            t.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    ((ClosedAnswer) mAnswer).clear();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((ClosedAnswer) mAnswer).addAnswer(s.toString());
                }
            });
            // if we click on the RadioButton ...
            openRB.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ... clear the answers and add only the on in EditText
                    ((ClosedAnswer) mAnswer).clear();
                    ((ClosedAnswer) mAnswer).addAnswer(t.getText().toString());
                    // ... the EditText should be enabled
                    t.setEnabled(true);
                    t.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                    // ... all RadioButton of the RadioGroup must be disabled
                    rg.clearCheck();

                    if (openRB.isChecked()) {
                        unClickAnswer(context);
                    } else {
                        onClickAnswer(context);
                    }
                }
            });
            // add RadioButton and EditText to the RelativeLayout
            rl.addView(openRB);
            rl.addView(t);
            // add the RelativeLayout to the parent LinarLayout
            ll.addView(rl);
        }
        // disable keyboard on Activity.onCreate()
        ll.setFocusableInTouchMode(true);

        // return the layout
        return ll;
    }

    /**
     * @param context
     * @return
     * @author thg
     * @param answer
     */
    public View getQuestionAnswerDisplayView(Context context, Answer answer) {
        mAnswer = answer;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        // check the answer object
        if (mAnswer == null) {
            throw new IllegalStateException(
                    "Found no answer object");
        } else if (!(mAnswer instanceof ClosedAnswer))
            throw new IllegalStateException(
                    "Answer Object is not a ClosedAnswer");

        List<String> answers = new ArrayList<String>();
        answers.addAll(((ClosedAnswer) mAnswer).getAnswers());

        for (String s : mPossibleAnswers) {

            if (mIsMutipleChoice) {
                CheckBox b = new CheckBox(context);
                b.setText(s);
                b.setFocusable(false);
                b.setClickable(false);
                for (String a : ((ClosedAnswer) mAnswer).getAnswers())
                    if (s.equals(a)) {
                        b.setChecked(true);
                        answers.remove(a);
                    }
                ll.addView(b);
            }
            else {
                RadioButton b = new RadioButton(context);
                b.setText(s);
                b.setFocusable(false);
                b.setClickable(false);
                for (String a : ((ClosedAnswer) mAnswer).getAnswers())
                    if (s.equals(a)) {
                        b.setChecked(true);
                        answers.remove(a);
                    }
                ll.addView(b);
            }
        }

        if (mHasOpenField && answers.size() == 1) {
            RelativeLayout rl = new RelativeLayout(context);
            EditText t = new EditText(context);
            RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tParams.addRule(RelativeLayout.RIGHT_OF, 123);
            t.setLayoutParams(tParams);

            if (mIsMutipleChoice) {
                CheckBox b = new CheckBox(context);
                b.setId(123);
                t.setText(answers.get(0));
                t.setFocusable(false);
                b.setFocusable(false);
                b.setChecked(true);
                rl.addView(b);
            }
            else {
                RadioButton b = new RadioButton(context);
                b.setId(123);
                t.setText(answers.get(0));
                t.setFocusable(false);
                b.setFocusable(false);
                b.setChecked(true);
                rl.addView(b);
            }
            rl.addView(t);
            ll.addView(rl);
        } else if ((mHasOpenField && answers.size() > 1) || (!mHasOpenField && answers.size() == 1)) {
            throw new IllegalStateException(
                    "Answers do not fit the question");
        }
        return ll;
    }

    private void unClickAnswer(final Context context) {
        Activity currentActvity = (Activity) context;
        View parent = currentActvity.findViewById(R.id.fragment_qsort_question_nav);
        View child = parent
                .findViewById(QSortQuestionContainer.getQSortQuestionContainer().location + 100000);
        child.setBackgroundColor(Color.RED);
    }

    private void onClickAnswer(final Context context) {
        Activity currentActvity = (Activity) context;
        View parent = currentActvity.findViewById(R.id.fragment_qsort_question_nav);
        View child = parent
                .findViewById(QSortQuestionContainer.getQSortQuestionContainer().location + 100000);
        child.setBackgroundResource(android.R.drawable.btn_default);
    }

    @Override
    public boolean isConsistent() {
        if (this.getText().length() < 1)
        {
            return false;
        }
        
        if (mPossibleAnswers.size() < 1)
        {
            return false;
        }

        for (int i = 0; i < mPossibleAnswers.size(); i++)
        {
            if (mPossibleAnswers.get(i).length() < 1)
            {
                return false;
            }
        }      
        
        return true;
    }

}
