package com.relsib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by sim on 22.07.2016.
 */
public class TableProfile implements BaseColumns {
    static final String TABLE_NAME = "profile";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_PASSWORD = "password";
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_AGE + " INTEGER, "
                    + COLUMN_PASSWORD + " TEXT "
                    + ");";
    private static SQLiteDatabase db; //link to open database

    public TableProfile(SQLiteDatabase _db) {
        db = _db;
    }

    public Profile getRecordById(long id) {
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE _id=" + id, null);
        res.moveToFirst();
        return Profile.Parse(
                res.getLong(res.getColumnIndex(_ID)),
                res.getString(res.getColumnIndex(COLUMN_NAME)),
                res.getInt(res.getColumnIndex(COLUMN_AGE)),
                res.getString(res.getColumnIndex(COLUMN_PASSWORD))
        );


    }

    public void save(Profile profile) {
        long result;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, profile.name);
        contentValues.put(COLUMN_AGE, profile.age);
        contentValues.put(COLUMN_PASSWORD, profile.password);

        if (profile.getId().equals(Profile.UNSAVED_ID)) {
            // return inserted id
            profile.setId(db.insert(TABLE_NAME, null, contentValues));
        } else {
            result = db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{profile.getId().toString()}); //if affected row >0 then true
        }

    }

    public void createTable() {
        db.execSQL(DATABASE_CREATE);
    }

    public void addRecord(String name, Integer age, String password) {
        Profile newRecord = new Profile(name, age, password);
        save(newRecord);
    }

    public boolean deleteRecord(Profile toDelete) {
        int result = db.delete(TABLE_NAME, "id = ? ", new String[]{toDelete.getId().toString()});
        return (result != 0);
    }

    public void updateRecord(Profile toUpdate) {
        save(toUpdate);
    }

    public ArrayList<String> getAllRecords() {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(_ID)) + " " + res.getString(res.getColumnIndex(COLUMN_NAME)) + " " + res.getString(res.getColumnIndex(COLUMN_AGE)) + " " + res.getString(res.getColumnIndex(COLUMN_PASSWORD)));
            res.moveToNext();
        }
        return array_list;
    }

    public Cursor makeCursor() {
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
}