package com.relsib.adapters;


import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.relsib.application.BLEService;
import com.relsib.application.MyDevicesSingleView;
import com.relsib.application.R;
import com.relsib.application.SettingsViewCommon;
import com.relsib.application.SmartThermometer;


public class MyDevicesListViewAdapter extends RecyclerView.Adapter<MyDevicesListViewAdapter.MyViewHolder>
    //    implements ItemTouchHelperAdapter
{
    OnItemClickListener mListener;
    //SmartThermometer tempThermometer;
    BLEService service;
   // private final OnStartDragListener mDragStartListener;

    public MyDevicesListViewAdapter(BLEService service) {
        this.service = service;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        //тут можно изменить создание холдера
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_mydevices_view_item, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        final SmartThermometer tempThermometer = BLEService.thermometers.get(listPosition);
        holder.textViewName.setText(tempThermometer.mDeviceName);
        holder.textViewMac.setText(tempThermometer.mDeviceMacAddress);

        holder.battery_level.setProgress(tempThermometer.mDeviceBatteryLevel);
        //holder.deviceBatteryText.setText(tempThermometer.mDeviceBatteryLevel + "%");

//        holder.textDeviceSerial.setText("SN: " + tempThermometer.getmDeviceSerialNumber());

        holder.itemView.setBackgroundColor(tempThermometer.mDeviceBackgroundColor);
        holder.textViewName.setTextColor(tempThermometer.mDeviceColorLabel);
        //holder.topToolBar.setTitle(tempThermometer.mDeviceName);// + " SN:" + tempThermometer.mDeviceSerialNumber);
        // Log.e(TAG, "measure time " + String.valueOf(tempThermometer.measureTime));
        //  if (tempThermometer.measureTime != -1) {// if  gettempbynotify set's timer

        if (tempThermometer.mConnectionState == BLEService.STATE_CONNECTED && tempThermometer.isNotifyEnabled) {
            holder.chronometer.setBase(tempThermometer.measureTime);
            holder.chronometer.start();
        } else {
            // Log.e(TAG,tempThermometer.mDeviceMacAddress + " " +String.valueOf(tempThermometer.mConnectionState) + " - state isnotifyenabled = " + tempThermometer.isNotifyEnabled);
            //  Log.e(TAG, String.valueOf(tempThermometer.measureTime) + " " +String.valueOf(SystemClock.elapsedRealtime()));
            holder.chronometer.setBase(SystemClock.elapsedRealtime()); //saved basetime
        }

        //  }

        tempThermometer.setAdapterPosition(holder.getAdapterPosition());

        if (tempThermometer.intermediateTemperature != 1000f && tempThermometer.mConnectionState != BLEService.STATE_DISCONNECTED)
            holder.textViewIntermediateTemperature.setText(String.valueOf(tempThermometer.intermediateTemperature) + " " + tempThermometer.mDeviceMeasureUnits);
        else holder.textViewIntermediateTemperature.setText("-,-");

        if (tempThermometer.maxTemperature != -1000f)
            holder.textViewMaximumTemperature.setText(String.valueOf(tempThermometer.maxTemperature) + " " + tempThermometer.mDeviceMeasureUnits);
        else holder.textViewMaximumTemperature.setText("-,-");

        if (tempThermometer.minTemperature != 1000f)
            holder.textViewMinimumTemperature.setText(String.valueOf(tempThermometer.minTemperature) + " " + tempThermometer.mDeviceMeasureUnits);
        else holder.textViewMaximumTemperature.setText("-,-");

        holder.mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, SettingsViewCommon.newInstance(tempThermometer.mDeviceMacAddress)).commit();
            }
        });
        holder.topToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_disconnect:
                        holder.chronometer.stop();
                        holder.chronometer.setBase(SystemClock.elapsedRealtime());
                        //      tempThermometer.measureTime = holder.chronometer.getBase(); //save basetime when disconnect
                        tempThermometer.shutdown();
                        holder.textViewIntermediateTemperature.setText("-,-");
                        break;
                    case R.id.action_reset:
                        tempThermometer.resetValues();
                        holder.textViewMaximumTemperature.setText("-,-");
                        holder.textViewIntermediateTemperature.setText("-,-");
                        holder.textViewMinimumTemperature.setText("-,-");
                        holder.chronometer.setBase(tempThermometer.measureTime);
                        break;
                    case R.id.action_delete:
                        BLEService.removeThermometer(tempThermometer);
                        break;
                    default:

                        break;
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e(TAG, "onClick: " + tempThermometer.mDeviceMacAddress );
                BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, MyDevicesSingleView.newInstance(tempThermometer.mDeviceMacAddress, "1")).commit();
                //(BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, MyDevicesSingleView.newInstance(tempThermometer.mDeviceMacAddress,"1")).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (BLEService.thermometers != null)
        return BLEService.thermometers.size();
        else return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(String macAddress);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewMac;
        TextView textViewIntermediateTemperature;
        TextView textViewMaximumTemperature;
        TextView textViewMinimumTemperature;
        com.github.lzyzsd.circleprogress.DonutProgress battery_level;
        TextView textDeviceSerial;
        Toolbar topToolBar;
        Chronometer chronometer;
        //TextView deviceBatteryText;
        ImageButton mSettingsButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewMac = (TextView) itemView.findViewById(R.id.textViewMac);
            this.textViewIntermediateTemperature = (TextView) itemView.findViewById(R.id.intermediateTemperature);
            this.textViewMaximumTemperature = (TextView) itemView.findViewById(R.id.MaximumTemperature);
            this.textViewMinimumTemperature = (TextView) itemView.findViewById(R.id.minimumTemperature);
            this.battery_level = (com.github.lzyzsd.circleprogress.DonutProgress) itemView.findViewById(R.id.device_battery);
            // this.textDeviceSerial = (TextView) itemView.findViewById(R.id.device_serial);
            this.topToolBar = (Toolbar) itemView.findViewById(R.id.card_toolbar);
            this.chronometer = (Chronometer) itemView.findViewById(R.id.chronometer);
            //this.deviceBatteryText = (TextView) itemView.findViewById(R.id.deviceBatteryText);
            this.mSettingsButton = (ImageButton) itemView.findViewById(R.id.settingsButton);
            topToolBar.inflateMenu(R.menu.card_toolbar_menu);

        }

    }
}
