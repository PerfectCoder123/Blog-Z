package com.example.autoslider.Models;

public class ModelUser {

    private String text;
    private String images,img;

    public ModelUser(String img, String images, String text) {
        this.images = images;
        this.text = text;
        this.img = img;
    }

  public ModelUser(){

    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
