package com.example.restrate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RestaurantListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        FloatingActionButton addRestaurantBtn = view.findViewById(R.id.restlist_add_btn);

        addRestaurantBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_restaurantList_to_addRestaurant));

        return view;
    }
}