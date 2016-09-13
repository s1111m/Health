package com.relsib.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sim on 20.07.2016.
 */

public class DbModel extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "relsib.db";
    public static final long UNSAVED_ID = -1;

//TODO: Возможно таблицу циклов id, date_start, date_end и к таблице измерений привязать id цикла

    public DbModel(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        TableThermometers tableThermometers = new TableThermometers(db);
        tableThermometers.createTable();
        //создаем таблицы
//        TableProfile tableProfile = new TableProfile(db);
//        tableProfile.createTable();
//
//        tableProfile.addRecord("S1M", 27, "0000");
//        tableProfile.addRecord("Evgeniy", 30, "0000");
//
        TableMeasurment tableMeasurment = new TableMeasurment(db);
        tableMeasurment.createTable();
//
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.6, "Комментарий");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.8, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.4, "NOTICE");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.6, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.4, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.6, "Примечание");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.4, "NOTICE");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.6, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.4, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.6, "WOW");
//        tableMeasurment.addRecord((long) 1, "2016-07-22", 36.4, "NOTICE");
//
//
//        TablePreferences tablePreferences = new TablePreferences(db);
//        tablePreferences.createTable();
//        tablePreferences.addRecord((long) 1, TablePreferences.DEGREES_TYPE.Celsium.name(), "", "", 1);
//        tablePreferences.addRecord((long) 2, TablePreferences.DEGREES_TYPE.Fahrenheit.name(), "", "", 1);
//        tablePreferences.addRecord((long) 3, TablePreferences.DEGREES_TYPE.Kelvin.name(), "", "", 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TableProfile.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableMeasurment.TABLE_NAME);
        //  db.execSQL("DROP TABLE IF EXISTS " + TablePreferences.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableThermometers.TABLE_NAME);
        onCreate(db);
    }
}

