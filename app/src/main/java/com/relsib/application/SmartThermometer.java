package com.relsib.application;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.util.Log;

import com.relsib.bluetooth.RelsibBluetoothProfile;
import com.relsib.dao.DbModel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;


/**
 * Created by S1M on 11.09.2016.
 */
public class SmartThermometer {
    private final String TAG = SmartThermometer.class.getSimpleName();
    public long _ID = DbModel.UNSAVED_ID;
    public int mDeviceRssi;
    public String mDeviceMacAddress;
    public String mDeviceName;
    public String mDeviceModelNumber;
    public String mDeviceSerialNumber;
    public String mDeviceFirmwareRevisionNumber;
    public String mDeviceHardwareRevisionNumber;
    public String mDeviceSoftwareRevisionNumber;
    public String mDeviceManufacturer;
    public int mDeviceBatteryLevel;
    public Float intermediateTemperature;
    public Float maxTemperature = -200F;
    public boolean selected = false;
    public int mConnectionState = BLEService.STATE_DISCONNECTED;
    private BluetoothGatt mBluetoothGatt;
    private boolean isNotifyEnabled = false;
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedList<BluetoothGattCharacteristic>();

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // intentAction = BLEService.ACTION_GATT_CONNECTED;
                mConnectionState = BLEService.STATE_CONNECTED;
                // broadcastUpdate(intentAction);
                Log.e(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = BLEService.ACTION_GATT_DISCONNECTED;
                mConnectionState = BLEService.STATE_DISCONNECTED;
                Log.e(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.v(TAG, "Services discovered");
                broadcastUpdate(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
                    if (mDeviceSerialNumber == null) {
                        readInfoTypes();
                    } else getTemperatureByNotify(true);
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            characteristicReadQueue.remove();


            if (status == BluetoothGatt.GATT_SUCCESS) {
                UUID uuid = characteristic.getUuid();
                if (uuid.equals(RelsibBluetoothProfile.SERIAL_NUMBER_UUID)) {
                    setmDeviceSerialNumber(characteristic.getStringValue(0).substring(0, 12));
                    Log.e(TAG, "Device serial + " + mDeviceSerialNumber);
                    // return;
                }
//                if (uuid.equals(RelsibBluetoothProfile.FIRMWARE_REVISION_UUID)) {
//                    mDeviceFirmwareRevisionNumber = characteristic.getStringValue(0);
//                  //  return;
//                }
//                if (uuid.equals(RelsibBluetoothProfile.HARDWARE_REVISION_UUID)) {
//                    mDeviceHardwareRevisionNumber = characteristic.getStringValue(0);
//                  //  return;
//                }
                if (uuid.equals(RelsibBluetoothProfile.MANUFACTURER_NAME_UUID)) {
                    mDeviceManufacturer = characteristic.getStringValue(0).substring(0, 10);
                    Log.e(TAG, "Device Manufacturer + " + mDeviceManufacturer);
                    // return;
                }
//                if (uuid.equals(RelsibBluetoothProfile.SOFTWARE_REVISION_UUID)) {
//                    mDeviceSoftwareRevisionNumber = characteristic.getStringValue(0);
//                   // return;
//                }
                if (uuid.equals(RelsibBluetoothProfile.MODEL_NUMBER_UUID)) {
                    mDeviceModelNumber = characteristic.getStringValue(0).substring(0, 10);
                    Log.e(TAG, "Device model + " + mDeviceModelNumber);
                    //   return;
                }
                if (uuid.equals(RelsibBluetoothProfile.BATTERY_LEVEL)) {
                    mDeviceBatteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Log.e(TAG, "Battery level + " + mDeviceBatteryLevel);
                    //   return;
                }
                broadcastUpdate(BLEService.ACTION_DATA_AVAILABLE, characteristic);
                if (characteristicReadQueue.size() > 0)
                    mBluetoothGatt.readCharacteristic(characteristicReadQueue.element());
                if (characteristicReadQueue.size() == 0)

                    getTemperatureByNotify(true);

            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCHANGE " + mDeviceMacAddress);
            broadcastUpdate(BLEService.ACTION_DATA_AVAILABLE, characteristic);
            UUID uuid = characteristic.getUuid();
            if (uuid.equals(RelsibBluetoothProfile.INTERMEDIATE_TEMPERATURE)) {
                intermediateTemperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
                if (maxTemperature < intermediateTemperature)
                    maxTemperature = intermediateTemperature;
            }
            if (uuid.equals(RelsibBluetoothProfile.BATTERY_LEVEL)) {
                mDeviceBatteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.e(TAG, "Battery level + " + mDeviceBatteryLevel);
                //mDeviceBatteryLevel=Integer.valueOf(characteristic.getStringValue(0));
            }

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            mDeviceRssi = rssi;
        }
    };

    public SmartThermometer(String mDeviceMacAddress, String mDeviceName) {
        this.mDeviceMacAddress = mDeviceMacAddress;
        this.mDeviceName = mDeviceName;
    }

    public SmartThermometer(BluetoothDevice device) {
        this.mDeviceMacAddress = device.getAddress();
        this.mDeviceName = device.getName();

    }

    public static SmartThermometer SmartThermometerFactory(long _ID, String mDeviceName, String mDeviceMac, String mDeviceModelNumber, String mDeviceSerialNumber, String mDeviceFirmwareRevisionNumber,
                                                           String mDeviceHardwareRevisionNumber, String mDeviceSoftwareRevisionNumber, String mDeviceManufacturer, int mDeviceBatteryLevel) {
        SmartThermometer thermomether = new SmartThermometer(mDeviceMac, mDeviceName);
        thermomether._ID = _ID;
        thermomether.mDeviceModelNumber = mDeviceModelNumber;
        thermomether.mDeviceSerialNumber = mDeviceSerialNumber;
        thermomether.mDeviceFirmwareRevisionNumber = mDeviceFirmwareRevisionNumber;
        thermomether.mDeviceHardwareRevisionNumber = mDeviceHardwareRevisionNumber;
        thermomether.mDeviceSoftwareRevisionNumber = mDeviceSoftwareRevisionNumber;
        thermomether.mDeviceManufacturer = mDeviceManufacturer;
        thermomether.mDeviceBatteryLevel = mDeviceBatteryLevel;
        return thermomether;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        BLEService.mServiceContext.sendBroadcast(intent);

    }

    public void broadcastUpdate(final String action,
                                final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        //final float temperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
//        String temp = String.valueOf(temperature);
        //intent.putExtra(BLEService.EXTRA_DATA);
        BLEService.mServiceContext.sendBroadcast(intent);

    }

    //
    public String getmDeviceSerialNumber() {
//        if (mDeviceSerialNumber==null && mConnectionState==BLEService.STATE_CONNECTED){
//            readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE,RelsibBluetoothProfile.SERIAL_NUMBER_UUID);
//        }
        return mDeviceSerialNumber;
    }

    public void setmDeviceSerialNumber(String mDeviceSerialNumber) {
        this.mDeviceSerialNumber = mDeviceSerialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartThermometer that = (SmartThermometer) o;
        return mDeviceMacAddress.equals(that.mDeviceMacAddress);
    }

    @Override
    public int hashCode() {
        return mDeviceMacAddress.hashCode();
    }

    public boolean connect(boolean autoConnect) {

        if (BLEService.mBluetoothAdapter == null || mDeviceMacAddress == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        //Previously connected device.  Try to reconnect.
        if (mDeviceMacAddress != null //&& address.equals(mDeviceMacAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = BLEService.STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = BLEService.mBluetoothAdapter.getRemoteDevice(mDeviceMacAddress); //address
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            mConnectionState = BLEService.STATE_DISCONNECTED;
            return false;
        }

//         We want to directly connect to the device, so we are setting the autoConnect
//         parameter to false.
        mBluetoothGatt = device.connectGatt(BLEService.mServiceContext, autoConnect, mGattCallback);
        if (mBluetoothGatt == null) return false;
        Log.e(TAG, "Trying to create a new connection.");

        mConnectionState = BLEService.STATE_CONNECTED;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {

        if (BLEService.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        broadcastUpdate(BLEService.ACTION_DATA_AVAILABLE);
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        Log.e(TAG, "KILL connection");
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
//        if (isNotifyEnabled) {
//            getTemperatureByNotify(false);
//        }
        if (BLEService.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or gatt null");
            return;
        }

        BluetoothGattService mCustomService = mBluetoothGatt.getService(serviceUUID);
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(characteristicUUID);

        if (mReadCharacteristic == null) {
            Log.e(TAG, "READ_CHAR_NULL" + mDeviceMacAddress);
            return;
        }
        characteristicReadQueue.add(mReadCharacteristic);

        //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
        //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
        if ((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0))
            mBluetoothGatt.readCharacteristic(mReadCharacteristic);


    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (BLEService.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                RelsibBluetoothProfile.CLIENT_CHARACTERISTIC_CONFIG);
        if (enabled) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        isNotifyEnabled = enabled;
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    public boolean getTemperatureByNotify(boolean enabled) {

        setCharacteristicNotify(RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE, RelsibBluetoothProfile.INTERMEDIATE_TEMPERATURE, enabled);
//        BLEService.tableThermometers.save(this);
//        Log.e(TAG, "NOTIFY_CUSTOM " + mDeviceMacAddress);
//        if (BLEService.mBluetoothAdapter == null) {
//            Log.e(TAG, "BluetoothAdapter not initialized");
//            return false;
//        }
//        /*check if the service is available on the device*/
//        if (mBluetoothGatt == null) {
//            Log.e(TAG, "GATT FAILED");
//            return false;
//        }
//
//        BluetoothGattService mCustomService = mBluetoothGatt.getService(RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE);
//        if (mCustomService == null) {
//            Log.e(TAG, "NOTIFY_FAILED " + mDeviceMacAddress);
//            return false;
//        }
//        BluetoothGattCharacteristic characteristic = mCustomService.getCharacteristic(RelsibBluetoothProfile.INTERMEDIATE_TEMPERATURE);
//        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                RelsibBluetoothProfile.CLIENT_CHARACTERISTIC_CONFIG);
//        if (enabled) {
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        } else {
//            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
//        }
//        isNotifyEnabled = enabled;
//        mBluetoothGatt.writeDescriptor(descriptor);
        return true;
    }
    public boolean setCharacteristicNotify(UUID mServiceName, UUID mCharacteristicName, boolean enabled) {

        Log.e(TAG, "NOTIFY_CUSTOM " + mDeviceMacAddress);
        if (BLEService.mBluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        /*check if the service is available on the device*/
        if (mBluetoothGatt == null) {
            Log.e(TAG, "GATT FAILED");
            return false;
        }

        BluetoothGattService mCustomService = mBluetoothGatt.getService(mServiceName);
        if (mCustomService == null) {
            Log.e(TAG, "NOTIFY_FAILED " + mDeviceMacAddress);
            return false;
        }
        BluetoothGattCharacteristic characteristic = mCustomService.getCharacteristic(mCharacteristicName);
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                RelsibBluetoothProfile.CLIENT_CHARACTERISTIC_CONFIG);
        if (enabled) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        isNotifyEnabled = enabled;
        mBluetoothGatt.writeDescriptor(descriptor);

        return true;
    }
    public void readInfoTypes() {

        readCharacteristic(RelsibBluetoothProfile.GENERIC_ACCESS_SERVICE, RelsibBluetoothProfile.DEVICE_NAME);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.MODEL_NUMBER_UUID);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.SERIAL_NUMBER_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.FIRMWARE_REVISION_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.HARDWARE_REVISION_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.SOFTWARE_REVISION_UUID);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.MANUFACTURER_NAME_UUID);
        readCharacteristic(RelsibBluetoothProfile.BATTERY_SERVICE, RelsibBluetoothProfile.BATTERY_LEVEL);
    }

    public Long getId() {
        return _ID;
    }

    public void setId(Long _ID) {
        this._ID = _ID;
    }
}