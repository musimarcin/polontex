package com.polontex;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.time.LocalDate;
import java.time.LocalTime;

public class Report extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView reportType, reportDesc;
    Button btnReport;

    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);
        btnReport = findViewById(R.id.btnReport);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Report.this);
        Session session = new Session(Report.this);
        int userID = session.getSession();
        dataBaseHelper.setHeaderUsername(userID, navigationView, dataBaseHelper);
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
                Session session1 = new Session(Report.this);
                session1.removeSession();
                changeActivity("Login");
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });


        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reportType = findViewById(R.id.report_type);
                reportDesc = findViewById(R.id.report_detail);
                String type = String.valueOf(reportType.getText());
                String desc = String.valueOf(reportDesc.getText());

                String tmp = "report";
                String date = currentDate.toString();
                String time = currentTime.toString().substring(0,5);
                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(Report.this, R.string.select_an_issue_type, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(desc) || desc.matches("\\s*")) {
                    Toast.makeText(Report.this, R.string.provide_short_description, Toast.LENGTH_SHORT).show();
                } else {
                    if (dataBaseHelper.addHistory(userID, tmp, desc, tmp, date, time) && dataBaseHelper.addVisit(userID, type, desc, date, time)) {
                        Toast.makeText(Report.this, R.string.issue_reported_successfully
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Report.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
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
