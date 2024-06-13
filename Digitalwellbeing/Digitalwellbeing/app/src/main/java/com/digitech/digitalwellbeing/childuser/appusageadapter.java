package com.digitech.digitalwellbeing.childuser;

import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationHrs;
import static com.digitech.digitalwellbeing.utiles.SessionClass.formatDurationMins;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitech.digitalwellbeing.R;
import com.digitech.digitalwellbeing.utiles.ChildUserdataObject;
import com.digitech.digitalwellbeing.utiles.SessionClass;

import java.util.List;

public class appusageadapter extends RecyclerView.Adapter<appusageadapter.ViewHolder> {

    private ChildUserdataObject itemList;
    Context context;

    public appusageadapter(ChildUserdataObject cud, Context context) {
        itemList=cud;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.getDWDO().get(position).getPackgeName();
        holder.AppName.setText(itemList.getDWDO().get(position).getAppName());
        SessionClass.setIconToApps(holder.icon, item, context);
        holder.TimeSpend.setText(formatDurationHrs(Long.parseLong(itemList.getDWDO().get(position).getTime()))+":"+formatDurationMins(Long.parseLong(itemList.getDWDO().get(position).getTime()))+" hrs");
    }

    @Override
    public int getItemCount() {
        return itemList.getDWDO().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView AppName;
        TextView TimeSpend;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            AppName = itemView.findViewById(R.id.appname);
            TimeSpend = itemView.findViewById(R.id.timespend);
        }
    }
}