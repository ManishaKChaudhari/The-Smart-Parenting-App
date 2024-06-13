package com.digitech.digitalwellbeing.Registration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.digitech.digitalwellbeing.childuser.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start your application's main activity here
            Intent activityIntent = new Intent(context, MainActivity.class);
            // Set flags to ensure proper activity launch
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}