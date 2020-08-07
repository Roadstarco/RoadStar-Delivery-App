package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user88 on 3/22/2017.
 */

public class FeedBackPage extends Activity
{
    private RelativeLayout back;
    SessionManager session;
    EditText et_msg,et_subject;
    RelativeLayout submit;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    Dialog dialog;
    private ServiceRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_page);
        back=(RelativeLayout)findViewById(R.id.feedback_header_back_layout);
        submit=(RelativeLayout)findViewById(R.id.rl_submit);
        et_msg=(EditText)findViewById(R.id.et_msg);
        et_subject=(EditText)findViewById(R.id.et_sub);

        et_subject.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        et_msg.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        session= new SessionManager(FeedBackPage.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                onBackPressed();
                finish();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_msg.setText(Html.fromHtml(et_msg.getText().toString()).toString());
                et_subject.setText(Html.fromHtml(et_subject.getText().toString()).toString());
               if (et_subject.length() == 0) {
                    erroredit(et_subject, getResources().getString(R.string.action_alert_sub));
                }else  if (et_msg.length() == 0) {
                    erroredit(et_msg,getResources().getString(R.string.action_alert_msg));
                }
                else {
                    cd = new ConnectionDetector(FeedBackPage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        postRequest_saveFeedback(Iconstant.send_report);
                        System.out.println("send_report------------------" + Iconstant.send_report);
                    } else {
                        Alert(getResources().getString(R.string.timer_label_alert_sorry), getResources().getString(R.string.alert_nointernet));
                    }
                }

            }
        });



    }



    private void postRequest_saveFeedback(String Url) {
        dialog = new Dialog(FeedBackPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        HashMap<String, String> user = session.getUserDetails();
        String driver_id = user.get(SessionManager.KEY_USERID);


        final TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------saveFeedback----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", driver_id);
        jsonParams.put("user_type", "user");
        jsonParams.put("subject", et_subject.getText().toString());
        jsonParams.put("message", et_msg.getText().toString());


        System.out.println("id----------" + driver_id);



        System.out.println("subject----------" + et_subject.getText().toString());

        System.out.println("message----------" + et_msg.getText().toString());



        mRequest = new ServiceRequest(FeedBackPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                String Strstatus = "", Sresponse = "", messhae = "";

                System.out.println("saveFeedback-----------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Strstatus = object.getString("status");

                    if (Strstatus.equalsIgnoreCase("1")){

                        JSONObject jsonObject = object.getJSONObject("response");

                        messhae=jsonObject.getString("message");

                    }else{
                        Sresponse = object.getString("response");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (Strstatus.equalsIgnoreCase("1")) {
                    et_msg.setText("");
                    et_subject.setText("");
                    Alert(getResources().getString(R.string.action_success), messhae);



                } else {
                    Alert(getResources().getString(R.string.timer_label_alert_sorry), Sresponse);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }

        });


    }


    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(FeedBackPage.this, R.anim.shake);
        editname.startAnimation(shake);
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            return true;
        }
        return false;
    }



}