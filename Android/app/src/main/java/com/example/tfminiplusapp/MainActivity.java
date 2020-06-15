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
import butterknife.BindView;

import com.example.tfminiplusapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {

    final Fragment fragment_home = new Fragment_home();
    final Fragment fragment_message = new Fragment_message();
    final Fragment fragment_help = new Fragment_help();
    final Fragment fragment_setting = new Fragment_setting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // startActivity(new Intent(MainActivity.this, Fragment_home.class));
                        Toast.makeText(MainActivity.this, "Home", LENGTH_SHORT).show();
                        break;
                    case R.id.nav_message:
                        Toast.makeText(MainActivity.this, "message", LENGTH_SHORT).show();
                        break;
                    case R.id.nav_help:
                        Toast.makeText(MainActivity.this, "help", LENGTH_SHORT).show();
                        break;
                    case R.id.nav_setting:
                        Toast.makeText(MainActivity.this, "setting", LENGTH_SHORT).show();
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