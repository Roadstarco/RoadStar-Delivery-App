package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.adapter.PlaceSearchAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.EstimateDetailPojo;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Prem Kumar on 10/7/2015.
 */
public class EstimatePage extends Activity {

    private RelativeLayout back;
    private EditText et_search;
    private ListView listview;
    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private TextView tv_emptyText;
    private ProgressBar progresswheel;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    private ServiceRequest mRequest;
    private ServiceRequest estimate_mRequest;
    Context context;
    ArrayList<String> itemList_location = new ArrayList<String>();
    ArrayList<String> itemList_placeId = new ArrayList<String>();

    PlaceSearchAdapter adapter;
    private boolean isdataAvailable = false;
    private boolean isEstimateAvailable = false;

    private String Slatitude = "", Slongitude = "", Sdrop_location = "";
    private String Suserid = "", Spickup = "", Spickup_lat = "", Spickup_long = "", Scategory = "", Stype = "", Spickup_date = "", Spickup_time = "";

    Dialog dialog;
    ArrayList<EstimateDetailPojo> ratecard_list = new ArrayList<EstimateDetailPojo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estimate_price);
        context = getApplicationContext();
        initialize();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sdrop_location = itemList_location.get(position);

                cd = new ConnectionDetector(EstimatePage.this);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    LatLongRequest(Iconstant.GetAddressFrom_LatLong_url + itemList_placeId.get(position));
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }

            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                cd = new ConnectionDetector(EstimatePage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }
                    CitySearchRequest(Iconstant.place_search_url + et_search.getText().toString().toLowerCase().replace("%","").replace(" ","%20")+"&radius=500&location="+Spickup_lat+","+Spickup_long);
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                }

            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(et_search);
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                EstimatePage.this.finish();
                EstimatePage.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        et_search.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(et_search, 0);
            }
        },200);
    }

    private void initialize() {
        alert_layout = (RelativeLayout) findViewById(R.id.estimate_price_alert_layout);
        alert_textview = (TextView) findViewById(R.id.estimate_price_alert_textView);
        back = (RelativeLayout) findViewById(R.id.estimate_price_back_layout);
        et_search = (EditText) findViewById(R.id.estimate_price_editText);
        listview = (ListView) findViewById(R.id.estimate_price_listView);
        progresswheel = (ProgressBar) findViewById(R.id.estimate_price_progressBar);
        tv_emptyText = (TextView) findViewById(R.id.estimate_price_empty_textview);

        et_search.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Intent i = getIntent();
        if (i != null) {
            Suserid = i.getStringExtra("UserId");
            Spickup = i.getStringExtra("PickUp");
            Spickup_lat = i.getStringExtra("PickUp_Lat");
            Spickup_long = i.getStringExtra("PickUp_Long");
            Scategory = i.getStringExtra("Category");
            Stype = i.getStringExtra("Type");
            Spickup_date = i.getStringExtra("PickUp_Date");
            Spickup_time = i.getStringExtra("PickUp_Time");

        }
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(EstimatePage.this);
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

    //-------------------Search Place Request----------------
    private void CitySearchRequest(String Url) {

        progresswheel.setVisibility(View.VISIBLE);
        System.out.println("--------------Search city url-------------------" + Url);

        mRequest = new ServiceRequest(EstimatePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Search city  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONArray place_array = object.getJSONArray("predictions");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_array.length() > 0) {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                for (int i = 0; i < place_array.length(); i++) {
                                    JSONObject place_object = place_array.getJSONObject(i);
                                    itemList_location.add(place_object.getString("description"));
                                    itemList_placeId.add(place_object.getString("place_id"));
                                }
                                isdataAvailable = true;
                            } else {
                                itemList_location.clear();
                                itemList_placeId.clear();
                                isdataAvailable = false;
                            }
                        } else {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);
                if (isdataAvailable) {
                    tv_emptyText.setVisibility(View.GONE);
                } else {
                    tv_emptyText.setVisibility(View.VISIBLE);
                }
                adapter = new PlaceSearchAdapter(EstimatePage.this, itemList_location);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorListener() {
                progresswheel.setVisibility(View.INVISIBLE);
                alert_layout.setVisibility(View.GONE);

                // close keyboard
                CloseKeyboard(et_search);
            }
        });
    }


    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
    private void LatLongRequest(String Url) {

        dialog = new Dialog(EstimatePage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new ServiceRequest(EstimatePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.GET, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------LatLong  reponse-------------------" + response);
                String status = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {

                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {
                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (isdataAvailable) {


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sdrop_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    finish();




        //            EstimatePriceRequest(Iconstant.estimate_price_url);







                } else {
                    dialog.dismiss();
                    Alert(getResources().getString(R.string.alert_label_title), status);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    //-------------------Estimate Price Request----------------
    private void EstimatePriceRequest(String Url) {
        System.out.println("--------------Estimate url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Suserid);
        jsonParams.put("pickup", Spickup);
        jsonParams.put("drop", Sdrop_location);
        jsonParams.put("pickup_lat", Spickup_lat);
        jsonParams.put("pickup_lon", Spickup_long);
        jsonParams.put("drop_lat", Slatitude);
        jsonParams.put("drop_lon", Slongitude);
        jsonParams.put("category", Scategory);
        jsonParams.put("type", Stype);
        jsonParams.put("pickup_date", Spickup_date);
        jsonParams.put("pickup_time", Spickup_time);

        estimate_mRequest = new ServiceRequest(EstimatePage.this);
        estimate_mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Estimate  reponse-------------------" + response);
                String status = "", ScurrencyCode = "", Spickup = "", Sdrop = "", Smin_amount = "", Smax_amount = "", SapproxTime = "", SpeakTime = "", SnightCharge = "", Snote = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject response_object = object.getJSONObject("response");
                            if (response_object.length() > 0) {
                                ScurrencyCode = response_object.getString("currency");
                                JSONObject eta_object = response_object.getJSONObject("eta");
                                if (eta_object.length() > 0) {
                                    Spickup = eta_object.getString("pickup");
                                    Sdrop = eta_object.getString("drop");
                                    Smin_amount = eta_object.getString("min_amount");
                                    Smax_amount = eta_object.getString("max_amount");
                                    SapproxTime = eta_object.getString("att");
                                    SpeakTime = eta_object.getString("peak_time");
                                    SnightCharge = eta_object.getString("night_charge");
                                    Snote = eta_object.getString("note");
                                }

                                JSONObject ratecard_object = response_object.getJSONObject("ratecard");
                                if (ratecard_object.length() > 0) {
                                    ratecard_list.clear();
                                    EstimateDetailPojo pojo = new EstimateDetailPojo();

                                    pojo.setRate_note(ratecard_object.getString("note"));
                                    pojo.setCurrencyCode(response_object.getString("currency"));
                                    pojo.setRate_cartype(eta_object.getString("catrgory_name"));

                                    JSONObject farebreakup_object = ratecard_object.getJSONObject("farebreakup");
                                    if (farebreakup_object.length() > 0) {
                                        JSONObject minfare_object = farebreakup_object.getJSONObject("min_fare");
                                        if (minfare_object.length() > 0) {
                                            pojo.setMinfare_amt(minfare_object.getString("amount"));
                                            pojo.setMinfare_km(minfare_object.getString("text"));
                                        }

                                        JSONObject afterfare_object = farebreakup_object.getJSONObject("after_fare");
                                        if (afterfare_object.length() > 0) {
                                            pojo.setAfterfare_amt(afterfare_object.getString("amount"));
                                            pojo.setAfterfare_km(afterfare_object.getString("text"));
                                        }

                                        JSONObject otherfare_object = farebreakup_object.getJSONObject("other_fare");
                                        if (otherfare_object.length() > 0) {
                                            pojo.setOtherfare_amt(otherfare_object.getString("amount"));
                                            pojo.setOtherfare_km(otherfare_object.getString("text"));
                                        }
                                    }


                                    ratecard_list.add(pojo);
                                }

                                isEstimateAvailable = true;
                            } else {
                                isEstimateAvailable = false;
                            }
                        } else {
                            isEstimateAvailable = false;
                        }
                    } else {
                        isEstimateAvailable = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isEstimateAvailable) {
                    finish();
                    Intent intent = new Intent(EstimatePage.this, EstimateDetailPage.class);
                    intent.putExtra("CurrencyCode", ScurrencyCode);
                    intent.putExtra("PickUp", Spickup);
                    intent.putExtra("Drop", Sdrop);
                    intent.putExtra("MinPrice", Smin_amount);
                    intent.putExtra("MaxPrice", Smax_amount);
                    intent.putExtra("ApproxPrice", SapproxTime);
                    intent.putExtra("PeakTime", SpeakTime);
                    intent.putExtra("NightCharge", SnightCharge);
                    intent.putExtra("Note", Snote);
                    intent.putExtra("RateCard", new EstimateDetailPojo("", ratecard_list));
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.estimate_price_label_not_found));
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

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            EstimatePage.this.finish();
            EstimatePage.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }
}
