package com.huntloc.handheldvehiclecontroloffline;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.huntloc.handheldvehiclecontroloffline.model.SQLiteHelper;
import com.huntloc.handheldvehiclecontroloffline.model.VehicleLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ExitFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{


    private OnExitFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView exitList = null;
    ArrayList<HashMap<String, String>> list = null;
    public ExitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        updateExits();
    }
    public static ExitFragment newInstance(String param1, String param2) {
        ExitFragment fragment = new ExitFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exit,
                container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.list_Exit_Layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        updateExits();        return view;
    }
    private void updateExits() {
        try {
            exitList = (ListView) getView().findViewById(R.id.list_Exit);
        } catch (NullPointerException e) {
            return;
        }
        try {
            SQLiteHelper db = new SQLiteHelper(getContext());
            List<VehicleLog> records = db.getLog("0");
            list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < records.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                Log.d("list", records.get(i).toString());
                item.put("Plate", records.get(i).getPlate());
                item.put(
                        "Type",
                        records.get(i).getType());
                item.put(
                        "Contractor",
                        records.get(i).getContractor());

                String dateString = DateFormat.format("E, MMM dd, h:mm aa",
                        new Date(records.get(i).getTime())).toString();
                item.put("Time", dateString);
                list.add(item);
            }
            String[] columns = new String[] {
                    "Plate", "Type", "Contractor", "Time" };
            int[] renderTo = new int[] {
                    R.id.plate, R.id.type,
                    R.id.contractor, R.id.time };
            ListAdapter listAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.journallog_list_row, columns, renderTo);

            exitList.setAdapter(listAdapter);
        } catch (Exception e) {
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExitFragmentInteractionListener) {
            mListener = (OnExitFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnExitFragmentInteractionListener {
        // TODO: Update argument type and name
        void onExitFragmentInteraction();
    }
}
