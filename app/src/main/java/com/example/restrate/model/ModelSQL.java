package com.example.restrate.model;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ModelSQL {
    public LiveData<List<Restaurant>> getAllRestaurants() {
        return AppLocalDb.db.restaurantDAO().getAll();
    }

    public LiveData<RestaurantWithReviews> getRestaurantWithReviews(String id) {
        return AppLocalDb.db.restaurantDAO().getRestaurantWithReviews(id);
    }

    public void upsertRestaurant(Restaurant restToAdd, GenericEventListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.restaurantDAO().insertAll(restToAdd);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void deleteRestaurantById(Restaurant restaurant, GenericEventListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.restaurantDAO().delete(restaurant);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void upsertReview(Review reviewToAdd, GenericEventListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.reviewDAO().insertAll(reviewToAdd);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void deleteReview(Review review, GenericEventListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.reviewDAO().delete(review);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
}
