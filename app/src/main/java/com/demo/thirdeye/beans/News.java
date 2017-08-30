package com.demo.thirdeye.beans;

import android.graphics.Bitmap;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by ammu on 8/4/2017.
 */

public class News {
    String heading;
    List<Bitmap> newsPic;
    String Details;
    int likeCount;
    int viewCount;
    int dislikeCount;
    UserProfile userProfile;
    String date;
    String time;

    public News(){}

    public News(String heading, List<Bitmap> newsPic, String details, int likeCount, int viewCount, int dislikeCount, UserProfile userProfile, String date, String time) {
        this.heading = heading;
        this.newsPic = newsPic;
        Details = details;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.dislikeCount = dislikeCount;
        this.userProfile = userProfile;
        this.date =date;
        this.time = time;
    }
    public News(String heading, List<Bitmap> newsPic, String details, int likeCount, int viewCount, int dislikeCount, UserProfile userProfile) {
        this.heading = heading;
        this.newsPic = newsPic;
        Details = details;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.dislikeCount = dislikeCount;
        this.userProfile = userProfile;
        this.date =date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        time = time;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public List<Bitmap> getNewsPic() {
        return newsPic;
    }

    public void setNewsPic(List<Bitmap> newsPic) {
        this.newsPic = newsPic;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
