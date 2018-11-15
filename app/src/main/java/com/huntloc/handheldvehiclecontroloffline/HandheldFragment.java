package com.huntloc.handheldvehiclecontroloffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huntloc.handheldvehiclecontroloffline.model.SQLiteHelper;
import com.huntloc.handheldvehiclecontroloffline.model.Vehicle;


public class HandheldFragment extends Fragment {

    private static EditText editText_Plate;
    private Button button_Ckeck;public static final String VEHICLE_MESSAGE = "com.huntloc.handheldvehiclecontrol.VEHICLE";
    private OnHandheldFragmentInteractionListener mListener;

    public HandheldFragment() {
        // Required empty public constructor
    }

    public static HandheldFragment newInstance(String param1, String param2) {
        HandheldFragment fragment = new HandheldFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handheld, container, false);
        editText_Plate = (EditText) view
                .findViewById(R.id.editText_Plate);
        editText_Plate.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (editText_Plate.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(),
                                "Enter a vehicle plate i.e. ABC123",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //sendRequest();
                        findVehicle();
                    }
                    return true;
                }
                return false;
            }
        });

        button_Ckeck = (Button) view.findViewById(R.id.button_Check);
        button_Ckeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_Plate.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),
                            "Enter a vehicle plate i.e. ABC123",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //sendRequest();
                    findVehicle();
                    hideKeyboard();
                }
            }
        });


        return view;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    protected void displayVehicle(String plate) {
        Intent intent = new Intent(getActivity(),
                VehicleActivity.class);
        intent.putExtra(VEHICLE_MESSAGE, plate);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        //editText_Plate.setText("");
    }

    private void findVehicle(){
        SQLiteHelper db = new SQLiteHelper(getActivity().getApplicationContext());
        String plate = editText_Plate.getText().toString();
        Vehicle vehicle = db.selectVehicle(plate);
        if (vehicle!=null) {
            displayVehicle(plate);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Vehicle Control");
            alertDialogBuilder.setMessage("Vehicle not found!");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialogBuilder.create().show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHandheldFragmentInteractionListener) {
            mListener = (OnHandheldFragmentInteractionListener) context;
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
    public interface OnHandheldFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHandheldFragmentInteraction();
    }
}
