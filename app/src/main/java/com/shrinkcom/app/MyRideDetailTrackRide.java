package com.shrinkcom.app;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.latlnginterpolation.LatLngInterpolator;
import com.mylibrary.latlnginterpolation.MarkerAnimation;
import com.mylibrary.volley.ServiceRequest;
import com.mylibrary.widgets.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.shrinkcom.adapter.EmergencyContactAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.CancelTripPojo;
import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.pojo.MultipleLatLongPojo;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by Prem Kumar and Anitha on 11/20/2015.
 */
public class MyRideDetailTrackRide extends FragmentActivity implements View.OnClickListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private RelativeLayout tv_done;
    private TextView tv_drivername, tv_carModel, tv_carNo, tv_rating, tv_pickup, tv_drop, tv_car_cat, tv_share_blink, tv_passenger_name;
    private RoundedImageView driver_image;
    private LinearLayout rl_callDriver, rl_endTrip, rl_share;
    private GoogleMap googleMap;
    //   private MarkerOptions marker;
    private GPSTracker gps;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String ride_type = "", driverID = "", driverName = "", driverImage = "", driverRating = "",
            driverLat = "", driverLong = "", driverTime = "", rideID = "", driverMobile = "",
            driverCar_no = "", driverCar_model = "", userLat = "", userLong = "", sRideStatus = "", cab_type = "", cabImage = "", co_rider_name = "", has_co_rider = "";
    private boolean isReasonAvailable = false;
    private ServiceRequest mRequest;
    private Dialog dialog;
    private SessionManager session;
    private String UserID = "";
    private ArrayList<CancelTripPojo> itemlist_reason;
    public static MyRideDetailTrackRide trackyour_ride_class;
    private TextView Tv_headerTitle;
    //   private View track_your_ride_view1;
    private LatLng fromPosition;
    private LatLng toPosition;
    private MarkerOptions markerOptions;
    private static Marker curentDriverMarker, DriverMarker;
    private static Marker movingMarker;
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    final static int REQUEST_LOCATION = 199;
    private Location currentLocation;
    private RefreshReceiver refreshReceiver;
    private Handler realTimeHandler = new Handler();


    String sPickUpLocation = "", sPickUpLatitude = "", sPickUpLongitude = "";
    String sDropLocation = "", sDropLatitude = "", sDropLongitude = "";
    private boolean isRidePickUpAvailable = false;
    private boolean isRideDropAvailable = false;

    final int PERMISSION_REQUEST_CODE = 111;
    final int PERMISSION_REQUEST_CODES = 222;
    private CardView panic_btn;
    private String sSelectedPanicNumber = "", dialCodeno = "";
    String base64;
    Bitmap bmp;
    private Dialog cantact_dialog, sms_popup;

    private MaterialDialog completejob_dialog;
    TextView code_;
    private EditText Et_share_trip_mobileno;

    CountryPicker picker;

    Animation blink;

    private ArrayList<LatLng> wayPointList;
    private LatLngBounds.Builder wayPointBuilder;
    private List<Polyline> polyLines;
    ArrayList<MultipleLatLongPojo> maplist;

    ArrayList<EmergencyPojo> emergencyList;
    private CardView Rl_drop;
    private View PickupLineView;
    private ImageView Iv_cabImg;
    private String mapCarIcon = "";
    private String sType = "";
    private ImageButton cur_location;
    private boolean foregroundState = false;
    private boolean pageRefresh = false;
    private LatLng driverLastlatLng = null;

    private void setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        ChatService.startUserAction(MyRideDetailTrackRide.this);
        startLocationUpdates();
        System.out.println("----------------jai------------------RESUME---------");
    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_TrackYourRide_REFRESH_Arrived_Driver")) {
                System.out.println("triparrived----------------------");
                cd = new ConnectionDetector(MyRideDetailTrackRide.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isInternetPresent) {
                    postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url, rideID);
                }
                   /* Tv_headerTitle.setText(getResources().getString(R.string.action_driver_arrived));
                    rl_endTrip.setVisibility(View.VISIBLE);
                //    track_your_ride_view1.setVisibility(View.VISIBLE);*/
            } else if (intent.getAction().equals("com.package.ACTION_CLASS_TrackYourRide_REFRESH_BeginTrip")) {
                System.out.println("tripbegin----------------------");

                cd = new ConnectionDetector(MyRideDetailTrackRide.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isInternetPresent) {
                    postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url, rideID);
                }
            } else if (intent.getAction().equals("com.package.track.ACTION_CLASS_TrackYourRide_REFRESH_UpdateDriver")) {
            } else if (intent.getAction().equals("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page")) {
                if (intent.getExtras() != null) {
                    rideID = intent.getStringExtra("rideID");
                    System.out.println("--------------Jai Refresh-------------------------");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (isInternetPresent) {
                        postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url, rideID);
                    }


                }

            } else if (intent.getAction().equals("com.package.ACTION_CLASS_TrackYourRide_foreground_REFRESH_page")) {
                foregroundState = true;
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isInternetPresent) {
                    postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url, rideID);
                }

            }

            System.out.println("out else--------------");
            if (intent.getExtras() != null && intent.getExtras().containsKey(Iconstant.isContinousRide)) {
                String lat = (String) intent.getExtras().get("latitude");
                String lng = (String) intent.getExtras().get("longitude");
                String bearing = (String) intent.getExtras().get("bearing");
                String ride_id = (String) intent.getExtras().get("ride_id");

//                Toast.makeText(MyRideDetailTrackRide.this,"TrackLatLng:"+lat+","+lng+" bearing :"+bearing,Toast.LENGTH_SHORT).show();
                try {
                    double lat_decimal = Double.parseDouble(lat);
                    double lng_decimal = Double.parseDouble(lng);
                    float bearing_float = Float.parseFloat(bearing);
                    updateDriverOnMap(lat_decimal, lng_decimal, bearing_float);
                    //    updateDriverOnMap(lat_decimal, lng_decimal);
                } catch (Exception e) {
                }
            }
        }
    }

    static LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();


    LatLngInterpolator mLatLngInterpolator;

    private void updateDriverOnMap(double lat_decimal, double lng_decimal, float bearing) {

        driverLastlatLng = new LatLng(lat_decimal, lng_decimal);

        if (mLatLngInterpolator == null) {
            mLatLngInterpolator = new LatLngInterpolator.Linear();
        }

       /* if (DriverMarker != null) {
            DriverMarker.remove();
        }*/
        if (DriverMarker != null) {

            //       curentDriverMarker.remove();
            System.out.println("values" + lat_decimal + " " + lng_decimal + " " + bearing);
            rotateMarker(DriverMarker, bearing, googleMap);
            MarkerAnimation.animateMarkerToGB(DriverMarker, driverLastlatLng, mLatLngInterpolator);

            float zoom = googleMap.getCameraPosition().zoom;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(driverLastlatLng).zoom(zoom).build();
            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.moveCamera(camUpdate);

        } else {
            mLatLngInterpolator = new LatLngInterpolator.Linear();
            System.out.println("values------" + lat_decimal + " " + lng_decimal + " " + bearing);

            if (bmp != null) {
                DriverMarker = googleMap.addMarker(new MarkerOptions()
                        .position(driverLastlatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 0.5f)
                        .flat(true));
            } else {
                Picasso.with(MyRideDetailTrackRide.this)
                        .load(mapCarIcon)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                if (bitmap != null) {
                                    String s = BitMapToString(bitmap);
                                    System.out.println("session bitmap" + s);
                                    session.setVehicle_BitmapImage(s);
                                    bmp = bitmap;

                                    DriverMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(driverLastlatLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                            .anchor(0.5f, 0.5f)
                                            .flat(true));
                                }

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                HashMap<String, String> bit_map = session.getVehicleBitmap();
                                base64 = bit_map.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
                                bmp = StringToBitMap(base64);
                                DriverMarker = googleMap.addMarker(new MarkerOptions()
                                        .position(driverLastlatLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .anchor(0.5f, 0.5f)
                                        .flat(true));
                                ;

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }


           /* DriverMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    .anchor(0.5f, 0.5f)
                    .flat(true));*/

            CameraPosition cameraPosition = new CameraPosition.Builder().target(driverLastlatLng).zoom(17).build();
            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.moveCamera(camUpdate);
        }
    }

    static public void rotateMarker(final Marker marker, final float toRotation, GoogleMap map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_your_ride_new);
        trackyour_ride_class = MyRideDetailTrackRide.this;
        initialize();
        try {
            setLocationRequest();
            buildGoogleApiClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeMap();
        System.out.println("----------------jai------------------CREATE---------");

        panic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----------------panic onclick method-----------------");
                panic();
            }
        });

        cur_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setLocationRequest();
                buildGoogleApiClient();

                if (gps.canGetLocation() && gps.isgpsenabled()) {
                    double Dlatitude = gps.getLatitude();
                    double Dlongitude = gps.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

            }
        });

    }


    private void initialize() {
        cd = new ConnectionDetector(MyRideDetailTrackRide.this);
        isInternetPresent = cd.isConnectingToInternet();
        gps = new GPSTracker(MyRideDetailTrackRide.this);
        session = new SessionManager(MyRideDetailTrackRide.this);
        itemlist_reason = new ArrayList<CancelTripPojo>();
        markerOptions = new MarkerOptions();
        maplist = new ArrayList<MultipleLatLongPojo>();

        wayPointList = new ArrayList<LatLng>();
        polyLines = new ArrayList<Polyline>();
        emergencyList = new ArrayList<EmergencyPojo>();


        tv_done = (RelativeLayout) findViewById(R.id.track_your_ride_done_textview);
        tv_drivername = (TextView) findViewById(R.id.myride_detail_track_your_ride_driver_name);
        tv_carModel = (TextView) findViewById(R.id.myride_detail_track_your_ride_driver_carmodel);
        tv_car_cat = (TextView) findViewById(R.id.track_your_ride_driver_vehicle_model_name);
        Tv_headerTitle = (TextView) findViewById(R.id.myride_detail_track_your_ride_track_label);
        tv_carNo = (TextView) findViewById(R.id.myride_detail_track_your_ride_driver_carNo);
        tv_rating = (TextView) findViewById(R.id.myride_detail_track_your_ride_driver_rating);
        driver_image = (RoundedImageView) findViewById(R.id.myride_detail_track_your_ride_driverimage);
        rl_callDriver = (LinearLayout) findViewById(R.id.myride_detail_track_your_ride_calldriver_layout);
        rl_endTrip = (LinearLayout) findViewById(R.id.track_your_ride_cancel_layout1);

        rl_share = (LinearLayout) findViewById(R.id.track_your_ride_share);
        tv_share_blink = (TextView) findViewById(R.id.myride_detail_track_your_ride_share_blink);
        tv_passenger_name = (TextView) findViewById(R.id.myride_detail_track_your_ride_share_passenger_name);
        picker = CountryPicker.newInstance("Select Country");

        Rl_drop = (CardView) findViewById(R.id.book_cardview_destination_address_layout);
        PickupLineView = (View) findViewById(R.id.view12);

        tv_drop = (TextView) findViewById(R.id.book_navigation_destination_address_search_address);
        tv_pickup = (TextView) findViewById(R.id.book_navigation_source_address_address_textView);
        Iv_cabImg = (ImageView) findViewById(R.id.track_your_ride_star_image);
        cur_location = (ImageButton) findViewById(R.id.myride_detail_track_your_ride_current_location);


        blink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        // share ncode to blink
        tv_share_blink.startAnimation(blink);


        //   track_your_ride_view1 = (View) findViewById(R.id.myride_detail_track_your_ride_view1);

        panic_btn = (CardView) findViewById(R.id.myride_detail_track_your_ride_panic_cardview_layout);

        emergencyList = session.getEmergencyContactDetails();
        for (int i = 0; i < emergencyList.size(); i++) {
            System.out.println("---emergencyList-----" + emergencyList.get(i).getTitle() + " " + emergencyList.get(i).getNumber());
        }


        Intent intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("type")) {
                sType = intent.getStringExtra("type");
            }
           /* driverID = intent.getStringExtra("driverID");
            driverName = intent.getStringExtra("driverName");
            driverImage = intent.getStringExtra("driverImage");
            driverRating = intent.getStringExtra("driverRating");
            driverLat = intent.getStringExtra("driverLat");
            driverLong = intent.getStringExtra("driverLong");
            driverTime = intent.getStringExtra("driverTime");*/
            rideID = intent.getStringExtra("rideID");
            /*driverMobile = intent.getStringExtra("driverMobile");
            driverCar_no = intent.getStringExtra("driverCar_no");
            driverCar_model = intent.getStringExtra("driverCar_model");
            userLat = intent.getStringExtra("userLat");
            userLong = intent.getStringExtra("userLong");
            sRideStatus = intent.getStringExtra("rideStatus");
            cab_type= intent.getStringExtra("cab_type");
            System.out.println("Ride Status--------------jai-------------->"+sRideStatus);
            if (intent.hasExtra("PickUpLocation")) {
                sPickUpLocation = intent.getStringExtra("PickUpLocation");
                sPickUpLatitude = intent.getStringExtra("PickUpLatitude");
                sPickUpLongitude = intent.getStringExtra("PickUpLongitude");
                tv_pickup.setText(sPickUpLocation);

                isRidePickUpAvailable = true;
            } else {
                isRidePickUpAvailable = false;
            }

            if (intent.hasExtra("DropLocation")) {
                sDropLocation = intent.getStringExtra("DropLocation");
                sDropLatitude = intent.getStringExtra("DropLatitude");
                sDropLongitude = intent.getStringExtra("DropLongitude");
                tv_drop.setText(sDropLocation);
                isRideDropAvailable = true;
            } else {
                isRideDropAvailable = false;
            }


        }*/
        }


        if (isInternetPresent) {
            postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url, rideID);
        }


        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_Arrived_Driver");
        intentFilter.addAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_BeginTrip");
        intentFilter.addAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_UpdateDriver");
        intentFilter.addAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page");
        intentFilter.addAction("com.package.ACTION_CLASS_TrackYourRide_foreground_REFRESH_page");
        registerReceiver(refreshReceiver, intentFilter);


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);


        tv_done.setOnClickListener(this);
        rl_callDriver.setOnClickListener(this);
        rl_endTrip.setOnClickListener(this);
        rl_share.setOnClickListener(this);
    }


    //-----------------------Track Ride Post Request-----------------
    private void postRequest_TrackRide(String Url, String SrideId_intent) {
        if (!foregroundState) {
            foregroundState = false;
            dialog = new Dialog(MyRideDetailTrackRide.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        }


        System.out.println("-------------Track Ride Url----------------" + Url);
        System.out.println("-------------Track Ride ride_id----------------" + SrideId_intent);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);

        mRequest = new ServiceRequest(MyRideDetailTrackRide.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Track Ride Response----------------" + response);
                String Sstatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject driver_profile_object = response_object.getJSONObject("driver_profile");
                            ride_type = response_object.getString("ride_type");
                            if (driver_profile_object.length() > 0) {
                                driverID = driver_profile_object.getString("driver_id");
                                driverName = driver_profile_object.getString("driver_name");
                                driverImage = driver_profile_object.getString("driver_image");
                                driverRating = driver_profile_object.getString("driver_review");

                                driverLat = driver_profile_object.getString("driver_lat");
                                driverLong = driver_profile_object.getString("driver_lon");

                                driverTime = driver_profile_object.getString("min_pickup_duration");
                                rideID = driver_profile_object.getString("ride_id");
                                driverMobile = driver_profile_object.getString("phone_number");
                                driverCar_no = driver_profile_object.getString("vehicle_number");
                                driverCar_model = driver_profile_object.getString("vehicle_model");
                                userLat = driver_profile_object.getString("rider_lat");
                                userLong = driver_profile_object.getString("rider_lon");
                                sRideStatus = driver_profile_object.getString("ride_status");
                                cab_type = driver_profile_object.getString("cab_type");
                                cabImage = driver_profile_object.getString("cab_image");
                                mapCarIcon = driver_profile_object.getString("map_car");
                                if (response_object.has("has_co_rider")) {
                                    has_co_rider = response_object.getString("has_co_rider");
                                }
                                if (response_object.has("co_rider_name")) {
                                    co_rider_name = response_object.getString("co_rider_name");
                                } else {
                                    co_rider_name = "";
                                }
                                Object check_pickUp_object = driver_profile_object.get("pickup");
                                if (check_pickUp_object instanceof JSONObject) {
                                    JSONObject pickup_object = driver_profile_object.getJSONObject("pickup");
                                    if (pickup_object.length() > 0) {
                                        sPickUpLocation = pickup_object.getString("location");
                                        JSONObject latLong_object = pickup_object.getJSONObject("latlong");
                                        if (latLong_object.length() > 0) {
                                            sPickUpLatitude = latLong_object.getString("lat");
                                            sPickUpLongitude = latLong_object.getString("lon");

                                            isRidePickUpAvailable = true;
                                        } else {
                                            isRidePickUpAvailable = false;
                                        }
                                    } else {
                                        isRidePickUpAvailable = false;
                                    }
                                } else {
                                    isRidePickUpAvailable = false;
                                }


                                Object check_drop_object = driver_profile_object.get("drop");
                                if (check_drop_object instanceof JSONObject) {
                                    JSONObject drop_object = driver_profile_object.getJSONObject("drop");
                                    if (drop_object.length() > 0) {
                                        sDropLocation = drop_object.getString("location");
                                        JSONObject latLong_object = drop_object.getJSONObject("latlong");
                                        if (latLong_object.length() > 0) {
                                            sDropLatitude = latLong_object.getString("lat");
                                            sDropLongitude = latLong_object.getString("lon");

                                            isRideDropAvailable = true;
                                        } else {
                                            isRideDropAvailable = false;
                                        }
                                    } else {
                                        isRideDropAvailable = false;
                                    }
                                } else {
                                    isRideDropAvailable = false;
                                }

                                maplist.clear();
                                JSONArray maparray = response_object.getJSONArray("map_locations");
                                if (maparray.length() > 0) {
                                    for (int i = 0; i < maparray.length(); i++) {
                                        JSONObject map_object = maparray.getJSONObject(i);
                                        MultipleLatLongPojo mapojo = new MultipleLatLongPojo();
                                        mapojo.setLat(map_object.getString("lat"));
                                        mapojo.setLon(map_object.getString("lon"));
                                        maplist.add(mapojo);

                                    }
                                }


                                if (ride_type.equals("Share")) {

                                    if (has_co_rider.equals("1")) {
                                        tv_share_blink.clearAnimation();
                                        tv_share_blink.setText(getResources().getString(R.string.Track_ur_ride_co_passenger_yes));
                                        tv_passenger_name.setText(co_rider_name);
                                        tv_passenger_name.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_share_blink.setText(getResources().getString(R.string.Track_ur_ride_co_passenger));
                                        tv_passenger_name.setVisibility(View.GONE);
                                    }

                                } else {
                                    tv_share_blink.setVisibility(View.GONE);
                                    tv_passenger_name.setVisibility(View.GONE);
                                }


                                tv_drivername.setText(driverName);
                                tv_carNo.setText(driverCar_no);
                                tv_carModel.setText(driverCar_model);
                                tv_rating.setText(driverRating);
                                tv_car_cat.setText(cab_type);
                                tv_pickup.setText(sPickUpLocation);
                                tv_drop.setText(sDropLocation);
                                if (sDropLocation != null && !"".equalsIgnoreCase(sDropLocation)) {
                                    Rl_drop.setVisibility(View.VISIBLE);
                                    PickupLineView.setVisibility(View.VISIBLE);
                                } else {
                                    Rl_drop.setVisibility(View.GONE);
                                    PickupLineView.setVisibility(View.GONE);
                                }

                                Picasso.with(MyRideDetailTrackRide.this)
                                        .load(driverImage).placeholder(R.drawable.default1)
                                        .into(driver_image);

                                Picasso.with(MyRideDetailTrackRide.this)
                                        .load(cabImage)
                                        .into(Iv_cabImg);

                                Picasso.with(MyRideDetailTrackRide.this)
                                        .load(mapCarIcon)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                                if (bitmap != null) {
                                                    String s = BitMapToString(bitmap);
                                                    System.out.println("session bitmap" + s);
                                                    session.setVehicle_BitmapImage(s);
                                                    bmp = bitmap;
                                                }
                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable errorDrawable) {
                                                HashMap<String, String> bit_map = session.getVehicleBitmap();
                                                base64 = bit_map.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
                                                bmp = StringToBitMap(base64);

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });


                                if (sRideStatus.equalsIgnoreCase("Onride")) {
                                    Tv_headerTitle.setText(getResources().getString(R.string.action_enjy_your_ride));
                                    rl_endTrip.setVisibility(View.GONE);
                                    //       track_your_ride_view1.setVisibility(View.GONE);
                                    panic_btn.setVisibility(View.VISIBLE);
                                } else if (sRideStatus.equalsIgnoreCase("arrived")) {
                                    Tv_headerTitle.setText(getResources().getString(R.string.action_driver_arrived));
                                    rl_endTrip.setVisibility(View.VISIBLE);
                                    //       track_your_ride_view1.setVisibility(View.VISIBLE);
                                } else if (sRideStatus.equalsIgnoreCase("Confirmed")) {
                                    Tv_headerTitle.setText(getResources().getString(R.string.track_your_ride_label_track));
                                    rl_endTrip.setVisibility(View.VISIBLE);
                                    //        track_your_ride_view1.setVisibility(View.VISIBLE);
                                } else {
                                    if ("push".equalsIgnoreCase(sType)) {
                                        Intent i = new Intent(MyRideDetailTrackRide.this, NavigationDrawer.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    } else {

                                        Intent finish_timerPage = new Intent();
                                        finish_timerPage.setAction("com.mylibrary.pushnotification.finish.TimerPage");
                                        sendBroadcast(finish_timerPage);
                                        Intent broadcastIntent = new Intent();
                                        broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                                        sendBroadcast(broadcastIntent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                }
                                System.out.println("-------maplist.size(------------" + maplist.size());


                                if (maplist.size() >= 2) {

                                    GetRouteTask getRoute = new GetRouteTask(maplist, driverLat, driverLong);
                                    getRoute.execute();
                                } else {
                                    String sLat = maplist.get(0).getLat();
                                    String sLng = maplist.get(0).getLon();

                                    double Dlatitude = Double.parseDouble(sLat);
                                    double Dlongitude = Double.parseDouble(sLng);

                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                    googleMap.addMarker(marker);
                                }


                                /*if (DriverMarker != null) {
                                    DriverMarker.remove();
                                }*/


                            }
                        }
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";

        private ArrayList<LatLng> wayLatLng;
        private ArrayList<MultipleLatLongPojo> multipleDropList;
        private String dLat, dLong;

        GetRouteTask(ArrayList<MultipleLatLongPojo> multipleDropList, String lat, String lon) {
            this.multipleDropList = multipleDropList;
            dLat = lat;
            dLong = lon;
            wayLatLng = addWayPointPoint(multipleDropList, dLat, dLong);
            if (wayLatLng.size() < 2) {
                wayLatLng.clear();
                wayLatLng = addWayPointPoint(maplist, dLat, dLong);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {

            System.out.println("-------jai wayLatLng---------" + wayLatLng);
            try {
                if (wayLatLng.size() >= 2) {

                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(listner)
                            .alternativeRoutes(true)
                            .key(Utils.GOOGLE_KEY)
                            .waypoints(wayLatLng)
                            .build();
                    routing.execute();
                }

            } catch (Exception e) {

            }


            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
            }
        }
    }

    private ArrayList<LatLng> addWayPointPoint(ArrayList<MultipleLatLongPojo> mMultipleDropLatLng, String lat, String lon) {


        try {
            if (googleMap != null) {
                googleMap.clear();
                wayPointList.clear();

                wayPointBuilder = new LatLngBounds.Builder();


                if (mMultipleDropLatLng != null) {

                    for (int i = 0; i < mMultipleDropLatLng.size(); i++) {

                        String sLat = mMultipleDropLatLng.get(i).getLat();
                        String sLng = mMultipleDropLatLng.get(i).getLon();


                        double Dlatitude = Double.parseDouble(sLat);
                        double Dlongitude = Double.parseDouble(sLng);

                        System.out.println("------jai----lat and long-----------" + Dlatitude + "sfsdfsdfdsd" + Dlongitude);

                        wayPointList.add(new LatLng(Dlatitude, Dlongitude));
                        wayPointBuilder.include(new LatLng(Dlatitude, Dlongitude));


                        // wayPointList.add(new LatLng(Dlatitude, Dlongitude));

                        if ("Share".equalsIgnoreCase(ride_type)) {
                            if (i == 0) {
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                googleMap.addMarker(marker);
                            } else if (i == mMultipleDropLatLng.size() - 1) {

                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                googleMap.addMarker(marker);
                            } else {
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_map_pointer_pin));
                                googleMap.addMarker(marker);
                            }
                        } else {
                            if (i == 0) {
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                                googleMap.addMarker(marker);
                            } else if (i == mMultipleDropLatLng.size() - 1) {

                                MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                googleMap.addMarker(marker);
                            }
                        }
                        if (!lat.equalsIgnoreCase("") && !lon.equalsIgnoreCase("")) {
                            double lat_decimal = Double.parseDouble(lat);
                            double lng_decimal = Double.parseDouble(lon);
                            if (lat_decimal != 0.0 && lng_decimal != 0.0) {
                                driverLastlatLng = new LatLng(lat_decimal, lng_decimal);
                            }
                        }

                        if (driverLastlatLng != null && (driverLastlatLng.latitude != 0.0 && driverLastlatLng.longitude != 0.0)) {
                            if (DriverMarker != null) {
                                DriverMarker.remove();
                                DriverMarker = null;
                            }
                            if (bmp != null) {
                                DriverMarker = googleMap.addMarker(new MarkerOptions()
                                        .position(driverLastlatLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                        .anchor(0.5f, 0.5f)
                                        .flat(true));
                            } else {
                                Picasso.with(MyRideDetailTrackRide.this)
                                        .load(mapCarIcon)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                                if (bitmap != null) {
                                                    String s = BitMapToString(bitmap);
                                                    System.out.println("session bitmap" + s);
                                                    session.setVehicle_BitmapImage(s);
                                                    bmp = bitmap;

                                                    DriverMarker = googleMap.addMarker(new MarkerOptions()
                                                            .position(driverLastlatLng)
                                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                            .anchor(0.5f, 0.5f)
                                                            .flat(true));
                                                }

                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable errorDrawable) {
                                                HashMap<String, String> bit_map = session.getVehicleBitmap();
                                                base64 = bit_map.get(SessionManager.KEY_VEHICLE_BitMap_IMAGE);
                                                bmp = StringToBitMap(base64);
                                                DriverMarker = googleMap.addMarker(new MarkerOptions()
                                                        .position(driverLastlatLng)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                        .anchor(0.5f, 0.5f)
                                                        .flat(true));
                                                ;

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });
                            }

                        } else {
                            if (DriverMarker != null) {
                                DriverMarker.remove();
                                DriverMarker = null;
                            }
                        }

                        // wayPointBuilder.include(new LatLng(Dlatitude, Dlongitude));


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wayPointList;
    }

    RoutingListener listner = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            System.out.println("-----------jai onRoutingFailure-----------------" + e);
            if (dialog != null) {
                dialog.dismiss();
            }

            if (maplist.size() >= 2) {
                GetRouteTask getRoute = new GetRouteTask(maplist, driverLat, driverLong);
                getRoute.execute();
            } else {
                Intent broadcastIntent1 = new Intent();
                broadcastIntent1.setAction("com.package.ACTION_CLASS_TrackYourRide_REFRESH_page");
                broadcastIntent1.putExtra("rideID", rideID);
                sendBroadcast(broadcastIntent1);
            }
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            if (polyLines.size() > 0) {
                for (Polyline poly : polyLines) {
                    poly.remove();
                }
            }

            polyLines = new ArrayList<>();

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.app_color));
            polyOptions.width(7);
            polyOptions.addAll(arrayList.get(0).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polyLines.add(polyline);


            System.out.println("------------route--------------jai----" + arrayList);


            if (wayPointBuilder != null) {
                LatLngBounds bounds = wayPointBuilder.build();
                int padding = 90; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cu);
            }
        }

        @Override
        public void onRoutingCancelled() {

        }
    };


    private void panic() {
        System.out.println("----------------panic method-----------------");
        View view = View.inflate(MyRideDetailTrackRide.this, R.layout.panic_page, null);
        final MaterialDialog dialog = new MaterialDialog(MyRideDetailTrackRide.this);
        dialog.setContentView(view).setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }
        ).show();
        ListView emergencyListview = (ListView) view.findViewById(R.id.panic_listView);

        EmergencyContactAdapter emergencyContactAdapter = new EmergencyContactAdapter(MyRideDetailTrackRide.this, emergencyList);
        emergencyListview.setAdapter(emergencyContactAdapter);

        emergencyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();

                sSelectedPanicNumber = emergencyList.get(position).getNumber();
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                        requestPermissions();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                        startActivity(callIntent);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                    startActivity(callIntent);
                }

            }
        });

    }


    private void initializeMap() {
        if (googleMap == null) {

            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.myride_detail_track_your_ride_mapview));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);


                }
            });

            /*googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.myride_detail_track_your_ride_mapview)).getMap();
            if (googleMap == null) {
                Toast.makeText(MyRideDetailTrackRide.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }*/
        }
        // Changing map type

    }


    public void loadMap(GoogleMap arg) {
        googleMap = arg;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MyRideDetailTrackRide.this, R.raw.mapstyle));
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture

        // Enable / Disable zooming functionality

        googleMap.setMyLocationEnabled(false);

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


        googleMap.getUiSettings().setTiltGesturesEnabled(false);


        if (gps.canGetLocation() && gps.isgpsenabled()) {
            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();
            MyCurrent_lat = Dlatitude;
            MyCurrent_long = Dlongitude;
            // Move the camera to last position with a zoom level
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
        }


    }

    @Override
    public void onClick(View v) {
        if (v == tv_done) {
            if ("push".equalsIgnoreCase(sType)) {
                Intent i = new Intent(MyRideDetailTrackRide.this, NavigationDrawer.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } else {

                Intent finish_timerPage = new Intent();
                finish_timerPage.setAction("com.mylibrary.pushnotification.finish.TimerPage");
                sendBroadcast(finish_timerPage);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                sendBroadcast(broadcastIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        } else if (v == rl_callDriver) {
            if (driverMobile != null) {

                showContactOptionsPopup();

/*
                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);


                if (mask_status.equalsIgnoreCase("Yes")) {

                    System.out.println("=========PHONEMASKINGSTATUS track ur ride YES ==========>= " + mask_status);

                    cd = new ConnectionDetector(MyRideDetailTrackRide.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        phonemask_Call(Iconstant.phoneMasking);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    if (driverMobile != null) {

                        System.out.println("=========PHONEMASKINGSTATUS NO==========>= " + mask_status);

                        if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                requestPermission();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driverMobile));
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driverMobile));
                            startActivity(intent);

                        }
                    } else {
                        Alert(MyRideDetailTrackRide.this.getResources().getString(R.string.alert_label_title), MyRideDetailTrackRide.this.getResources().getString(R.string.track_your_ride_alert_content1));
                    }
                }

*/

/*                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                        requestPermission();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + driverMobile));
                        startActivity(callIntent);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + driverMobile));
                    startActivity(callIntent);
                }*/
            } else {
                Alert(MyRideDetailTrackRide.this.getResources().getString(R.string.alert_label_title), MyRideDetailTrackRide.this.getResources().getString(R.string.track_your_ride_alert_content1));
            }
        } else if (v == rl_endTrip) {
            final PkDialog mDialog = new PkDialog(MyRideDetailTrackRide.this);
            mDialog.setDialogTitle(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_title));
            mDialog.setDialogMessage(getResources().getString(R.string.my_rides_detail_cancel_ride_alert));
            mDialog.setPositiveButton(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    cd = new ConnectionDetector(MyRideDetailTrackRide.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        postRequest_CancelRides_Reason(Iconstant.cancel_myride_reason_url);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_no), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.show();

        } else if (v == rl_share) {
            shareTrip();

        }

    }


    private void shareTrip() {
        completejob_dialog = new MaterialDialog(MyRideDetailTrackRide.this);
        View view = LayoutInflater.from(MyRideDetailTrackRide.this).inflate(R.layout.share_trip_popup, null);
        Et_share_trip_mobileno = (EditText) view.findViewById(R.id.sharetrip_mobilenoEt);
        Button Bt_Submit = (Button) view.findViewById(R.id.jsharetrip_popup_submit);
        Button Bt_Cancel = (Button) view.findViewById(R.id.sharetrip_popup_cancel);
        code_ = (TextView) view.findViewById(R.id.sharetrip_country_codeEt);

        Et_share_trip_mobileno.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        completejob_dialog.setView(view).show();

        code_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                code_.setText(dialCode);
                dialCodeno = dialCode;
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(code_.getWindowToken(), 0);
            }
        });

        Bt_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidPhoneNumber(Et_share_trip_mobileno.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.profile_lable_error_mobile));
                } else if (!isValidCodeNumber(code_.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.register_label_alert_country_code));
                } else {
                    if (isInternetPresent) {
                        share_trip_postRequest_MyRides(MyRideDetailTrackRide.this, Iconstant.share_trip_url, "jobcomplete");
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });

        Bt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completejob_dialog.dismiss();
            }
        });

    }

    //----------------------------------Share Trip post reques------------------------
    private void share_trip_postRequest_MyRides(Context mContext, String url, String key) {
        dialog = new Dialog(MyRideDetailTrackRide.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("------------- ride_id----------------" + rideID);
        System.out.println("------------- mobile_no----------------" + dialCodeno + Et_share_trip_mobileno.getText().toString());

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", rideID);
        jsonParams.put("mobile_no", dialCodeno + Et_share_trip_mobileno.getText().toString());

        mRequest = new ServiceRequest(MyRideDetailTrackRide.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("share trip", response);

                String Str_status = "", Str_response = "";
                System.out.println("sharetrip response-------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");

                    if (Str_status.equalsIgnoreCase("1")) {

                        AlertCloseShare(getResources().getString(R.string.action_success), Str_response);

                    } else {
                        AlertCloseShare(getResources().getString(R.string.action_error), Str_response);

                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });

    }

    private void AlertCloseShare(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRideDetailTrackRide.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                if (completejob_dialog != null) {
                    completejob_dialog.dismiss();
                }
            }
        });
        mDialog.show();

    }


    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static final boolean isValidCodeNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return true;
        }
    }

    public void showContactOptionsPopup() {

        cantact_dialog = new Dialog(MyRideDetailTrackRide.this);
        cantact_dialog.getWindow();
        cantact_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cantact_dialog.setContentView(R.layout.choose_contact_popup);
        cantact_dialog.setCanceledOnTouchOutside(true);
        cantact_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        cantact_dialog.show();
        cantact_dialog.getWindow().setGravity(Gravity.CENTER);

        RelativeLayout call = (RelativeLayout) cantact_dialog
                .findViewById(R.id.call_layout);
        RelativeLayout message = (RelativeLayout) cantact_dialog
                .findViewById(R.id.message_layout);
        RelativeLayout bottom_layout = (RelativeLayout) cantact_dialog
                .findViewById(R.id.bottom_layout);

        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantact_dialog.dismiss();
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);


                if (mask_status.equalsIgnoreCase("Yes")) {

                    System.out.println("=========PHONEMASKINGSTATUS track ur ride YES ==========>= " + mask_status);

                    cd = new ConnectionDetector(MyRideDetailTrackRide.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        phonemask_Call(Iconstant.phoneMasking);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    if (driverMobile != null) {

                        System.out.println("=========PHONEMASKINGSTATUS NO==========>= " + mask_status);

                        if (Build.VERSION.SDK_INT >= 23) {
                            // Marshmallow+
                            if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                                requestPermission();
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driverMobile));
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driverMobile));
                            startActivity(intent);

                        }
                    } else {
                        Alert(MyRideDetailTrackRide.this.getResources().getString(R.string.alert_label_title), MyRideDetailTrackRide.this.getResources().getString(R.string.track_your_ride_alert_content1));
                    }
                }


                cantact_dialog.dismiss();


            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantact_dialog.dismiss();
                HashMap<String, String> mask = session.getPhoneMasking();
                String mask_status = mask.get(SessionManager.KEY_PHONE_MASKING_STATUS);


                /*System.out.println("=========PHONEMASKINGSTATUS track ur ride get ==========>= " + mask_status);

                Intent n = new Intent(Intent.ACTION_VIEW);
                n.setType("vnd.android-dir/mms-sms");
                n.putExtra("address", driverMobile);
                startActivity(n);*/

                if (mask_status.equalsIgnoreCase("Yes")) {
                    showMessagePopup();


                } else {

                    try {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + Uri.encode(driverMobile)));
                        startActivity(intent);
                    } catch (Exception e) {

                    }

                   /* Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", driverMobile);
                    startActivity(i);*/


                }


            }
        });
    }

    public void showMessagePopup() {

        sms_popup = new Dialog(MyRideDetailTrackRide.this);
        sms_popup.getWindow();
        sms_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sms_popup.setContentView(R.layout.sms_popup);
        sms_popup.setCanceledOnTouchOutside(true);
        sms_popup.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sms_popup.show();
        sms_popup.getWindow().setGravity(Gravity.CENTER);

        TextView cancel = (TextView) sms_popup
                .findViewById(R.id.cancel);
        final EditText ed_msg = (EditText) sms_popup
                .findViewById(R.id.text_editview);
        TextView send = (TextView) sms_popup
                .findViewById(R.id.send);

        ed_msg.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sms_popup.dismiss();


            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = ed_msg.getText().toString();
                sms_popup.dismiss();
                if (sms.trim().length() > 0) {
                    phonemask_sms(Iconstant.phoneMasking_sms, sms);
                } else {
                    Alert(getResources().getString(R.string.timer_label_alert_sorry), getResources().getString(R.string.sms_masking_text));
                }

            }
        });
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(MyRideDetailTrackRide.this);
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

    //--------------Alert Method-----------
    private void Alert1(String title, String alert) {
        final PkDialog mDialog = new PkDialog(MyRideDetailTrackRide.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                sendBroadcast(broadcastIntent);

                Intent finish_MyRideDetails = new Intent();
                finish_MyRideDetails.setAction("com.package.track.ACTION_CLASS_TrackYourRide_FINISH");
                sendBroadcast(finish_MyRideDetails);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /* //---------------AsyncTask to Draw PolyLine Between Two Point--------------
     public class GetRouteTask extends AsyncTask<String, Void, String> {

         String response = "";
         GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
         Document document;

         @Override
         protected void onPreExecute() {
         }

         @Override
         protected String doInBackground(String... urls) {
             //Get All Route values

             document = v2GetRouteDirection.getDocument(toPosition, fromPosition, GMapV2GetRouteDirection.MODE_DRIVING);
             response = "Success";
             return response;

         }

         @Override
         protected void onPostExecute(String result) {
             if (result.equalsIgnoreCase("Success")) {
                 if(googleMap != null){
                     googleMap.clear();
                 }
                 try {
                     ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                     PolylineOptions rectLine = new PolylineOptions().width(18).color(getResources().getColor(R.color.ployline_color));
                     for (int i = 0; i < directionPoint.size(); i++) {
                         rectLine.add(directionPoint.get(i));
                     }
                     // Adding route on the map
                     googleMap.addPolyline(rectLine);
                     markerOptions.position(fromPosition);
                     markerOptions.position(toPosition);
                     markerOptions.draggable(true);

                     System.out.println("-------------inside getRoute---------------------");



                     //googleMap.addMarker(markerOptions);
                     googleMap.addMarker(new MarkerOptions()
                             .position(fromPosition)
                             .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));
                     googleMap.addMarker(new MarkerOptions()
                             .position(toPosition)
                             .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                     System.out.println("bitmap---jai----------->"+bmp);


                     System.out.println("fromPosition----first-----jai-----drop_marker--------------"+fromPosition);
                     System.out.println("toPosition----first-----jai-----pickup_marker--------------"+toPosition);

                     curentDriverMarker = googleMap.addMarker(new MarkerOptions()
                             .position(toPosition)
                             .icon(BitmapDescriptorFactory.fromBitmap(bmp)));

                     //Show path in
                     LatLngBounds.Builder builder = new LatLngBounds.Builder();
                     builder.include(fromPosition);
                     builder.include(toPosition);
                     LatLngBounds bounds = builder.build();
                     googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }*/
    private void phonemask_Call(String Url) {
        dialog = new Dialog(MyRideDetailTrackRide.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("-------------phone Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", rideID);
        jsonParams.put("user_type", "user");

        System.out.println("ride_id---------" + rideID);
        System.out.println("user_type---------" + "user");

        mRequest = new ServiceRequest(MyRideDetailTrackRide.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert(getResources().getString(R.string.action_success), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.timer_label_alert_sorry), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    private void phonemask_sms(String Url, String msg) {
        dialog = new Dialog(MyRideDetailTrackRide.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("-------------phonemask_sms----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", rideID);
        jsonParams.put("user_type", "user");
        jsonParams.put("sms_content", msg);
        System.out.println("ride_id---------" + rideID);
        System.out.println("user_type---------" + "user");

        mRequest = new ServiceRequest(MyRideDetailTrackRide.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println(response);
                Log.e("phonemask_sms", response);
                String Sstatus = "", SResponse = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    SResponse = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Alert(getResources().getString(R.string.action_success), SResponse);
                    } else {
                        Alert(getResources().getString(R.string.timer_label_alert_sorry), SResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //-----------------------MyRide Cancel Reason Post Request-----------------
    private void postRequest_CancelRides_Reason(String Url) {
        dialog = new Dialog(MyRideDetailTrackRide.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("-------------MyRide Cancel Reason Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("ride_id", rideID);

        jsonParams.put("id", UserID);

        jsonParams.put("user_type", "user");

        mRequest = new ServiceRequest(MyRideDetailTrackRide.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------MyRide Cancel Reason Response----------------" + response);
                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONArray reason_array = response_object.getJSONArray("reason");
                            if (reason_array.length() > 0) {
                                itemlist_reason.clear();
                                for (int i = 0; i < reason_array.length(); i++) {
                                    JSONObject reason_object = reason_array.getJSONObject(i);
                                    CancelTripPojo pojo = new CancelTripPojo();
                                    pojo.setReason(reason_object.getString("reason"));
                                    pojo.setReasonId(reason_object.getString("id"));
                                    itemlist_reason.add(pojo);
                                }
                                isReasonAvailable = true;
                            } else {
                                isReasonAvailable = false;
                            }
                        }
                    } else {
                        String Sresponse = object.getString("response");
                        Alert1(getResources().getString(R.string.alert_label_title), Sresponse);
                    }


                    if (Sstatus.equalsIgnoreCase("1") && isReasonAvailable) {
                        Intent finish_timerPage = new Intent();
                        finish_timerPage.setAction("com.mylibrary.pushnotification.finish.TimerPage");
                        sendBroadcast(finish_timerPage);
                        Intent passIntent = new Intent(MyRideDetailTrackRide.this, TrackRideCancelTrip.class);
                        Bundle bundleObject = new Bundle();
                        bundleObject.putSerializable("Reason", itemlist_reason);
                        passIntent.putExtras(bundleObject);
                        passIntent.putExtra("RideID", rideID);
                        startActivity(passIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            if ("push".equalsIgnoreCase(sType)) {
                Intent i = new Intent(MyRideDetailTrackRide.this, NavigationDrawer.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } else {

                Intent finish_timerPage = new Intent();
                finish_timerPage.setAction("com.mylibrary.pushnotification.finish.TimerPage");
                sendBroadcast(finish_timerPage);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                sendBroadcast(broadcastIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();

        if (mRequest != null) {
            mRequest.cancelRequest();
        }


    }


    /*//---------------AsyncTask to Draw PolyLine Between Two Point--------------
    private class GetDropRouteTask extends AsyncTask<String, Void, String> {

        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;
        private LatLng currentLocation;
        private LatLng endLocation;

        public void setToAndFromLocation(LatLng currentLocation, LatLng endLocation) {
            this.currentLocation = currentLocation;
            this.endLocation = endLocation;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(endLocation, currentLocation, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Success")) {
                if(googleMap != null){
                    googleMap.clear();
                }
                try {
                    ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                    PolylineOptions rectLine = new PolylineOptions().width(18).color(
                            getResources().getColor(R.color.ployline_color));
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    // Adding route on the map
                    googleMap.addPolyline(rectLine);
                    markerOptions.position(endLocation);
                    markerOptions.position(currentLocation);
                    markerOptions.draggable(true);

                    //googleMap.addMarker(markerOptions);
                    googleMap.addMarker(new MarkerOptions()
                            .position(endLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));

                    curentDriverMarker = googleMap.addMarker(new MarkerOptions()
                            .position(endLocation)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp)));


                    System.out.println("endLocation----xmpp-----jai-----pickup_marker--------------"+endLocation);
                    System.out.println("currentLocation----xmpp-----jai-----drop_marker--------------"+currentLocation);


                    System.out.println("inside---------marker--------------");

                    //Show path in
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(endLocation);
                    builder.include(currentLocation);
                    LatLngBounds bounds = builder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 162));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODES);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + driverMobile));
                    startActivity(callIntent);
                }
                break;


            case PERMISSION_REQUEST_CODES:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                    startActivity(callIntent);
                }
                break;

        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        System.out.println("base 64" + encodedString);
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void DialogBargaining() {
        final String Current_Amount = new SessionManager(getApplicationContext()).getEstimateAmount();
        final Dialog dialog = new Dialog(MyRideDetailTrackRide.this);
        dialog.setContentView(R.layout.bargaining_dialog);
        AppCompatTextView title_text_dialog = dialog.findViewById(R.id.title_text_dialog);
        title_text_dialog.setText("Current Estimate Amount is " + Current_Amount + " you can Update Amount by Bargain from driver Enter Amount and hit Update.");
        final AppCompatEditText amount_enter_bargaining = dialog.findViewById(R.id.amount_enter_bargaining);
        amount_enter_bargaining.setText("" + Current_Amount);

        AppCompatButton bargaining_accept_button = dialog.findViewById(R.id.bargaining_accept_button);
        AppCompatButton bargaining_update_button = dialog.findViewById(R.id.bargaining_update_button);
        AppCompatButton bargaining_cancel_button = dialog.findViewById(R.id.bargaining_cancel_button);
        bargaining_accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bargaining_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount_enter_bargaining.getText().toString().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Amount Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Current_Amount != null) {
                    try {
                        Double bargain_Amount = Double.parseDouble(amount_enter_bargaining.getText().toString());
                        Double amount = Double.parseDouble(Current_Amount);
                        if (amount < bargain_Amount) {
                            Toast.makeText(getApplicationContext(), "Amount Cannot Be Grater Then Estimated Amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (amount.equals(bargain_Amount)) {
                            Toast.makeText(getApplicationContext(), "Amount Cannot Be Equal To Estimated Amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bargaining_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // set the custom dialog components - text, image and button


        dialog.show();
    }
}
