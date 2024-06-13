package com.digitech.digitalwellbeing.childuser;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppUsageStatsHelper {
    private static final String TAG = "AppUsageStatsHelper";
    private Context context;

    public AppUsageStatsHelper(Context context) {
        this.context = context;
    }

    public ArrayList<DigitalWelbeingDataObject> getAppUsageStats() {

        ArrayList<DigitalWelbeingDataObject> DWBD = new ArrayList<>();

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            for (UsageStats usageStats : usageStatsList) {
                // Get the package name and total time in foreground
                String packageName = usageStats.getPackageName();
                //String appName = getAppNameFromPackage(context, packageName);



                long timeInForeground = usageStats.getTotalTimeInForeground();
                Log.d(TAG, "" +
                        ": " + packageName + "  " + timeInForeground);

                long usageTime = usageStats.getLastTimeUsed();
                if (usageTime >= startTime && usageTime <= endTime) {

                    if (timeInForeground > 60000) {
                        String appName = null;
                        PackageManager packageManager = context.getPackageManager();
                        String applicationName = null;
                        try {
                            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                            if (applicationInfo != null) {
                                applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "getAppUsageStats: AppName" + applicationName);
                        DigitalWelbeingDataObject DB = new DigitalWelbeingDataObject();
                        DB.setAppName(applicationName);
                        DB.setPackgeName(packageName);
                        DB.setTime(timeInForeground + "");
                        DWBD.add(DB);
                    }
                }

                // You can do further processing with packageName and timeInForeground here
                // For example, you can store it in a database or display it in your UI
            }
        }
        return DWBD;
    }


    public static String getAppNameFromPackage(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            CharSequence appName = packageManager.getApplicationLabel(applicationInfo);
            if (!TextUtils.isEmpty(appName)) {
                return appName.toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
