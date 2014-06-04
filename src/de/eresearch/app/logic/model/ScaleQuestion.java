
package de.eresearch.app.logic.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.CurrentQSort;
import de.eresearch.app.gui.QSortQuestionContainer;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author Tammo
 */
public class ScaleQuestion extends Question {

    /** ScaleQuestion's list of scales */
    private List<Scale> mScales;

    /**
     * Creates a new scale question with the given id.
     * 
     * @param id The id of this question
     * @param studyid of this question
     */
    public ScaleQuestion(int id, int studyid) {
        super(id, studyid);
        mScales = new ArrayList<Scale>();
    }

    /**
     * @param scale A scale to be added to this question
     */
    public void addScale(Scale scale) {
        mScales.add(scale);
    }

    /**
     * @param scaleId The id of the scale to be deleted from this question
     */
    public void deleteScale(int scaleId) {
        List<Scale> newScales = new ArrayList<Scale>();

        for (Scale scale : mScales) {
            if (scale.getId() != scaleId)
                newScales.add(scale);
        }

        mScales = newScales;
    }

    /**
     * @return A list of all the scales attached to this question
     */
    public List<Scale> getScales() {
        return mScales;
    }

    /**
     * overwrite scale List
     * 
     * @param scales
     */
    public void setScales(List<Scale> scales) {
        mScales = scales;
    }

