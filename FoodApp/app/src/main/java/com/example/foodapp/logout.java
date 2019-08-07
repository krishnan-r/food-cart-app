package com.example.foodapp;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class logout extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final userLoginHandler userlogin = new userLoginHandler();
       final  View view = inflater.inflate(R.layout.fragment_logout, container, false);
        final LoginViewModel viewModel =userlogin.getViewModel();
        final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment2);
        viewModel.authenticationState.observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:
                                viewModel.authenticationState.setValue(LoginViewModel.AuthenticationState.UNAUTHENTICATED);
                                break;
                            case UNAUTHENTICATED:
                                Snackbar.make(view,"No user loggedin. Redirecting..",Snackbar.LENGTH_SHORT).show();
                                navController.navigate(R.id.userLoginHandler);
                        }
                    }
                });
        Button switchUser = (Button) view.findViewById(R.id.switchUserID);
        switchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(view,"Redirecting to login page",Snackbar.LENGTH_SHORT).show();
                navController.navigate(R.id.userLoginHandler);
//                navController.popBackStack();
            }
        });
        return view;
    }

}