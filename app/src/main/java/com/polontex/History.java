package com.polontex;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    RecyclerView historyRecView;

    DataBaseHelper dataBaseHelper;
    ArrayList<String> action, date, timestamp;
    HistoryRecViewAdapter historyRecViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
                Session session = new Session(History.this);
                session.removeSession();
                changeActivity("Login");
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return false;
        });

        Session session = new Session(History.this);
        int userID = session.getSession();

        dataBaseHelper = new DataBaseHelper(History.this);
        action = new ArrayList<>();
        date = new ArrayList<>();
        timestamp = new ArrayList<>();

        getHistoryData(userID);

        historyRecView = findViewById(R.id.historyRecView);
        historyRecViewAdapter = new HistoryRecViewAdapter(History.this, action, date, timestamp);
        historyRecView.setAdapter(historyRecViewAdapter);
        historyRecView.setLayoutManager(new LinearLayoutManager(History.this));

        DataBaseHelper dataBaseHelper = new DataBaseHelper(History.this);
        dataBaseHelper.setHeaderUsername(userID, navigationView, dataBaseHelper);
    }

    void getHistoryData(int user_id) {
        Cursor cursor = dataBaseHelper.getHistory(user_id);
        if (cursor.getCount() == 0) {
            Toast.makeText(History.this, "No history", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                action.add(cursor.getString(4));
                date.add(cursor.getString(5));
                timestamp.add(cursor.getString(6));
            }
        }
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
