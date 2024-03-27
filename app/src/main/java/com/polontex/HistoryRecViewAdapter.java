package com.polontex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryRecViewAdapter extends RecyclerView.Adapter<HistoryRecViewAdapter.ViewHolder> {

    private ArrayList<HistoryInfo> history = new ArrayList<>();



    public HistoryRecViewAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.txtHistory.setText(history.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public void setHistory(ArrayList<HistoryInfo> history) {
        this.history = history;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtHistory;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtHistory = itemView.findViewById(R.id.txtHistory);
        }
    }

}
