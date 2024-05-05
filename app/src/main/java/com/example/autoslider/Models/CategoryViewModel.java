package com.example.autoslider.Models;

public class CategoryViewModel {
    private  String imgUrl;
    private  String blogTitle;
    private  String date;
    private String postId;

    public CategoryViewModel(String imgUrl, String blogTitle, String date, String postId) {
        this.imgUrl = imgUrl;
        this.blogTitle = blogTitle;
        this.date = date;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
