package com.huntloc.handheldvehiclecontroloffline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huntloc.handheldvehiclecontroloffline.model.SQLiteHelper;
import com.huntloc.handheldvehiclecontroloffline.model.Vehicle;
import com.huntloc.handheldvehiclecontroloffline.model.VehicleLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String VEHICLE_MESSAGE = "com.huntloc.handheldvehiclecontrol.VEHICLE";
    private static long back_pressed;
    private static EditText editText_Plate;
    public static final String PREFS_NAME = "HandheldVehicleOfflinePrefsFile";
    private Button button_Ckeck;
    ProgressDialog progress;
    TextView textView_lastupdate_date;

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
                    Toast.makeText(MainActivity.this,
                            "Enter a vehicle plate i.e. ABC123",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //sendRequest();
                    findVehicle();
                    hideKeyboard();
                }
            }
        });
        Log.d("MainActivity Intent", getIntent().getAction());
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);
        progress.setCanceledOnTouchOutside(false);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        textView_lastupdate_date = (TextView) findViewById(R.id.textView_lastupdate_date);
        textView_lastupdate_date.setText("Última actualización: " + getSharedPreferences(PREFS_NAME, 0).getString("lastupdate", "No se ha sincronizado"));

        //si viene de la notificacion...
        /*if(getIntent().getExtras()!=null && getIntent().getExtras().getString("plate")!= null){
            editText_Plate.setText(getIntent().getExtras().getString("plate"));
            sendRequest();
        }*/
    }

    private void showLastUpdate() {

        textView_lastupdate_date.setText("Última actualización: "
                + getSharedPreferences(PREFS_NAME, 0).getString("lastupdate",
                "No se ha sincronizado"));
    }

    /* @Override
     protected void onNewIntent(Intent intent) {
         Log.d("MainActivity Intent", intent.getAction());
         if(intent.getExtras().getString("plate")!= null){
             editText_Plate.setText(intent.getExtras().getString("plate"));
             sendRequest();
         }
     }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                updateVehicles();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateVehicles() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (!ni.isConnected()) {
                    Toast.makeText(this, "Por favor conectar a red PeruLNG.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
        }
        String serverURL = getResources().getString(R.string.service_url)
                + "/VehicleService/Retrieve";
        Log.d("URL vehicle", serverURL);

        new QueryVehiclesTask().execute(serverURL);
        progress.setMessage(getResources().getString(
                R.string.action_update_message));
        progress.show();

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
private void findVehicle(){
    SQLiteHelper db = new SQLiteHelper(MainActivity.this.getApplicationContext());
    String plate = editText_Plate.getText().toString();
    Vehicle vehicle = db.selectVehicle(plate);
    if (vehicle!=null) {
        displayVehicle(plate);
    } else {
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
}
    private void sendRequest() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (!ni.isConnected()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Vehicle Control");
                    alertDialogBuilder.setMessage("Red WiFi no Disponible");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialogBuilder.create().show();
                    return;
                }
        }
        SQLiteHelper db = new SQLiteHelper(MainActivity.this.getApplicationContext());
        List<VehicleLog> records = db.getAllLog();
        if (records.size() > 0) {
            progress.setMessage(getResources().getString(
                    R.string.action_send_message));
            progress.show();

            for (int i = 0; i < records.size(); i++) {

                String serverURL = getResources().getString(
                        R.string.service_url)
                        + "/VehicleLogService/Offline/"
                        + records.get(i).getId()
                        + "/"
                        +  records.get(i).getPlate()
                        + "/"
                        +  records.get(i).getLog();
                if(records.get(i).getDestination()>0){
                    serverURL = serverURL + "/"+records.get(i).getDestination() ;
                }

                LogOperation journalTask = new LogOperation();
                journalTask.setParent(this);
                journalTask.setProgressDialog(progress);
                journalTask.setIndex(i);
                journalTask.setTotal(records.size());
                journalTask.execute(serverURL);
                Log.d("Send", serverURL);
            }

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Vehicle Control");
            alertDialogBuilder.setMessage("No hay marcaciones registradas");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.create().show();

        }
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

    private class QueryVehiclesTask extends AsyncTask<String, String, String> {
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
                progress.dismiss();
                Log.d("Exception", e.getMessage());
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
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArrayVehicles = jsonObject.getJSONArray("Vehicles");
                JSONArray jsonArrayDestinations = jsonObject.getJSONArray("Destinations");

                Log.d("vehicles", jsonArrayVehicles.length() + "");
                Log.d("destinationa", jsonArrayDestinations.length() + "");
                SQLiteHelper db = new SQLiteHelper(MainActivity.this.getApplicationContext());
                db.deleteVehicles();
                db.insertVehicleLogDestination(jsonArrayDestinations.toString());

                for (int i = 0; i < jsonArrayVehicles.length(); i++) {
                    db.insertVehicle(new Vehicle(jsonArrayVehicles.getJSONObject(i).optString("Plate"),
                            jsonArrayVehicles.getJSONObject(i).toString()));
                }
                progress.dismiss();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        SimpleDateFormat newDateFormat = new SimpleDateFormat(
                                "EEEE, d MMMM yyyy h:mm a");
                        Calendar today = Calendar.getInstance();
                        SharedPreferences settings = getSharedPreferences(
                                PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("lastupdate",
                                newDateFormat.format(today.getTime()));
                        editor.commit();

                        MainActivity.this.showLastUpdate();
                        //Concatenate task
                        MainActivity.this.sendRequest();
                    }
                });
            } catch (Exception e) {
                Log.d(e.getClass().toString(), e.getMessage());
                /*
				 * MainActivity.this.runOnUiThread(new Runnable() { public void
				 * run() { Toast.makeText(MainActivity.this,
				 * "Couldn't Complete Update", Toast.LENGTH_LONG).show(); } });
				 */
            }

        }
    }

    private class LogOperation extends AsyncTask<String, Integer, Void> {
        HttpURLConnection urlConnection;
        ProgressDialog progress;
        int index, total;
        MainActivity parent;
        public void setParent(MainActivity parent) {
            this.parent = parent;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void setProgressDialog(ProgressDialog progress) {
            this.progress = progress;
        }

        public void setIndex(int index) {
            this.index = index;
        }
        @SuppressWarnings("unchecked")
        protected Void doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);

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
            return null;
        }

        protected void onPostExecute(Void result) {

            try {

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void
                    run() {
                        publishProgress(0);
                    }
                });

            } catch (Exception e) {
            }

        }

        protected void onProgressUpdate(Integer... _progress) {
            if (index == total - 1) {
                SQLiteHelper db = new SQLiteHelper(MainActivity.this.getApplicationContext());
                db.deleteVehicleLog();

                progress.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Vehicle Control");
                alertDialogBuilder.setMessage("Actualización Completa");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.create().show();
            }
        }
    }
}
