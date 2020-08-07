package com.mylibrary.locationservice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GEOGpsLocation implements LocationListener {

    private Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location = null; // location
    private double latitude; // latitude
    private double longitude; // longitude
    private LatLng myLatLng;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 100 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30; // 30 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public GEOGpsLocation(FragmentActivity context) {
        this.mContext = context;
        getLocation();
    }

    public GEOGpsLocation(Activity context) {
        this.mContext = context;
        getLocation();
    }

    public GEOGpsLocation(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                try {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    try {
                        if (location == null) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // location permissions have not been granted.
                                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {

                                    // Provide an additional rationale to the user if the permission was not granted
                                    // and the user would benefit from additional context for the use of the permission.
                                    // For example, if the request has been denied previously.

                                    // Display a SnackBar with an explanation and a button to trigger the request.

                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

                                    // Setting Dialog Title
                                    //  alertDialog.setTitle("GPS setting");

                                    // Setting Dialog Message
                                    alertDialog
                                            .setMessage("Allow to access to your location");

                                    // On pressing Settings button
                                    alertDialog.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATION,
                                                            0);
                                                }
                                            });

                                    // on pressing cancel button
                                    alertDialog.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                    // Showing Alert Message
                                    alertDialog.show();

                                } else {
                                    // Contact permissions have not been granted yet. Request them directly.
                                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATION, 0);
                                }

                            }

                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    0,
                                    0, this);

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(GEOGpsLocation.this);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get latitude and longitude
     */
    public LatLng getLatLng() {
        if (location != null) {
            myLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());

        }

        // return latitude and longitude
        return myLatLng;
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check whether the gps is enable or not
     */
    public boolean getGpsStatus() {
        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // return GpsStatus
        return isGPSEnabled;
    }

    /**
     * Function to check whether the network is enable or not
     */
    public boolean getNetworkStatus() {
        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // return NetworkStatus
        return isNetworkEnabled;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS setting");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you wish to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location alocation) {
        if (alocation != null) {
            location = alocation;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * Get distance between two locations
     *
     * @param aFromLat
     * @param aFromLongitude
     * @param aToLat
     * @param aToLongitude
     * @return Distance in KM
     */
    public static String getDistanceInKM(String aFromLat,
                                         String aFromLongitude, String aToLat, String aToLongitude) {

        String aDistance = "0";

        try {

            Location aLocation1 = new Location("locationA");
            aLocation1.setLatitude(Double.parseDouble(aFromLat));
            aLocation1.setLongitude(Double.parseDouble(aFromLongitude));
            Location aLocation2 = new Location("locationB");
            aLocation2.setLatitude(Double.parseDouble(aToLat));
            aLocation2.setLongitude(Double.parseDouble(aToLongitude));

            aDistance = new DecimalFormat("##.##").format(aLocation1
                    .distanceTo(aLocation2) / 1000);
        } catch (Exception e) {

            return aDistance;
        }

        return aDistance;

    }

    public static double sumDistance(ArrayList<Marker> mMapList) {
        Location loc = new Location("distance provider");
        double previousLatitude = mMapList.get(0).getPosition().latitude;
        double previousLongitude = mMapList.get(0).getPosition().longitude;
        float[] results = {0};
        for (int i = 1; i < mMapList.size(); i++) {
            loc.distanceBetween(previousLatitude, previousLongitude, mMapList.get(i).getPosition().latitude,
                    mMapList.get(i).getPosition().longitude, results);
            previousLatitude = mMapList.get(i).getPosition().latitude;
            previousLongitude = mMapList.get(i).getPosition().longitude;
        }

        Log.e("Sum", "" + results[0]);
        return results[0];
    }


}
