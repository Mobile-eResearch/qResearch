
package de.eresearch.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import de.eresearch.app.R;

/**
 * Provides Fragment for edit/create meta data (frame on the right side, next to
 * the {@link StudyEditListFragment})
 * 
 * @author thg
 */
public class StudyEditMetaDataFragment extends Fragment {
 
    private EditText mTitle, mAuthor, mDescription, mQuestion;
    private StudyEditContainer sec;
    private TextView mStatusText, mNameTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sec = StudyEditContainer.getStudyEditContainer();
    }

    /**
     * Callback for fragment lifecycle onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_study_edit_metadata,
                container, false);

        mTitle = (EditText) rootView.findViewById(R.id.meta_name);
        mAuthor = (EditText) rootView.findViewById(R.id.meta_author); 
        mDescription = (EditText) rootView.findViewById(R.id.meta_desc);
        mQuestion = (EditText) rootView.findViewById(R.id.meta_question);

        mTitle.setText(sec.getStudy().getName());
        mAuthor.setText(sec.getStudy().getAuthor());
        mDescription.setText(sec.getStudy().getDescription());
        mQuestion.setText(sec.getStudy().getResearchQuestion());
        
        mStatusText = (TextView) rootView.findViewById(R.id.textViewStatusMetaData);
        mNameTitle = (TextView) rootView.findViewById(R.id.textViewMetaStudyTitle);
        
        mTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                sec.getStudy().setName(mTitle.getText().toString());
                sec.isChanged = true;
                status();
            }
            
        });
        
        mAuthor.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                sec.getStudy().setAuthor(mAuthor.getText().toString());
                sec.isChanged = true;
            }
            
        });
        
        mDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                sec.getStudy().setDescription(mDescription.getText().toString());
                sec.isChanged = true;
            }
            
        });
        
        mQuestion.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                sec.getStudy().setResearchQuestion(mQuestion.getText().toString());
                sec.isChanged = true;
            }
            
        });
        
        status();
         
        return rootView;
    }
    
    private void status(){
        
        if (sec.getStudy().getName() == null || sec.getStudy().getName().equals("") || sec.checkDuplicateStudyName(sec.getStudy().getName())){
            mStatusText.setVisibility(View.VISIBLE);
            mNameTitle.setTextColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
            ((StudyEditListFragment) getFragmentManager().findFragmentById(R.id.fragment_study_edit_list))
                .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Meta,false);
        }
        else {
            mStatusText.setVisibility(View.INVISIBLE);
            mNameTitle.setTextColor(getActivity().getResources().getColor(android.R.color.black));
            ((StudyEditListFragment) getFragmentManager().findFragmentById(R.id.fragment_study_edit_list))
                .setMenuStatus(StudyEditListFragment.MENU_ENTRY.Meta,true);    
        }
        
    }
}
