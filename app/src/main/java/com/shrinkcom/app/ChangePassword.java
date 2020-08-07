package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.facebook.Util;
import com.mylibrary.volley.ServiceRequest;
//import com.mylibrary.xmpp.XmppService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 10/8/2015.
 */
public class ChangePassword extends Activity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    private SessionManager session;
    private RelativeLayout back;
    private EditText Et_old_password, Et_new_password, Et_confirm_password;
    private String UserID = "";
    private ServiceRequest mRequest;
    private Button Bt_submit;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        Bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_old_password.setText(Et_old_password.getText().toString().trim());
                Et_new_password.setText(Et_new_password.getText().toString().trim());
                Et_confirm_password.setText(Et_confirm_password.getText().toString().trim());
                if (Et_old_password.getText().toString().length() == 0) {
                    erroredit(Et_old_password, getResources().getString(R.string.changepassword_label_alert_oldpassword));
                } else if (!isValidPassword(Et_new_password.getText().toString())) {
                    erroredit(Et_new_password, getResources().getString(R.string.changepassword_label_alert_newpassword));
                } else if (!isValidPassword(Et_confirm_password.getText().toString())) {
                    erroredit(Et_confirm_password, getResources().getString(R.string.changepassword_label_alert_newpassword));
                }else if (Et_old_password.getText().toString().equals(Et_new_password.getText().toString())) {
                    erroredit(Et_new_password, getResources().getString(R.string.changepassword_lable_old_notmatch_edittext));
                } else if (!Et_new_password.getText().toString().equals(Et_confirm_password.getText().toString())) {
                    erroredit(Et_confirm_password, getResources().getString(R.string.changepassword_lable_confirm_notmatch_edittext));
                }  else {

                    cd = new ConnectionDetector(ChangePassword.this);
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent) {
                        postRequest_changePassword(Iconstant.changePassword_url);

                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });


        Et_old_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_old_password);
                }
                return false;
            }
        });

        Et_new_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_new_password);
                }
                return false;
            }
        });

        Et_confirm_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_confirm_password);
                }
                return false;
            }
        });


    }

    private void initialize() {
        session = new SessionManager(ChangePassword.this);

        Bt_submit = (Button) findViewById(R.id.change_password_submitbutton);
        back = (RelativeLayout) findViewById(R.id.changepassword_header_back_layout);
        Et_old_password = (EditText) findViewById(R.id.change_password_enter_old_password_editText);
        Et_new_password = (EditText) findViewById(R.id.change_password_enter_new_password_edittext);
        Et_confirm_password = (EditText) findViewById(R.id.change_password_confirm_editText);

        Et_old_password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_new_password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_confirm_password.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Bt_submit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        Et_old_password.addTextChangedListener(EditorWatcher);
        Et_new_password.addTextChangedListener(EditorWatcher);
        Et_confirm_password.addTextChangedListener(EditorWatcher);
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(ChangePassword.this, R.anim.shake);
        editname.startAnimation(shake);

        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-------------------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ChangePassword.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setCancelOnTouchOutside(false);

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
            if (Et_old_password.getText().length() > 0) {
                Et_old_password.setError(null);
            }
            if (Et_new_password.getText().length() > 0) {
                Et_new_password.setError(null);
            }
            if (Et_confirm_password.getText().length() > 0) {
                Et_confirm_password.setError(null);
            }
        }
    };


    // ---validating password with retype password---
    private boolean isValidPassword(String pass) {
        if (pass.length() < 6) {
            return false;
        }

            /*
             * else if(!pass.matches("(.*[A-Z].*)")) { return false; }
			 */
       /* else if (!pass.matches("(.*[a-z].*)")) {
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

    //-----------------------Change Password Post Request-----------------
    private void postRequest_changePassword(String Url) {
        dialog = new Dialog(ChangePassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_otp));

        System.out.println("-------------change password Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("password", Et_old_password.getText().toString());
        jsonParams.put("new_password", Et_new_password.getText().toString());

        mRequest = new ServiceRequest(ChangePassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------change password Response----------------" + response);

                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dialog.dismiss();
                if (Sstatus.equalsIgnoreCase("1")) {

                    postRequest_Logout(Iconstant.logout_url);

                    final PkDialog mDialog = new PkDialog(ChangePassword.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                    mDialog.setDialogMessage(getResources().getString(R.string.changepassword_label_changed_success));
                    mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                           /* onBackPressed();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();*/
//                            postRequest_Logout(Iconstant.logout_url);

                        }
                    });
                    mDialog.show();
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
//        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("device", "ANDROID");

        mRequest = new ServiceRequest(ChangePassword.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------LogOut Response-----------------" + response);
                String Sstatus = "", Sresponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Sresponse = object.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (Sstatus.equalsIgnoreCase("1")) {

                    logoutFromFacebook();
                    session.logoutUser();
//                    stopService(new Intent(ChangePassword.this, XmppService.class));

                    Intent local = new Intent();
                    local.setAction("com.app.logout");
                    ChangePassword.this.sendBroadcast(local);

                    onBackPressed();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    Alert(getResources().getString(R.string.action_error), Sresponse);
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

    public void logoutFromFacebook() {
        Util.clearCookies(ChangePassword.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    private void showDialog(String data) {
        dialog = new Dialog(ChangePassword.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(data);
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

           /* onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
            return true;
        }
        return false;
    }
}

