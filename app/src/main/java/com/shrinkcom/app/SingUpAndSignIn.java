package com.shrinkcom.app;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SingUpAndSignIn extends Activity {
    //private SimpleGestureFilter detector;

    private TextView signin,register;
    public static SingUpAndSignIn activty;
    private boolean isAppInfoAvailable = false;
    private String Str_Hash="",Language_code="";
    private ServiceRequest mRequest;
    SessionManager session;

    private ArrayList<EmergencyPojo> emergencyAraryList;
    private boolean isEmergencyAvailabe = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_home_page);
        activty=this;
        initialize();
        signin.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SingUpAndSignIn.this, LoginPage.class);
                i.putExtra("HashKey",Str_Hash);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        register.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SingUpAndSignIn.this, RegisterPage.class);
                startActivity(i);
                //	overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        // Detect touched area
        //	detector = new SimpleGestureFilter(this,this);


    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);

    }*/
    private void initialize()
    {
        signin=(TextView)findViewById(R.id.signin_main_button);
        register=(TextView)findViewById(R.id.register_main_button);
        session = new SessionManager(getApplicationContext());
        emergencyAraryList = new ArrayList<EmergencyPojo>();
        Intent i = getIntent();
        Str_Hash = i.getStringExtra("HashKey");

        System.out.println("Str_Hash--------------------"+Str_Hash);

        signin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        register.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        postRequest_AppInformation(Iconstant.app_info_url);
    }


    private void postRequest_AppInformation(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", "");
        mRequest = new ServiceRequest(SingUpAndSignIn.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------SingUpAndSignIn appinfo----------------" +response);
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

                                Object emercencyObject = info_object.get("emergency_numbers");
                                if (emercencyObject instanceof JSONArray) {
                                    JSONArray emercency_array = info_object.getJSONArray("emergency_numbers");
                                    if (emercency_array.length() > 0) {
                                        emergencyAraryList.clear();
                                        for (int j = 0; j < emercency_array.length(); j++) {
                                            JSONObject job = emercency_array.getJSONObject(j);
                                            EmergencyPojo emergencyPojo = new EmergencyPojo();
                                            emergencyPojo.setTitle(job.getString("title"));
                                            emergencyPojo.setNumber(job.getString("number"));
                                            emergencyAraryList.add(emergencyPojo);
                                        }
                                        isEmergencyAvailabe = true;
                                    } else {
                                        isEmergencyAvailabe = false;
                                    }

                                }
                                Language_code = info_object.getString("lang_code");


                                /* Language_code="es";*/
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


                        HashMap<String, String> language = session.getLanaguage();
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
                                session.setlamguage("ta","ta");
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



                        if (isEmergencyAvailabe){
                            session.putEmergencyContactDetails(emergencyAraryList);
                        }

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

            }
        });
    }



	/*@Override
	public void onSwipe(int direction) {
		String str = "";

		switch (direction) {

			case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
				break;
			case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
				break;
			case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
				break;
			case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
				break;

		}
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDoubleTap() {
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
	}*/
}
