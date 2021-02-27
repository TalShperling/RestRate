package com.example.restrate.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

@Dao
public interface ReviewDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Review... reviews);

    @Delete
    void delete(Review review);
}
