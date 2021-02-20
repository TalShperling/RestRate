package com.example.restrate.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.restrate.MyApplication;
import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.GenericEventListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.utils.Photos;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.restrate.Utils.isValidEmail;
import static com.example.restrate.utils.Photos.SELECT_PICTURE_FROM_CAMERA;
import static com.example.restrate.utils.Photos.SELECT_PICTURE_FROM_GALLERY;

public class RegistrationFragment extends Fragment {
    private View view;
    ImageView avatarImageView;
    ImageButton editImageButton;
    private TextInputLayout fullNameET;
    private TextInputLayout emailET;
    private TextInputLayout passwordET;
    private TextInputLayout repeatPasswordET;
    private Button registerBtn;
    private Button cancelBtn;
    private ProgressBar registerPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        avatarImageView = view.findViewById(R.id.register_image);
        editImageButton = view.findViewById(R.id.register_edit_image_btn);
        fullNameET = view.findViewById(R.id.register_full_name);
        emailET = view.findViewById(R.id.register_email);
        passwordET = view.findViewById(R.id.register_password);
        repeatPasswordET = view.findViewById(R.id.register_repeat_password);
        registerBtn = view.findViewById(R.id.register_register_btn);
        cancelBtn = view.findViewById(R.id.register_cancel_btn);
        registerPB = view.findViewById(R.id.register_pb);

        registerPB.setVisibility(View.INVISIBLE);

        repeatPasswordET.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    register();
                    return true;
                }

                return  false;
            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                editImage();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(getActivity());
                Utils.returnBack(view);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        return view;
    }

    private void register() {
        registerPB.setVisibility(View.VISIBLE);
        String email = emailET.getEditText().getText().toString();
        String password = passwordET.getEditText().getText().toString();
        String repeatPassword = repeatPasswordET.getEditText().getText().toString();
        String fullName = fullNameET.getEditText().getText().toString();

        if (verifyRegisterCredentials(fullName, email, password, repeatPassword)) {
            Model.instance.register(email, password, fullName, new GenericEventListenerWithNoParam() {
                @Override
                public void onComplete() {
                    Toast.makeText(getActivity(), "Register succeeded!", Toast.LENGTH_LONG).show();
                    registerPB.setVisibility(View.INVISIBLE);
                    navigateAfterRegister();
                }
            }, new GenericEventListenerWithNoParam() {
                @Override
                public void onComplete() {
                    Toast.makeText(getActivity(), "An error occurred while trying to register, please try again", Toast.LENGTH_LONG).show();
                    registerPB.setVisibility(View.INVISIBLE);
                    navigateAfterRegister();
                }
            });
        } else {
            registerPB.setVisibility(View.INVISIBLE);
        }
    }

    private void navigateAfterRegister() {
        Utils.hideKeyboard(getActivity());
        Utils.returnBack(view);
    }

    private boolean verifyRegisterCredentials(String fullName, String email, String password, String repeatPassword) {
        boolean isValid = true;

        if (fullName.equals("")) {
            isValid = false;
            fullNameET.setError("Name cannot be empty");
        } else {
            fullNameET.setErrorEnabled(false);
        }

        if (!isValidEmail(email)) {
            isValid = false;
            emailET.setError("Email is invalid");
        } else {
            emailET.setErrorEnabled(false);
        }

        if (password.equals("")) {
            isValid = false;
            passwordET.setError("Password cannot be empty");
        } else if (password.length() < 6) {
            isValid = false;
            passwordET.setError("Password must be at least 6 characters");
        } else {
            passwordET.setErrorEnabled(false);
        }

        if (repeatPassword.equals("")) {
            isValid = false;
            repeatPasswordET.setError("Repeat password cannot be empty");
        } else if (!repeatPassword.equals(password)) {
            isValid = false;
            repeatPasswordET.setError("This password mismatch to your password");
        } else {
            repeatPasswordET.setErrorEnabled(false);
        }

        return isValid;
    }

    private void editImage() {
        GenericEventListenerWithNoParam onCameraChosen = new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, SELECT_PICTURE_FROM_CAMERA);
            }
        };

        GenericEventListenerWithParam<Intent> onGalleryChosen = new GenericEventListenerWithParam<Intent>() {
            @Override
            public void onComplete(Intent i) {
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_FROM_GALLERY);
            }
        };

        Photos.editImage(getActivity(), onCameraChosen, onGalleryChosen);
    }

    private float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case Photos.SELECT_PICTURE_FROM_CAMERA:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        avatarImageView.setImageBitmap(selectedImage);
                    }

                    break;
                case Photos.SELECT_PICTURE_FROM_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        if (null != selectedImage) {
                            // update the preview image in the layout
                            avatarImageView.setImageURI(selectedImage);
                            avatarImageView.getLayoutParams().height = (int) convertDpToPixel(200, MyApplication.context);
                            avatarImageView.getLayoutParams().width = (int) convertDpToPixel(200, MyApplication.context);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideKeyboard(getActivity());
    }
}