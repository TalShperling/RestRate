package com.example.restrate.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.restrate.MyApplication;

import java.util.List;

public class Model {
    public final static Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    ModelSQL modelSQL = new ModelSQL();

    private Model() {
    }

    LiveData<List<Restaurant>> restaurantList;

    public LiveData<List<Restaurant>> getAllRestaurants() {
        if (restaurantList == null) {
            restaurantList = modelSQL.getAllRestaurants();
            refreshAllRestaurants(null);
        }

        return restaurantList;
    }

    public void refreshAllRestaurants(final GenericRestaurantListenerWithNoParam listener) {
        // 1. get local last update date
        SharedPreferences sp = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long lastUpdated = sp.getLong("lastUpdated", 0);

        // 2. Get all updated records from firebase from the last update data
        modelFirebase.getAllRestaurants(lastUpdated, new GenericRestaurantListenerWithParam<List<Restaurant>>() {
            @Override
            public void onComplete(List<Restaurant> data) {
                // 3. Insert the new updates to the local db
                long lastU = 0;
                for (Restaurant rst : data) {
                    if(rst.getIsDeleted()) {
                        modelSQL.deleteRestaurantById(rst, null);
                    } else {
                        modelSQL.upsertRestaurant(rst, null);
                        if (rst.getLastUpdated() > lastU) {
                            lastU = rst.getLastUpdated();
                        }
                    }
                }

                // 4. Update the local last update date
                sp.edit().putLong("lastUpdated", lastU).commit();

                // 5. Return the updates data to the listeners
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    public void getRestaurantById(String id, GenericRestaurantListenerWithParam<Restaurant> listener) {
        modelFirebase.getRestaurantById(id, listener);
    }


    public void upsertRestaurant(Restaurant restToAdd, GenericRestaurantListenerWithParam<Restaurant> listener) {
        modelFirebase.upsertRestaurant(restToAdd, new GenericRestaurantListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant restaurant) {
                refreshAllRestaurants(new GenericRestaurantListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        listener.onComplete(restaurant);
                    }
                });
            }
        });
    }

    public void deleteRestaurantById(String id, GenericRestaurantListenerWithNoParam listener) {
        modelFirebase.deleteRestaurantById(id, listener);
    }

    public void getLatestId(Restaurant restToAdd, GenericRestaurantListenerWithNoParam listener) {
        modelFirebase.getLatestId(new GenericRestaurantListenerWithNoParam() {
            @Override
            public void onComplete() {

            }
        });
    }

    public void uploadImage(Bitmap imageBmp, GenericRestaurantListenerWithParam<String> listener) {
        modelFirebase.uploadImage(imageBmp, listener);
    }
}
