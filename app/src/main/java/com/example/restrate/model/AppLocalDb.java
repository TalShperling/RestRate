package com.example.restrate.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.restrate.MyApplication;

@Database(entities = {Restaurant.class, Review.class}, version = 15)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract RestaurantDAO restaurantDAO();
    public abstract ReviewDAO reviewDAO();
}

public class AppLocalDb {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}