
package de.eresearch.app.gui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Question;

import java.util.List;

/**
 * Custom ArrayAdapter for AppSettingDialog
 * 
 * @author Titu
 */
public class QuestionAnswerListAdapter extends ArrayAdapter<Question> {

    private Context context;
    private List<Question> questions;

    /**
     * Constructor of the QuestionAnswerListAdapter
     * 
     * @param context
     * @param textViewResourceId
     * @param objects
     */

    public QuestionAnswerListAdapter(Context context, int textViewResourceId,
            List<Question> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.questions = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Set text and image

        View rowView = inflater.inflate(R.layout.list_question_fragment, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.questions);
        Question q = questions.get(position);
        
        textView.setText((position +1) + ". " + q.getText());

        return rowView;

    }
}
