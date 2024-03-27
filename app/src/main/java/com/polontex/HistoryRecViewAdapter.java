package com.polontex;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryRecViewAdapter extends RecyclerView.Adapter<HistoryRecViewAdapter.ViewHolder> {

    private ArrayList<History> history = new ArrayList<>();



    public HistoryRecViewAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public void setHistory(ArrayList<History> history) {
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
