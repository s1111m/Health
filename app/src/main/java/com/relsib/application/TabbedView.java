package com.relsib.application;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import android.support.v4.app.Fragment;


public class TabbedView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TabbedView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabbedView.
     */
    // TODO: Rename and change types and number of parameters
    public static TabbedView newInstance(String param1, String param2) {
        TabbedView fragment = new TabbedView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.f_tabbed_view, container, false);
//        TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
//        tabHost.setup();
//        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
//        tabSpec.setContent(R.id.smartview);
//        tabSpec.setIndicator("Общее");
//        tabHost.addTab(tabSpec);
//        tabSpec = tabHost.newTabSpec("tag2");
//        tabSpec.setContent(R.id.linearLayout2);
//        tabSpec.setIndicator("График");
//        tabHost.addTab(tabSpec);
//        tabSpec = tabHost.newTabSpec("tag3");
//        tabSpec.setContent(R.id.linearLayout3);
//        tabSpec.setIndicator("Инфо");
//        tabHost.addTab(tabSpec);
//        tabHost.setCurrentTab(0);
        return rootView;
    }

}
