package com.polontex;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_NAME = "session";
    String KEY = "session_user";

    public Session(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user) {
        int id = user.getId();
        editor.putInt(KEY, id).commit();

    }

    public int getSession() {
        return sharedPreferences.getInt(KEY, -1);
    }

    public void removeSession() {
        editor.putInt(KEY, -1).commit();
    }
}
