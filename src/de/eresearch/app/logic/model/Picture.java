
package de.eresearch.app.logic.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import de.eresearch.app.util.Util;

import java.io.File;

public class Picture extends Item {

    /**
     * ImageView for the bitmap of this picture
     */
    private ImageView mView = null;

    /**
     * Absolute path of this image file
     */
    private String mFilePath;

    /**
     * Creates a new picture with the given id.
     * 
     * @param id the id of this picture
     */
    public Picture(int id) {
        super(id);
    }

    /**
     * @param path The absolute path of the corresponding image file
     */
    public void setFilePath(String path) {
        mFilePath = path;
    }

    /**
     * @return The absolute path of the corresponding image file
     */
    public String getFilePath() {
        return mFilePath;
    }

    /**
     * Returns an {@link ImageView} of this picture to display it on the screen.
     * The image is loaded the first time this {@link ImageView} is requested.
     * No settings are configured yet! If the file does not exist in the given
     * path, the ImageView will be empty.
     * 
     * @return ImageView with a bitmap of the file or an empty ImageView
     */
    @Override
    public ImageView getView(Context context) {
        if (mView == null)
            mView = new ImageView(context);

        File imageFile = new File(mFilePath);

        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            mView.setImageBitmap(bmp);
        }

        return mView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!super.equals(o)) {
            return false;
        }

        if (!(o instanceof Picture)) {
            return false;
        }

        Picture other = (Picture) o;

        if (!Util.nullSafeEquals(mFilePath, other.mFilePath)) {
            return false;
        }

        return true;
    }
    
    @Override
    public String toString(){

        return new File(mFilePath).getName();
    }

}
