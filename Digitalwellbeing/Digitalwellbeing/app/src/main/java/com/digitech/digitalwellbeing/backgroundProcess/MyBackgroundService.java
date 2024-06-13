package com.digitech.digitalwellbeing.backgroundProcess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyBackgroundService  extends Service {
    private static final String TAG = "MyBackgroundService";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Your task logic goes here
        // This method will be called each time the service is started

        Log.d(TAG, "onStartCommand: This is Excuting");
        return START_STICKY; // or other appropriate return value
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
