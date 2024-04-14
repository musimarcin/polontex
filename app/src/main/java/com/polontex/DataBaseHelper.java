package com.polontex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String HISTORY = "history";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ACTION = "act";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String VISITS = "visits";


    public DataBaseHelper(@Nullable Context context) {
        super(context, "polontex.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_EMAIL + " TEXT NOT NULL, "
                    + COLUMN_PASSWORD + " TEXT NOT NULL, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
        String createTable2 =
                "CREATE TABLE " + HISTORY + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_USER_ID + " INTEGER NOT NULL, "
                    + COLUMN_TYPE + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                    + COLUMN_ACTION + " TEXT NOT NULL, "
                    + COLUMN_DATE + " TEXT NOT NULL, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY("
                    + COLUMN_USER_ID + ") REFERENCES users(id));";
        String createTable3 =
                "CREATE TABLE " + VISITS + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_USER_ID + " INTEGER NOT NULL, "
                    + COLUMN_TYPE + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                    + COLUMN_DATE + " TEXT NOT NULL, "
                    + COLUMN_TIME + " TEXT NOT NULL, FOREIGN KEY("
                    + COLUMN_USER_ID + ") REFERENCES users(id));";

        db.execSQL(createTable);
        db.execSQL(createTable2);
        db.execSQL(createTable3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean Register(String name, String email, String password) {
        String hashedpwd = PasswordManager.hashPassword(password);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, hashedpwd);

        long rowsInserted = db.insert(USERS, null, cv);
        db.close();
        return rowsInserted > 0;
    }


    public boolean UpdateRow(String updateRow, int id, int button, String oldPassword)
    {
        ContentValues cv = new ContentValues();
        int rowsUpdated;
        String[] selectionArgs;
        switch (button) {
            case 1:
                HashMap<Integer,String> namesDB = getUsersName();
                if (!namesDB.containsValue(updateRow)) {
                    cv.put(COLUMN_NAME, updateRow);
                    break;
                } else {
                    return false;
                }
            case 2:
                HashMap<String, Integer> emailsDB = getUsersID();
                if (!emailsDB.containsKey(updateRow)) {
                    cv.put(COLUMN_EMAIL, updateRow);
                    break;
                } else {
                    return false;
                }
            case 3:
                HashMap<Integer, String> passwordDB = getUsersPassword();
                String checkpwd = passwordDB.get(id);
                 if (PasswordManager.verifyPassword(oldPassword, checkpwd)) {
                     String hashedpwd = PasswordManager.hashPassword(updateRow);
                     cv.put(COLUMN_PASSWORD, hashedpwd);
                     break;
                 } else {
                     return false;
                 }
        }
        selectionArgs = new String[]{String.valueOf(id)};
        SQLiteDatabase db = this.getWritableDatabase();
        rowsUpdated = db.update(USERS, cv, "id = ?", selectionArgs);
        db.close();
        return rowsUpdated > 0;
    }

    public boolean addVisit(int id, String type, String description, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_ID, id);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);

        long rowsInserted = db.insert(VISITS, null, cv);
        db.close();
        return rowsInserted > 0;
    }

    public boolean addHistory(int id, String type, String description, String act, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String fullDate = date+" "+time;

        cv.put(COLUMN_USER_ID, id);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_ACTION, act);
        cv.put(COLUMN_DATE, fullDate);


        long rowsInserted = db.insert(HISTORY, null, cv);
        db.close();
        return rowsInserted > 0;
    }

    public boolean deleteEntry(int user_id, String action, String date)
    {
        String whereClause = "user_id = ? AND type = ? AND date = ? AND time = ?";
        String[] splitDate = date.split(" ");
        System.out.println("date0: " + splitDate[0] + " date1: " + splitDate[1]);
        String[] selectionArgs = {String.valueOf(user_id), action, splitDate[0], splitDate[1]};
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(VISITS, whereClause, selectionArgs);
        db.close();
        return rowsDeleted > 0;
    }

    Cursor getDate(Integer user_id) {
        String q = "SELECT " + COLUMN_DATE + ", " + COLUMN_TIME + ", " + COLUMN_TYPE + " FROM " + VISITS + " WHERE user_id = ?";
        String[] selectionArgs = {String.valueOf(user_id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(q, selectionArgs);
        }
        return cursor;
    }

    Cursor getUserNameEmail(Integer user_id) {
        String q = "SELECT " + COLUMN_NAME + ", " + COLUMN_EMAIL + " FROM " + USERS + " WHERE id = ?";
        String[] selectionArgs = new String[]{String.valueOf(user_id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) cursor = db.rawQuery(q, selectionArgs);
        return cursor;
    }

    public void setHeaderUsername(Integer user_id, NavigationView navigationView, DataBaseHelper dataBaseHelper) {
        View header = navigationView.getHeaderView(0);
        TextView headerUsername = header.findViewById(R.id.logged_name);
        TextView headerEmail = header.findViewById(R.id.logged_email);
        Cursor cursor = dataBaseHelper.getUserNameEmail(user_id);
        while (cursor.moveToNext()) {
            String username = cursor.getString(0);
            String email = cursor.getString(1);
            headerUsername.setText(username);
            headerEmail.setText(email);
        }
    }

    public HashMap<String, String> getUsersDB() {
        HashMap <String, String> queryResult = new HashMap<>();
        String q = "SELECT * FROM " + USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                String userEmail = cursor.getString(2);
                String userPassword = cursor.getString(3);
                queryResult.put(userEmail, userPassword);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return queryResult;
    }

    public HashMap <String, Integer> getUsersID() {
        HashMap <String, Integer> emailsID = new HashMap<>();
        String q = "SELECT * FROM " + USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                int userID = cursor.getInt(0);
                String userEmail = cursor.getString(2);
                emailsID.put(userEmail, userID);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return emailsID;
    }

    public HashMap <Integer, String> getUsersName() {
        HashMap <Integer, String> namesID = new HashMap<>();
        String q = "SELECT * FROM " + USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                int userID = cursor.getInt(0);
                String userName = cursor.getString(1);
                namesID.put(userID, userName);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return namesID;
    }

    public HashMap <Integer, String> getUsersPassword() {
        HashMap <Integer, String> passwordID = new HashMap<>();
        String q = "SELECT * FROM " + USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                int userID = cursor.getInt(0);
                String password = cursor.getString(3);
                passwordID.put(userID, password);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return passwordID;
    }

    Cursor getHistory(int user_id) {
        String q = "SELECT * FROM " + HISTORY + " WHERE user_id = ?";
        String[] selectionArgs = {String.valueOf(user_id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(q, selectionArgs);
        }
        return cursor;
    }



}