    /**
     * Returns a View to display and edit a scale Question
     * 
     * @param context
     * @return
     */
    public View getQuestionAnswerEditView(final Context context) {

        Log.d("ScaleQuestion", "Number of Scales: " + mScales.size());

        // Source: StackOverFlow
        // TODO: find better solution
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        array.recycle();
        // ---------------------

        LinearLayout frame = new LinearLayout(context);
        frame.setOrientation(LinearLayout.VERTICAL);

        // create the answer object
        if (mAnswer == null) {
            mAnswer = new ScaleAnswer(-1);
            ((ScaleAnswer) mAnswer).setQuestion(this);
            if (CurrentQSort.getTimer() != null)
                ((ScaleAnswer) mAnswer).setTime(CurrentQSort.getTimer().getTime());
            // work directly on the answer list
            ((ScaleAnswer) mAnswer).setScales(mScales);
        } else {
            // if we have a existing answer object, we check if
            // it is a ScaleAnswer

            if (!(mAnswer instanceof ScaleAnswer))
                throw new IllegalStateException(
                        "Answer Object is not a ScaleAnswer");
        }

        for (final Scale s : mScales) {

            /*
             * ------ MOST OUTER LAYER - (META LAYOUT) ---------
             */

            // the pole layer is the most outer layer of this view
            LinearLayout poleLayer = new LinearLayout(context);

            // set left pole TextView
            LinearLayout.LayoutParams lPoleLayout = new LinearLayout.LayoutParams(100,
                    LayoutParams.WRAP_CONTENT);
            lPoleLayout.gravity = Gravity.CENTER_VERTICAL;
            lPoleLayout.setMargins(5, 0, 0, 20);
            TextView leftPole = new TextView(context);
            leftPole.setText(s.getPoleLeft());
            leftPole.setTypeface(null, Typeface.BOLD);
            leftPole.setLayoutParams(lPoleLayout);

            // set right pole TextView
            LinearLayout.LayoutParams rPoleLayout = new LinearLayout.LayoutParams(100,
                    LayoutParams.WRAP_CONTENT);
            rPoleLayout.gravity = Gravity.CENTER_VERTICAL;
            rPoleLayout.setMargins(0, 0, 0, 20);
            TextView rightPole = new TextView(context);
            rightPole.setText(s.getPoleRight());
            rightPole.setTypeface(null, Typeface.BOLD);
            rightPole.setLayoutParams(rPoleLayout);

            // declare the inner layer
            FrameLayout fl = new FrameLayout(context);

            /*
             * ------ SCALE LAYER - (SCALE LAYOUT)--------
             */

            // setup line in background
            View line = new View(context);
            // int size = (s.getScaleValues().size()-1)*210; //calc the size of
            // the line
            int size = (s.getScaleValues().size() - 1) * 100; // calc the size
                                                              // of the line
            FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(size, 2);
            // lineParams.setMargins(70, 28,0, 0);
            lineParams.setMargins(50, 18, 0, 0);
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            fl.addView(line);

            // setup outer LinearLayout
            final LinearLayout mOuterLinearLayout = new LinearLayout(context);
            fl.addView(mOuterLinearLayout);

            // for each scale build a inner Linear Layout
            // this displays one RadioButton with the text at the bottom
            for (String scaleValue : s.getScaleValues()) {

                LinearLayout innerLinearLayout = new LinearLayout(context);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);

                // setup the RadioButton
                RadioButton b = new RadioButton(context);
                if (s.getScaleValues().indexOf(scaleValue) == s.getSelectedValueIndex())
                    b.setChecked(true);
                b.setTag(s.getScaleValues().indexOf(scaleValue));
                b.setBackgroundColor(backgroundColor);
                b.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                radioParams.gravity = Gravity.CENTER_HORIZONTAL;
                b.setLayoutParams(radioParams);
                b.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton b = (RadioButton) v;
                        // set the selected position in the ScaleAnswer
                        s.setSelectedValueIndex(Integer.parseInt(b.getTag().toString()));
                        // "uncheck" all the other RadioButton
                        for (int i = 0; i < mOuterLinearLayout.getChildCount(); i++) {
                            LinearLayout l = (LinearLayout) mOuterLinearLayout.getChildAt(i);
                            if (l.getChildCount() > 0)
                                ((RadioButton) l.getChildAt(0)).setChecked(false);
                        }
                        // and check this one
                        b.setChecked(true);

                        QSortQuestionContainer container = QSortQuestionContainer
                                .getQSortQuestionContainer();
                        Activity currentActvity = (Activity) context;
                        View parent = currentActvity.findViewById(R.id.fragment_qsort_question_nav);
                        View child = parent.findViewById(container.location + 100000);
                        child.setBackgroundColor(Color.RED);
                    }
                });
                innerLinearLayout.addView(b);

                // setup the text under the RadioButton
                TextView t = new TextView(context);
                // LinearLayout.LayoutParams textParams = new
                // LinearLayout.LayoutParams(200,LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(100,
                        LayoutParams.WRAP_CONTENT);
                t.setLayoutParams(textParams);
                t.setGravity(Gravity.CENTER_HORIZONTAL);
                t.setSingleLine(false);
                t.setText(scaleValue);
                innerLinearLayout.addView(t);

                // add the inner layout to the outer one
                mOuterLinearLayout.addView(innerLinearLayout);

                // add a spacer after each RadioButton-TextView combination
                LinearLayout spacer = new LinearLayout(context);
                // LayoutParams spacerParams = new
                // LayoutParams(10,LayoutParams.WRAP_CONTENT);
                LayoutParams spacerParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
                spacer.setLayoutParams(spacerParams);
                mOuterLinearLayout.addView(spacer);
            }

            // stick the poleLayer together
            poleLayer.addView(leftPole);
            poleLayer.addView(fl);
            poleLayer.addView(rightPole);

            // add the whole scale to the outer frame
            Log.d("ScaleQuestion", "add Scale to most outer layout");
            frame.addView(poleLayer);
        }
        return frame;
    }

    /**
     * Returns a View to display and edit a scale Question
     * 
     * @param context
     * @return
     */
    public View getQuestionAnswerDisplayView(Context context, Answer answer) {
        mAnswer = answer;

        // Log.d("","inViewDisplay: "+((ScaleAnswer)
        // mAnswer).getScales().get(0).getSelectedValueIndex());

        // Source: StackOverFlow
        // TODO: find better solution
        TypedArray array = context.getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        array.recycle();
        // ---------------------

        LinearLayout frame = new LinearLayout(context);
        frame.setOrientation(LinearLayout.VERTICAL);

        // check the answer object
        if (mAnswer == null) {
            Log.d("TEST", "Answer was null");
            mAnswer = new ScaleAnswer(-1);
            ((ScaleAnswer) mAnswer).setQuestion(this);
            throw new IllegalStateException(
                    "There is no answer object");
        } else {
            // if we have a existing answer object, we check if
            // it is a ScaleAnswer
            if (!(mAnswer instanceof ScaleAnswer))
                throw new IllegalStateException(
                        "Answer Object is not a ScaleAnswer");
        }

        // work directly on the answer list
        // ((ScaleAnswer) mAnswer).setScales(mScales);

        for (Scale s : ((ScaleAnswer) answer).getScales()) {

            /*
             * ------ MOST OUTER LAYER - (META LAYOUT) ---------
             */

            // the pole layer is the most outer layer of this view
            LinearLayout poleLayer = new LinearLayout(context);

            // set left pole TextView
            LinearLayout.LayoutParams lPoleLayout = new LinearLayout.LayoutParams(100,
                    LayoutParams.WRAP_CONTENT);
            lPoleLayout.gravity = Gravity.CENTER_VERTICAL;
            lPoleLayout.setMargins(5, 0, 0, 20);
            TextView leftPole = new TextView(context);
            leftPole.setText(s.getPoleLeft());
            leftPole.setTypeface(null, Typeface.BOLD);
            leftPole.setLayoutParams(lPoleLayout);

            // set right pole TextView
            LinearLayout.LayoutParams rPoleLayout = new LinearLayout.LayoutParams(100,
                    LayoutParams.WRAP_CONTENT);
            rPoleLayout.gravity = Gravity.CENTER_VERTICAL;
            rPoleLayout.setMargins(0, 0, 0, 20);
            TextView rightPole = new TextView(context);
            rightPole.setText(s.getPoleRight());
            rightPole.setTypeface(null, Typeface.BOLD);
            rightPole.setLayoutParams(rPoleLayout);

            // declare the inner layer
            FrameLayout fl = new FrameLayout(context);

            // stick the poleLayer together
            poleLayer.addView(leftPole);
            poleLayer.addView(fl);
            poleLayer.addView(rightPole);

            // add the whole scale to the outer frame
            frame.addView(poleLayer);

            /*
             * ------ SCALE LAYER - (SCALE LAYOUT)--------
             */

            // setup line in background
            View line = new View(context);
            int size = (s.getScaleValues().size() - 1) * 108; // calc the size
                                                              // of the line
            FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(size, 2);
            lineParams.setMargins(50, 18, 0, 0);
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            fl.addView(line);

            // setup outer LinearLayout
            final LinearLayout mOuterLinearLayout = new LinearLayout(context);
            fl.addView(mOuterLinearLayout);

            // for each scale build a inner Linear Layout
            // this displays one RadioButton with the text at the bottom
            for (String scaleValue : s.getScaleValues()) {

                LinearLayout innerLinearLayout = new LinearLayout(context);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);

                // setup the RadioButton
                RadioButton b = new RadioButton(context);
                // Log.d("",""+s.getScaleValues().indexOf(scaleValue)+";"+
                // s.getSelectedValueIndex());
                if (s.getScaleValues().indexOf(scaleValue) == s.getSelectedValueIndex())
                    b.setChecked(true);
                b.setTag(s.getScaleValues().indexOf(scaleValue));
                b.setBackgroundColor(backgroundColor);
                b.setGravity(Gravity.CENTER_HORIZONTAL);
                b.setClickable(false);
                b.setFocusable(false);
                LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                radioParams.gravity = Gravity.CENTER_HORIZONTAL;
                b.setLayoutParams(radioParams);
                innerLinearLayout.addView(b);

                // setup the text under the RadioButton
                TextView t = new TextView(context);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(100,
                        LayoutParams.WRAP_CONTENT);
                t.setLayoutParams(textParams);
                t.setGravity(Gravity.CENTER_HORIZONTAL);
                t.setSingleLine(false);
                t.setText(scaleValue);
                innerLinearLayout.addView(t);

                // add the inner layout to the outer one
                mOuterLinearLayout.addView(innerLinearLayout);

                // add a spacer after each RadioButton-TextView combination
                LinearLayout spacer = new LinearLayout(context);
                LayoutParams spacerParams = new LayoutParams(10, LayoutParams.WRAP_CONTENT);
                spacer.setLayoutParams(spacerParams);
                mOuterLinearLayout.addView(spacer);
            }
        }
        return frame;
    }

    @Override
    public boolean isConsistent() {
        if (this.getText().length() < 1)
        {
            return false;
        }

        if (mScales.size() < 1)
        {
            return false;
        }

        for (int i = 0; i < mScales.size(); i++)
        {
            if ((mScales.get(i).getPoleLeft() == null || mScales.get(i).getPoleLeft().length() == 0)
                    || (mScales.get(i).getPoleRight() == null || mScales.get(i).getPoleRight()
                            .length() == 0)
                    || !scalesReady(mScales.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    private boolean scalesReady(Scale s) {
        for (int i = 0; i < s.getScaleValues().size(); i++)
        {
            if (s.getScaleValues().get(i) == null
                    || s.getScaleValues().get(i).length() == 0)
            {
                return false;
            }

        }

        return true;
    }
}
