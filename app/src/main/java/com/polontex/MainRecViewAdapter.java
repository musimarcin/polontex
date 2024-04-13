package com.polontex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainRecViewAdapter extends RecyclerView.Adapter<MainRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList action, date;
    private List<List<String>> buttonDataList;
    TextView list_date;

    public MainRecViewAdapter(Context context, ArrayList date, ArrayList action, List<List<String>> buttonDataList) {
        this.date = date;
        this.action = action;
        this.context = context;
        this.buttonDataList = buttonDataList;
    }

    @NonNull
    @Override
    public MainRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //LayoutInflater inflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_list_item, viewGroup, false);
        return new MainRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MainRecViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txtAction.setText(String.valueOf(action.get(i)));
        viewHolder.txtDate.setText(String.valueOf(date.get(i)));
        List<String> buttonData = buttonDataList.get(i);
        ConstraintLayout constraintLayout = viewHolder.itemView.findViewById(R.id.homeConstraint);
        list_date = viewHolder.itemView.findViewById(R.id.home_list_date);

        for (String buttonText : buttonData) {
            Button button = new Button(viewHolder.itemView.getContext());
            button.setText("DELETE");
            int buttonID = View.generateViewId();
            button.setId(buttonID);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            button.setOnClickListener(v -> {
                Toast.makeText(viewHolder.itemView.getContext(), "Button " + buttonID + " clicked", Toast.LENGTH_SHORT).show();
            });
            button.setLayoutParams(layoutParams);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintLayout.addView(button);
            constraintSet.clone(constraintLayout);

            constraintSet.connect(button.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
            constraintSet.connect(button.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
            constraintSet.connect(button.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);

            constraintSet.applyTo(constraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return buttonDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAction, txtDate;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtAction = itemView.findViewById(R.id.home_list_action);
            txtDate = itemView.findViewById(R.id.home_list_date);
        }

    }
}
