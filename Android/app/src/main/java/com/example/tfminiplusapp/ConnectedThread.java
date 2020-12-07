package com.example.tfminiplusapp;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final static String TAG = "MY_Bluetooth";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private byte[] mmBuffer;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "ConnectedThread input stream: ", e);
        }

        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException  e) {
            Log.e(TAG, "ConnectedThread output stream: ", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[200];
        int numBytes;

        while (true) {
            try {
                // write command to ESP32
                write(MainActivity.ESP32_command);
                SystemClock.sleep(100);
                numBytes = mmInStream.read(mmBuffer);
                SystemClock.sleep(100);
                Message readMsg = mHandler.obtainMessage(MainActivity.MESSAGE_READ, numBytes, -1, mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected:", e);
                break;
            }
        }
    }

    public void write(String input) {
        byte[] bytes = input.getBytes();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}
