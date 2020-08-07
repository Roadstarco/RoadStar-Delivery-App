package com.shrinkcom.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 10/13/2015.
 */
public class InviteAndEarn extends Activity implements View.OnClickListener {
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private TextView Tv_friends_earn, Tv_you_earn, Tv_referral_code;
    private RelativeLayout Rl_whatsApp, Rl_messenger, Rl_sms, Rl_email, Rl_twitter, Rl_facebook;
    private ServiceRequest mRequest;
    private String UserID = "";
    private boolean isdataPresent = false;
    private String Sstatus = "",subject="",message="", friend_earn_amount = "",Earn_Condition="", you_earn_amount = "", friends_rides = "", ScurrencyCode = "", referral_code = "", sShareLink = "";
    String sCurrencySymbol = "";

    final int PERMISSION_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inviteandearn);
        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(InviteAndEarn.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


    }

    private void initialize() {
        session = new SessionManager(InviteAndEarn.this);
        cd = new ConnectionDetector(InviteAndEarn.this);
        isInternetPresent = cd.isConnectingToInternet();

        back = (RelativeLayout) findViewById(R.id.invite_earn_header_back_layout);
        Rl_whatsApp = (RelativeLayout) findViewById(R.id.invite_earn_whatsapp_layout);
        Rl_messenger = (RelativeLayout) findViewById(R.id.invite_earn_messenger_layout);
        Rl_sms = (RelativeLayout) findViewById(R.id.invite_earn_sms_layout);
        Rl_email = (RelativeLayout) findViewById(R.id.invite_earn_email_layout);
        Rl_twitter = (RelativeLayout) findViewById(R.id.invite_earn_twitter_layout);
        Rl_facebook = (RelativeLayout) findViewById(R.id.invite_earn_facebook_layout);
        Tv_friends_earn = (TextView) findViewById(R.id.invite_earn_friend_earn_textview);
        Tv_you_earn = (TextView) findViewById(R.id.invite_earn_you_earn_textview);
        Tv_referral_code = (TextView) findViewById(R.id.invite_earn_referral_code_textview);

        Rl_whatsApp.setOnClickListener(this);
        Rl_messenger.setOnClickListener(this);
        Rl_sms.setOnClickListener(this);
        Rl_email.setOnClickListener(this);
        Rl_twitter.setOnClickListener(this);
        Rl_facebook.setOnClickListener(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        if (isInternetPresent) {
            displayInvite_Request(Iconstant.invite_earn_friends_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }

    }

    @Override
    public void onClick(View v) {
        if (isdataPresent) {
          //  String text = getResources().getString(R.string.invite_earn_label_share_messgae1) + " " + sCurrencySymbol + "" + friend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_messgae2) + " " + referral_code + " " + getResources().getString(R.string.invite_earn_label_share_messgae3);
            String text=message;
            if (v == Rl_whatsApp) {
                whatsApp_sendMsg(text);
            } else if (v == Rl_messenger) {
                messenger_sendMsg(text);
            } else if (v == Rl_sms) {
                sms_sendMsg(text);
            } else if (v == Rl_email) {
                sendEmail(text,subject);
            } else if (v == Rl_twitter) {
             //   String twitter_text = getResources().getString(R.string.invite_earn_label_share_messgae1) + " " + sCurrencySymbol + "" + friend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_messgae2) + " " + referral_code + " " + getResources().getString(R.string.invite_earn_label_twitter_share_messgae);
                String twitter_text =message;
                Uri imageUri = null;
                try {
                    imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(),
                            BitmapFactory.decodeResource(getResources(), R.drawable.invite_and_earn_car_new), null, null));
                } catch (NullPointerException e) {
                }
                shareTwitter(twitter_text, imageUri);
            } else if (v == Rl_facebook) {
                String facebook_text=message;
              //  String facebook_text = getResources().getString(R.string.invite_earn_label_share_messgae1) + " " + sCurrencySymbol + "" + friend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_messgae2) + " " + referral_code + " " + getResources().getString(R.string.invite_earn_label_twitter_share_messgae);
                Uri imageUri = null;
                try {
                    imageUri = Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(),
                            BitmapFactory.decodeResource(getResources(), R.drawable.invite_and_earn_car_new), null, null));
                } catch (NullPointerException e) {
                }

                if (sShareLink.length() > 0) {
                    System.out.println("link");
                    shareFacebookLink(sShareLink);
                } else {
                    System.out.println("text");
                    shareFacebook(facebook_text, imageUri);
                }

            }
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_problem_server));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(InviteAndEarn.this);
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

    //--------Sending message on WhatsApp Method------
    private void whatsApp_sendMsg(String text) {
        PackageManager pm = InviteAndEarn.this.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);  //Check if package exists or not. If not then codein catch block will be called
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_whatsApp_not_installed));
        }
    }

    //--------Sending message on Facebook Messenger Method------
    private void messenger_sendMsg(String text) {
        PackageManager pm = InviteAndEarn.this.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info = pm.getPackageInfo("com.facebook.orca", PackageManager.GET_META_DATA);  //Check if package exists or not. If not then codein catch block will be called
            waIntent.setPackage("com.facebook.orca");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_messenger_not_installed));
        }
    }

    //--------Sending message on SMS Method------
    private void sms_sendMsg(String text) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (!checkSmsPermission()) {
                requestPermission();
            } else {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(InviteAndEarn.this); //Need to change the build to API 19
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                if (defaultSmsPackageName != null) {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                startActivity(sendIntent);
            }
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", text);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
        }
    }


    //----------Sending message on Email Method--------
    protected void sendEmail(String text,String sub) {
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_email_not_installed));
        }
    }


    //----------Share Image and Text on Twitter Method--------
    protected void shareTwitter(String text, Uri image) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, "");
        intent.setType("image/jpeg");
        intent.setPackage("com.twitter.android");

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_twitter_not_installed));
        }
    }

    //----------Share Link on Method--------
    private void shareFacebookLink(String link)
    {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link);
    //    Intent pacakage1 = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
        Intent pacakage2 = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
      //  Intent pacakage4 = getPackageManager().getLaunchIntentForPackage("com.facebook.android");
        /*Intent pacakage3 = getPackageManager().getLaunchIntentForPackage("com.example.facebook");
        Intent pacakage4 = getPackageManager().getLaunchIntentForPackage("com.facebook.android");*/
       /* if (pacakage1 != null) {
            System.out.println("link1");
            intent.setPackage("com.facebook.orca");
        } */

        if (pacakage2 != null) {
            intent.setPackage("com.facebook.katana");
        }
        else
        {
            intent.setPackage("");
        }
        /* else if (pacakage4 != null) {
             System.out.println("link4");
             intent.setPackage("com.facebook.android");
         }*/
        /*else if (pacakage3 != null) {
            System.out.println("link3");
            intent.setPackage("com.facebook.facebook");
        } else if (pacakage4 != null) {
            System.out.println("link4");
            intent.setPackage("com.facebook.android");
        }*/

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_facebook_not_installed));
        }
    }

    //----------Share Image and Text on Facebook Method--------
    protected void shareFacebook(String text, Uri image) {

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_SEND);
        //intent.setType("text/plain");
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_TEXT, "http://project.dectar.com/fortaxi/rider/signup?ref=QVJPQ0tJQQ==");
        intent.putExtra(Intent.EXTRA_STREAM, image);

        Intent pacakage2 = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
      //  Intent pacakage4 = getPackageManager().getLaunchIntentForPackage("com.facebook.android");
        /*Intent pacakage3 = getPackageManager().getLaunchIntentForPackage("com.example.facebook");
        Intent pacakage4 = getPackageManager().getLaunchIntentForPackage("com.facebook.android");*/

        if (pacakage2 != null) {
            intent.setPackage("com.facebook.katana");
        }
        else
        {
            intent.setPackage("");
        }
       /* else if (pacakage4 != null) {
            intent.setPackage("com.facebook.android");
        }*/

        /* else if (pacakage3 != null) {
            intent.setPackage("com.facebook.facebook");
        } else if (pacakage4 != null) {
            intent.setPackage("com.facebook.android");
        }*/

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.invite_earn_label_facebook_not_installed));
        }
    }


    //-----------------------Display Invite Amount Post Request-----------------
    private void displayInvite_Request(String Url) {
        final Dialog dialog = new Dialog(InviteAndEarn.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(InviteAndEarn.this.getResources().getString(R.string.action_pleasewait));

        System.out.println("-------------displayInvite_Request Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);

        mRequest = new ServiceRequest(InviteAndEarn.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------displayInvite_Request Response----------------" + response);

                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (object.length() > 0) {
                        JSONObject response_Object = object.getJSONObject("response");
                        if (response_Object.length() > 0) {
                            JSONObject detail_object = response_Object.getJSONObject("details");
                            if (detail_object.length() > 0) {
                                friend_earn_amount = detail_object.getString("friends_earn_amount");
                                you_earn_amount = detail_object.getString("your_earn_amount");
                                friends_rides = detail_object.getString("your_earn");
                                Earn_Condition= detail_object.getString("your_earn_condition");
                                referral_code = detail_object.getString("referral_code");
                                ScurrencyCode = detail_object.getString("currency");
                                sShareLink = detail_object.getString("url");
                                subject = detail_object.getString("subject");
                                message = detail_object.getString("message");

                                isdataPresent = true;
                            } else {
                                isdataPresent = false;
                            }
                        } else {
                            isdataPresent = false;
                        }
                    } else {
                        isdataPresent = false;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isdataPresent) {
                    sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    if (Earn_Condition.equals("on_first_ride")) {
                        Tv_you_earn.setText(getResources().getString(R.string.invite_earn_label_friend_ride) + " " + sCurrencySymbol + "" + you_earn_amount + ", " + getResources().getString(R.string.invite_earn_label_on_first_ride));
                    } else {
                        Tv_you_earn.setText(getResources().getString(R.string.invite_earn_label_friend_ride) + " " + sCurrencySymbol + you_earn_amount);
                    }


                    Tv_friends_earn.setText(getResources().getString(R.string.invite_earn_label_friends_earn) + " " + sCurrencySymbol + friend_earn_amount);


                    Tv_referral_code.setText(referral_code);
                }
                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    private boolean checkSmsPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                 //   String text = getResources().getString(R.string.invite_earn_label_share_messgae1) + " " + sCurrencySymbol + "" + friend_earn_amount + " " + getResources().getString(R.string.invite_earn_label_share_messgae2) + " " + referral_code + " " + getResources().getString(R.string.invite_earn_label_share_messgae3);
                    String text=message;
                    String defaultSmsPackageName = null; //Need to change the build to API 19
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);
                    }

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    if (defaultSmsPackageName != null) {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    startActivity(sendIntent);
                } else {
                }
                break;
        }
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

}