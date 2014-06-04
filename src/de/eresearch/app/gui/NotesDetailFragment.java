package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView.BufferType;

import de.eresearch.app.R;
import de.eresearch.app.logic.model.Note;
import de.eresearch.app.logic.tasks.common.qsort.GetQSortTask;
import de.eresearch.app.logic.tasks.media.GetNotesTask;

/**
 * Provides Fragment for notes view (frame on the right side, next to 
 * the {@link NotesListFragment})
 *
 */
public class NotesDetailFragment extends Fragment implements GetNotesTask.Callbacks {
    
    public static final String NOTE_ID = "de.eresearch.app.gui.NotesDetailFragment.note_id";
    public static final String QSORT_NAME = "de.eresearch.app.gui.NotesDetailFragment.qsort_name";
    public static final String QSORT_ID = "de.eresearch.app.gui.qsort_id"; 
    public static final String NOTE_TEXT = "de.eresearch.app.gui.NotesDetailFragment.note_text"; 
    public static final String NOTE_TITLE = "de.eresearch.app.gui.NotesDetailFragment.note_title"; 
 
    private EditText mTitle;
    private EditText mText;
    
    private String newTitle;
    private String newText;
    
    
    @Override
    public void onStart(){
        super.onStart();
        
        
    }
    
    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        String title = getArguments().getString(NotesDetailFragment.NOTE_TITLE);
        String text = getArguments().getString(NotesDetailFragment.NOTE_TEXT);
        
        View rootView = inflater.inflate(R.layout.fragment_notes_detail,
                container, false);
        
        mText = (EditText) rootView.findViewById(R.id.note_text);
            mText.addTextChangedListener(new TextWatcher(){

                @Override
                public void afterTextChanged(Editable arg0) {
                    newText = mText.getText().toString();
                    
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    newText = mText.getText().toString();
                    
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    newText = mText.getText().toString();

                    NotesActivity n = (NotesActivity) getActivity();
                    n.isEdited(true);
                    
                }
                
                
        });

        mTitle = (EditText) rootView.findViewById(R.id.note_title);

        mTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                newTitle = mTitle.getText().toString();

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                newTitle = mTitle.getText().toString();

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                newTitle = mTitle.getText().toString();
                NotesActivity n = (NotesActivity) getActivity();
                n.isEdited(true);

            }

        });
        mText.setText(text, BufferType.EDITABLE);
        mTitle.setText(title, BufferType.EDITABLE);
        
        return rootView;
    }

    @Override
    public void onGetNotesTaskUpdate(Note[] noteList) {
        // TODO Auto-generated method stub
        
    }
    
    
    public String getNoteTitle(){
        return newTitle;
    }
    
    public String getNoteText(){
        return newText;
    }

   

}
