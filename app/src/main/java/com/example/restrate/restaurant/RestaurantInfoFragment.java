package com.example.restrate.restaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.GenericEventListenerWithParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.example.restrate.review.ReviewListFragment;
import com.squareup.picasso.Picasso;

public class RestaurantInfoFragment extends Fragment {
    View view;
    TextView restaurantName;
    TextView restaurantDescription;
    TextView restaurantAddress;
    TextView restaurantPhone;
    TextView restaurantSiteLink;
    ImageView restaurantImage;
    Restaurant restaurant = new Restaurant();
    ProgressBar pb;
    Button backBtn;
    Button editBtn;
    Button deleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        restaurantName = view.findViewById(R.id.restinfo_name);
        restaurantDescription = view.findViewById(R.id.restinfo_description);
        restaurantAddress = view.findViewById(R.id.restinfo_address);
        restaurantPhone = view.findViewById(R.id.restinfo_phone);
        restaurantSiteLink = view.findViewById(R.id.restinfo_siteLink);
        restaurantImage = view.findViewById(R.id.restinfo_image);
        backBtn = view.findViewById(R.id.restinfo_back);
        editBtn = view.findViewById(R.id.restinfo_edit);
        deleteBtn = view.findViewById(R.id.restinfo_delete);

        pb = view.findViewById(R.id.restinfo_pb);
        pb.setVisibility(View.VISIBLE);

        Fragment reviewsFregment = new ReviewListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.restinfo_reviews_container, reviewsFregment).commit();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.returnBack(view);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantInfoFragmentDirections.ActionRestaurantInfoFragmentToEditRestaurantFragment direction =
                        RestaurantInfoFragmentDirections.actionRestaurantInfoFragmentToEditRestaurantFragment(restaurant.getId());
                Navigation.findNavController(view).navigate(direction);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRestaurant();
            }
        });

        final String restaurantId = RestaurantInfoFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        Model.instance.getRestaurantById(restaurantId, new GenericEventListenerWithParam<Restaurant>() {
            @Override
            public void onComplete(Restaurant data) {
                pb.setVisibility(View.INVISIBLE);
                if (data != null) {
                    restaurant = data;
                    bindData(restaurant);
                } else {
                    cancelLoad();
                }
            }
        });

        return view;
    }

    private void cancelLoad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Operation failed");
        builder.setMessage("Could not retrieve the data from the server, please try again later");

        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Utils.returnBack(view);
            }
        });

        builder.show();
    }

    private void deleteRestaurant() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete restaurant");
        builder.setMessage("Are you sure you want to delete this restaurant?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Model.instance.deleteRestaurant(restaurant, new GenericEventListenerWithNoParam() {
                    @Override
                    public void onComplete() {
                        dialogInterface.dismiss();
                        Utils.returnBack(view);
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void bindData(Restaurant restaurant) {
        restaurantName.setText(restaurant.getName());
        restaurantDescription.setText(restaurant.getDescription());
        restaurantAddress.setText(restaurant.getAddress());
        restaurantPhone.setText(restaurant.getPhoneNumber());
        restaurantSiteLink.setText(restaurant.getSiteLink());
        loadImage();
    }

    private void loadImage() {
        if (restaurant.getImageURL() != null) {
            Picasso.get().load(restaurant.getImageURL()).placeholder(R.drawable.restaurant).into(restaurantImage);
        }
    }
}