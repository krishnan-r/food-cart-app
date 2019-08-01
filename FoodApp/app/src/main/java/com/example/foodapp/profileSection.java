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

public class profileSection extends Fragment {

//    private profileSectionViewModel viewModel;
    private VendorListFragmentBinding binding;
    private VendorListAdapter adapter;
//    FragmentProfileSectionBinding binding;

    public static VendorListFragment newInstance() {
        return new VendorListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_section, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}

//class profileSectionViewModel extends ViewModel {
//    private MutableLiveData<List<Vendor>> vendors = new MutableLiveData<>();
//
//    public LiveData<List<Vendor>> getVendors() {
//        DataLoader data = DataLoader.getInstance();
//        data.getVendors(new Callback<List<Vendor>>() {
//            @Override
//            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
//                vendors.postValue(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<Vendor>> call, Throwable t) {
//                vendors.postValue(null);
//            }
//        });
//        return vendors;
//    }
//}
//
//class profileSectionAdapter extends RecyclerView.Adapter<profileSectionAdapter.profileSectionViewHolder> {
//
//    private List<Vendor> vendors;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    private OnItemClickListener onItemClickListener;
//
//
//    public interface OnItemClickListener {
//        void onItemClick(String id);
//    }
//
//    public void setVendors(List<Vendor> items) {
//        this.vendors = items;
//        this.notifyDataSetChanged();
//    }
//
//    public void clear() {
//        vendors = null;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public profileSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        VendorViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.vendor_view, parent, false);
//        return new profileSectionViewHolder(binding);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (vendors == null) return 0;
//        else return vendors.size();
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull profileSectionViewHolder holder, final int position) {
//        holder.binding.setItem(vendors.get(position));
//        holder.binding.itemCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onItemClickListener.onItemClick(vendors.get(position).id);
//            }
//        });
//    }

//    public class profileSectionViewHolder extends RecyclerView.ViewHolder {
//        public VendorViewBinding binding;
//
//        public profileSectionViewHolder(VendorViewBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//}