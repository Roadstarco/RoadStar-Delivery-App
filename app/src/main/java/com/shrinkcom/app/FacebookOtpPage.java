package com.shrinkcom.app;

/**
 * Created by  on 10/1/2015.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;
//import com.mylibrary.xmpp.XmppService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;



public class FacebookOtpPage extends Activity {
    private Context context;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;

    private RelativeLayout back;
    private EditText Eotp;
    private Button send;

    private ServiceRequest mRequest;
    Dialog dialog;

    private String Susername = "", Semail = "", Spassword = "", Sphone = "", ScountryCode = "", SreferalCode = "", SgcmId = "";
    private String Sotp_Status = "", Sotp = "",media_id="",sLongitude,sLatitude;
    private RelativeLayout Rl_resendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_page_new);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Eotp.getText().toString().length() == 0) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_otp));
                } else if (!Sotp.equals(Eotp.getText().toString())) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_invalid));
                } else {
                    cd = new ConnectionDetector(FacebookOtpPage.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        PostRequest(Iconstant.facebook_register_url);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });


        Eotp.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(Eotp.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        Rl_resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent) {
                    PostRequest_ResendCode(Iconstant.otp_resend_url);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }

            }
        });
    }

    private void initialize() {
        session = new SessionManager(FacebookOtpPage.this);
        cd = new ConnectionDetector(FacebookOtpPage.this);
        isInternetPresent = cd.isConnectingToInternet();


        back = (RelativeLayout) findViewById(R.id.forgot_password_otp_header_back_layout);
        Eotp = (EditText) findViewById(R.id.forgot_password_otp_password_editText);
        send = (Button) findViewById(R.id.forgot_password_otp_submit_button);
        Rl_resendCode = (RelativeLayout) findViewById(R.id.otp_page_resend_code_layout);

        Eotp.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Eotp.addTextChangedListener(EditorWatcher);

        Intent intent = getIntent();
        Susername = intent.getStringExtra("UserName");
        Semail = intent.getStringExtra("Email");
        Spassword = intent.getStringExtra("Password");
        Sphone = intent.getStringExtra("Phone");
        ScountryCode = intent.getStringExtra("CountryCode");
        SreferalCode = intent.getStringExtra("ReferalCode");
        SgcmId = intent.getStringExtra("GcmID");
        Sotp_Status = intent.getStringExtra("Otp_Status");
        Sotp = intent.getStringExtra("Otp");
        media_id = intent.getStringExtra("MediaId");
        sLongitude = intent.getStringExtra("sLongitude");
        sLatitude = intent.getStringExtra("sLatitude");
        if (Sotp_Status.equalsIgnoreCase("development")) {
            Eotp.setText(Sotp);
        } else {
            Eotp.setText("");
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FacebookOtpPage.this);
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
    private final TextWatcher EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Eotp.getText().length() > 0) {
                Eotp.setError(null);
            }
        }
    };

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(FacebookOtpPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
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

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            FacebookOtpPage.this.finish();
            FacebookOtpPage.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    // -------------------------code for resend otp code Post Request----------------------------------

    private void PostRequest_ResendCode(String Url) {

        dialog = new Dialog(FacebookOtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("--------------Otp resend url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("type", "register");
        jsonParams.put("mobile_number", Sphone);
        jsonParams.put("dail_code", ScountryCode);

        System.out.println("--------------Otp resend---------jsonParams----------" + jsonParams);



        mRequest = new ServiceRequest(FacebookOtpPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Otp resend reponse-------------------" + response);


                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "",
                        SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";
                String sCurrencySymbol="";

                String gcmId = "";

                try {
                    JSONObject object = new JSONObject(response);

                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {

                        Sotp = object.getString("otp");
                        Sotp_Status = object.getString("otp_status");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    if (Sotp_Status.equalsIgnoreCase("development")) {
                        Eotp.setText(Sotp);
                    } else {
                        Eotp.setText("");
                    }

                    Toast.makeText(FacebookOtpPage.this,Smessage,Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(FacebookOtpPage.this,Smessage,Toast.LENGTH_SHORT).show();
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eotp.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }
    // -------------------------code for Login Post Request----------------------------------

    private void PostRequest(String Url) {

        dialog = new Dialog(FacebookOtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("--------------facebook Otp url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();





        jsonParams.put("user_name", Susername);
        jsonParams.put("email", Semail);
        jsonParams.put("password", Spassword);
        jsonParams.put("phone_number", Sphone);
        jsonParams.put("country_code", ScountryCode);
        jsonParams.put("referal_code", SreferalCode);
        jsonParams.put("gcm_id", SgcmId);
        jsonParams.put("media_id", media_id);
        jsonParams.put("latitude", sLatitude);
        jsonParams.put("longitude", sLongitude);
        System.out.println("--------------facebook Otp jsonParams-------------------" + jsonParams);
        mRequest = new ServiceRequest(FacebookOtpPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------facebook Otp reponse-------------------" + response);


                String Sstatus = "", Smessage = "", Suser_image = "", Suser_id = "", Suser_name = "",
                        Semail = "", Scountry_code = "", SphoneNo = "", Sreferal_code = "", Scategory = "",
                        SsecretKey = "", SwalletAmount = "", ScurrencyCode = "";
                String sCurrencySymbol="";


                String gcmId = "";

                String is_alive_other = "";


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
                        ScurrencyCode = object.getString("currency");

                        gcmId = object.getString("key");
                     //   is_alive_other = object.getString("is_alive_other");


                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if (Sstatus.equalsIgnoreCase("1")) {
                    SingUpAndSignIn.activty.finish();

                    session.createLoginSession(Semail, Suser_id, Suser_name, Suser_image, Scountry_code, SphoneNo, Sreferal_code, Scategory,gcmId);
                    session.createWalletAmount(sCurrencySymbol + SwalletAmount);
                    session.setXmppKey(Suser_id, SsecretKey);

                    //starting XMPP service
//                    ChatService.startUserAction(FacebookOtpPage.this);

/*
                    if (!isMyServiceRunning(XmppService.class)) {
                        System.out.println("-----------OtpPage xmpp service start---------");
                        startService(new Intent(FacebookOtpPage.this, XmppService.class));
                    }
*/


                    Intent intent = new Intent(context, UpdateUserLocation.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                } else {

                    final PkDialog mDialog = new PkDialog(FacebookOtpPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_error));
                    mDialog.setDialogMessage(Smessage);
                    mDialog.setCancelOnTouchOutside(false);
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Eotp.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


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

}
