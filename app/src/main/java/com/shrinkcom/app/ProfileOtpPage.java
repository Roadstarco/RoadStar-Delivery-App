package com.shrinkcom.app;

import android.app.Activity;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 10/8/2015.
 */
public class ProfileOtpPage extends Activity {
    private Context context;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;

    private RelativeLayout back;
    private EditText Eotp;
    private Button send;

    private ServiceRequest mRequest;
    Dialog dialog;

    private String Suserid = "", Sphone = "", ScountryCode = "";
    private String Sotp_Status = "", Sotp = "";
    private RelativeLayout Rl_resendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_otp_page);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Eotp.getText().toString().length() == 0) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_otp));
                } else if (!Sotp.equals(Eotp.getText().toString())) {
                    erroredit(Eotp, getResources().getString(R.string.otp_label_alert_invalid));
                } else {
                    if (isInternetPresent) {
                        PostRequest(Iconstant.profile_edit_mobileNo_url);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });


        Eotp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        session = new SessionManager(ProfileOtpPage.this);
        cd = new ConnectionDetector(ProfileOtpPage.this);
        isInternetPresent = cd.isConnectingToInternet();

        back = (RelativeLayout) findViewById(R.id.profile_otp_header_back_layout);
        Eotp = (EditText) findViewById(R.id.profile_otp_password_editText);
        send = (Button) findViewById(R.id.profile_otp_submit_button);

        Rl_resendCode = (RelativeLayout)findViewById(R.id.profilepage_resend_code_layout);

        Eotp.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Eotp.addTextChangedListener(EditorWatcher);

        Intent intent = getIntent();
        Suserid = intent.getStringExtra("UserID");
        Sphone = intent.getStringExtra("Phone");
        ScountryCode = intent.getStringExtra("CountryCode");
        Sotp_Status = intent.getStringExtra("Otp_Status");
        Sotp = intent.getStringExtra("Otp");

        if (Sotp_Status.equalsIgnoreCase("development")) {
            Eotp.setText(Sotp);
        } else {
            Eotp.setText("");
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
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
        Animation shake = AnimationUtils.loadAnimation(ProfileOtpPage.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

    // -------------------------code for resend otp code Post Request----------------------------------

    private void PostRequest_ResendCode(String Url) {

        dialog = new Dialog(ProfileOtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("--------------Otp resend url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("type", "change");
        jsonParams.put("user_id",Suserid);
        jsonParams.put("mobile_number", Sphone);
        jsonParams.put("dail_code", ScountryCode);

        System.out.println("--------------Otp resend---------jsonParams----------" + jsonParams);



        mRequest = new ServiceRequest(ProfileOtpPage.this);
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
                        Sphone = object.getString("phone_number");
                        ScountryCode = object.getString("country_code");
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

                    Toast.makeText(ProfileOtpPage.this,Smessage,Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ProfileOtpPage.this,Smessage,Toast.LENGTH_SHORT).show();
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

    // -----------------code for OTP Verification Post Request----------------
    private void PostRequest(String Url) {

        dialog = new Dialog(ProfileOtpPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("--------------Otp url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Suserid);
        jsonParams.put("country_code", ScountryCode);
        jsonParams.put("phone_number", Sphone);
        jsonParams.put("otp", Sotp);

        mRequest = new ServiceRequest(ProfileOtpPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Otp reponse-------------------" + response);
                String Sstatus = "", Smessage = "", Scountry_code = "", Sphone_number = "";
                try {

                    JSONObject object = new JSONObject(response);

                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        Scountry_code = object.getString("country_code");
                        Sphone_number = object.getString("phone_number");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    ProfilePage.updateMobileDialog(Scountry_code, Sphone_number);
                    session.setMobileNumberUpdate(Scountry_code, Sphone_number);

                    final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                    mDialog.setDialogMessage(getResources().getString(R.string.profile_lable_mobile_success));
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        }
                    });
                    mDialog.show();

                } else {

                    final PkDialog mDialog = new PkDialog(ProfileOtpPage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_error));
                    mDialog.setDialogMessage(Smessage);
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

}

