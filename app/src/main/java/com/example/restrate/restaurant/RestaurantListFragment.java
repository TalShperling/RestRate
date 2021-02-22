package com.example.restrate.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restrate.R;
import com.example.restrate.Utils;
import com.example.restrate.model.Model;
import com.example.restrate.model.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListFragment extends Fragment {
    RestaurantListViewModel viewModel;
    List<Restaurant> FullRestaurantList;
    List<Restaurant> filteredRestaurantList;

    ProgressBar pb;
    TextView emptyList;
    FloatingActionButton addRestaurantBtn;
    RecyclerView restList;
    MyAdapter adapter;
    SwipeRefreshLayout sref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RestaurantListViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        pb = view.findViewById(R.id.restlist_progress_bar);
        addRestaurantBtn = view.findViewById(R.id.restlist_add_btn);
        restList = view.findViewById(R.id.restlist_recycler_view);
        sref = view.findViewById(R.id.restlist_swipe);
        emptyList = view.findViewById(R.id.restlist_no_restaurants);

        pb.setVisibility(View.INVISIBLE);
        restList.setHasFixedSize(true);
        sref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sref.setRefreshing(true);
                reloadData();
            }
        });

        addRestaurantBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_restaurantListFragment_to_addRestaurantFragment));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        restList.setLayoutManager(layoutManager);

        adapter = new MyAdapter();
        restList.setAdapter(adapter);

        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = filteredRestaurantList.get(position).getId();
                RestaurantListFragmentDirections.ActionRestaurantListFragmentToRestaurantInfoFragment direction = RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantInfoFragment(id);
                Navigation.findNavController(view).navigate(direction);
            }
        });



        viewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                FullRestaurantList = new ArrayList<>(restaurants);
                filteredRestaurantList = new ArrayList<>(restaurants);

                emptyList.setVisibility(View.INVISIBLE);
                if(restaurants.size() == 0) {
                    reloadData();
                    emptyList.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.restlist_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.restlist_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void reloadData() {
        pb.setVisibility(View.VISIBLE);
        addRestaurantBtn.setEnabled(false);
        Model.instance.refreshAllRestaurants(() -> {
            pb.setVisibility(View.INVISIBLE);
            addRestaurantBtn.setEnabled(true);
            sref.setRefreshing(false);
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView restName;
        TextView restAddress;
        RatingBar restRating;
        TextView restCostMeter;
        int position;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            restName = itemView.findViewById(R.id.restlistrow_name);
            restAddress = itemView.findViewById(R.id.restlistrow_address);
            restRating = itemView.findViewById(R.id.restlistrow_rating);
            restCostMeter = itemView.findViewById(R.id.restlistrow_cost_meter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        private String costMeterTextConverter(String costMeter) {
            String costMeterStringified = "";

            for (int i = 0; i < Integer.parseInt(costMeter); i++) {
                costMeterStringified = costMeterStringified.concat("$");
            }

            return costMeterStringified;
        }

        private void bindData(Restaurant restaurant, int position) {
            this.position = position;
            restName.setText(restaurant.getName());
            restAddress.setText(restaurant.getAddress());
            restRating.setRating(Float.parseFloat(restaurant.getRate()));
            restCostMeter.setText(costMeterTextConverter(restaurant.getCostMeter()));
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {
        private OnItemClickListener listener;

        void setOnClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.restaurant_list_row, null);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Restaurant restaurant = filteredRestaurantList.get(position);
            holder.bindData(restaurant, position);
        }

        @Override
        public int getItemCount() {
            if(filteredRestaurantList != null) {
                return filteredRestaurantList.size();
            } else {
                if (viewModel.getRestaurants().getValue() == null) {
                    return 0;
                }
            }
            return viewModel.getRestaurants().getValue().size();
        }


        @Override
        public Filter getFilter() {
            return listFilter;
        }

        private Filter listFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Restaurant> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.clear();
                    filteredList.addAll(FullRestaurantList);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();

                    for (Restaurant rest : FullRestaurantList) {
                        if (rest.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(rest);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredRestaurantList.clear();
                filteredRestaurantList.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }
}