package com.digitech.digitalwellbeing.utiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;

public class SessionClass {


    public static SharedPreferences prefs;

    public static void setIconToApps(ImageView imageView, String packageName, Context context) {
        try
        {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            imageView.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static String formatDurationHrs(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        return String.valueOf(hours);
    }

    public static String formatDurationMins(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;

        return String.valueOf(minutes);
    }

}
