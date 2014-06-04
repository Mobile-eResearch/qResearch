
package de.eresearch.app.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditQuestionFragment;
import de.eresearch.app.logic.model.ClosedQuestion;
import de.eresearch.app.logic.model.OpenQuestion;
import de.eresearch.app.logic.model.Phase;
import de.eresearch.app.logic.model.Question;
import de.eresearch.app.logic.model.Scale;
import de.eresearch.app.logic.model.ScaleQuestion;

public class StudyEditQuestionListAdapter extends ArrayAdapter<Question> {

    private final Context context;
    private List<Question> mQuestions;
    private StudyEditQuestionFragment fragment;
    private Phase p;

    public StudyEditQuestionListAdapter(Context context, int resource, List<Question> q,
            StudyEditQuestionFragment fragment, Phase p) {
        super(context, resource, q);
        this.context = context;
        mQuestions = q;
        this.fragment = fragment;
        this.p = p;
    }

    static class ViewHolder {
        public ImageView delete;
        public ImageView up;
        public ImageView down;
        public ImageView alert;
        public TextView questionNum;
        public TextView questionDesc;
        public TextView questionType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            rowView = inflater.inflate(R.layout.fragment_study_edit_question_row, parent, false);
            viewHolder.questionNum = (TextView) rowView.findViewById(R.id.label_num);
            viewHolder.questionDesc = (TextView) rowView.findViewById(R.id.question_label);
            viewHolder.questionType = (TextView) rowView.findViewById(R.id.question_type);
            viewHolder.delete = (ImageView) rowView.findViewById(R.id.question_remove);
            viewHolder.alert = (ImageView) rowView.findViewById(R.id.attention_icon);
            viewHolder.up = (ImageView) rowView.findViewById(R.id.button_up);
            viewHolder.down = (ImageView) rowView.findViewById(R.id.button_down);
            rowView.setTag(viewHolder);
        } else {
            // get holder back...much faster than inflate
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Question q = mQuestions.get(position);

        if (!q.isConsistent())
        {
            holder.alert.setVisibility(View.VISIBLE);
        }

        else
        {
            holder.alert.setVisibility(View.INVISIBLE);
        }

        holder.questionNum.setText((position + 1) + ".");
        if (q.getText().length() > 60)
        {
            String s = q.getText().substring(0, 59);
            holder.questionDesc.setText(s + "...");
        }
        else
        {

            holder.questionDesc.setText(q.getText());
        }

        if (q instanceof OpenQuestion)
        {
            holder.questionType.setText(R.string.study_edit_question_type_open);
        }
        else if (q instanceof ClosedQuestion)
        {
            holder.questionType.setText(R.string.study_edit_question_type_closed);
        }
        else
        {
            holder.questionType.setText(R.string.study_edit_question_type_scale);
        }

        holder.delete.setOnClickListener(onDeleteClickListener);
        holder.up.setOnClickListener(onUpClickListener);
        holder.down.setOnClickListener(onDownClickListener);
        holder.delete.setTag(q);
        holder.up.setTag(q);
        holder.down.setTag(q);

        if (position == 0)
            holder.up.setVisibility(View.INVISIBLE);
        else
            holder.up.setVisibility(View.VISIBLE);

        if (position == mQuestions.size() - 1)
            holder.down.setVisibility(View.INVISIBLE);
        else
            holder.down.setVisibility(View.VISIBLE);

        return rowView;
    }   

    OnClickListener onDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Question q = (Question) v.getTag();
            fragment.onListDeleteClick(q, p);
        }
    };

    OnClickListener onUpClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Question q = (Question) v.getTag();
            fragment.onListUpClick(q, p);
        }
    };

    OnClickListener onDownClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Question q = (Question) v.getTag();
            fragment.onListDownClick(q, p);
        }
    };

}
