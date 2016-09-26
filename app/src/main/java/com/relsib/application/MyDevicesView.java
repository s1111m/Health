package com.relsib.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relsib.adapters.MyDevicesViewAdapter;

public class MyDevicesView extends Fragment {
    private final static String TAG = MyDevicesView.class.getSimpleName();
    private static final String ARG_PARAM1 = "data";
    private static final String ARG_PARAM2 = "mDeviceAddress";
    private static BLEService service = null;
    private static MyDevicesViewAdapter adapter;
    private static RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            boolean mConnected = false;
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                adapter.notifyDataSetChanged();
                //invalidateOptionsMenu();
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                adapter.notifyDataSetChanged();
                //invalidateOptionsMenu();
                //clearUI();
            } else if (BLEService.EXTRA_DATA.equals(action)) {
                adapter.notifyDataSetChanged();
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                View itemView = recyclerView.getLayoutManager().findViewByPosition((int) intent.getLongExtra(SmartThermometer.ADAPTER_POSITION, 0));

                if (itemView != null) {
                    ((TextView) itemView.findViewById(R.id.intermediateTemperature)).setText(String.valueOf(intent.getFloatExtra(SmartThermometer.TEMP_CURR, 0)));
                    ((TextView) itemView.findViewById(R.id.MaximumTemperature)).setText(String.valueOf(intent.getFloatExtra(SmartThermometer.TEMP_MAX, 0)));
                }
                // adapter.notifyDataSetChanged();
            }
        }
    };

    public MyDevicesView() {

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.EXTRA_DATA);
        return intentFilter;
    }

    public static MyDevicesView newInstance(String param1, String param2) {
        MyDevicesView fragment = new MyDevicesView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // data =(ArrayList<com.relsib.application.BLEService.SmartThermometer>)getArguments().getSerializable(ARG_PARAM1);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_mydevices_view, container, false);
        adapter = new MyDevicesViewAdapter(MainActivityView.mBLEService);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyDevicesViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String macAddress) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, TabbedView.newInstance(macAddress, macAddress)).commit();
            }
        });


        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    private void updateConnectionState(final int resourceId) {

    }
}