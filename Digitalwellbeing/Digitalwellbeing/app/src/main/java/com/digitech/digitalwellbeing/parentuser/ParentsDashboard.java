package com.digitech.digitalwellbeing.parentuser;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.Registration.SettingActivity;
import com.digitech.digitalwellbeing.backgroundProcess.MBroadcastReceiver;
import com.digitech.digitalwellbeing.backgroundProcess.ParentBroadcastReceiver;
import com.digitech.digitalwellbeing.childuser.MainActivity;
import com.digitech.digitalwellbeing.childuser.appusageadapter;
import com.digitech.digitalwellbeing.parentuser.adapter.ParentChildrenAdapter;
import com.digitech.digitalwellbeing.utiles.ChildPersonlData;
import com.digitech.digitalwellbeing.utiles.ChildUserdataObject;
import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;
import com.digitech.digitalwellbeing.utiles.MyComparator;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ParentsDashboard extends AppCompatActivity {

    FloatingActionButton scanncode;
    RecyclerView recyclerView;
    int REQUEST_CAMERA_PERMISSION = 100;

    public static ArrayList<ChildUserdataObject> CUD;
    ParentChildrenAdapter parentChildrenAdapter;
    TextView name;
    TextView number;
    String token;
    ImageView noData;
    TextView noDataTxt;
    ImageView setting;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_dashboard);

        recyclerView = findViewById(R.id.recyclerView2);
        scanncode = findViewById(R.id.scanbtn);
        name = findViewById(R.id.textName);
        number = findViewById(R.id.textMobileNumber);
        noData = findViewById(R.id.no_data);
        noDataTxt = findViewById(R.id.no_data_txt);
        setting = findViewById(R.id.setting);

        progressDialog = new ProgressDialog(ParentsDashboard.this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading Child Data");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(ParentsDashboard.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ParentsDashboard.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
            else {

            }
        }

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParentsDashboard.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ParentBroadcastReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);

        long intervalMillis = 1 * 60 * 1000; // 2 minutes in milliseconds
        long triggerTime = SystemClock.elapsedRealtime() + intervalMillis;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, intervalMillis, pendingIntent);



        scanncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(ParentsDashboard.this, QrcodeScanActivity.class);
                    startActivityForResult(intent, 10);
                }
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name.setText("Welcome, " + snapshot.child("name").getValue().toString());
                number.setText(snapshot.child("number").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        token = "No token";
                    } else {
                        token = task1.getResult();
                    }
                });
        getChildData();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= 33) {
                    if (ContextCompat.checkSelfPermission(ParentsDashboard.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ParentsDashboard.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
                    }
                    else {

                    }
                }

            }
            else {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (ContextCompat.checkSelfPermission(ParentsDashboard.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ParentsDashboard.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
                    }
                    else {

                    }
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");

                FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(result).child("parentstoken").setValue(token);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("ChildList").child(result);
                HashMap<String, String> userData = new HashMap<>();
                userData.put("Date", "s");
                databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ParentsDashboard.this, "Child Listed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }

    }

    public void getChildData() {

        CUD = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("ChildList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                if (snap.exists()) {

                    for (DataSnapshot sorted : snap.getChildren()) {

                        ChildUserdataObject childUserdataObject = new ChildUserdataObject();
                        ChildPersonlData childPersonlData = new ChildPersonlData();


                        DatabaseReference childData = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(sorted.getKey());
                        childData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                childPersonlData.setChildname(snapshot.child("name").getValue().toString());
                                childPersonlData.setChildMobileNO(snapshot.child("number").getValue().toString());
                                childPersonlData.setChildUID(sorted.getKey());

                                childUserdataObject.setCPD(childPersonlData);

                                try {
                                    childUserdataObject.setAddress(snapshot.child("Location").child("Address").getValue().toString());
                                    childUserdataObject.setCurrentlat(snapshot.child("Location").child("Latitude").getValue().toString());
                                    childUserdataObject.setCurrentlog(snapshot.child("Location").child("Logitude").getValue().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                childData.child("AppUsage").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        Collections.sort(DWD, new MyComparator());
                                        childUserdataObject.setDWDO(DWD);
                                        Collections.reverse(DWD);
                                        CUD.add(childUserdataObject);

                                        recyclerView.setVisibility(View.VISIBLE);
                                        noData.setVisibility(View.GONE);
                                        noDataTxt.setVisibility(View.GONE);

                                        progressDialog.dismiss();

                                        recyclerView.setLayoutManager(new LinearLayoutManager(ParentsDashboard.this));
                                        parentChildrenAdapter = new ParentChildrenAdapter(ParentsDashboard.this); // You need to create your adapter
                                        recyclerView.setAdapter(parentChildrenAdapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
                else {
                    progressDialog.dismiss();
                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    noDataTxt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}