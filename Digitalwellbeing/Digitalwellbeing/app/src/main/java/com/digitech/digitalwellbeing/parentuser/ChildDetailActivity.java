package com.digitech.digitalwellbeing.parentuser;

import static com.digitech.digitalwellbeing.parentuser.ParentsDashboard.CUD;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationHrs;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationMins;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.childuser.appusageadapter;
import com.digitech.digitalwellbeing.parentuser.adapter.ParentChildrenAdapter;
import com.digitech.digitalwellbeing.utiles.DigitalWelbeingDataObject;
import com.digitech.digitalwellbeing.utiles.MyComparator;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChildDetailActivity extends AppCompatActivity {

    private TextView textName, textMobileNumber, textAddress, textTotalTimeHrs, textTotalTimeMin;
    private RecyclerView recyclerView;
    private appusageadapter adapter;
    int index;
    private static final String TAG = "ChildDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        textName = findViewById(R.id.textName);
        textMobileNumber = findViewById(R.id.textMobileNumber);
        textAddress = findViewById(R.id.textAddress);
        textTotalTimeHrs = findViewById(R.id.textTotalTimeHrs);
        textTotalTimeMin = findViewById(R.id.textTotalTimeMin);
        recyclerView = findViewById(R.id.recyclerView);

        index = getIntent().getIntExtra("index", 0);

        Log.d(TAG, "onCreate: "+index);
        textName.setText(CUD.get(index).getCPD().getChildname());
        textMobileNumber.setText(CUD.get(index).getCPD().getChildMobileNO());
        textAddress.setText(CUD.get(index).getAddress());

        long TotalTime = 0;
        for (int i = 0; i < CUD.get(index).getDWDO().size(); i++) {
            TotalTime = TotalTime + Long.parseLong(CUD.get(index).getDWDO().get(i).getTime());

            //Log.d(TAG, "AskForData: " + CUD.getDWDO().get(i).getPackgeName() + "  " + CUD.getDWDO().get(i).getTime());

        }



        textTotalTimeHrs.setText(formatDurationHrs(TotalTime));
        textTotalTimeMin.setText(formatDurationMins(TotalTime));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new appusageadapter(CUD.get(index), ChildDetailActivity.this); // You need to create your adapter
        recyclerView.setAdapter(adapter);

    }

}