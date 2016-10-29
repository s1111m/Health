package com.relsib.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.util.Log;

import static android.text.TextUtils.split;

/**
 * Created by sim on 28.10.2016.
 */

public class SettingsViewAlarm extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String KEY_NAME = "_name";
    public static final String KEY_AUTOCONNECT = "_autoconnect";
    public static final String KEY_COLOR_LABEL = "_colorlabel";
    public static final String KEY_BACKGROUND_COLOR = "_backgroundcolor";
    public static final String KEY_ALARMS = "_alarms";
    public static final String KEY_ALARMS_SCREEN = "_alarmsscreen";
    public static final String KEY_ALARMS_MIN_VIBRATE = "_alarmsminvibrate";
    public static final String KEY_ALARMS_MIN_VALUE = "_alarmsminvalue";

    public static final String KEY_ALARMS_MIN_SOUND = "_alarmsminsound";

    public static final String KEY_ALARMS_MAX_VALUE = "_alarmmaxvalue";
    public static final String KEY_ALARMS_MAX_VIBRATE = "_alarmsmaxvibrate";
    public static final String KEY_ALARMS_MAX_SOUND = "_alarmmaxsound";
    public static final String KEY_MEASURE_UNITS = "_units";
    public static final String FILE_NAME = "set_filename";
    private static final String TAG = "param1";
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    String idTag;
    EditTextPreference thermometer_name;
    ListPreference list;

    public SettingsViewAlarm() {
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
//        TypedValue themeTypedValue = new TypedValue();
//        activityContext.getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        getPreferenceManager().setSharedPreferencesName(idTag + FILE_NAME);

        //final PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(activityContext);


        final PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(activityContext);
        setPreferenceScreen(rootScreen);
        //thermometer_alarms_screen.setTitle("Настройка уведомлений");
        //  thermometer_alarms_screen.setSummary("Установка режимов уведомлений");


        //thermometer_alarms_screen.setFragment(SettingsViewCommon.class.getName());

//        thermometer_alarms_screen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                //    Log.e(TAG,"clicked " + this.getClass().getSimpleName());
//                setPreferenceScreen(thermometer_alarms_screen);
//                return false;
//            }
//        });

        final PreferenceCategory rootCategory2 = new PreferenceCategory(activityContext);
        rootCategory2.setTitle("Уведомлять о минимальной температуре");
        rootScreen.addPreference(rootCategory2);

        final EditTextPreference thermometer_alarms_min_treshold = new EditTextPreference(activityContext);
        thermometer_alarms_min_treshold.setKey(idTag + KEY_ALARMS_MIN_VALUE);
        thermometer_alarms_min_treshold.setTitle("Установить порог");
        rootCategory2.addPreference(thermometer_alarms_min_treshold);

        final CheckBoxPreference thermometer_alarms_min_vibrate = new CheckBoxPreference(activityContext);
        thermometer_alarms_min_vibrate.setTitle("Вибрация");
        thermometer_alarms_min_vibrate.setSummary("Отключено");
        thermometer_alarms_min_vibrate.setDefaultValue(false);
        thermometer_alarms_min_vibrate.setKey(idTag + KEY_ALARMS_MIN_VIBRATE);

        thermometer_alarms_min_vibrate.setChecked(false);
        rootCategory2.addPreference(thermometer_alarms_min_vibrate);

        final RingtonePreference thermometer_alarms_min_sound = new RingtonePreference(activityContext);
        thermometer_alarms_min_sound.setKey(idTag + KEY_ALARMS_MIN_SOUND);
        thermometer_alarms_min_sound.setTitle("Выбрать мелодию");
        rootCategory2.addPreference(thermometer_alarms_min_sound);

        final PreferenceCategory rootCategory3 = new PreferenceCategory(activityContext);
        rootCategory3.setTitle("Уведомлять о максимальной температуре");
        rootScreen.addPreference(rootCategory3);

        final EditTextPreference thermometer_alarms_max_treshold = new EditTextPreference(activityContext);
        thermometer_alarms_max_treshold.setKey(idTag + KEY_ALARMS_MIN_VALUE);
        thermometer_alarms_max_treshold.setTitle("Установить порог");
        thermometer_alarms_max_treshold.setDefaultValue("1");
        rootCategory3.addPreference(thermometer_alarms_max_treshold);

        final CheckBoxPreference thermometer_alarms_max_vibrate = new CheckBoxPreference(activityContext);
        thermometer_alarms_max_vibrate.setTitle("Вибрация");
        thermometer_alarms_max_vibrate.setSummary("Отключено");
        thermometer_alarms_max_vibrate.setKey(idTag + KEY_ALARMS_MAX_VIBRATE);
        thermometer_alarms_max_vibrate.setDefaultValue(false);
        rootCategory3.addPreference(thermometer_alarms_max_vibrate);

        final RingtonePreference thermometer_alarms_max_sound = new RingtonePreference(activityContext);
        thermometer_alarms_max_sound.setKey(idTag + KEY_ALARMS_MAX_SOUND);
        thermometer_alarms_max_sound.setTitle("Выбрать мелодию");
        rootCategory3.addPreference(thermometer_alarms_max_sound);

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
        String[] changes = split(s, "_");
        final SmartThermometer whoChanged = MainActivityView.mBLEService.findThermometerByMac(changes[0]);

        if (whoChanged != null) {
            switch ("_" + changes[1]) {
                case SettingsViewCommon.KEY_NAME:
                    String mDeviceName = sharedPreferences.getString(s, "WT-50");
                    whoChanged.setmDeviceName(mDeviceName);
                    thermometer_name.setSummary(mDeviceName);
                    break;
                case SettingsViewCommon.KEY_COLOR_LABEL:
                    Log.e(TAG, "Changing " + whoChanged.mDeviceMacAddress);
                    whoChanged.setmDeviceColorLabel(sharedPreferences.getInt(s, Color.WHITE));
                    break;
                case SettingsViewCommon.KEY_BACKGROUND_COLOR:
                    whoChanged.setmDeviceBackgroundColor(sharedPreferences.getInt(s, Color.WHITE));
                    break;
                case SettingsViewCommon.KEY_ALARMS:
                    break;
                case SettingsViewCommon.KEY_MEASURE_UNITS:
                    String newMeasureUnits = sharedPreferences.getString(s, SmartThermometer.MeasureUnits.Celsium);
                    //// whoChanged.changeMeasureUnits(newMeasureUnits);
                    Log.e(TAG, newMeasureUnits);
                    whoChanged.setmDeviceMeasureUnits(newMeasureUnits);
                    list.setSummary(whoChanged.mDeviceMeasureUnits);
                    break;
                default:
                    break;
            }
        }
    }
}
