package com.relsib.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import static android.text.TextUtils.split;

public class SettingsViewCommon extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String KEY_NAME = "_name";
    public static final String KEY_AUTOCONNECT = "_autoconnect";
    public static final String KEY_COLOR_LABEL = "_colorlabel";
    public static final String KEY_BACKGROUND_COLOR = "_backgroundcolor";
    public static final String KEY_ALARMS = "_alarms";
    public static final String KEY_ALARMS_SCREEN = "_alarmsscreen";
    public static final String KEY_ALARMS_MIN_VIBRATE = "_alarmsminvibrate";
    public static final String KEY_ALARMS_MIN_VALUE = "_alarmsminvalue";
    public static final String KEY_ALARMS_MIN_ENABLED = "_alarmsminenabled";
    public static final String KEY_ALARMS_MIN_SOUND = "_alarmsminsound";
    public static final String KEY_ALARMS_MIN_GRAPHIC = "_alarmsmingraphic";
    public static final String KEY_ALARMS_MAX_VALUE = "_alarmmaxvalue";
    public static final String KEY_ALARMS_MAX_VIBRATE = "_alarmsmaxvibrate";
    public static final String KEY_ALARMS_MAX_SOUND = "_alarmmaxsound";
    public static final String KEY_ALARMS_MAX_ENABLED = "_alarmmaxenabled";
    public static final String KEY_ALARMS_MAX_GRAPHIC = "_alarmmaxgraphic";
    public static final String KEY_MEASURE_UNITS = "_units";
    public static final String FILE_NAME = "set_filename";
    private static final String TAG = "param1";
    String currentMeasureUnits;
    int minAlarmTresholdBound;
    int maxAlarmTresholdBound;
    String idTag;
    EditTextPreference thermometer_name;
    ListPreference list;
    de.mrapp.android.preference.SeekBarPreference thermometer_alarms_min_treshold;
    de.mrapp.android.preference.SeekBarPreference thermometer_alarms_max_treshold;

    public SettingsViewCommon() {
        // Required empty public constructor
    }

    public static SettingsViewCommon newInstance(String param1) {
        SettingsViewCommon fragment = new SettingsViewCommon();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            idTag = getArguments().getString(TAG);


        }
        super.onCreate(savedInstanceState);

        Context activityContext = getActivity();

        TypedValue themeTypedValue = new TypedValue();

        activityContext.getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        getPreferenceManager().setSharedPreferencesName(idTag + FILE_NAME);

        final PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(activityContext);
        setPreferenceScreen(rootScreen);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


        //создаем категорию и добавляем в экран
        final PreferenceCategory rootCategory = new PreferenceCategory(activityContext);
        rootCategory.setTitle("Настройки");
        rootScreen.addPreference(rootCategory);

        thermometer_name = new EditTextPreference(activityContext);
        thermometer_name.setKey(KEY_NAME);
        thermometer_name.setTitle("Имя термометра");
        thermometer_name.setSummary(getPreferenceManager().getSharedPreferences().getString(KEY_NAME, "WT-50"));
        thermometer_name.setDialogTitle("Введите новое имя термометра");
        rootCategory.addPreference(thermometer_name);


        de.mrapp.android.preference.ColorPalettePreference thermometer_color_label = new de.mrapp.android.preference.ColorPalettePreference(BLEService.mActivityContext);
        //  thermometer_color_label = new com.kizitonwose.colorpreference.ColorPreference(BLEService.mActivityContext);
        thermometer_color_label.setKey(KEY_COLOR_LABEL);
        thermometer_color_label.setColorPalette(R.array.color_choices);
        thermometer_color_label.setTitle("Цветовая метка");
        thermometer_color_label.setSummary("Установить цветовую метку в программе");
        thermometer_color_label.setDefaultValue(Color.WHITE);
        rootCategory.addPreference(thermometer_color_label);

        final com.rarepebble.colorpicker.ColorPreference thermometer_background_color = new com.rarepebble.colorpicker.ColorPreference(BLEService.mActivityContext);
        thermometer_background_color.setKey(KEY_BACKGROUND_COLOR);
        thermometer_background_color.setTitle("Цвет фона");
        thermometer_background_color.setSummary("Установить цвет фона");
        rootCategory.addPreference(thermometer_background_color);

    /* вложенный экран настроек */
        final PreferenceScreen thermometer_alarms_screen = getPreferenceManager().createPreferenceScreen(BLEService.mActivityContext);

        thermometer_alarms_screen.setTitle("Настройка уведомлений");
        thermometer_alarms_screen.setSummary("Установка режимов уведомлений");
        thermometer_alarms_screen.setKey(KEY_ALARMS_SCREEN);
        rootCategory.addPreference(thermometer_alarms_screen);
        thermometer_alarms_screen.setFragment(com.relsib.application.SettingsViewAlarm.class.getName());
        thermometer_alarms_screen.setOnPreferenceClickListener(new PreferenceScreen.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.e(TAG, "clicked 3" + SettingsViewCommon.class.getName());
                //preference.
                 setPreferenceScreen(thermometer_alarms_screen);
                //BLEService.mActivityContext.getFragmentManager().beginTransaction().replace(R.id.frgmCont, SettingsViewAlarm.newInstance(idTag)).commit();
              //  BLEService.mActivityContext.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, SettingsViewCommon.newInstance(idTag)).commit();
                return false;
            }
        });


        final PreferenceCategory minCategory = new PreferenceCategory(activityContext);
        minCategory.setTitle("Уведомлять о минимальной температуре");
        thermometer_alarms_screen.addPreference(minCategory);

        currentMeasureUnits = getPreferenceManager().getSharedPreferences().getString(KEY_MEASURE_UNITS, SmartThermometer.MeasureUnits.Celsium);
