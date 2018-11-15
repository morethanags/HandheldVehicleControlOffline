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


public class EntranceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private OnEntranceFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView entranceList = null;
    ArrayList<HashMap<String, String>> list = null;
    public EntranceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        updateEntrances();
    }
    public static EntranceFragment newInstance(String param1, String param2) {
        EntranceFragment fragment = new EntranceFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance,
                container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.list_Entrance_Layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        updateEntrances();
        return view;
    }

    private void updateEntrances() {
        try {
            entranceList = (ListView) getView().findViewById(R.id.list_Entrance);
        } catch (NullPointerException e) {
            return;
        }
        try {
            SQLiteHelper db = new SQLiteHelper(getContext());
            List<VehicleLog> records = db.getLog("1");
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

            entranceList.setAdapter(listAdapter);
        } catch (Exception e) {
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEntranceFragmentInteractionListener) {
            mListener = (OnEntranceFragmentInteractionListener) context;
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


    public interface OnEntranceFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEntranceFragmentInteraction();
    }
}
