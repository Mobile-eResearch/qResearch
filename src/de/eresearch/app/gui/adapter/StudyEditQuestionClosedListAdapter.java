
package de.eresearch.app.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditQuestionClosedFragment;
import de.eresearch.app.logic.model.Phase;

public class StudyEditQuestionClosedListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> mAnswers;
    private StudyEditQuestionClosedFragment fragment;
    private Phase p;
    private String answer= "";

    public StudyEditQuestionClosedListAdapter(Context context, int resource, ArrayList<String> a,
            StudyEditQuestionClosedFragment fragment) {
        super(context, resource, a);
        this.context = context;
        mAnswers = a;
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
            rowView = inflater.inflate(R.layout.fragment_study_edit_question_closed_row, parent,
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
        answer = mAnswers.get(position);

        if (mAnswers.size() > 0)
        {
            if (answer.length() > 0)
            {
                holder.alert.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.alert.setVisibility(View.VISIBLE);
            }

            holder.answerNum.setText((position + 1) + ".");
            if (answer.length() > 60)
            {
                String s = answer.substring(0, 59);
                holder.answerDesc.setText(s + "...");
            }
            else
            {
                holder.answerDesc.setText(answer);
            }
        }

        holder.delete.setOnClickListener(onDeleteClickListener);
        holder.up.setOnClickListener(onUpClickListener);
        holder.down.setOnClickListener(onDownClickListener);
        holder.delete.setTag(answer);
        holder.up.setTag(answer);
        holder.down.setTag(answer);

        if (position == 0)
            holder.up.setVisibility(View.INVISIBLE);
        else
            holder.up.setVisibility(View.VISIBLE);

        if (position == mAnswers.size() - 1)
            holder.down.setVisibility(View.INVISIBLE);
        else
            holder.down.setVisibility(View.VISIBLE);

        return rowView;
    }

    OnClickListener onDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String s = (String) v.getTag();
            fragment.onListDeleteClick(s);
        }
    };

    OnClickListener onUpClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String s = (String) v.getTag();
            fragment.onListUpClick(s);
        }
    };

    OnClickListener onDownClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String s = (String) v.getTag();
            fragment.onListDownClick(s);
        }
    };

}
