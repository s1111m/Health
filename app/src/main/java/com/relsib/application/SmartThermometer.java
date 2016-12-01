package com.relsib.application;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import com.relsib.bluetooth.RelsibBluetoothProfile;
import com.relsib.dao.DbModel;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by S1M on 11.09.2016.
 */
public class SmartThermometer {
    public static final String ADAPTER_POSITION = "ADAPTER_POSITION";
    public static final String TEMP_MAX = "TEMP_MAX";
    public static final String TEMP_MIN = "TEMP_MIN";
    public static final String TEMP_CURR = "TEMP_CURR";
    private static final String TAG = SmartThermometer.class.getSimpleName();
    public long _ID = DbModel.UNSAVED_ID;
    public int mDeviceRssi = 0;
    public String mDeviceMacAddress;
    public String mDeviceName;
    public String mDeviceModelNumber;
    public String mDeviceSerialNumber;
    public String mDeviceFirmwareRevisionNumber;
    public String mDeviceHardwareRevisionNumber;
    public String mDeviceSoftwareRevisionNumber;
    public String mDeviceManufacturer;
    public String mDeviceMeasureUnits;
    public int mDeviceBatteryLevel;
    public long measureTime;// = -1;
    public int mDeviceColorLabel = Color.WHITE;
    public int mDeviceBackgroundColor = Color.WHITE;
    public float intermediateTemperature = 1000f;
    public float maxTemperature = -1000f;
    public float minTemperature = 1000f;
    public boolean selected = false;
    public boolean autoconnect = true;
    public int mConnectionState = BLEService.STATE_DISCONNECTED;
    public boolean isNotifyEnabled = false;
    public float minAlarm = -1000f;
    public float maxAlarmTemperature = -1000f;
    public boolean minAlarmEnabled;
    public boolean maxAlarmEnabled;
    public boolean minAlarmVibrateEnabled;
    public boolean maxAlarmVibrareEnabled;
    public boolean minAlarmGraphicEnabled;
    public boolean maxAlarmGraphicEnabled;
    private final Vibrator vibroService = ((Vibrator) BLEService.mServiceContext.getSystemService(BLEService.VIBRATOR_SERVICE));
    SharedPreferences preferences;
    private long adapterPosition;
    Ringtone maxRingtone;
    Ringtone minRingtone;
    private BluetoothGatt mBluetoothGatt;
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedList<BluetoothGattCharacteristic>();
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            Log.e(TAG, "State changed  " + newState);
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
                intermediateTemperature = 1000f;
                mDeviceRssi = 0;
                isNotifyEnabled = false;
                Log.e(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Services discovered");
                getTemperatureByNotify(true);
                broadcastUpdate(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
                if (mDeviceSerialNumber == null) { //
                    readInfoTypes();
                } //else {
                    // broadcastUpdate(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
                //getTemperatureByNotify(true);
                    //getBatteryByNotify(true);
                //readInfoTypes();
                getBatteryByNotify(true);
                // }
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
                disconnect();
                //connect(true);
                //mBluetoothGatt.discoverServices();
                // disconnect();
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
                if (characteristicReadQueue.size() > 0)
                    mBluetoothGatt.readCharacteristic(characteristicReadQueue.element());

                if (characteristicReadQueue.size() == 0) {
                    BLEService.tableThermometers.save(SmartThermometer.this);
                    broadcastUpdate(BLEService.EXTRA_DATA);
                    //getTemperatureByNotify(true);
                    //getBatteryByNotify(true);
                }

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e(TAG, "onChange ");
            if (uuid.equals(RelsibBluetoothProfile.INTERMEDIATE_TEMPERATURE)) {
                intermediateTemperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
                switch (mDeviceMeasureUnits) {
                    case MeasureUnits.Fahrenheit:
                        intermediateTemperature = round(intermediateTemperature * 1.8f + 32f, 1);
                        break;
                    case MeasureUnits.Kelvin:
                        intermediateTemperature = round(intermediateTemperature - 273.15f, 1);
                        break;
                    default:
                        break;
                }
                if (maxAlarmEnabled && intermediateTemperature>=maxAlarmTemperature){
                    //if (maxAlarmEnabled && maxTemperature>=maxAlarmTemperature) {
                        Log.e(TAG,"maxTemperature = " + maxTemperature + " maxAlarmTemperature = " + maxAlarmTemperature);
                        // внести в условие, если есть звук

                        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        ringtoneSound = RingtoneManager.getRingtone(BLEService.mServiceContext, ringtoneUri);
                        if (maxAlarmVibrareEnabled) {
                            Log.e(TAG, "Vibrate");
                            vibroService.vibrate(800);
                        }
                        //if (ringtoneSound != null) {
                        Log.e(TAG, "ringtone");
                        ringtoneSound.play();

                }else if (intermediateTemperature<=minAlarm){

                }
                if (maxTemperature <= intermediateTemperature) {
                    setMaxTemperature(intermediateTemperature);
                 } else if (minTemperature >= intermediateTemperature) {
                    setMinTemperature(intermediateTemperature);

                }
                broadcastUpdate(BLEService.ACTION_DATA_AVAILABLE, characteristic);
                //getBatteryByNotify(true);
            }
            if (uuid.equals(RelsibBluetoothProfile.BATTERY_LEVEL)) {
                mDeviceBatteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.e(TAG, "Battery level + " + mDeviceBatteryLevel);
                BLEService.tableThermometers.save(SmartThermometer.this);
                broadcastUpdate(BLEService.EXTRA_DATA);
            }


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
            } else {
                Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
            }
            descriptorWriteQueue.remove();  //pop the item that we just finishing writing
            //if there is more to write, do it!
            if (descriptorWriteQueue.size() > 0)
                mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
            else if (characteristicReadQueue.size() > 0)
                mBluetoothGatt.readCharacteristic(characteristicReadQueue.element());

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            mDeviceRssi = rssi;
            broadcastUpdate(BLEService.EXTRA_DATA);
        }
    };
    private Uri ringtoneUri;
    private Ringtone ringtoneSound;

    public SmartThermometer(String mDeviceMacAddress, String mDeviceName) {
        this.mDeviceMacAddress = mDeviceMacAddress;
        this.preferences = BLEService.mActivityContext.getSharedPreferences(this.mDeviceMacAddress + SettingsViewCommon.FILE_NAME, MODE_PRIVATE);
        setmDeviceName(preferences.getString(SettingsViewCommon.KEY_NAME, mDeviceName));
        //setmDeviceName(mDeviceName);
    }
    public void initPreferences(){
        setmDeviceColorLabel(preferences.getInt(SettingsViewCommon.KEY_COLOR_LABEL, Color.WHITE));
        setmDeviceBackgroundColor(preferences.getInt(SettingsViewCommon.KEY_BACKGROUND_COLOR, Color.WHITE));
        setmDeviceMeasureUnits(preferences.getString(SettingsViewCommon.KEY_MEASURE_UNITS, MeasureUnits.Celsium));
        minAlarm = preferences.getFloat(SettingsViewCommon.KEY_ALARMS_MIN_VALUE, -20f);
        maxAlarmTemperature = preferences.getFloat(SettingsViewCommon.KEY_ALARMS_MAX_VALUE, 70f);
        minAlarmEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MIN_ENABLED, false);
        maxAlarmEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MAX_ENABLED, false);
        minAlarmVibrateEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MIN_VIBRATE, false);
        maxAlarmVibrareEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MAX_VIBRATE, false);
        minAlarmGraphicEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MIN_GRAPHIC, false);
        maxAlarmGraphicEnabled = preferences.getBoolean(SettingsViewCommon.KEY_ALARMS_MAX_GRAPHIC, false);
        maxRingtone = RingtoneManager.getRingtone(BLEService.mServiceContext,Uri.parse(preferences.getString(SettingsViewCommon.KEY_ALARMS_MAX_SOUND,"default ringtone")));
        minRingtone = RingtoneManager.getRingtone(BLEService.mServiceContext,Uri.parse(preferences.getString(SettingsViewCommon.KEY_ALARMS_MIN_SOUND,"default ringtone")));
    }
    private static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public static SmartThermometer SmartThermometerFactory(long _ID, String mDeviceName, String mDeviceMac, String mDeviceModelNumber, String mDeviceSerialNumber, String mDeviceFirmwareRevisionNumber,
                                                           String mDeviceHardwareRevisionNumber, String mDeviceSoftwareRevisionNumber, String mDeviceManufacturer, int mDeviceBatteryLevel) {
        SmartThermometer thermomether = new SmartThermometer(mDeviceMac, mDeviceName);
        thermomether._ID = _ID;
        thermomether.mDeviceModelNumber = mDeviceModelNumber;
        thermomether.setmDeviceSerialNumber(mDeviceSerialNumber);
        thermomether.mDeviceFirmwareRevisionNumber = mDeviceFirmwareRevisionNumber;
        thermomether.mDeviceHardwareRevisionNumber = mDeviceHardwareRevisionNumber;
        thermomether.mDeviceSoftwareRevisionNumber = mDeviceSoftwareRevisionNumber;
        thermomether.mDeviceManufacturer = mDeviceManufacturer;
        thermomether.mDeviceBatteryLevel = mDeviceBatteryLevel;
        thermomether.initPreferences();
        return thermomether;

    }

    public void refreshRssi() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.readRemoteRssi();
        }
    }

    public String getmDeviceName() {
        if (mDeviceName == null)
            mDeviceName = preferences.getString(SettingsViewCommon.KEY_NAME, "WT-50");
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        Log.e(TAG, "Setting mDeviceName : " + mDeviceName);
        this.mDeviceName = mDeviceName;
    }

    public void changeMeasureUnits(String to) {
        maxTemperature = MeasureUnits.convertMeasureUnits(maxTemperature, mDeviceMeasureUnits, to);
        minTemperature = MeasureUnits.convertMeasureUnits(minTemperature, mDeviceMeasureUnits, to);
        intermediateTemperature = MeasureUnits.convertMeasureUnits(intermediateTemperature, mDeviceMeasureUnits, to);
        minAlarm = MeasureUnits.convertMeasureUnits(minAlarm, mDeviceMeasureUnits, to);
        maxAlarmTemperature = MeasureUnits.convertMeasureUnits(maxAlarmTemperature, mDeviceMeasureUnits, to);

        broadcastUpdate(BLEService.EXTRA_DATA);
    }

    public void setmDeviceMeasureUnits(String mDeviceMeasureUnits) {
        Log.e(TAG, "Call change units current: " + this.mDeviceMeasureUnits + " to: " + mDeviceMeasureUnits);
        if (this.mDeviceMeasureUnits != null && !this.mDeviceMeasureUnits.equals(mDeviceMeasureUnits)) {
            Log.e(TAG, "Call change units======================================");
            changeMeasureUnits(mDeviceMeasureUnits);
        }
        this.mDeviceMeasureUnits = mDeviceMeasureUnits;
    }

    public void setmDeviceColorLabel(Integer mDeviceColorLabel) {

        this.mDeviceColorLabel = mDeviceColorLabel;
    }

    public void setmDeviceBackgroundColor(Integer mDeviceBackgroundColor) {
        Log.e(TAG, mDeviceName);
        this.mDeviceBackgroundColor = mDeviceBackgroundColor;
    }

    public void setMaxTemperature(Float maxTemperature) {
        this.maxTemperature = maxTemperature;
//        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        ringtoneSound = RingtoneManager.getRingtone(BLEService.mServiceContext, ringtoneUri);
//        Log.e(TAG,"ENTER SET MAX");
//        if (maxAlarmEnabled && this.maxTemperature >= this.maxAlarmTemperature) {
//            Log.e(TAG,"condition 1");
//            if (maxAlarmVibrareEnabled) {
//                Log.e(TAG,"Vibrate");
//                ((Vibrator) BLEService.mServiceContext.getSystemService(BLEService.VIBRATOR_SERVICE)).vibrate(800);
//            }
//            //if (ringtoneSound != null) {
//            Log.e(TAG,"ringtone");
//                ringtoneSound.play();
//            //}
//        } else {
//            ringtoneSound.stop();
  //      }
    }

    public void setMinTemperature(Float minTemperature) {
        this.minTemperature = minTemperature;
//        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        ringtoneSound = RingtoneManager.getRingtone(BLEService.mServiceContext, ringtoneUri);
//        if (minAlarmEnabled && minTemperature <= minAlarm) {
//            if (minAlarmVibrateEnabled) {
//                ((Vibrator) BLEService.mServiceContext.getSystemService(BLEService.VIBRATOR_SERVICE)).vibrate(800);
//            }
//            if (ringtoneSound != null) {
//                ringtoneSound.play();
//            }
//        } else {
//            ringtoneSound.stop();
//        }
        //Log.e(TAG, " setting " + maxTemperature);
    }

    public long getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(long adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public void resetValues() {
        setMaxTemperature(-1000F);
        setMinTemperature(1000F);
        intermediateTemperature = 1000f;
        measureTime = SystemClock.elapsedRealtime();

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        intent.putExtra(ADAPTER_POSITION, adapterPosition);
        //intent.putExtra(TEMP_CURR,intermediateTemperature);
        //intent.putExtra(TEMP_MAX,maxTemperature);
        // intent.putExtra(TEMP_MIN,minTemperature);
        BLEService.mActivityContext.sendBroadcast(intent);

    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        //     Log.e(TAG, "mac " + " SENDING adapter position  " + adapterPosition);
        //intent.putExtra(ADAPTER_POSITION, adapterPosition);
        //intent.putExtra(TEMP_CURR, intermediateTemperature);
        //intent.putExtra(TEMP_MAX, maxTemperature);
        //intent.putExtra(TEMP_MIN, minTemperature);
        BLEService.mActivityContext.sendBroadcast(intent);

    }

    //
    public String getmDeviceSerialNumber() {
        return mDeviceSerialNumber;
    }

    private void setmDeviceSerialNumber(String mDeviceSerialNumber) {
        this.mDeviceSerialNumber = mDeviceSerialNumber;
        //setmDeviceName(preferences.getString(SettingsViewCommon.KEY_NAME, "WT-50"));
        setmDeviceColorLabel(preferences.getInt(SettingsViewCommon.KEY_COLOR_LABEL, Color.WHITE));
        setmDeviceBackgroundColor(preferences.getInt(SettingsViewCommon.KEY_BACKGROUND_COLOR, Color.WHITE));
        setmDeviceMeasureUnits(preferences.getString(SettingsViewCommon.KEY_MEASURE_UNITS, MeasureUnits.Celsium));
        //minAlarm = preferences.getFloat(SettingsViewCommon.KEY_ALARMS_MIN_VALUE, -20f);
        //maxAlarmTemperature = preferences.getFloat(SettingsViewCommon.KEY_ALARMS_MAX_VALUE, 70f);
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

        Log.e(TAG, "start connecting");

        if (BLEService.mBluetoothAdapter == null || mDeviceMacAddress == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        //Previously connected device.  Try to reconnect.
        if (mDeviceMacAddress != null // && address.equals(mDeviceMacAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = BLEService.STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        BluetoothDevice device = BLEService.mBluetoothAdapter.getRemoteDevice(mDeviceMacAddress); //address
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            mConnectionState = BLEService.STATE_DISCONNECTED;
            return false;
        }
//         We want to directly connect to the device, so we are setting the autoConnect
//         parameter to false.
        mBluetoothGatt = device.connectGatt(BLEService.mActivityContext, autoConnect, mGattCallback);
        if (mBluetoothGatt == null) {
            mConnectionState = BLEService.STATE_DISCONNECTED;
            return false;

        }
        Log.e(TAG, "Trying to create a new connection.");

        mConnectionState = BLEService.STATE_CONNECTING;
        return true;
    }

    public void disconnect() {

        if (BLEService.mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        intermediateTemperature = 1000f;
        broadcastUpdate(BLEService.ACTION_GATT_DISCONNECTED);
        mBluetoothGatt.disconnect();

    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        //mConnectionState=BLEService.STATE_DISCONNECTED;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        Log.e(TAG, "KILL connection");
    }

    private void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
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
        if ((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0)) {
            mBluetoothGatt.readCharacteristic(mReadCharacteristic);

        }

    }

    private boolean getTemperatureByNotify(boolean enabled) {
        setCharacteristicNotify(RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE, RelsibBluetoothProfile.INTERMEDIATE_TEMPERATURE, enabled);
        measureTime = SystemClock.elapsedRealtime();
        isNotifyEnabled = enabled;

        return true;
    }

    private void getBatteryByNotify(boolean enabled) {
        setCharacteristicNotify(RelsibBluetoothProfile.BATTERY_SERVICE, RelsibBluetoothProfile.BATTERY_LEVEL, enabled);
        Log.e(TAG, "call get battery by notify + isnotify " + enabled);
    }

    private boolean setCharacteristicNotify(UUID mServiceName, UUID mCharacteristicName, boolean enabled) {

        Log.e(TAG, "CALL setCharacteristicNotify " + mCharacteristicName.toString() + " " + mDeviceMacAddress);
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
        /*****/
        descriptorWriteQueue.add(descriptor);
        //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
        //GIVE PRECEDENCE to descriptor writes.  They must all finish first.

        if (((descriptorWriteQueue.size() == 1) && characteristicReadQueue.size() == 0)) {
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        //mBluetoothGatt.writeDescriptor(descriptor);
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

    public void shutdown() {
        intermediateTemperature = 1000f;
        mConnectionState = BLEService.STATE_DISCONNECTED;
        isNotifyEnabled = false;
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = mBluetoothGatt.getService(RelsibBluetoothProfile.RELSIBPROFILE_SERV);
        if (mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(RelsibBluetoothProfile.RELSIBPROFILE_SHUTDOWN);
        mWriteCharacteristic.setValue(RelsibBluetoothProfile.SHUTDOWN_THERMOMETER, android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        if (!mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
            Log.w(TAG, "Failed to write characteristic");
        }

    }

    public static class MeasureUnits {
        public final static String Celsium = "°C";
        public final static String Fahrenheit = "°F";
        public final static String Kelvin = "K";

        public static float convertMeasureUnits(float valueFrom, String fromMeasureUnits, String toMeasureUnits) {
            if (valueFrom == 1000f || valueFrom == -1000f || fromMeasureUnits.equals(toMeasureUnits))
                return valueFrom;
            switch (fromMeasureUnits) {
                case MeasureUnits.Celsium:
                    //   Log.e(TAG, "from celsium");
                    if (toMeasureUnits.equals(MeasureUnits.Fahrenheit)) {
                        //     Log.e(TAG, "to fahrengheit");
                        return round(valueFrom * 1.8f + 32f, 1);
                    } else if (toMeasureUnits.equals(MeasureUnits.Kelvin)) {
                        //   Log.e(TAG, "to kelvin");
                        return round(valueFrom - 273.15f, 1);
                    }
                    //   return valueFrom;

                case MeasureUnits.Fahrenheit:
                    //Log.e(TAG, "from fahr");
                    if (toMeasureUnits.equals(MeasureUnits.Celsium)) {
                        //  Log.e(TAG, "to celsium");
                        return round((valueFrom - 32) * 5 / 9, 1);
                    } else if (toMeasureUnits.equals(MeasureUnits.Kelvin)) {
                        return round((valueFrom - 32) * 5 / 9 - 273.15f, 1);
                    }
                    //  return valueFrom;
                case MeasureUnits.Kelvin:
                    //Log.e(TAG, "kelvin");
                    if (toMeasureUnits.equals(MeasureUnits.Celsium)) {
                        //  Log.e(TAG, "to celsium");
                        return round(valueFrom + 273.15f, 1);
                    } else if (toMeasureUnits.equals(MeasureUnits.Fahrenheit)) {
                        return round((valueFrom + 273.15f) * 9 / 5 + 32f, 1);
                    }
                    //   return valueFrom;
                default:
                    return valueFrom;
            }
        }
    }
}