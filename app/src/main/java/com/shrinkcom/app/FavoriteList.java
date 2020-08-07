package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shrinkcom.adapter.FavoriteListAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.FavoriteListPojo;
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
 */
public class FavoriteList extends Activity {

    private TextView Tv_selectedlocation;
    private RelativeLayout Rl_back, Rl_favorite, Rl_empty;
    private SwipeMenuListView listview;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";
    private String SselectedAddress = "";
    private String Sselected_latitude = "", Sselected_longitude = "";
    private StringRequest postrequest, deleteRequest;
    private Dialog dialog;
    private boolean isFavouriteListAvailable = false;
    private ArrayList<FavoriteListPojo> itemList;
    private FavoriteListAdapter adapter;
    private BroadcastReceiver updateReceiver;
    private ServiceRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);
        initialize();
        swipeMenu_Initialize();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.favoriteList.refresh");
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.favoriteList.refresh")) {
                    if (isInternetPresent) {
                        postRequest_FavoriteList(Iconstant.favoritelist_display_url);
                    }
                }
            }
        };
        registerReceiver(updateReceiver, filter);

        Rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Selected_Latitude", itemList.get(position).getLatitude());
                returnIntent.putExtra("Selected_Longitude", itemList.get(position).getLongitude());
                returnIntent.putExtra("Selected_Location", itemList.get(position).getAddress());
                setResult(RESULT_OK, returnIntent);
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
        Rl_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteList.this, FavoriteAdd.class);
                intent.putExtra("Intent_Latitude", Sselected_latitude);
                intent.putExtra("Intent_Longitude", Sselected_longitude);
                intent.putExtra("Intent_Address", SselectedAddress);
                intent.putExtra("Intent_IdentityKey", "New");
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                cd = new ConnectionDetector(FavoriteList.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    switch (index) {
                        case 0:
                            Intent intent = new Intent(FavoriteList.this, FavoriteAdd.class);
                            intent.putExtra("Intent_Title", itemList.get(position).getTitle());
                            intent.putExtra("Intent_Latitude", itemList.get(position).getLatitude());
                            intent.putExtra("Intent_Longitude", itemList.get(position).getLongitude());
                            intent.putExtra("Intent_Address", itemList.get(position).getAddress());
                            intent.putExtra("Intent_LocationKey", itemList.get(position).getLocation_key());
                            intent.putExtra("Intent_IdentityKey", "Edit");
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            break;
                        case 1:
                            postRequest_FavoriteDelete(Iconstant.favoritelist_delete_url, itemList.get(position).getLocation_key(), position);
                            break;
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }

                return false;// false : close the menu; true : not close the menu
            }
        });

    }

    private void initialize() {
        session = new SessionManager(FavoriteList.this);
        cd = new ConnectionDetector(FavoriteList.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemList = new ArrayList<FavoriteListPojo>();
        listview = (SwipeMenuListView) findViewById(R.id.favorite_list_listView);
        Tv_selectedlocation = (TextView) findViewById(R.id.favorite_list_favorite_location_textview);
        Rl_back = (RelativeLayout) findViewById(R.id.favorite_list_header_back_layout);
        Rl_favorite = (RelativeLayout) findViewById(R.id.favorite_list_favorite_heart_layout);
        Rl_empty = (RelativeLayout) findViewById(R.id.favorite_list_listview_empty_layout);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        Intent intent = getIntent();
        SselectedAddress = intent.getStringExtra("SelectedAddress");
        Sselected_latitude = intent.getStringExtra("SelectedLatitude");
        Sselected_longitude = intent.getStringExtra("SelectedLongitude");
        Tv_selectedlocation.setText(SselectedAddress);
        if (isInternetPresent) {
            postRequest_FavoriteList(Iconstant.favoritelist_display_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    private void swipeMenu_Initialize() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(0xFFAFAFAF));
                // set item width
                editItem.setWidth(150);
                // set a icon
                // editItem.setIcon(R.drawable.menu_edit_icon);
                // set item title
                editItem.setTitle(getResources().getString(R.string.fragment_edit));
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(0xFFFD4542));
                // set item width
                deleteItem.setWidth(150);
                // set a icon
                deleteItem.setIcon(R.drawable.menu_delete_icon);
                // set item title
                //deleteItem.setTitle("Delete");
                // set item title fontsize
                //deleteItem.setTitleSize(17);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listview.setMenuCreator(creator);
        // Swipe directions Right
        listview.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FavoriteList.this);
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

    //-----------------------Favourite List Display Post Request-----------------
    private void postRequest_FavoriteList(String Url) {
        dialog = new Dialog(FavoriteList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));
        System.out.println("-------------Favourite List Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);


        mRequest = new ServiceRequest(FavoriteList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {


                System.out.println("-------------Favourite List Response----------------" + response);
                String Sstatus = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONArray location_array = response_object.getJSONArray("locations");
                            if (location_array.length() > 0) {
                                itemList.clear();
                                for (int i = 0; i < location_array.length(); i++) {
                                    JSONObject location_object = location_array.getJSONObject(i);
                                    FavoriteListPojo pojo = new FavoriteListPojo();
                                    pojo.setTitle(location_object.getString("title"));
                                    pojo.setAddress(location_object.getString("address"));
                                    pojo.setLatitude(location_object.getString("latitude"));
                                    pojo.setLongitude(location_object.getString("longitude"));
                                    pojo.setLocation_key(location_object.getString("location_key"));
                                    itemList.add(pojo);
                                }
                                isFavouriteListAvailable = true;
                            }
                        }
                    } else {
                        isFavouriteListAvailable = false;
                    }
                    if (Sstatus.equalsIgnoreCase("1") && isFavouriteListAvailable) {
                        listview.setVisibility(View.INVISIBLE);
                        Rl_empty.setVisibility(View.GONE);
                        adapter = new FavoriteListAdapter(FavoriteList.this, itemList);
                        listview.setAdapter(adapter);
                    } else {
                        listview.setVisibility(View.GONE);
                        Rl_empty.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();


            }
            @Override
            public void onErrorListener() {

            }
        });
       /* postrequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("-------------Favourite List Response----------------" + response);
                        String Sstatus = "";
                        try {
                            JSONObject object = new JSONObject(response);
                            Sstatus = object.getString("status");
                            if (Sstatus.equalsIgnoreCase("1")) {
                                JSONObject response_object = object.getJSONObject("response");
                                if (response_object.length() > 0) {
                                    JSONArray location_array = response_object.getJSONArray("locations");
                                    if (location_array.length() > 0) {
                                        itemList.clear();
                                        for (int i = 0; i < location_array.length(); i++) {
                                            JSONObject location_object = location_array.getJSONObject(i);
                                            FavoriteListPojo pojo = new FavoriteListPojo();
                                            pojo.setTitle(location_object.getString("title"));
                                            pojo.setAddress(location_object.getString("address"));
                                            pojo.setLatitude(location_object.getString("latitude"));
                                            pojo.setLongitude(location_object.getString("longitude"));
                                            pojo.setLocation_key(location_object.getString("location_key"));
                                            itemList.add(pojo);
                                        }
                                        isFavouriteListAvailable = true;
                                    }
                                }
                            } else {
                                isFavouriteListAvailable = false;
                            }
                            if (Sstatus.equalsIgnoreCase("1") && isFavouriteListAvailable) {
                                listview.setVisibility(View.INVISIBLE);
                                Rl_empty.setVisibility(View.GONE);
                                adapter = new FavoriteListAdapter(FavoriteList.this, itemList);
                                listview.setAdapter(adapter);
                            } else {
                                listview.setVisibility(View.GONE);
                                Rl_empty.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyErrorResponse.volleyError(FavoriteList.this, error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", Iconstant.cabily_userAgent);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("user_id", UserID);
                return jsonParams;
            }
        };
        postrequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postrequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(postrequest);*/
    }


    //-----------------------Favourite List Delete Post Request-----------------
    private void postRequest_FavoriteDelete(String Url, final String locationKey, final int position) {
        dialog = new Dialog(FavoriteList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_deleting));
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("location_key", locationKey);

        System.out.println("-------------Favourite Delete Url----------------" + Url);
        mRequest = new ServiceRequest(FavoriteList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Favourite Delete Response----------------" + response);

                String Sstatus = "", Smessage = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        //removing the deleted position from listView
                        itemList.remove(position);
                        adapter.notifyDataSetChanged();

                        //code to show empty layout
                        if (itemList.size() == 0) {
                            listview.setVisibility(View.GONE);
                            Rl_empty.setVisibility(View.VISIBLE);
                        }

                        Alert(getResources().getString(R.string.action_success), Smessage);

                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Smessage);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dialog.dismiss();
            }



        @Override
        public void onErrorListener() {

        }
    });





        /*deleteRequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("-------------Favourite Delete Response----------------" + response);

                        String Sstatus = "", Smessage = "";

                        try {
                            JSONObject object = new JSONObject(response);
                            Sstatus = object.getString("status");
                            Smessage = object.getString("message");

                            if (Sstatus.equalsIgnoreCase("1")) {
                                //removing the deleted position from listView
                                itemList.remove(position);
                                adapter.notifyDataSetChanged();

                                //code to show empty layout
                                if (itemList.size() == 0) {
                                    listview.setVisibility(View.GONE);
                                    Rl_empty.setVisibility(View.VISIBLE);
                                }

                                Alert(getResources().getString(R.string.action_success), Smessage);

                            } else {
                                Alert(getResources().getString(R.string.alert_label_title), Smessage);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyErrorResponse.volleyError(FavoriteList.this, error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", Iconstant.cabily_userAgent);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("user_id", UserID);
                jsonParams.put("location_key", locationKey);
                return jsonParams;
            }
        };
        deleteRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        deleteRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(deleteRequest);*/
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
    public void onDestroy() {
        unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

}
