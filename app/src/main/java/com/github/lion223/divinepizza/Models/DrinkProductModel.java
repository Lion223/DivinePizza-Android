package com.github.lion223.divinepizza.Models;

import java.util.List;

public class DrinkProductModel {
   private String name, description, price, rating, weight, image;
   private List<Boolean> types;

   public DrinkProductModel(){

   }

   public DrinkProductModel(String name, String description, String price, String rating,
                            String weight, String image, List<Boolean> types) {
      this.price = price;
      this.name = name;
      this.description = description;
      this.rating = rating;
      this.weight = weight;
      this.image = image;
      this.types = types;
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

   public String getImage() {
      return image;
   }

   public List<Boolean> getTypes() {
      return types;
   }
}
