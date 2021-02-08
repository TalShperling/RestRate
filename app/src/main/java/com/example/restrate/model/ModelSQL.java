package com.example.restrate.model;

import android.os.AsyncTask;

import java.util.List;

public class ModelSQL {
    public void getAllRestaurants(GenericRestaurantListenerWithParam<List<Restaurant>> listener) {
        class MyAsyncTask extends AsyncTask {
            List<Restaurant> restList;

            @Override
            protected Object doInBackground(Object[] objects) {
                restList = AppLocalDb.db.restaurantDAO().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onComplete(restList);
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void addRestaurant(Restaurant restToAdd, GenericRestaurantListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.restaurantDAO().insertAll(restToAdd);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onComplete();
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void updateRestaurant(Restaurant restaurant, GenericRestaurantListenerWithNoParam listener) {
        class MyAsyncTask extends AsyncTask {

            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.restaurantDAO().update(restaurant);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onComplete();
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
                listener.onComplete();
            }
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
}
