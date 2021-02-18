package com.example.restrate.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.restrate.Utils.isValidEmail;

public class RegistrationFragment extends Fragment {
    private View view;
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

        fullNameET = view.findViewById(R.id.register_full_name);
        emailET = view.findViewById(R.id.register_email);
        passwordET = view.findViewById(R.id.register_password);
        repeatPasswordET = view.findViewById(R.id.register_repeat_password);
        registerBtn = view.findViewById(R.id.register_register_btn);
        cancelBtn = view.findViewById(R.id.register_cancel_btn);
        registerPB = view.findViewById(R.id.register_pb);

        registerPB.setVisibility(View.INVISIBLE);

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
}