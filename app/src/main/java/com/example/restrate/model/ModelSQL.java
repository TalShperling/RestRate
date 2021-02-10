package com.example.restrate.model;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ModelSQL {
    public LiveData<List<Restaurant>> getAllRestaurants() {
        return AppLocalDb.db.restaurantDAO().getAll();
    }

    public void upsertRestaurant(Restaurant restToAdd, GenericRestaurantListenerWithNoParam listener) {
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

    public void deleteRestaurantById(Restaurant restaurant, GenericRestaurantListenerWithNoParam listener) {
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
}
