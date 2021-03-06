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

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.relsib.dao.DbModel;
import com.relsib.dao.TableThermometers;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private final static Integer UNKNOWN_THERMOMETER = -1;
    private final static String TAG = BLEService.class.getSimpleName();
    public static BluetoothAdapter mBluetoothAdapter;
    public static Activity mActivityContext;
    public static Context mServiceContext;
    public static ArrayList<SmartThermometer> thermometers = new ArrayList<>();
    public static ArrayList<SmartThermometer> unknownThermometers = new ArrayList<>();
    public static TableThermometers tableThermometers;
    public static int MAX_BLE_DEVICES = 6;
    private static BluetoothManager mBluetoothManager;
    private static DbModel db;
    private static int thermometersActiveCount = 0;
    private final IBinder mBinder = new LocalBinder();
    private Timer myTimer = new Timer(); // Создаем таймер

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public static void clear() {

        for (int i = 0; i < thermometers.size(); i++) {
            removeThermometer(thermometers.get(i));
        }
        thermometers.clear();
        tableThermometers.clear();
        final Intent intent = new Intent(BLEService.ACTION_GATT_DISCONNECTED);
        mServiceContext.sendBroadcast(intent);
    }

    public static SmartThermometer findThermometerByMac(String macAddress) {
        //Log.e(TAG, "mac to search " + macAddress + "size " + thermometers.size());
        SmartThermometer tempThermometer;
        for (int i = 0; i < thermometers.size(); i++) {
            //Log.e(TAG, " enumerate " + thermometers.get(i).mDeviceMacAddress);
            tempThermometer = thermometers.get(i);
            if (tempThermometer.mDeviceMacAddress.equals(macAddress)) {
                //        Log.e(TAG, "findThermometerByMac return: " + thermometers.get(i).mDeviceMacAddress);
                return tempThermometer;
            }
        }

        //  Log.e(TAG, "NOT FOUND ");
        return null;
    }

    public static void removeThermometer(SmartThermometer thermometer) {
        thermometers.remove(thermometer);
        String filePath = mServiceContext.getFilesDir().getParent() + "/shared_prefs/" + thermometer.mDeviceMacAddress + SettingsViewCommon.FILE_NAME + ".xml";
        File deletePrefFile = new File(filePath);
        Log.e(TAG, filePath);
        if (deletePrefFile.delete()) Log.e(TAG, "deleted");
        else Log.e(TAG, "delete failed");
        tableThermometers.deleteRecord(thermometer);
        //thermometer.shutdown();
        thermometer.disconnect();
        thermometer.close();
        return;
    }

    public static void addUnknownSmartThermometer(BluetoothDevice device, int rssi) {
        SmartThermometer tempDevice = new SmartThermometer(device.getAddress(), device.getName());
        if (!unknownThermometers.contains(tempDevice)) {
            unknownThermometers.add(tempDevice);
            tempDevice.mDeviceRssi = rssi;
        }
    }

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
        for (int i = 0; i < thermometers.size(); i++) {
            thermometers.get(i).disconnect();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
//        for (int i = 0; i < thermometers.size(); i++) {
//            thermometers.get(i).connect(true);
//        }
        super.onRebind(intent);
    }

    public boolean initialize(Activity mContext) {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (BLEService.mBluetoothManager == null) {
            // BLEService.mActivityContext = getApplicationContext();
            BLEService.mActivityContext = mContext;
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

        db = new DbModel(mServiceContext);
        tableThermometers = new TableThermometers(db.getWritableDatabase());

        return true;
    }

    public void loadMyThermometers() {
        thermometers = tableThermometers.getAllRecordsAsObjects();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                for (int i = 0; i < thermometers.size(); i++) {
                    SmartThermometer tempThermometer = thermometers.get(i);
                    if (!BLEService.mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(enableBtIntent);
                    }
                    if (tempThermometer != null) {
                        if (!tempThermometer.mDeviceMacAddress.equals("aa:aa:aa:aa:aa")) {
                            //if (tempThermometer.mConnectionState == BLEService.STATE_CONNECTED){
                            tempThermometer.refreshRssi();
                            //}
                            if (tempThermometer.mConnectionState == BLEService.STATE_DISCONNECTED) {
                                // new ConnectThread(tempThermometer).start();
                                tempThermometer.connect(true);
                            }
                        }
                    }
                }
            }

            ;
        }, 0L, 60L * 100); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.
        SmartThermometer stubThermometer = new SmartThermometer("aa:aa:aa:aa:aa", "TEST DEVICE");
        stubThermometer.initPreferences();
        thermometers.add(stubThermometer);
    }

    public void addSmartThermometer(String mDeviceAddress, String deviceName) {
        thermometers.add(new SmartThermometer(mDeviceAddress, deviceName));
    }

    public void pushUnknownToMyDevices() {

        SmartThermometer tempThermometer;
        for (int i = 0; i < unknownThermometers.size(); i++) {
            tempThermometer = unknownThermometers.get(i);
            if (tempThermometer.selected) {
                if (thermometers.size() < MAX_BLE_DEVICES) {
                    thermometers.add(tempThermometer);
                    tempThermometer.initPreferences();
                    new ConnectThread(tempThermometer).start();
                } else {
                    Toast.makeText(mActivityContext, "Протокол BLE не поддерживает больше " + MAX_BLE_DEVICES + " устройств", Toast.LENGTH_LONG).show();
                }


            }
        }
        unknownThermometers.clear();
    }

    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }

    class ConnectThread extends Thread {
        SmartThermometer thermometer;

        public ConnectThread(SmartThermometer thermometer2) {
            thermometer = thermometer2;
        }
        public void run() {
            if (thermometers.size() < MAX_BLE_DEVICES) {
                Log.e(TAG, "thread");
                thermometer.connect(true);
            } else {
                Toast.makeText(mActivityContext, "Протокол BLE не поддерживает больше " + MAX_BLE_DEVICES + " устройств", Toast.LENGTH_LONG);
            }
        }

    }
}