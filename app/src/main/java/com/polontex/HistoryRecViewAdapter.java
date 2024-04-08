package com.polontex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryRecViewAdapter extends RecyclerView.Adapter<HistoryRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList action, date, timestamp;


    HistoryRecViewAdapter(Context context, ArrayList action, ArrayList date, ArrayList timestamp) {
        this.context = context;
        this.action = action;
        this.date = date;
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.txtAction.setText(String.valueOf(action.get(position)));
        holder.txtDate.setText(String.valueOf(date.get(position)));
        holder.txtTimestamp.setText(String.valueOf(timestamp.get(position)).substring(0,16));
    }

    @Override
    public int getItemCount() {
        return action.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtAction, txtDate, txtTimestamp;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtAction = itemView.findViewById(R.id.history_action);
            txtDate = itemView.findViewById(R.id.history_date);
            txtTimestamp = itemView.findViewById(R.id.history_timestamp);
        }
    }

}
