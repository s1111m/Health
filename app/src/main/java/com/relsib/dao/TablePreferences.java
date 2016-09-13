package com.relsib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by S1M on 07.08.2016.
 */
public class TablePreferences implements BaseColumns {

    public static final long UNSAVED_ID = -1;
    static final String TABLE_NAME = "preferences";
    private static final long DEFAULT_PROFILE_ID = 1;
    private static final String COLUMN_PROFILE_ID = "profile_id";
    private static final String COLUMN_DEGREES_TYPE = "degrees_type";
    private static final String COLUMN_LAST_DEVICE_NAME = "device_name";
    private static final String COLUMN_LAST_DEVICE_MAC = "device_mac";
    private static final String COLUMN_LAST_DEVICE_AUTOCONNECT = "device_autoconnect";
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PROFILE_ID + " INTEGER NOT NULL, "
                    + COLUMN_DEGREES_TYPE + " TEXT, "
                    + COLUMN_LAST_DEVICE_NAME + " TEXT, "
                    + COLUMN_LAST_DEVICE_MAC + " TEXT, "
                    + COLUMN_LAST_DEVICE_AUTOCONNECT + " INTEGER "
                    + ");";
    private static SQLiteDatabase db; //link to open database

    public TablePreferences(SQLiteDatabase _db) {
        db = _db;
    }

    public void save(Preference preference) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PROFILE_ID, preference.getProfile_id());
        contentValues.put(COLUMN_DEGREES_TYPE, preference.getDegreesType());
        contentValues.put(COLUMN_LAST_DEVICE_NAME, preference.getLastDeviceName());
        contentValues.put(COLUMN_LAST_DEVICE_MAC, preference.getLastDeviceMac());
        contentValues.put(COLUMN_LAST_DEVICE_AUTOCONNECT, preference.getLastDeviceAutoconnect());
        if (preference.getId().equals(UNSAVED_ID)) {
            preference.setId(db.insert(TABLE_NAME, null, contentValues));
        } else
            db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{preference.getId().toString()});
    }

    public void createTable() {
        db.execSQL(DATABASE_CREATE);
    }

    public void addRecord(Long profile_id, String degreesType, String lastDeviceName, String lastDeviceMac, Integer lastDeviceAutoconnect) {
        Preference newRecord = new Preference(profile_id, degreesType, lastDeviceName, lastDeviceMac, lastDeviceAutoconnect);
        save(newRecord);
    }

    public Preference getRecordById(long id) {
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE _id=" + id, null);
        res.moveToFirst();
        return Preference.Parse(
                res.getLong(res.getColumnIndex(_ID)),
                res.getLong(res.getColumnIndex(COLUMN_PROFILE_ID)),
                res.getString(res.getColumnIndex(COLUMN_DEGREES_TYPE)),
                res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_NAME)),
                res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_MAC)),
                res.getInt(res.getColumnIndex(COLUMN_LAST_DEVICE_AUTOCONNECT))
        );


    }

    public void addRecord(Preference preference) {
        save(preference);
    }

    public boolean deleteRecord(Preference toDelete) {
        int result = db.delete(TABLE_NAME, "id = ? ", new String[]{toDelete.getId().toString()});
        return (result != 0);
    }

    public void updateRecord(Preference toUpdate) {
        save(toUpdate);

    }

    public ArrayList<String> getAllRecords() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(_ID))
                    + " " + res.getString(res.getColumnIndex(COLUMN_PROFILE_ID))
                    + " " + res.getString(res.getColumnIndex(COLUMN_DEGREES_TYPE))
                    + " " + res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_NAME))
                    + " " + res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_MAC))
                    + " " + res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_AUTOCONNECT))
            );
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Preference> getAllRecordsAsObjects() {
        ArrayList<Preference> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Preference newRecord = Preference.Parse(
                    res.getLong(res.getColumnIndex(_ID)),
                    res.getLong(res.getColumnIndex(COLUMN_PROFILE_ID)),
                    res.getString(res.getColumnIndex(COLUMN_DEGREES_TYPE)),
                    res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_LAST_DEVICE_MAC)),
                    res.getInt(res.getColumnIndex(COLUMN_LAST_DEVICE_AUTOCONNECT)));
            array_list.add(newRecord);
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor makeCursor() {
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    enum DEGREES_TYPE {Celsium, Kelvin, Fahrenheit}
}