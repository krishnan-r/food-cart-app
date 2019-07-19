package com.example.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodapp.databinding.ActivityMainBinding;
import com.example.foodapp.databinding.ItemViewBinding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ItemListAdaptor itemListAdaptor;
    static int integerCode = 10;
    private Retrofit retrofit;
    private AppService service;
    static String EXTRA_MESSAGE ="com.example.foodApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        itemListAdaptor = new ItemListAdaptor();
        binding.itemList.setAdapter(itemListAdaptor);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("App","Refreshing List");
                loadData();
            }
        });

        loadData();
    }

    private void loadData(){
        Log.e("App", "Loading data from server.");
        binding.swipeRefreshLayout.setRefreshing(true);
         retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:86/api/").addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(AppService.class);
        Call<List<Item>> request = service.getItems();
        final Activity that = this;
        request.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.e("App", "Response Received");
                Log.e("Request Response", response.message());
                Toast.makeText(MainActivity.this,"Response received",Toast.LENGTH_LONG).show();
                List<Item> items = response.body();
                itemListAdaptor.setItems(items);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("App", "Request Failed: " + t.toString());
                itemListAdaptor.clear();
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(that, "Unable to reach server", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            Intent intent = new Intent(this,LoginHandlerActivity.class);
            startActivityForResult(intent,integerCode);
            Toast.makeText(MainActivity.this, "Welcome to Login Portal", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (10) : {
                if (resultCode == Activity.RESULT_OK) {
                    String jsonString = data.getStringExtra(EXTRA_MESSAGE);
                   JsonParser parser = new JsonParser();
                    JsonObject json = (JsonObject) parser.parse(jsonString);

                    Call<LoginObject> request = service.verifyUser(json);
                    request.enqueue(new Callback<LoginObject>() {
                        @Override
                        public void onResponse(Call<LoginObject> call, Response<LoginObject> response) {
                            Log.e("Request Response", response.message());
                            LoginObject result = response.body();
                            Toast.makeText(MainActivity.this,result.message,Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onFailure(Call<LoginObject> call, Throwable t) {
                            Log.e("App", "Request Failed: " + t.toString());
                            itemListAdaptor.clear();
                            binding.swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            }
        }
    }
}

interface AppService {
    @GET("list")
    Call<List<Item>> getItems();

    @POST("login")
    Call<LoginObject> verifyUser(@Body JsonObject json);

}


class ItemListAdaptor extends RecyclerView.Adapter<ItemListAdaptor.ItemViewHolder> {

    private List<Item> items;

    public void setItems(List<Item> items){
        this.items = items;
        this.notifyDataSetChanged();
    }

    public void clear(){
        items=null;
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
        if(items==null)
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

