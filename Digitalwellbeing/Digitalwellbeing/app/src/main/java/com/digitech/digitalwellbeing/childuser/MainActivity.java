package com.digitech.digitalwellbeing.childuser;


import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationHrs;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationMins;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.Registration.MobileVerificationActivity;
import com.digitech.digitalwellbeing.Registration.RegistrationActivity;
import com.digitech.digitalwellbeing.Registration.SettingActivity;
import com.digitech.digitalwellbeing.Registration.SplashActivity;
import com.digitech.digitalwellbeing.backgroundProcess.MBroadcastReceiver;
import com.digitech.digitalwellbeing.childuser.GenrateqrCode;
import com.digitech.digitalwellbeing.childuser.appusageadapter;
import com.digitech.digitalwellbeing.parentuser.ParentsDashboard;
import com.digitech.digitalwellbeing.parentuser.adapter.ParentChildrenAdapter;
import com.digitech.digitalwellbeing.utiles.ChildUserdataObject;
import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;
import com.digitech.digitalwellbeing.utiles.MyComparator;
import com.digitech.digitalwellbeing.utiles.SessionClass;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "MainActivity";
    private appusageadapter adapter;
    int PERMISSION_REQUEST_CODE = 1;
    private TextView textName, textMobileNumber, textAddress, textTotalTimeHrs, textTotalTimeMin;
    private RecyclerView recyclerView;
    //  private MyAdapter adapter;
    FloatingActionButton Qrbutton;
    ImageView setting;
    ChildUserdataObject CUD;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = findViewById(R.id.textName);
        textMobileNumber = findViewById(R.id.textMobileNumber);
        textAddress = findViewById(R.id.textAddress);
        textTotalTimeHrs = findViewById(R.id.textTotalTimeHrs);
        textTotalTimeMin = findViewById(R.id.textTotalTimeMin);
        recyclerView = findViewById(R.id.recyclerView);
        Qrbutton = findViewById(R.id.floatingActionButton);
        setting = findViewById(R.id.setting);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading Your Data");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {

            }
        }

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        Qrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GenrateqrCode.class);
                startActivity(i);
            }
        });


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MBroadcastReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long intervalMillis = 5 * 60 * 1000; // 2 minutes in milliseconds
        long triggerTime = SystemClock.elapsedRealtime() + intervalMillis;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, intervalMillis, pendingIntent);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    textName.setText("Hey, " + snapshot.child("name").getValue().toString());
                    textMobileNumber.setText(snapshot.child("number").getValue().toString());
                } else {
                    Intent startactivity = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(startactivity);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            DatabaseReference childData = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("AppUsage");
            childData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<DigitalWelbeingDataObject> DWD = new ArrayList<>();

                    for (DataSnapshot digital : snapshot.getChildren()) {
                        DigitalWelbeingDataObject digitalWelbeingDataObject = new DigitalWelbeingDataObject();
                        digitalWelbeingDataObject.setAppName(digital.child("name").getValue().toString());
                        digitalWelbeingDataObject.setPackgeName(digital.child("package").getValue().toString());
                        digitalWelbeingDataObject.setTime(digital.child("time").getValue().toString());
                        DWD.add(digitalWelbeingDataObject);

                    }
                  //  Collections.sort(DWD, new MyComparator());
                    long TotalTime = 0;
                    for (int i = 0; i < DWD.size(); i++) {
                        TotalTime = TotalTime + Long.parseLong(DWD.get(i).getTime());

                        Log.d(TAG, "AskForData: " + DWD.get(i).getPackgeName() + "  " + DWD.get(i).getTime());
                    }
                    CUD.setDWDO(DWD);

                    textTotalTimeHrs.setText(formatDurationHrs(TotalTime));
                    textTotalTimeMin.setText(formatDurationMins(TotalTime));

                    progressDialog.dismiss();

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter = new appusageadapter(CUD, MainActivity.this); // You need to create your adapter
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }, 4000);





        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //statusCheck();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (!isUsageStatsPermissionGranted()) {
                requestUsageStatsPermission();
            } else {
                AskForData();
            }
        }
    }

    private void AskForData() {
        ArrayList<DigitalWelbeingDataObject> DB = accessAppUsageStats();
        CUD = getCurrentLocation();
        CUD.setDWDO(DB);

        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            textAddress.setText(CUD.getAddress());
            SaveDataOnFirebase(CUD);
        }, 2000);


    }

    private void SaveDataOnFirebase(ChildUserdataObject cud) {
        Log.d(TAG, "SaveDataOnFirebase: " + cud.getCurrentlat());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("Location");
        HashMap<String, String> userData = new HashMap<>();
        userData.put("Latitude", cud.getCurrentlat());
        userData.put("Logitude", cud.getCurrentlog());
        userData.put("Address", cud.getAddress());
        databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                     FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("AppUsage").removeValue();
                    for (int i = 0; i < cud.getDWDO().size(); i++) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("AppUsage").child(cud.getDWDO().get(i).getAppName());
                        HashMap<String, String> userData = new HashMap<>();
                        userData.put("package", cud.getDWDO().get(i).getPackgeName());
                        userData.put("name", cud.getDWDO().get(i).getAppName());
                        userData.put("time", cud.getDWDO().get(i).getTime());
                        databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(MainActivity.this, "Data Save Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    Toast.makeText(MainActivity.this, "Data Save Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private boolean isUsageStatsPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Please enable Usage Access permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                AskForData();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access app usage stats.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (!isUsageStatsPermissionGranted()) {
                    requestUsageStatsPermission();
                } else {
                    AskForData();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<DigitalWelbeingDataObject> accessAppUsageStats() {
        AppUsageStatsHelper appUsageStatsHelper = new AppUsageStatsHelper(this);
        ArrayList<DigitalWelbeingDataObject> DB = appUsageStatsHelper.getAppUsageStats();
        return DB;
    }

    public ChildUserdataObject getCurrentLocation() {
        ChildUserdataObject CUD = new ChildUserdataObject();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this,

                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();


                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }

                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();


                                    CUD.setCurrentlat(location.getLatitude() + "");
                                    CUD.setCurrentlog(location.getLongitude() + "");
                                    CUD.setAddress(address);

                                    // Use latitude and longitude values
                                    Toast.makeText(MainActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        return CUD;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                if (!isUsageStatsPermissionGranted()) {
                    requestUsageStatsPermission();
                } else {
                    AskForData();
                }
            }
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
