package com.example.restrate.model;

public interface GenericRestaurantListenerWithParam<T> {
    void onComplete(T data);
}