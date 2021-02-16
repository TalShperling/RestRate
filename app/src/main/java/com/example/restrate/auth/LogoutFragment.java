package com.example.restrate.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Model.instance.logout(new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigate(R.id.action_logoutFragment_to_loginFragment);
            }
        });
    }
}