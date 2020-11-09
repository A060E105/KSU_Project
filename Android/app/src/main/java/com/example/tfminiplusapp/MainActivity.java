package com.example.tfminiplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;



public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static boolean destroyFlag = true;
    private int Range_old = 0;

    public static int Range_new = 0;
    private final static int REQUEST_ENABLE_BT = 1;
    public final static int MESSAGE_READ = 2;
    private final static int CONNECTING_STATUS = 3;

    final Fragment fragment_home = new Fragment_home();
    final Fragment fragment_message = new Fragment_message();
    final Fragment fragment_help = new Fragment_help();
    final Fragment fragment_setting = new Fragment_setting();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = fragment_home;

    private ArrayAdapter<String> mBTArrayAdapter;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ConnectedThread mConnectedThread;

    private Handler mHandler;
    private BluetoothSocket mBTSocket;


    AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBTArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);
        initFragment();

        Range_old = loadDistRange();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(MainActivity.this, readMessage, LENGTH_SHORT).show();
//                    Log.d(TAG, "handleMessage: " + readMessage);
                    to_JSON(splitData(readMessage));
                }

                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                        Toast.makeText(MainActivity.this, "Connected to Device: " + msg.obj, LENGTH_SHORT).show();
                        Log.d(TAG, "Connected to Device: " + msg.obj);
                    } else {
                        Toast.makeText(MainActivity.this, "Connection Failed", LENGTH_SHORT).show();
                        Log.d(TAG, "Connection Failed");
                    }
                }
            }
        };

        // check device is support bluetooth
        if (isBluetoothSupport()) {
            // check bluetooth is Enable
            if (!mBTAdapter.isEnabled()) {
                bluetoothOn();
            } else {
                String address = load();
                Log.d(TAG, "onCreate: load address " + address);
                if (!address.matches("")) {
                    connectBluetooth(address);
                } else {
                    listPairedDevices();
                    showAlert();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "ActivityResult Enable", LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Enable");
                if (mBTAdapter.isEnabled()) {
                    String address = load();
                    Log.d(TAG, "onCreate: load address " + address);
                    if (address != null) {
                        connectBluetooth(address);
                    } else {
                        listPairedDevices();
                        showAlert();
                    }
                }
            } else {
                Toast.makeText(this, "ActivityResult Disabled", LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Disabled");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (destroyFlag == true) {
            bluetoothOff();
        } else {
            destroyFlag = true;
        }
    }

    private boolean isBluetoothSupport() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter == null) {
            Toast.makeText(this, "Device doesn't support Bluetooth", LENGTH_SHORT).show();
            Log.d(TAG, "isBluetoothSupport: Device doesn't support Bluetooth");
            return false;
        } else {
            Toast.makeText(this, "Device does support Bluetooth", LENGTH_SHORT).show();
            Log.d(TAG, "isBluetoothSupport: Device does support Bluetooth");
            return true;
        }
    }

    private void bluetoothOn() {
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (mBTAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is open", LENGTH_SHORT).show();
            Log.d(TAG, "bluetoothOn: Bluetooth is open");
        }
    }

    private void bluetoothOff() {
        mBTAdapter.disable();
        Toast.makeText(MainActivity.this, "Bluetooth turned off", LENGTH_SHORT).show();
        Log.d(TAG, "bluetoothOff: Bluetooth turned off");
    }

    private void discover() {
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
            Toast.makeText(MainActivity.this, "Discovery stopped", LENGTH_SHORT).show();
            Log.d(TAG, "discover: Stopped");
        } else {
            if (mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear();
                mBTAdapter.startDiscovery();
                Toast.makeText(MainActivity.this, "Discovery started", LENGTH_SHORT).show();
                Log.d(TAG, "discover: started");
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Toast.makeText(MainActivity.this, "Bluetooth not on", LENGTH_SHORT).show();
                Log.d(TAG, "discover: Bluetooth not on");
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = null;
//                device =  intent.getParcelableArrayExtra(EXTRA_DEVICE);
//                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                mBTArrayAdapter.notifyDataSetChanged();
//            }
        }
    };

    private void listPairedDevices() {
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices) {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(MainActivity.this, "Show Paired Devices", LENGTH_SHORT).show();
            Log.d(TAG, "listPairedDevices: Show Paired Devices");
        } else {
            Toast.makeText(MainActivity.this, "Bluetooth not on", LENGTH_SHORT).show();
            Log.d(TAG, "listPairedDevices: Bluetooth not on");
        }
    }

    public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View converView = (View) inflater.inflate(R.layout.listview, null);
        alertDialog.setView(converView);
        alertDialog.setTitle("Devices");
        ListView  lv_devices = (ListView) converView.findViewById(R.id.lv_devices);
        lv_devices.setAdapter(mBTArrayAdapter);
        lv_devices.setOnItemClickListener(mDeviceClickListener);
        alert = alertDialog.create();
        alertDialog.show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            alert.cancel();
            if (!mBTAdapter.isEnabled()) {
                Toast.makeText(MainActivity.this, "Bluetooth not on", LENGTH_SHORT).show();
                Log.d(TAG, "onItemClick: Bluetootn not on");
                return;
            }


            Log.d(TAG, "onItemClick: Connecting ");
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() -17);

            new Thread() {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
                    save(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(MainActivity.this, "Socket creation failed", LENGTH_SHORT).show();
                        Log.e(TAG, "Socket creation failed");
                    }

                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        } catch (IOException e2) {
                            Toast.makeText(MainActivity.this, "Socket creation failed", LENGTH_SHORT).show();
                            Log.e(TAG, "Socket creation failed");
                        }
                    }

                    if (!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, uuid);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createInsecureRfcommSocketToServiceRecord(uuid);
    }

    private void initFragment() {
        fragmentManager.beginTransaction().add(R.id.container, fragment_home).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_message).hide(fragment_message).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_help).hide(fragment_help).commit();
        fragmentManager.beginTransaction().add(R.id.container, fragment_setting).hide(fragment_setting).commit();

