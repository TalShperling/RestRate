package com.example.restrate;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.restrate.model.Model;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {
    View view;
    ImageView userImage;
    TextView userName;
    ProgressBar pb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        userImage = view.findViewById(R.id.user_profile_image);
        userName = view.findViewById(R.id.user_profile_name);
        pb = view.findViewById(R.id.user_profile_pb);

        pb.setVisibility(View.VISIBLE);

        bindData();

        return view;
    }

    private void bindData() {
        // TODO: add the reviews connection
        // Model.instance.getCurrentUserReviews();

        userName.setText(Model.instance.getCurrentUser().getDisplayName());
        Uri imageURL = Model.instance.getCurrentUser().getPhotoUrl();
        loadImage(imageURL.toString());
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.my_profile_menu, menu);

        MenuItem editItem = menu.findItem(R.id.myprofile_edit);

        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Navigation.findNavController(view).navigate(R.id.editUserProfileFragment);
                return true;
            }
        });
    }

    private void loadImage(String photoURL) {
        if (photoURL != null) {
            Picasso.get().load(photoURL).placeholder(R.drawable.restaurant).into(userImage);
        }
    }
}