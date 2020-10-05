package com.example.tfminiplusapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_setting extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listview;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        ArrayList<String> data = new ArrayList<>();
        data.add("設定藍芽名稱");
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
                Toast.makeText(
                        getContext(),
                        "ID：" + index +
                                "   選單文字："+ listView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
                switch ((int) index) {
                    case 0:
//                        Toast.makeText(getContext(), "你按了第一個", Toast.LENGTH_SHORT).show();
                        LayoutInflater inflater=getLayoutInflater();
                        View view=inflater.inflate(R.layout.change_bluetooth_name, null);
                        dialogBuilder = new AlertDialog.Builder(getContext());
                        dialogBuilder.setTitle("藍芽名稱")
                                .setView(view)
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        View v = inflater.inflate(R.layout.fragment_setting, container, false);
                                        EditText editText = (EditText) (v.findViewById(R.id.ed_btName));
                                        Toast.makeText(getContext(), "你的id是" +
                                                editText.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        dialog = dialogBuilder.show();
                        break;
                    case 1:
//                        Toast.makeText(getContext(), "你按了第二個", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
//                        MainActivity.showAlert();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + (int) index);
                }
            }
        });


        return view;
    }



}