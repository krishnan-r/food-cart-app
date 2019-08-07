package com.example.foodapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class userSignUpFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);
        Button signUp = (Button) view.findViewById(R.id.signup_ID);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(view);
            }
        });


        return view;
    }

       private void registerUser(final View view) {
           //TODO: handle no inputs.. write unitTests
               EditText name = (EditText) view.findViewById(R.id.nameID);
               final EditText employee_id = (EditText) view.findViewById(R.id.eid_ID);
               EditText password = (EditText) view.findViewById(R.id.pwdID);
               EditText confirmPassword = (EditText) view.findViewById(R.id.cnfrmpwdID);
               EditText email = (EditText) view.findViewById(R.id.emailID);
               EditText phone = (EditText) view.findViewById(R.id.phNoID);

               boolean error = false;


               if(TextUtils.isEmpty(name.getText())){
                   name.setError("Name is a required field");
                   error = true;
               }

               if(TextUtils.isEmpty(employee_id.getText())){
                   employee_id.setError("Employee ID is required");
                   error = true;
               }

               if(TextUtils.isEmpty(password.getText())){
                   password.setError("Password is required");
                   error = true;
               }

               if(TextUtils.isEmpty(confirmPassword.getText())){
                   confirmPassword.setError("Confirm Password is required");
                   error = true;
               }

             if (!error && !password.getText().toString().equals(confirmPassword.getText().toString()) ) {
                   confirmPassword.setError("Password and confirm password must match");
                   error= true;
             }
               if(TextUtils.isEmpty(email.getText())){
                   email.setError("Email is required");
                   error = true;
               }

               else  if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                   email.setError("Invalid email address");
                   error = true;
               }

               if(TextUtils.isEmpty(phone.getText())){
                   phone.setError("Phone is required");
                   error = true;
               }
                else  if (!android.util.Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
                    phone.setError("Invalid mobile number");
                    error = true;
                 }

               if (!view.findViewById(R.id.checkBoxID).isEnabled()) {
                   Snackbar.make(view.findViewById(R.id.checkBoxID), "Accept the terms and conditions to proceed", Snackbar.LENGTH_SHORT);
                   error = true;
               }



        if(!error) {

            String nameString = name.getText().toString();
            String eIDString = employee_id.getText().toString();
            String passwordString = password.getText().toString();
            String emailString = email.getText().toString();
            String phoneString = phone.getText().toString();



            JsonObject signupInfo = new JsonObject();
            signupInfo.addProperty("employee_id", eIDString);
            signupInfo.addProperty("name", nameString);
            signupInfo.addProperty("password", passwordString);
            signupInfo.addProperty("mobile", phoneString);
            signupInfo.addProperty("email", emailString);

            DataLoader dataLoader = DataLoader.getInstance();
            dataLoader.registerUser(signupInfo, new Callback<SignUpObject>() {
                @Override
                public void onResponse(Call<SignUpObject> call, Response<SignUpObject> response) {
                    Snackbar.make(view.findViewById(R.id.signup_ID), response.body().message, Snackbar.LENGTH_SHORT).show();
                    if(response.body().successful){
                        final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment2);
                        navController.popBackStack();
                    }
                }

                @Override
                public void onFailure(Call<SignUpObject> call, Throwable t) {
                    Snackbar.make(view.findViewById(R.id.signup_ID), t.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
       }
}
