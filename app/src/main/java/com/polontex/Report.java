package com.polontex;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Report extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView reportDesc;
    Button btnReport;

    String[] issue_type = {"Electric", "Hydraulic", "Construction", "Computer", "Cleaning", "Remove", "Other"};
    Spinner spinner;
    String spinner_item;

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

        spinner = findViewById(R.id.report_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Report.this, android.R.layout.simple_spinner_dropdown_item, issue_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_item = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Report.this, R.string.select_an_issue_type, Toast.LENGTH_SHORT).show();
            }
        });

                btnReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reportDesc = findViewById(R.id.report_detail);
                        String desc = String.valueOf(reportDesc.getText());
                        Cursor cursor = dataBaseHelper.getDescription(userID);

                        String tmp = "report";
                        String date = currentDate.toString();
                        String time = currentTime.toString().substring(0, 5);
                        if (TextUtils.isEmpty(desc) || desc.matches("\\s*")) {
                            Toast.makeText(Report.this, R.string.provide_short_description, Toast.LENGTH_SHORT).show();
                        } else if (isDescription(cursor, desc, spinner_item)) {
                            Toast.makeText(Report.this, R.string.report_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            if (dataBaseHelper.addHistory(userID, tmp, desc, tmp, date, time) && dataBaseHelper.addVisit(userID, spinner_item, desc, date, time)) {
                                Toast.makeText(Report.this, R.string.issue_reported_successfully, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Report.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private boolean isDescription(Cursor cursor, String description, String type) {
        HashMap<String, String> typeDesc = new HashMap<>();
        if (cursor.getCount() == 0) {
            Toast.makeText(Report.this, R.string.no_descriptions, Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                System.out.println("cursor 0: " + cursor.getString(0) + " cursor 1: " + cursor.getString(1));
                typeDesc.put(cursor.getString(0), cursor.getString(1));
            }
        }
        if (typeDesc.containsKey(description)) {
            System.out.println(typeDesc);
            return Objects.equals(typeDesc.get(description), type);
        }
        return false;
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
