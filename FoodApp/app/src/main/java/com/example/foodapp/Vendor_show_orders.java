package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.foodapp.databinding.ActivityVendorShowOrdersBinding;
import com.example.foodapp.databinding.VendorShowOrdersBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Vendor_show_orders extends AppCompatActivity {

    private ActivityVendorShowOrdersBinding binding;
    private Myviewmodel viewmodel;
    private Orders_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_vendor_show_orders);
        viewmodel = ViewModelProviders.of(this).get(Myviewmodel.class);
        adapter = new Orders_adapter();
        binding.showOrdersRecycler.setAdapter(adapter);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });
        loadItems();
    }

    void loadItems()
    {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewmodel.get_orders().observe(this, new Observer<List<Vendor_orders>>() {
            @Override
            public void onChanged(List<Vendor_orders> vendor_orders) {
                binding.swipeRefreshLayout.setRefreshing(false);
                adapter.setOrders(vendor_orders);
                if(vendor_orders==null)
                {
                    Snackbar.make(binding.showOrdersRecycler,"Error while fetching data",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

}

class Myviewmodel extends ViewModel
{
    private MutableLiveData<List<Vendor_orders>> orders;
    public LiveData<List<Vendor_orders>> get_orders()
    {
        DataLoader dataLoader = DataLoader.getInstance();
        dataLoader.get_orders("", new Callback<List<Vendor_orders>>() {
            @Override
            public void onResponse(Call<List<Vendor_orders>> call, Response<List<Vendor_orders>> response) {
                orders.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Vendor_orders>> call, Throwable t) {
                orders.postValue(null);
            }
        });

        return orders;
    }

}

class Orders_adapter extends RecyclerView.Adapter<Orders_adapter.order_view_holder>
{

    private List<Vendor_orders>  orders;

    public void setOrders(List<Vendor_orders> orders) {
        this.orders = orders;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public order_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        VendorShowOrdersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.vendor_show_orders,parent,false);
        return new order_view_holder(binding) ;
    }

    @Override
    public void onBindViewHolder(@NonNull order_view_holder holder, int position) {
        Vendor_orders order = orders.get(position);
        holder.binding.setItem(order);
        
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class order_view_holder extends RecyclerView.ViewHolder
    {
        public VendorShowOrdersBinding binding;
        public order_view_holder(@NonNull VendorShowOrdersBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
