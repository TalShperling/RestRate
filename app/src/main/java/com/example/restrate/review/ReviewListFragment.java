package com.example.restrate.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.example.restrate.model.GenericEventListenerWithNoParam;
import com.example.restrate.model.Model;
import com.example.restrate.model.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.restrate.Utils.costMeterTextConverter;

public class ReviewListFragment extends Fragment {
    ReviewListViewModel viewModel;
    List<Review> reviewList;

    TextView emptyList;
    FloatingActionButton addReviewBtn;
    RecyclerView reviewListRV;
    ProgressBar reviewListPB;
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
        reviewListPB = view.findViewById(R.id.review_list_pb);

        reviewListRV.setHasFixedSize(true);
        reviewListPB.setVisibility(View.VISIBLE);

        addReviewBtn.setEnabled(false);

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("restaurantId", viewModel.getRestaurantId());
                Navigation.findNavController(view).navigate(R.id.addReviewFragment, bundle);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        reviewListRV.setLayoutManager(layoutManager);

        adapter = new MyAdapter();
        reviewListRV.setAdapter(adapter);

        adapter.setOnEditClickListener(new OnClickListener() {
            @Override
            public void onClick(int position) {
                String reviewId = reviewList.get(position).getReviewId();
                String restaurantId = reviewList.get(position).getRestaurantId();

                Bundle bundle = new Bundle();
                bundle.putString("restaurantId", restaurantId);
                bundle.putString("reviewId", reviewId);
                Navigation.findNavController(view).navigate(R.id.editReviewFragment, bundle);
            }
        });

        adapter.setOnDeleteClickListener(new OnClickListener() {
            @Override
            public void onClick(int position) {
                this.deleteReview(reviewList.get(position));
            }

            private void deleteReview(Review review) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Review");
                builder.setMessage("Are you sure you want to delete this review?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reviewListPB.setVisibility(View.VISIBLE);
                        Model.instance.deleteReview(review, new GenericEventListenerWithNoParam() {
                            @Override
                            public void onComplete() {
                                dialogInterface.dismiss();
                                reviewListPB.setVisibility(View.INVISIBLE);
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
        });

        viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null) {
                addReviewBtn.setEnabled(true);
                reviewList = reviews;
                boolean showAdd = true;
                for (Review review : reviews) {
                    if (review.getUserId().equals(Model.instance.getCurrentUser().getUid())) {
                        showAdd = false;
                    }
                }
                viewModel.setIsAddShown(showAdd && viewModel.getUserId() == null);
                emptyList.setVisibility(View.INVISIBLE);
                if (reviews.size() == 0) {
                    emptyList.setVisibility(View.VISIBLE);
                }
                reviewListPB.setVisibility(View.INVISIBLE);
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

    interface OnClickListener {
        void onClick(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName;
        TextView reviewDescription;
        RatingBar reviewRating;
        TextView reviewCostMeter;
        ImageButton editReviewBtn;
        ImageButton deleteReviewBtn;
        int position;

        public MyViewHolder(@NonNull View itemView, final OnClickListener editClickListener, final OnClickListener deleteClickListener) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.review_listrow_username);
            reviewDescription = itemView.findViewById(R.id.review_listrow_desc);
            reviewRating = itemView.findViewById(R.id.review_listrow_rating);
            reviewCostMeter = itemView.findViewById(R.id.review_listrow_cost_meter);
            editReviewBtn = itemView.findViewById(R.id.review_listrow_edit_btn);
            deleteReviewBtn = itemView.findViewById(R.id.review_listrow_delete_btn);

            editReviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            editClickListener.onClick(position);
                        }
                    }
                }
            });

            deleteReviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (deleteClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteClickListener.onClick(position);
                        }
                    }
                }
            });
        }

        private void bindData(Review review, int position) {
            this.position = position;
            reviewerName.setText(review.getUserDisplayName());
            reviewDescription.setText(review.getDescription());
            reviewRating.setRating(Float.parseFloat(review.getRate()));
            reviewCostMeter.setText(costMeterTextConverter(review.getCostMeter()));
            deleteReviewBtn.setVisibility(review.getUserId().equals(Model.instance.getCurrentUser().getUid()) ? View.VISIBLE : View.INVISIBLE);
            editReviewBtn.setVisibility(review.getUserId().equals(Model.instance.getCurrentUser().getUid()) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private OnClickListener editClickListener;
        private OnClickListener deleteClickListener;

        void setOnEditClickListener(OnClickListener listener) {
            this.editClickListener = listener;
        }

        void setOnDeleteClickListener(OnClickListener listener) {
            this.deleteClickListener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.review_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, editClickListener, deleteClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Review review = reviewList.get(position);
            holder.bindData(review, position);
        }

        @Override
        public int getItemCount() {
            if (reviewList != null) {
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