package com.example.restrate.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDAO {
    @Query("select * from Restaurant")
    LiveData<List<Restaurant>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Restaurant... restaurants);

    @Delete
    void delete(Restaurant restaurant);
}
