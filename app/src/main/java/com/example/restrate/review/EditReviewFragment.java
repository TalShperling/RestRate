package com.example.restrate.review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Review;

public class EditReviewFragment extends AddReviewFragment {
    String reviewId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        reviewId = EditReviewFragmentArgs.fromBundle(getArguments()).getReviewId();

        pb.setVisibility(View.VISIBLE);

        Model.instance.getReviewById(reviewId, new GenericEventListenerWithParam<Review>() {
            @Override
            public void onComplete(Review data) {
                reviewToSave = data;

                reviewDesc.getEditText().setText(reviewToSave.getDescription());
                reviewCostMeter.setSelection(Integer.parseInt(reviewToSave.getCostMeter()) - 1);
                reviewRate.setRating(Float.valueOf(reviewToSave.getRate()));
            }
        });

        return view;
    }
}