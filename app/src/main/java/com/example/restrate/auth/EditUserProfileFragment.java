package com.example.restrate.auth;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.User;
import com.squareup.picasso.Picasso;

public class EditUserProfileFragment extends RegistrationFragment {
    private User currentUser = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        pb.setVisibility(View.VISIBLE);

        currentUser.setFromModel(Model.instance.getCurrentUser());

        passwordET.setHint(getResources().getString(R.string.old_password));
        repeatPasswordET.setHint(getResources().getString(R.string.new_password));
        fullNameET.getEditText().setText(currentUser.getFullName());
        emailET.getEditText().setText(currentUser.getEmail());
        registerBtn.setText("Update");


        loadImage();

        pb.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    protected boolean verifyPasswords(String oldPassword, String newPassword) {
        boolean isValid = true;

        if (oldPassword.equals("")) {
            isValid = false;
            passwordET.setError("Old password cannot be empty");
        } else if (oldPassword.length() < 6) {
            isValid = false;
            passwordET.setError("Old password must be at least 6 characters");
        } else {
            passwordET.setErrorEnabled(false);
        }

        if (newPassword.equals("")) {
            isValid = false;
            repeatPasswordET.setError("New password cannot be empty");
        } else if (newPassword.length() < 6) {
            isValid = false;
            repeatPasswordET.setError("New password must be at least 6 characters");
        } else {
            repeatPasswordET.setErrorEnabled(false);
        }

        return isValid;
    }

    @Override
    protected void saveUserOnServer(String email, String oldPassword, String newPassword, String fullName, String url) {
        Model.instance.reAuthenticate(email, oldPassword, new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                Model.instance.updateUser(email, newPassword, fullName, Uri.parse(url), new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        onUserSaveSuccess(fullName);
                    }
                }, new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        onUserSaveFail();
                    }
                });
            }
        }, new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                Toast.makeText(getActivity(), "Email/Old password are incorrect!", Toast.LENGTH_LONG).show();
                passwordET.setError("Email/Old password are incorrect");
                emailET.setError("Email/Old password are incorrect");
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onUserSaveSuccess(String fullName) {
        Toast.makeText(getActivity(), "Profile was updated successfully!", Toast.LENGTH_LONG).show();
        pb.setVisibility(View.INVISIBLE);
        navigateAfterRegister();
    }

    @Override
    protected void onUserSaveFail() {
        Toast.makeText(getActivity(), "An error occurred while trying to update your profile, please try again", Toast.LENGTH_LONG).show();
        pb.setVisibility(View.INVISIBLE);
        navigateAfterRegister();
    }

    private void loadImage() {
        if (currentUser.getImageURL() != null) {
            Picasso.get().load(currentUser.getImageURL()).placeholder(R.drawable.restaurant).into(avatarImageView);
        }
    }
}