package com.relsib.application;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.relsib.adapters.BLEDevicesViewAdapter;
import com.relsib.bluetooth.RelsibBluetoothProfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceSearchView extends Fragment {
    public static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 1 seconds.
    private static final long SCAN_PERIOD = 1000;
    private final static String TAG = DeviceSearchView.class.getSimpleName();
    UUID[] services = {RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE};
    private BLEDevicesViewAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    // попытка работать в новом потоке

                    List<UUID> uuids = parseUuids(scanRecord);
                    if (uuids.get(0).equals(RelsibBluetoothProfile.HEALTH_THERMOMETER_SERVICE)) {
                        //for (int i=0;i<uuids.size();i++){
                        //  Log.e(TAG,uuids.get(i).toString());
                        //}
                        mLeDeviceListAdapter.addDevice(device, rssi);
                    }
                }
            };

    public DeviceSearchView() {
    }

    public static DeviceSearchView newInstance() {
        DeviceSearchView fragment = new DeviceSearchView();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    private List<UUID> parseUuids(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();


        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }

        return uuids;
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
                    Log.e(TAG, "stopLeScan");
                }
            }, SCAN_PERIOD);
            mScanning = true;
            //   if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // Do something for lollipop and above versions
            //   mBluetoothAdapter.startLeScan(mLeScanCallback);
//            } else {
            //UUID[] services = {UUID.fromString("1809")};
            mBluetoothAdapter.startLeScan(mLeScanCallback);

            Log.e(TAG, "starting scan");
            //  UUID test= new UUID(0x1809);
            //UUID
            //      mBluetoothAdapter.startLeScan(], mLeScanCallback);
//            }


            mLeDeviceListAdapter.notifyDataSetChanged();
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
//public static boolean hasMyService(byte[] scanRecord) {

    // UUID we want to filter by (without hyphens)
    //   final String myServiceID = "0177AAA0B4550E17D0DA14EA33F8DE11";

    // The offset in the scan record. In my case the offset was 13; it will probably be different for you
//    final int serviceOffset = 6;

//    try{

    // Get a 16-byte array of what may or may not be the service we're filtering for
    //  byte[] service = ArrayUtils.subarray(scanRecord, serviceOffset, serviceOffset + 16);

    // The bytes are probably in reverse order, so we need to fix that
//        ArrayUtils.reverse(service);

    // Get the hex string
    //    String discoveredServiceID = bytesToHex(service);

    // Compare against our service
    //   return myServiceID.equals(discoveredServiceID);

    //   } catch (Exception e){
//        return false;
//    }
//
//}
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
        swipeRefreshLayout.setRefreshing(false);
        super.onDetach();

    }
}

