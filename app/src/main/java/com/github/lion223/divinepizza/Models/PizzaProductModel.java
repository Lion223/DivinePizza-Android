package com.github.lion223.divinepizza.Models;


import java.util.List;

public class PizzaProductModel {
    private String name, description, diameter, price, rating, weight, img_thumb_url, img_full_url;
    private List<Boolean> types;
    //private List<String> diameters;

    public PizzaProductModel(){

    }

    public PizzaProductModel(String name, String description, String diameter, String price, String rating,
                             String weight, String img_thumb_url, String img_full_url, List<Boolean> types) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.weight = weight;
        this.img_thumb_url = img_thumb_url;
        this.img_full_url = img_full_url;
        this.types = types;
        this.diameter = diameter;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getWeight() {
        return weight;
    }

    public String getImg_thumb_url() {
        return img_thumb_url;
    }

    public String getImg_full_url() {
        return img_full_url;
    }

    public List<Boolean> getTypes() {
        return types;
    }

    public String getDiameter() { return diameter; }
}
