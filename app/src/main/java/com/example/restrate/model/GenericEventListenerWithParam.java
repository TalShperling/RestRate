package com.example.restrate.model;

public interface GenericEventListenerWithParam<T> {
    void onComplete(T data);
}