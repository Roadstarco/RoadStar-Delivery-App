package com.shrinkcom.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.dialog.PkDialogWithoutButton;
import com.mylibrary.facebook.AsyncFacebookRunner;
import com.mylibrary.facebook.DialogError;
import com.mylibrary.facebook.Facebook;
import com.mylibrary.facebook.FacebookError;
import com.mylibrary.facebook.Util;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.pushnotification.GCMInitializer;
import com.mylibrary.volley.ServiceRequest;
//import com.mylibrary.xmpp.XmppService;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class LoginPage extends Activity {
    // Your FaceBook APP ID
    private static String APP_ID = "468945646630814";
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
           /* if (username.getText().length() > 0) {
                username.setError(null);
            }*/
            if (password.getText().length() > 0) {
                password.setError(null);
            }
        }
    };

    RelativeLayout facebooklayout;
    AsyncFacebookRunner mAsyncRunner;
    GPSTracker gps;
    PkDialogWithoutButton mInfoDialog;
    AppInfoSessionManager appInfo_Session;
    String sPendingRideId = "", sRatingStatus = "", sCategoryImage = "", sOngoingRide = "", sOngoingRideId = "";
    private RelativeLayout back;
    private TextView forgotPwd, register;
    private EditText username, password, login_phone_number_editText, login_country_code_editText;
    private TextView submit;
    private TextView Tv_or, tv_privacy_policy, tv_terms_and_conditions;
    // Instance of FaceBook Class
    private Facebook facebook;
    private SharedPreferences mPrefs;
    private String email = "", profile_image = "", username1 = "", userid = "", Language_code = "";
    private JsonObjectRequest jsonObjReq;
    private StringRequest postrequest;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    private ServiceRequest mRequest;
    private Dialog dialog;
    //--------Handler Method------------
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(LoginPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    };
    //--------Handler Method------------
    Runnable dialogFacebookRunnable = new Runnable() {
        @Override
        public void run() {
            dialog = new Dialog(LoginPage.this);
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_loading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
            dialog_title.setText(getResources().getString(R.string.action_loading));
        }
    };
    private SessionManager session;
    private AppInfoSessionManager appinfo_session;
    private String Str_Hash = "";
    private Handler mHandler;
    private String sCurrencySymbol = "";
    private String android_id;
    private String GCM_Id = "";
    private String sMediaId = "";
    private String Str_FacebookId = "";
    private String user_id = "", sLatitude = "", sLongitude = "";
    private boolean isAppInfoAvailable = false;

    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //method to convert currency code to currency symbol
    private static Locale getLocale(String strCode) {
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode.equals(code)) {
                return locale;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_new);
        context = getApplicationContext();
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        initialize();

        forgotPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, ForgotPassword.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText(username.getText().toString().trim());
                password.setText(password.getText().toString().trim());

                if (username.getText().toString().length() == 0) {
                    erroredit(username, getResources().getString(R.string.login_label_alert_username));
                } else if (!isValidEmail(username.getText().toString().replace(" ", "").trim())) {
                    erroredit(username, getResources().getString(R.string.login_label_alert_email_invalid));
                } else if (password.getText().toString().length() == 0) {
                    erroredit(password, getResources().getString(R.string.login_label_alert_password));
                }

                else {
                    cd = new ConnectionDetector(LoginPage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (gps.isgpsenabled() && gps.canGetLocation()) {
                        if (isInternetPresent) {
                            mHandler.post(dialogRunnable);
                            //---------Getting GCM Id----------
                            GCMInitializer initializer = new GCMInitializer(LoginPage.this, new GCMInitializer.CallBack() {
                                @Override
                                public void onRegisterComplete(String registrationId) {
                                    GCM_Id = registrationId;

                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());


                                    PostRequest(Iconstant.loginurl);

                                }

                                @Override
                                public void onError(String errorMsg) {

                                    sLatitude = String.valueOf(gps.getLatitude());
                                    sLongitude = String.valueOf(gps.getLongitude());

                                    PostRequest(Iconstant.loginurl);

                                }
                            });
                            initializer.init();

                        } else {
                            Alert(getResources().getString(R.string.alert_nointernet), getResources().getString(R.string.alert_nointernet_message));
                        }
                    } else {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.alert_gpsEnable));
                    }
                }
            }
        });


        username.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(username);
                }
                return false;
            }
        });


        password.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(password);
                }
                return false;
            }
        });


        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });



    }

    private void initialize() {
        session = new SessionManager(LoginPage.this);
        appinfo_session = new AppInfoSessionManager(LoginPage.this);
        cd = new ConnectionDetector(LoginPage.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        appInfo_Session = new AppInfoSessionManager(LoginPage.this);
        HashMap<String, String> user = appinfo_session.getAppInfo();
        Str_FacebookId = user.get(AppInfoSessionManager.KEY_FACEBOOK_ID);
        facebook = new Facebook(Str_FacebookId);


        gps = new GPSTracker(getApplicationContext());


        Intent i = getIntent();
        Str_Hash = i.getStringExtra("HashKey");

        System.out.println("Str_FacebookId------jai------" + Str_FacebookId);


        username = (EditText) findViewById(R.id.edt_email);
        password = (EditText) findViewById(R.id.edt_pass);
        forgotPwd = (TextView) findViewById(R.id.tv_forgtpass);
        submit = (TextView) findViewById(R.id.btn_signin);
        tv_terms_and_conditions = (TextView) findViewById(R.id.tv_googlelogin);// login with google
        register = (TextView) findViewById(R.id.tv_signup);





        username.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        //code to make password editText as dot
        password.setTransformationMethod(new PasswordTransformationMethod());

        username.addTextChangedListener(loginEditorWatcher);
        password.addTextChangedListener(loginEditorWatcher);
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(LoginPage.this);
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

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(LoginPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }


    // -------------------------code for Login Post Request----------------------------------

    private void PostRequest(final String Url) {

        System.out.println("-------GCM_Id-------" + GCM_Id);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", username.getText().toString().replace(" ", "").trim());
//        jsonParams.put("phone_number", login_phone_number_editText.getText().toString().replace(" ", "").trim());
//        jsonParams.put("country_code", login_country_code_editText.getText().toString().replace(" ", "").trim());
        jsonParams.put("password", password.getText().toString());
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);

        System.out.println("----------------Login jsonParams-------------------" + jsonParams);


        mRequest = new ServiceRequest(LoginPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("login", response);

                System.out.println("----------------Login reponse-------------------" + response);
                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "", SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";

                String is_alive_other = "";

                String gcmId = "";


                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
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
                        ScurrencyCode = object.getString("currency");
                        is_alive_other = object.getString("is_alive_other");
                        // Language_code ="es";
                        //
                        Language_code = object.getString("lang_code");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (Sstatus.equalsIgnoreCase("1")) {


                    Locale locale = null;

                    switch (Language_code) {

                        case "en":
                            locale = new Locale("en");
                            session.setlamguage("en", "en");
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
                            session.setlamguage("es", "es");
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
                            session.setlamguage("ta", "ta");
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
                            session.setlamguage("en", "en");
                            break;
                    }

                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());


                    session.createLoginSession(Semail, Suser_id, Suser_name, Suser_image, Scountry_code, SphoneNo, Sreferal_code, Scategory, gcmId);
                    session.createWalletAmount(sCurrencySymbol + SwalletAmount);
                    session.setXmppKey(Suser_id, SsecretKey);

                    System.out.println("insidesession gcm--------------" + gcmId);

                    if (is_alive_other.equalsIgnoreCase("Yes")) {

                        final PkDialog mDialog = new PkDialog(LoginPage.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.app_name));
                        mDialog.setDialogMessage(getResources().getString(R.string.alert_multiple_login));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                //  postRequest_AppInformation(Iconstant.app_info_url);
                                //   ChatService.startUserAction(LoginPage.this);

/*
                                if (!isMyServiceRunning(XmppService.class)) {
                                    System.out.println("-----------OtpPage xmpp service start---------");
                                    startService(new Intent(LoginPage.this, XmppService.class));
                                }
*/
                                SingUpAndSignIn.activty.finish();
                                Intent intent = new Intent(context, UpdateUserLocation.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();

                    } else {
                        //   postRequest_AppInformation(Iconstant.app_info_url);
                        //   ChatService.startUserAction(LoginPage.this);
/*
                        if (!isMyServiceRunning(XmppService.class)) {
                            System.out.println("-----------OtpPage xmpp service start---------");
                            startService(new Intent(LoginPage.this, XmppService.class));
                        }
*/

                       /* if (!isMyServiceRunning(GEOService.class)) {
                            Intent serviceIntent = new Intent(LoginPage.this, GEOService.class);
                            startService(serviceIntent);
                        }*/
                        SingUpAndSignIn.activty.finish();
                        Intent intent = new Intent(context, UpdateUserLocation.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }

                  /*  //starting XMPP service
                    if("No".equalsIgnoreCase(is_alive_other)){
                        ChatService.startUserAction(LoginPage.this);
                        SingUpAndSignIn.activty.finish();
                        Intent intent = new Intent(context, UpdateUserLocation.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                    }else{
                        Alert(getResources().getString(R.string.alert_multiple_login), Smessage);
                    }*/

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_signIn_failed), Smessage);
                }
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);

                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onErrorListener() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }


    //-----------------------------------------------------------------

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


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            return true;
        }
        return false;
    }


    //--------------------------------code for faceBook------------------------------

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
            facebook.authorize(LoginPage.this,
                    new String[]{"email", "publish_actions"},
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
                            GCMInitializer initializer = new GCMInitializer(LoginPage.this, new GCMInitializer.CallBack() {
                                @Override
                                public void onRegisterComplete(String registrationId) {
                                    GCM_Id = registrationId;
                                    //getProfileInformation();

                                    String accessToken1 = facebook.getAccessToken();
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

    public void getProfileInformation() {
        mAsyncRunner.request("me", new AsyncFacebookRunner.RequestListener() {
            @Override
            public void onComplete(String response, Object state) {
                String json = response;
                try {

                    System.out.println("----------facebook response----------------" + response);

                    // Facebook Profile JSON data
                    JSONObject profile = new JSONObject(json);
                    sMediaId = profile.getString("id");
                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PostRequest_facebook(Iconstant.social_check_url);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOException(IOException e, Object state) {
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                                                Object state) {
            }

            @Override
            public void onMalformedURLException(MalformedURLException e,
                                                Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    public void logoutFromFacebook() {
        Util.clearCookies(LoginPage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    private void PostRequest_facebook(final String Url) {


        final ProgressDialog progress;
        progress = new ProgressDialog(LoginPage.this);
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
        jsonParams.put("lat", sLatitude);
        jsonParams.put("lon", sLongitude);
        mRequest = new ServiceRequest(LoginPage.this);
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

                    SingUpAndSignIn.activty.finish();
                    Intent intent = new Intent(context, UpdateUserLocation.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (Sstatus.equalsIgnoreCase("2")) {

                    Intent intent = new Intent(LoginPage.this, RegisterFacebook.class);
                    intent.putExtra("userId", userid);
                    intent.putExtra("userName", username1);
                    intent.putExtra("userEmail", email);
                    intent.putExtra("media", sMediaId);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);

                } else {
                    Alert(getResources().getString(R.string.login_label_alert_signIn_failed), Smessage);
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);
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

    private void JsonRequest(final String Url) {

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        mRequest = new ServiceRequest(LoginPage.this);
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


    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        } else {
            return true;

        }
    }

    private void postRequest_AppInformation(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", user_id);
        mRequest = new ServiceRequest(LoginPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------appinfo----------------" + response);
                String Str_status = "", sContact_mail = "", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
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
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                               /* server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
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
                        appInfo_Session.setAppInfo(sContact_mail, sCustomerServiceNumber, sSiteUrl, sXmppHostUrl, sHostName, sFacebookId, sGooglePlusId, sCategoryImage, sOngoingRide, sOngoingRideId, sPendingRideId, sRatingStatus);
                        //  postRequest_AppInformation(Iconstant.app_info_url);
                        //     ChatService.startUserAction(LoginPage.this);

                        Intent intent = new Intent(context, UpdateUserLocation.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                    } else {
                        mInfoDialog = new PkDialogWithoutButton(LoginPage.this);
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
                mInfoDialog = new PkDialogWithoutButton(LoginPage.this);
                mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
                mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
                mInfoDialog.show();
            }
        });
    }


}
