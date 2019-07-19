package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.google.gson.JsonObject;

public class LoginHandlerActivity extends AppCompatActivity {
    static String EXTRA_MESSAGE="com.example.foodApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_handler);
    }
    public void sendMessage(View view){
        EditText username = (EditText) findViewById(R.id.usernameID);
        EditText password = (EditText) findViewById(R.id.passwordID);

        JsonObject json = new JsonObject();
        json.addProperty("username",username.getText().toString());
        json.addProperty("password",password.getText().toString());


        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE,json.toString());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
