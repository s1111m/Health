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


public class DeviceInfoViewAdapter extends RecyclerView.Adapter<DeviceInfoViewAdapter.MyViewHolder> {
    String[][] arr = new String[4][2];

    public DeviceInfoViewAdapter(BLEService service) {
        //this.service = service;
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

        SmartThermometer currentThermometer = BLEService.unknownThermometers.get(listPosition);
        holder.mDeviceName.setText(currentThermometer.mDeviceName);
        holder.mDeviceMac.setText(currentThermometer.mDeviceMacAddress);
        holder.mDeviceSerialNumber.setText(currentThermometer.getmDeviceSerialNumber());
        holder.mDeviceRssi.setText(String.valueOf(currentThermometer.mDeviceRssi) + " " + "dB");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mDeviceSelected.setChecked(!holder.mDeviceSelected.isChecked());
                BLEService.unknownThermometers.get(listPosition).selected = holder.mDeviceSelected.isChecked();
            }

        });
        holder.mDeviceSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BLEService.unknownThermometers.get(listPosition).selected = holder.mDeviceSelected.isChecked();
            }
        });
    }

    @Override
    public int getItemCount() {
        return BLEService.unknownThermometers.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        // for (int i=0;i<service.unknownThermometers.size();i++){
        //   service.unknownThermometers.get(i).close();
        //  }
        BLEService.unknownThermometers.clear();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mDeviceName;
        TextView mDeviceMac;
        TextView mDeviceSerialNumber;
        CheckBox mDeviceSelected;
        TextView mDeviceRssi;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.mDeviceName = (TextView) itemView.findViewById(R.id.device_name);
            this.mDeviceSerialNumber = (TextView) itemView.findViewById(R.id.device_serial);
            this.mDeviceMac = (TextView) itemView.findViewById(R.id.device_mac);
            this.mDeviceSelected = (CheckBox) itemView.findViewById(R.id.device_selected);
            this.mDeviceRssi = (TextView) itemView.findViewById(R.id.device_rssi);

        }
    }
}
