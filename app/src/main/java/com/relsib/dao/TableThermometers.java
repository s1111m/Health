package com.relsib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.relsib.application.SmartThermometer;

import java.util.ArrayList;

/**
 * Created by S1M on 11.09.2016.
 */
public class TableThermometers implements BaseColumns {
    static final String TABLE_NAME = "thermometers";
    private static final String TAG = TableThermometers.class.getSimpleName();
    private static final long DEFAULT_PROFILE_ID = 1;
    private static final String COLUMN_DEVICE_NAME = "device_name";
    private static final String COLUMN_DEVICE_MAC = "device_mac";
    private static final String COLUMN_DEVICE_MODEL = "device_model";
    private static final String COLUMN_DEVICE_SERIAL = "device_serial";
    private static final String COLUMN_DEVICE_FIRMWARE = "device_firmware";
    private static final String COLUMN_DEVICE_HARDWARE = "device_hardware";
    private static final String COLUMN_DEVICE_SOFTWARE = "device_software";
    private static final String COLUMN_DEVICE_MANUFACTURER = "device_manufaturer";
    private static final String COLUMN_DEVICE_BATTERY = "device_battery";

    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_DEVICE_NAME + " TEXT  , "
                    + COLUMN_DEVICE_MAC + " TEXT  , "
                    + COLUMN_DEVICE_MODEL + " TEXT  , "
                    + COLUMN_DEVICE_SERIAL + " TEXT  , "
                    + COLUMN_DEVICE_FIRMWARE + " TEXT , "
                    + COLUMN_DEVICE_HARDWARE + " TEXT , "
                    + COLUMN_DEVICE_SOFTWARE + " TEXT  , "
                    + COLUMN_DEVICE_MANUFACTURER + " TEXT  , "
                    + COLUMN_DEVICE_BATTERY + " INT"
                    + ");";

    private static SQLiteDatabase db; //link to open database

    public TableThermometers(SQLiteDatabase _db) {
        db = _db;
    }


    public void save(SmartThermometer thermometer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEVICE_NAME, thermometer.mDeviceName);
        contentValues.put(COLUMN_DEVICE_MAC, thermometer.mDeviceMacAddress);
        contentValues.put(COLUMN_DEVICE_MODEL, thermometer.mDeviceModelNumber);
        contentValues.put(COLUMN_DEVICE_SERIAL, thermometer.mDeviceSerialNumber);
        contentValues.put(COLUMN_DEVICE_FIRMWARE, thermometer.mDeviceFirmwareRevisionNumber);
        contentValues.put(COLUMN_DEVICE_HARDWARE, thermometer.mDeviceHardwareRevisionNumber);
        contentValues.put(COLUMN_DEVICE_SOFTWARE, thermometer.mDeviceSoftwareRevisionNumber);
        contentValues.put(COLUMN_DEVICE_MANUFACTURER, thermometer.mDeviceManufacturer);
        contentValues.put(COLUMN_DEVICE_BATTERY, thermometer.mDeviceBatteryLevel);
        Log.e(TAG, "pre_insert");
        if (thermometer._ID == DbModel.UNSAVED_ID) {
            Log.e(TAG, "insert");
            thermometer.setId(db.insert(TABLE_NAME, null, contentValues));
        } else
            db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{thermometer.getId().toString()});
    }

    public void createTable() {
        db.execSQL(DATABASE_CREATE);
    }

    public void addThermometer(SmartThermometer thermometer) {
        save(thermometer);
    }

    public boolean deleteRecord(SmartThermometer toDelete) {
        int result = db.delete(TABLE_NAME, "id = ? ", new String[]{toDelete.getId().toString()});
        return (result != 0);
    }

    public void updateRecord(SmartThermometer toUpdate) {
        save(toUpdate);
    }

    public ArrayList<String> getAllRecords() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(
                    res.getString(res.getColumnIndex(_ID))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_NAME))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_MAC))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_MODEL))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_SERIAL))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_FIRMWARE))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_HARDWARE))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_SOFTWARE))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DEVICE_MANUFACTURER))
                            + " " + res.getInt(res.getColumnIndex(COLUMN_DEVICE_BATTERY))
            );
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<SmartThermometer> getAllRecordsAsObjects() {
        ArrayList<SmartThermometer> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            Log.e(TAG, res.getLong(res.getColumnIndex(_ID)) + res.getString(res.getColumnIndex(COLUMN_DEVICE_NAME)));
            array_list.add(SmartThermometer.SmartThermometerFactory(
                    res.getLong(res.getColumnIndex(_ID)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_MAC)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_MODEL)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_SERIAL)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_FIRMWARE)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_HARDWARE)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_SOFTWARE)),
                    res.getString(res.getColumnIndex(COLUMN_DEVICE_MANUFACTURER)),
                    res.getInt(res.getColumnIndex(COLUMN_DEVICE_BATTERY))
            ));
            res.moveToNext();
        }
        return array_list;
    }
    public void clear(){
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM");
    }
    public Cursor makeCursor() {
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
}



