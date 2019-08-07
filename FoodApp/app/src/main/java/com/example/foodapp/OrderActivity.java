package com.example.foodapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.foodapp.databinding.ActivityOrderBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.security.spec.ECField;

public class OrderActivity extends AppCompatActivity {

    private ActivityOrderBinding binding;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavController navControllerProfile;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        setContentView( R.layout.activity_order);
        androidx.appcompat.widget.Toolbar myToolBar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        navController = Navigation.findNavController(this,R.id.navhost_fragment);
        NavigationUI.setupWithNavController(myToolBar, navController);
               bottomNavigationView = (BottomNavigationView)findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setDrawerLayout(drawerLayout)
                        .build();

         navControllerProfile = Navigation.findNavController(this, R.id.nav_host_fragment2);
        navigationView = findViewById(R.id.profile_nav_view);
        NavigationUI.setupWithNavController(navigationView, navControllerProfile);

         drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
         actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,myToolBar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//navigationView = (NavigationView) findViewById(R.id.profile_nav_view);



        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setupDrawerContent(navigationView);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}