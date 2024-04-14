package com.polontex;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.*;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainRecViewAdapter extends RecyclerView.Adapter<MainRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList action, date;
    private List<List<String>> buttonDataList;
    TextView txtAction, txtDate;

    public MainRecViewAdapter(Context context, ArrayList date, ArrayList action, List<List<String>> buttonDataList) {
        this.date = date;
        this.action = action;
        this.context = context;
        this.buttonDataList = buttonDataList;
    }

    @NonNull
    @Override
    public MainRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_list_item, viewGroup, false);
        return new MainRecViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MainRecViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txtAction.setText(String.valueOf(action.get(i)));
        viewHolder.txtDate.setText(String.valueOf(date.get(i)));
        List<String> buttonData = buttonDataList.get(i);
        ConstraintLayout constraintLayout = viewHolder.itemView.findViewById(R.id.homeConstraint);

        for (String buttonText : buttonData) {
            Button button = new Button(viewHolder.itemView.getContext());
            button.setText(R.string.delete);
            int buttonID = View.generateViewId();
            button.setId(buttonID);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            button.setOnClickListener(v -> {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup, null);
                LinearLayout linearLayout = viewHolder.itemView.findViewById(R.id.homeParent);
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                linearLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAtLocation(linearLayout, Gravity.CENTER, 0, 0);

                    }
                });
                TextView popupText = popupView.findViewById(R.id.popupText);
                Button btnNO = popupView.findViewById(R.id.btnNO);
                Button btnYES = popupView.findViewById(R.id.btnYES);

                popupText.setText(R.string.are_you_sure);

                btnNO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnYES.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
                        Session session = new Session(context);
                        txtAction = viewHolder.itemView.findViewById(R.id.home_list_action);
                        txtDate = viewHolder.itemView.findViewById(R.id.home_list_date);
                        if (dataBaseHelper.deleteEntry(session.getSession(), String.valueOf(txtAction.getText()), String.valueOf(txtDate.getText()))) {
                            Toast.makeText(viewHolder.itemView.getContext(), context.getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        } else {
                            Toast.makeText(viewHolder.itemView.getContext(), context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                    }
                });
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
