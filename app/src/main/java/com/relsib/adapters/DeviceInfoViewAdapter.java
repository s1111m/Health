package com.relsib.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.relsib.application.BLEService;
import com.relsib.application.R;
import com.relsib.application.SmartThermometer;
import com.relsib.bluetooth.RelsibBluetoothProfile;


public class DeviceInfoViewAdapter extends RecyclerView.Adapter<DeviceInfoViewAdapter.MyViewHolder> {
    SmartThermometer thermometer;

    public DeviceInfoViewAdapter(String deviceMacAddress) {
        thermometer=BLEService.findThermometerByMac(deviceMacAddress);
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        Log.e("BLEADAPTER", "find  " + device.getAddress());
        //  this.service.addUnknownSmartThermometer(device, rssi);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_bledevices_listview_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


    }

    @Override
    public int getItemCount() {
        return (thermometer==null)?0:1;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mDeviceName;
        TextView mDeviceModelNumber;
        TextView mDeviceSerialNumber;
        TextView mDeviceManufacturer;
        TextView mDeviceBatteryLevel;
        TextView mDeviceRssi;

        /*
        readCharacteristic(RelsibBluetoothProfile.GENERIC_ACCESS_SERVICE, RelsibBluetoothProfile.DEVICE_NAME);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.MODEL_NUMBER_UUID);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.SERIAL_NUMBER_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.FIRMWARE_REVISION_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.HARDWARE_REVISION_UUID);
        //readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.SOFTWARE_REVISION_UUID);
        readCharacteristic(RelsibBluetoothProfile.DEVICE_INFORMATION_SERVICE, RelsibBluetoothProfile.MANUFACTURER_NAME_UUID);
        readCharacteristic(RelsibBluetoothProfile.BATTERY_SERVICE, RelsibBluetoothProfile.BATTERY_LEVEL);
        */

        public MyViewHolder(View itemView) {
            super(itemView);

        }
    }
}
