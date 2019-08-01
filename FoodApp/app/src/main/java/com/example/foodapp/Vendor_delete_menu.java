package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodapp.databinding.VendorDeleteMenuBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vendor_delete_menu extends AppCompatActivity {
    private  myviewmodel viewmodel;
    private  delete_adaptor adaptor;
    private  VendorDeleteMenuBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_vendor_delete_menu);
        viewmodel = ViewModelProviders.of(this).get(myviewmodel.class);
        adaptor = new delete_adaptor();

        RecyclerView recyclerView = findViewById(R.id.delete_menu_recycler);
        recyclerView.setAdapter(adaptor);
        loaditems();

    }
    void loaditems()
    {
        viewmodel.get_menu().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adaptor.setMenu(items);
            }
        });
    }
}

class myviewmodel extends ViewModel
{
    private MutableLiveData<List<Item>> menu;
    public LiveData<List<Item>> get_menu()
    {
        DataLoader dataLoader = DataLoader.getInstance();
        dataLoader.get_menu("", new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                menu.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                menu.postValue(null);
            }
        });
        return menu;
    }
}

class delete_adaptor extends RecyclerView.Adapter<delete_adaptor.delete_view_holder>
{
    private List<Item> menu;

    public void setMenu(List<Item> menu) {
        this.menu = menu;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public delete_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VendorDeleteMenuBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.vendor_delete_menu,parent,false);
        return new delete_view_holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull delete_view_holder holder, int position) {
        Item item = menu.get(position);
        holder.binding.setItem(item);

    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class delete_view_holder extends RecyclerView.ViewHolder
    {

        VendorDeleteMenuBinding binding;
        public delete_view_holder(@NonNull VendorDeleteMenuBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }


    }
}

