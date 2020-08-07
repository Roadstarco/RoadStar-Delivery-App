package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
public class ResetPassword extends Activity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private RelativeLayout Rl_back;

    private EditText Et_email, Et_password;
    private Button Bt_send;
    private CheckBox Cb_showPwd;

    private ServiceRequest mRequest;
    Dialog dialog;
    private String sEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
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

        Et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_email);
                }
                return false;
            }
        });

        Et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_password);
                }
                return false;
            }
        });

        Cb_showPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    Et_password.setTransformationMethod(null);
                } else {
                    Et_password.setTransformationMethod(new PasswordTransformationMethod());
                }

                Et_password.setSelection(Et_password.getText().length());
            }
        });

        Bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(ResetPassword.this);
                isInternetPresent = cd.isConnectingToInternet();

                Et_password.setText(Et_password.getText().toString().trim());

                if (!isValidEmail(Et_email.getText().toString().replace(" ","").trim())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.reset_password_email_label_enter_valid_email));
                } else if (Et_password.getText().toString().length() == 0) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.reset_password_email_label_enter_valid_password));
                }else if (!Et_email.getText().toString().equalsIgnoreCase(sEmail)) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.reset_password_email_label_enter_valid_email));
                }
                else if (!isValidPassword(Et_password.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.register_label_alert_password));
                }
                else {
                    if (isInternetPresent) {
                        PostRequest(Iconstant.reset_password_url);
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });
    }

    private void initialize() {
        Rl_back = (RelativeLayout) findViewById(R.id.reset_password_header_back_layout);
        Et_email = (EditText) findViewById(R.id.reset_password_email_editText);
        Et_password = (EditText) findViewById(R.id.reset_password_password_editText);
        Bt_send = (Button) findViewById(R.id.reset_password_submit_button);
        Cb_showPwd = (CheckBox) findViewById(R.id.reset_password_show_password_checkBox);

        Et_email.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Intent intent = getIntent();
        sEmail = intent.getStringExtra("Intent_email");

        Et_email.setText(sEmail);
        Et_password.setTransformationMethod(new PasswordTransformationMethod());
    }

    //----------------code to Check Email Validation----------
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

        final PkDialog mDialog = new PkDialog(ResetPassword.this);
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

        dialog = new Dialog(ResetPassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Et_email.getText().toString().replace(" ","").trim());
        jsonParams.put("password", Et_password.getText().toString());

        mRequest = new ServiceRequest(ResetPassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Reset Password reponse-------------------" + response);

                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(ResetPassword.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(Smessage);
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                onBackPressed();
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
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }
        else {
            return true;
        }
    }

    //-----------------Move Back on pressed phone back button-------------
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
