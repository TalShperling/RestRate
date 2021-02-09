package com.example.restrate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.model.GenericRestaurantListenerWithNoParam;
import com.example.restrate.model.GenericRestaurantListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_CANCELED;
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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAddRestaurant();
            }
        });

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Operation failed");
                        builder.setMessage("Could not save the new restaurant, please try again later");

                        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                cancelAddRestaurant();
                            }
                        });

                        builder.show();
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

    private void editImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        avatarImageView.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                avatarImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
}