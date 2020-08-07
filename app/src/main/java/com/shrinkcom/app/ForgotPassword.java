package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
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
public class ForgotPassword extends Activity {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;
    Dialog dialog;

    private EditText Et_email;
    private TextView Bt_submit;

    String Sstatus = "", Smessage = "", Ssms_status = "", SverificationCode = "", SemailAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        initialize();


        Bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(ForgotPassword.this);
                isInternetPresent = cd.isConnectingToInternet();
                if(Et_email.getText().toString().replace(" ","").trim().length()>0) {
                    if (!isValidEmail(Et_email.getText().toString())) {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.forgot_password_email_label_enter_valid_email));
                    }
                    else
                    if (isInternetPresent) {
                        PostRequest(Iconstant.forgot_password_url);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }

                }else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.forgotpassword));

                }
            }
        });

        Et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_email);
                }
                return false;
            }
        });
    }

    private void initialize() {


        Et_email = (EditText) findViewById(R.id.edt_email);
        Bt_submit = (TextView) findViewById(R.id.btn_frpass);

        Et_email.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Bt_submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
    }

    //-------------------------code to Check Email Validation-----------------------
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    //--------------Close Keyboard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
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


    // -------------------------code for Forgot Password Post Request----------------------------------

    private void PostRequest(final String Url) {

        dialog = new Dialog(ForgotPassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_verifying));

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString());

        mRequest = new ServiceRequest(ForgotPassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Forgot Password reponse-------------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Ssms_status = object.getString("sms_status");
                        SverificationCode = object.getString("verification_code");
                        SemailAddress = object.getString("email_address");
                    }

                    if (Sstatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(ForgotPassword.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.forgot_password_email_label_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                Intent i = new Intent(ForgotPassword.this, ForgotPasswordOtp.class);
                                i.putExtra("Intent_Otp_Status", Ssms_status);
                                i.putExtra("Intent_verificationCode", SverificationCode);
                                i.putExtra("Intent_email", SemailAddress);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();

                    } else {
                        Alert(getResources().getString(R.string.action_error), Smessage);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(Et_email.getWindowToken(), 0);

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
            finish();
            return true;
        }
        return false;
    }

}
