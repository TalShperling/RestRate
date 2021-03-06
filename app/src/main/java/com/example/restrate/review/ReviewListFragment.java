package com.example.restrate.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReviewListFragment extends Fragment {
    ReviewListViewModel viewModel;
    List<Review> reviewList;

    TextView emptyList;
    FloatingActionButton addReviewBtn;
    RecyclerView reviewListRV;
    MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getParentFragment()).get(ReviewListViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);
        addReviewBtn = view.findViewById(R.id.review_list_add_btn);
        reviewListRV = view.findViewById(R.id.review_list_recycler_view);
        emptyList = view.findViewById(R.id.review_list_empty);

        reviewListRV.setHasFixedSize(true);

        // TODO: create add review fragment
        addReviewBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_restaurantListFragment_to_addRestaurantFragment));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        reviewListRV.setLayoutManager(layoutManager);

        adapter = new MyAdapter();
        reviewListRV.setAdapter(adapter);

        viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null){
                reviewList = reviews;
                emptyList.setVisibility(View.INVISIBLE);
                if(reviews.size() == 0) {
                    emptyList.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.isAddShown().observe(getViewLifecycleOwner(), isShown -> {
            addReviewBtn.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName;
        TextView reviewDescription;
        RatingBar reviewRating;
        TextView reviewCostMeter;
        int position;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.review_listrow_username);
            reviewDescription = itemView.findViewById(R.id.review_listrow_desc);
            reviewRating = itemView.findViewById(R.id.review_listrow_rating);
            reviewCostMeter = itemView.findViewById(R.id.review_listrow_cost_meter);
        }

        private String costMeterTextConverter(String costMeter) {
            String costMeterStringified = "";

            for (int i = 0; i < Integer.parseInt(costMeter); i++) {
                costMeterStringified = costMeterStringified.concat("$");
            }

            return costMeterStringified;
        }

        private void bindData(Review review, int position) {
            this.position = position;
            reviewerName.setText(review.getUserDisplayName());
            reviewDescription.setText(review.getDescription());
            reviewRating.setRating(Float.parseFloat(review.getRate()));
            reviewCostMeter.setText(costMeterTextConverter(review.getCostMeter()));
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private OnItemClickListener listener;

        void setOnClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.review_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Review review = reviewList.get(position);
            holder.bindData(review, position);
        }

        @Override
        public int getItemCount() {
            if(reviewList != null) {
                return reviewList.size();
            } else {
                if (viewModel.getReviews().getValue() == null) {
                    return 0;
                }
            }
            return viewModel.getReviews().getValue().size();
        }
    }
}