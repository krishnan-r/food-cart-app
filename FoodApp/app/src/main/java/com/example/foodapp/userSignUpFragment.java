package com.example.foodapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
        // Inflate the layout for this fragment
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
               EditText employee_id = (EditText) view.findViewById(R.id.eid_ID);
               EditText password = (EditText) view.findViewById(R.id.pwdID);
               EditText confirmPassword = (EditText) view.findViewById(R.id.cnfrmpwdID);
               EditText email = (EditText) view.findViewById(R.id.emailID);
               EditText phone = (EditText) view.findViewById(R.id.phNoID);

               boolean restart = false;
               String nameString = name.getText().toString();
               String eIDString = employee_id.getText().toString();
               String passwordString = password.getText().toString();
               String cnfrmPwdString = confirmPassword.getText().toString();
               String emailString = email.getText().toString();
               String phoneString = phone.getText().toString();

               if (nameString.equals("") || eIDString.equals("") || passwordString.equals("") || cnfrmPwdString.equals("") || emailString.equals("")
                       || phoneString.equals("")) {
                   Snackbar.make(view.findViewById(R.id.signup_ID), "All fields are required", Snackbar.LENGTH_SHORT);
                   restart = true;
               }

               if (!passwordString.equals(cnfrmPwdString)) {
                   Snackbar.make(view.findViewById(R.id.cnfrmpwdID), "Password and confirm password must match", Snackbar.LENGTH_SHORT);
                   restart = true;
               }
               if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                   Snackbar.make(view.findViewById(R.id.emailID), "Invalid email address", Snackbar.LENGTH_SHORT);
                   restart = true;
               }
               if (!android.util.Patterns.PHONE.matcher(phoneString).matches()) {
                   Snackbar.make(view.findViewById(R.id.phNoID), "Invalid mobile number", Snackbar.LENGTH_SHORT);
                   restart = true;
               }


               if (!view.findViewById(R.id.checkBoxID).isEnabled()) {
                   Snackbar.make(view.findViewById(R.id.checkBoxID), "Accept the terms and conditions to proceed", Snackbar.LENGTH_SHORT);
                   restart = true;
               }

               if (restart) {
                   restartFragment();
               }

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
//                   if(response.body().successful)
                       Snackbar.make(view.findViewById(R.id.signup_ID), response.body().message, Snackbar.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onFailure(Call<SignUpObject> call, Throwable t) {
                       Snackbar.make(view.findViewById(R.id.signup_ID), t.getMessage(), Snackbar.LENGTH_SHORT).show();
                   }
               });

       }

       public void restartFragment(){
        userSignUpFragment fragment = (userSignUpFragment)
                   getFragmentManager().findFragmentById(R.id.userSignUpFragment);

           getFragmentManager().beginTransaction()
                   .detach(fragment)
                   .attach(fragment)
                   .commit();
       }

}
