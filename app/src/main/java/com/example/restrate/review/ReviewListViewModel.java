package com.example.restrate.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.restrate.model.Model;
import com.example.restrate.model.RestaurantWithReviews;

public class ReviewListViewModel extends ViewModel {
    private final MutableLiveData<String> restaurantId = new MutableLiveData<>();
    private final LiveData<RestaurantWithReviews> restaurantWithReviews =
            Transformations.switchMap(restaurantId, Model.instance::getRestaurantWithReviews);

    public void selectRestaurant(String id) {
        restaurantId.setValue(id);
    }

    public LiveData<RestaurantWithReviews> getRestaurantWithReviews() {
        return restaurantWithReviews;
    }
}
