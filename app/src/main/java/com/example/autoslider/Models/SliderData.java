package com.example.autoslider.Models;

public class SliderData {

    // string for our image url.
    private String imgUrl,link;
    // empty constructor which is
    // required when using Firebase.
    public SliderData() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // Constructor
    public SliderData(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getter method.
    public String getImgUrl() {
        return imgUrl;
    }

    // Setter method.
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}