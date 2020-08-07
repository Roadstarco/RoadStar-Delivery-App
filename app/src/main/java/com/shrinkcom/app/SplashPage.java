package com.shrinkcom.app;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.IdentifyAppKilled;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.dialog.PkDialogWithoutButton;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.volley.ServiceRequest;
//import com.mylibrary.xmpp.XmppService;
import com.wang.avi.AVLoadingIndicatorView;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class SplashPage extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    SessionManager session;
    Context context;
    GPSTracker gps;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private String userID = "", sLatitude = "", sLongitude = "";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;

    private String Str_HashKey = "", currentVersion = "";

    AppInfoSessionManager appInfo_Session;

    private boolean isAppInfoAvailable = false;

    final int PERMISSION_REQUEST_CODE = 111;
    private String server_mode, site_mode, site_string, site_url, About_Content = "", user_image = "", userName = "", Language_code = "";
    PkDialogWithoutButton mInfoDialog;
    String sPendingRideId = "", sRatingStatus = "", sCategoryImage = "", sOngoingRide = "", sOngoingRideId = "", app_identity_name = "";

    public static String PHONEMASKINGSTATUS = "";
    private int playserviceVersion;
    private boolean isNeedBanner = true;

    private ArrayList<EmergencyPojo> emergencyAraryList;
    private boolean isEmergencyAvailabe = false;
    private String sPage = "", sType = "", sRideId = "";
    private String title = "", msg = "", banner = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash);
        context = getApplicationContext();
        cd = new ConnectionDetector(SplashPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.splash_avLoadingIndicatorView);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                Str_HashKey = (Base64.encodeToString(md.digest(), Base64.DEFAULT));


                System.out.println("Str_HashKey--------------" + Str_HashKey);


            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // Session class instance
        session = new SessionManager(getApplicationContext());
        appInfo_Session = new AppInfoSessionManager(SplashPage.this);
        gps = new GPSTracker(getApplicationContext());

        emergencyAraryList = new ArrayList<EmergencyPojo>();

        mGoogleApiClient = new GoogleApiClient.Builder(SplashPage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        try {
            currentVersion = SplashPage.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            playserviceVersion = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final int appPlayServiceVersion = getResources()
                .getInteger(com.google.android.gms.R.integer.google_play_services_version);

        if (appPlayServiceVersion > playserviceVersion) {
            Alert1(getResources().getString(R.string.action_error), getResources().getString(R.string.update_paly_service));
        }


        System.out.println("---------------playserviceVersion------------------" + playserviceVersion);
        System.out.println("---------------playserviceVersion1------------------" + appPlayServiceVersion);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra("page")) {
                sPage = i.getStringExtra("page");
                sType = i.getStringExtra("type");
                if (sPage.equalsIgnoreCase("Ads")) {
                    title = i.getStringExtra("title");
                    msg = i.getStringExtra("msg");
                    banner = i.getStringExtra("banner");
                    session.setADS(true);
                    session.setAds(title, msg, banner);
                } else {
                    sRideId = i.getStringExtra("rideId");
                }
            }

        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                      /*  GetVersionCode versionCode = new GetVersionCode();
                        versionCode.execute();*/
                        //need to check previous version in playstore
                        setLocation();
                    }
                } else {
                    /*GetVersionCode versionCode = new GetVersionCode();
                    versionCode.execute();*/
                    //need to check previous version in playstore
                    setLocation();
                }
            }
        }, SPLASH_TIME_OUT);

    }


    //Code to check playStore update version
    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = "";
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashPage.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            if (!onlineVersion.equals("")) {
                if (onlineVersion != null && !onlineVersion.isEmpty()) {
                    if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                        if (SplashPage.this != null && !SplashPage.this.isFinishing()) {

                            Alert(getResources().getString(R.string.app_name), "There is newer version of this application available, click OK to upgrade now?");
                        }
                        //                Alert(getResources().getString(R.string.app_name), "There is newer version of this application available, click OK to upgrade now?");
                    } else {
                        setLocation();



                       /* if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                                requestPermission();
                            } else {
                                setLocation();
                            }
                        } else {
                            setLocation();
                        }*/
                    }
                }

            } else {
                setLocation();
               /* if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                        setLocation();
                    }
                } else {
                    setLocation();
                }*/
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }

    private void Alert(String title, String alert) {
        try {
            final PkDialog mDialog = new PkDialog(SplashPage.this);
            mDialog.setDialogTitle(title);
            mDialog.setDialogMessage(alert);
            mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.couponcode_label_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void Alert1(String title, String alert) {
        try {
            final PkDialog mDialog = new PkDialog(SplashPage.this);
            mDialog.setDialogTitle(title);
            mDialog.setDialogMessage(alert);
            mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();

                }
            });

            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // enableGpsService();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    private void setLocation() {
        cd = new ConnectionDetector(SplashPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            if (gps.isgpsenabled() && gps.canGetLocation()) {
                if (session.isLoggedIn()) {
                    //starting XMPP service
                    //      ChatService.startUserAction(SplashPage.this);

                    HashMap<String, String> user = session.getUserDetails();
                    userID = user.get(SessionManager.KEY_USERID);
                    sLatitude = String.valueOf(gps.getLatitude());
                    sLongitude = String.valueOf(gps.getLongitude());


                    //            facebook_details_PostRequest(Iconstant.app_facebook_post_url);

                    postRequest_AppInformation(Iconstant.app_info_url);
                } else {
                    HashMap<String, String> user = session.getUserDetails();
                    userID = user.get(SessionManager.KEY_USERID);
                    sLatitude = String.valueOf(gps.getLatitude());
                    sLongitude = String.valueOf(gps.getLongitude());
                    postRequest_AppInformation(Iconstant.app_info_url);
                }
            } else {
                enableGpsService();
            }
        } else {
            final PkDialog mDialog = new PkDialog(SplashPage.this);
            mDialog.setDialogTitle(getResources().getString(R.string.alert_nointernet));
            mDialog.setDialogMessage(getResources().getString(R.string.alert_nointernet_message));
            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    setLocation();
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
            mDialog.show();

        }
    }

    //Enabling Gps Service
    private void enableGpsService() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashPage.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(SplashPage.this, getResources().getString(R.string.splash_toast_loaction_enable), Toast.LENGTH_LONG).show();
                        if (session.isLoggedIn()) {
                            //starting XMPP service
                            //                         ChatService.startUserAction(SplashPage.this);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    session = new SessionManager(getApplicationContext());
                                    gps = new GPSTracker(SplashPage.this);

                                    HashMap<String, String> user = session.getUserDetails();
                                    userID = user.get(SessionManager.KEY_USERID);
                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());

                                    postRequest_AppInformation(Iconstant.app_info_url);

                                }
                            }, 2000);

                        } else {
                            postRequest_AppInformation(Iconstant.app_info_url);
                        }
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    //-----------------------User Current Location Post Request-----------------
    private void postRequest_SetUserLocation(String Url) {

        System.out.println("----------sLatitude----------" + sLatitude);
        System.out.println("----------sLongitude----------" + sLongitude);
        System.out.println("----------userID----------" + userID);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userID);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);

        System.out.println("-------------Splash UserLocation Url----------------" + Url);
        mRequest = new ServiceRequest(SplashPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash UserLocation Response----------------" + response);

                String Str_status = "", sCategoryID = "", sTripProgress = "", sRideStage = "";
                try {
                    JSONObject object = new JSONObject(response);
                  /*  Str_status = object.getString("status");
                    sCategoryID = object.getString("category_id");
                    sTripProgress = object.getString("trip_in_progress");
                    sRideId = object.getString("ride_id");
                    sRideStage = object.getString("ride_stage");*/

                   /* if (Str_status.equalsIgnoreCase("1")) {
                        session.setCategoryID(sCategoryID);
                        session.setRidePendingStatus(sTripProgress,sRideId,sRideStage);
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
/*
                if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(SplashPage.this, XmppService.class));
                }
*/

                /*if (!isMyServiceRunning(GEOService.class)) {
                    Intent serviceIntent = new Intent(SplashPage.this, GEOService.class);
                    startService(serviceIntent);
                }*/

                intentMethods();


            }

            @Override
            public void onErrorListener() {
               /* if (!isMyServiceRunning(XmppService.class)) {
                    startService(new Intent(SplashPage.this, XmppService.class));
                }*/
                /*if (!isMyServiceRunning(GEOService.class)) {
                    Intent serviceIntent = new Intent(SplashPage.this, GEOService.class);
                    startService(serviceIntent);
                }*/

                intentMethods();
            }
        });
    }

    private void intentMethods() {
        if ("track".equalsIgnoreCase(sPage)) {
            Intent i = new Intent(SplashPage.this, MyRideDetailTrackRide.class);
            i.putExtra("rideID", sRideId);
            i.putExtra("type", sType);
            startActivity(i);
            finish();
             overridePendingTransition(R.anim.enter, R.anim.exit);


        } else if ("farebreakup".equalsIgnoreCase(sPage)) {

            Intent i = new Intent(SplashPage.this, FareBreakUp.class);
            i.putExtra("RideID", sRideId);
            i.putExtra("type", sType);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if ("rating".equalsIgnoreCase(sPage)) {
            Intent i = new Intent(SplashPage.this, MyRideRating.class);
            i.putExtra("RideID", sRideId);
            i.putExtra("type", sType);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if ("Ads".equalsIgnoreCase(sPage)) {
            Intent i1 = new Intent(SplashPage.this, AdsPage.class);
            i1.putExtra("AdsTitle", title);
            i1.putExtra("AdsMessage", msg);
            i1.putExtra("type", sType);
            if (!"".equalsIgnoreCase(banner)) {
                i1.putExtra("AdsBanner", banner);
            }
            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i1);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);

        }  else if ("details".equalsIgnoreCase(sPage)) {
            Intent i = new Intent(SplashPage.this, MyRidesDetail.class);
            i.putExtra("RideID", sRideId);
            i.putExtra("type", sType);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);

        }else {

            //Intent i = new Intent(SplashPage.this, NavigationDrawer.class);
            Intent i = new Intent(SplashPage.this, UpdateUserLocation.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }


    //-----------------------App Information Post Request-----------------
    private void postRequest_AppInformation(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", userID);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);


        System.out.println("-------------appinfo---------jsonParams-------" + jsonParams);


        mRequest = new ServiceRequest(SplashPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------SplashPage appinfo----------------" + response);
                String Str_status = "", sContact_mail = "", customer_service_number = "", customer_service_address = "", sShare_status = "", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject info_object = response_object.getJSONObject("info");
                            if (info_object.length() > 0) {
                                sContact_mail = info_object.getString("site_contact_mail");
                                sCustomerServiceNumber = info_object.getString("customer_service_number");
                                sSiteUrl = info_object.getString("site_url");
                                sXmppHostUrl = info_object.getString("xmpp_host_url");
                                sHostName = info_object.getString("xmpp_host_name");
                                sFacebookId = info_object.getString("facebook_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                app_identity_name = info_object.getString("app_identity_name");
                                server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");
                                About_Content = info_object.getString("about_content");
                                user_image = info_object.getString("user_image");
                                userName = info_object.getString("user_name");
                                Language_code = info_object.getString("lang_code");
                                if (info_object.has("customer_service_number")) {
                                    customer_service_number = info_object.getString("customer_service_number");
                                }
                                if (info_object.has("site_contact_address")) {
                                    customer_service_address = info_object.getString("site_contact_address");
                                }

                                if (info_object.has("pooling")) {

                                    sShare_status = info_object.getString("pooling");

                                } else {

                                    sShare_status = "0";

                                }

                                if (info_object.has("phone_masking_status")) {
                                    String phoneMaskingStatus = info_object.getString("phone_masking_status");
                                    PHONEMASKINGSTATUS = phoneMaskingStatus;
                                    // set phone masking
                                    session.setKeyPhoneMaskingStatus(PHONEMASKINGSTATUS);
                                    System.out.println("=====>>>===PHONEMASKINGSTATUS ==========>>>>> " + PHONEMASKINGSTATUS);
                                }


                                Object emercencyObject = info_object.get("emergency_numbers");
                                if (info_object.has("emergency_numbers")) {
                                    String emergency_numbers = String.valueOf(info_object.getJSONArray("emergency_numbers"));
                                    session.setEmergencyNumber(emergency_numbers);
                                }

                                if (emercencyObject instanceof JSONArray) {
                                    JSONArray emercency_array = info_object.getJSONArray("emergency_numbers");
                                    if (emercency_array.length() > 0) {
                                        emergencyAraryList.clear();
                                        for (int j = 0; j < emercency_array.length(); j++) {
                                            JSONObject job = emercency_array.getJSONObject(j);
                                            EmergencyPojo emergencyPojo = new EmergencyPojo();
                                            emergencyPojo.setTitle(job.getString("title"));
                                            emergencyPojo.setNumber(job.getString("number"));
                                            emergencyAraryList.add(emergencyPojo);
                                        }
                                        isEmergencyAvailabe = true;
                                    } else {
                                        isEmergencyAvailabe = false;
                                    }

                                }

                                /* Language_code="es";*/
                                isAppInfoAvailable = true;
                            } else {
                                isAppInfoAvailable = false;
                            }
                            //sCategoryImage = response_object.getString("category_image");
                            // sOngoingRide = response_object.getString("ongoing_ride");
                            // sOngoingRideId = response_object.getString("ongoing_ride_id");
                            // sPendingRideId = response_object.getString("rating_pending_ride_id");
                            // sRatingStatus = response_object.getString("rating_pending");
                        } else {
                            isAppInfoAvailable = false;
                        }
                    } else {
                        isAppInfoAvailable = false;
                    }

                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {


                        HashMap<String, String> language = session.getLanaguage();
                        Locale locale = null;

                        switch (Language_code) {

                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en", "en");
                                break;
                            case "es":
                                locale = new Locale("es");
                                session.setlamguage("es", "es");
                                break;
                            case "ta":
                                locale = new Locale("ta");
                                session.setlamguage("ta", "ta");
                                break;
                            default:
                                locale = new Locale("en");
                                session.setlamguage("en", "en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

                        appInfo_Session.setAppInfo(sContact_mail, sCustomerServiceNumber, sSiteUrl, sXmppHostUrl, sHostName, sFacebookId, sGooglePlusId, sCategoryImage, sOngoingRide, sOngoingRideId, sPendingRideId, sRatingStatus);
                        session.setXmpp(sXmppHostUrl, sHostName);
                        session.setAgent(app_identity_name);
                        session.setAbout(About_Content);
                        session.setcustomerdetail(customer_service_number, customer_service_address);
                        session.setuser_image(user_image);
                        session.setUserNameUpdate(userName);


                        session.setShareStatus(sShare_status);

                        if (isEmergencyAvailabe) {
                            session.putEmergencyContactDetails(emergencyAraryList);
                        }

                        if (site_mode.equalsIgnoreCase("development")) {
                            mInfoDialog = new PkDialogWithoutButton(SplashPage.this);
                            mInfoDialog.setDialogTitle("ALERT");
                            mInfoDialog.setDialogMessage(site_string);
                            mInfoDialog.show();
                        } else {
                            if (session.isLoggedIn()) {
                                postRequest_SetUserLocation(Iconstant.setUserLocation);
                            } else {


                                if (isNeedBanner) {
                                    Intent i = new Intent(SplashPage.this, SignUpBannerPage.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.enter, R.anim.exit);

                                } else {
                                    Intent i = new Intent(SplashPage.this, SingUpAndSignIn.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }

                            }

                        }
                        if (server_mode.equalsIgnoreCase("0")) {
                            // Toast.makeText(context, site_url, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        if (!((Activity) context).isFinishing()) {
                            swtDialogSucces();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onErrorListener() {
                try {
                    if (!((Activity) context).isFinishing()) {
                        swtDialogSucces();
                    }
                } catch (ClassCastException e1){
                    e1.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }


    private void swtDialogSucces() {
        mInfoDialog = new PkDialogWithoutButton(SplashPage.this);
        mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
        mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
        mInfoDialog.show();
    }


    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // need to check previous version api in playstore
             /*       GetVersionCode versionCode = new GetVersionCode();
                    versionCode.execute();*/

                    setLocation();

                } else {
                    finish();
                }
                break;
        }
    }

//----------------------------------------------

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("1 already running");
                b = true;
                break;
            } else {
                System.out.println("2 not running");
                b = false;
            }
        }
        System.out.println("3 not running");
        return b;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //--------Start Service to identify app killed or not---------
        if (!isMyServiceRunning(IdentifyAppKilled.class)) {
            startService(new Intent(SplashPage.this, IdentifyAppKilled.class));
        }
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            return true;
        }
        return false;
    }


    //-----------------------code for detail ButtonAction Post request---------------------
    private void facebook_details_PostRequest(String url) {
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("hash_key", Str_HashKey);
        jsonParams.put("package_name", "com.zuber.passenger");


        ServiceRequest mservicerequest = new ServiceRequest(SplashPage.this);
        mservicerequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                Log.e("facebookpost------", response);


            }

            @Override
            public void onErrorListener() {

            }
        });
    }


}