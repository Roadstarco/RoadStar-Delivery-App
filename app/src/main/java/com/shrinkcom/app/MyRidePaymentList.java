package com.shrinkcom.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by Prem Kumar and Anitha on 11/2/2015.
 */
public class MyRidePaymentList extends ActivitySubClass {
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
    private boolean isPaymentAvailable = false;
    private String SpaymentCode = "";

    public static MyRidePaymentList myride_paymentList_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myride_payment_list);
        myride_paymentList_class = MyRidePaymentList.this;
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
                cd = new ConnectionDetector(MyRidePaymentList.this);
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
        //makeFareBreakUp(Iconstant.getfareBreakUpURL);
    }

    private void initialize() {
        session = new SessionManager(MyRidePaymentList.this);
        cd = new ConnectionDetector(MyRidePaymentList.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemlist = new ArrayList<PaymentListPojo>();

        back = (RelativeLayout) findViewById(R.id.my_rides_payment_header_back_layout);
        listview = (ExpandableHeightListView) findViewById(R.id.my_rides_payment_listView);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        Intent intent = getIntent();
        SrideId_intent = intent.getStringExtra("RideID");

        if (isInternetPresent) {
            postRequest_PaymentList(Iconstant.paymentList_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRidePaymentList.this);
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
    private void postRequest_PaymentList(String Url) {
        dialog = new Dialog(MyRidePaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("-------------PaymentList Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(MyRidePaymentList.this);
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
                        adapter = new MyRidePaymentListAdapter(MyRidePaymentList.this, itemlist);
                        listview.setAdapter(adapter);
                        listview.setExpanded(true);
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



    public void showLoadingDialog() {
        dialog = new Dialog(MyRidePaymentList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    //-----------------------MakePayment Cash Post Request-----------------
    private void MakePayment_Cash(String Url) {
        showLoadingDialog();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));
        System.out.println("-------------MakePayment Cash Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(MyRidePaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Cash Response----------------" + response);

                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        final PkDialog mDialog = new PkDialog(MyRidePaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_driver_confirm_label));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                MyRidesDetail.myrideDetail_class.finish();
                                MyRides.myride_class.finish();
                                onBackPressed();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                        mDialog.show();
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
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


    //-----------------------MakePayment Wallet Post Request-----------------

    private void MakePayment_Wallet(String Url) {
        showLoadingDialog();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));
        System.out.println("-------------MakePayment Wallet Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        mRequest = new ServiceRequest(MyRidePaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MakePayment Wallet Response----------------" + response);

                String Sstatus = "", Scurrency_code = "", Scurrent_wallet_balance = "";
                String sCurrencySymbol = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("0")) {
                        Alert(getResources().getString(R.string.my_rides_payment_empty_wallet_sorry), getResources().getString(R.string.my_rides_payment_empty_wallet));
                    } else if (Sstatus.equalsIgnoreCase("1")) {
                        //Updating wallet amount on Navigation Drawer Slide
                        Scurrency_code = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);
                        Scurrent_wallet_balance = object.getString("wallet_amount");
                        session.createWalletAmount(sCurrencySymbol + Scurrent_wallet_balance);
                        NavigationDrawer.navigationNotifyChange();
                        final PkDialog mDialog = new PkDialog(MyRidePaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_wallet_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                MyRidesDetail.myrideDetail_class.finish();
                                MyRides.myride_class.finish();
                                Intent intent = new Intent(MyRidePaymentList.this, MyRideRating.class);
                                intent.putExtra("RideID", SrideId_intent);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });
                        mDialog.show();

                    } else if (Sstatus.equalsIgnoreCase("2")) {
                        //Updating wallet amount on Navigation Drawer Slide
                        Scurrency_code = object.getString("currency");
                        sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);
                        Scurrent_wallet_balance = object.getString("wallet_amount");

                        session.createWalletAmount(sCurrencySymbol + Scurrent_wallet_balance);
                        NavigationDrawer.navigationNotifyChange();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_REFRESH");
                        sendBroadcast(broadcastIntent);

                        final PkDialog mDialog = new PkDialog(MyRidePaymentList.this);
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

    private void getFareBreakUp() {
    }


    //-----------------------MakePayment Auto-Detect Post Request-----------------

    private void MakePayment_Stripe(String Url) {
        dialog = new Dialog(MyRidePaymentList.this);
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
        mRequest = new ServiceRequest(MyRidePaymentList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------MakePayment Auto-Detect Response----------------" + response);
                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        final PkDialog mDialog = new PkDialog(MyRidePaymentList.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_payment_cash_success));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
                                MyRidesDetail.myrideDetail_class.finish();
                                MyRides.myride_class.finish();
                                Intent intent = new Intent(MyRidePaymentList.this, MyRideRating.class);
                                intent.putExtra("RideID", SrideId_intent);
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
        dialog = new Dialog(MyRidePaymentList.this);
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
        mRequest = new ServiceRequest(MyRidePaymentList.this);
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
                        Intent intent = new Intent(MyRidePaymentList.this, MyRidePaymentWebView.class);
                        intent.putExtra("MobileID", mobileId);
                        intent.putExtra("RideID", SrideId_intent);
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

