package com.example.restrate.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.example.restrate.MyApplication;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Model {
    public final static Model instance = new Model();
    private final static SharedPreferences sp = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
    ModelFirebase modelFirebase = new ModelFirebase();
    ModelSQL modelSQL = new ModelSQL();
    LiveData<List<Restaurant>> restaurantList;

    private Model() {
    }

    public LiveData<List<Restaurant>> getAllRestaurants() {
        if (restaurantList == null) {
            restaurantList = modelSQL.getAllRestaurants();
            refreshAllReviews(null);
            refreshAllRestaurants(null);
        }

        return restaurantList;
    }

    public void refreshAllRestaurants(final GenericEventListenerWithNoParam listener) {
        // 1. get local last update date
        long lastUpdated = sp.getLong("lastUpdatedRestaurants", 0);

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
                sp.edit().putLong("lastUpdatedRestaurants", lastU).commit();

                // 5. Return the updates data to the listeners
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    public void refreshAllReviews(final GenericEventListenerWithNoParam listener) {
        long lastUpdated = sp.getLong("lastUpdatedReviews", 0);

        modelFirebase.getAllReviews(lastUpdated, new GenericEventListenerWithParam<List<Review>>() {
            @Override
            public void onComplete(List<Review> data) {
                long lastU = 0;
                for (Review review : data) {
                    if (review.getIsDeleted()) {
                        modelSQL.deleteReview(review, null);
                    } else {
                        modelSQL.upsertReview(review, null);
                        if (review.getLastUpdated() > lastU) {
                            lastU = review.getLastUpdated();
                        }
                    }
                }

                sp.edit().putLong("lastUpdatedReviews", lastU).commit();

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
                Review review = new Review(restaurant.getId(), getCurrentUser().getUid());
                review.setCostMeter("2");
                review.setDescription("aaaa this is the description");
                review.setRate("4");
                review.setUserDisplayName("vaisman hard coded");
                modelFirebase.upsertReview(review, new GenericEventListenerWithParam<Review>() {

                    @Override
                    public void onComplete(Review data) {
                        refreshAllReviews(new GenericEventListenerWithNoParam() {
                            @Override
                            public void onComplete() {
                                listener.onComplete(restaurant);
                            }
                        });
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

    public LiveData<RestaurantWithReviews> getRestaurantWithReviews(String id) {
        return modelSQL.getRestaurantWithReviews(id);
    }

    public void upsertReview(Review reviewToAdd, GenericEventListenerWithParam<Review> listener) {
        modelFirebase.upsertReview(reviewToAdd, new GenericEventListenerWithParam<Review>() {
            @Override
            public void onComplete(Review review) {
                refreshAllReviews(new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        listener.onComplete(review);
                    }
                });
            }
        });
    }

    public void deleteReview(Review review, GenericEventListenerWithNoParam listener) {
        modelFirebase.deleteReview(review, new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                refreshAllReviews(new GenericEventListenerWithNoParam() {
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

    public void register(String email, String password, String fullName, Uri imageUri, GenericEventListenerWithNoParam onSuccessListener, GenericEventListenerWithNoParam onFailListener) {
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

        modelFirebase.register(email, password, fullName, imageUri, onSuccess, onFail);
    }

    public void updateUser(String email, String password, String fullName, Uri imageUri, GenericEventListenerWithNoParam onSuccessListener, GenericEventListenerWithNoParam onFailListener) {
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

        modelFirebase.update(email, password, fullName, imageUri, onSuccess, onFail);
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

    public void reAuthenticate(String email, String password, GenericEventListenerWithNoParam onSuccessListener, GenericEventListenerWithNoParam onFailListener) {

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

        modelFirebase.reAuthenticate(email, password, onSuccess, onFail);
    }

    public void logout(GenericEventListenerWithNoParam listener) {
        modelFirebase.logout(listener);
    }
}
