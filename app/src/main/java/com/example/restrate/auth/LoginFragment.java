package com.example.restrate.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.DrawerLocker;
import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.restrate.Utils.hideKeyboard;
import static com.example.restrate.Utils.isValidEmail;

public class LoginFragment extends Fragment {
    private View view;
    private TextInputLayout emailET;
    private TextInputLayout passwordET;
    private TextView newUser;
    private Button loginBtn;
    private ProgressBar loginPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        emailET = view.findViewById(R.id.login_email);
        passwordET = view.findViewById(R.id.login_password);
        loginBtn = view.findViewById(R.id.login_login_btn);
        newUser = view.findViewById(R.id.login_newUser);
        loginPB = view.findViewById(R.id.login_pb);

        loginPB.setVisibility(View.INVISIBLE);

        passwordET.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    login();
                    return true;
                }

                return  false;
            }
        });

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Model.instance.isUserLoggedIn()) {
            Log.d("TAG", "User is already logged in: " + Model.instance.getCurrentUser());
            navigateAfterLoggedIn();
        }
    }

    private void login() {
        loginPB.setVisibility(View.VISIBLE);
        String email = emailET.getEditText().getText().toString();
        String password = passwordET.getEditText().getText().toString();

        if (verifyLoginCredentials(email, password)) {
            Model.instance.login(email, password, new GenericEventListenerWithNoParam() {
                @Override
                public void onComplete() {
                    navigateAfterLoggedIn();
                    hideKeyboard(getActivity());
                    loginPB.setVisibility(View.INVISIBLE);
                }
            }, new GenericEventListenerWithNoParam() {
                @Override
                public void onComplete() {
                    Toast.makeText(getActivity(), "Email/Password are incorrect", Toast.LENGTH_LONG).show();
                    loginPB.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            loginPB.setVisibility(View.INVISIBLE);
        }
    }

    private void navigateAfterLoggedIn() {
        Navigation.findNavController(view).navigate(LoginFragmentDirections.actionLoginFragmentToRestaurantListFragment());
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