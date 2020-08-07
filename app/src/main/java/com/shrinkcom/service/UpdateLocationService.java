package com.shrinkcom.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mylibrary.volley.ServiceRequest;

import java.util.HashMap;


public class UpdateLocationService extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private String TAG = UpdateLocationService.class.getSimpleName();
    private Location mLastLocation;
    private ServiceRequest mAvailabilityRequest;

    private String sUserID = "", gcmID = "", sTimeZone = "";
    private String sMode = "";
    private String sState = "";
    private SessionManager sessionManager;

    public UpdateLocationService() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        mAvailabilityRequest = new ServiceRequest(mContext);
        sessionManager = new SessionManager(mContext);

        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        sUserID = user.get(SessionManager.KEY_USERID);
        gcmID = user.get(SessionManager.KEY_GCM_ID);

        HashMap<String, String> state = sessionManager.getAppStatus();
        sState = state.get(SessionManager.KEY_APP_STATUS);

        System.out.println("-------------jai UpdateLocationService----------------");

        googleApiClientCreation();
    }

    // Creating a googleApiClient object using builder pattern
    private void googleApiClientCreation() {
        buildGoogleApiClient();
    }

    // Creating a googleApiClient object using builder pattern
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());

            System.out.println("--------sState---------"+sState);

            if (sUserID.length() > 0) {
                if (sState.equalsIgnoreCase("resume")) {
                    postChatRequest(lat, lng, "available");
                } else if (sState.equalsIgnoreCase("pause")) {
                    postChatRequest(lat, lng, "unavailable");
                } else if (sState.equalsIgnoreCase("dead")) {
                    postChatRequest(lat, lng, "unavailable");
                }
            } else {
                    postChatRequest(lat, lng, "unavailable");
            }


            Log.d("Location_jai -> ", "lat : " + lat + "Longi : " + lng);

        } else {
      //      Toast.makeText(mContext, "no_location_detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    public void postChatRequest(String lat, String lng, String mode) {

        System.out.println("-----------app_status url---------------" + Iconstant.updateAppStatus_url);
        System.out.println("-----------id---------------" + sUserID);
        System.out.println("-----------mode---------------" + mode);
        System.out.println("-----------lat---------------" + lat);
        System.out.println("-----------lng---------------" + lng);

        if (mAvailabilityRequest != null) {
            mAvailabilityRequest.cancelRequest();
        }

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", sUserID);
        jsonParams.put("mode", mode);
        jsonParams.put("latitude", lat);
        jsonParams.put("longitude", lng);

        mAvailabilityRequest.makeServiceRequest(Iconstant.updateAppStatus_url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("---------app_status response-----------------" + response);
                if (sState.equalsIgnoreCase("dead")) {
                    Intent alarmIntent = new Intent(mContext, UpdateLocationService.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }

            @Override
            public void onErrorListener() {
            }
        });
    }

}
