package com.example.restrate.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;

public class LogoutFragment extends Fragment {
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        pb = view.findViewById(R.id.logout_pb);
        pb.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pb.setVisibility(View.VISIBLE);
        Model.instance.logout(new GenericEventListenerWithNoParam() {
            @Override
            public void onComplete() {
                pb.setVisibility(View.INVISIBLE);
                Navigation.findNavController(view).navigate(LogoutFragmentDirections.actionLogoutFragmentToLoginFragment());
            }
        });
    }
}