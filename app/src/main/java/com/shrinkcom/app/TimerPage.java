package com.shrinkcom.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.subclass.ActivitySubClass;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by PremKumar on 9/23/2015.
 */
public class TimerPage extends ActivitySubClass {
    CircleProgressView mCircleView;
    //private CountDownTimer timer;
    int seconds = 0;
    private String retry = "";
    private String rideID = "";
    private String userID = "", userLat = "", userLong = "";

    BroadcastReceiver updateReciver;
    private SessionManager sessionManager;

    private ImageView Iv_cancelRide;
    private LinearLayout Ll_cancelRide_message;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private ServiceRequest mRequest;
    // private Toast toast;
    private PkDialog mdialog;
    Handler mHandler;
    int count = 0;


    Dialog dialog;
    private boolean isTrackRideAvailable = false;
    private boolean isRidePickUpAvailable = false;
    private boolean isRideDropAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timerpage);
        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(TimerPage.this);

        // Receiving the data from broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.pushnotification.RideAccept");
        filter.addAction("com.timerhandler.stop");
        updateReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                System.out.println("----------message--------------" + intent.getStringExtra("message"));
                Intent local = new Intent();
                local.setAction("com.handler.stop");
                sendBroadcast(local);
                if (intent.hasExtra("Action")) {

                    if (intent.getStringExtra("Action").equalsIgnoreCase("ride_confirmed")) {

                        String SrideId_intent1 = intent.getStringExtra("rideID");
                        System.out.println("---------------------jai----------------test--------------------" + intent.getStringExtra("Action") + SrideId_intent1);
                        cd = new ConnectionDetector(TimerPage.this);
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {


                            mHandler.removeCallbacks(mRunnable);
                            Intent i = new Intent(TimerPage.this, MyRideDetailTrackRide.class);
                            i.putExtra("rideID", SrideId_intent1);
                            startActivity(i);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();

                            //  postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url,SrideId_intent1);
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }

                    }
                } else if (intent.getAction().equals("com.timerhandler.stop")) {
                    System.out.println("handler stops jai");
                    mHandler.removeCallbacks(mRunnable);
                }


            }
        };
        registerReceiver(updateReciver, filter);


        Iv_cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    mdialog = new PkDialog(TimerPage.this);
                    mdialog.setDialogTitle(getResources().getString(R.string.timer_label_alert_cancel_ride));
                    mdialog.setDialogMessage(getResources().getString(R.string.timer_label_alert_cancel_ride_message));
                    mdialog.setPositiveButton(getResources().getString(R.string.timer_label_alert_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mdialog.dismiss();
                            cd = new ConnectionDetector(TimerPage.this);
                            isInternetPresent = cd.isConnectingToInternet();

                            if (isInternetPresent) {
                                //    toast.cancel();
                                DeleteRideRequest(Iconstant.delete_ride_url);
                            } else {
                                Alert(getResources().getString(R.string.timer_label_alert_sorry), getResources().getString(R.string.alert_nointernet_message));
                                /*toast.setText(getResources().getString(R.string.alert_nointernet_message));
                                toast.show();*/
                            }
                        }
                    });
                    mdialog.setNegativeButton(getResources().getString(R.string.timer_label_alert_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mdialog.dismiss();
                        }
                    });
                    mdialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //-----------------------Track Ride Post Request-----------------
    private void postRequest_TrackRide(String Url, String SrideId_intent) {
        dialog = new Dialog(TimerPage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        System.out.println("-------------Track Ride Url----------------" + Url);
        System.out.println("-------------Track Ride ride_id----------------" + SrideId_intent);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);

        mRequest = new ServiceRequest(TimerPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Track Ride Response----------------" + response);
                String Sstatus = "";
                String driverID = "", driverName = "", driverImage = "", driverRating = "",
                        driverLat = "", driverLong = "", driverTime = "", rideID = "", driverMobile = "",
                        driverCar_no = "", driverCar_model = "", userLat = "", userLong = "", sRideStatus = "", cab_type = "";

                String sPickUpLocation = "", sPickUpLatitude = "", sPickUpLongitude = "";
                String sDropLocation = "", sDropLatitude = "", sDropLongitude = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            JSONObject driver_profile_object = response_object.getJSONObject("driver_profile");
                            if (driver_profile_object.length() > 0) {
                                driverID = driver_profile_object.getString("driver_id");
                                driverName = driver_profile_object.getString("driver_name");
                                driverImage = driver_profile_object.getString("driver_image");
                                driverRating = driver_profile_object.getString("driver_review");
                                driverLat = driver_profile_object.getString("driver_lat");
                                driverLong = driver_profile_object.getString("driver_lon");
                                driverTime = driver_profile_object.getString("min_pickup_duration");
                                rideID = driver_profile_object.getString("ride_id");
                                driverMobile = driver_profile_object.getString("phone_number");
                                driverCar_no = driver_profile_object.getString("vehicle_number");
                                driverCar_model = driver_profile_object.getString("vehicle_model");
                                userLat = driver_profile_object.getString("rider_lat");
                                userLong = driver_profile_object.getString("rider_lon");
                                sRideStatus = driver_profile_object.getString("ride_status");
                                cab_type = driver_profile_object.getString("cab_type");
                                Object check_pickUp_object = driver_profile_object.get("pickup");
                                if (check_pickUp_object instanceof JSONObject) {
                                    JSONObject pickup_object = driver_profile_object.getJSONObject("pickup");
                                    if (pickup_object.length() > 0) {
                                        sPickUpLocation = pickup_object.getString("location");
                                        JSONObject latLong_object = pickup_object.getJSONObject("latlong");
                                        if (latLong_object.length() > 0) {
                                            sPickUpLatitude = latLong_object.getString("lat");
                                            sPickUpLongitude = latLong_object.getString("lon");

                                            isRidePickUpAvailable = true;
                                        } else {
                                            isRidePickUpAvailable = false;
                                        }
                                    } else {
                                        isRidePickUpAvailable = false;
                                    }
                                } else {
                                    isRidePickUpAvailable = false;
                                }


                                Object check_drop_object = driver_profile_object.get("drop");
                                if (check_drop_object instanceof JSONObject) {
                                    JSONObject drop_object = driver_profile_object.getJSONObject("drop");
                                    if (drop_object.length() > 0) {
                                        sDropLocation = drop_object.getString("location");
                                        JSONObject latLong_object = drop_object.getJSONObject("latlong");
                                        if (latLong_object.length() > 0) {
                                            sDropLatitude = latLong_object.getString("lat");
                                            sDropLongitude = latLong_object.getString("lon");

                                            isRideDropAvailable = true;
                                        } else {
                                            isRideDropAvailable = false;
                                        }
                                    } else {
                                        isRideDropAvailable = false;
                                    }
                                } else {
                                    isRideDropAvailable = false;
                                }


                                isTrackRideAvailable = true;
                            }


                        }
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }


                    if (Sstatus.equalsIgnoreCase("1") && isTrackRideAvailable) {
                        Intent i = new Intent(TimerPage.this, MyRideDetailTrackRide.class);
                        i.putExtra("driverID", driverID);
                        i.putExtra("driverName", driverName);
                        i.putExtra("driverImage", driverImage);
                        i.putExtra("driverRating", driverRating);
                        i.putExtra("driverLat", driverLat);
                        i.putExtra("driverLong", driverLong);
                        i.putExtra("driverTime", driverTime);
                        i.putExtra("rideID", rideID);
                        i.putExtra("driverMobile", driverMobile);
                        i.putExtra("driverCar_no", driverCar_no);
                        i.putExtra("driverCar_model", driverCar_model);
                        i.putExtra("userLat", userLat);
                        i.putExtra("userLong", userLong);
                        i.putExtra("rideStatus", sRideStatus);
                        i.putExtra("cab_type", cab_type);
                        if (isRidePickUpAvailable) {
                            i.putExtra("PickUpLocation", sPickUpLocation);
                            i.putExtra("PickUpLatitude", sPickUpLatitude);
                            i.putExtra("PickUpLongitude", sPickUpLongitude);
                        }

                        if (isRideDropAvailable) {
                            i.putExtra("DropLocation", sDropLocation);
                            i.putExtra("DropLatitude", sDropLatitude);
                            i.putExtra("DropLongitude", sDropLongitude);
                        }
                        mHandler.removeCallbacks(mRunnable);
                        //timer.cancel();

                        if (mRequest != null) {
                            mRequest.cancelRequest();
                        }

                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(TimerPage.this);
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

    private void initialize() {
        // toast = new Toast(TimerPage.this);
        sessionManager = new SessionManager(TimerPage.this);

        Iv_cancelRide = (ImageView) findViewById(R.id.timer_cancel_ride_image);
        Ll_cancelRide_message = (LinearLayout) findViewById(R.id.timer_cancel_ride_layout);
        mCircleView = (CircleProgressView) findViewById(R.id.timer_circleView);
        mCircleView.setEnabled(false);
        mCircleView.setFocusable(false);


        sessionManager.setCouponCode("");

        HashMap<String, String> userDetail = sessionManager.getUserDetails();
        userID = userDetail.get(SessionManager.KEY_USERID);

        Intent intent = getIntent();
        seconds = Integer.parseInt(intent.getStringExtra("Time")) + 1;
        retry = intent.getStringExtra("retry_count");
        rideID = intent.getStringExtra("ride_ID");
        userLat = intent.getStringExtra("userLat");
        userLong = intent.getStringExtra("userLong");
        if (retry != null && retry.length() > 0) {
            retry = "2";
        } else {
            retry = "2";
        }


        //value setting
        mCircleView.setMaxValue(seconds);
        mCircleView.setValueAnimated(0);

        //show unit
        // mCircleView.setUnit("");
        // mCircleView.setShowUnit(true);

        //text sizes
        mCircleView.setTextSize(50);
        // mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size

        // enable auto text size, previous values are overwritten
        mCircleView.setAutoTextSize(true);

        //if you want the calculated text sizes to be bigger/smaller you can do so via
        //mCircleView.setUnitScale(0.9f);
        mCircleView.setTextScale(0.6f);

        //colors of text and unit can be set via
        mCircleView.setTextColor(getResources().getColor(R.color.app_color_transperent));


        /*timer =new CountDownTimer((seconds*1000), 500)
        {
            public void onTick(long millisUntilFinished)
            {
                long sec = millisUntilFinished/1000;

                mCircleView.setText(String.valueOf(sec));
                mCircleView.setTextMode(TextMode.TEXT);
                mCircleView.setValueAnimated(sec, 500);
            }
            public void onFinish()
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Accepted_or_Not", "not");
                returnIntent.putExtra("Retry_Count", retry);
                setResult(RESULT_OK, returnIntent);
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };
        timer.start();*/

        System.out.println("Seconds " + seconds);

        mHandler = new Handler();
        mHandler.post(mRunnable);
    }


    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (count < seconds) {
                count++;
                System.out.println("-----jai--user timer---------" + count);
                mCircleView.setText(String.valueOf(Math.abs(seconds - count)));
                mCircleView.setTextMode(TextMode.TEXT);
                mCircleView.setValueAnimated(count, 500);
                mHandler.postDelayed(this, 1000);
            } else {
                mHandler.removeCallbacks(this);
                System.out.println("---jai----user timer--finised-------" + count);
                if (mRequest != null) {
                    mRequest.cancelRequest();
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("Accepted_or_Not", "not");
                returnIntent.putExtra("Retry_Count", retry);
                setResult(RESULT_OK, returnIntent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        }
    };


    //-------------------Delete Ride Post Request----------------

    private void DeleteRideRequest(String Url) {

        Iv_cancelRide.setVisibility(View.GONE);
        Ll_cancelRide_message.setVisibility(View.VISIBLE);

        System.out.println("--------------Timer Delete Ride url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", userID);
        jsonParams.put("ride_id", rideID);

        mRequest = new ServiceRequest(TimerPage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String Sacceptance = "";
                String Str_driver_id = "", response_value = "", Str_driver_name = "", Str_driver_email = "", Str_driver_image = "", Str_driver_review = "",
                        Str_driver_lat = "", Str_driver_lon = "", Str_min_pickup_duration = "", Str_ride_id = "", Str_phone_number = "",
                        Str_vehicle_number = "", Str_vehicle_model = "";

                System.out.println("--------------Timer Delete Ride reponse-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                            Iv_cancelRide.setVisibility(View.VISIBLE);
                            Ll_cancelRide_message.setVisibility(View.GONE);
                            mHandler.removeCallbacks(mRunnable);


                            Sacceptance = object.getString("acceptance");
                            if (Sacceptance.equalsIgnoreCase("No")) {
                                response_value = object.getString("response");

                            }
                            if (Sacceptance.equalsIgnoreCase("Yes")) {
                                JSONObject response_object = object.getJSONObject("response");

                                JSONObject driverObject = response_object.getJSONObject("driver_profile");

                                Str_driver_id = driverObject.getString("driver_id");
                                Str_driver_name = driverObject.getString("driver_name");
                                Str_driver_email = driverObject.getString("driver_email");
                                Str_driver_image = driverObject.getString("driver_image");
                                Str_driver_review = driverObject.getString("driver_review");
                                Str_driver_lat = driverObject.getString("driver_lat");
                                Str_driver_lon = driverObject.getString("driver_lon");
                                Str_min_pickup_duration = driverObject.getString("min_pickup_duration");
                                Str_ride_id = driverObject.getString("ride_id");
                                Str_phone_number = driverObject.getString("phone_number");
                                Str_vehicle_number = driverObject.getString("vehicle_number");
                                Str_vehicle_model = driverObject.getString("vehicle_model");
                            }

                            if (Sacceptance.equalsIgnoreCase("Yes")) {

                                Intent local = new Intent();
                                local.setAction("com.handler.stop");
                                sendBroadcast(local);


                                cd = new ConnectionDetector(TimerPage.this);
                                isInternetPresent = cd.isConnectingToInternet();
                                if (isInternetPresent) {

                                    mHandler.removeCallbacks(mRunnable);
                                    Intent i = new Intent(TimerPage.this, MyRideDetailTrackRide.class);
                                    i.putExtra("rideID", rideID);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();


                                    //       postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url,Str_ride_id);


                                } else {
                                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                                }



                                /*Intent i = new Intent(TimerPage.this, TrackYourRide.class);
                                i.putExtra("driverID", Str_driver_id);
                                i.putExtra("driverName", Str_driver_name);
                                i.putExtra("driverImage", Str_driver_image);
                                i.putExtra("driverRating", Str_driver_review);
                                i.putExtra("driverLat", Str_driver_lat);
                                i.putExtra("driverLong", Str_driver_lon);
                                i.putExtra("driverTime", Str_min_pickup_duration);
                                i.putExtra("rideID", Str_ride_id);
                                i.putExtra("driverMobile", Str_phone_number);
                                i.putExtra("driverCar_no", Str_vehicle_number);
                                i.putExtra("driverCar_model", Str_vehicle_model);
                                i.putExtra("userLat", String.valueOf(userLat));
                                i.putExtra("userLong", String.valueOf(userLong));
                                i.putExtra("rideStatus", "Confirmed");
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
                            }
                            if (Sacceptance.equalsIgnoreCase("No")) {
                                final PkDialog mDialog = new PkDialog(TimerPage.this);
                                mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                                mDialog.setDialogMessage(response_value);
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();

                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("Accepted_or_Not", "Cancelled");
                                        returnIntent.putExtra("Retry_Count", retry);
                                        setResult(RESULT_OK, returnIntent);
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }
                                });
                                mDialog.show();

                            }
                        } else {
                            response_value = object.getString("response");
                            Alert(getResources().getString(R.string.timer_label_alert_sorry), response_value);

                           /* toast.setText(response_value);
                            toast.show();*/
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Iv_cancelRide.setVisibility(View.VISIBLE);
                Ll_cancelRide_message.setVisibility(View.GONE);
            }

            @Override
            public void onErrorListener() {
                Iv_cancelRide.setVisibility(View.VISIBLE);
                Ll_cancelRide_message.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onResume() {
//        ChatService.startUserAction(TimerPage.this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        //timer.cancel();

        if (mdialog != null) {
            mdialog.dismiss();
        }

        mHandler.removeCallbacks(mRunnable);
        unregisterReceiver(updateReciver);
        super.onDestroy();
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            //Do nothing
            return true;
        }
        return false;
    }

}
