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

import com.example.foodapp.databinding.CartFragmentBinding;
import com.example.foodapp.databinding.CartItemViewBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private CartModel viewModel;
    private CartFragmentBinding binding;
    private CartListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.cart_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CartModel.class);
        adapter = new CartListAdapter();
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
        viewModel.getItems().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> items) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (items != null) adapter.setItems(items);
            }
        });
    }
}

class CartModel extends ViewModel {

    MutableLiveData<List<CartItem>> items = new MutableLiveData<>();

    public LiveData<List<CartItem>> getItems() {
        DataLoader dataLoader = DataLoader.getInstance();
        dataLoader.getCartItems(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                items.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                items.postValue(null);
            }
        });
        return items;
    }
}

class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ItemViewHolder> {

    private List<CartItem> items;

    public void setItems(List<CartItem> items) {
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
        CartItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_item_view, parent, false);
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
        public CartItemViewBinding binding;

        public ItemViewHolder(CartItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}