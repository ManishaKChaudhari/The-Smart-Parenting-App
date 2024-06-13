package com.digitech.digitalwellbeing.backgroundProcess;


import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationHrs;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationMins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.digitech.digitalwellbeing.childuser.AppUsageStatsHelper;
import com.digitech.digitalwellbeing.notification.NotificationHelper;
import com.digitech.digitalwellbeing.utiles.ChildPersonlData;
import com.digitech.digitalwellbeing.utiles.ChildUserdataObject;
import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;
import com.digitech.digitalwellbeing.utiles.MyComparator;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class ParentBroadcastReceiver extends BroadcastReceiver {
    public static ArrayList<ChildUserdataObject> CUD;
    Context contexts;
    String TAG = "ParentBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        contexts = context;
        getChildData();
    }

    public void getChildData() {

        CUD = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(FirebaseAuth.getInstance().getUid()).child("ChildList");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

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

                            try{
                                childUserdataObject.setTimeLock(snapshot.child("TimeLock").getValue().toString());

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            childUserdataObject.setTimeLock(snapshot.child("TimeLock").getValue().toString());

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

                                    CheckNotificationValue(childUserdataObject);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void CheckNotificationValue(ChildUserdataObject childUserdataObject) {
        long TotalTime = 0;
        childUserdataObject.getCPD().getChildUID();
        for (int i = 0; i < childUserdataObject.getDWDO().size(); i++) {
            TotalTime = TotalTime + Long.parseLong(childUserdataObject.getDWDO().get(i).getTime());
        }
        if(TotalTime>Integer.parseInt(childUserdataObject.getTimeLock())){
        Log.d(TAG, "CheckNotificationValue: " + formatDurationHrs(TotalTime) + ":" + formatDurationMins(TotalTime));
        NotificationHelper.showNotification(contexts, childUserdataObject.CPD.getChildname(), "Has Used Device For "+formatDurationHrs(TotalTime) + ":" + formatDurationMins(TotalTime) + " Minutes");
    }

}
}