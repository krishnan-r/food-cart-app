package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Vendor_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_main);


    }

    public void add_menu(View view)
    {
        Intent intent = new Intent(this,Vendor_add_menu.class);
        startActivity(intent);

    }

    public void show_orders(View view) {
        Intent intent = new Intent(this,Vendor_show_orders.class);
        startActivity(intent);

    }

    public void delete_menu(View view) {
        Intent intent = new Intent(this,Vendor_delete_menu.class);
        startActivity(intent);

    }

    public void change_password(View view) {
        Intent intent = new Intent(this,Vendor_change_password.class);
        startActivity(intent);

    }

    public void update_delivery(View view) {
        Intent intent = new Intent(this,Vendor_update_delivery.class);
        startActivity(intent);

    }
}
