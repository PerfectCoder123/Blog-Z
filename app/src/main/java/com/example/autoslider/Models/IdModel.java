package com.example.autoslider.Models;

public class IdModel {
    String postId;
    String userId;

    public IdModel(String postId) {
        this.postId = postId;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public IdModel() {

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

}
