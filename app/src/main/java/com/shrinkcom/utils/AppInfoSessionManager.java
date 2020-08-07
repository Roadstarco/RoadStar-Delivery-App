package com.shrinkcom.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class AppInfoSessionManager
{
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared_pref file name
    private static final String PREF_NAME = "AppInfo_PremKumar";

    public static final String KEY_CONTACT_EMAIL = "contactEmail";
    public static final String KEY_CUSTOMER_NUMBER = "customerNumber";
    public static final String KEY_SITE_URL = "siteUrl";
    public static final String KEY_HOST_URL = "hostUrl";
    public static final String KEY_HOST_NAME = "hostName";
    public static final String KEY_FACEBOOK_ID = "facebookId";
    public static final String KEY_GOOGLE_PLUS_ID = "googlePlusId";

    public static final String KEY_CATEGORY_IMAGE = "sCategoryImage";
    public static final String KEY_ON_GOING_RIDE = "sOngoingRide";
    public static final String KEY_ON_GOING_RIDE_ID = "sOngoingRideId";
    public static final String KEY_PENDING_RIDE_ID = "sPendingRideId";
    public static final String KEY_RATING_STATUS = "sRatingStatus";

    public AppInfoSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //---------set AppInfo--------
    public void setAppInfo(String contactEmail,String customerNumber,String siteUrl,String hostUrl,String hostName,String facebookId,String googlePlusId,String sCategoryImage,String sOngoingRide,String sOngoingRideId,String sPendingRideId,String sRatingStatus) {
        editor.putString(KEY_CONTACT_EMAIL, contactEmail);
        editor.putString(KEY_CUSTOMER_NUMBER, customerNumber);
        editor.putString(KEY_SITE_URL, siteUrl);
        editor.putString(KEY_HOST_URL, hostUrl);
        editor.putString(KEY_HOST_NAME, hostName);
        editor.putString(KEY_FACEBOOK_ID, facebookId);
        editor.putString(KEY_GOOGLE_PLUS_ID, googlePlusId);

        editor.putString(KEY_CATEGORY_IMAGE, sCategoryImage);
        editor.putString(KEY_ON_GOING_RIDE, sOngoingRide);
        editor.putString(KEY_ON_GOING_RIDE_ID, sOngoingRideId);
        editor.putString(KEY_PENDING_RIDE_ID, sPendingRideId);
        editor.putString(KEY_RATING_STATUS, sRatingStatus);

        editor.commit();
    }

    //-----------Get AppInfo-------
    public HashMap<String, String> getAppInfo() {
        HashMap<String, String> timeZone = new HashMap<String, String>();
        timeZone.put(KEY_CONTACT_EMAIL, pref.getString(KEY_CONTACT_EMAIL, ""));
        timeZone.put(KEY_CUSTOMER_NUMBER, pref.getString(KEY_CUSTOMER_NUMBER, ""));
        timeZone.put(KEY_SITE_URL, pref.getString(KEY_SITE_URL, ""));
        timeZone.put(KEY_HOST_URL, pref.getString(KEY_HOST_URL, ""));
        timeZone.put(KEY_HOST_NAME, pref.getString(KEY_HOST_NAME, ""));
        timeZone.put(KEY_FACEBOOK_ID, pref.getString(KEY_FACEBOOK_ID, ""));
        timeZone.put(KEY_GOOGLE_PLUS_ID, pref.getString(KEY_GOOGLE_PLUS_ID, ""));
        timeZone.put(KEY_CATEGORY_IMAGE, pref.getString(KEY_CATEGORY_IMAGE, ""));
        timeZone.put(KEY_ON_GOING_RIDE, pref.getString(KEY_ON_GOING_RIDE, ""));
        timeZone.put(KEY_ON_GOING_RIDE_ID, pref.getString(KEY_ON_GOING_RIDE_ID, ""));
        timeZone.put(KEY_PENDING_RIDE_ID, pref.getString(KEY_PENDING_RIDE_ID, ""));
        timeZone.put(KEY_RATING_STATUS, pref.getString(KEY_RATING_STATUS, ""));
        return timeZone;
    }
}

