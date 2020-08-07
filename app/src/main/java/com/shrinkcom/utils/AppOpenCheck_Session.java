package com.shrinkcom.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class AppOpenCheck_Session {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "Cabilysession";
    public static final String KEY_APP_STATUS = "appStatus";


    public AppOpenCheck_Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //------string user TimeZone code-----
    public void setAppOpenStatus(String status) {
        editor.putString(KEY_APP_STATUS, status);
        editor.commit();
    }

    //-----------Get Time Zone-----
    public HashMap<String, String> getAppOpenStatus() {
        HashMap<String, String> status = new HashMap<String, String>();
        status.put(KEY_APP_STATUS, pref.getString(KEY_APP_STATUS, ""));
        return status;
    }

}
