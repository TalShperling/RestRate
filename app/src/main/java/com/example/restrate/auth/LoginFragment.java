package com.example.restrate.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.User;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.restrate.Utils.isValidEmail;

public class LoginFragment extends Fragment {
    private View view;
    private TextInputLayout emailET;
    private TextInputLayout passwordET;
    private TextView newUser;
    private Button loginBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        if (Model.instance.isUserLoggedIn()) {
            navigateAfterLoggedIn();
        }

        emailET = view.findViewById(R.id.login_email);
        passwordET = view.findViewById(R.id.login_password);
        loginBtn = view.findViewById(R.id.login_login_btn);
        newUser = view.findViewById(R.id.login_newUser);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToRegister();
            }
        });

        return view;
    }

    private void login() {
        String email = emailET.getEditText().getText().toString();
        String password = passwordET.getEditText().getText().toString();

        if (verifyLoginCredentials(email, password)) {
            Model.instance.login(email, password, new GenericEventListenerWithNoParam() {
                @Override
                public void onComplete() {
                    navigateAfterLoggedIn();
                }
            });
        }
    }

    private void navigateAfterLoggedIn() {
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_restaurantListFragment);
    }

    private void navigateToRegister() {
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment);
    }

    private boolean verifyLoginCredentials(String email, String password) {
        boolean isValid = true;

        if (!isValidEmail(email)) {
            isValid = false;
            emailET.setError("Email is invalid");
        } else {
            emailET.setErrorEnabled(false);
        }

        if (password.equals("")) {
            isValid = false;
            passwordET.setError("Password cannot be empty");
        } else {
            passwordET.setErrorEnabled(false);
        }

        return isValid;
    }
}