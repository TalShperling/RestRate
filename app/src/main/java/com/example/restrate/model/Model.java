package com.example.restrate.model;

import android.graphics.Bitmap;

import java.util.List;

public class Model {
    public final static Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    ModelSQL modelSQL = new ModelSQL();

    private Model() {
    }

    public void getAllRestaurants(GenericRestaurantListenerWithParam<List<Restaurant>> listener) {
        modelFirebase.getAllRestaurants(listener);
    }

    public void getRestaurantById(String id, GenericRestaurantListenerWithParam<Restaurant> listener) {
        modelFirebase.getRestaurantById(id, listener);
    }


    public void addRestaurant(Restaurant restToAdd, GenericRestaurantListenerWithNoParam listener) {
        modelFirebase.upsertRestaurant(restToAdd, listener);
    }

    public void updateRestaurant(Restaurant restaurant, GenericRestaurantListenerWithNoParam listener) {
        modelFirebase.upsertRestaurant(restaurant, listener);
    }

    public void deleteRestaurantById(String id, GenericRestaurantListenerWithNoParam listener) {
        modelFirebase.deleteRestaurantById(id, listener);
    }

    public void uploadImage(Bitmap imageBmp, String name, GenericRestaurantListenerWithParam<String> listener) {
        modelFirebase.uploadImage(imageBmp, name, listener);
    }
}
