package com.ak.mymanagement.model;

import android.net.Uri;

public class Component {
    private String name;
    private String quality;
    private String price;
    private String quantity;
    private String image;
    private String updatedBy;

    public Component() {}

    public Component(String name, String quality, String price, String quantity, String imageUrl, String updatedBy) {
        this.name = name;
        this.quality = quality;
        this.price = price;
        this.quantity = quantity;
        this.image = imageUrl;
        this.updatedBy = updatedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
