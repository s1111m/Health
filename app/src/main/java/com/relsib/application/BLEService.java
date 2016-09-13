/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.relsib.application;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.relsib.dao.DbModel;
import com.relsib.dao.TableThermometers;

import java.util.ArrayList;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BLEService extends Service {

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    private final static String TAG = BLEService.class.getSimpleName();
    public static BluetoothAdapter mBluetoothAdapter;
    public static Context mServiceContext;
    public static ArrayList<SmartThermometer> thermometers = new ArrayList<>();
    public static ArrayList<SmartThermometer> unknownThermometers = new ArrayList<>();
    public static TableThermometers tableThermometers;
    private static BluetoothManager mBluetoothManager;
    private static DbModel db;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < thermometers.size(); i++) {
            thermometers.get(i).close();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        //close();
        for (int i = 0; i < thermometers.size(); i++) {
            thermometers.get(i).disconnect();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        for (int i = 0; i < thermometers.size(); i++) {
            thermometers.get(i).connect(true);
        }
        super.onRebind(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public static void clear (){

        for (int i = 0; i < thermometers.size(); i++) {
            thermometers.get(i).close();
        }
        thermometers.clear();
        tableThermometers.clear();
    }
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (BLEService.mBluetoothManager == null) {
            BLEService.mServiceContext = getApplicationContext();
            mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (BLEService.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        BLEService.mBluetoothAdapter = BLEService.mBluetoothManager.getAdapter();
        if (BLEService.mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        if (!BLEService.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }

        db = new DbModel(BLEService.mServiceContext);
        tableThermometers = new TableThermometers(db.getWritableDatabase());
        return true;
    }

    public void loadMyThermometers() {
        Log.e(TAG, "LOADING");
        thermometers = tableThermometers.getAllRecordsAsObjects();
    }

    public void addSmartThermometer(String mDeviceAddress, String deviceName) {
        thermometers.add(new SmartThermometer(mDeviceAddress, deviceName));
    }

    public void addUnknownSmartThermometer(BluetoothDevice device, int rssi) {
        SmartThermometer tempDevice = new SmartThermometer(device);
        if (!unknownThermometers.contains(tempDevice)) {
            unknownThermometers.add(tempDevice);
            tempDevice.mDeviceRssi = rssi;
        }
    }

    public void pushUnknownToMyDevices() {
        for (int i = 0; i < unknownThermometers.size(); i++) {
            SmartThermometer tempThermometer = unknownThermometers.get(i);
            if (tempThermometer.selected && !thermometers.contains(tempThermometer)) {
                // tempThermometer.connect(true);
                thermometers.add(tempThermometer);
                tableThermometers.save(tempThermometer);

            }
        }
        unknownThermometers.clear();
    }

    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }
}