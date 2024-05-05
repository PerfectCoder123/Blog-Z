package com.example.autoslider.Models;

public class TrendingFirebaseModel {

    private String profileImage,postImage,Date,username;

    public TrendingFirebaseModel(String profileImage, String postImage, String date, String username) {
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.Date = date;
        this.username = username;
    }

    public TrendingFirebaseModel() {

    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