//        if (currentMeasureUnits == null)
//            currentMeasureUnits = SmartThermometer.MeasureUnits.Celsium;

        minAlarmTresholdBound = (int) SmartThermometer.MeasureUnits.convertMeasureUnits(-20, SmartThermometer.MeasureUnits.Celsium, currentMeasureUnits);
        maxAlarmTresholdBound = (int) SmartThermometer.MeasureUnits.convertMeasureUnits(70, SmartThermometer.MeasureUnits.Celsium, currentMeasureUnits);
        //   Log.e(TAG, "converted " + minAlarmTresholdBound + " " + maxAlarmTresholdBound);

        final CheckBoxPreference thermometer_alarms_min_enabled = new CheckBoxPreference(activityContext);
        thermometer_alarms_min_enabled.setTitle("Включить уведомление");
        //thermometer_alarms_min_enabled.set
        thermometer_alarms_min_enabled.setDefaultValue(false);
        thermometer_alarms_min_enabled.setKey(KEY_ALARMS_MIN_ENABLED);
        minCategory.addPreference(thermometer_alarms_min_enabled);

        thermometer_alarms_min_treshold = new de.mrapp.android.preference.SeekBarPreference(activityContext);
        thermometer_alarms_min_treshold.setKey(KEY_ALARMS_MIN_VALUE);
        thermometer_alarms_min_treshold.setTitle("Установить порог");
        thermometer_alarms_min_treshold.setMinValue(minAlarmTresholdBound);
        thermometer_alarms_min_treshold.setMaxValue(maxAlarmTresholdBound);
        thermometer_alarms_min_treshold.setDefaultValue((float) minAlarmTresholdBound);
        thermometer_alarms_min_treshold.setSummary(String.valueOf(getPreferenceManager().getSharedPreferences().getFloat(KEY_ALARMS_MIN_VALUE, minAlarmTresholdBound)) + " " + currentMeasureUnits.toString());
        thermometer_alarms_min_treshold.setUnit(currentMeasureUnits);

        minCategory.addPreference(thermometer_alarms_min_treshold);
        thermometer_alarms_min_treshold.setDependency(KEY_ALARMS_MIN_ENABLED);

        final CheckBoxPreference thermometer_alarms_min_graphic = new CheckBoxPreference(activityContext);
        thermometer_alarms_min_graphic.setTitle("Мерцание индикатора");
        thermometer_alarms_min_graphic.setDefaultValue(false);
        thermometer_alarms_min_graphic.setKey(KEY_ALARMS_MIN_GRAPHIC);

        minCategory.addPreference(thermometer_alarms_min_graphic);
        thermometer_alarms_min_graphic.setDependency(KEY_ALARMS_MIN_ENABLED);

        final CheckBoxPreference thermometer_alarms_min_vibrate = new CheckBoxPreference(activityContext);
        thermometer_alarms_min_vibrate.setTitle("Вибрация");
        thermometer_alarms_min_vibrate.setDefaultValue(false);
        thermometer_alarms_min_vibrate.setKey(KEY_ALARMS_MIN_VIBRATE);

        minCategory.addPreference(thermometer_alarms_min_vibrate);
        thermometer_alarms_min_vibrate.setDependency(KEY_ALARMS_MIN_ENABLED);

        final RingtonePreference thermometer_alarms_min_sound = new RingtonePreference(activityContext);
        thermometer_alarms_min_sound.setKey(KEY_ALARMS_MIN_SOUND);
        thermometer_alarms_min_sound.setTitle("Выбрать мелодию");

        minCategory.addPreference(thermometer_alarms_min_sound);
        thermometer_alarms_min_sound.setDependency(KEY_ALARMS_MIN_ENABLED);

