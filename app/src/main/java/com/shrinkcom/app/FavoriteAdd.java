package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.GeocoderHelper;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
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
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static java.lang.Double.parseDouble;


/**
 * Created by Prem Kumar and Anitha on 11/13/2015.
 */
public class FavoriteAdd extends Activity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";
    private String SselectedAddress = "", Slatitude = "", Slongitude = "", Stitle = "", SlocationKey = "", SidentityKey = "";

    private RelativeLayout Rl_back, Rl_save;
    private EditText Et_name;
    private TextView Tv_address;
    private ImageView currentLocation_image;
    private ServiceRequest mRequest, editRequest;
    private GoogleMap googleMap;
    GPSTracker gps;
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    Dialog dialog;

    private RelativeLayout Rl_alert;
    private TextView Tv_alert;
    private boolean isAddressAvailable = false;
    GoogleMap.OnCameraChangeListener mOnCameraChangeListener;

    private int placeSearch_request_code = 500;
    private boolean isLocationType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_add);
        initialize();
        initializeMap();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        Tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocationType = true;
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(toBounds(new LatLng(Double.parseDouble(Slatitude),Double.parseDouble(Slongitude)), 50000)).build(FavoriteAdd.this);
                    startActivityForResult(intent, placeSearch_request_code);
                } catch (GooglePlayServicesRepairableException e) {

                    GoogleApiAvailability.getInstance().getErrorDialog(FavoriteAdd.this, e.getConnectionStatusCode(),
                            0 /* requestCode */).show();
                } catch (GooglePlayServicesNotAvailableException e) {

                    String message = "Google Play Services is not available: " +
                            GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                    Toast.makeText(FavoriteAdd.this, message, Toast.LENGTH_SHORT).show();
                }


               /* Intent intent = new Intent(FavoriteAdd.this, FavLocationSearch.class);
                intent.putExtra("nearLatitude", String.valueOf(Slatitude));
                intent.putExtra("nearLongitude", String.valueOf(Slongitude));
                startActivityForResult(intent, placeSearch_request_code);
                overridePendingTransition(R.anim.slideup, R.anim.slidedown);*/
            }
        });
        currentLocation_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(FavoriteAdd.this);
                isInternetPresent = cd.isConnectingToInternet();
                gps = new GPSTracker(FavoriteAdd.this);

                if (gps.isgpsenabled() && gps.canGetLocation()) {

                    double Dlatitude = gps.getLatitude();
                    double Dlongitude = gps.getLongitude();

                    // Move the camera to last position with a zoom level
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    Toast.makeText(FavoriteAdd.this, "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(FavoriteAdd.this);
                isInternetPresent = cd.isConnectingToInternet();
                Et_name.setText(Et_name.getText().toString().trim());
                if (isInternetPresent) {
                    if (Et_name.getText().toString().trim().length() == 0) {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.favorite_add_label_name_empty));
                    } else {
                        if (Tv_address.getText().length() > 0 && !Tv_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.favorite_add_label_gettingAddress))) {
                            if (SidentityKey.equalsIgnoreCase("Edit")) {
                                postRequest_FavoriteEdit(Iconstant.favoritelist_edit_url);
                            } else {
                                postRequest_FavoriteSave(Iconstant.favoritelist_add_url);
                            }
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.favorite_add_label_invalid_address));
                        }
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }

            }
        });

         mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                double latitude = 0.0;
                double longitude = 0.0;
                if (!isLocationType){
                    latitude = cameraPosition.target.latitude;
                    longitude = cameraPosition.target.longitude;
                }else{
                    latitude = Double.parseDouble(Slatitude);
                    longitude = Double.parseDouble(Slongitude);
                }

                Rl_save.setVisibility(View.GONE);
                cd = new ConnectionDetector(FavoriteAdd.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (latitude != 0.0) {
                    googleMap.clear();

                    Slatitude = String.valueOf(latitude);
                    Slongitude = String.valueOf(longitude);

                    if (isInternetPresent) {
                        if (Slatitude != null && Slongitude != null) {
                            GetCompleteAddressAsyncTask asyncTask = new GetCompleteAddressAsyncTask();
                            asyncTask.execute();
                        } else {
                            Rl_alert.setVisibility(View.VISIBLE);
                            Tv_alert.setText(getResources().getString(R.string.favorite_add_label_no_address));
                        }
                    } else {
                        Rl_alert.setVisibility(View.VISIBLE);
                        Tv_alert.setText(getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        };

        if (CheckPlayService()) {
            if(googleMap!=null) {
            googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String tittle = marker.getTitle();
                    Log.e("tittle--on_camera_change---->", "" + tittle);
                    return true;

                }
            });
        }
        } else {
            Alert(getResources().getString(R.string.alert_label_title), "Install Google Play service To View Location !!!");
        }

        Et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_name);
                }
                return false;
            }
        });
    }

    private void initialize() {
        session = new SessionManager(FavoriteAdd.this);
        cd = new ConnectionDetector(FavoriteAdd.this);
        isInternetPresent = cd.isConnectingToInternet();
        gps = new GPSTracker(FavoriteAdd.this);

        Rl_back = (RelativeLayout) findViewById(R.id.favorite_add_header_back_layout);
        Rl_save = (RelativeLayout) findViewById(R.id.favorite_add_header_save_layout);
        Et_name = (EditText) findViewById(R.id.favorite_add_name_edittext);
        Tv_address = (TextView) findViewById(R.id.favorite_add_address);
        Rl_alert = (RelativeLayout) findViewById(R.id.favorite_add_alert_layout);
        Tv_alert = (TextView) findViewById(R.id.favorite_add_alert_textView);
        currentLocation_image = (ImageView) findViewById(R.id.favorite_add_current_location_imageview);

        InputFilter[] filters = {new InputFilter.LengthFilter(30), new EmojiExcludeFilter()};
        Et_name.setFilters(filters);

       /* int maxLength = 30;
        Et_name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});*/

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        Intent intent = getIntent();
        SselectedAddress = intent.getStringExtra("Intent_Address");
        Slatitude = intent.getStringExtra("Intent_Latitude");
        Slongitude = intent.getStringExtra("Intent_Longitude");
        SidentityKey = intent.getStringExtra("Intent_IdentityKey");
        if (SidentityKey.equalsIgnoreCase("Edit")) {
            Stitle = intent.getStringExtra("Intent_Title");
            SlocationKey = intent.getStringExtra("Intent_LocationKey");
            isLocationType = true;

            Et_name.setText(Stitle);
            Tv_address.setText(SselectedAddress);
        }
    }

    private void initializeMap() {
        if (googleMap == null) {


            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.favorite_add_mapview));
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

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        FavoriteAdd.this, R.raw.mapstyle));

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

    // Move the camera to last position with a zoom level
    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(parseDouble(Slatitude), parseDouble(Slongitude))).zoom(17).build();
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (CheckPlayService()) {
            if(googleMap!=null) {
                googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String tittle = marker.getTitle();
//                        Log.e("tittle--on_camera_change---->", "" + tittle);
                        return true;

                    }
                });
            }
        } else {
            Alert(getResources().getString(R.string.alert_label_title), "Install Google Play service To View Location !!!");
        }


}
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FavoriteAdd.this);
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

    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(FavoriteAdd.this);
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
                        connectionStatusCode, FavoriteAdd.this, REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(FavoriteAdd.this, "incompatible version of Google Play Services", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //-------------AsyncTask to get Complete Address------------
    public class GetCompleteAddressAsyncTask extends AsyncTask<Void, Void, String> {
        String strAdd = "";

        @Override
        protected void onPreExecute() {
            if (!isLocationType) {
            Tv_address.setText(getResources().getString(R.string.favorite_add_label_gettingAddress));
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String address = new GeocoderHelper().fetchCityName(FavoriteAdd.this, Double.parseDouble(Slatitude), Double.parseDouble(Slongitude), callBack);

            /*Geocoder geocoder = new Geocoder(FavoriteAdd.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(Slatitude), Double.parseDouble(Slongitude), 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    isAddressAvailable = true;
                } else {
                    Log.e("My Current loction address", "No Address returned!");
                    isAddressAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("My Current loction address", "Canont get Address!");
                isAddressAvailable = false;
            }*/

            return address;
        }

        @Override
        protected void onPostExecute(String address) {

            /*if (isAddressAvailable) {
                Rl_alert.setVisibility(View.GONE);
                Tv_address.setText(address);
            } else {
                Rl_alert.setVisibility(View.VISIBLE);
                Tv_alert.setText(getResources().getString(R.string.favorite_add_label_no_address));
                Tv_address.setText("");
            }*/
        }
    }


    CallBack callBack = new CallBack() {
        @Override
        public void onComplete(String LocationName) {
            System.out.println("-------------------addreess----------------0" + LocationName);

            if (LocationName != null) {
                if (!isLocationType) {
                    Tv_address.setText(LocationName);
                }
                Rl_alert.setVisibility(View.GONE);
                Rl_save.setVisibility(View.VISIBLE);
                isLocationType = false;
            }else
            {
                Rl_save.setVisibility(View.GONE);
                Rl_alert.setVisibility(View.VISIBLE);
                Tv_alert.setText(getResources().getString(R.string.favorite_add_label_no_address));
                Tv_address.setText("");
                isLocationType = false;
            }
        }

        @Override
        public void onError(String errorMsg) {

        }
    };


    //-----------------------Favourite Save Post Request-----------------
    private void postRequest_FavoriteSave(String Url) {
        dialog = new Dialog(FavoriteAdd.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_saving));


        System.out.println("-------------Favourite Save Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("title", Et_name.getText().toString());
        jsonParams.put("latitude", Slatitude);
        jsonParams.put("longitude", Slongitude);
        jsonParams.put("address", Tv_address.getText().toString());

        mRequest = new ServiceRequest(FavoriteAdd.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Favourite Save Response----------------" + response);
                String Sstatus = "", Smessage = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Et_name.getWindowToken(), 0);

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Intent local = new Intent();
                        local.setAction("com.favoriteList.refresh");
                        sendBroadcast(local);

                        final PkDialog mDialog = new PkDialog(FavoriteAdd.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(Smessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();

                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Smessage);
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


    //-----------------------Favourite List Edit Post Request-----------------
    private void postRequest_FavoriteEdit(String Url) {
        dialog = new Dialog(FavoriteAdd.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_saving));

        System.out.println("-------------Favourite Edit Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("title", Et_name.getText().toString());
        jsonParams.put("latitude", Slatitude);
        jsonParams.put("longitude", Slongitude);
        jsonParams.put("address", Tv_address.getText().toString());
        jsonParams.put("location_key", SlocationKey);
        System.out.println("------------Favourite Edit save jsonParams------------------"+jsonParams);

        editRequest = new ServiceRequest(FavoriteAdd.this);
        editRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Favourite Edit Response----------------" + response);

                String Sstatus = "", Smessage = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Et_name.getWindowToken(), 0);

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Intent local = new Intent();
                        local.setAction("com.favoriteList.refresh");
                        sendBroadcast(local);

                        final PkDialog mDialog = new PkDialog(FavoriteAdd.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(Smessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Smessage);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        System.out.println("--------------onActivityResult requestCode----------------" + requestCode);

        // code to get country name
        if ((requestCode == placeSearch_request_code && resultCode == RESULT_OK && data != null)) {

            // Check if no view has focus:
            CloseKeyBoard();

            Place place = PlaceAutocomplete.getPlace(FavoriteAdd.this, data);

            String SselectedLocation = "";

            if ( String.valueOf(place.getAddress()).contains(String.valueOf(place.getName())) ){
                SselectedLocation = place.getAddress() + "";
            }else{
                SselectedLocation = place.getName()+" "+place.getAddress() + "";
            }

            Slatitude = place.getLatLng().latitude + "";
            Slongitude = place.getLatLng().longitude + "";

                if (!Slatitude.equalsIgnoreCase("") && Slatitude.length() > 0 && !Slongitude.equalsIgnoreCase("") && Slongitude.length() > 0) {
                    // Move the camera to last position with a zoom level
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Slatitude), Double.parseDouble(Slongitude))).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if((SselectedLocation != null) && (!"".equalsIgnoreCase(SselectedLocation.trim()))){
                    Tv_address.setText(SselectedLocation);
                }else
                {
                    Tv_address.setText("");
                }

        }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
//            Log.e(TAG, "Error: Status = " + status.toString());
            CloseKeyBoard();
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            CloseKeyBoard();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void CloseKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }

}
