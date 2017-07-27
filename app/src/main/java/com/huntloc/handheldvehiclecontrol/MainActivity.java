package com.huntloc.handheldvehiclecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
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
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String VEHICLE_MESSAGE = "com.huntloc.handheldvehiclecontrol.VEHICLE";
    private static long back_pressed;
    private static EditText editText_Plate;
    private Button button_Ckeck;
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
                                "Enter a vehicle plate i.e. ABC123",
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
                            "Enter a vehicle plate i.e. ABC123",
                            Toast.LENGTH_SHORT).show();
                } else {

                    sendRequest();
                    hideKeyboard();
                }
            }
        });
        Log.d("MainActivity Intent", getIntent().getAction());
        //si viene de la notificacion...
        /*if(getIntent().getExtras()!=null && getIntent().getExtras().getString("plate")!= null){
            editText_Plate.setText(getIntent().getExtras().getString("plate"));
            sendRequest();
        }*/
    }

   /* @Override
    protected void onNewIntent(Intent intent) {
        Log.d("MainActivity Intent", intent.getAction());
        if(intent.getExtras().getString("plate")!= null){
            editText_Plate.setText(intent.getExtras().getString("plate"));
            sendRequest();
        }
    }*/

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void sendRequest() {
        String serverURL = getResources().getString(R.string.service_url)
                + "/VehicleService/Retrieve/ByPlate/" + editText_Plate.getText().toString();
        Log.d("URL vehicle", serverURL);
        new QueryVehicleTask().execute(serverURL);
    }

    protected void displayVehicle(String plate) {
        Intent intent = new Intent(this,
                VehicleActivity.class);
        intent.putExtra(VEHICLE_MESSAGE, plate);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        //editText_Plate.setText("");
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
                Log.d("Exception",e.getMessage());
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
            Log.d("Result", result);
            try {
                if (result!=null && !result.equals("")) {

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
                    } else {
                        MainActivity.this.displayVehicle(plate);
                    }
                }
            } catch (Exception ex) {

            }
        }
    }


}
