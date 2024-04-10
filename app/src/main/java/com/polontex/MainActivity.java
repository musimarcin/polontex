package com.polontex;

import android.content.Intent;
import android.database.Cursor;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    View header;

    TextView headerUsername, headerEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_menu);
        Session session = new Session(MainActivity.this);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        setHeaderUsername();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
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
            }
        });
    }

    public void setHeaderUsername() {
        View header = getLayoutInflater().inflate(R.layout.header, null);
        TextView headerUsername = header.findViewById(R.id.logged_name);
        TextView headerEmail = header.findViewById(R.id.logged_email);
        Session session = new Session(getApplicationContext());
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        Cursor cursor = dataBaseHelper.getUserNameEmail(session.getSession()); //TODO: bug
        if (cursor.getCount() == 0) {
            Toast.makeText(MainActivity.this, "No dates", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String username = cursor.getString(0);
                String email = cursor.getString(1);
                headerUsername.setText(username);
                headerEmail.setText(email);
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
