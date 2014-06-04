package de.eresearch.app.logic.tasks.media;

import android.os.AsyncTask;

import de.eresearch.app.logic.model.Picture;

/** Task to save a picture with statement etc.
 * 
 */


import android.content.Context;

import java.util.concurrent.CountDownLatch;

import de.eresearch.app.db.DatabaseConnector;
import de.eresearch.app.db.exception.HelperNotFoundException;
import de.eresearch.app.db.helper.ItemHelper;


/** Saves a picture for a study to the database.
 *
 */
public class SaveStudyItemsTask extends AsyncTask<Void, Void, Void> {
    public final CountDownLatch signal = new CountDownLatch(1);    

    private Context context;
    private Picture picture;
    private ItemHelper pch;
    
    public SaveStudyItemsTask(Context context, Picture picture){
        this.context = context;
        this.picture = picture;
        }   
    
         
    /**
     *  Saves the picture to the database.
     */
 


    @Override
    protected Void doInBackground(Void...voids ) {
        try {
            pch = (ItemHelper) DatabaseConnector.getInstance(context).getHelper(DatabaseConnector.TYPE_ITEM);
            pch.saveObject(picture);
        } catch (HelperNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    protected void onPostExecute(){
        signal.countDown();  
    }

}




  


