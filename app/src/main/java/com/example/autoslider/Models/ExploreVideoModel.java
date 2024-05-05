package com.example.autoslider.Models;

public class ExploreVideoModel {
    String videoUrl;
    String title,likeCount;
    String description;
    String videoId;






    public ExploreVideoModel(String videoUrl, String title, String description) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.description = description;
    }

    public ExploreVideoModel() {

    }
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
