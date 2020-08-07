package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.adapter.MyRidesAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.MyRidesPojo;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 10/28/2015.
 */

public class MyRides extends Activity {
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";

    private ServiceRequest mRequest;
    Dialog dialog;
    private boolean isRideAvailable = false;
    ArrayList<MyRidesPojo> itemlist_all;
    ArrayList<MyRidesPojo> itemlist_upcoming;
    ArrayList<MyRidesPojo> itemlist_completed;
    MyRidesAdapter adapter;

    private ListView listview;
    private TextView empty_text;
    private LinearLayout layout_All, layout_Upcoming, layout_Completed;
    private TextView Tv_All, Tv_Upcoming, Tv_Completed;

    private String StabSelectedCheck = "All";
    public static MyRides myride_class;

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.MYRIDES_FINISH")) {
//                if (isInternetPresent) {
               //     postRequest_MyRides(Iconstant.myride_details_url);
                    finish();
//                }
            }
            if (intent.getAction().equals("com.mylibrary.pushnotification.updateBottom_view")) {
                finish();
            }
            if (intent.getAction().equals("com.MyRides.MYRIDES_REFRESH")) {
                if (isInternetPresent) {
                    postRequest_MyRides(Iconstant.myrides_url);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }



        }
    }
    private RefreshReceiver refreshReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myrides);
        myride_class = MyRides.this;
        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(MyRides.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        layout_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StabSelectedCheck = "All";
                layout_All.setBackgroundColor(0xFF39611A);
                layout_Upcoming.setBackgroundColor(0xFFFFFFFF);
                layout_Completed.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFFFFFFFF);
                Tv_Upcoming.setTextColor(0xFF39611A);
                Tv_Completed.setTextColor(0xFF39611A);

                adapter = new MyRidesAdapter(MyRides.this, itemlist_all);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (itemlist_all.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }

            }
        });

        layout_Upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StabSelectedCheck = "Upcoming";
                layout_All.setBackgroundColor(0xFFFFFFFF);
                layout_Upcoming.setBackgroundColor(0xFF39611A);
                layout_Completed.setBackgroundColor(0xFFFFFFFF);
                Tv_All.setTextColor(0xFF39611A);
                Tv_Upcoming.setTextColor(0xFFFFFFFF);
                Tv_Completed.setTextColor(0xFF39611A);

                adapter = new MyRidesAdapter(MyRides.this, itemlist_upcoming);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (itemlist_upcoming.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }
            }
        });

        layout_Completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StabSelectedCheck = "Completed";
                layout_All.setBackgroundColor(0xFFFFFFFF);
                layout_Upcoming.setBackgroundColor(0xFFFFFFFF);
                layout_Completed.setBackgroundColor(0xFF39611A);
                Tv_All.setTextColor(0xFF39611A);
                Tv_Upcoming.setTextColor(0xFF39611A);
                Tv_Completed.setTextColor(0xFFFFFFFF);

                adapter = new MyRidesAdapter(MyRides.this, itemlist_completed);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (itemlist_completed.size() > 0) {
                    empty_text.setVisibility(View.GONE);
                } else {
                    empty_text.setVisibility(View.VISIBLE);
                    listview.setEmptyView(empty_text);
                }
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cd = new ConnectionDetector(MyRides.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    if (StabSelectedCheck.equalsIgnoreCase("All")) {
                        Intent intent = new Intent(MyRides.this, MyRidesDetail.class);
                        intent.putExtra("RideID", itemlist_all.get(position).getRide_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else if (StabSelectedCheck.equalsIgnoreCase("Upcoming")) {
                        Intent intent = new Intent(MyRides.this, MyRidesDetail.class);
                        intent.putExtra("RideID", itemlist_upcoming.get(position).getRide_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Intent intent = new Intent(MyRides.this, MyRidesDetail.class);
                        intent.putExtra("RideID", itemlist_completed.get(position).getRide_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });
    }

    private void initialize() {

        session = new SessionManager(MyRides.this);
        cd = new ConnectionDetector(MyRides.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemlist_all = new ArrayList<MyRidesPojo>();
        itemlist_upcoming = new ArrayList<MyRidesPojo>();
        itemlist_completed = new ArrayList<MyRidesPojo>();

        back = (RelativeLayout) findViewById(R.id.my_rides_header_back_layout);
        listview = (ListView) findViewById(R.id.my_rides_listview);
        empty_text = (TextView) findViewById(R.id.my_rides_listview_empty_text);
        layout_All = (LinearLayout) findViewById(R.id.my_rides_all_layout);
        layout_Upcoming = (LinearLayout) findViewById(R.id.my_rides_upcoming_layout);
        layout_Completed = (LinearLayout) findViewById(R.id.my_rides_completed_layout);
        Tv_All = (TextView) findViewById(R.id.my_rides_all_textview);
        Tv_Upcoming = (TextView) findViewById(R.id.my_rides_upcoming_textview);
        Tv_Completed = (TextView) findViewById(R.id.my_rides_completed_textview);

        layout_All.setBackgroundColor(0xFF39611A);
        Tv_All.setTextColor(0xFFFFFFFF);

        refreshReceiver = new RefreshReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.MYRIDES_FINISH");
        intentFilter.addAction("com.mylibrary.pushnotification.updateBottom_view");
        intentFilter.addAction("com.MyRides.MYRIDES_REFRESH");

        registerReceiver(refreshReceiver, intentFilter);



        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        if (isInternetPresent) {
            postRequest_MyRides(Iconstant.myrides_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRides.this);
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


    //-----------------------MyRides Post Request-----------------
    private void postRequest_MyRides(String Url) {
        dialog = new Dialog(MyRides.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        System.out.println("-------------MyRides Url----------------" + Url);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("type", "all");
        System.out.println("-------------MyRides jsonParams----------------" + jsonParams);
        mRequest = new ServiceRequest(MyRides.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MyRides Response----------------" + response);

                String Sstatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            Object check_rides_object = response_object.get("rides");
                            if (check_rides_object instanceof JSONArray) {
                                JSONArray ride_array = response_object.getJSONArray("rides");
                                if (ride_array.length() > 0) {
                                    itemlist_all.clear();

                                    for (int i = 0; i < ride_array.length(); i++) {
                                        JSONObject ride_object = ride_array.getJSONObject(i);

                                        MyRidesPojo pojo = new MyRidesPojo();
                                        pojo.setRide_id(ride_object.getString("ride_id"));
                                        pojo.setRide_time(ride_object.getString("ride_time"));
                                        pojo.setRide_date(ride_object.getString("ride_date"));
                                        pojo.setPickup(ride_object.getString("pickup"));
                                        pojo.setRide_status(ride_object.getString("ride_status"));
                                        pojo.setGroup(ride_object.getString("group"));
                                        pojo.setDatetime(ride_object.getString("datetime"));



                                        itemlist_all.add(pojo);

                                        if (ride_object.getString("group").equalsIgnoreCase("upcoming")) {
                                            itemlist_upcoming.add(pojo);
                                        } else if (ride_object.getString("group").equalsIgnoreCase("completed")) {
                                            itemlist_completed.add(pojo);
                                        }
                                    }
                                    isRideAvailable = true;
                                } else {
                                    isRideAvailable = false;
                                }
                            }else {
                                isRideAvailable = false;
                            }
                        }

                    }


                    if (Sstatus.equalsIgnoreCase("1")) {
                        if (isRideAvailable) {
                            empty_text.setVisibility(View.GONE);
                            adapter = new MyRidesAdapter(MyRides.this, itemlist_all);
                            listview.setAdapter(adapter);
                        } else {
                            empty_text.setVisibility(View.VISIBLE);
                            listview.setEmptyView(empty_text);
                        }
                    } else {
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    dialog.dismiss();
                }

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
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshReceiver);
    }
}
