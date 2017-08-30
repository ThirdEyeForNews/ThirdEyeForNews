package com.demo.thirdeye.beans;

/**
 * Created by ammu on 7/28/2017.
 */

public class DataObject {
    private int imageId;
    private int caption;
    private int captionDetails;
    private String imageName;
    public DataObject(int imageId, String imageName,int caption,int captionDetails) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.caption = caption;
        this.captionDetails = captionDetails;
    }

    public int getCaption() {
        return caption;
    }

    public void setCaption(int caption) {
        this.caption = caption;
    }

    public int getCaptionDetails() {
        return captionDetails;
    }

    public void setCaptionDetails(int captionDetails) {
        this.captionDetails = captionDetails;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

