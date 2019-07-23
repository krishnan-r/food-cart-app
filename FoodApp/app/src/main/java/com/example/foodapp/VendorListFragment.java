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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodapp.databinding.VendorListFragmentBinding;
import com.example.foodapp.databinding.VendorViewBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorListFragment extends Fragment {

    private VendorListViewModel viewModel;
    private VendorListFragmentBinding binding;
    private VendorListAdapter adapter;

    public static VendorListFragment newInstance() {
        return new VendorListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.vendor_list_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(VendorListViewModel.class);
        // TODO: Use the ViewModel
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadVendors();
            }
        });
        adapter = new VendorListAdapter();
        final Fragment that = this;
        adapter.setOnItemClickListener(new VendorListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {
                Bundle args = new Bundle();
                args.putString("vendor_id", "asdf");
                NavHostFragment.findNavController(that).navigate(R.id.action_vendorListFragment_to_itemListFragment, args);
            }
        });
        binding.vendorListRecyclerView.setAdapter(adapter);
        loadVendors();
    }

    void loadVendors() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getVendors().observe(this, new Observer<List<Vendor>>() {
            @Override
            public void onChanged(@Nullable final List<Vendor> vendors) {
                binding.swipeRefreshLayout.setRefreshing(false);
                adapter.setVendors(vendors);
                if (vendors == null)
                    Snackbar.make(binding.swipeRefreshLayout, "Error Fetching Content", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}

class VendorListViewModel extends ViewModel {
    private MutableLiveData<List<Vendor>> vendors = new MutableLiveData<>();

    public LiveData<List<Vendor>> getVendors() {
        DataLoader data = DataLoader.getInstance();
        data.getVendors(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                vendors.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                vendors.postValue(null);
            }
        });
        return vendors;
    }
}

class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorViewHolder> {

    private List<Vendor> vendors;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    public void setVendors(List<Vendor> items) {
        this.vendors = items;
        this.notifyDataSetChanged();
    }

    public void clear() {
        vendors = null;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VendorViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.vendor_view, parent, false);
        return new VendorViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        if (vendors == null) return 0;
        else return vendors.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, final int position) {
        holder.binding.setItem(vendors.get(position));
        holder.binding.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(vendors.get(position).id);
            }
        });
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {
        public VendorViewBinding binding;

        public VendorViewHolder(VendorViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}