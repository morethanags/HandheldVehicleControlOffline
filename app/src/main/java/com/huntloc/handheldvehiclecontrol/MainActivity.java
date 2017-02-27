package com.huntloc.handheldvehiclecontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static long back_pressed;
    private EditText editText_Plate;
    private Button button_Ckeck, button_Clear;
    private ImageView imageView_EnvironmentalApproval, imageView_TechnicalInspection, imageView_EmissionCertificate, imageView_Photo;
    private TextView textView_Plate, textView_OwnerShipCard, textView_Maker, textView_Model_Year, textView_CategoryClass, textView_Colour, textView_EnvironmentalApproval, textView_TechnicalInspection,
            textView_EmissionCertificate;
    private TextView  textView_Insurance, textView_InsuranceExpiry, textView_Soat, textView_SoatExpiry, textView_Enabling, textView_EnablingExpiry;
    private ImageView imageView_Insurance, imageView_Soat, imageView_Enabling;

    SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy"), format1 = new SimpleDateFormat("MMM dd yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(android.R.id.content);

        editText_Plate = (EditText) view
                .findViewById(R.id.editText_Plate);
        editText_Plate.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (editText_Plate.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this,
                                "Enter a vehicle plate i.e. ABC-123",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        sendRequest();
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
                    Toast.makeText(MainActivity.this,
                            "Enter a vehicle plate i.e. ABC-123",
                            Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest();
                    hideKeyboard();
                }
            }
        });

        button_Clear = (Button) view.findViewById(R.id.button_Clear);
        button_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearVehicle();
                hideKeyboard();
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
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void sendRequest() {
        String serverURL = getResources().getString(R.string.service_url)
                + "/VehicleService/ByPlate/" + editText_Plate.getText().toString();
        Log.d("URL vehicle", serverURL);
        new QueryVehicleTask().execute(serverURL);
    }

    protected void displayVehicle(JSONObject response) {
        try {
            Log.d("response", response.toString());
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
        } catch (JSONException je) {
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

    protected void clearVehicle() {
        textView_Plate.setText("");
        textView_OwnerShipCard.setText("");
        textView_Maker.setText("");
        textView_Model_Year.setText("");
        textView_Colour.setText("");
        textView_CategoryClass.setText("");
        textView_EnvironmentalApproval.setText("");
        imageView_EnvironmentalApproval.setImageResource(0);
        textView_TechnicalInspection.setText("");
        imageView_TechnicalInspection.setImageResource(0);

        textView_Insurance.setText("");
        textView_InsuranceExpiry.setText("");
        imageView_Insurance.setImageResource(0);

        textView_Soat.setText("");
        textView_SoatExpiry.setText("");
        imageView_Soat.setImageResource(0);

        textView_Enabling.setText("");
        textView_EnablingExpiry.setText("");
        imageView_Enabling.setImageResource(0);

        imageView_Photo.setImageResource(R.mipmap.ic_no_image);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
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
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Vehicle Control");
                        alertDialogBuilder.setMessage("Red WiFi no Disponible");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        alertDialogBuilder.create().show();
                    }
                });
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        protected void onPostExecute(String result) {
            try {
                if (!result.equals("")) {
                    JSONObject jsonResponse = new JSONObject(result);
                    String plate = jsonResponse.optString("Plate");
                    if (plate.equals("null")) {
                        MainActivity.this
                                .runOnUiThread(new Runnable() {
                                    public void run() {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                                });
                        MainActivity.this.clearVehicle();
                    } else {
                        MainActivity.this.displayVehicle(jsonResponse);
                    }
                }
            } catch (Exception ex) {

            }
        }
    }
}
