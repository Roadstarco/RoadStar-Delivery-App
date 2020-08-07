package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.passenger.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.gps.GPSTracker;

import java.util.List;
import java.util.Locale;

/**
 * Created by Prem Kumar and Anitha on 5/25/2016.
 */
public class DestinationSearchPage extends Activity {
    private RelativeLayout Rl_done, Rl_destinationAddress;
    private ProgressBar progressBar;
    private RelativeLayout Rl_back;
    private TextView Tv_address;

    private GoogleMap googleMap;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    GPSTracker gps;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private int placeSearch_request_code = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_search_layout);
        initialize();
        initializeMap();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        Rl_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("---------MyCurrent_lat--------"+MyCurrent_lat);
                System.out.println("---------MyCurrent_long--------"+MyCurrent_long);

                if (Tv_address.getText().toString().length() > 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", String.valueOf(MyCurrent_lat));
                    returnIntent.putExtra("Selected_Longitude", String.valueOf(MyCurrent_long));
                    returnIntent.putExtra("Selected_Location", Tv_address.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    overridePendingTransition(R.anim.slideup, R.anim.slidedown);
                    finish();
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.destination_address_search_invalid));
                }
            }
        });

        Rl_destinationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DestinationSearchPage.this, LocationSearch.class);
                intent.putExtra("nearLatitude", String.valueOf(MyCurrent_lat));
                intent.putExtra("nearLongitude", String.valueOf(MyCurrent_long));
                startActivityForResult(intent, placeSearch_request_code);
                overridePendingTransition(R.anim.slideup, R.anim.slidedown);

            }
        });

        GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;

                cd = new ConnectionDetector(DestinationSearchPage.this);
                isInternetPresent = cd.isConnectingToInternet();

                Log.e("camerachange lat-->", "" + latitude);
                Log.e("on_camera_change lon-->", "" + longitude);

                if (latitude != 0.0) {
                    googleMap.clear();

                    MyCurrent_lat = latitude;
                    MyCurrent_long = longitude;

                    if (isInternetPresent) {
                        GetAddressTask asynTask = new GetAddressTask(latitude, longitude);
                        asynTask.execute();
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet_message));
                    }
                }
            }
        };

        if (CheckPlayService()) {
            if (googleMap != null) {
                googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String tittle = marker.getTitle();
                        return true;
                    }

                });
            }
        }
    }

    private void initialize() {
        gps = new GPSTracker(DestinationSearchPage.this);

        Rl_done = (RelativeLayout) findViewById(R.id.destination_search_done_layout);
        Rl_destinationAddress = (RelativeLayout) findViewById(R.id.destination_search_address_layout);
        progressBar = (ProgressBar) findViewById(R.id.destination_search_progressBar);
        Rl_back = (RelativeLayout) findViewById(R.id.destination_search_back_layout);
        Tv_address = (TextView) findViewById(R.id.destination_search_address_textView);
    }

    private void initializeMap() {
        if (googleMap == null) {


            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.destination_search_mapView));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);


                }
            });

        }


    }
    public void loadMap(GoogleMap arg)
    {

        googleMap=arg;
        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        DestinationSearchPage.this, R.raw.mapstyle));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);

        if (gps.canGetLocation() && gps.isgpsenabled()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;

            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_gpsEnable));
        }
    }
    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(DestinationSearchPage.this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, DestinationSearchPage.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(DestinationSearchPage.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(DestinationSearchPage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }


    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(DestinationSearchPage.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.e("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


    //---------------AsyncTask to Get Address--------------
    public class GetAddressTask extends AsyncTask<String, Void, String> {

        String strAdd = "";
        double LATITUDE = 0.0, LONGITUDE = 0.0;

        public GetAddressTask(double latitude, double longitude) {
            LATITUDE = latitude;
            LONGITUDE = longitude;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {

            Geocoder geocoder = new Geocoder(DestinationSearchPage.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                } else {
                    Log.e("Current loction address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Current loction address", "Canont get Address!");
            }

            return strAdd;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            if (result.length() > 0) {
                Tv_address.setText(result);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == placeSearch_request_code && resultCode == Activity.RESULT_OK && data != null)) {
            String SdestinationLatitude = data.getStringExtra("Selected_Latitude");
            String SdestinationLongitude = data.getStringExtra("Selected_Longitude");
            String SdestinationLocation = data.getStringExtra("Selected_Location");

            Intent returnIntent = new Intent();
            returnIntent.putExtra("Selected_Latitude", SdestinationLatitude);
            returnIntent.putExtra("Selected_Longitude", SdestinationLongitude);
            returnIntent.putExtra("Selected_Location", SdestinationLocation);
            setResult(RESULT_OK, returnIntent);
            onBackPressed();
            overridePendingTransition(R.anim.slideup, R.anim.slidedown);
            finish();
        }
    }
}
