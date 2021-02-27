package com.example.restrate.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RestaurantDAO {
    @Query("select * from Restaurant")
    LiveData<List<Restaurant>> getAll();

    @Transaction
    @Query("SELECT * FROM Restaurant where id = :id")
    LiveData<RestaurantWithReviews> getRestaurantWithReviews(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Restaurant... restaurants);

    @Delete
    void delete(Restaurant restaurant);
}
