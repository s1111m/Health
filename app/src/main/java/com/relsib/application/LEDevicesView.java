package com.relsib.application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.relsib.adapters.BLEDevicesViewAdapter;

//import com.relsib.application.dummy.DummyContent;
//import com.relsib.application.dummy.DummyContent.DummyItem;

//import com.relsib.application.dummy.DummyContent;
//import com.relsib.application.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LEDevicesView extends Fragment {
    public static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 3000;
    private final static String TAG = LEDevicesView.class.getSimpleName();
    public static BLEService service;
    private BLEDevicesViewAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    // попытка работать в новом потоке
                    mLeDeviceListAdapter.addDevice(device, rssi);
                }
            };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LEDevicesView() {
    }

    public static void setCallbackService(BLEService mService) {
        service = mService;
    }

    public static LEDevicesView newInstance() {
        LEDevicesView fragment = new LEDevicesView();
        // Bundle args = new Bundle();
        //args.put(ARG_PARAM1,mBluetoothThermometerService);
        //fragment.setArguments(args);
        return fragment;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            //UUID[] services = {RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE};
            //mBluetoothAdapter.startLeScan(services,mLeScanCallback);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mLeDeviceListAdapter.notifyDataSetChanged();
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        //  super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_bledeviceslistview_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.bledevices_listview_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mLeDeviceListAdapter = new BLEDevicesViewAdapter(MainActivityView.mBLEService);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        if (BLEService.unknownThermometers != null) swipeRefreshLayout.setRefreshing(true);
        scanLeDevice(true);
        recyclerView.setAdapter(mLeDeviceListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
                scanLeDevice(true);
            }
        });
        return rootView;
    }

    //on move down restart
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLeDeviceListAdapter = null;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        swipeRefreshLayout.setRefreshing(false);
    }
}

