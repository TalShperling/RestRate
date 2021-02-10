package com.example.restrate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;

import java.util.List;

public class RestaurantListViewModel extends ViewModel {
    private LiveData<List<Restaurant>> restaurants = Model.instance.getAllRestaurants();

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }
}