//        bottomNavigationView_color_status(Bluetooth_Status.bluetooth_not_open);
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

    private String splitData(String str) {
        String data = new String(str);
        int start = data.indexOf("{\"L\":");
        int end = data.lastIndexOf("}}") + 2;
        if (start > end) {
            String[] array = data.split("}}", 1);
            data = array[1];
            start = data.indexOf("{\"L\":");
            end = data.lastIndexOf("}}") + 2;
        }
        if (end - start >= 100) {
            data = data.substring(start, end);
            start = data.indexOf("{\"L\":");
            end = data.indexOf("}}") + 2;
        }
        data = data.substring(start, end);
//        Log.d(TAG, "splitData: sbustring => " + data);
        return data;
    }

    private void to_JSON(String str) {
        try {
            JSONObject jsonobject = new JSONObject(str);
//            Log.d(TAG, "to_JSON: " + jsonobject);
            Log.d(TAG, "to_JSON: left dist => " + jsonobject.getJSONObject("L").getString("dist"));
            Log.d(TAG, "to_JSON: right dist => " + jsonobject.getJSONObject("R").getString("dist"));
            JSONObject left = new JSONObject(jsonobject.getString("L"));
            JSONObject right = new JSONObject(jsonobject.getString("R"));
            Log.d(TAG, "to_JSON: left" + left);
            Log.d(TAG, "to_JSON: right" + right);
            String L_dist = left.getString("dist");
            String L_strength = left.getString("strength");
            String L_temp = left.getString("temp");
            String R_dist = right.getString("dist");
            String R_strength = right.getString("strength");
            String R_temp = right.getString("temp");
            display(L_dist, L_strength, L_temp, R_dist, R_strength, R_temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void display(String L_dist, String L_strength, String L_temp, String R_dist, String R_strength, String R_temp) {
        // fragment_home
        Button btn_left = (Button)findViewById(R.id.btn_left);
        Button btn_right = (Button)findViewById(R.id.btn_right);
        btn_left.setText(L_dist);
        btn_right.setText(R_dist);
        // 如果有更改靈敏度則使用新設定的靈敏度，否則使用上次設定的靈敏度
        if (Range_new != 0) {
            // 小於設定的範圍則顯示按鈕
            if (Integer.parseInt(L_dist) < Range_new) {
                // 顯示按鈕
                btn_left.setVisibility(View.VISIBLE);
            } else {
                // 隱藏按鈕
                btn_left.setVisibility(View.INVISIBLE);
            }
            if (Integer.parseInt(R_dist) < Range_new) {
                btn_right.setVisibility(View.VISIBLE);
            } else {
                btn_right.setVisibility(View.INVISIBLE);
            }
        } else {
            if (Integer.parseInt(L_dist) < Range_old) {
                btn_left.setVisibility(View.VISIBLE);
            } else {
                btn_left.setVisibility(View.INVISIBLE);
            }
            if (Integer.parseInt(R_dist) < Range_old) {
                btn_right.setVisibility(View.VISIBLE);
            } else {
                btn_right.setVisibility(View.INVISIBLE);
            }
        }
        // fragment_message left
        TextView tv_L_dist = (TextView)findViewById(R.id.tv_L_dist);
        TextView tv_L_strength = (TextView)findViewById(R.id.tv_L_strength);
        TextView tv_L_temp = (TextView)findViewById(R.id.tv_L_temp);
        tv_L_dist.setText(L_dist + "cm");
        tv_L_strength.setText(L_strength);
        tv_L_temp.setText(L_temp);
        // fragment_message right
        TextView tv_R_dist = (TextView)findViewById(R.id.tv_R_dist);
        TextView tv_R_strength = (TextView)findViewById(R.id.tv_R_strength);
        TextView tv_R_temp = (TextView)findViewById(R.id.tv_R_temp);
        tv_R_dist.setText(R_dist + "cm");
        tv_R_strength.setText(R_strength);
        tv_R_temp.setText(R_temp);
    }

    public void save(String address) {
        FileOutputStream output = null;
        BufferedWriter writer = null;

        try {
            output = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(output));
            writer.write(address);
            Log.d(TAG, "save: save address");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int loadDistRange() {
        FileInputStream input = null;
        BufferedReader reader = null;
        StringBuilder distRange = new StringBuilder();

        try {
            input = openFileInput("distrange");
            reader = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while ((line = reader.readLine()) != null) {
                distRange.append(line);
            }
            Log.d(TAG, "load: load dist range");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (distRange.toString().matches("")) {
            // default
            return 300;
        } else {
            String str = distRange.toString();
            return Integer.parseInt(str);
        }
    }

    public String load() {
        FileInputStream input = null;
        BufferedReader reader = null;
        StringBuilder address = new StringBuilder();

        try {
            input = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(input));
            String line = "";
            while ((line = reader.readLine()) != null) {
                address.append(line);
            }
            Log.d(TAG, "load: load address");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return address.toString();
    }

    public void connectBluetooth(String address) {
        boolean fail = false;

        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

        try {
            mBTSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            fail = true;
            Toast.makeText(MainActivity.this, "Socket creation failed", LENGTH_SHORT).show();
            Log.e(TAG, "Socket creation failed");
        }

        try {
            mBTSocket.connect();
        } catch (IOException e) {
            try {
                fail = true;
                mBTSocket.close();
                mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
            } catch (IOException e2) {
                Toast.makeText(MainActivity.this, "Socket creation failed", LENGTH_SHORT).show();
                Log.e(TAG, "Socket creation failed");
            }
        }

        if (!fail) {
            mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
            mConnectedThread.start();

//            mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
        }

    }
}

