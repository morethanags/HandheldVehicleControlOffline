package com.huntloc.handheldvehiclecontroloffline;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dmoran on 3/14/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String refreshedToken){
        String serverURL = getResources().getString(
                R.string.service_url)+ "/TabletService/Create";
        RegistrationTask  registrationTask = new RegistrationTask();
        registrationTask.execute(serverURL, refreshedToken);

    }
    private class RegistrationTask extends
            AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @SuppressWarnings("unchecked")
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject tablet =new JSONObject();
                tablet.put("RegistrationToken", args[1]);

                OutputStream out = urlConnection.getOutputStream();
                Log.d("json token", tablet.toString());
                out.write(tablet.toString().getBytes("UTF-8"));

                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (Exception e) {
                Log.d("Send exception", e.toString());
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        protected void onPostExecute(String result) {
            Log.d("Send result", result);
            try {
                Toast.makeText(getBaseContext(), "Tablet registered for notifications!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.d(e.getClass().toString(), e.getMessage());
            }
        }
    }

}
