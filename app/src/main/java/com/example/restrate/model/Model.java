package com.example.restrate.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.restrate.MyApplication;
import com.google.firebase.auth.FirebaseUser;

import java.util.EventListener;
import java.util.List;

public class Model {
    public final static Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    ModelSQL modelSQL = new ModelSQL();
    private final static SharedPreferences sp = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);

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

    public void refreshAllRestaurants(final GenericEventListenerWithNoParam listener) {
        // 1. get local last update date
        long lastUpdated = sp.getLong("lastUpdated", 0);

        // 2. Get all updated records from firebase from the last update data
        modelFirebase.getAllRestaurants(lastUpdated, new GenericEventListenerWithParam<List<Restaurant>>() {
            @Override
            public void onComplete(List<Restaurant> data) {
                // 3. Insert the new updates to the local db
                long lastU = 0;
                for (Restaurant rst : data) {
                    if (rst.getIsDeleted()) {
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

    public void getRestaurantById(String id, GenericEventListenerWithParam<Restaurant> listener) {
        modelFirebase.getRestaurantById(id, listener);
    }


    public void upsertRestaurant(Restaurant restToAdd, GenericEventListenerWithParam<Restaurant> listener) {
        modelFirebase.upsertRestaurant(restToAdd, new GenericEventListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant restaurant) {
                refreshAllRestaurants(new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        listener.onComplete(restaurant);
                    }
                });
            }
        });
    }

    public void deleteRestaurant(Restaurant restaurant, GenericEventListenerWithNoParam listener) {
        modelFirebase.deleteRestaurant(restaurant, new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                refreshAllRestaurants(new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onComplete();
                        }
                    }
                });
            }
        });
    }

    public void uploadImage(Bitmap imageBmp, GenericEventListenerWithParam<String> listener) {
        modelFirebase.uploadImage(imageBmp, listener);
    }

    public FirebaseUser getCurrentUser() {
        return modelFirebase.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return modelFirebase.isUserLoggedIn();
    }

    public void register(String email, String password, String fullName,GenericEventListenerWithNoParam onSuccessListener, GenericEventListenerWithNoParam onFailListener) {
        GenericEventListenerWithNoParam onSuccess = new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                onSuccessListener.onComplete();
            }
        };

        GenericEventListenerWithNoParam onFail = new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                onFailListener.onComplete();
            }
        };

        modelFirebase.register(email, password, fullName, onSuccess, onFail);
    }

    public void login(String email, String password, GenericEventListenerWithNoParam onSuccessListener, GenericEventListenerWithNoParam onFailListener) {

        GenericEventListenerWithNoParam onSuccess = new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                onSuccessListener.onComplete();
            }
        };

        GenericEventListenerWithNoParam onFail = new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                onFailListener.onComplete();
            }
        };

        modelFirebase.login(email, password, onSuccess, onFail);
    }

    public void logout(GenericEventListenerWithNoParam listener) {
        modelFirebase.logout(listener);
    }
}
