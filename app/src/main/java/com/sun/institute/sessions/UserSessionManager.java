package com.sun.institute.sessions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserSessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "SunInstitute";

    // All Shared Preferences Keys
    public static final String KEY_ID = "id";
    public static final String KEY_TIME_TABLE_ID = "time_table_id";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @SuppressLint("CommitPrefEdits")
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public  void createLogin(String id,String time_table_id) {
        editor.putString(KEY_ID, id);
        editor.putString(KEY_TIME_TABLE_ID, time_table_id);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("id", pref.getString(KEY_ID, null));
        profile.put("time_table_id", pref.getString(KEY_TIME_TABLE_ID, null));
        return profile;
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }




}
