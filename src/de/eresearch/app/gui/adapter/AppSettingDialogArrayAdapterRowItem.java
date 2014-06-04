package de.eresearch.app.gui.adapter;

/**
 * Class for AppSettingDialogArrayAdapterRowItem
 * 
 * @author domme
 *
 */
public class AppSettingDialogArrayAdapterRowItem {
    private int imageResourceId;
    private String local;
    private int textResourceId;
    
    /**
     * Constructor of AppSettingDialogArrayAdapterRowItem
     * 
     * @param imageResourceId
     * @param textResourceId
     * @param local
     */
    public AppSettingDialogArrayAdapterRowItem(int imageResourceId, 
            int textResourceId, String local){
        this.setImageResourceId(imageResourceId);
        this.setTextResourceId(textResourceId);
        this.local = local;
    }

    /**
     * @return the imageResourceId
     */
    public int getImageResourceId() {
        return imageResourceId;
    }

    /**
     * @param imageResourceId the imageResourceId to set
     */
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    /**
     * @return the textResourceId
     */
    public int getTextResourceId() {
        return textResourceId;
    }

    /**
     * @param textResourceId the textResourceId to set
     */
    public void setTextResourceId(int textResourceId) {
        this.textResourceId = textResourceId;
    }

    /**
     * @return the local
     */
    public String getLocal() {
        return local;
    }

    /**
     * @param local the local to set
     */
    public void setLocal(String local) {
        this.local = local;
    }


}
