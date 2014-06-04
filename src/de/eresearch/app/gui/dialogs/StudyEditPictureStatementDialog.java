package de.eresearch.app.gui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditItemsFragment;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Picture;

/**
 * 
 * @author thg
 *
 */
public class StudyEditPictureStatementDialog extends DialogFragment{

    private Item mItem;
    private EditText statement;
    
    public void setItem(Item i){
        mItem = i;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
     // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        View v = inflater.inflate(R.layout.dialog_study_edit_item_statement, null);
        
        ImageView itemView = (ImageView) v.findViewById(R.id.statementDialogImageContainer);
        File imageFile = new File(((Picture) mItem).getFilePath());

        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            itemView.setImageBitmap(bmp);
        }
        
        statement = (EditText) v.findViewById(R.id.statmentEditText);
        statement.setText(mItem.getStatement());

        builder.setTitle(mItem.toString());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mItem.setStatement(statement.getText().toString());
                       
                       
                       FragmentTransaction ft =
                       getActivity().getFragmentManager().beginTransaction();
                       ft.replace(R.id.activity_study_edit_right_pane, new StudyEditItemsFragment());
                       ;
                       ft.commit();
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // TODO: User cancelled the dialog
                   }
               });
        
        
        
        
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
