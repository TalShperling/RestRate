package com.example.restrate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RestaurantInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        String RestId = RestaurantInfoFragmentArgs.fromBundle(getArguments()).getRestaurantId();
        TextView text = (TextView)view.findViewById(R.id.restinfo_id_text);

        text.setText(RestId);
        

        return view;
    }
}