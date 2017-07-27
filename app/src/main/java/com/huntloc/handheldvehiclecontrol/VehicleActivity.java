package com.huntloc.handheldvehiclecontrol;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VehicleActivity extends AppCompatActivity {
    private ImageView imageView_TechnicalInspection, imageView_EnvironmentalApproval, imageView_SafetyApproval,imageView_Photo;
    private TextView textView_Plate, textView_Contractor, textView_Type,
            textView_Maker_Model_Year, textView_OwnerShipCard, textView_TechnicalInspection,
            textView_EnvironmentalApproval, textView_SafetyApproval;
    private TextView textView_InsuranceExpiry, textView_SoatExpiry;
    private ImageView imageView_Insurance, imageView_Soat;
    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy"), format1 = new SimpleDateFormat("MMM dd yyyy");
    String GUID, PLATE;
    private Button button_Entrance, button_Exit;
    LogOperation logOperation = null;
    private boolean  enabled = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String response = intent.getStringExtra(MainActivity.VEHICLE_MESSAGE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        View view = findViewById(android.R.id.content);
        logOperation = new LogOperation();
        button_Entrance = (Button) view.findViewById(R.id.button_Entrance);
        button_Entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogRequest(GUID, PLATE, 1);
            }
        });
        button_Exit = (Button) view.findViewById(R.id.button_Exit);
        button_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogRequest(GUID, PLATE, 0);
            }
        });
        textView_Plate = (TextView) view
                .findViewById(R.id.textView_Plate);
        textView_Contractor = (TextView) view
                .findViewById(R.id.textView_Contractor);
        textView_Type = (TextView) view
                .findViewById(R.id.textView_Type);
        textView_Maker_Model_Year = (TextView) view
                .findViewById(R.id.textView_Maker_Model_Year);
        textView_OwnerShipCard = (TextView) view
                .findViewById(R.id.textView_OwnerShipCard);
        textView_TechnicalInspection = (TextView) view
                .findViewById(R.id.textView_TechnicalInspection);
        imageView_TechnicalInspection = (ImageView) view
                .findViewById(R.id.imageView_TechnicalInspection);

        imageView_Insurance = (ImageView) view
                .findViewById(R.id.imageView_Insurance);
        textView_InsuranceExpiry = (TextView) view
                .findViewById(R.id.textView_InsuranceExpiry);


        imageView_Soat = (ImageView) view
                .findViewById(R.id.imageView_Soat);
        textView_SoatExpiry = (TextView) view
                .findViewById(R.id.textView_SoatExpiry);

        textView_EnvironmentalApproval = (TextView) view
                .findViewById(R.id.textView_EnvironmentalApproval);
        imageView_EnvironmentalApproval = (ImageView) view
                .findViewById(R.id.imageView_EnvironmentalApproval);

        textView_SafetyApproval = (TextView) view
                .findViewById(R.id.textView_SafetyApproval);
        imageView_SafetyApproval = (ImageView) view
                .findViewById(R.id.imageView_SafetyApproval);

        imageView_Photo = (ImageView) view
                .findViewById(R.id.imageView_Photo);

        String serverURL = getResources().getString(R.string.service_url)
                + "/VehicleService/Retrieve/ByPlate/" + response;
        Log.d("URL vehicle", serverURL);
        new QueryVehicleTask().execute(serverURL);

    }

    private void sendLogRequest(String GUID, String plate, int log) {
        String serverURL = getResources().getString(
                R.string.service_url)
                + "/VehicleLogService/"
                + GUID
                + "/"
                + plate
                + "/"
                + log;
        logOperation.execute(serverURL);
    }

    private void displayTitle(String plate, String contractor) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(plate);
        actionBar.setSubtitle(contractor);
    }

    private void displayVehicle(String result) {
        try {
            JSONObject response = new JSONObject(result);
            Log.d("response", response.toString());
            GUID = response.optString("GUID");
            PLATE = response.optString("Plate");

            textView_Plate.setText(response.optString("Plate"));
            textView_Contractor.setText(response.optString("Contractor"));
            JSONObject type = response.getJSONObject("Type");
            JSONObject category = type.getJSONObject("Category");

            textView_Type.setText(type.optString("Description"));
            displayTitle(response.optString("Plate"), response.optString("Contractor"));
            String make = response.isNull("Make") ? "" : response.optString("Make");
            String model = response.isNull("Model") ? "" : response.optString("Model");
            String year = response.isNull("ManufacturingYear") ? "" : response.optString("ManufacturingYear");
            textView_Maker_Model_Year.setText(make + " " + model + " " + year);

            String ownershipCard = response.isNull("OwnershipCard") ? "-" : response.optString("OwnershipCard");
            textView_OwnerShipCard.setText(ownershipCard);

            if (!response.isNull("TechnicalInspectionDate")) {
                s(response.optString("TechnicalInspectionDate"), textView_TechnicalInspection, imageView_TechnicalInspection);
            } else {
                c(textView_TechnicalInspection, imageView_TechnicalInspection);
            }
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

            if (!response.isNull("Photo")) {
                byte[] byteArray;
                Bitmap bitmap;
                byteArray = Base64
                        .decode(response.optString("Photo"), 0);
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView_Photo.setImageBitmap(bitmap);
            } else {
                Log.d("Photo", "no image");
                imageView_Photo.setImageResource(R.mipmap.ic_no_image);
            }
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
            Date TechnicalInspetionDate = format.parse(p);
            t.setText(format1.format(TechnicalInspetionDate));
            Calendar c = Calendar.getInstance();
            c.setTime(TechnicalInspetionDate);
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
        if (certificate != null && !certificate.isNull("Expiry")) {
            s(certificate.optString("Expiry"), te, i);
        } else {
            c(te, i);
        }
    }

    //clear textview and imageview
    private void c(TextView t, ImageView i) {
        t.setText("-");
        i.setImageResource(0);
    }

    private class LogOperation extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @SuppressWarnings("unchecked")
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);
                Log.d("Log URL", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        protected void onPostExecute(String result) {

            try {
                JSONObject jsonResponse = new JSONObject(result);

                String log = jsonResponse.optString("log")
                        .contains("Entry") ? "Entrada" : "Salida";
                String response = jsonResponse.optString("records") + " " + log
                        + " Registrada";

                Toast.makeText(
                        VehicleActivity.this, response, Toast.LENGTH_LONG)
                        .show();

                NavUtils.navigateUpFromSameTask(VehicleActivity.this);

					/*
                     * Intent intent = new
					 * Intent(journalSectionFragmentWeakReference
					 * .get().getActivity(), MainActivity.class);
					 * journalSectionFragmentWeakReference
					 * .get().getActivity().startActivity(intent);
					 */
            } catch (JSONException e) {
            }

        }
    }

    private class QueryVehicleTask extends AsyncTask<String, String, String> {
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
}
