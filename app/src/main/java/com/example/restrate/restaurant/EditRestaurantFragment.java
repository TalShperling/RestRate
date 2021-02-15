package com.example.restrate.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.GenericEventListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.squareup.picasso.Picasso;

public class EditRestaurantFragment extends AddRestaurantFragment {
    Restaurant editRestaurant = new Restaurant();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final String restaurantId = RestaurantInfoFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        pb.setVisibility(View.VISIBLE);

        Model.instance.getRestaurantById(restaurantId, new GenericEventListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant restaurant) {
                editRestaurant = restaurant;

                nameET.getEditText().setText(editRestaurant.getName());
                descriptionET.getEditText().setText(editRestaurant.getDescription());
                addressET.getEditText().setText(editRestaurant.getAddress());
                phoneET.getEditText().setText(editRestaurant.getPhoneNumber());
                linkET.getEditText().setText(editRestaurant.getSiteLink());

                loadImage();

                pb.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    @Override
    protected void saveRestaurant() {
        editRestaurant.setName(nameET.getEditText().getText().toString());
        editRestaurant.setDescription(descriptionET.getEditText().getText().toString());
        editRestaurant.setPhoneNumber(phoneET.getEditText().getText().toString());
        editRestaurant.setAddress(addressET.getEditText().getText().toString());
        editRestaurant.setSiteLink(linkET.getEditText().getText().toString());

        if (validateNewRestaurantForm(editRestaurant)) {
            saveRestaurantOnServer(editRestaurant);
        }
    }

    @Override
    protected void onSaveRestaurantOnServerSuccess(Restaurant rest) {
        EditRestaurantFragmentDirections.ActionEditRestaurantFragmentToRestaurantInfoFragment direction = EditRestaurantFragmentDirections.actionEditRestaurantFragmentToRestaurantInfoFragment(rest.getId());
        Navigation.findNavController(view).navigate(direction);
    }

    private void loadImage() {
        if (editRestaurant.getImageURL() != null) {
            Picasso.get().load(editRestaurant.getImageURL()).placeholder(R.drawable.restaurant).into(avatarImageView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideKeyboard(getActivity());
    }
}