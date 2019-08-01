package com.example.foodapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
//                                        Snackbar.make(view,viewModel.username,Snackbar.LENGTH_LONG).show();
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
        LoginViewModel loginViewModel = new LoginViewModel();
        EditText oldpassword = (EditText)view.findViewById(R.id.oldpasswordID);
        EditText newpassword = (EditText)view.findViewById(R.id.newpasswordID);
        EditText newpassword2 = (EditText)view.findViewById(R.id.reEnterNewPasswordId);

        if (!newpassword.getText().toString().equals(newpassword2.getText().toString())) {
               Snackbar.make(view, "Password and confirm password must match", Snackbar.LENGTH_SHORT);
//            restartFragment();
        }
        JsonObject pwdInfo  = new JsonObject();
        pwdInfo.addProperty("username",username);
        pwdInfo.addProperty("password",oldpassword.getText().toString());
        pwdInfo.addProperty("newPassword",newpassword.getText().toString());
        pwdInfo.addProperty("editField","password");
//            Snackbar.make(view,pwdInfo.toString(),Snackbar.LENGTH_LONG).show();
            DataLoader dataLoader = DataLoader.getInstance();
            dataLoader.changePassword(pwdInfo, new Callback<editProfileObject>() {
                @Override
                public void onResponse(Call<editProfileObject> call, Response<editProfileObject> response) {

                    if(response.body().edit_success){
                         Snackbar.make(view,response.body().message, Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        Snackbar.make(view,response.body().message, Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<editProfileObject> call, Throwable t) {
                      Snackbar.make(view,t.getMessage(), Snackbar.LENGTH_SHORT).show();

                }
            });
           // TODO:  add  signup.. load orders
    }


}
