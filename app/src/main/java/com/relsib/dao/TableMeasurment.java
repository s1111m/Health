package com.relsib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by sim on 22.07.2016.
 */
public class TableMeasurment implements BaseColumns {
    static final String TABLE_NAME = "measurment";
    private static final long DEFAULT_PROFILE_ID = 1;
    private static final String COLUMN_PROFILE_ID = "profile_id";
    private static final String COLUMN_THERMOMETER_ID = "thermometer_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_NOTICE = "notice";
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PROFILE_ID + " INTEGER NOT NULL, "
                    + COLUMN_THERMOMETER_ID + " INTEGER NOT NULL, "
                    + COLUMN_DATE + " TEXT NOT NULL, "
                    + COLUMN_TEMPERATURE + " REAL NOT NULL, "
                    + COLUMN_NOTICE + " TEXT "
                    + ");";

    private static SQLiteDatabase db; //link to open database

    public TableMeasurment(SQLiteDatabase _db) {
        db = _db;
    }

    public Measurment getRecordById(long id) {
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE _id=" + id, null);
        res.moveToFirst();
        return Measurment.MeasurmentFactory(
                res.getLong(res.getColumnIndex(_ID)),
                res.getLong(res.getColumnIndex(COLUMN_PROFILE_ID)),
                res.getLong(res.getColumnIndex(COLUMN_THERMOMETER_ID)),
                res.getString(res.getColumnIndex(COLUMN_DATE)),
                res.getDouble(res.getColumnIndex(COLUMN_TEMPERATURE)),
                res.getString(res.getColumnIndex(COLUMN_NOTICE))
        );


    }

    public void save(Measurment measurment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PROFILE_ID, measurment.getProfile_id());
        contentValues.put(COLUMN_THERMOMETER_ID, measurment.getThermometer_id());
        contentValues.put(COLUMN_DATE, measurment.getDate());
        contentValues.put(COLUMN_TEMPERATURE, measurment.getTemperature());
        contentValues.put(COLUMN_NOTICE, measurment.getNotice());
        if (measurment.getId().equals(Measurment.UNSAVED_ID)) {
            measurment.setId(db.insert(TABLE_NAME, null, contentValues));
        } else
            db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{measurment.getId().toString()});
    }

    public void createTable() {
        db.execSQL(DATABASE_CREATE);
    }

    public void addRecord(Long profile_id, String date, Double temperature, String notice) {
        Measurment newRecord = new Measurment(profile_id, date, temperature, notice);
        save(newRecord);
    }

    public boolean deleteRecord(Measurment toDelete) {
        int result = db.delete(TABLE_NAME, "id = ? ", new String[]{toDelete.getId().toString()});
        return (result != 0);
    }

    public void updateRecord(Measurment toUpdate) {
        save(toUpdate);
    }

    public ArrayList<String> getAllRecords() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(
                    res.getString(res.getColumnIndex(_ID)) + " " + res.getString(res.getColumnIndex(COLUMN_PROFILE_ID))
                            + res.getString(res.getColumnIndex(COLUMN_THERMOMETER_ID))
                            + " " + res.getString(res.getColumnIndex(COLUMN_DATE))
                            + " " + res.getString(res.getColumnIndex(COLUMN_TEMPERATURE))
                            + " " + res.getString(res.getColumnIndex(COLUMN_NOTICE))
            );
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Measurment> getAllRecordsAsObjects() {
        ArrayList<Measurment> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(Measurment.MeasurmentFactory(
                    res.getLong(res.getColumnIndex(_ID)),
                    res.getLong(res.getColumnIndex(COLUMN_PROFILE_ID)),
                    res.getLong(res.getColumnIndex(COLUMN_THERMOMETER_ID)),
                    res.getString(res.getColumnIndex(COLUMN_DATE)),
                    res.getDouble(res.getColumnIndex(COLUMN_TEMPERATURE)),
                    res.getString(res.getColumnIndex(COLUMN_NOTICE))
            ));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Double> getAllTemperaturePoints() {
        ArrayList<Double> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select temperature from " + TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(
                    res.getDouble(res.getColumnIndex(COLUMN_TEMPERATURE))
            );
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor makeCursor() {
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
}



