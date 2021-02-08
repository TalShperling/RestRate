package com.example.restrate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.model.GenericRestaurantListenerWithNoParam;
import com.example.restrate.model.GenericRestaurantListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;

public class AddRestaurantFragment extends Fragment {
    View view;
    ImageView avatarImageView;
    ImageButton editImageButton;
    TextInputLayout nameET;
    TextInputLayout descriptionET;
    TextInputLayout addressET;
    TextInputLayout phoneET;
    TextInputLayout linkET;
    Button cancelBtn;
    Button saveBtn;
    ProgressBar pb;
    Restaurant restaurant = new Restaurant();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_restaurant, container, false);
        pb = view.findViewById(R.id.addrest_progress_bar);

        avatarImageView = view.findViewById(R.id.addrest_image);
        editImageButton = view.findViewById(R.id.addrest_edit_image_btn);
        nameET = view.findViewById(R.id.addrest_name);
        descriptionET = view.findViewById(R.id.addrest_description);
        addressET = view.findViewById(R.id.addrest_address);
        phoneET = view.findViewById(R.id.addrest_phoneNumber);
        linkET = view.findViewById(R.id.addrest_siteLink);

        cancelBtn = view.findViewById(R.id.addrest_cancel_btn);
        saveBtn = view.findViewById(R.id.addrest_save_btn);

        editImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                editImage();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRestaurant();
            }
        });

        cancelBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_addRestaurantFragment_pop));

        pb.setVisibility(View.INVISIBLE);

        return view;
    }

    private void cancelAddRestaurant() {
        Navigation.findNavController(view).popBackStack();
    }

    private boolean validateNewRestaurantForm() {
        boolean isFormValid = true;

        if (restaurant.getName().equals("")) {
            nameET.setError("Name cannot be empty");
            isFormValid = false;
        } else {
            nameET.setErrorEnabled(false);
        }

        if (restaurant.getDescription().equals("")) {
            descriptionET.setError("Description cannot be empty");
            isFormValid = false;
        } else {
            descriptionET.setErrorEnabled(false);
        }

        if (restaurant.getPhoneNumber().equals("")) {
            phoneET.setError("Phone number cannot be empty");
            isFormValid = false;
        } else {
            phoneET.setErrorEnabled(false);
        }

        if (restaurant.getAddress().equals("")) {
            addressET.setError("Address cannot be empty");
            isFormValid = false;
        } else {
            addressET.setErrorEnabled(false);
        }

        if (restaurant.getSiteLink().equals("")) {
            linkET.setError("Site URL cannot be empty");
            isFormValid = false;
        } else {
            linkET.setErrorEnabled(false);
        }

        return isFormValid;
    }

    static int id = 0;

    private void saveRestaurant() {
        restaurant.setName(nameET.getEditText().getText().toString());
        restaurant.setDescription(descriptionET.getEditText().getText().toString());
        restaurant.setPhoneNumber(phoneET.getEditText().getText().toString());
        restaurant.setAddress(addressET.getEditText().getText().toString());
        restaurant.setSiteLink(linkET.getEditText().getText().toString());

        if (validateNewRestaurantForm()) {
            pb.setVisibility(View.VISIBLE);

            restaurant.setId(String.valueOf(id));
            id++;

            BitmapDrawable drawable = (BitmapDrawable) avatarImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            Model.instance.uploadImage(bitmap, restaurant.getId(), new GenericRestaurantListenerWithParam<String>() {
                @Override
                public void onComplete(String url) {
                    if (url == null) {
                        cancelAddRestaurant();
                    } else {
                        restaurant.setImageURL(url);
                        Model.instance.addRestaurant(restaurant, new GenericRestaurantListenerWithNoParam() {
                            @Override
                            public void onComplete() {
                                AddRestaurantFragmentDirections.ActionAddRestaurantToRestaurantInfo action = AddRestaurantFragmentDirections.actionAddRestaurantToRestaurantInfo(restaurant.getId());
                                Navigation.findNavController(view).navigate(action);
                            }
                        });
                    }
                }
            });
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESULT_SUCCESS = 0;

    private void editImage() {
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatarImageView.setImageBitmap(imageBitmap);
        }
    }
}