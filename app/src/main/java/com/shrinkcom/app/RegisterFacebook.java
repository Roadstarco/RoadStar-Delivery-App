package com.shrinkcom.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CountryDialCode;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.dialog.PkDialogWithoutButton;
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

import me.drakeet.materialdialog.MaterialDialog;


public class RegisterFacebook extends FragmentActivity {
    private String email="",profile_image="",username1="",userid="",media="";
    private SessionManager session;

    private RelativeLayout back;
    private EditText Eusername, Epassword, Eemail, EphoneNo, Ereferalcode,Econfirm_pss;
    private Button submit;
    private ImageView help;
    private TextInputLayout Rl_countryCode;
    private TextView Tv_countryCode;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;

    private ServiceRequest mRequest;
    Dialog dialog;
    Handler mHandler;
    //------------------GCM Initialization------------------
    private GoogleCloudMessaging gcm;
    private String GCM_Id = "";

    CountryPicker picker;
    private GPSTracker gpsTracker;
    GPSTracker gps;
    private String user_id="";
    PkDialogWithoutButton mInfoDialog;
    AppInfoSessionManager appInfo_Session;
    String sPendingRideId = "", sRatingStatus = "", sCategoryImage = "", sOngoingRide = "", sOngoingRideId = "";
    private boolean isAppInfoAvailable = false;
    private String userID = "", sLatitude = "", sLongitude = "";
    private TextView tv_privacy_policy,tv_terms_and_conditions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_register_new);
        context = getApplicationContext();
        initialize();

       /* help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Referral_information();
            }
        });*/

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                logoutFromFacebook();

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        Rl_countryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        Tv_countryCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Tv_countryCode.setText(dialCode);

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_countryCode.getWindowToken(), 0);
            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Epassword.setText(Epassword.getText().toString().trim());

                if (!isValidEmail(Eemail.getText().toString().replace(" ", "").trim())) {
                    erroredit(Eemail, getResources().getString(R.string.register_label_alert_email));
                } else if (!isValidPassword(Epassword.getText().toString())) {
                    erroredit(Epassword, getResources().getString(R.string.register_label_alert_password));
                } else if (Eusername.getText().toString().length() == 0) {
                    erroredit(Eusername, getResources().getString(R.string.register_label_alert_username));
                } else if (!isValidPhoneNumber(EphoneNo.getText().toString())) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_phoneNo));
                } else if (Tv_countryCode.getText().toString().equalsIgnoreCase("code")) {
                    erroredit(EphoneNo, getResources().getString(R.string.register_label_alert_country_code));
                }
                else if (!Epassword.getText().toString().equals(Econfirm_pss.getText().toString())) {
                    erroredit(Econfirm_pss, getResources().getString(R.string.register_label_alert_pass_not_match));
                }

                else {

                    cd = new ConnectionDetector(RegisterFacebook.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {

                        mHandler.post(dialogRunnable);

                        //---------Getting GCM Id----------
                        GCMInitializer initializer = new GCMInitializer(RegisterFacebook.this, new GCMInitializer.CallBack() {
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


        EphoneNo.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(EphoneNo);
                }
                return false;
            }
        });
        Ereferalcode.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Ereferalcode);
                }
                return false;
            }
        });
      /*  tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);



            }
        });*/


    }

    private void initialize() {
        session = new SessionManager(RegisterFacebook.this);
        cd = new ConnectionDetector(RegisterFacebook.this);
        isInternetPresent = cd.isConnectingToInternet();
        mHandler = new Handler();
        picker = CountryPicker.newInstance("Select Country");
        appInfo_Session = new AppInfoSessionManager(RegisterFacebook.this);
        gps = new GPSTracker(getApplicationContext());
        back = (RelativeLayout) findViewById(R.id.facebook_register_header_back_layout);
        Eusername = (EditText) findViewById(R.id.facebook_register_username_editText);
        Epassword = (EditText) findViewById(R.id.facebook_register_password_editText);
        Eemail = (EditText) findViewById(R.id.facebook_register_email_editText);
        EphoneNo = (EditText) findViewById(R.id.facebook_register_phone_editText);
        Ereferalcode = (EditText) findViewById(R.id.facebook_register_referalcode_editText);
        Econfirm_pss = (EditText) findViewById(R.id.register_con_password_editText);
     //   help = (ImageView) findViewById(R.id.facebook_register_referalcode_help_image);
        Rl_countryCode = (TextInputLayout) findViewById(R.id.signup_input_layout_code);
        Tv_countryCode = (TextView) findViewById(R.id.facebook_register_country_code_textview);
//        tv_privacy_policy = (TextView) findViewById(R.id.policy);
        tv_terms_and_conditions = (TextView) findViewById(R.id.terms);
        submit = (Button) findViewById(R.id.facebook_register_submit_button);
        submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        String first_text="<font color='#5b5b5b'>"+getResources().getString(R.string.login_label_privacypolicy)+" "+"</font>";
        String second_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Terms_of_service)+" "+"</font>";
        String tired_text="<font color='#5b5b5b'>"+getResources().getString(R.string.login_label_and)+" "+"</font>";
        String fourth_text="<font color='#e84c3d'>"+getResources().getString(R.string.login_label_Privacy_Policy)+"</font>";
        tv_terms_and_conditions.setText(Html.fromHtml(first_text+second_text+tired_text+fourth_text));

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
            }
        };

        makeLinks(tv_terms_and_conditions, new String[] { getResources().getString(R.string.login_label_Terms_of_service), getResources().getString(R.string.login_label_Privacy_Policy) }, new ClickableSpan[] {
                termsOfServicesClick, privacyPolicyClick
        });

        Eusername.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Epassword.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Eemail.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        EphoneNo.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Ereferalcode.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Econfirm_pss.setFilters(new InputFilter[]{new EmojiExcludeFilter()});





        Intent intent = getIntent();
        userid = intent.getStringExtra("userId");
        username1 = intent.getStringExtra("userName");
        email = intent.getStringExtra("userEmail");
        media = intent.getStringExtra("media");

        if(!username1.equalsIgnoreCase(""))
        {
            Eusername.setText(username1);
        }
        if(!email.equalsIgnoreCase(""))
        {
            Eemail.setText(email);
        }

        //code to make password editText as dot
        Epassword.setTransformationMethod(new PasswordTransformationMethod());
        Econfirm_pss.setTransformationMethod(new PasswordTransformationMethod());
        Eusername.addTextChangedListener(loginEditorWatcher);
        Epassword.addTextChangedListener(loginEditorWatcher);


        gpsTracker = new GPSTracker(RegisterFacebook.this);
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
                        Tv_countryCode.setText(Str_countyCode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);

            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void Referral_information() {
        final MaterialDialog dialog = new MaterialDialog(RegisterFacebook.this);
        View view = LayoutInflater.from(this).inflate(R.layout.register_referalcode_dialog, null);

        TextView tv_ok = (TextView) view.findViewById(R.id.referral_code_popup_text_ok);
        tv_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.setView(view).show();
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void logoutFromFacebook() {
        Util.clearCookies(RegisterFacebook.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RegisterFacebook.this);
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
            if (EphoneNo.getText().length() > 0) {
                EphoneNo.setError(null);
            }

        }
    };


    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(RegisterFacebook.this, R.anim.shake);
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
            /*
       /*      * else if(!pass.matches("(.*[A-Z].*)")) { return false; }
			 *//*
        else if (!pass.matches("(.*[a-z].*)")) {
            return false;
        } else if (!pass.matches("(.*[0-9].*)")) {
            return false;
        }*/
            /*
             * else if(!pass.matches(
			 * "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)")) {
			 * return false; }
			 */
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
            dialog = new Dialog(RegisterFacebook.this);
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

        System.out.println("--------------facebook register url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_name", Eusername.getText().toString());
        jsonParams.put("user_id", userid );
        jsonParams.put("email", Eemail.getText().toString().replace(" ", "").trim());
        jsonParams.put("password", Epassword.getText().toString());
        jsonParams.put("phone_number", EphoneNo.getText().toString());
        jsonParams.put("country_code", Tv_countryCode.getText().toString());
        jsonParams.put("referal_code", Ereferalcode.getText().toString());
        jsonParams.put("gcm_id", GCM_Id);
        jsonParams.put("media_id", media);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);
        System.out.println("--------------facebook register jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(RegisterFacebook.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------facebook register reponse-------------------" + response);

