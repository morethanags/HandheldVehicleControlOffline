package com.huntloc.handheldvehiclecontroloffline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huntloc.handheldvehiclecontroloffline.model.SQLiteHelper;
import com.huntloc.handheldvehiclecontroloffline.model.Vehicle;
import com.huntloc.handheldvehiclecontroloffline.model.VehicleLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VehicleActivity extends AppCompatActivity {
    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy"), format1 = new SimpleDateFormat("MMM dd yyyy");
    String VEHICLEID, PLATE, TYPE, CONTRACTOR;
    private ImageView imageView_SecurityApproval, imageView_EnvironmentalApproval, imageView_SafetyApproval;
    private TextView textView_Plate, textView_Contractor, textView_Type, textView_Category,
            textView_Maker_Model_Year, textView_OwnershipCard, textView_SecurityApproval,
            textView_EnvironmentalApproval, textView_SafetyApproval;
    private TextView textView_InsuranceExpiry, textView_SoatExpiry;
    private ImageView imageView_Insurance, imageView_Soat;
    private Button button_Entrance, button_Exit;
    private Spinner destination;
    private boolean enabled = true;
    private LinearLayout delivery;
    private TextView textView_arrival, textView_days;
    private ImageView  imageView_arrival;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String response = intent.getStringExtra(HandheldFragment.VEHICLE_MESSAGE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        View view = findViewById(android.R.id.content);

        button_Entrance = (Button) view.findViewById(R.id.button_Entrance);
        button_Entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sendLogRequest(VEHICLEID, PLATE, 1, TYPE, CONTRACTOR);
            }
        });
        button_Exit = (Button) view.findViewById(R.id.button_Exit);
        button_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sendLogRequest(VEHICLEID, PLATE, 0, TYPE, CONTRACTOR);
            }
        });
        textView_Plate = (TextView) view
                .findViewById(R.id.textView_Plate);
        textView_Contractor = (TextView) view
                .findViewById(R.id.textView_Contractor);
        textView_Type = (TextView) view
                .findViewById(R.id.textView_Type);


        textView_Category = (TextView) view
                .findViewById(R.id.textView_Category);
        textView_Maker_Model_Year = (TextView) view
                .findViewById(R.id.textView_Maker_Model_Year);
        textView_OwnershipCard = (TextView) view
                .findViewById(R.id.textView_OwnershipCard);


        textView_arrival = (TextView) view
                .findViewById(R.id.textView_arrival);
        imageView_arrival = (ImageView) view
                .findViewById(R.id.imageView_arrival);
        textView_days = (TextView) view
                .findViewById(R.id.textView_days);

        imageView_Insurance = (ImageView) view
                .findViewById(R.id.imageView_Insurance);
        textView_InsuranceExpiry = (TextView) view
                .findViewById(R.id.textView_InsuranceExpiry);


        imageView_Soat = (ImageView) view
                .findViewById(R.id.imageView_Soat);
        textView_SoatExpiry = (TextView) view
                .findViewById(R.id.textView_SoatExpiry);

        textView_SecurityApproval = (TextView) view
                .findViewById(R.id.textView_SecurityApproval);
        imageView_SecurityApproval = (ImageView) view
                .findViewById(R.id.imageView_SecurityApproval);

        textView_EnvironmentalApproval = (TextView) view
                .findViewById(R.id.textView_EnvironmentalApproval);
        imageView_EnvironmentalApproval = (ImageView) view
                .findViewById(R.id.imageView_EnvironmentalApproval);

      textView_SafetyApproval = (TextView) view
                .findViewById(R.id.textView_SafetyApproval);
        imageView_SafetyApproval = (ImageView) view
                .findViewById(R.id.imageView_SafetyApproval);


        destination = (Spinner) findViewById(R.id.spinner_destination);

        delivery = (LinearLayout) findViewById(R.id.delivery);


        displayVehicle(response);

        showDestinations();
        /*String serverURL = getResources().getString(R.string.service_url)
                + "/VehicleService/Retrieve/ByPlate/" + response;
        Log.d("url", serverURL);
        new QueryVehicleTask().execute(serverURL);
        String serverURL1 = getResources().getString(R.string.service_url)
                + "/VehicleLogService/Destinations";
        new QueryDestinationsTask().execute(serverURL1);*/

    }

    private void sendLogRequest(String GUID, String plate, int log, String type, String contractor) {
        java.util.Date date = new java.util.Date();
        VehicleLog vehicleLog = new VehicleLog(GUID, plate, log, date.getTime(), type, contractor);

        /*String serverURL = getResources().getString(
                R.string.service_url)
                + "/VehicleLogService/"
                + GUID
                + "/"
                + plate
                + "/"
                + log;*/
        if (log == 0) {
            int d = ((Destination) destination.getSelectedItem()).getId();
            if (d != 0) {
                vehicleLog.setDestination(d);
                /*serverURL = getResources().getString(
                        R.string.service_url)
                        + "/VehicleLogService/"
                        + GUID
                        + "/"
                        + plate
                        + "/"
                        + log + "/"
                        + d;*/

            }
        }
        SQLiteHelper db = new SQLiteHelper(this.getApplicationContext());
        db.insertVehicleLog(vehicleLog);

        String response = "1 " + (log==0?"Salida":"Entrada") + " Registrada";
        Toast.makeText(
                VehicleActivity.this, response, Toast.LENGTH_LONG)
                .show();

        NavUtils.navigateUpFromSameTask(VehicleActivity.this);
        //logOperation.execute(serverURL);
    }

    private void displayTitle(String plate, String type) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(plate);
        if (type != null) {
            actionBar.setSubtitle(type);
        }
    }

    private void displayVehicle(String plate) {
        try {
            SQLiteHelper db = new SQLiteHelper(this.getApplicationContext());
            Vehicle vehicle = db.selectVehicle(plate);

            JSONObject response = new JSONObject(vehicle.getJSONString());
            Log.d("response", response.toString());
            VEHICLEID = response.optString("VehicleId");
            PLATE = response.optString("Plate");
            CONTRACTOR = response.optString("Contractor");

            Log.d("vehicle", VEHICLEID + " " + PLATE);
            JSONObject requestType = response.getJSONObject("VehicleRequestType");
            TYPE = requestType.optString("Description");
            displayTitle(PLATE,TYPE);

            if (requestType.optInt("VehicleRequestTypeId") == 0) {
                delivery.setVisibility(View.VISIBLE);
                String days = response.isNull("DaysOfPermanency") ? "" : response.optInt("DaysOfPermanency") + "";
                textView_days.setText(days);
                if (!response.isNull("ArrivalDate")) {
                    Date d = format.parse(response.optString("ArrivalDate"));
                    textView_arrival.setText(format1.format(d));
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    Log.d("arrival", c.getTime().toString());

                    Calendar today = Calendar.getInstance();

                    today.set(Calendar.HOUR, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    Log.d("today", today.getTime().toString());
                    if (c.getTime().compareTo(today.getTime())==0) {Log.d("display","ok");
                        imageView_arrival.setImageResource(R.mipmap.ic_verified);
                        imageView_arrival.setColorFilter(ContextCompat.getColor(this, R.color.check));

                    }
                    else{
                        Log.d("display","warn");
                        imageView_arrival.setImageResource(R.mipmap.ic_warning);
                        imageView_arrival.setColorFilter(ContextCompat.getColor(this, R.color.warning));
                    }
                }
            } else {
                delivery.setVisibility(View.GONE);
            }
            textView_Plate.setText(response.optString("Plate"));
            textView_Contractor.setText(response.optString("Contractor"));

            JSONObject type = response.getJSONObject("VehicleType");
            JSONObject category = type.getJSONObject("VehicleCategory");

            textView_Type.setText(type.optString("Description"));
            textView_Category.setText(category.optString("Description"));
            String operator = response.isNull("Operator") ? "-" : response.optString("Operator");
            String make = response.isNull("Manufacturer") ? "" : response.optString("Manufacturer");
            String model = response.isNull("Model") ? "" : response.optString("Model");
            String year = response.isNull("ManufacturingYear") ? "" : response.optString("ManufacturingYear");
            textView_Maker_Model_Year.setText(make + " " + model + " " + year);

            //String ownershipCard = response.isNull("OwnershipCard") ? "-" : response.optString("OwnershipCard");
            //textView_OwnershipCard.setText(ownershipCard);
            if (!response.isNull("OwnershipCard")) {
                JSONObject ownershipCard = response.getJSONObject("OwnershipCard");
                String ownershipCardNumber = ownershipCard.isNull("OwnershipCardNumber") ? "-" : ownershipCard.optString("OwnershipCardNumber");
                textView_OwnershipCard.setText(ownershipCardNumber);
            } else {
                textView_OwnershipCard.setText("-");
            }

            /*if (!response.isNull("TechnicalInspection")) {
                s(response.getJSONObject("TechnicalInspection"), imageView_TechnicalInspection, textView_TechnicalInspection);
            } else {
                c(textView_TechnicalInspection, imageView_TechnicalInspection);
            }*/

            if (!response.isNull("Insurance")) {
                s(response.getJSONObject("Insurance"), imageView_Insurance, textView_InsuranceExpiry);
            } else {
                c(textView_InsuranceExpiry, imageView_Insurance);
            }

            if (!response.isNull("SOAT")) {
                s(response.getJSONObject("SOAT"), imageView_Soat, textView_SoatExpiry);
            } else {
                c(textView_SoatExpiry, imageView_Soat);
            }

            JSONObject securityapproval = response.getJSONObject("SecurityApproval");
            if (securityapproval.optBoolean("Validity") == true) {
                textView_SecurityApproval.setText("VALID");
                imageView_SecurityApproval.setImageResource(R.mipmap.ic_verified);
                imageView_SecurityApproval.setColorFilter(ContextCompat.getColor(this, R.color.check));
            } else {
                enabled = false;
                textView_SecurityApproval.setText("NOT VALID");
                imageView_SecurityApproval.setImageResource(R.mipmap.ic_error);
                imageView_SecurityApproval.setColorFilter(ContextCompat.getColor(this, R.color.error));
            }

            JSONObject envapproval = response.getJSONObject("EmissionsCertificate");
            if (envapproval.optBoolean("Validity") == true) {
                textView_EnvironmentalApproval.setText("VALID");
                imageView_EnvironmentalApproval.setImageResource(R.mipmap.ic_verified);
                imageView_EnvironmentalApproval.setColorFilter(ContextCompat.getColor(this, R.color.check));
            } else {
                enabled = false;
                textView_EnvironmentalApproval.setText("NOT VALID");
                imageView_EnvironmentalApproval.setImageResource(R.mipmap.ic_error);
                imageView_EnvironmentalApproval.setColorFilter(ContextCompat.getColor(this, R.color.error));
            }

           JSONObject safetyapproval = response.getJSONObject("SafetyApproval");
            if (safetyapproval.optBoolean("Validity") == true) {
                textView_SafetyApproval.setText("VALID");
                imageView_SafetyApproval.setImageResource(R.mipmap.ic_verified);
                imageView_SafetyApproval.setColorFilter(ContextCompat.getColor(this, R.color.check));
            }
            else {
                enabled = false;
                textView_SafetyApproval.setText("NOT VALID");
                imageView_SafetyApproval.setImageResource(R.mipmap.ic_error);
                imageView_SafetyApproval.setColorFilter(ContextCompat.getColor(this, R.color.error));
            }
            button_Entrance.setEnabled(enabled);
            button_Exit.setEnabled(enabled);


        } catch (Exception je) {
            Log.d("JSONException", je.toString());
        }
    }

    //show date and image
    private void s(String p, TextView t, ImageView i) {
        Calendar monthAhead = Calendar.getInstance();
        monthAhead.add(Calendar.MONTH, 1);
        monthAhead.add(Calendar.DATE, 1);
        monthAhead.set(Calendar.HOUR, 0);
        monthAhead.set(Calendar.MINUTE, 0);
        monthAhead.set(Calendar.SECOND, 0);
        monthAhead.set(Calendar.HOUR_OF_DAY, 0);
        try {
            Date ExpirationDate = format.parse(p);
            t.setText(format1.format(ExpirationDate));
            Calendar c = Calendar.getInstance();
            c.setTime(ExpirationDate);
            c.add(Calendar.DATE, 1);
            if (c.getTime().before(Calendar.getInstance().getTime())) {
                i.setImageResource(R.mipmap.ic_error);
                i.setColorFilter(ContextCompat.getColor(this, R.color.error));
                enabled = false;
            } else {
                if (c.getTime().before(monthAhead.getTime())) {// a un mes
                    i.setImageResource(R.mipmap.ic_warning);
                    i.setColorFilter(ContextCompat.getColor(this, R.color.warning));
                } else {
                    i.setImageResource(R.mipmap.ic_verified);
                    i.setColorFilter(ContextCompat.getColor(this, R.color.check));
                }
            }
        } catch (ParseException pe) {
        }
    }

    //show certificate and company, date and image
    private void s(JSONObject certificate, ImageView i, TextView te) {
        if (certificate != null && !certificate.isNull("ExpirationDate")) {
            s(certificate.optString("ExpirationDate"), te, i);
        } else {
            c(te, i);
        }
    }

    //clear textview and imageview
    private void c(TextView t, ImageView i) {
        t.setText("-");
        i.setImageResource(0);
    }

    private void showDestinations() {
        try{
        SQLiteHelper db = new SQLiteHelper(this.getApplicationContext());
        String destinations = db.selectVehicleLogDestination();
        JSONArray array =   new JSONArray(destinations);
        List<Destination> list = new ArrayList<>();
        try {
            list.add(new Destination("DESTINO", 0));
            for (int i = 0; i < array.length(); i++) {
                list.add(new Destination(array.getJSONObject(i).getString("Description"), array.getJSONObject(i).getInt("VehicleLogDestinationId")));
            }
        } catch (Exception e) {
        }
        ArrayAdapter<Destination> adapter = new ArrayAdapter<Destination>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinner_destination)).setAdapter(adapter);}
        catch (Exception e){}
    }



    /*private class QueryVehicleTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @SuppressWarnings("unchecked")
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());

            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        protected void onPostExecute(String result) {
            Log.d("Result", result);
            try {
                if (result != null && !result.equals("")) {
                    VehicleActivity.this.displayVehicle(result);
                    //JSONObject jsonResponse = new JSONObject(result);
                }
            } catch (Exception ex) {

            }
        }
    }

    private class QueryDestinationsTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @SuppressWarnings("unchecked")
        protected String doInBackground(String... args) {
            String toReturn = "";
            StringBuilder result = new StringBuilder();
            try {

                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                toReturn = result.toString();
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            } finally {
                urlConnection.disconnect();
            }
            return toReturn;
        }

        protected void onPostExecute(String result) {
            try {
                if (result != null && !result.equals("")) {
                    JSONArray jsonResponse = new JSONArray(result);
                    if (jsonResponse.length() > 0) {
                        VehicleActivity.this.showDestinations(jsonResponse);
                        return;
                    }
                }
            } catch (Exception ex) {
                Log.d("Exception", ex.getMessage());
            }
        }
    }
*/
    public class Destination {
        private int id;
        private String desc;

        public Destination(String desc, int id) {
            this.id = id;
            this.desc = desc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String toString() {
            return this.desc;
        }
    }

}
