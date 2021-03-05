package com.example.restrate.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RestaurantWithReviews {
    @Embedded
    public Restaurant restaurant;
    @Relation(
            parentColumn = "id",
            entityColumn = "restaurantId"
    )
    public List<Review> reviews;
}
