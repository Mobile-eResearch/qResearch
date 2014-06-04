package de.eresearch.app.gui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.eresearch.app.R;
import de.eresearch.app.gui.StudyEditItemsFragment;
import de.eresearch.app.logic.model.Item;
import de.eresearch.app.logic.model.Picture;

/**
 * 
 * @author thg
 *
 */
public class StudyEditPictureListArrayAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private List<Item> mItems;
    private StudyEditItemsFragment fragment;
    
    public StudyEditPictureListArrayAdapter(Context context, int resource, List<Item> items, StudyEditItemsFragment fragment) {
        super(context, resource, items);
        this.context = context;
        mItems = items;
        this.fragment = fragment;
    }
    
    static class ViewHolder {
        public ImageView delete;
        public ImageView up;
        public ImageView down;
        public ImageView itemPictureContainer;
        public TextView itemFileName;
        public TextView itemStatement;
      }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     
      View rowView = convertView;
      ViewHolder viewHolder;
      
      if (convertView == null){
          viewHolder = new ViewHolder();
          rowView = inflater.inflate(R.layout.list_study_edit_picture, parent, false); 
          viewHolder.itemPictureContainer = (ImageView) rowView.findViewById(R.id.itemEditListPicture);
          viewHolder.itemFileName = (TextView) rowView.findViewById(R.id.itemFileName);
          viewHolder.itemStatement = (TextView) rowView.findViewById(R.id.itemStatement);
          viewHolder.delete = (ImageView) rowView.findViewById(R.id.itemDelButton);
          viewHolder.up = (ImageView) rowView.findViewById(R.id.itemUpButton);
          viewHolder.down = (ImageView) rowView.findViewById(R.id.itemDownButton);
          rowView.setTag(viewHolder);
      }else {
          // get holder back...much faster than inflate
          viewHolder = (ViewHolder) convertView.getTag();
      }

      
      ViewHolder holder = (ViewHolder) rowView.getTag();
      Item i = mItems.get(position);
      
     
      File imageFile = new File(((Picture) i).getFilePath());

      if (imageFile.exists()) {
          
          
          BitmapFactory.Options o = new BitmapFactory.Options();
          Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), o), 
                                                     200, 
                                                     200, 
                                                     false);
          
          //Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
          holder.itemPictureContainer.setImageBitmap(bmp);
      }
      
      holder.itemFileName.setText(i.toString());
      holder.itemStatement.setText(i.getStatement());
      holder.delete.setOnClickListener(onDeleteClickListener);
      holder.up.setOnClickListener(onUpClickListener);
      holder.down.setOnClickListener(onDownClickListener);
      holder.delete.setTag(i);
      holder.up.setTag(i);
      holder.down.setTag(i);
      
      if (position == 0)
          holder.up.setVisibility(View.INVISIBLE);
      else
          holder.up.setVisibility(View.VISIBLE);
     
      if (position == mItems.size()-1)
          holder.down.setVisibility(View.INVISIBLE);
      else
          holder.down.setVisibility(View.VISIBLE);
      
      return rowView;
    }
    
    
    OnClickListener onDeleteClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            Item i = (Item) v.getTag();
            fragment.onListDeleteClick(i);
        }
    };
    
    OnClickListener onUpClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            Item i = (Item) v.getTag();
            fragment.onListUpClick(i);
        }
    };
    
    OnClickListener onDownClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            Item i = (Item) v.getTag();
            fragment.onListDownClick(i);
        }
    };
    
   
}
