package com.digitech.digitalwellbeing.Registration;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.digitech.digitalwellbeing.backgroundProcess.MBroadcastReceiver;
import com.digitech.digitalwellbeing.childuser.MainActivity;
import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.notification.NotificationHelper;
import com.digitech.digitalwellbeing.notification.NotificationSender;
import com.digitech.digitalwellbeing.parentuser.ParentsDashboard;
import com.digitech.digitalwellbeing.utiles.SessionClass;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        addNotification();
        String fcmToken = "APA91bFR1xQ_7fBvfRkzylUDheIDSlckiVHufhYVQRo_BhdZbKSl2K4bHas7ONC9zQM7oy4LkjcyFj1aTzrl7i812MRQ1Bg7PGRZdZSe99jrh4d9k1gz_T5LsTNHTZEIQ8g80oMIbaqj";
        String title = "Notification Title";
        String body = "Notification Body";
        new NotificationSender(fcmToken, title, body).execute();



     /*   boolean isPermissionGranted = isOverlayPermissionGranted(getApplication());

        if (isPermissionGranted) {
            Log.d(TAG, "onCreate: Granteded");
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Grant Permission")
                    .setMessage("Search and Select 'Neurotech Child Lock' application and allow the permission.")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getApplication().getPackageName()));
                            startActivityForResult(intent, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();

        }*/

/*
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/


        SessionClass.prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);


        mAuth = FirebaseAuth.getInstance();


        //statusCheck();


        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {

            if (mAuth.getCurrentUser() != null) {

                DatabaseReference databasereference = FirebaseDatabase.getInstance()
                        .getReference().child(firebasetables.UserTable).child(mAuth.getCurrentUser().getUid());
                databasereference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("number").getValue()!=null){

                            if (!snapshot.child("token").exists()||snapshot.child("token").getValue()!=null) {

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                databasereference.child("token").setValue(task1.getResult());
                                            }});
                            }

                            if(snapshot.child("type").getValue().toString().equals("0")) {
                                Intent startactivity = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(startactivity);
                                finish();
                            }else {
                                Intent startactivity = new Intent(SplashActivity.this, ParentsDashboard.class);
                                startActivity(startactivity);
                                finish();
                            }

                        }
                        else {
                            Intent startactivity= new Intent(SplashActivity.this, RegistrationActivity.class);
                            startActivity(startactivity);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {

                Intent startactivity= new Intent(SplashActivity.this, MobileVerificationActivity.class);
                startActivity(startactivity);
                finish();

            }

        }, 1000);



    }

    public boolean isOverlayPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            // For versions below Android M, overlay permission is always granted.
            return true;
        }
    }
    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_logo) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


     /*   Intent notificationIntent = new Intent(this, SplashActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);*/

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}