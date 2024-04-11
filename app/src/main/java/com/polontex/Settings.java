package com.polontex;


import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;

public class Settings extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    Button btnName, btnEmail, btnPassword, btnNO, btnYES;

    TextView popupText, newName, newEmail, newPassword, oldPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Session session = new Session(Settings.this);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Settings.this);
        dataBaseHelper.setHeaderUsername(session.getSession(), navigationView, dataBaseHelper);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (R.id.mainActivity == menuItem.getItemId()) {
                changeActivity("MainActivity");
            }
            if (R.id.report == menuItem.getItemId()) {
                changeActivity("Report");
            }
            if (R.id.plan == menuItem.getItemId()) {
                changeActivity("Plan");
            }
            if (R.id.history == menuItem.getItemId()) {
                changeActivity("History");
            }
            if (R.id.settings == menuItem.getItemId()) {
                changeActivity("Settings");
            }
            if (R.id.logout == menuItem.getItemId()) {
                Session session1 = new Session(Settings.this);
                session1.removeSession();
                changeActivity("Login");
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        btnName = findViewById(R.id.btnName);
        btnEmail = findViewById(R.id.btnEmail);
        btnPassword = findViewById(R.id.btnPassword);

        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationPopup(1);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationPopup(2);
            }
        });

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationPopup(3);
            }
        });


    }

    private void ConfirmationPopup(Integer btnClicked) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;
        if (btnClicked == 3) {
            popupView = inflater.inflate(R.layout.popup_password, null);
            oldPassword = popupView.findViewById(R.id.popup_previous_password);
        } else {
            popupView = inflater.inflate(R.layout.popup, null);

        }

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(drawerLayout, Gravity.CENTER, 0, 0);

            }
        });

        Session session = new Session(Settings.this);
        int userID = session.getSession();

        popupText = popupView.findViewById(R.id.popupText);
        btnNO = popupView.findViewById(R.id.btnNO);
        btnYES = popupView.findViewById(R.id.btnYES);

        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        
        String updateRow = null;
        
        if (btnClicked == 1) {
            popupText.setText(R.string.are_you_sure_you_want_to_change_your_name);
            newName = findViewById(R.id.new_name);
            updateRow = String.valueOf(newName.getText());
        } else if (btnClicked == 2) {
            popupText.setText(R.string.are_you_sure_you_want_to_change_your_e_mail);
            newEmail = findViewById(R.id.new_email);
            updateRow = String.valueOf(newEmail.getText());
        } else if (btnClicked == 3) {
            newPassword = findViewById(R.id.new_password);
            updateRow = String.valueOf(newPassword.getText());
        }

        String finalUpdateRow = updateRow;

        btnYES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPasswordstr;
                if (findViewById(R.id.popup_previous_password) == null) {
                    oldPasswordstr = "";
                } else {
                    oldPasswordstr = String.valueOf(oldPassword.getText());
                }
                DataBaseHelper dataBaseHelper = new DataBaseHelper(Settings.this);
                if (!dataBaseHelper.UpdateRow(finalUpdateRow, userID, btnClicked, oldPasswordstr)) {
                    Toast.makeText(Settings.this, "Update failed", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                } else {
                    dataBaseHelper.UpdateRow(finalUpdateRow, userID, btnClicked, oldPasswordstr);
                    Toast.makeText(Settings.this, "Update succeed", Toast.LENGTH_SHORT).show();
                    switch (btnClicked) {
                        case 1:
                            newName.setText("");
                            break;
                        case 2:
                            newEmail.setText("");
                            break;
                        case 3:
                            newPassword.setText("");
                            break;
                    }
                    popupWindow.dismiss();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeActivity(String className) {
        className = "com.polontex." + className;
        Intent intent = new Intent();
        intent.setClassName(this, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
