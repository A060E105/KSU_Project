package com.example.tfminiplusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.EXTRA_DEVICE;
import static android.widget.Toast.LENGTH_SHORT;



public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
    private ListView myListView;

    private Handler mHandler;
    private BluetoothSocket mBTSocket;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBTArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);
        myListView = (ListView)findViewById(R.id.listview);
        myListView.setAdapter(mBTArrayAdapter);
        myListView.setOnItemClickListener(mDeviceClickListener);

        initFragment();

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
                    Toast.makeText(MainActivity.this, readMessage, LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: " + readMessage);
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

        if (isBluetoothSupport()) {
            bluetoothOn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Enable", LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Enable");
            } else {
                Toast.makeText(this, "Disabled", LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Disabled");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluefunction();
    }


    protected void onResume() {
        super.onResume();
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
            listPairedDevices();
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.listview, null);
            dialogBuilder.setTitle("select device").setView(view).show();
        }
    }

    private void bluetoothOff() {
        mBTAdapter.disable();
        Log.d(TAG, "bluetoothOff: ");
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

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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

    @SuppressLint("LongLogTag")
    public void bluefunction() {
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // 驗證是否有藍芽裝置
//        if (mBluetoothAdapter == null) {
//            // device doesn't support Bluetooth
//            Log.d("Bluetooth", "device doesn't support Bluetooth");
//            return;
//        } else {
//            // device does support Bluetooth
//            Log.d("Bluetooth", "device does support Bluetooth");
//        }
//
//        // 檢查藍芽裝置是否已經開啟，如果沒有開啟，彈出對話方塊讓使用者選擇開啟
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            if (mBluetoothAdapter.isEnabled()) {
//                // 藍芽開啟，尚未連接裝置
////                bottomNavigationView_color_status(Bluetooth_Status.bluetooth_is_open_not_connect);
//            } else {
//                // 藍芽未開啟
////                bottomNavigationView_color_status(Bluetooth_Status.bluetooth_not_open);
//            }
//        }
//
//        // 搜尋裝置，查詢已經與本機配對的裝置
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            Log.d("BluetoothDevice", "get Bonded Devices");
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceMACAddress = device.getAddress();
//
//                Log.d("BluetoothDevice name", deviceName);
//                Log.d("BluetoothDevice MAC Address", deviceMACAddress);
//                this.bluetooth_flag = true;
//            }
//        } else {
//            Log.d("BluetoothDevice", "not get Bonded Devices");
//            this.bluetooth_flag = false;
//        }
//
//
//        if (this.bluetooth_flag == true) {
//            Toast.makeText(this, "bluetooth Open", LENGTH_SHORT);
//            String macAddr = "30:AE:A4:97:AF:52";
//            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddr);
//            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//
//            BluetoothSocket tempSocket = null;
//            try {
//                 tempSocket = device.createRfcommSocketToServiceRecord(uuid);
//                Log.d("BluetoothDevice", "get UUID");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            // 使用 final 宣告，否著後面try會出現錯誤訊息
//            final BluetoothSocket mySocket = tempSocket;
//
//            new Thread() {
//                @Override
//                public void run() {
//                    mBluetoothAdapter.cancelDiscovery();
//                    Log.d("BluetoothSocket", "run: cancelDiscovery");
//                    try {
//                        mySocket.connect();
//                        Log.d("BluetoothSocket", "run: mySocket.connect");
////                        bottomNavigationView_color_status(Bluetooth_Status.bluetooth_is_connect);
//                    } catch (IOException e) {
//                        try {
//                            mySocket.close();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                        e.printStackTrace();
//                    }
//                    super.run();
//                }
//            }.start();
//        } else {
//            Toast.makeText(this, "bluetooth Close", LENGTH_SHORT);
//        }
    }
}
