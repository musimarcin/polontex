package com.polontex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

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
    public static final String COLUMN_ISSUE = "type";
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
                    + COLUMN_ISSUE + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                    + COLUMN_ACTION + " TEXT NOT NULL, "
                    + COLUMN_DATE + " TEXT NOT NULL, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY("
                    + COLUMN_USER_ID + ") REFERENCES users(id));";
        String createTable3 =
                "CREATE TABLE " + VISITS + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + COLUMN_USER_ID + " INTEGER NOT NULL, "
                    + COLUMN_ISSUE + " TEXT NOT NULL, "
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
        cv.put(COLUMN_ISSUE, type);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);

        long rowsInserted = db.insert(VISITS, null, cv);
        return rowsInserted > 0;
    }

    public boolean addHistory(int id, String issue, String description, String act, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String fullDate = date+" "+time;

        cv.put(COLUMN_USER_ID, id);
        cv.put(COLUMN_ISSUE, issue);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_ACTION, act);
        cv.put(COLUMN_DATE, fullDate);

        if (addVisit(id, issue,description, date, time)) {
            long rowsInserted = db.insert(HISTORY, null, cv);
            db.close();
            return rowsInserted > 0;
        } else {
            db.close();
            return false;
        }
    }

    public HashMap<Integer, String> getDate() {
        HashMap <Integer, String> queryResult = new HashMap<>();
        String q = "SELECT * FROM " + VISITS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.moveToFirst()) {
            do {
                Integer userID = cursor.getInt(1);
                String time = cursor.getString(4) + " " + cursor.getString(5);
                queryResult.put(userID, time);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return queryResult;
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
                String userName = cursor.getString(3);
                passwordID.put(userID, userName);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return passwordID;
    }

    Cursor getHistory(int user_id) {
        String q = "SELECT * FROM " + HISTORY + " WHERE id = ?";
        String[] selectionArgs = {String.valueOf(user_id)};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(q, selectionArgs);
        }
        return  cursor;
    }

}