//------------------------------------------------------------
        final PreferenceCategory maxCategory = new PreferenceCategory(activityContext);
        maxCategory.setTitle("Уведомлять о максимальной температуре");
        thermometer_alarms_screen.addPreference(maxCategory);
//
        final CheckBoxPreference thermometer_alarms_max_enabled = new CheckBoxPreference(activityContext);
        thermometer_alarms_max_enabled.setTitle("Включить уведомление");
        thermometer_alarms_max_enabled.setDefaultValue(false);
        thermometer_alarms_max_enabled.setKey(KEY_ALARMS_MAX_ENABLED);
        maxCategory.addPreference(thermometer_alarms_max_enabled);

        thermometer_alarms_max_treshold = new de.mrapp.android.preference.SeekBarPreference(activityContext);

        thermometer_alarms_max_treshold.setKey(KEY_ALARMS_MAX_VALUE);
        thermometer_alarms_max_treshold.setTitle("Установить порог");
        thermometer_alarms_max_treshold.setMinValue(minAlarmTresholdBound);
        thermometer_alarms_max_treshold.setMaxValue(maxAlarmTresholdBound);
        thermometer_alarms_max_treshold.setDefaultValue((float) maxAlarmTresholdBound);
        thermometer_alarms_max_treshold.setSummary(String.valueOf(getPreferenceManager().getSharedPreferences().getFloat(KEY_ALARMS_MAX_VALUE, maxAlarmTresholdBound)) + " " + currentMeasureUnits);
        thermometer_alarms_max_treshold.setUnit(currentMeasureUnits);
        maxCategory.addPreference(thermometer_alarms_max_treshold);
        thermometer_alarms_max_treshold.setDependency(KEY_ALARMS_MAX_ENABLED);

        final CheckBoxPreference thermometer_alarms_max_graphic = new CheckBoxPreference(activityContext);
        thermometer_alarms_max_graphic.setTitle("Мерцание индикатора");
        thermometer_alarms_max_graphic.setDefaultValue(false);
        thermometer_alarms_max_graphic.setKey(KEY_ALARMS_MAX_GRAPHIC);
        maxCategory.addPreference(thermometer_alarms_max_graphic);
        thermometer_alarms_max_graphic.setDependency(KEY_ALARMS_MAX_ENABLED);

        final CheckBoxPreference thermometer_alarms_max_vibrate = new CheckBoxPreference(activityContext);
        thermometer_alarms_max_vibrate.setTitle("Вибрация");
        thermometer_alarms_max_vibrate.setKey(KEY_ALARMS_MAX_VIBRATE);
        thermometer_alarms_max_vibrate.setDefaultValue(false);
        maxCategory.addPreference(thermometer_alarms_max_vibrate);
        thermometer_alarms_max_vibrate.setDependency(KEY_ALARMS_MAX_ENABLED);

        final RingtonePreference thermometer_alarms_max_sound = new RingtonePreference(activityContext);
        thermometer_alarms_max_sound.setKey(KEY_ALARMS_MAX_SOUND);
        thermometer_alarms_max_sound.setTitle("Выбрать мелодию");
        maxCategory.addPreference(thermometer_alarms_max_sound);
        thermometer_alarms_max_sound.setDependency(KEY_ALARMS_MAX_ENABLED);

      /* ---- вложенный экран настроек */
        list = new ListPreference(activityContext);
        list.setKey(KEY_MEASURE_UNITS);
        list.setTitle("Единицы измерения");

        list.setSummary(getPreferenceManager().getSharedPreferences().getString(KEY_MEASURE_UNITS, "°C"));
        list.setEntries(R.array.entries);
        list.setEntryValues(R.array.entries);
        rootCategory.addPreference(list);

