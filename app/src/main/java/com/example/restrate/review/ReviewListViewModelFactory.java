package com.example.restrate.review;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class ReviewListViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            T model = modelClass.newInstance();
            ((ReviewListViewModel) model).init();
            return model;
        } catch (IllegalAccessException |
                InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}