package com.digitech.digitalwellbeing.notification;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationSender extends AsyncTask<Void, Void, Void> {
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAMqiiv4Q:APA91bE4KxokAqAbM8fM8uapyGP1fFee4PRkxJC1HsKN5MYUFP8D3D9P1vea34yBBtEMtg2z1vW-Cah6OgwBdMdHkJs3eBPUyV9nfIms30msZeeBJ_bvUYwGCS9qDn6UwTs4J4zmPSEk";

    private static final String CONTENT_TYPE = "application/json";
    private String token;
    private String title;
    private String body;

    public NotificationSender(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(FCM_API);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE);
            conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);

            // Create JSON payload
            String jsonBody = "{\"to\":\"" + token + "\",\"notification\":{\"title\":\"" + title + "\",\"body\":\"" + body + "\"}}";

            // Send POST request
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonBody.getBytes());
            outputStream.flush();
            outputStream.close();

            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("NotificationSender", "Notification sent successfully: " + response.toString());
            } else {
                Log.e("NotificationSender", "Failed to send notification. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("NotificationSender", "Exception: " + e.getMessage());
        }
        return null;
    }
}
