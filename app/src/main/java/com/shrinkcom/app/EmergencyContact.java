package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
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
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 10/12/2015.
 */
public class EmergencyContact extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private EditText Et_name, Et_phoneNo, Et_emailId;
    private RelativeLayout Rl_save_edit;
    private RelativeLayout Rl_deleteContact;
    private ServiceRequest mRequest;
    Dialog dialog;
    private String UserID = "";
    private TextView tv_code;

    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;

    GPSTracker gps;

    //-----Declaration For Enabling Gps-------
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;

    private ImageView Img_send_notification;


    CountryPicker picker;
    private TextView Tv_save_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contact);
        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(EmergencyContact.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(back.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        Rl_deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EmergencyContact.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    deleteContact_Request(Iconstant.emergencycontact_delete_url);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        Img_send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(EmergencyContact.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    gps = new GPSTracker(EmergencyContact.this);
                    if (gps.canGetLocation() && gps.isgpsenabled()) {
                        MyCurrent_lat = gps.getLatitude();
                        MyCurrent_long = gps.getLongitude();
                        sendContact_Request(Iconstant.emergencycontact_send_message_url);
                    } else {
                        enableGpsService();
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        Rl_save_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-----------------------click-----------------------");


                if (getResources().getString(R.string.fragment_edit).equalsIgnoreCase(Tv_save_edit.getText().toString())){
                    Et_name.setEnabled(true);
                    tv_code.setEnabled(true);
                    Et_phoneNo.setEnabled(true);
                    Et_emailId.setEnabled(true);
                    Tv_save_edit.setText(getResources().getString(R.string.emergencycontact_lable_save_contact_textview));

                }else {
                    if (Et_name.getText().toString().trim().length() == 0) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.emergencycontact_lable_namevalidate_textview));
                    } else if (tv_code.getText().toString().length() == 0) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.emergencycontact_lable_code_validate_textview));
                    } else if (!isValidPhoneNumber(Et_phoneNo.getText().toString())) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.emergencycontact_lable_mobilenovalidate_textview));
                    } else if (!isValidEmail(Et_emailId.getText().toString().trim())) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.emergencycontact_lable_email_validate_textview));
                    } else {
                        cd = new ConnectionDetector(EmergencyContact.this);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            updateContact_Request(Iconstant.emergencycontact_add_url);
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    }
                }
            }
        });


        Et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_name);
                }
                if ((Et_name.getText().toString().trim().length() > 0) && (tv_code.getText().toString().trim().length() > 0)
                        && (Et_phoneNo.getText().toString().trim().length() > 0) && (Et_emailId.getText().toString().trim().length() > 0)) {
                    Rl_save_edit.setVisibility(View.VISIBLE);
                } else {
                    Rl_save_edit.setVisibility(View.GONE);
                }
                return false;
            }
        });

       /* tv_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(tv_code);
                }
                return false;
            }
        });*/

        Et_phoneNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_phoneNo);
                }
                if ((Et_name.getText().toString().trim().length() > 0) && (tv_code.getText().toString().trim().length() > 0)
                        && (Et_phoneNo.getText().toString().trim().length() > 0) && (Et_emailId.getText().toString().trim().length() > 0)) {
                    Rl_save_edit.setVisibility(View.VISIBLE);
                } else {
                    Rl_save_edit.setVisibility(View.GONE);
                }
                return false;
            }
        });


        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    if (view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });


        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                tv_code.setText(dialCode);

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }


                if ((Et_name.getText().toString().trim().length() > 0) && (tv_code.getText().toString().trim().length() > 0)
                        && (Et_phoneNo.getText().toString().trim().length() > 0) && (Et_emailId.getText().toString().trim().length() > 0)) {
                    Rl_save_edit.setVisibility(View.VISIBLE);
                } else {
                    Rl_save_edit.setVisibility(View.GONE);
                }
            }
        });


        Et_emailId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_emailId);
                }
                if ((Et_name.getText().toString().trim().length() > 0) && (tv_code.getText().toString().trim().length() > 0)
                        && (Et_phoneNo.getText().toString().trim().length() > 0) && (Et_emailId.getText().toString().trim().length() > 0)) {
                    Rl_save_edit.setVisibility(View.VISIBLE);
                } else {
                    Rl_save_edit.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void initialize() {
        session = new SessionManager(EmergencyContact.this);
        cd = new ConnectionDetector(EmergencyContact.this);
        isInternetPresent = cd.isConnectingToInternet();
        gps = new GPSTracker(EmergencyContact.this);
        picker = CountryPicker.newInstance("Select Country");
        back = (RelativeLayout) findViewById(R.id.emergency_contact_header_back_layout);
        Et_name = (EditText) findViewById(R.id.emergency_contact_name_editText);
        tv_code = (TextView) findViewById(R.id.emergency_contact_country_code_edittext);
        Et_phoneNo = (EditText) findViewById(R.id.emergency_contact_mobile_edittext);
        Et_emailId = (EditText) findViewById(R.id.emergency_contact_email_editText);
        Rl_save_edit = (RelativeLayout) findViewById(R.id.emergency_contact_save_edit_layout);
        Rl_deleteContact = (RelativeLayout) findViewById(R.id.emergency_contact_delete_contact_layout);
        Img_send_notification = (ImageView) findViewById(R.id.emeregency_contact_notification_icon);
        Tv_save_edit = (TextView) findViewById(R.id.emergency_contact_save_edit_textview);


        Et_name.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_phoneNo.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_emailId.setFilters(new InputFilter[]{new EmojiExcludeFilter()});


        Et_name.addTextChangedListener(EditorWatcher);
        Et_phoneNo.addTextChangedListener(EditorWatcher);
        Et_emailId.addTextChangedListener(EditorWatcher);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);


        mGoogleApiClient = new GoogleApiClient.Builder(EmergencyContact.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        if (isInternetPresent) {
            displayContact_Request(Iconstant.emergencycontact_view_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }


    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if ((Et_name.getText().toString().trim().length() > 0) && (tv_code.getText().toString().trim().length() > 0)
                    && (Et_phoneNo.getText().toString().trim().length() > 0) && (Et_emailId.getText().toString().trim().length() > 0)) {
                Rl_save_edit.setVisibility(View.VISIBLE);
            } else {
                Rl_save_edit.setVisibility(View.GONE);
            }

        }
    };

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(EmergencyContact.this);
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

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //---------code to Check Email Validation------
    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
                            status.startResolutionForResult(EmergencyContact.this, REQUEST_LOCATION);
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
                        Toast.makeText(EmergencyContact.this, "", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    //-----------------------Display Emergency Contact Post Request-----------------
    private void displayContact_Request(String Url) {
        dialog = new Dialog(EmergencyContact.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));

        System.out.println("-------------displayContact_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------displayContact_Request Response----------------" + response);

                String Sstatus = "", Smessage = "", Sname = "", Smobilnumber = "", Semail = "", Scountry_code = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject jobject = object.getJSONObject("emergency_contact");
                        Sname = jobject.getString("name");
                        Smobilnumber = jobject.getString("mobile");
                        Semail = jobject.getString("email");
                        Scountry_code = jobject.getString("code");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    Et_name.setText(Sname);
                    Et_emailId.setText(Semail);
                    tv_code.setText(Scountry_code);
                    Et_phoneNo.setText(Smobilnumber);

                    if (Sname.length() == 0) {
                        Rl_deleteContact.setVisibility(View.INVISIBLE);
                        Img_send_notification.setVisibility(View.INVISIBLE);

                    } else {
                        Rl_deleteContact.setVisibility(View.VISIBLE);
                        Img_send_notification.setVisibility(View.VISIBLE);
                        Rl_save_edit.setVisibility(View.VISIBLE);
                        Et_name.setEnabled(false);
                        tv_code.setEnabled(false);
                        Et_phoneNo.setEnabled(false);
                        Et_emailId.setEnabled(false);
                        Tv_save_edit.setText(getResources().getString(R.string.fragment_edit));
                    }
                } else {
                    Rl_deleteContact.setVisibility(View.INVISIBLE);
                    Rl_save_edit.setVisibility(View.GONE);

                    Et_name.setEnabled(true);
                    tv_code.setEnabled(true);
                    Et_phoneNo.setEnabled(true);
                    Et_emailId.setEnabled(true);
                    Tv_save_edit.setText(getResources().getString(R.string.emergencycontact_lable_save_contact_textview));
//                    Alert(getResources().getString(R.string.action_error), Smessage);
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    //-----------------------Update Emergency Contact Post Request-----------------
    private void updateContact_Request(String Url) {
        dialog = new Dialog(EmergencyContact.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_updating));

        System.out.println("-------------updateContact Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("em_name", Et_name.getText().toString());
        jsonParams.put("em_email", Et_emailId.getText().toString().trim());
        jsonParams.put("em_mobile_code", tv_code.getText().toString());
        jsonParams.put("em_mobile", Et_phoneNo.getText().toString());

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------updateContact Response----------------" + response);
                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    Rl_deleteContact.setVisibility(View.VISIBLE);
                    Img_send_notification.setVisibility(View.VISIBLE);

                    Et_name.setEnabled(false);
                    tv_code.setEnabled(false);
                    Et_phoneNo.setEnabled(false);
                    Et_emailId.setEnabled(false);
                    Tv_save_edit.setText(getResources().getString(R.string.fragment_edit));

                    Alert(getResources().getString(R.string.action_success), getResources().getString(R.string.emergencycontact_lable_saved_emergencycontacts));
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    //-----------------------Delete Emergency Contact Post Request-----------------
    private void deleteContact_Request(String Url) {
        dialog = new Dialog(EmergencyContact.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_deleting));

        System.out.println("-------------deleteContact Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------deleteContact Response----------------" + response);
                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {

                    Et_name.setEnabled(true);
                    tv_code.setEnabled(true);
                    Et_phoneNo.setEnabled(true);
                    Et_emailId.setEnabled(true);
                    Tv_save_edit.setText(getResources().getString(R.string.emergencycontact_lable_save_contact_textview));

                    Et_name.setText("");
                    Et_emailId.setText("");
                    tv_code.setText("");
                    Et_phoneNo.setText("");
                    Rl_deleteContact.setVisibility(View.INVISIBLE);
                    Img_send_notification.setVisibility(View.INVISIBLE);



                    Alert(getResources().getString(R.string.action_success), getResources().getString(R.string.emergencycontact_lable_deletesuccess_textview));
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    //-----------------------Delete Emergency Contact Post Request-----------------
    private void sendContact_Request(String Url) {
        dialog = new Dialog(EmergencyContact.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------sendContact Url----------------" + Url);

        System.out.println("-------------sendContact user_id----------------" + UserID);
        System.out.println("-------------sendContact MyCurrent_lat----------------" + MyCurrent_lat);
        System.out.println("-------------sendContact MyCurrent_long----------------" + MyCurrent_long);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("latitude", String.valueOf(MyCurrent_lat));
        jsonParams.put("longitude", String.valueOf(MyCurrent_long));

        mRequest = new ServiceRequest(EmergencyContact.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------sendContact Response----------------" + response);

                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    Alert(getResources().getString(R.string.action_success), Smessage);
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    //-----------------Move Back on pressed phone back button-------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(back.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }
}
