
package de.eresearch.app.gui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import de.eresearch.app.R;
import de.eresearch.app.gui.LogParentActivity;
import de.eresearch.app.gui.QSortView;
import de.eresearch.app.gui.dialogs.LogNoteDialog;
import de.eresearch.app.gui.dialogs.ViewQuestionDialog;
import de.eresearch.app.logic.model.Answer;
import de.eresearch.app.logic.model.LogEntry;
import de.eresearch.app.logic.model.Loggable;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.model.Picture;

import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * Custom ArrayAdapter for LogParentActivity
 * @author domme
 *
 */
public class LogListArrayAdapter extends ArrayAdapter<Loggable> {

    private final Context mContext;
    private final List<Loggable> mEntries;
    private ListView mListView;
    private long mEndTime;

    public LogListArrayAdapter(Context context,long endTime, int resource, List<Loggable> entries) {
        super(context, resource, entries);
        this.mContext = context;
        this.mEntries = entries;
        this.mEndTime = endTime;

        mListView = (ListView) ((Activity) context).findViewById(R.id.log_list);
    }

    static class ViewHolder {
        public Button timeStamp;
        public TextView logEntry;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.activity_log_list_row, parent, false);

            viewHolder.timeStamp = (Button) rowView.findViewById(R.id.log_timestamp);

            viewHolder.logEntry = (TextView) rowView.findViewById(R.id.log_entry);
            rowView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.timeStamp.setText(mEntries.get(position).getTimeString());
        if (LogParentActivity.mHighlightedItem == position)
            holder.timeStamp.setBackgroundColor(Color.rgb(0, 102, 150));
        else
            holder.timeStamp.setBackgroundColor(Color.TRANSPARENT);
        
        System.out.println(mEntries.get(position).toString(mContext));

        holder.logEntry.setText(mEntries.get(position).toString(mContext));

        holder.logEntry = setEntryIcon(holder.logEntry, position);

        holder.logEntry.setOnClickListener(mOnEntryClickListener);
        holder.timeStamp.setOnClickListener(mOnTimestampClickListener);

        return rowView;
    }

    /**
     * Gives the log entry the icon
     * 
     * @param logEntrie Logentry Item
     * @param position Position in the List
     * @return
     */
    private TextView setEntryIcon(TextView logEntry, int position) {

        if (mEntries.get(position) instanceof Note)
            logEntry.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().
                    getDrawable(R.drawable.ic_log_item_note), null, null, null);
        else if (mEntries.get(position) instanceof LogEntry)
            logEntry.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().
                    getDrawable(R.drawable.ic_log_item_picture), null, null, null);
        else if (mEntries.get(position) instanceof Answer)
            logEntry.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().
                    getDrawable(R.drawable.ic_log_item_help), null, null, null);
        return logEntry;
    }

    private OnClickListener mOnTimestampClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = mListView.getPositionForView((View) v.getParent());
            if( ((LogParentActivity) mContext).getPlayPause()){
                ((LogParentActivity) mContext).setPlayTime(mEntries.get(position).getTime());
                ((LogParentActivity) mContext).playAudio(true);
            }
            else
                ((LogParentActivity) mContext).setPlayTime(mEntries.get(position).getTime());
            ((LogParentActivity) mContext).mHighlightedItem = -1;
            ((LogParentActivity) mContext).calculateHighlightedItem();
        }
    };

    private OnClickListener mOnEntryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = mListView.getPositionForView((View) v.getParent());
            if (mEntries.get(position) instanceof Note) {
                Note tmpNote = (Note) mEntries.get(position);
                LogNoteDialog logNoteDialog =
                        new LogNoteDialog(mContext, tmpNote.getPhase(), tmpNote.getQSortId(),
                                false, tmpNote,
                                (int)TimeUnit.MILLISECONDS.toMinutes(mEndTime),
                                (int)TimeUnit.MILLISECONDS.toMinutes(mEntries.get(position).getTime()),
                                (int)(TimeUnit.MILLISECONDS.toSeconds(mEndTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mEndTime))),
                                (int)(TimeUnit.MILLISECONDS.toSeconds(mEntries.get(position).getTime()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mEntries.get(position).getTime()))));
            logNoteDialog.makeLogNoteDialog().show();
            }
            else if (mEntries.get(position) instanceof LogEntry){
                QSortView qSortView = new QSortView(mContext);
                qSortView.showBigImage(((Picture)((LogEntry)mEntries.get(position)).getItem()));
            }
            else if(mEntries.get(position) instanceof Answer){
                ViewQuestionDialog viewQuestionDialog = new ViewQuestionDialog(mContext, (Answer)mEntries.get(position));
                viewQuestionDialog.makeDialog().show();
            }

        }
    };
}
