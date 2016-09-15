package com.relsib.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.relsib.application.BLEService;
import com.relsib.application.R;
import com.relsib.application.SmartThermometer;


public class MyDevicesViewAdapter extends RecyclerView.Adapter<MyDevicesViewAdapter.MyViewHolder>
    //    implements ItemTouchHelperAdapter
{
    OnItemClickListener mListener;
    SmartThermometer tempThermometer;
    BLEService service;
   // private final OnStartDragListener mDragStartListener;

    public MyDevicesViewAdapter(BLEService service) {
        this.service = service;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_mydevices_view_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        tempThermometer = BLEService.thermometers.get(listPosition);
        holder.textViewName.setText(tempThermometer.mDeviceName);
        holder.textViewVersion.setText(tempThermometer.mDeviceMacAddress);
        holder.imageViewIcon.setImageResource(R.drawable.ic_launcher);
        holder.battery_level.setProgress(tempThermometer.mDeviceBatteryLevel);
        holder.textDeviceSerial.setText("SN: " + tempThermometer.getmDeviceSerialNumber());


        if (tempThermometer.intermediateTemperature != null)
            holder.textViewIntermediateTemperature.setText(String.valueOf(tempThermometer.intermediateTemperature));

        if (tempThermometer.maxTemperature != -200F)
            holder.textViewMaximumTemperature.setText(String.valueOf(tempThermometer.maxTemperature));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onItemClick(tempThermometer.mDeviceMacAddress);
            }
        });
    }

    @Override
    public int getItemCount() {
        // if (service.thermometers!=null)
        return BLEService.thermometers.size();
        // else return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(String macAddress);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        TextView textViewIntermediateTemperature;
        TextView textViewMaximumTemperature;
        ImageView imageViewIcon;
        ProgressBar battery_level;
        TextView textDeviceSerial;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.textViewIntermediateTemperature = (TextView) itemView.findViewById(R.id.intermediateTemperature);
            this.textViewMaximumTemperature = (TextView) itemView.findViewById(R.id.MaximumTemperature);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.battery_level = (ProgressBar) itemView.findViewById(R.id.device_battery);
            this.textDeviceSerial = (TextView) itemView.findViewById(R.id.device_serial);


        }

    }
}
