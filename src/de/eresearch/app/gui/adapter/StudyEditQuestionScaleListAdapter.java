
package de.eresearch.app.gui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditListFragment;
import de.eresearch.app.gui.StudyEditQuestionClosedFragment;
import de.eresearch.app.gui.StudyEditQuestionScaleFragment;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Scale;

public class StudyEditQuestionScaleListAdapter extends ArrayAdapter<Scale> {

    private final Context context;
    // private List<Question> mQuestions;
    private ArrayList<Scale> mScales;
    private StudyEditQuestionScaleFragment fragment;
    private String answer = "";
    private Scale mScale;

    public StudyEditQuestionScaleListAdapter(Context context, int resource, ArrayList<Scale> list,
            StudyEditQuestionScaleFragment fragment) {
        super(context, resource, list);
        this.context = context;
        mScales = list;
        this.fragment = fragment;
    }

    static class ViewHolder {
        public ImageView delete;
        public ImageView up;
        public ImageView down;
        public ImageView alert;
        public TextView answerNum;
        public TextView answerDesc;
        // public TextView questionType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            rowView = inflater.inflate(R.layout.fragment_study_edit_question_scale_row, parent,
                    false);
            viewHolder.answerNum = (TextView) rowView.findViewById(R.id.answer_label_num);
            viewHolder.answerDesc = (TextView) rowView.findViewById(R.id.answer_label);
            viewHolder.delete = (ImageView) rowView.findViewById(R.id.answer_remove);
            viewHolder.alert = (ImageView) rowView.findViewById(R.id.answer_attention_icon);
            viewHolder.up = (ImageView) rowView.findViewById(R.id.answers_button_up);
            viewHolder.down = (ImageView) rowView.findViewById(R.id.answers_button_down);
            rowView.setTag(viewHolder);
        } else {
            // get holder back...much faster than inflate
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        mScale = mScales.get(position);

        if (((mScale.getPoleLeft() == null) || mScale.getPoleLeft().length() == 0)
                || ((mScale.getPoleRight() == null) || mScale.getPoleRight().length() == 0)
                || !fieldsReady())
        {
            holder.alert.setVisibility(View.VISIBLE);          
        }
        else
        {
            holder.alert.setVisibility(View.INVISIBLE);   
        }

        holder.answerNum.setText((position + 1) + ".");
        String preparedAnsw = "; ";
        for (int i = 0; i < mScale.getScaleValues().size(); i++)
        {
            preparedAnsw = preparedAnsw + mScale.getScaleValues().get(i)+"; ";
        }
        holder.answerDesc.setText(mScale.getPoleLeft() + preparedAnsw + mScale.getPoleRight());

        holder.delete.setOnClickListener(onDeleteClickListener);
        holder.up.setOnClickListener(onUpClickListener);
        holder.down.setOnClickListener(onDownClickListener);
        holder.delete.setTag(mScale);
        holder.up.setTag(mScale);
        holder.down.setTag(mScale);

        if (position == 0)
            holder.up.setVisibility(View.INVISIBLE);
        else
            holder.up.setVisibility(View.VISIBLE);

        if (position == mScales.size() - 1)
            holder.down.setVisibility(View.INVISIBLE);
        else
            holder.down.setVisibility(View.VISIBLE);

        return rowView;
    }

    OnClickListener onDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Scale sc = (Scale) v.getTag();
            // String s = (String) v.getTag();
            fragment.onListDeleteClick(sc);
        }
    };

    OnClickListener onUpClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Scale s = (Scale) v.getTag();
            fragment.onListUpClick(s);
        }
    };

    OnClickListener onDownClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Scale s = (Scale) v.getTag();
            fragment.onListDownClick(s);
        }
    };

    private boolean fieldsReady() {
        // boolean check = false;
        for (int i = 0; i < mScale.getScaleValues().size(); i++)
        {
            if (mScale.getScaleValues().get(i) == null
                    || mScale.getScaleValues().get(i).length() == 0)
            {
                return false;
            }

        }
        return true;
    }

}
