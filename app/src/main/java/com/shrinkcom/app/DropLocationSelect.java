package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.GeocoderHelper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.mylibrary.InterFace.CallBack;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.gps.GPSTracker;
import com.shrinkcom.passenger.R;

/**
 * Created by Prem Kumar and Anitha on 2/26/2016.
 */
public class DropLocationSelect extends Activity {
    RelativeLayout Rl_done, Rl_back;
    RelativeLayout Rl_selectDrop;
    private TextView Tv_dropLocation;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    private GoogleMap googleMap;
    private GPSTracker gps;
    ProgressBar progressBar;

    private String sLatitude="";
    private double Recent_lat=0.0;
    private String sLongitude="";
    private double Recent_long=0.0;

    public static final int ActivityDropRequestCode = 6000;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private boolean isLocationType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.droplocation_select);
        initialize();
        initializeMap();

        Rl_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Tv_dropLocation.getText().toString() != null) && (!"".equalsIgnoreCase(Tv_dropLocation.getText().toString().trim()))) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", sLatitude);
                    returnIntent.putExtra("Selected_Longitude", sLongitude);
                    returnIntent.putExtra("Selected_Location", Tv_dropLocation.getText().toString().trim());
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    finish();
                }
            }
        });

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        Rl_selectDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPSTracker gps = new GPSTracker(DropLocationSelect.this);
                if (gps.canGetLocation()) {
                    isLocationType = true;
                    openAutocompleteActivity(Recent_lat,Recent_long);

                } else {
                    Toast.makeText(DropLocationSelect.this, "Enable Gps", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void initialize() {
        cd = new ConnectionDetector(DropLocationSelect.this);
        isInternetPresent = cd.isConnectingToInternet();
        gps=new GPSTracker(DropLocationSelect.this);

        Rl_done = (RelativeLayout) findViewById(R.id.drop_location_select_done_layout);
        Rl_back = (RelativeLayout) findViewById(R.id.drop_location_select_back_layout);
        Rl_selectDrop = (RelativeLayout) findViewById(R.id.drop_location_select_dropLocation_layout);
        Tv_dropLocation = (TextView) findViewById(R.id.drop_location_select_drop_address);
        progressBar=(ProgressBar)findViewById(R.id.drop_location_select_progress_bar);

    }

    private void initializeMap() {



        if (googleMap == null) {

            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.drop_location_select_view_map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);


                }
            });
           /* googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.track_your_ride_mapview)).getMap();
            if (googleMap == null) {
                Toast.makeText(TrackYourRide.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }*/
        }



        // Changing map type


    }
    public void loadMap(GoogleMap arg) {
        googleMap=arg;
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        DropLocationSelect.this, R.raw.mapstyle));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
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
            Recent_lat= gps.getLatitude();
            Recent_long= gps.getLongitude();
            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Alert(getResources().getString(R.string.timer_label_alert_sorry), getResources().getString(R.string.alert_gpsEnable));
        }


        if (CheckPlayService()) {
            googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String tittle = marker.getTitle();
                    return true;
                }
            });
        } else {
            Toast.makeText(DropLocationSelect.this, "Install Google Play service To View Location !!!", Toast.LENGTH_LONG).show();
        }
    }
    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(DropLocationSelect.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }





    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(DropLocationSelect.this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }


    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        DropLocationSelect.this.runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,   DropLocationSelect.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(  DropLocationSelect.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    //-------------------------------code for map marker moving-------------------------------
    GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            double latitude = 0.0;
            double longitude = 0.0;
            if (!isLocationType){
                latitude = cameraPosition.target.latitude;
                longitude = cameraPosition.target.longitude;
            }else{
                latitude = Double.parseDouble(sLatitude);
                longitude = Double.parseDouble(sLongitude);
            }


            cd = new ConnectionDetector(DropLocationSelect.this);
            isInternetPresent = cd.isConnectingToInternet();

            Log.e("camerachange lat-->", "" + latitude);
            Log.e("on_camera_change lon-->", "" + longitude);

            if (googleMap != null) {
                googleMap.clear();

                if (isInternetPresent) {
                    Rl_done.setVisibility(View.GONE);
                    sLatitude = String.valueOf(latitude);
                    sLongitude = String.valueOf(longitude);


                    Map_movingTask asynTask=new Map_movingTask(latitude,longitude);
                    asynTask.execute();
                } else {
                    Alert(getResources().getString(R.string.timer_label_alert_sorry), getResources().getString(R.string.alert_nointernet_message));
                }
            }
        }
    };




    private class Map_movingTask extends AsyncTask<String, Void, String> {

        String response = "";
        private double dLatitude=0.0;
        private double dLongitude=0.0;
        Map_movingTask(double lat,double lng)
        {
            dLatitude=lat;
            dLongitude=lng;
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
          //  String address = getCompleteAddressString(dLatitude, dLongitude);
            String address=  new GeocoderHelper().fetchCityName(DropLocationSelect.this, dLatitude,dLongitude,callBack );
            return address;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    CallBack callBack=new CallBack() {
        @Override
        public void onComplete(String LocationName) {
            progressBar.setVisibility(View.GONE);
            if((LocationName != null) && (!"".equalsIgnoreCase(LocationName.trim())))
            {
                if (!isLocationType) {
                    Tv_dropLocation.setText(LocationName);
                }
                Rl_done.setVisibility(View.VISIBLE);
                isLocationType = false;
            }else
            {
                Tv_dropLocation.setText("");
                Rl_done.setVisibility(View.GONE);
                isLocationType = false;
            }
        }

        @Override
        public void onError(String errorMsg) {
            Rl_done.setVisibility(View.GONE);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == RESULT_OK) {
            if (requestCode == ActivityDropRequestCode) {
                // Check if no view has focus:
                CloseKeyBoard();
                Place place = PlaceAutocomplete.getPlace(DropLocationSelect.this, data);


                String sAddress = "";
                if ( String.valueOf(place.getAddress()).contains(String.valueOf(place.getName())) ){
                    sAddress = place.getAddress() + "";
                }else{
                    sAddress = place.getName()+" "+place.getAddress() + "";
                }
                sLatitude = place.getLatLng().latitude + "";
                sLongitude = place.getLatLng().longitude + "";

                if((sAddress != null) && (!"".equalsIgnoreCase(sAddress.trim())))
                {
                    Tv_dropLocation.setText(sAddress);
                    Rl_done.setVisibility(View.VISIBLE);
                }else
                {
                    Tv_dropLocation.setText("");
                    Rl_done.setVisibility(View.GONE);
                }

                // Move the camera to last position with a zoom level
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(sLatitude), Double.parseDouble(sLongitude))).zoom(17).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        }
        else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
//            Log.e(TAG, "Error: Status = " + status.toString());
            isLocationType = false;
            CloseKeyBoard();
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            isLocationType = false;
            CloseKeyBoard();
        }
    }

    private void CloseKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    private void openAutocompleteActivity(double recent_lat, double recent_long) {
        try {

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(toBounds(new LatLng(recent_lat, recent_long), 50000)).build(DropLocationSelect.this);
            startActivityForResult(intent, ActivityDropRequestCode);
        } catch (GooglePlayServicesRepairableException e) {

            GoogleApiAvailability.getInstance().getErrorDialog(DropLocationSelect.this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {

            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(DropLocationSelect.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }


}
