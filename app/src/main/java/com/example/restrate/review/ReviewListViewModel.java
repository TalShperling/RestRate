package com.example.restrate.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restrate.model.Model;
import com.example.restrate.model.Review;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReviewListViewModel extends ViewModel {
    private final MutableLiveData<String> restaurantId = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showAddOption = new MutableLiveData<>(true);

    private final MediatorLiveData<List<Review>> reviews = new MediatorLiveData<>();

    public void init () {
        reviews.addSource(userId, value -> {
            try {
                reviews.setValue(Model.instance.getReviewsByUserId(value));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        reviews.addSource(restaurantId, value -> {
            try {
                reviews.setValue(Model.instance.getReviewsByRestaurantId(value));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void selectRestaurant(String id) {
        restaurantId.setValue(id);
    }

    public void selectUser(String id) {
        userId.setValue(id);
    }

    public void setIsAddShown(boolean isShown) {
        showAddOption.setValue(isShown);
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> isAddShown() {
        return showAddOption;
    }

    public String getRestaurantId() { return restaurantId.getValue(); }
    public String getUserId() { return userId.getValue(); }
}
