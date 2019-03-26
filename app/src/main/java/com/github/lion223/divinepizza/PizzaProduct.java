package com.github.lion223.divinepizza;


import java.util.List;

public class PizzaProduct {
    private String name, description, price, rating, weight, urlThumb, urlFull;
    private List<Boolean> types;
    //private String[] diameters;
    //private boolean[] types;
    //private some kind of bullshit i write while hes near, like now of the cause heh

    public PizzaProduct(){

    }

    public PizzaProduct(String name, String description, String price, String rating,
                        String weight, String urlThumb, String urlFull, List<Boolean> types) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.weight = weight;
        this.urlThumb = urlThumb;
        this.urlFull = urlFull;
        this.types = types;
        //this.diameters = diameters;
        //.types = types;
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


    public String getUrlThumb() {
        return urlThumb;
    }


    public String getUrlFull() {
        return urlFull;
    }

    public List<Boolean> getTypes() {
        return types;
    }

    /*
    public String[] getDiameters() {
        return diameters;
    }


    public boolean[] getTypes() {
        return types;
    }

    */
}
