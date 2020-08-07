package com.shrinkcom.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.adapter.MyRidePaymentListAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.PaymentListPojo;
import com.shrinkcom.subclass.ActivitySubClass;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by Prem Kumar and Anitha on 11/7/2015.
 */
public class FareBreakUpPaymentList extends ActivitySubClass {


    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";
    private ServiceRequest mRequest;
    private Dialog dialog;
    private ArrayList<PaymentListPojo> itemlist;
    private MyRidePaymentListAdapter adapter;
    private ExpandableHeightListView listview;
    private String SrideId_intent = "";
    private boolean isPaymentAvailable=false;
    private String SpaymentCode="";
    public  static FareBreakUpPaymentList myride_paymentList_class;
    String   ScurrencySymbol;
    private String sType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myride_payment_list);
        myride_paymentList_class = FareBreakUpPaymentList.this;
        initialize();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cd = new ConnectionDetector(FareBreakUpPaymentList.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (itemlist.get(position).getPaymentCode().equalsIgnoreCase("cash")) {
                        MakePayment_Cash(Iconstant.makepayment_cash_url);
                    } else if (itemlist.get(position).getPaymentCode().equalsIgnoreCase("wallet")) {
                        MakePayment_Wallet(Iconstant.makepayment_wallet_url);
                    } else if (itemlist.get(position).getPaymentCode().equalsIgnoreCase("auto_detect")) {
                        MakePayment_Stripe(Iconstant.makepayment_autoDetect_url);
                    } else {
                        SpaymentCode = itemlist.get(position).getPaymentCode();
                        MakePayment_WebView_MobileID(Iconstant.makepayment_Get_webview_mobileId_url);
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });
       // makeFareBreakUp(Iconstant.getfareBreakUpURL);
    }
    public void showLoadingDialog() {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void makeFareBreakUp(String url) {
        showLoadingDialog();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                try {
                    JSONObject mainResponse = new JSONObject(response);
                    JSONObject responseJSON = mainResponse.getJSONObject("response");
                    JSONObject driverInfoJSON = mainResponse.getJSONObject("driverinfo");
                    JSONObject fareJSON = mainResponse.getJSONObject("fare");
                    String status = mainResponse.getString("status");
                    if ("1".equalsIgnoreCase(status)) {
                        String stripe_connected = fareJSON.getString("stripe_connected");
                        if ("yes".equalsIgnoreCase(stripe_connected)) {
                            String payment_timeout = fareJSON.getString("payment_timeout");
                           // showLoadTimer(payment_timeout);
                        }
                    } else {
                        String errorResponse  = mainResponse.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), errorResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onErrorListener() {
            }
        });
    }
    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            MakePayment_Stripe(Iconstant.makepayment_autoDetect_url);
        }
    };


    private void initialize() {
        session = new SessionManager(FareBreakUpPaymentList.this);
        cd = new ConnectionDetector(FareBreakUpPaymentList.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemlist =new ArrayList<PaymentListPojo>();
        back = (RelativeLayout) findViewById(R.id.my_rides_payment_header_back_layout);
        listview = (ExpandableHeightListView) findViewById(R.id.my_rides_payment_listView);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            sType = intent.getStringExtra("type");
        }
        SrideId_intent = intent.getStringExtra("RideID");
        if (isInternetPresent) {
            postRequest_PaymentList(Iconstant.paymentList_url);
        }else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentList.this);
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



    //-----------------------PaymentList Post Request-----------------
    private void postRequest_PaymentList(String Url)
    {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title=(TextView)dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("-------------PaymentList Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------PaymentList Response----------------" + response);
                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONArray payment_array = response_object.getJSONArray("payment");
                            if (payment_array.length() > 0) {
                                itemlist.clear();
                                for (int i = 0; i < payment_array.length(); i++) {
                                    JSONObject reason_object = payment_array.getJSONObject(i);
                                    PaymentListPojo pojo = new PaymentListPojo();
                                    pojo.setPaymentName(reason_object.getString("name"));
                                    pojo.setPaymentCode(reason_object.getString("code"));
                                    itemlist.add(pojo);
                                }
                                isPaymentAvailable = true;
                            } else {
                                isPaymentAvailable = false;
                            }
                        }
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                    if (Sstatus.equalsIgnoreCase("1") && isPaymentAvailable) {
                        adapter = new MyRidePaymentListAdapter(FareBreakUpPaymentList.this, itemlist);
                        listview.setAdapter(adapter);
                        listview.setExpanded(true);
                    }

                } catch (JSONException e) {
                }
                dialog.dismiss();
            }
            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }



    //-----------------------MakePayment Cash Post Request-----------------
    private void MakePayment_Cash(String Url) {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));


        System.out.println("-------------MakePayment Cash Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);

        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------MakePayment Cash Response----------------" + response);
                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_driver_confirm_label));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();

                                Intent finish_timerPage = new Intent();
                                finish_timerPage.setAction("com.FareBreakUp.FAREBREAKUP_FINISH");
                                sendBroadcast(finish_timerPage);

                                Intent refresh_myridesPage = new Intent();
                                refresh_myridesPage.setAction("com.package.MYRIDES_FINISH");
                                sendBroadcast(refresh_myridesPage);

                                if ("push".equalsIgnoreCase(sType)){
                                    Intent i = new Intent(FareBreakUpPaymentList.this, NavigationDrawer.class);
                                    startActivity(i);
                                    finish();
                                }else {
                                    finish();
                                    onBackPressed();
                                }
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();

                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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




    //-----------------------MakePayment Wallet Post Request-----------------
    private void MakePayment_Wallet(String Url) {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------MakePayment Wallet Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);

        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------MakePayment Wallet Response----------------" + response);
                String Sstatus = "", Scurrency_code = "", Scurrent_wallet_balance = "";
              //  Currency currencycode = null;
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("0")) {
                        Alert(getResources().getString(R.string.my_rides_payment_empty_wallet_sorry), getResources().getString(R.string.my_rides_payment_empty_wallet));
                    } else if (Sstatus.equalsIgnoreCase("1")) {
                        //Updating wallet amount on Navigation Drawer Slide
                        Scurrency_code = object.getString("currency");
                    //    currencycode = Currency.getInstance(getLocale(Scurrency_code));
                       ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);
                        Scurrent_wallet_balance = object.getString("wallet_amount");
                        session.createWalletAmount(ScurrencySymbol + Scurrent_wallet_balance);
                        NavigationDrawer.navigationNotifyChange();
                        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_wallet_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                FareBreakUp.farebreakup_class.finish();
                                Intent intent = new Intent(FareBreakUpPaymentList.this, MyRideRating.class);
                                if ("push".equalsIgnoreCase(sType)){
                                    intent.putExtra("type", sType);
                                    intent.putExtra("RideID", SrideId_intent);
                                }else {
                                    intent.putExtra("RideID", SrideId_intent);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();

                    } else if (Sstatus.equalsIgnoreCase("2")) {

                        FareBreakUp.invisibleTips();

                        //Updating wallet amount on Navigation Drawer Slide
                        Scurrency_code = object.getString("currency");
                    //    currencycode = Currency.getInstance(getLocale(Scurrency_code));
                        ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);
                        Scurrent_wallet_balance = object.getString("wallet_amount");
                        session.createWalletAmount(ScurrencySymbol + Scurrent_wallet_balance);
                        NavigationDrawer.navigationNotifyChange();
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_REFRESH");
                        sendBroadcast(broadcastIntent);
                        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_driver_confirm_label));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                postRequest_PaymentList(Iconstant.paymentList_url);
                            }
                        });
                        mDialog.show();

                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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

    //-----------------------MakePayment Auto-Detect Post Request-----------------
    private void MakePayment_Stripe(String Url) {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));
        System.out.println("-------------MakePayment Auto-Detect Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Auto-Detect Response----------------" + response);

                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                FareBreakUp.farebreakup_class.finish();
                                Intent intent = new Intent(FareBreakUpPaymentList.this, MyRideRating.class);
                                if ("push".equalsIgnoreCase(sType)){
                                    intent.putExtra("type", sType);
                                    intent.putExtra("RideID", SrideId_intent);
                                }else {
                                    intent.putExtra("RideID", SrideId_intent);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();

                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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


    //-----------------------MakePayment WebView-MobileID Post Request-----------------
    private void MakePayment_WebView_MobileID(String Url) {
        dialog = new Dialog(FareBreakUpPaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));
        System.out.println("-------------MakePayment WebView-MobileID Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        jsonParams.put("gateway", SpaymentCode);

        mRequest = new ServiceRequest(FareBreakUpPaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment WebView-MobileID Response----------------" + response);

                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        String mobileId = object.getString("mobile_id");
                        Intent intent = new Intent(FareBreakUpPaymentList.this, FareBreakUpPaymentWebView.class);
                        intent.putExtra("MobileID", mobileId);
                        if ("push".equalsIgnoreCase(sType)){
                            intent.putExtra("type", sType);
                            intent.putExtra("RideID", SrideId_intent);
                        }else {
                            intent.putExtra("RideID", SrideId_intent);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return false;
    }
}


