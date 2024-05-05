package com.ak.mymanagement.model;

public class Component {
    private String name;
    private String model;
    private String quality;
    private String price;
    private String quantity;
    private String image;
    private String updatedBy;
    private String category;

    public Component() {}

    public Component(String name, String model, String quality, String price, String quantity, String imageUrl, String updatedBy, String category) {
        this.name = name;
        this.model = model;
        this.quality = quality;
        this.price = price;
        this.quantity = quantity;
        this.image = imageUrl;
        this.updatedBy = updatedBy;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
