package com.polontex;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    ArrayList<String> action, date;
    MainRecViewAdapter mainRecViewAdapter;
    List<List<String>> buttonDataList;
    Button goPlan, goReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);
        Session session = new Session(MainActivity.this);
        int userID = session.getSession();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
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
                session.removeSession();
                changeActivity("Login");
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        Cursor cursor = dataBaseHelper.getDate(userID);
        action = new ArrayList<>();
        date = new ArrayList<>();
        buttonDataList = new ArrayList<>();

        getVisitData(cursor);

        recyclerView = findViewById(R.id.mainRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(new MainRecViewAdapter(MainActivity.this, date, action, buttonDataList));

        goPlan = findViewById(R.id.goPlan);
        goReport = findViewById(R.id.goReport);

        goPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity("Plan");
            }
        });

        goReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity("Report");
            }
        });
    }


    void getVisitData(Cursor cursor) {
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, R.string.no_visits, Toast.LENGTH_SHORT).show();
        } else {
            int i = 0;
            while (cursor.moveToNext()) {
                String cDate = cursor.getString(0);
                String cTime = cursor.getString(1);
                action.add(cursor.getString(2));
                date.add(cDate+" "+cTime);
                buttonDataList.add(Arrays.asList(String.valueOf(i)));
                i++;
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
