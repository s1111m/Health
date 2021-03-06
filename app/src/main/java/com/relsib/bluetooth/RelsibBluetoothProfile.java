package com.relsib.bluetooth;

import java.util.UUID;

public interface RelsibBluetoothProfile {

    int SHUTDOWN_THERMOMETER = 0x01;
    //Каждый блок будет начинаться с имени сервиса и далее переменные по стандарту блютуса
    //=====================================================================================
    UUID GENERIC_ACCESS_SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    UUID DEVICE_NAME = UUID.fromString("00002A00-0000-1000-8000-00805f9b34fb");
    UUID APPEARANCE = UUID.fromString("00002A01-0000-1000-8000-00805f9b34fb");
    UUID PERIPHERAL_PRIVACY_FLAG = UUID.fromString("00002A02-0000-1000-8000-00805f9b34fb");
    UUID RECONNECTION_ADDRESS = UUID.fromString("00002A03-0000-1000-8000-00805f9b34fb");
    UUID PERIPHERAL_PREFERRED_CONNECTION = UUID.fromString("00002A04-0000-1000-8000-00805f9b34fb");
    //=====================================================================================
    UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    UUID SERVICE_CHANGED = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");
    //====================================================================================
    UUID HEALTH_THERMOMETER_SERVICE = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    UUID TEMPERATURE_MEASUREMENT = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");
    UUID INTERMEDIATE_TEMPERATURE = UUID.fromString("00002A1E-0000-1000-8000-00805f9b34fb");
    UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //возможно одинаков для всех переменных
    //====================================================================================
    UUID DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    UUID SYSTEM_ID = UUID.fromString("00002a23-0000-1000-8000-00805f9b34fb");
    UUID MODEL_NUMBER_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
    UUID SERIAL_NUMBER_UUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    UUID FIRMWARE_REVISION_UUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    UUID HARDWARE_REVISION_UUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb");
    UUID SOFTWARE_REVISION_UUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");
    UUID MANUFACTURER_NAME_UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    //=====================================================================================
    UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    UUID BATTERY_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    //=====================================================================================

    UUID RELSIBPROFILE_SERV = UUID.fromString("0177AAA0-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_TEMP = UUID.fromString("0177AAA1-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_SERIAL_NUMBER = UUID.fromString("0177AAA2-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_CALIBR_FLAG = UUID.fromString("0177AAA3-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_CALIBR_NUMBER = UUID.fromString("0177AAA4-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_CALIBR_DATE = UUID.fromString("0177AAA5-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_RESET_MEAS_FLAG = UUID.fromString("0177AAA6-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_WHITE_LIST_FLAG = UUID.fromString("0177AAA7-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_SHUTDOWN = UUID.fromString("0177AAA8-B455-0E17-D0DA-14EA33F8DE11");
    UUID RELSIBPROFILE_MODE = UUID.fromString("0177AAA9-B455-0E17-D0DA-14EA33F8DE11");


}