//        final CheckBoxPreference thermometer_autoconnect = new CheckBoxPreference(activityContext);
//        thermometer_autoconnect.setTitle("Автоподключение");
//        thermometer_autoconnect.setSummary("Обнаруживать и производить подключение к термометру");
//        thermometer_autoconnect.setKey(KEY_AUTOCONNECT);
//        thermometer_autoconnect.setDefaultValue(true);

        //rootCategory.addPreference(thermometer_autoconnect);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //String[] changes = split(s, "_");
        final SmartThermometer whoChanged = MainActivityView.mBLEService.findThermometerByMac(idTag);

        if (whoChanged != null) {
            switch (s) {
                case SettingsViewCommon.KEY_NAME:
                    String mDeviceName = sharedPreferences.getString(s, "WT-50");
                    whoChanged.setmDeviceName(mDeviceName);
                    thermometer_name.setSummary(mDeviceName);
                    break;
                case SettingsViewCommon.KEY_COLOR_LABEL:
                    // Log.e(TAG, "Changing " + whoChanged.mDeviceMacAddress);
                    whoChanged.setmDeviceColorLabel(sharedPreferences.getInt(s, Color.WHITE));
                    break;
                case SettingsViewCommon.KEY_BACKGROUND_COLOR:
                    whoChanged.setmDeviceBackgroundColor(sharedPreferences.getInt(s, Color.WHITE));
                    break;
                case SettingsViewCommon.KEY_ALARMS:
                    break;
                case SettingsViewCommon.KEY_MEASURE_UNITS:
                    String newMeasureUnits = sharedPreferences.getString(s, SmartThermometer.MeasureUnits.Celsium);

                    Log.e(TAG, "Values in object " + whoChanged.minAlarmTemperature + " " + whoChanged.maxAlarmTemperature + " " + whoChanged.mDeviceMeasureUnits);
                    whoChanged.setmDeviceMeasureUnits(newMeasureUnits);

                    Log.e(TAG, "Values in object after converting " + whoChanged.minAlarmTemperature + " " + whoChanged.maxAlarmTemperature + " " + whoChanged.mDeviceMeasureUnits);

                    list.setSummary(whoChanged.mDeviceMeasureUnits);

                    minAlarmTresholdBound = (int) SmartThermometer.MeasureUnits.convertMeasureUnits(-20, SmartThermometer.MeasureUnits.Celsium, newMeasureUnits);
                    maxAlarmTresholdBound = (int) SmartThermometer.MeasureUnits.convertMeasureUnits(70, SmartThermometer.MeasureUnits.Celsium, newMeasureUnits);

                    Log.e(TAG, " bounds " + minAlarmTresholdBound + " max " + maxAlarmTresholdBound);


                    Log.e(TAG, "Saving min values" + whoChanged.minAlarmTemperature + "  bounds " + minAlarmTresholdBound + " max " + maxAlarmTresholdBound);
                    thermometer_alarms_min_treshold.setMinValue(-1000);
                    thermometer_alarms_min_treshold.setMaxValue(1000);
                    //thermometer_alarms_min_treshold.setDefaultValue((float) minAlarmTresholdBound);
                    thermometer_alarms_min_treshold.setUnit(whoChanged.mDeviceMeasureUnits);
                    getPreferenceManager().getSharedPreferences().edit().putFloat(KEY_ALARMS_MIN_VALUE, whoChanged.minAlarmTemperature).apply();
                    thermometer_alarms_min_treshold.setValue(whoChanged.minAlarmTemperature);
                    thermometer_alarms_min_treshold.setMinValue(minAlarmTresholdBound);
                    thermometer_alarms_min_treshold.setMaxValue(maxAlarmTresholdBound);
                    Log.e(TAG, "get setted min value " + thermometer_alarms_min_treshold.getValue());

                    Log.e(TAG, "Saving max values" + whoChanged.maxAlarmTemperature + " bounds " + minAlarmTresholdBound + " max " + maxAlarmTresholdBound);
                    thermometer_alarms_max_treshold.setMinValue(-1000);
                    thermometer_alarms_max_treshold.setMaxValue(1000);
                    // thermometer_alarms_max_treshold.setDefaultValue((float) maxAlarmTresholdBound);
                    thermometer_alarms_max_treshold.setUnit(whoChanged.mDeviceMeasureUnits);
                    getPreferenceManager().getSharedPreferences().edit().putFloat(KEY_ALARMS_MAX_VALUE, whoChanged.maxAlarmTemperature).apply();

                    thermometer_alarms_max_treshold.setValue(whoChanged.maxAlarmTemperature);
                    thermometer_alarms_max_treshold.setMinValue(minAlarmTresholdBound);
                    thermometer_alarms_max_treshold.setMaxValue(maxAlarmTresholdBound);

                    Log.e(TAG, "get setted value " + thermometer_alarms_max_treshold.getValue());
                    break;
                case SettingsViewCommon.KEY_ALARMS_MIN_VALUE:
                    Log.e(TAG, "Calling onChange min");
                    Log.e(TAG, "min current form value " + thermometer_alarms_min_treshold.getValue());
                    whoChanged.minAlarmTemperature = getPreferenceManager().getSharedPreferences().getFloat(KEY_ALARMS_MIN_VALUE, minAlarmTresholdBound);
                    Log.e(TAG, "min read from file " + whoChanged.minAlarmTemperature);
                    thermometer_alarms_min_treshold.setSummary(String.valueOf(whoChanged.minAlarmTemperature) + " " + whoChanged.mDeviceMeasureUnits);
                    break;
                case SettingsViewCommon.KEY_ALARMS_MIN_ENABLED:
                    whoChanged.minAlarmEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MIN_ENABLED, false);
                    break;
                case SettingsViewCommon.KEY_ALARMS_MIN_VIBRATE:
                    whoChanged.minAlarmVibrateEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MIN_VIBRATE, false);
                    break;
                case SettingsViewCommon.KEY_ALARMS_MIN_GRAPHIC:
                    whoChanged.minAlarmGraphicEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MIN_GRAPHIC, false);
                    break;
                case SettingsViewCommon.KEY_ALARMS_MIN_SOUND:
                    whoChanged.minRingtone.stop();
                    whoChanged.minRingtone = RingtoneManager.getRingtone(BLEService.mServiceContext, Uri.parse(getPreferenceManager().getSharedPreferences().getString(idTag+ SettingsViewCommon.KEY_ALARMS_MIN_SOUND,"default ringtone")));
                    break;
                case SettingsViewCommon.KEY_ALARMS_MAX_ENABLED:
                    whoChanged.maxAlarmEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MAX_ENABLED, false);
                    break;
                case SettingsViewCommon.KEY_ALARMS_MAX_VIBRATE:
                    whoChanged.maxAlarmVibrateEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MAX_VIBRATE, false);
                    Log.e(TAG, String.valueOf(whoChanged.maxAlarmVibrateEnabled));
                    break;
                case SettingsViewCommon.KEY_ALARMS_MAX_GRAPHIC:
                    whoChanged.maxAlarmGraphicEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARMS_MAX_GRAPHIC, false);
                    Log.e(TAG, String.valueOf(whoChanged.maxAlarmGraphicEnabled));
                    break;
                case SettingsViewCommon.KEY_ALARMS_MAX_SOUND:
                    whoChanged.maxRingtone.stop();
                    whoChanged.maxRingtone = RingtoneManager.getRingtone(BLEService.mServiceContext, Uri.parse(getPreferenceManager().getSharedPreferences().getString(SettingsViewCommon.KEY_ALARMS_MAX_SOUND,"default ringtone")));
                    break;
                case SettingsViewCommon.KEY_ALARMS_MAX_VALUE:
                    Log.e(TAG, "Calling onChange max");
                    Log.e(TAG, "max current form value " + thermometer_alarms_max_treshold.getValue());
                    whoChanged.maxAlarmTemperature = getPreferenceManager().getSharedPreferences().getFloat(KEY_ALARMS_MAX_VALUE, maxAlarmTresholdBound);
                    Log.e(TAG, "max read from file " + whoChanged.maxAlarmTemperature);
                    thermometer_alarms_max_treshold.setSummary(String.valueOf(String.valueOf(whoChanged.maxAlarmTemperature)) + " " + whoChanged.mDeviceMeasureUnits);
                    break;
                default:
                    break;
            }
        }
    }
}
