package com.shrinkcom.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.subclass.ActivitySubClass;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;
import com.mylibrary.widgets.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by Prem Kumar and Anitha on 11/7/2015.
 */
public class FareBreakUp extends ActivitySubClass {
    private TextView Tv_baseFare, Tv_duration, Tv_waiting, Tv_timeTravel;
    private RelativeLayout Rl_payment;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String SrideId_intent = "", ScurrencyCode = "", StotalAmount = "", Sduation = "", SwaitingTime = "", StravelDistance = "";
    Currency currencycode = null;
    private RoundedImageView Im_DriverImage;
    private TextView Tv_DriverName, Tv_SubTotal, Tv_TripTotal;

    public static FareBreakUp farebreakup_class;

    //------Tip Declaration-----
    private EditText Et_tip_Amount;
    private Button Bt_tip_Apply;
    private RelativeLayout Rl_tip;
    private TextView Tv_tip;
    private LinearLayout Ll_TipAmount;
    private static LinearLayout Ll_RemoveTip;
    private static RelativeLayout Rl_TipMain;
    private String sSelectedTipAmount = "";
    private RatingBar Rb_driver;
    private TextView Tv_serviceTax;
    private Boolean click = true;
    private String sDriverName = "", sDriverImage = "", sDriverRating = "", sDriverLat = "", sDriverLong = "",
            sUserLat = "", sUserLong = "", sSubTotal = "", sServiceTax = "", sTotalPayment = "",stripe_connected="No";

