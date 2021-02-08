package com.example.restrate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AddRestaurantFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_restaurant, container, false);
        Button cancelBtn = view.findViewById(R.id.addrest_cancel_btn);
        Button saveBtn = view.findViewById(R.id.addrest_save_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String restId = "1234";

                AddRestaurantFragmentDirections.ActionAddRestaurantToRestaurantInfo action = AddRestaurantFragmentDirections.actionAddRestaurantToRestaurantInfo(restId);
                Navigation.findNavController(view).navigate(action);
            }
        });

        cancelBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_addRestaurantFragment_pop));

        return view;
    }
}