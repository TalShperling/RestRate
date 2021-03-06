package com.example.restrate.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Review... reviews);

    @Query("SELECT * FROM Review where restaurantId = :id")
    List<Review> getReviewsByRestaurantId(String id);

    @Query("SELECT * FROM Review where userId = :id")
    List<Review> getReviewsByUserId(String id);

    @Delete
    void delete(Review review);
}
