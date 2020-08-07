package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.adapter.RatingAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.RatingPojo;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 11/4/2015.
 */
public class MyRideRating extends Activity {
    private RelativeLayout skip;
    private RelativeLayout submit;
    private EditText Et_comment;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";

    private ServiceRequest mRequest;
    Dialog dialog;
    ArrayList<RatingPojo> itemlist;
    RatingAdapter adapter;
    private ExpandableHeightListView listview;
    private String SrideId_intent = "";
    private boolean isDataAvailable = false;
    private String sType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myride_rating);
        initialize();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ("push".equalsIgnoreCase(sType)) {
                    Intent i = new Intent(MyRideRating.this, NavigationDrawer.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
               /* } else {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                    sendBroadcast(broadcastIntent);
                    finish();
                    onBackPressed();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }*/
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isRatingEmpty = false;
                if (isDataAvailable) {
                    if (itemlist != null) {
                        for (int i = 0; i < itemlist.size(); i++) {
                            if (itemlist.get(i).getRatingcount().length() == 0 || itemlist.get(i).getRatingcount().equalsIgnoreCase("0.0")) {
                                isRatingEmpty = true;
                            }
                        }
                        if (!isRatingEmpty) {
                            if (isInternetPresent) {
                                if (Et_comment.getText().toString().length() > 0) {
                                    HashMap<String, String> jsonParams = new HashMap<String, String>();
                                    jsonParams.put("comments", Et_comment.getText().toString());
                                    jsonParams.put("ratingsFor", "driver");
                                    jsonParams.put("ride_id", SrideId_intent);
                                    for (int i = 0; i < itemlist.size(); i++) {
                                        jsonParams.put("ratings[" + i + "][option_id]", itemlist.get(i).getRatingId());
                                        jsonParams.put("ratings[" + i + "][option_title]", itemlist.get(i).getRatingName());
                                        jsonParams.put("ratings[" + i + "][rating]", itemlist.get(i).getRatingcount());
                                    }
                                    System.out.println("------------jsonParams-------------" + jsonParams);
                                    postRequest_SubmitRating(Iconstant.myride_rating_submit_url, jsonParams);
                                } else {
                                    Alert(getResources().getString(R.string.my_rides_rating_header_sorry_textview), getResources().getString(R.string.my_rides_rating_header_comment_textview));
                                }
                            } else {
                                Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                            }
                        } else {
                            Alert(getResources().getString(R.string.my_rides_rating_header_sorry_textview), getResources().getString(R.string.my_rides_rating_header_enter_all));
                        }
                    }
                } else {
                    if (isInternetPresent) {
                        if (Et_comment.getText().toString().length() > 0) {
                            HashMap<String, String> jsonParams = new HashMap<String, String>();
                            jsonParams.put("comments", Et_comment.getText().toString());
                            jsonParams.put("ratingsFor", "driver");
                            jsonParams.put("ride_id", SrideId_intent);
                            System.out.println("------------jsonParams-------------" + jsonParams);
                            postRequest_SubmitRating(Iconstant.myride_rating_submit_url, jsonParams);
                        } else {
                            Alert(getResources().getString(R.string.my_rides_rating_header_sorry_textview), getResources().getString(R.string.my_rides_rating_header_comment_textview));
                        }
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });
    }

    private void initialize() {
        session = new SessionManager(MyRideRating.this);
        cd = new ConnectionDetector(MyRideRating.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemlist = new ArrayList<RatingPojo>();
        skip = (RelativeLayout) findViewById(R.id.my_rides_rating_header_skip_layout);
        listview = (ExpandableHeightListView) findViewById(R.id.my_rides_rating_listView);
        submit = (RelativeLayout) findViewById(R.id.my_rides_rating_submit_layout);
        Et_comment = (EditText) findViewById(R.id.my_rides_rating_comment_edittext);
        Et_comment.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Et_comment.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            sType = intent.getStringExtra("type");
        }
        SrideId_intent = intent.getStringExtra("RideID");

        if (isInternetPresent) {
            postRequest_RatingList(Iconstant.myride_rating_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRideRating.this);
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

    //-----------------------Rating List Post Request-----------------
    private void postRequest_RatingList(String Url) {
        dialog = new Dialog(MyRideRating.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        System.out.println("-------------Rating List Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("optionsFor", "driver");
        jsonParams.put("ride_id", SrideId_intent);

        System.out.println("rideid-----------" + SrideId_intent);

        mRequest = new ServiceRequest(MyRideRating.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("rateing", response);

                System.out.println("-------------Rating List Response----------------" + response);

                String Sstatus = "";
                String SRating_status = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");


                    if (Sstatus.equalsIgnoreCase("1")) {
                        SRating_status = object.getString("ride_ratting_status");

                        JSONArray payment_array = object.getJSONArray("review_options");
                        if (payment_array.length() > 0) {
                            itemlist.clear();
                            for (int i = 0; i < payment_array.length(); i++) {
                                JSONObject reason_object = payment_array.getJSONObject(i);
                                RatingPojo pojo = new RatingPojo();
                                pojo.setRatingId(reason_object.getString("option_id"));
                                pojo.setRatingName(reason_object.getString("option_title"));
                                pojo.setRatingcount("");
                                itemlist.add(pojo);
                            }
                            isDataAvailable = true;
                        } else {
                            isDataAvailable = false;
                        }
                    } else if (Sstatus.equalsIgnoreCase("0")) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                        sendBroadcast(broadcastIntent);
                        finish();
                        onBackPressed();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    if (Sstatus.equalsIgnoreCase("1")) {

                        if (SRating_status.equalsIgnoreCase("1")) {
                            Toast.makeText(getApplicationContext(), "Already submitted your rating", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isDataAvailable) {
                                listview.setVisibility(View.INVISIBLE);
                                adapter = new RatingAdapter(MyRideRating.this, itemlist);
                                listview.setAdapter(adapter);
                                listview.setExpanded(true);
                            } else {
                                listview.setVisibility(View.GONE);


                            }
                        }


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


    //-----------------------Submit Rating Post Request-----------------
    private void postRequest_SubmitRating(String Url, final HashMap<String, String> jsonParams) {
        dialog = new Dialog(MyRideRating.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        System.out.println("-------------Submit Rating Url----------------" + Url);

        mRequest = new ServiceRequest(MyRideRating.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("------------Submit Rating Response----------------" + response);

                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                        sendBroadcast(broadcastIntent);

                        final PkDialog mDialog = new PkDialog(MyRideRating.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_rating_submit_successfully));
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                finish();
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
            //Do nothing
            return true;
        }
        return false;
    }

}
