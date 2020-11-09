package com.example.tfminiplusapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_setting extends Fragment {

    private final String TAG = MainActivity.class.getSimpleName() + " setting";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listview;

    AlertDialog alert;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    private ArrayAdapter<String> mBTArrayAdapter;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    public Fragment_setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_setting.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_setting newInstance(String param1, String param2) {
        Fragment_setting fragment = new Fragment_setting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mBTArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        ArrayList<String> data = new ArrayList<>();
        data.add("設定靈敏度");
        data.add("選擇藍芽裝置");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                data
        );

        ListView lvset = (ListView) view.findViewById(R.id.lvset);
        lvset.setAdapter(adapter);

        lvset.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int pos,
                                    long index) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) arg0;
//                Toast.makeText(
//                        getContext(),
//                        "ID：" + index +
//                                "   選單文字："+ listView.getItemAtPosition(pos).toString(),
//                        Toast.LENGTH_SHORT).show();
                switch ((int) index) {
                    case 0:
//                        Toast.makeText(getContext(), "你按了第一個", Toast.LENGTH_SHORT).show();
                        LayoutInflater inflater = getLayoutInflater();
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.change_bluetooth_name, null);
                        final EditText dist = (EditText) view.findViewById(R.id.ed_distRange);
                        dialogBuilder = new AlertDialog.Builder(getContext());
                        dialogBuilder.setTitle("設定靈敏度")
                                .setView(view)
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        View v = inflater.inflate(R.layout.change_bluetooth_name, container, false);
                                        String str = dist.getText().toString();
                                        if (!str.matches("")) {
                                            int distRange = Integer.parseInt(str);
                                            Log.d(TAG, "onClick: dist rnage is " + distRange);
                                            if (distRange <= 800 && distRange >= 300) {
                                                // save dist rnage to file
                                                saveDistRange(distRange);
                                                MainActivity.Range_new = distRange;
//                                                Toast.makeText(getContext(), "將於下次啟動時生效", LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), "range is 300 to 800", LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.d(TAG, "onClick: dist string is null");
                                        }
                                    }
                                });

                        dialog = dialogBuilder.show();
                        break;
                    case 1:
                        listPairedDevices();
                        showAlert();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + (int) index);
                }
            }
        });


        return view;
    }

    public void save(String address) {
        FileOutputStream output = null;
        BufferedWriter writer = null;

        try {
            output = getActivity().openFileOutput("data", Context.MODE_PRIVATE);
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

    public void saveDistRange(int distRange) {
        FileOutputStream output = null;
        BufferedWriter writer = null;

        try {
            output = getActivity().openFileOutput("distrange", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(output));
            writer.write(String.valueOf(distRange));
            Log.d(TAG, "save: save dist rnage");
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



    private void listPairedDevices() {
        mBTArrayAdapter.clear();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            for (BluetoothDevice device : mPairedDevices) {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(getActivity(), "Show Paired Devices", LENGTH_SHORT).show();
            Log.d(TAG, "listPairedDevices: Show Paired Devices");
        } else {
            Toast.makeText(getActivity(), "Bluetooth not on", LENGTH_SHORT).show();
            Log.d(TAG, "listPairedDevices: Bluetooth not on");
        }
    }

    public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
                Toast.makeText(getActivity(), "Bluetooth not on", LENGTH_SHORT).show();
                Log.d(TAG, "onItemClick: Bluetootn not on");
                return;
            }

            Log.d(TAG, "onItemClick: Save device address");
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() -17);

            new Thread() {
                @Override
                public void run() {
                    save(address);
                    Intent intent = getActivity().getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    MainActivity.destroyFlag = false;
                    getActivity().finish();
                    startActivity(intent);
                }
            }.start();
        }
    };
}