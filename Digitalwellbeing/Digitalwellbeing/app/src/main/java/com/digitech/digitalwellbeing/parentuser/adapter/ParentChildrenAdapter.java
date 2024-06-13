package com.digitech.digitalwellbeing.parentuser.adapter;

import static com.digitech.digitalwellbeing.parentuser.ParentsDashboard.CUD;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationHrs;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationMins;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.parentuser.ChildDetailActivity;
import com.digitech.digitalwellbeing.parentuser.MapViewActivity;
import com.digitech.digitalwellbeing.utiles.firebasetables;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ParentChildrenAdapter extends RecyclerView.Adapter<ParentChildrenAdapter.ViewHolder> {

    Context context;
    private static final String TAG = "ParentChildrenAdapter";
    int hrsStr, minStr;
    public ParentChildrenAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(CUD.get(position).getCPD().getChildname());
        holder.number.setText(CUD.get(position).getCPD().getChildMobileNO());
        holder.address.setText(CUD.get(position).getAddress());

        long TotalTime = 0;
        for (int i = 0; i < CUD.get(position).getDWDO().size(); i++) {
            TotalTime = TotalTime + Long.parseLong(CUD.get(position).getDWDO().get(i).getTime());

            //Log.d(TAG, "AskForData: " + CUD.getDWDO().get(i).getPackgeName() + "  " + CUD.getDWDO().get(i).getTime());

        }
        holder.mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + CUD.get(position).getCurrentlat());
                String geoUri = "http://maps.google.com/maps?q=loc:" + CUD.get(position).getCurrentlat() + "," + CUD.get(position).getCurrentlog() + " (" + CUD.get(position).getCPD().getChildname() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                context.startActivity(intent);
            }
        });

        holder.textTotalTimeHrs.setText(formatDurationHrs(TotalTime));
        holder.textTotalTimeMin.setText(formatDurationMins(TotalTime));

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChildDetailActivity.class);
            intent.putExtra("index", position);
            context.startActivity(intent);
        });

        holder.addnotification.setOnClickListener(view -> {
            showPasswordPopup(context, position);
        });

    }

    @Override
    public int getItemCount() {
        return CUD.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name;
        TextView number;
        TextView address;
        TextView textTotalTimeHrs, textTotalTimeMin;
        ImageView mapview;
        ImageView addnotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.child_card);
            name = itemView.findViewById(R.id.textName);
            number = itemView.findViewById(R.id.textMobileNumber);
            address = itemView.findViewById(R.id.textAddress);
            mapview = itemView.findViewById(R.id.mapview);
            addnotification = itemView.findViewById(R.id.addnotification);
            textTotalTimeHrs = itemView.findViewById(R.id.textTotalTimeHrs);
            textTotalTimeMin = itemView.findViewById(R.id.textTotalTimeMin);

        }
    }


    public void showPasswordPopup(Context activity, int position) {

        Dialog ScannInprogress;
        ScannInprogress = new Dialog(activity);
        ScannInprogress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScannInprogress.setCancelable(true);
        ScannInprogress.setContentView(R.layout.set_time_layout);
        ScannInprogress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText hrs = ScannInprogress.findViewById(R.id.hrs);
        EditText min = ScannInprogress.findViewById(R.id.min);
        Button save = ScannInprogress.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View v) {

                hrsStr =Integer.parseInt(hrs.getText().toString());
                minStr = Integer.parseInt(min.getText().toString());

                if (hrs.getText().toString().isEmpty()) {
                    hrs.setError("Field cannot be empty");
                } else if (min.getText().toString().isEmpty()) {
                    min.setError("Field cannot be empty");
                } else {
                    FirebaseDatabase.getInstance().getReference().child(firebasetables.UserTable).child(CUD.get(position).getCPD().getChildUID()).child("TimeLock").setValue(TimeUnit.MINUTES.toMillis(minStr)+TimeUnit.HOURS.toMillis(hrsStr));
                    ScannInprogress.dismiss();
                }

            }
        });

        ScannInprogress.show();

    }
}
