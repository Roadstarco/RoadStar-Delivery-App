package com.shrinkcom.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ChatAvailabilityCheck;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by Prem Kumar and Anitha on 12/23/2015.
 */
public class UpdateUserLocation extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    SessionManager session;
    GPSTracker gps;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private String userID = "", sLatitude = "", sLongitude = "",app_identity_name="",About_Content="",user_image="",userName ="",Language_code="";
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;


    PkDialogWithoutButton mInfoDialog;
    AppInfoSessionManager appInfo_Session;
    String sPendingRideId = "", sRatingStatus = "", sCategoryImage = "", sOngoingRide = "", sOngoingRideId = "";
    private boolean isAppInfoAvailable = false;
    private String PHONEMASKINGSTATUS = "";

    private ArrayList<EmergencyPojo> emergencyAraryList;
    private boolean isEmergencyAvailabe = false;
    private String lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user_location);
        initialize();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLocation();
            }
        }, 2000);

    }

    private void initialize() {
        cd = new ConnectionDetector(UpdateUserLocation.this);
        session = new SessionManager(getApplicationContext());
        gps = new GPSTracker(getApplicationContext());
        emergencyAraryList = new ArrayList<EmergencyPojo>();
        appInfo_Session = new AppInfoSessionManager(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(UpdateUserLocation.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        HashMap<String, String> language = session.getLanaguage();
        lang = language.get(SessionManager.KEY_Language);

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

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
        cd = new ConnectionDetector(UpdateUserLocation.this);
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            if (gps.isgpsenabled() && gps.canGetLocation()) {

                HashMap<String, String> user = session.getUserDetails();
                userID = user.get(SessionManager.KEY_USERID);
                sLatitude = String.valueOf(gps.getLatitude());
                sLongitude = String.valueOf(gps.getLongitude());
                postRequest_AppInformation(Iconstant.app_info_url);
                //     postRequest_SetUserLocation(Iconstant.setUserLocation);

            } else {
                enableGpsService();
            }
        } else {

            final PkDialog mDialog = new PkDialog(UpdateUserLocation.this);
            mDialog.setDialogTitle(getResources().getString(R.string.alert_nointernet));
            mDialog.setDialogMessage(getResources().getString(R.string.alert_nointernet_message));
            mDialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    setLocation();
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
                            status.startResolutionForResult(UpdateUserLocation.this, REQUEST_LOCATION);
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                session = new SessionManager(getApplicationContext());
                                gps = new GPSTracker(UpdateUserLocation.this);

                                HashMap<String, String> user = session.getUserDetails();
                                userID = user.get(SessionManager.KEY_USERID);
                                sLatitude = String.valueOf(gps.getLatitude());
                                sLongitude = String.valueOf(gps.getLongitude());
                                postRequest_AppInformation(Iconstant.app_info_url);
                                //   postRequest_SetUserLocation(Iconstant.setUserLocation);

                            }
                        }, 2000);

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        enableGpsService();
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

        System.out.println("-------------UpdateUserLocation UserLocation Url----------------" + Url);
        mRequest = new ServiceRequest(UpdateUserLocation.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------UpdateUserLocation UserLocation Response----------------" + response);

                String Str_status = "", sCategoryID = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    sCategoryID = object.getString("category_id");

                    if (Str_status.equalsIgnoreCase("1")) {
                        session.setCategoryID(sCategoryID);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
/*
                if (!isMyServiceRunning(XmppService.class)) {
                    System.out.println("-----------OtpPage xmpp service start---------");
                    startService(new Intent(UpdateUserLocation.this, XmppService.class));
                }
*/
                ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(UpdateUserLocation.this, "available");
                chatAvailability.postChatRequest();
                Intent intent = new Intent(UpdateUserLocation.this, SelectCorrierServicesActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }

            @Override
            public void onErrorListener() {
/*
                if (!isMyServiceRunning(XmppService.class)) {
                    System.out.println("-----------OtpPage xmpp service start---------");
                    startService(new Intent(UpdateUserLocation.this, XmppService.class));
                }
*/
                ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(UpdateUserLocation.this, "available");
                chatAvailability.postChatRequest();
                Intent intent = new Intent(UpdateUserLocation.this, SelectCorrierServicesActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }


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
    private void postRequest_AppInformation(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", userID);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);

        System.out.println("-------------appinfo---------jsonParams-------" + jsonParams);



        mRequest = new ServiceRequest(UpdateUserLocation.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------UpdateUserLocation appinfo----------------" +response);
                String Str_status = "",sShare_status="", sContact_mail = "",customer_service_number="",customer_service_address="" , sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
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
                                app_identity_name = info_object.getString("app_identity_name");
                                About_Content = info_object.getString("about_content");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                user_image = info_object.getString("user_image");
                                userName = info_object.getString("user_name");
                               /* server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
                                Language_code = info_object.getString("lang_code");
                                if(info_object.has("customer_service_number")) {
                                    customer_service_number = info_object.getString("customer_service_number");
                                }
                                if(info_object.has("site_contact_address")) {
                                    customer_service_address = info_object.getString("site_contact_address");
                                }
                                if(info_object.has("pooling")) {
                                    sShare_status = info_object.getString("pooling");
                                }
                                else {
                                    sShare_status="0";
                                }
                                if (info_object.has("phone_masking_status")) {
                                    String phoneMaskingStatus = info_object.getString("phone_masking_status");
                                    PHONEMASKINGSTATUS = phoneMaskingStatus;
                                    // set phone masking
                                    session.setKeyPhoneMaskingStatus(PHONEMASKINGSTATUS);
                                    System.out.println("=====>>>===PHONEMASKINGSTATUS ==========>>>>> " + PHONEMASKINGSTATUS);
                                }

                                Object emercencyObject = info_object.get("emergency_numbers");
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

                        switch (Language_code){

                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                //  System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                                //  Intent in=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //   finish();
                                //  startActivity(in);

//                        Intent bi = new Intent();
//                        bi.setAction("homepage");
//                        sendBroadcast(bi);
//                        finish();

                                break;
                            case "es":
                                locale = new Locale("es");
                                session.setlamguage("es","es");
                                //      session.setlamguage("Ar",language_change.getSelectedItem().toString());
                                //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //     finish();
                                //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                break;
                            case "ta":
                                locale = new Locale("ta");
                                session.setlamguage("ta","ta");
                                //      session.setlamguage("Ar",language_change.getSelectedItem().toString());
                                //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                //     finish();
                                //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                break;
                            default:
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());


                        session.setcustomerdetail(customer_service_number,customer_service_address);
                        appInfo_Session.setAppInfo(sContact_mail, sCustomerServiceNumber, sSiteUrl, sXmppHostUrl, sHostName, sFacebookId, sGooglePlusId, sCategoryImage, sOngoingRide, sOngoingRideId, sPendingRideId, sRatingStatus);
                        session.setXmpp(sXmppHostUrl,sHostName);
                        session.setAgent(app_identity_name);
                        session.setAbout(About_Content);
                        session.setuser_image(user_image);
                        session.setUserNameUpdate(userName);


                        session.setShareStatus(sShare_status);
                        if (isEmergencyAvailabe){
                            session.putEmergencyContactDetails(emergencyAraryList);
                        }



                        //  postRequest_AppInformation(Iconstant.app_info_url);
                      /*  ChatService.startUserAction(UpdateUserLocation.this);

                        Intent intent = new Intent(context, UpdateUserLocation.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);*/
                        postRequest_SetUserLocation(Iconstant.setUserLocation);


                    } else {
                        mInfoDialog = new PkDialogWithoutButton(UpdateUserLocation.this);
                        mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
                        mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
                        mInfoDialog.show();
                    }



/*                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {
                        appInfo_Session.setAppInfo(sContact_mail, sCustomerServiceNumber, sSiteUrl, sXmppHostUrl, sHostName, sFacebookId, sGooglePlusId, sCategoryImage, sOngoingRide, sOngoingRideId, sPendingRideId, sRatingStatus);

                        if (session.isLoggedIn()) {
                            postRequest_SetUserLocation(Iconstant.setUserLocation);
                        } else {
                            Intent i = new Intent(SplashPage.this, SingUpAndSignIn.class);
                            i.putExtra("HashKey",Str_HashKey);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    } else {
                        mInfoDialog = new PkDialogWithoutButton(SplashPage.this);
                        mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
                        mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
                        mInfoDialog.show();
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                mInfoDialog = new PkDialogWithoutButton(UpdateUserLocation.this);
                mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
                mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
                mInfoDialog.show();
            }
        });
    }




}
