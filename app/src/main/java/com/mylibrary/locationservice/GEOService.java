package com.mylibrary.locationservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.shrinkcom.utils.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.mylibrary.xmpp.XmppService;

public class GEOService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String TAG = GEOService.class.getSimpleName();
    private Context myContext;
    private GEOGpsLocation myGPSLocation;
    private SessionManager session;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    protected double currentLat = 0.0;
    protected double currentLong = 0.0;
    protected int periodicTime = 1000;


    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        doBindService();
        try {
            setLocationRequest();
            buildGoogleApiClient();
        } catch (Exception e) {
        }

        myContext = getApplicationContext();
        myGPSLocation = new GEOGpsLocation(myContext);
        session = new SessionManager(myContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        super.onStartCommand(aIntent, aFlags, aStartId);
        return START_STICKY;
    }


    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(periodicTime);
        mLocationRequest.setFastestInterval(periodicTime);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(GEOService.this)
                .addOnConnectionFailedListener(GEOService.this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

        session.setlatlong(String.valueOf(currentLat),String.valueOf(currentLong));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

/*
    void doBindService() {
        System.out.println("---------jai-----service bind------");
        bindService(new Intent(this, XmppService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }
*/

    void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {

        System.out.println("---------jai-----destroyed 3------");
        return super.onUnbind(intent);


    }


}
