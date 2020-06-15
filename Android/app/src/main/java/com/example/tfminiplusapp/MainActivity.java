package com.example.tfminiplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;

import com.example.tfminiplusapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {

    final Fragment fragment_home = new Fragment_home();
    final Fragment fragment_message = new Fragment_message();
    final Fragment fragment_help = new Fragment_help();
    final Fragment fragment_setting = new Fragment_setting();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = fragment_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       initFragment();
    }

    private void initFragment() {
        fragmentManager.beginTransaction().add(R.id.container, fragment_home).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_message).hide(fragment_message).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_help).hide(fragment_help).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_setting).hide(fragment_setting).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Toast.makeText(MainActivity.this, "Home", LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().hide(active).show(fragment_home).commit();
                        active = fragment_home;
                        break;
                    case R.id.nav_message:
                        // Toast.makeText(MainActivity.this, "message", LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().hide(active).show(fragment_message).commit();
                        active = fragment_message;
                        break;
                    case R.id.nav_help:
                        // Toast.makeText(MainActivity.this, "help", LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().hide(active).show(fragment_help).commit();
                        active = fragment_help;
                        break;
                    case R.id.nav_setting:
                        // Toast.makeText(MainActivity.this, "setting", LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().hide(active).show(fragment_setting).commit();
                        active = fragment_setting;
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Error", LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }


    
    
}