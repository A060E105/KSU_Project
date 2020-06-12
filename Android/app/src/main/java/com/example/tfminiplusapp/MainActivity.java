package com.example.tfminiplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;

import com.example.tfminiplusapp.R;

import static com.example.tfminiplusapp.R.id.navigation_view;


public class MainActivity extends AppCompatActivity {

    // @BindView(R.id.navigation_view)
    NavigationView navigation_view;

    @BindView(R.id.DrawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigation_view = (NavigationView) findViewById(R.id.navigation_view);

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項ID
                int id = menuItem.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.nav_home) {
                    // 按下「首頁」要做的事
                    Toast.makeText(MainActivity.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.nav_message) {
                    Toast.makeText(MainActivity.this, "詳細說明", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.nav_help) {
                    Toast.makeText(MainActivity.this, "幫助", Toast.LENGTH_LONG).show();
                }
                else if (id == R.id.nav_setting) {
                    // 按下「使用說明」要做的事
                    Toast.makeText(MainActivity.this, "設定", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }

}