package com.relsib.adapters;

import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.relsib.application.BLEService;
import com.relsib.application.MyDevicesSingleView;
import com.relsib.application.R;
import com.relsib.application.SettingsViewCommon;
import com.relsib.application.SmartThermometer;

/**
 * Created by sim on 17.10.2016.
 */

public class MyDevicesSingleViewAdapter extends RecyclerView.Adapter<MyDevicesSingleViewAdapter.MyViewHolder> {
    private static final String TAG = MyDevicesSingleView.class.getSimpleName();
    SmartThermometer thermometer = null;

    public MyDevicesSingleViewAdapter(String mDeviceMac) {
        //Log.e(TAG,"search in adapter " + mDeviceMac);

        this.thermometer = BLEService.findThermometerByMac(mDeviceMac);

    }

    @Override
    public MyDevicesSingleViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        //тут можно изменить создание холдера
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_mydevices_singleview_item, parent, false);


        return new MyDevicesSingleViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyDevicesSingleViewAdapter.MyViewHolder holder, final int listPosition) {
        if (thermometer != null) {

            holder.textViewName.setText(thermometer.mDeviceName);
            holder.textViewSerial.setText("SN: " + thermometer.mDeviceSerialNumber);
            holder.battery_level.setProgress(thermometer.mDeviceBatteryLevel);
            holder.itemView.setBackgroundColor(thermometer.mDeviceBackgroundColor);
            ((GradientDrawable) holder.color_label.getBackground()).setColor(thermometer.mDeviceColorLabel);
            if (thermometer.mConnectionState == BLEService.STATE_CONNECTED && thermometer.isNotifyEnabled) {
                holder.chronometer.setBase(thermometer.measureTime);
                holder.chronometer.start();
            } else {
                // Log.e(TAG,thermometer.mDeviceMacAddress + " " +String.valueOf(thermometer.mConnectionState) + " - state isnotifyenabled = " + thermometer.isNotifyEnabled);
                //  Log.e(TAG, String.valueOf(thermometer.measureTime) + " " +String.valueOf(SystemClock.elapsedRealtime()));
                holder.chronometer.setBase(SystemClock.elapsedRealtime()); //saved basetime
            }
            if (thermometer.intermediateTemperature != 1000f && thermometer.mConnectionState == BLEService.STATE_CONNECTED) {
                holder.textViewIntermediateTemperature.setText(String.valueOf(thermometer.intermediateTemperature) + " " + thermometer.mDeviceMeasureUnits);
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(900); //You can manage the time of the blink with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                if (thermometer.isBlinking){
                    //  holder.textViewIntermediateTemperature.clearAnimation(); // cancel blink animation
                    holder.textViewIntermediateTemperature.startAnimation(anim);
                } else {
                    holder.textViewIntermediateTemperature.clearAnimation(); // cancel blink animation
                    //holder.textViewIntermediateTemperature.setAlpha(1.0f); // restore original alpha
                }
            }
            else holder.textViewIntermediateTemperature.setText("-,-");

            if (thermometer.maxTemperature != -1000f)
                holder.textViewMaximumTemperature.setText(String.valueOf(thermometer.maxTemperature) + " " + thermometer.mDeviceMeasureUnits);
            else holder.textViewMaximumTemperature.setText("-,-");

            if (thermometer.minTemperature != 1000f)
                holder.textViewMinimumTemperature.setText(String.valueOf(thermometer.minTemperature) + " " + thermometer.mDeviceMeasureUnits);
            else holder.textViewMaximumTemperature.setText("-,-");

            holder.btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, SettingsViewCommon.newInstance(thermometer.mDeviceMacAddress)).commit();
                }
            });
            holder.btnShutdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                            holder.chronometer.stop();
                            holder.chronometer.setBase(SystemClock.elapsedRealtime());
                    thermometer.measureTime = holder.chronometer.getBase(); //save basetime when disconnect
                            thermometer.shutdown();
                            holder.textViewIntermediateTemperature.setText("-,-");
                }
            });
            holder.btnReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    thermometer.resetValues();
                    holder.textViewMaximumTemperature.setText("-,-");
                    holder.textViewIntermediateTemperature.setText("-,-");
                    holder.textViewMinimumTemperature.setText("-,-");
                    holder.chronometer.setBase(thermometer.measureTime);
                }
            });

//            holder.topToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    int id = menuItem.getItemId();
//                    switch (id) {
//                        case R.id.action_disconnect:
//                            holder.chronometer.stop();
//                            holder.chronometer.setBase(SystemClock.elapsedRealtime());
//                            //      thermometer.measureTime = holder.chronometer.getBase(); //save basetime when disconnect
//                            thermometer.shutdown();
//                            holder.textViewIntermediateTemperature.setText("-,-");
//                            break;
//                        case R.id.action_reset:
//                            thermometer.resetValues();
//                            holder.textViewMaximumTemperature.setText("-,-");
//                            holder.textViewIntermediateTemperature.setText("-,-");
//                            holder.textViewMinimumTemperature.setText("-,-");
//                            holder.chronometer.setBase(thermometer.measureTime);
//                            break;
//                        case R.id.action_delete:
//                            BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, MyDevicesListView.newInstance("tesy", "test")).commit();
//                            BLEService.removeThermometer(thermometer);
//                            break;
//
//                        default:
//
//                            break;
//                    }
//                    return true;
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        // if (service.thermometers!=null)
        return 1;
        // else return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(String macAddress);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewSerial;
        TextView textViewIntermediateTemperature;
        TextView textViewMaximumTemperature;
        TextView textViewMinimumTemperature;
        com.github.lzyzsd.circleprogress.DonutProgress battery_level;
        //TextView textDeviceSerial;
        // Toolbar topToolBar;
        Chronometer chronometer;
        ImageButton btnSettings;
        ImageButton btnReload;
        ImageButton btnShutdown;
        ImageView color_label;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewSerial = (TextView) itemView.findViewById(R.id.textDeviceSerial);
            this.textViewIntermediateTemperature = (TextView) itemView.findViewById(R.id.intermediateTemperature);
            this.textViewMaximumTemperature = (TextView) itemView.findViewById(R.id.MaximumTemperature);
            this.textViewMinimumTemperature = (TextView) itemView.findViewById(R.id.minimumTemperature);
            this.battery_level = (com.github.lzyzsd.circleprogress.DonutProgress) itemView.findViewById(R.id.device_battery);
            this.chronometer = (Chronometer) itemView.findViewById(R.id.chronometer);
            this.btnSettings = (ImageButton) itemView.findViewById(R.id.btnSettings);
            this.btnReload = (ImageButton) itemView.findViewById(R.id.btnReload);
            this.btnShutdown = (ImageButton) itemView.findViewById(R.id.btnShutdown);
            this.color_label = (ImageView) itemView.findViewById(R.id.color_label);
            //topToolBar.inflateMenu(R.menu.card_toolbar_menu);

        }

    }
}