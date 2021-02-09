package com.example.restrate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.restrate.model.GenericRestaurantListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantInfoFragment extends Fragment {
    TextView restaurantName;
    TextView restaurantDescription;
    TextView restaurantAddress;
    TextView restaurantPhone;
    TextView restaurantSiteLink;
    ImageView restaurantImage;
    Restaurant restaurant = new Restaurant();
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        restaurantName = view.findViewById(R.id.restinfo_name);
        restaurantDescription = view.findViewById(R.id.restinfo_description);
        restaurantAddress = view.findViewById(R.id.restinfo_address);
        restaurantPhone = view.findViewById(R.id.restinfo_phone);
        restaurantSiteLink = view.findViewById(R.id.restinfo_siteLink);
        restaurantImage = view.findViewById(R.id.restinfo_image);

        pb = view.findViewById(R.id.restinfo_pb);
        pb.setVisibility(View.VISIBLE);

        final String restaurantId = RestaurantInfoFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        Model.instance.getRestaurantById(restaurantId, new GenericRestaurantListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant data) {
                restaurant = data;
                bindData(restaurant);
                pb.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void bindData(Restaurant restaurant) {
        restaurantName.setText(restaurant.getName());
        restaurantDescription.setText(restaurant.getDescription());
        restaurantAddress.setText(restaurant.getAddress());
        restaurantPhone.setText(restaurant.getPhoneNumber());
        loadImage();
    }

    private void loadImage() {
        if(restaurant.getImageURL() != null) {
            Picasso.get().load(restaurant.getImageURL()).placeholder(R.drawable.restaurant).into(restaurantImage);
        }
    }
}