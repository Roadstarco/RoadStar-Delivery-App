package com.shrinkcom.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CountryDialCode;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.countrycodepicker.CountryPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.facebook.AsyncFacebookRunner;
import com.mylibrary.facebook.DialogError;
import com.mylibrary.facebook.Facebook;
import com.mylibrary.facebook.FacebookError;
import com.mylibrary.facebook.Util;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.pushnotification.GCMInitializer;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class RegisterPage extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private EditText Eusername, Epassword, Eemail;
    private TextView submit,tv_signin;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    private ServiceRequest mRequest;
    Dialog dialog;
    Handler mHandler;
    //------------------GCM Initialization------------------
    private GoogleCloudMessaging gcm;
    private String GCM_Id = "";
    GPSTracker gps;
    CountryPicker picker;
    private GPSTracker gpsTracker;

    private TextView Tv_or,Tv_privacy_policy;
    AppInfoSessionManager appInfo_Session;
    private String Str_FacebookId = "";

    private Facebook facebook;
    AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;
    private String sMediaId = "";
    private String email = "", profile_image = "", username1 = "", userid = "", Language_code = "";
    private String sCurrencySymbol = "";
    private SessionManager session;
    final static int REQUEST_LOCATION = 199;
    public static RegisterPage registerPageClass;
    BroadcastReceiver logoutReciver;
    private String userID = "", sLatitude = "", sLongitude = "";
    private TextView tv_privacy_policy,tv_terms_and_conditions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new);
        context = getApplicationContext();
        registerPageClass = RegisterPage.this;


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.register.finish");
        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();

            }
        };
        registerReceiver(logoutReciver, filter);

        mGoogleApiClient = new GoogleApiClient.Builder(RegisterPage.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();


        initialize();

        tv_signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Eemail.getText().toString().trim().length() == 0) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email_register));
                } else if (!isValidEmail(Eemail.getText().toString().replace(" ", ""))) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email));
                }
                else  if (Epassword.getText().toString().length() == 0) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alertpassword_register));
                }else if (!isValidPassword(Epassword.getText().toString())) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alert_password));
                } else if (Eusername.getText().toString().trim().length() == 0) {
                    erroredit(Eusername, getResources().getString(R.string.register_label_alert_username));
                }
                else {

                    cd = new ConnectionDetector(RegisterPage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(RegisterPage.this, new GCMInitializer.CallBack() {
                            @Override
                            public void onRegisterComplete(String registrationId) {

                                GCM_Id = registrationId;

                                if (gps.isgpsenabled() && gps.canGetLocation()) {

                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());
                                    PostRequest(Iconstant.register_url);

                                }
                                else
                                {
                                    Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
                                }



                            }

                            @Override
                            public void onError(String errorMsg) {
                                PostRequest(Iconstant.register_url);
                            }
                        });
                        initializer.init();

                    } else {
                        Alert(getResources().getString(R.string.alert_nointernet), getResources().getString(R.string.alert_nointernet_message));
                    }
                }
            }
        });


        Eusername.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eusername);
                }
                return false;
            }
        });


        Epassword.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Epassword);
                }
                return false;
            }
        });

        Eemail.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Eemail);
                }
                return false;
            }
        });




    }

    private void initialize() {
        cd = new ConnectionDetector(RegisterPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        picker = CountryPicker.newInstance("Select Country");
        session = new SessionManager(RegisterPage.this);

        gps = new GPSTracker(getApplicationContext());




        Eusername = (EditText) findViewById(R.id.edt_username);
        Epassword = (EditText) findViewById(R.id.edt_pass);
        Eemail = (EditText) findViewById(R.id.edt_email);

        submit = (TextView) findViewById(R.id.btn_signup);
        tv_signin= (TextView) findViewById(R.id.tv_signin);

        Eusername.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Epassword.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Eemail.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        //code to make password editText as dot
        Epassword.setTransformationMethod(new PasswordTransformationMethod());


        Eusername.addTextChangedListener(loginEditorWatcher);
        Epassword.addTextChangedListener(loginEditorWatcher);





        if (gps.isgpsenabled() && gps.canGetLocation()) {

            sLatitude = String.valueOf(gps.getLatitude());
            sLongitude = String.valueOf(gps.getLongitude());

        }
        else
        {
            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
        }



        gpsTracker = new GPSTracker(RegisterPage.this);
        if (gpsTracker.canGetLocation() && gpsTracker.isgpsenabled()) {

            double MyCurrent_lat = gpsTracker.getLatitude();
            double MyCurrent_long = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
                if (addresses != null && !addresses.isEmpty()) {

                    String Str_getCountryCode = addresses.get(0).getCountryCode();
                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RegisterPage.this);
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

    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher loginEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Eusername.getText().length() > 0) {
                Eusername.setError(null);
            }
            if (Epassword.getText().length() > 0) {
                Epassword.setError(null);
            }
            if (Eemail.getText().length() > 0) {
                Eemail.setError(null);
            }


        }
    };


    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(RegisterPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }

        else {
            return true;
        }

    }

    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(RegisterPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_verifying));
        }
    };


    // -------------------------code for Login Post Request----------------------------------
    private void PostRequest(String Url) {

        System.out.println("--------------register url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_name", Eusername.getText().toString());
        jsonParams.put("email", Eemail.getText().toString().replace(" ", "").trim());
        jsonParams.put("password", Epassword.getText().toString());
        jsonParams.put("phone_number", "");
        jsonParams.put("country_code", "");
        jsonParams.put("referal_code","");
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);
        System.out.println("--------------register jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("registr", response);

                System.out.println("--------------register reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Sotp_status = "", Sotp = "";

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Sotp_status = object.getString("otp_status");
                        Sotp = object.getString("otp");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(context, OtpPage.class);
                    intent.putExtra("sLatitude", sLatitude);
                    intent.putExtra("sLongitude", sLongitude);

                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("UserName", Eusername.getText().toString());
                    intent.putExtra("Email", Eemail.getText().toString().replace(" ", "").trim());
                    intent.putExtra("Password", Epassword.getText().toString());
                    intent.putExtra("Phone", "");
                    intent.putExtra("CountryCode","");
                    intent.putExtra("ReferalCode", "");
                    intent.putExtra("GcmID", GCM_Id);

                    System.out.println("gcm---------" + GCM_Id);

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_register_failed), Smessage);
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    public void logoutFromFacebook() {
        Util.clearCookies(RegisterPage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    Runnable dialogFacebookRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(RegisterPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_loading));
        }
    };

    public void loginToFacebook() {

        System.out.println("---------------facebook login1-----------------------");
        mPrefs = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }


        System.out.println("---------------facebook expires-----------------------" + expires);

        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        System.out.println("---------------facebook isSessionValid-----------------------" + facebook.isSessionValid());
        if (!facebook.isSessionValid()) {
            facebook.authorize(RegisterPage.this,
                    new String[]{"email"},
                    new Facebook.DialogListener() {

                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires", facebook.getAccessExpires());
                            editor.commit();
                            String accessToken = facebook.getAccessToken();

                            mHandler.post(dialogFacebookRunnable);
                            //---------Getting GCM Id----------
                            GCMInitializer initializer = new GCMInitializer(RegisterPage.this, new GCMInitializer.CallBack() {
                                @Override
                                public void onRegisterComplete(String registrationId) {

                                    GCM_Id = registrationId;

                                    //getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();

                                    System.out.println("----------------------jai----------------------"+"https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);

                                    JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);

                                }

                                @Override
                                public void onError(String errorMsg) {
                                    //getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();
                                    JsonRequest("https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessToken1);
                                }
                            });
                            initializer.init();
                        }

                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error

                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors

                        }
                    });
        }
    }

    private void JsonRequest(final String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------access token reponse-------------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    System.out.println("---------facebook profile------------" + response);


                    sMediaId = object.getString("id");
                    userid = object.getString("id");
                    profile_image = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                    username1 = object.getString("name");
                    username1 = username1.replaceAll("\\s+", "");


                    if (object.has("email")) {
                        email = object.getString("email");
                    } else {
                        email = "";
                    }
                    System.out.println("-------sMediaId------------------" + sMediaId);
                    System.out.println("-------email------------------" + email);
                    System.out.println("-----------------userid-------------------------------" + userid);
                    System.out.println("----------------profile_image-----------------" + profile_image);
                    System.out.println("-----------username----------" + username1);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //post execute
                dialog.dismiss();

                PostRequest_facebook(Iconstant.social_check_url);
            }

            @Override
            public void onErrorListener() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void PostRequest_facebook(final String Url) {


        final ProgressDialog progress;
        progress = new ProgressDialog(RegisterPage.this);
        progress.setMessage(getResources().getString(R.string.action_pleasewait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.show();

        System.out.println("-----------media_id 1------------" + sMediaId);
        System.out.println("-----------deviceToken 1------------" + "");
        System.out.println("-----------gcm_id 1------------" + GCM_Id);
        System.out.println("-----------email 1------------" + email);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("media_id", sMediaId);
        jsonParams.put("deviceToken", "");
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("email", email);

        mRequest = new ServiceRequest(RegisterPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Login reponse-------------------" + response);

                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "", SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";

                String gcmId = "";
                String is_alive_other = "";

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
                    System.out.println("---------Sstatus--------" + Sstatus);
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Suser_image = object.getString("user_image");
                        Suser_id = object.getString("user_id");
                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");
                        Sreferal_code = object.getString("referal_code");
                        Scategory = object.getString("category");
                        SsecretKey = object.getString("sec_key");
                        SwalletAmount = object.getString("wallet_amount");

                        gcmId = object.getString("key");
                        is_alive_other = object.getString("is_alive_other");

                        ScurrencyCode = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (Sstatus.equalsIgnoreCase("1")) {
                    session.createLoginSession(Semail, Suser_id, Suser_name, Suser_image, Scountry_code, SphoneNo, Sreferal_code, Scategory, gcmId);
                    session.createWalletAmount(sCurrencySymbol + SwalletAmount);
                    session.setXmppKey(Suser_id, SsecretKey);

                    //starting XMPP service
                    //     postRequest_AppInformation(Iconstant.app_info_url);
                    //     ChatService.startUserAction(LoginPage.this);
                    SingUpAndSignIn.activty.finish();
                    Intent intent = new Intent(context, UpdateUserLocation.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (Sstatus.equalsIgnoreCase("2")) {

                    Intent intent = new Intent(RegisterPage.this, RegisterFacebook.class);
                    intent.putExtra("userId", userid);
                    intent.putExtra("userName", username1);
                    intent.putExtra("userEmail", email);
                    intent.putExtra("media", sMediaId);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_signIn_failed), Smessage);
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eusername.getWindowToken(), 0);
                progress.dismiss();
            }

            @Override
            public void onErrorListener() {
                if (progress != null) {
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("--------------jai---------------"+requestCode+resultCode+data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(logoutReciver);
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            // close keyboard
            return true;
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
