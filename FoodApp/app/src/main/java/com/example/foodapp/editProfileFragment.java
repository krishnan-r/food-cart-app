package com.example.foodapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class editProfileFragment extends Fragment {
    private LoginViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        final View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
       final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment2);
        viewModel.authenticationState.observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:
                                Button changePwd = (Button) view.findViewById(R.id.changePasswordId);
                                changePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       changeUserPassword(viewModel.username,view);
                                    }
                                });

                                break;
                            case UNAUTHENTICATED:
                                Snackbar.make(view,R.string.requiredSignIn,Snackbar.LENGTH_LONG).show();
                                navController.navigate(R.id.userLoginHandler);
                                break;
                        }
                    }
                });
        return view;
    }

    private void changeUserPassword(String username,final View view){
        boolean error = false;
        LoginViewModel loginViewModel = new LoginViewModel();
        EditText oldpassword = (EditText)view.findViewById(R.id.oldpasswordID);
        EditText newpassword = (EditText)view.findViewById(R.id.newpasswordID);
        EditText newpassword2 = (EditText)view.findViewById(R.id.reEnterNewPasswordId);

        if(TextUtils.isEmpty(oldpassword.getText())){
            oldpassword.setError("Password cannot be empty");
            error = true;
        }

        if(TextUtils.isEmpty(newpassword.getText())){
            newpassword.setError("New password cannot be empty");
            error = true;
        }

        if(TextUtils.isEmpty(newpassword2.getText())){
            newpassword2.setError("Confirm password cannot be empty");
            error = true;
        }

        if (!newpassword.getText().toString().equals(newpassword2.getText().toString())) {
            newpassword2.setError("new password and confirm password must match");
              error = true;
        }

        if(!error) {
            JsonObject pwdInfo = new JsonObject();
            pwdInfo.addProperty("username", username);
            pwdInfo.addProperty("password", oldpassword.getText().toString());
            pwdInfo.addProperty("newPassword", newpassword.getText().toString());
            pwdInfo.addProperty("editField", "password");
//            Snackbar.make(view,pwdInfo.toString(),Snackbar.LENGTH_LONG).show();
            DataLoader dataLoader = DataLoader.getInstance();
            dataLoader.changePassword(pwdInfo, new Callback<editProfileObject>() {
                @Override
                public void onResponse(Call<editProfileObject> call, Response<editProfileObject> response) {
                    Snackbar.make(view, response.body().message, Snackbar.LENGTH_SHORT).show();
                    if (response.body().edit_success) {
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment2);
                        navController.popBackStack();
                    }

                }

                @Override
                public void onFailure(Call<editProfileObject> call, Throwable t) {
                    Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_SHORT).show();

                }
            });
            // TODO:. load orders, logout
        }
    }


}
