package com.relsib.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsView extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public SettingsView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to MeasurmentFactory a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsView.
     */

    public static SettingsView newInstance(String param1, String param2) {
        SettingsView fragment = new SettingsView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//
//
//        if (preference instanceof CheckBoxPreference) {
//            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.
//                    getDefaultSharedPreferences(preference.getContext()).
//                    getBoolean(preference.getKey(),false));
//
//        } else {
//            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.
//                    getDefaultSharedPreferences(preference.getContext()).
//                    getString(preference.getKey(),""));
//        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            String mParam1 = getArguments().getString(ARG_PARAM1);
//            String mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        super.onCreate(savedInstanceState);
        // создаем экран
        PreferenceScreen p = createPreferences();
        this.setPreferenceScreen(p);//Set the PreferenceScreen as the current one on this fragment

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private PreferenceScreen createPreferences() {
        PreferenceScreen p =
                getPreferenceManager().createPreferenceScreen(getActivity());

        ListPreference listPref = new ListPreference(getActivity());

        listPref.setKey("some_key"); //Refer to get the pref value
        CharSequence[] csEntries = new String[]{"Item1", "Item2"};
        CharSequence[] csValues = new String[]{"1", "2"};
        listPref.setDefaultValue("-1");
        listPref.setEntries(csEntries); //Entries(how you display them)
        listPref.setEntryValues(csValues);//actual values
        listPref.setDialogTitle("Dialog title");
        listPref.setTitle("Title");
        listPref.setSummary("Some summary");

        p.addPreference(listPref);

        return p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.f_settings_view, container, false);


        //now bind values, notice we use p.findPreference which means whe look into the preferenceScreen Associated with the PreferenceFragment/Activity
        //  bindPreferenceSummaryToValue(p.findPreference("some_key"));
        //   PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(getLayoutInflater(savedInstanceState).getContext());
        //PreferenceScreen rootScreen = getLayoutInflater(savedInstanceState)
//        // говорим Activity, что rootScreen - корневой
        // setPreferenceScreen(rootScreen);
//        // даллее создаем элементы, присваиваем атрибуты и формируем иерархию
//        CheckBoxPreference chb1 = new CheckBoxPreference(getActivity());
//        chb1.setKey("chb1");
//        chb1.setTitle("CheckBox 1");
//        chb1.setSummaryOn("Description of checkbox 1 on");
//        chb1.setSummaryOff("Description of checkbox 1 off");
//
//        rootScreen.addPreference(chb1);

//
//        ListPreference list = new ListPreference(getActivity());
//        list.setKey("list");
//        list.setTitle("List");
//        list.setSummary("Description of list");
//        list.setEntries(R.array.entries);
//        list.setEntryValues(R.array.entry_values);
//
//        rootScreen.addPreference(list);
//
//
//        CheckBoxPreference chb2 = new CheckBoxPreference(getActivity());
//        chb2.setKey("chb2");
//        chb2.setTitle("CheckBox 2");
//        chb2.setSummary("Description of checkbox 2");
//
//        rootScreen.addPreference(chb2);
//
//
//        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());
//        screen.setKey("screen");
//        screen.setTitle("Screen");
//        screen.setSummary("Description of screen");
//
//
//        final CheckBoxPreference chb3 = new CheckBoxPreference(getActivity());
//        chb3.setKey("chb3");
//        chb3.setTitle("CheckBox 3");
//        chb3.setSummary("Description of checkbox 3");
//
//        screen.addPreference(chb3);
//
//
//        PreferenceCategory categ1 = new PreferenceCategory(getActivity());
//        categ1.setKey("categ1");
//        categ1.setTitle("Category 1");
//        categ1.setSummary("Description of category 1");
//
//        screen.addPreference(categ1);
//
//        CheckBoxPreference chb4 = new CheckBoxPreference(getActivity());
//        chb4.setKey("chb4");
//        chb4.setTitle("CheckBox 4");
//        chb4.setSummary("Description of checkbox 4");
//
//        categ1.addPreference(chb4);
//
//
//        final PreferenceCategory categ2 = new PreferenceCategory(getActivity());
//        categ2.setKey("categ2");
//        categ2.setTitle("Category 2");
//        categ2.setSummary("Description of category 2");
//
//        screen.addPreference(categ2);
//
//
//        CheckBoxPreference chb5 = new CheckBoxPreference(getActivity());
//        chb5.setKey("chb5");
//        chb5.setTitle("CheckBox 5");
//        chb5.setSummary("Description of checkbox 5");
//
//        categ2.addPreference(chb5);
//
//
//        CheckBoxPreference chb6 = new CheckBoxPreference(getActivity());
//        chb6.setKey("chb6");
//        chb6.setTitle("CheckBox 6");
//        chb6.setSummary("Description of checkbox 6");
//
//        categ2.addPreference(chb6);
//
//        rootScreen.addPreference(screen);
//
//        list.setDependency("chb1");
//        screen.setDependency("chb2");

//        // код из прошлого урока для связи активности categ2 и значения chb3
//        categ2.setEnabled(chb3.isChecked());
//        chb3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                categ2.setEnabled(chb3.isChecked());
//                return false;
//            }
//        });
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
