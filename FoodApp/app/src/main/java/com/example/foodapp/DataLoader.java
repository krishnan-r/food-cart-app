package com.example.foodapp;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class DataLoader {

    interface AppService {
        @GET("items")
        Call<List<Item>> getItems();

        @GET("vendors")
        Call<List<Vendor>> getVendors();

        @GET("cart")
        Call<List<CartItem>> getCartItems();

        @POST("login")
        Call<LoginObject> verifyUser(@Body JsonObject json);
    }

    private Retrofit retrofit;
    private AppService service;
    private final String baseUrl = "http://10.0.2.2:86/api/";

    private DataLoader() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(AppService.class);
    }

    private static DataLoader instance = new DataLoader();
    public static DataLoader getInstance(){
        return instance;
    }

    void getItems(String vendor_id, Callback<List<Item>> callback) {
        Call<List<Item>> request = service.getItems();
        request.enqueue(callback);
    }

    void getVendors(Callback<List<Vendor>> callback){
        Call<List<Vendor>> request = service.getVendors();
        request.enqueue(callback);
    }

    void getCartItems(Callback<List<CartItem>> callback){
        Call<List<CartItem>> request = service.getCartItems();
        request.enqueue(callback);
    }
}

