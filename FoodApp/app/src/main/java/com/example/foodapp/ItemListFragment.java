package com.example.foodapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodapp.databinding.ItemListFragmentBinding;
import com.example.foodapp.databinding.ItemViewBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemListFragment extends Fragment {

    private ItemListViewModel viewModel;
    private ItemListFragmentBinding binding;
    private ItemListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ItemListViewModel.class);
        adapter = new ItemListAdapter();
        binding.itemListRecyclerView.setAdapter(adapter);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });
        loadItems();
    }

    void loadItems() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                binding.swipeRefreshLayout.setRefreshing(false);
                adapter.setItems(items);
                if (items == null)
                    Snackbar.make(binding.itemListRecyclerView, "Error Fetching Content", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}

class ItemListViewModel extends ViewModel {

    MutableLiveData<List<Item>> items = new MutableLiveData<>();

    public LiveData<List<Item>> getItems() {
        DataLoader dataLoader = DataLoader.getInstance();
        dataLoader.getItems("", new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                items.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                items.postValue(null);
            }
        });
        return items;
    }
}

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    private List<Item> items;

    public void setItems(List<Item> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public void clear() {
        items = null;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_view, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        else return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.binding.setItem(items.get(position));

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewBinding binding;

        public ItemViewHolder(ItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}