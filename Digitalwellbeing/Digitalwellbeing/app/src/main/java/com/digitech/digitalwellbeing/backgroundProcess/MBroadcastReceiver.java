package com.digitech.digitalwellbeing.backgroundProcess;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.digitech.digitalwellbeing.childuser.AppUsageStatsHelper;
import com.digitech.digitalwellbeing.childuser.MainActivity;
import com.digitech.digitalwellbeing.utiles.ChildUserdataObject;
import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class MBroadcastReceiver extends BroadcastReceiver {

    String TAG = "MBroadcastReceiver";
    Context contexts;

    @Override
    public void onReceive(Context context, Intent intent) {
        contexts = context;
        AskForData();
    }


    private void AskForData() {
        ChildUserdataObject CUD = new ChildUserdataObject();
        ArrayList<DigitalWelbeingDataObject> DB = accessAppUsageStats(contexts);
        CUD.setDWDO(DB);
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            SaveDataOnFirebase(CUD);
            long TotalTime = 0;
            for (int i = 0; i < CUD.getDWDO().size(); i++) {
                TotalTime = TotalTime + Long.parseLong(CUD.getDWDO().get(i).getTime());

                Log.d(TAG, "AskForData: " + CUD.getDWDO().get(i).getPackgeName() + "  " + CUD.getDWDO().get(i).getTime());
            }
        }, 5000);
    }

    private ArrayList<DigitalWelbeingDataObject> accessAppUsageStats(Context contexts) {
        AppUsageStatsHelper appUsageStatsHelper = new AppUsageStatsHelper(contexts);
        ArrayList<DigitalWelbeingDataObject> DB = appUsageStatsHelper.getAppUsageStats();
        return DB;
    }


    private void SaveDataOnFirebase(ChildUserdataObject cud) {
     
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


    }

}