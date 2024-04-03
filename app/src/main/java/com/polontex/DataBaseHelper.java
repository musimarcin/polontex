package com.polontex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "polontex.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_EMAIL + " TEXT NOT NULL, " + COLUMN_PASSWORD + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void Register(String name, String email, String password) {
        String hashedpwd = PasswordManager.hashPassword(password);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, hashedpwd);


        db.insert(USERS, null, cv);
    }


    public boolean UpdateName(String name, int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        String[] selectionArgs = { String.valueOf(id) };

        int rowsUpdated = db.update(USERS, cv, "id = ?", selectionArgs);

        return rowsUpdated > 0;

    }
    public void UpdateEmail(String email, int id)
    {

    }
    public void UpdatePassword(String password, int id)
    {
        String hashedpwd = PasswordManager.hashPassword(password);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PASSWORD, hashedpwd);
        String[] selectionArgs = { String.valueOf(id) };

        db.update(USERS, cv, "id = ?", selectionArgs);
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
}