    private ServiceRequest mRequest;
    private String time_out="0";
    private CheckBox Cb_tip;
    Dialog dialog;
    private SessionManager session;
    private String UserID = "";
    private String Scurrency_value = "";
    private String Scurrency_code = "", ScurrencySymbol = "";
    private Handler mapHandler = new Handler();
    private String SduationUnit = "";
    private String sType = "";


    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.FareBreakUp.FAREBREAKUP_FINISH")) {
                if (isInternetPresent) {
                    //     postRequest_MyRides(Iconstant.myride_details_url);
                    finish();
                }
            }
        }
    }


    private RefreshReceiver refreshReceiver;


    Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {



            System.out.println("-----------***********-------RUN-------************------------");
            if (click == true) {
                if (stripe_connected.equals("No")) {
                    System.out.println("-----------***********-------NO-------************------------");
                } else {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }
                    System.out.println("-----------***********-------auto recharge-------************------------");

                    postRequest(Iconstant.Makepayment_url);
                }
                mapHandler.postDelayed(this, Long.parseLong(time_out)*1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farebreak_up);
        farebreakup_class = FareBreakUp.this;
        initialize();
        Rl_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(FareBreakUp.this);
                isInternetPresent = cd.isConnectingToInternet();
                click = false;
                if (isInternetPresent) {
                    mapHandler.removeCallbacks(mapRunnable);
                    if (stripe_connected.equals("No")) {
                        System.out.println("-----------***********-------NO-------************------------");
                        Intent passIntent = new Intent(FareBreakUp.this, FareBreakUpPaymentList.class);
                        if ("push".equalsIgnoreCase(sType)){
                            passIntent.putExtra("type", sType);
                            passIntent.putExtra("RideID", SrideId_intent);
                        }else {
                            passIntent.putExtra("RideID", SrideId_intent);
                        }
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        startActivity(passIntent);

                    } else {
                        if (mRequest != null) {
                            mRequest.cancelRequest();
                        }
                        System.out.println("-----------***********-------auto recharge-------************------------");

                        postRequest(Iconstant.Makepayment_url);
                    }

                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        Bt_tip_Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(FareBreakUp.this);
                isInternetPresent = cd.isConnectingToInternet();
                if(!Et_tip_Amount.getText().toString().equals(".")) {

                    if (Et_tip_Amount.getText().toString().length() > 0 && Double.parseDouble(Et_tip_Amount.getText().toString()) > 0.0) {
                        if (isInternetPresent) {
                            postRequest_Tip(Iconstant.tip_add_url, "Apply");
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.my_rides_detail_tip_empty_label));
                    }
                }
                else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.my_rides_detail_tip_empty_label));
                }

            }
        });


        Ll_RemoveTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(FareBreakUp.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    postRequest_Tip(Iconstant.tip_remove_url, "Remove");
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        Cb_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Rl_tip.setVisibility(View.VISIBLE);
                } else {
                    Rl_tip.setVisibility(View.GONE);
                }
            }
        });


    }

    private void initialize() {
        session = new SessionManager(FareBreakUp.this);
        cd = new ConnectionDetector(FareBreakUp.this);
        isInternetPresent = cd.isConnectingToInternet();
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        Tv_baseFare = (TextView) findViewById(R.id.fare_breakup_total_amount_textview);
        Tv_duration = (TextView) findViewById(R.id.fare_breakup_duration_textview);
        Tv_waiting = (TextView) findViewById(R.id.fare_breakup_waiting_textview);
        Tv_timeTravel = (TextView) findViewById(R.id.fare_breakup_timetravel_textview);
        Rl_payment = (RelativeLayout) findViewById(R.id.fare_breakup_payment_layout);

        Im_DriverImage = (RoundedImageView) findViewById(R.id.fare_breakup_imageview);
        Tv_DriverName = (TextView) findViewById(R.id.fare_breakup_driver_name_textView);
        Tv_SubTotal = (TextView) findViewById(R.id.fare_breakup_subtotal_textView);
        Tv_TripTotal = (TextView) findViewById(R.id.fare_breakup_trip_total_textView);

        Et_tip_Amount = (EditText) findViewById(R.id.fare_breakup_tip_editText);
        Bt_tip_Apply = (Button) findViewById(R.id.fare_breakup_tip_apply_button);
        Rl_tip = (RelativeLayout) findViewById(R.id.fare_breakup_tip_layout);
        Cb_tip = (CheckBox) findViewById(R.id.fare_breakup_tip_checkBox);

        Tv_tip = (TextView) findViewById(R.id.fare_breakup_tip_amount_textView);
        Ll_TipAmount = (LinearLayout) findViewById(R.id.fare_breakup_tip_amount_layout);
        Ll_RemoveTip = (LinearLayout) findViewById(R.id.fare_breakup_tip_amount_remove_layout);
        Rl_TipMain = (RelativeLayout) findViewById(R.id.fare_breakup_tip_top_layout);
        Rb_driver =(RatingBar) findViewById(R.id.fare_breakup_driver_ratingBar);
        Tv_serviceTax =(TextView) findViewById(R.id.fare_breakup_serviceTax_textView);

        int maxLength = 3;
        Et_tip_Amount.setFilters(new InputFilter[]{new EmojiExcludeFilter(),new InputFilter.LengthFilter(maxLength)});

        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.FareBreakUp.FAREBREAKUP_FINISH");
        registerReceiver(refreshReceiver, intentFilter);





        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            sType = intent.getStringExtra("type");
        }
        SrideId_intent = intent.getStringExtra("RideID");

       /* ScurrencyCode = intent.getStringExtra("CurrencyCode");
        StotalAmount = intent.getStringExtra("TotalAmount");
        StravelDistance = intent.getStringExtra("TravelDistance");
        Sduation = intent.getStringExtra("Duration");
        SwaitingTime = intent.getStringExtra("WaitingTime");
        sDriverName = intent.getStringExtra("DriverName");
        sDriverImage = intent.getStringExtra("DriverImage");
        sDriverRating = intent.getStringExtra("DriverRating");
        sDriverLat = intent.getStringExtra("DriverLatitude");
        sDriverLong = intent.getStringExtra("DriverLongitude");
        sUserLat = intent.getStringExtra("UserLatitude");
        sUserLong = intent.getStringExtra("UserLongitude");
        sSubTotal = intent.getStringExtra("SubTotal");
        sServiceTax = intent.getStringExtra("ServiceTax");
        sTotalPayment = intent.getStringExtra("TotalPayment");*/


       /* currencycode = Currency.getInstance(getLocale(ScurrencyCode));

        Picasso.with(FareBreakUp.this).invalidate(sDriverImage);
        Picasso.with(FareBreakUp.this).load(sDriverImage).into(Im_DriverImage);
        Tv_DriverName.setText(sDriverName);
        if(sDriverRating.length()>0)
        {
            Rb_driver.setRating(Float.parseFloat(sDriverRating));
        }

        Tv_baseFare.setText(currencycode.getSymbol() + StotalAmount);
        Tv_duration.setText(Sduation);
        Tv_waiting.setText(SwaitingTime);
        Tv_timeTravel.setText(StravelDistance);
        Tv_SubTotal.setText(currencycode.getSymbol() + sSubTotal);
        Tv_serviceTax.setText(currencycode.getSymbol() + sServiceTax);
        Tv_TripTotal.setText(currencycode.getSymbol() + sTotalPayment);*/
        if (isInternetPresent) {
            postRequest_FareBreakUp(Iconstant.getfareBreakUpURL);
        }
        else
        {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
    }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FareBreakUp.this);
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

    private void Alert1(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FareBreakUp.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                Intent intent = new Intent(FareBreakUp.this, MyRidesDetail.class);
                intent.putExtra("RideID", SrideId_intent);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        mDialog.show();
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

    private void postRequest_FareBreakUp(String Url) {
        dialog = new Dialog(FareBreakUp.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------FareBreakUp Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        System.out.println("---------- FareBreakUp jsonParams------------" + jsonParams);
        mRequest = new ServiceRequest(FareBreakUp.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------FareBreakUp Response----------------" + response);
                String Sstatus = "", Scurrentbalance = "",sTipAmount="";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                           /* Picasso.with(FareBreakUp.this).invalidate(sDriverImage);
                            Picasso.with(FareBreakUp.this).load(sDriverImage).into(Im_DriverImage);
                            Tv_DriverName.setText(sDriverName);
                            if(sDriverRating.length()>0)
                            {
                                Rb_driver.setRating(Float.parseFloat(sDriverRating));
                            }

                            Tv_baseFare.setText(currencycode.getSymbol() + StotalAmount);
                            Tv_duration.setText(Sduation);
                            Tv_waiting.setText(SwaitingTime);
                            Tv_timeTravel.setText(StravelDistance);
                            Tv_SubTotal.setText(currencycode.getSymbol() + sSubTotal);
                            Tv_serviceTax.setText(currencycode.getSymbol() + sServiceTax);
                            Tv_TripTotal.setText(currencycode.getSymbol() + sTotalPayment);*/

                         //   Scurrency_value = response_object.getString("currency_value");
                            Scurrency_code = response_object.getString("currency");
                            ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);
                            Object check_driverinfo_object = response_object.get("driverinfo");
                            if (check_driverinfo_object instanceof JSONObject) {
                                JSONObject driverinfo_object = response_object.getJSONObject("driverinfo");
                                if (driverinfo_object.length() > 0) {
                                    sDriverName = driverinfo_object.getString("name");
                                    sDriverImage = driverinfo_object.getString("image");
                                    sDriverRating= driverinfo_object.getString("ratting");
                                }
                            }
                            Object check_fare_object = response_object.get("fare");
                            if (check_fare_object instanceof JSONObject) {
                                JSONObject fare_object = response_object.getJSONObject("fare");
                                if (fare_object.length() > 0) {
                                    StotalAmount = fare_object.getString("base_fare");
                                    StravelDistance = fare_object.getString("ride_distance") + " " + fare_object.getString("distance_unit");
                                    Sduation = fare_object.getString("ride_duration");
                                    SduationUnit = fare_object.getString("ride_duration_unit");
                                    sSubTotal = fare_object.getString("sub_total");
                                    sServiceTax = fare_object.getString("tax_amount");
                                    sTotalPayment = fare_object.getString("total");
                                    stripe_connected = fare_object.getString("stripe_connected");
                                    time_out = fare_object.getString("payment_timeout");
                                    sTipAmount= fare_object.getString("tip_amount");
                                }
                            }
                           /* Object check_paypal_info_object = response_object.get("paypal_info");
                            if (check_paypal_info_object instanceof JSONObject) {
                                JSONObject paypal_info_object = response_object.getJSONObject("paypal_info");
                                Spayment_mode = paypal_info_object.getString("payment_mode");
                                Smerchant_email = paypal_info_object.getString("merchant_email");
                                Sclient_id = paypal_info_object.getString("client_id");
                                Spayment_status = paypal_info_object.getString("payment_status");
                            }*/
                        }
                    }
                    if (Sstatus.equalsIgnoreCase("1")) {
                        System.out.println("-----------------success-----------");
                        dialog.dismiss();
                        Picasso.with(FareBreakUp.this).invalidate(sDriverImage);
                        Picasso.with(FareBreakUp.this).load(sDriverImage).into(Im_DriverImage);
                        Tv_DriverName.setText(sDriverName);
                        if (sDriverRating.length() > 0)
                            Rb_driver.setRating(Float.parseFloat(sDriverRating));
                        Tv_waiting.setText(SwaitingTime);
                        Tv_serviceTax.setText(ScurrencySymbol + sServiceTax);
                       /* Tv_baseFare.setText(ScurrencySymbol + " " + StotalAmount);*/
//                        Tv_duration.setText(Sduation + " " + getResources().getString(R.string.my_rides_detail_mins_textview));
                        Tv_duration.setText(Sduation + " " + SduationUnit);
                        Tv_TripTotal.setText(ScurrencySymbol + sTotalPayment);
                        Tv_SubTotal.setText(ScurrencySymbol +sSubTotal);
                        Tv_timeTravel.setText(StravelDistance);
                    //    Tv_serviceTax.setText(currencycode.getSymbol() + sServiceTax);

                        if (!sTipAmount.equalsIgnoreCase("0.00")) {
                            System.out.println("--------------tip------------"+sTipAmount);
                            sSelectedTipAmount = sTipAmount;
                            Tv_tip.setText(ScurrencySymbol + sTipAmount);
                            Tv_TripTotal.setText(ScurrencySymbol + sTotalPayment);
                            Rl_TipMain.setVisibility(View.GONE);
                            Rl_tip.setVisibility(View.GONE);
                            Ll_TipAmount.setVisibility(View.VISIBLE);
                        } else {
                            System.out.println("----------no----tip------------"+sTipAmount);
                            Tv_TripTotal.setText(ScurrencySymbol + sTotalPayment);
                            Cb_tip.setChecked(false);
                            Et_tip_Amount.setText("");
                            Rl_TipMain.setVisibility(View.VISIBLE);
                            Ll_TipAmount.setVisibility(View.GONE);
                        }







                        System.out.println("-----------***********-------TimeOUt-------************------------"+time_out);
                        System.out.println("-----------***********-------stripe-------************------------"+stripe_connected);
                     //   mapRunnable.run();

                        mapHandler.postDelayed(mapRunnable, Long.parseLong(time_out)*1000);


                      /*  new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("-----------***********-------RUN-------************------------");
                                if (click == true) {
                                    if (stripe_connected.equals("No")) {
                                        System.out.println("-----------***********-------NO-------************------------");
                                    } else {
                                        if (mRequest != null) {
                                            mRequest.cancelRequest();
                                        }
                                        System.out.println("-----------***********-------auto recharge-------************------------");

                                        postRequest(Iconstant.Makepayment_url);
                                    }

                                }
                            }
                        },  Long.parseLong(time_out) * 1000);*/









                    } else {
                        dialog.dismiss();
                        String Sresponse = object.getString("response");
                        Alert1(getResources().getString(R.string.alert_label_title), Sresponse);
                    }
                } catch (JSONException e) { /* TODO Auto-generated catch block*/
                    e.printStackTrace();
                }
                if (dialog != null) dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }
    //-----------------------Tip Post Request-----------------
    private void postRequest_Tip(String Url, final String tipStatus) {
        dialog = new Dialog(FareBreakUp.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));

        System.out.println("Tips url------------->"+Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);
        if (tipStatus.equalsIgnoreCase("Apply")) {
            jsonParams.put("tips_amount", Et_tip_Amount.getText().toString());
        }

        mRequest = new ServiceRequest(FareBreakUp.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String sStatus = "", sResponse = "",sTipAmount="";
                try {
                    System.out.println("Tips response------------->"+response);
                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        JSONObject response_Object = object.getJSONObject("response");
                        sTipAmount = response_Object.getString("tips_amount");
                        sTotalPayment = response_Object.getString("total");
                        if (tipStatus.equalsIgnoreCase("Apply")) {
                            sSelectedTipAmount = sTipAmount;
                            Tv_tip.setText(ScurrencySymbol + sTipAmount);
                        //    Tv_TripTotal.setText(ScurrencySymbol + sTotalPayment);
                            Rl_TipMain.setVisibility(View.GONE);
                            Rl_tip.setVisibility(View.GONE);
                            Ll_TipAmount.setVisibility(View.VISIBLE);
                        } else {
                            Tv_TripTotal.setText(ScurrencySymbol + sTotalPayment);
                            Cb_tip.setChecked(false);
                            Et_tip_Amount.setText("");
                            Rl_TipMain.setVisibility(View.VISIBLE);
                            Ll_TipAmount.setVisibility(View.GONE);
                        }

                    } else {
                         sResponse = object.getString("response");
                         Alert(getResources().getString(R.string.alert_label_title), sResponse);
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

    private void postRequest(String Url) {
        dialog = new Dialog(FareBreakUp.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("----------------- fare breakup url-----------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);
        jsonParams.put("user_id", UserID);
        System.out.println("----------------- fare breakup jsonParams-----------" + jsonParams);

        mRequest = new ServiceRequest(FareBreakUp.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String sStatus = "", sResponse = "", sTipAmount = "";
                try {
                    System.out.println("----------------- fare breakup response-----------" + response);
                    JSONObject object = new JSONObject(response);
                    System.out.println("-----------------object-----------" + object);
                    sStatus = object.getString("status");
                    sResponse = object.getString("response");

                    if ("1".equalsIgnoreCase(sStatus)) {
                        final PkDialog mDialog = new PkDialog(FareBreakUp.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                click = false;
                                mapHandler.removeCallbacks(mapRunnable);
                                finish();

                                Intent intent = new Intent(FareBreakUp.this, MyRideRating.class);
                                if ("push".equalsIgnoreCase(sType)){
                                    intent.putExtra("type", sType);
                                }else {
                                    intent.putExtra("RideID", SrideId_intent);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                System.out.println("-----------------failed-----------" );
                dialog.dismiss();
            }
        });
    }

    public static void invisibleTips()
    {
        Ll_RemoveTip.setVisibility(View.INVISIBLE);
        Rl_TipMain.setVisibility(View.INVISIBLE);
    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.fare_breakup_label_complete_payment));
            return true;
        }
        return false;
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapHandler.removeCallbacks(mapRunnable);
    }

    @Override
    public void onDestroy() {
        mapHandler.removeCallbacks(mapRunnable);

        super.onDestroy();
        unregisterReceiver(refreshReceiver);
    }
}
