package com.polontex;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Plan extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView txtDate, txtTime, visitDesc;
    Button btnDate, btnTime, btnPlan;

    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Plan.this);
        Session session = new Session(Plan.this);
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
                Session session1 = new Session(Plan.this);
                session1.removeSession();
                changeActivity("Login");
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return false;
        });

        txtDate = findViewById(R.id.dateSelected);
        txtTime = findViewById(R.id.timeSelected);
        btnDate = findViewById(R.id.datePicker);
        btnTime = findViewById(R.id.timePicker);
        visitDesc = findViewById(R.id.visit_description);
        btnPlan = findViewById(R.id.btnPlan);


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });

        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = String.valueOf(visitDesc.getText());
                String tmp = "visit";
                String date = String.valueOf(txtDate.getText());
                String time = String.valueOf(txtTime.getText());

                if (TextUtils.isEmpty(desc)) {
                    Toast.makeText(Plan.this, R.string.provide_description_of_visit, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(date)) {
                    Toast.makeText(Plan.this, R.string.provide_date, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(time)) {
                    Toast.makeText(Plan.this, R.string.provide_time, Toast.LENGTH_SHORT).show();
                } else if (isDate(userID, date, time)) {
                    Toast.makeText(Plan.this, R.string.visit_on_that_time_and_date_already_exists, Toast.LENGTH_SHORT).show();
                } else {
                    if (isValidDate(date, time)) {
                        if (dataBaseHelper.addHistory(userID, tmp, desc, tmp, date, time) && dataBaseHelper.addVisit(userID, tmp, desc, date, time)) {
                            Toast.makeText(Plan.this, R.string.visit_added, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Plan.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Plan.this, R.string.invalid_date_cannot_be_in_the_past, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void openDateDialog() {
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String newmonth = null;
                month += 1;
                if (month < 10) {
                    newmonth = "0" + String.valueOf(month);
                } else {
                    newmonth = String.valueOf(month);
                }
                String newday = null;
                if (dayOfMonth < 10) {
                    newday = "0" + String.valueOf(dayOfMonth);
                } else {
                    newday = String.valueOf(dayOfMonth);
                }
                txtDate.setText(year + "-" + newmonth + "-" + newday);
            }
        }, currentDate.getYear(), currentDate.getMonthValue()-1, currentDate.getDayOfMonth());

        dateDialog.show();
    }

    private void openTimeDialog() {
        TimePickerDialog timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String newhour = null;
                if (hourOfDay < 10) {
                    newhour = "0" + String.valueOf(hourOfDay);
                } else {
                    newhour = String.valueOf(hourOfDay);
                }
                String newminute = null;
                if (minute < 10) {
                    newminute = "0" + String.valueOf(minute);
                } else {
                    newminute = String.valueOf(minute);
                }
                txtTime.setText(newhour +":"+ newminute);
            }
        }, currentTime.getHour(), currentTime.getMinute(), true);

        timeDialog.show();
    }


    private boolean isDate(Integer id, String date, String time) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(Plan.this);
        Cursor cursor = dataBaseHelper.getDate(id);
        String fulldate = date + " " + time;
        ArrayList<String> dbDates = new ArrayList<>();
        if (cursor.getCount() == 0) {
            Toast.makeText(Plan.this, R.string.no_dates, Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String cDate = cursor.getString(0);
                String cTime = cursor.getString(1);
                dbDates.add(cDate+" "+cTime);
            }
        }
        return dbDates.contains(fulldate);
    }


    private boolean isValidDate(String date, String time) {
        LocalDate parsedDate = LocalDate.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime parsedTime = LocalTime.parse(time, formatter);

        int dateComparison = parsedDate.compareTo(currentDate);
        if (dateComparison < 0) {
            return false;
        } else if (dateComparison == 0) {
            int timeComparison = parsedTime.compareTo(currentTime);
            if (timeComparison < 0) {
                return false;
            } else if (timeComparison == 0) {
                return parsedTime.getMinute() > currentTime.getMinute();
            }
        }
        return true;
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
