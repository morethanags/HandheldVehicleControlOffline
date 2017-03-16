package com.huntloc.handheldvehiclecontrol;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
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
    private ImageView imageView_EnvironmentalApproval, imageView_TechnicalInspection, imageView_EmissionCertificate, imageView_Photo;
    private TextView textView_Plate, textView_OwnerShipCard, textView_Maker, textView_Model_Year, textView_CategoryClass, textView_Colour, textView_EnvironmentalApproval, textView_TechnicalInspection,
            textView_EmissionCertificate;
    private TextView  textView_Insurance, textView_InsuranceExpiry, textView_Soat, textView_SoatExpiry, textView_Enabling, textView_EnablingExpiry;
    private ImageView imageView_Insurance, imageView_Soat, imageView_Enabling;
    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy"), format1 = new SimpleDateFormat("MMM dd yyyy");
    String GUID, PLATE;
    private Button button_Entrance, button_Exit;
    LogOperation logOperation = null;
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
                sendRequest(GUID, PLATE,1);
            }
        });
        button_Exit = (Button) view.findViewById(R.id.button_Exit);
        button_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(GUID,PLATE,0);
            }
        });
        textView_Plate = (TextView) view
                .findViewById(R.id.textView_Plate);
        textView_OwnerShipCard = (TextView) view
                .findViewById(R.id.textView_OwnerShipCard);
        textView_Maker = (TextView) view
                .findViewById(R.id.textView_Maker);
        textView_Model_Year = (TextView) view
                .findViewById(R.id.textView_Model_Year);
        textView_CategoryClass = (TextView) view
                .findViewById(R.id.textView_CategoryClass);
        textView_Colour = (TextView) view
                .findViewById(R.id.textView_Colour);
        textView_EnvironmentalApproval = (TextView) view
                .findViewById(R.id.textView_EnvironmentalApproval);
        imageView_EnvironmentalApproval = (ImageView) view
                .findViewById(R.id.imageView_EnvironmentalApproval);
        textView_TechnicalInspection = (TextView) view
                .findViewById(R.id.textView_TechnicalInspection);
        imageView_TechnicalInspection = (ImageView) view
                .findViewById(R.id.imageView_TechnicalInspection);
        textView_EmissionCertificate = (TextView) view
                .findViewById(R.id.textView_EmissionCertificate);
        imageView_EmissionCertificate = (ImageView) view
                .findViewById(R.id.imageView_EmissionCertificate);

        textView_Insurance = (TextView) view
                .findViewById(R.id.textView_Insurance);
        imageView_Insurance = (ImageView) view
                .findViewById(R.id.imageView_Insurance);
        textView_InsuranceExpiry = (TextView) view
                .findViewById(R.id.textView_InsuranceExpiry);

        textView_Soat = (TextView) view
                .findViewById(R.id.textView_Soat);
        imageView_Soat = (ImageView) view
                .findViewById(R.id.imageView_Soat);
        textView_SoatExpiry = (TextView) view
                .findViewById(R.id.textView_SoatExpiry);

        textView_Enabling = (TextView) view
                .findViewById(R.id.textView_Enabling);
        imageView_Enabling = (ImageView) view
                .findViewById(R.id.imageView_Enabling);
        textView_EnablingExpiry = (TextView) view
                .findViewById(R.id.textView_EnablingExpiry);

        imageView_Photo = (ImageView) view
                .findViewById(R.id.imageView_Photo);
        displayVehicle(response);

    }
    private void sendRequest(String GUID, String plate, int log){
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
    protected void displayVehicle(String result) {

        try {
            JSONObject response = new JSONObject(result);
            Log.d("response", response.toString());
            GUID = response.optString("GUID");
            PLATE = response.optString("Plate");
            textView_Plate.setText(response.optString("Plate"));
            textView_OwnerShipCard.setText(response.optString("OwnerShipCard"));
            textView_Maker.setText(response.optString("Make"));
            textView_Model_Year.setText(response.optString("Model") + " " + response.optString("Year"));
            textView_Colour.setText(response.optString("Colour"));
            textView_CategoryClass.setText(response.optString("CategoryClass"));

            JSONObject envapproval = response.getJSONObject("EnvironmentalApproval");
            textView_EnvironmentalApproval.setText(envapproval.optString("Description"));
            if (envapproval.optString("ID").equals("1")) {
                imageView_EnvironmentalApproval.setImageResource(R.mipmap.ic_verified);
                imageView_EnvironmentalApproval.setColorFilter(ContextCompat.getColor(this, R.color.check));
            } else {
                imageView_EnvironmentalApproval.setImageResource(R.mipmap.ic_error);
                imageView_EnvironmentalApproval.setColorFilter(ContextCompat.getColor(this, R.color.error));
            }

            if (!response.isNull("TechnicalInspetionExpiry")) {
                s(response.optString("TechnicalInspetionExpiry"), textView_TechnicalInspection, imageView_TechnicalInspection);
            } else {
                c(textView_TechnicalInspection, imageView_TechnicalInspection);
            }

            if (!response.isNull("EmissionCertificateExpiry")) {
                s(response.optString("EmissionCertificateExpiry"), textView_EmissionCertificate, imageView_EmissionCertificate);
            } else {
                c(textView_EmissionCertificate, imageView_EmissionCertificate);
            }

            s(response.getJSONObject("Insurance"),textView_Insurance,imageView_Insurance, textView_InsuranceExpiry);
            s(response.getJSONObject("SOAT"),textView_Soat,imageView_Soat, textView_SoatExpiry);
            s(response.getJSONObject("Enabling"),textView_Enabling,imageView_Enabling, textView_EnablingExpiry);

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
    private void s(JSONObject certificate, TextView t, ImageView i, TextView te){
        String text = "";
        if(!certificate.isNull("CertificateNumber")){
            text+=certificate.optString("CertificateNumber")+" ";
        }
        if(!certificate.isNull("Company")){
            text+=certificate.optString("Company");
        }
        t.setText(text);
        if (!certificate.isNull("Expiry")) {
            s(certificate.optString("Expiry"), te, i);
        } else {
            c(te, i);
        }

    }
    //clear textview and imageview
    private void c(TextView t, ImageView i) {
        t.setText("");
        i.setImageResource(0);
    }

    private class LogOperation extends
            AsyncTask<String, String, String> {
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
            }
            finally {
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
}
