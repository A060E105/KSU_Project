package com.example.tfminiplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
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
       bluefunction();
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

    @SuppressLint("LongLogTag")
    public void bluefunction() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 驗證是否有藍芽裝置
        if (mBluetoothAdapter == null) {
            // device doesn't support Bluetooth
            Log.d("Bluetooth", "device doesn't support Bluetooth");
        } else {
            // device does support Bluetooth
            Log.d("Bluetooth", "device does support Bluetooth");
        }

        // 檢查藍芽裝置是否已經開啟，如果沒有開啟，彈出對話方塊讓使用者選擇開啟
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // 搜尋裝置，查詢已經與本機配對的裝置
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            Log.d("BluetoothDevice", "get Bonded Devices");
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceMACAddress = device.getAddress();

                Log.d("BluetoothDevice name", deviceName);
                Log.d("BluetoothDevice MAC Address", deviceMACAddress);
            }
        } else {
            Log.d("BluetoothDevice", "not get Bonded Devices");
        }
    }

    
}