//                String Sstatus = "", Smessage = "", Sotp_status = "", Sotp = "";
                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "",
                        SsecretKey = "", SwalletAmount = "", ScurrencyCode = "", Sotp_status = "", Sotp = "";;
                String sCurrencySymbol="";

                String gcmId="";


                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        /*Suser_image = object.getString("user_image");
                        Suser_id = object.getString("user_id");
                        Suser_name = object.getString("user_name");
                        Semail = object.getString("email");
                        Scountry_code = object.getString("country_code");
                        SphoneNo = object.getString("phone_number");
                        Sreferal_code = object.getString("referal_code");
                        Scategory = object.getString("category");
                        SsecretKey = object.getString("sec_key");
                        SwalletAmount = object.getString("wallet_amount");
                        ScurrencyCode = object.getString("currency");*/
                        Sotp_status = object.getString("otp_status");
                        Sotp = object.getString("otp");
                        //gcmId = object.getString("key");

                     //   sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(context, FacebookOtpPage.class);
                    intent.putExtra("sLatitude", sLatitude);
                    intent.putExtra("sLongitude", sLongitude);
                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("UserName", Eusername.getText().toString());
                    intent.putExtra("Email", Eemail.getText().toString());
                    intent.putExtra("Password", Epassword.getText().toString());
                    intent.putExtra("Phone", EphoneNo.getText().toString());
                    intent.putExtra("CountryCode", Tv_countryCode.getText().toString());
                    intent.putExtra("ReferalCode", Ereferalcode.getText().toString());
                    intent.putExtra("GcmID", GCM_Id);
                    intent.putExtra("MediaId", userid);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoutFromFacebook();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            logoutFromFacebook();

            RegisterFacebook.this.finish();
            RegisterFacebook.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }


    private void postRequest_AppInformation(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", user_id);
        mRequest = new ServiceRequest(RegisterFacebook.this);
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
//                        ChatService.startUserAction(RegisterFacebook.this);

                        Intent intent = new Intent(context, UpdateUserLocation.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);


                    } else {
                        mInfoDialog = new PkDialogWithoutButton(RegisterFacebook.this);
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
                mInfoDialog = new PkDialogWithoutButton(RegisterFacebook.this);
                mInfoDialog.setDialogTitle(getResources().getString(R.string.app_info_header_textView));
                mInfoDialog.setDialogMessage(getResources().getString(R.string.app_info_content));
                mInfoDialog.show();
            }
        });
    }


}
