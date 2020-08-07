package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 11/18/2015.
 */
public class ForgotPasswordOtp extends Activity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private RelativeLayout Rl_back;
    private EditText Et_otp;
    private Button Bt_send;

    StringRequest postrequest;
    Dialog dialog;

    private String Semail = "", Sotp_Status = "", Sotp = "";
    private RelativeLayout Rl_resendCode;
    private ServiceRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_otp);
        initialize();

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        Et_otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(Et_otp.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        Bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Et_otp.getText().toString().length() == 0) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.otp_label_alert_otp));
                } else if (!Sotp.equals(Et_otp.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.otp_label_alert_invalid));
                } else {
                    Intent i = new Intent(ForgotPasswordOtp.this, ResetPassword.class);
                    i.putExtra("Intent_email",Semail);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
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
        cd = new ConnectionDetector(ForgotPasswordOtp.this);
        isInternetPresent = cd.isConnectingToInternet();

        Rl_back = (RelativeLayout) findViewById(R.id.forgot_password_otp_header_back_layout);
        Et_otp = (EditText) findViewById(R.id.forgot_password_otp_password_editText);
        Bt_send = (Button) findViewById(R.id.forgot_password_otp_submit_button);
        Rl_resendCode = (RelativeLayout) findViewById(R.id.forget_password_resend_code_layout);


        Et_otp.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Intent intent = getIntent();
        Semail = intent.getStringExtra("Intent_email");
        Sotp_Status = intent.getStringExtra("Intent_Otp_Status");
        Sotp = intent.getStringExtra("Intent_verificationCode");

        if (Sotp_Status.equalsIgnoreCase("development")) {
            Et_otp.setText(Sotp);
        } else {
            Et_otp.setText("");
        }

        Et_otp.setSelection(Et_otp.getText().length());
    }


    // -------------------------code for resend otp code Post Request----------------------------------

    private void PostRequest_ResendCode(String Url) {

        dialog = new Dialog(ForgotPasswordOtp.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("--------------Otp resend url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("type", "forgot");
        jsonParams.put("email", Semail);

        System.out.println("--------------Otp resend---------jsonParams----------" + jsonParams);



        mRequest = new ServiceRequest(ForgotPasswordOtp.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Otp resend reponse-------------------" + response);


                String Sstatus = "", Smessage = "";

                String gcmId = "";

                try {
                    JSONObject object = new JSONObject(response);

                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                    if (Sstatus.equalsIgnoreCase("1")) {

                        Sotp = object.getString("verification_code");
                        Semail = object.getString("email_address");
                        Sotp_Status = object.getString("sms_status");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Sstatus.equalsIgnoreCase("1")) {
                    if (Sotp_Status.equalsIgnoreCase("development")) {
                        Et_otp.setText(Sotp);
                    } else {
                        Et_otp.setText("");
                    }

                    Toast.makeText(ForgotPasswordOtp.this,Smessage,Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ForgotPasswordOtp.this,Smessage,Toast.LENGTH_SHORT).show();
                }

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_otp.getWindowToken(), 0);

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ForgotPasswordOtp.this);
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

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(Rl_back.getWindowToken(), 0);

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

}
