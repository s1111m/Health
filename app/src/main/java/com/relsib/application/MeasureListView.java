package com.relsib.application;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.relsib.dao.DbModel;
import com.relsib.dao.TableMeasurment;


public class MeasureListView extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // private OnFragmentInteractionListener mListener;

    public MeasureListView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to MeasurmentFactory a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeasureListView.
     */

    public static MeasureListView newInstance(String param1, String param2) {
        MeasureListView fragment = new MeasureListView();
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.f_measure_listview_list, container, false);
        //View rootView = inflater.inflate(android.R.layout.simple_list_item_single_choice, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.measureListView);

        DbModel dbModel = new DbModel(getContext());
        final TableMeasurment tableMeasurment = new TableMeasurment(dbModel.getReadableDatabase());
        final CursorAdapter cursorAdapter = new CursorAdapter(getActivity(), tableMeasurment.makeCursor()) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.f_measure_listview_item, parent, false);
                // return null;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView measureDate = (TextView) view.findViewById(R.id.measure_date);
                TextView measureValue = (TextView) view.findViewById(R.id.measure_value);
                TextView measureNotice = (TextView) view.findViewById(R.id.measure_notice);
                // Populate fields with extracted properties
                measureDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                measureValue.setText(cursor.getString(cursor.getColumnIndexOrThrow("temperature")));
                measureNotice.setText(cursor.getString(cursor.getColumnIndexOrThrow("notice")));
            }
        };
        listView.setAdapter(cursorAdapter);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
