package com.example.restrate.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.GenericEventListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.example.restrate.model.Review;
import com.example.restrate.restaurant.AddRestaurantFragmentDirections;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import static com.example.restrate.Utils.costMeterTextConverter;

public class AddReviewFragment extends Fragment {
    View view;
    String restaurantId;
    Restaurant restaurant;
    Review reviewToSave;

    TextView restName;
    TextView restAddress;
    TextView restLink;
    RatingBar restRate;
    TextView restCost;
    ImageView restAvatar;

    TextInputLayout reviewDesc;
    Spinner reviewCostMeter;
    RatingBar reviewRate;
    Button saveBtn;
    Button cancelBtn;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_review, container, false);
        restaurantId = AddReviewFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        Model.instance.getRestaurantById(restaurantId, new GenericEventListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant data) {
                restaurant = data;
                bindData();
            }
        });

        reviewToSave = new Review(restaurantId, Model.instance.getCurrentUser().getUid());

        restName = view.findViewById(R.id.addreview_rest_name);
        restAddress = view.findViewById(R.id.addreview_rest_address);
        restLink = view.findViewById(R.id.addreview_rest_link);
        restRate = view.findViewById(R.id.addreview_rest_rate);
        restCost = view.findViewById(R.id.addreview_rest_cost);
        restAvatar = view.findViewById(R.id.addreview_rest_avatar);

        reviewDesc = view.findViewById(R.id.addreview_review);
        reviewRate = view.findViewById(R.id.addreview_rate);
        saveBtn = view.findViewById(R.id.addreview_save_btn);
        cancelBtn = view.findViewById(R.id.addreview_cancel_btn);
        pb = view.findViewById(R.id.addreview_progress_bar);
        reviewCostMeter = view.findViewById(R.id.addreview_spinner);

        restRate.setOnClickListener(null);
        pb.setVisibility(View.INVISIBLE);

        initializeSpinner();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.returnBack(view);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveReview();
            }
        });

        return view;
    }

    private void initializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cost_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewCostMeter.setAdapter(adapter);
        reviewCostMeter.setSelection(2);

        reviewCostMeter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null) {
                    reviewToSave.setCostMeter(adapterView.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (adapterView != null) {
                    reviewToSave.setCostMeter(adapterView.getItemAtPosition(0).toString());
                }
            }
        });
    }

    private void saveReview() {
        pb.setVisibility(View.VISIBLE);
        reviewToSave.setDescription(reviewDesc.getEditText().getText().toString());
        reviewToSave.setRate(String.valueOf(reviewRate.getRating()));

        if (validateNewReviewForm(reviewToSave)) {
            saveReviewOnServer(reviewToSave);
        } else {
            pb.setVisibility(View.INVISIBLE);
        }
    }

    protected void onSaveRestaurantOnServerSuccess(Review rev) {
//        AddRestaurantFragmentDirections.ActionAddRestaurantToRestaurantInfo action = AddRestaurantFragmentDirections.actionAddRestaurantToRestaurantInfo(rev.get`());
//        Navigation.findNavController(view).navigate(action);
    }

    private void saveReviewOnServer(Review reviewToSaveOnServer) {
        Model.instance.upsertReview(reviewToSaveOnServer, new GenericEventListenerWithParam<Review>() {
            @Override
            public void onComplete(Review rev) {
                pb.setVisibility(View.INVISIBLE);
                onSaveRestaurantOnServerSuccess(rev);
            }
        });
    }

    private boolean validateNewReviewForm(Review reviewToValidate) {
        boolean isFormValid = true;

        if (reviewToValidate.getDescription().equals("")) {
            reviewDesc.setError("Name cannot be empty");
            isFormValid = false;
        } else {
            reviewDesc.setErrorEnabled(false);
        }

        return isFormValid;
    }

    private void bindData() {
        restName.setText(restaurant.getName());
        restAddress.setText(restaurant.getAddress());
        restLink.setText(restaurant.getSiteLink());
        restRate.setRating(Float.parseFloat(restaurant.getRate()));
        restCost.setText(costMeterTextConverter(restaurant.getCostMeter()));

        loadImage();
    }


    private void loadImage() {
        if (restaurant.getImageURL() != null) {
            Picasso.get().load(restaurant.getImageURL()).placeholder(R.drawable.restaurant).into(restAvatar);
        }
    }
}