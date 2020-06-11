package com.example.tfminiplusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @BindView(R.id.navigation_View) NavigationView navigation_view = null;
        navigation_view.setNavigationItemSelectedListener(this);

    }
}