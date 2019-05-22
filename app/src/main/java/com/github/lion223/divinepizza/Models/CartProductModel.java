package com.github.lion223.divinepizza.Models;

public class CartProductModel {
    private String name, price, total_price, quantity;

    public CartProductModel(){

    }

    public CartProductModel(String name, String price, String total_price, String quantity) {
        this.price = price;
        this.total_price = total_price;
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getQuantity() { return quantity; }

}
