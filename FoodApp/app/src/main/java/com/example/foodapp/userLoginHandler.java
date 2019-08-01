package com.example.foodapp;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class userLoginHandler extends Fragment {

    private  LoginViewModel viewModel;
    private String usernameString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.fragment_user_login_handler, container, false);
        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        Button userlogin = (Button) view.findViewById(R.id.loginasUser);
        userlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject  loginInfo = readData(view);
                loginInfo.addProperty("user", true);  // To identify vendor and user
                viewModel.authenticate(loginInfo,view);

            }
        });
//
        Button vendorlogin = (Button) view.findViewById(R.id.loginAsVendor);
        vendorlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject loginInfo = readData(view);
                loginInfo.addProperty("user",false);
                viewModel.authenticate(loginInfo,view);
            }
        });
//        final NavController navController = Navigation.findNavController(view);
//
//
////        requireActivity().getonBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
////                new OnBackPressedCallback(true) {
////                    @Override
////                    public void handleOnBackPressed() {
////                        viewModel.refuseAuthentication();
////                        navController.popBackStack(R.id.userLoginHandler, false);
////                    }
////                });
////        final View root = view;
////        viewModel.authenticationState.observe(getViewLifecycleOwner(),
////                new Observer<LoginViewModel.AuthenticationState>() {
////                    @Override
////                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
////                        switch (authenticationState) {
////                            case AUTHENTICATED:
////                                navController.popBackStack();
////                                break;
////                            case INVALID_AUTHENTICATION:
////                                Snackbar.make(root,
////                                        "Invalid Login Credentials",
////                                        Snackbar.LENGTH_SHORT
////                                ).show();
////                                break;
////                        }
////                    }
////                });
//        final NavController navController = Navigation.findNavController(view);

        Button signUp = (Button) view.findViewById(R.id.newUserID);
        final Fragment that = this;
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               NavHostFragment.findNavController(that).navigate(R.id.action_userLoginHandler_to_userSignUpFragment);
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private JsonObject readData(View view){

        EditText username = (EditText) view.findViewById(R.id.usernameID);
        EditText password = (EditText) view.findViewById(R.id.passwordID);
        usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        JsonObject json = new JsonObject();
        if(usernameString!=null && passwordString!=null) {

            json.addProperty("username",usernameString );
            json.addProperty("password",passwordString );

        }
        else{
            Snackbar.make(view,"Username or password cannot be empty", Snackbar.LENGTH_SHORT).show();
        }
        return json;
    }
    public void performAuthentication(final JsonObject loginInfo, final LoginViewModel vm, final View view)    {
//
        DataLoader dataLoader = DataLoader.getInstance();
        dataLoader.authentication(loginInfo, new Callback<LoginObject>() {
            @Override
            public void onResponse(Call<LoginObject> call, Response<LoginObject> response) {
            //todo. of it has been redirected from edit profile, get back there
                if(response.body().logged_in){
                    vm.authenticationState.setValue(LoginViewModel.AuthenticationState.AUTHENTICATED);
                    vm.username = loginInfo.get("username").getAsString().trim();
                    Snackbar.make(view,response.body().message, Snackbar.LENGTH_SHORT).show();
                }
                else{
                    vm.authenticationState.setValue(LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION);
                    Snackbar.make(view,response.body().message, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginObject> call, Throwable t) {
                vm.authenticationState.setValue(LoginViewModel.AuthenticationState.UNAUTHENTICATED);
                Snackbar.make(view,t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

}


class LoginViewModel extends ViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    final MutableLiveData<AuthenticationState> authenticationState =  new MutableLiveData<>();
    String username;

    public LoginViewModel() {
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        username = null;

    }

    public void authenticate(JsonObject loginInfo,View view) {
//        Snackbar.make(view,username,Snackbar.LENGTH_SHORT).show();
        userLoginHandler userlogin = new userLoginHandler();
        userlogin.performAuthentication(loginInfo,this,view);

//
//
//        if(authenticationState.getValue().compareTo(AuthenticationState.AUTHENTICATED)==0){
//            this.username = loginInfo.get("username").toString();
////            Snackbar.make(view,this.username,Snackbar.LENGTH_LONG).show();
//        }
//
//        if(this.username.equals(null))
//            Snackbar.make(view,"username null",Snackbar.LENGTH_LONG).show();

    }

    public void refuseAuthentication() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }
}
