package com.example.autoslider.Models;

import java.util.Map;

public class CategoryModel {
    public String name;
    public String imageUrl;
    public Map<String,IdModel> posts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, IdModel> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, IdModel> posts) {
        this.posts = posts;
    }
}

