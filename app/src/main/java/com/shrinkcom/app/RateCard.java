package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shrinkcom.adapter.RateCardAdapter;
import com.shrinkcom.adapter.RateCardStandardAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.RateCard_CarPojo;
import com.shrinkcom.pojo.RateCard_CardDisplayPojo;
import com.shrinkcom.pojo.RateCard_CityPojo;
import com.shrinkcom.pojo.RateCard_StdPojo;
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

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by Prem Kumar and Anitha on 10/14/2015.
 */
public class RateCard extends Activity {
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private MaterialSpinner city_spinner;
    private MaterialSpinner carType_spinner;
    private ExpandableHeightListView listview, standard_listview;
    private RelativeLayout rate_display_layout;

    private String Sselected_cityID = "";
    private ServiceRequest mRequest, car_mRequest, rateCard_mRequest;
    Dialog dialog;
    private boolean isCityAvailable = false;
    private boolean isCarAvailable = false;
    ArrayList<String> city_array = new ArrayList<String>();
    ArrayList<String> car_array = new ArrayList<String>();
    ArrayList<RateCard_CityPojo> city_itemList;
    ArrayList<RateCard_CarPojo> car_itemList;
    ArrayList<RateCard_CardDisplayPojo> rate_itemList;
    ArrayList<RateCard_StdPojo> stdrate_itemList;

    private String SfirstKm = "", SafterKm = "";
    private String SfirstKm_fare = "", SafterKm_fare = "";
    RateCardAdapter adapter;
    RateCardStandardAdapter standardAdapter;
    ArrayAdapter<String> city_adapter;
    ArrayAdapter<String> car_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratecard_page);
        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(RateCard.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                city_spinner.setFloatingLabelText(getResources().getString(R.string.ratecard_lable_selected_city_hint));
                cd = new ConnectionDetector(RateCard.this);
                isInternetPresent = cd.isConnectingToInternet();

                rate_display_layout.setVisibility(View.GONE);

                rate_itemList.clear();
                stdrate_itemList.clear();
                car_itemList.clear();
                car_array.clear();
                if (standardAdapter != null) {
                    standardAdapter.notifyDataSetChanged();
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                if (car_adapter != null) {
                    car_adapter = new ArrayAdapter<String>(RateCard.this,
                            R.layout.ratecard_city_spinner_dropdown, car_array);
                    carType_spinner.setAdapter(car_adapter);
                }

                if (!city_spinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.ratecard_lable_city))) {
                    if (isInternetPresent) {
                        if (car_mRequest != null) {
                            car_mRequest.cancelRequest();
                        }
                        Sselected_cityID = city_itemList.get(position).getCity_id();
                        postRequest_CarSelect(Iconstant.ratecard_select_cartype_url, city_itemList.get(position).getCity_id());
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                carType_spinner.setFloatingLabelText(getResources().getString(R.string.ratecard_lable_selected_car_hint));
                cd = new ConnectionDetector(RateCard.this);
                isInternetPresent = cd.isConnectingToInternet();

                rate_display_layout.setVisibility(View.GONE);

                rate_itemList.clear();
                stdrate_itemList.clear();
                if (standardAdapter != null) {
                    standardAdapter.notifyDataSetChanged();
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                if (!carType_spinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.ratecard_lable_carType))) {
                    if (isInternetPresent) {
                        if (rateCard_mRequest != null) {
                            rateCard_mRequest.cancelRequest();
                        }
                        rateCard_displayRequest(Iconstant.ratecard_display_url, Sselected_cityID, car_itemList.get(position).getCar_id());
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        listview.setExpanded(true);
        standard_listview.setExpanded(true);
    }

    private void initialize() {

        session = new SessionManager(RateCard.this);
        cd = new ConnectionDetector(RateCard.this);
        isInternetPresent = cd.isConnectingToInternet();
        city_itemList = new ArrayList<RateCard_CityPojo>();
        car_itemList = new ArrayList<RateCard_CarPojo>();
        rate_itemList = new ArrayList<RateCard_CardDisplayPojo>();
        stdrate_itemList = new ArrayList<RateCard_StdPojo>();

        city_spinner = (MaterialSpinner) findViewById(R.id.ratecard_city_spinner);
        carType_spinner = (MaterialSpinner) findViewById(R.id.ratecard_cartype_spinner);
        standard_listview = (ExpandableHeightListView) findViewById(R.id.ratecard_standardRate_listView);
        listview = (ExpandableHeightListView) findViewById(R.id.ratecard_listView);
        back = (RelativeLayout) findViewById(R.id.ratecard_header_back_layout);
        rate_display_layout = (RelativeLayout) findViewById(R.id.ratecard_ratedisplay_layout);

        rate_display_layout.setVisibility(View.GONE);

        if (isInternetPresent) {
            postRequest_CitySelect(Iconstant.ratecard_select_city_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(RateCard.this);
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


    //-----------------------City Select Post Request-----------------
    private void postRequest_CitySelect(String Url) {
        dialog = new Dialog(RateCard.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------CitySelect Url----------------" + Url);

        mRequest = new ServiceRequest(RateCard.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, null, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------CitySelect Response----------------" + response);

                String Sstatus = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_locations_object = response_object.get("locations");
                            if (check_locations_object instanceof JSONArray) {
                                JSONArray location_array = response_object.getJSONArray("locations");
                                if (location_array.length() > 0) {
                                    city_array.clear();
                                    city_itemList.clear();
                                    for (int i = 0; i < location_array.length(); i++) {
                                        JSONObject location_object = location_array.getJSONObject(i);
                                        RateCard_CityPojo city_pojo = new RateCard_CityPojo();
                                        city_pojo.setCity_id(location_object.getString("id"));
                                        city_pojo.setCity_name(location_object.getString("city"));

                                        city_array.add(location_object.getString("city"));

                                        city_itemList.add(city_pojo);
                                    }

                                    isCityAvailable = true;
                                } else {
                                    isCityAvailable = false;
                                }
                            }
                        } else {
                            isCityAvailable = false;
                        }
                    } else {
                        isCityAvailable = false;
                    }


                    if (Sstatus.equalsIgnoreCase("1") && isCityAvailable) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RateCard.this,
                                R.layout.ratecard_city_spinner_dropdown, city_array);
                        city_spinner.setAdapter(adapter);
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


    //-----------------------Car Type Select Post Request-----------------
    private void postRequest_CarSelect(String Url, final String location_id) {
        dialog = new Dialog(RateCard.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------CarSelect Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("location_id", location_id);

        car_mRequest = new ServiceRequest(RateCard.this);
        car_mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------CarSelect Response----------------" + response);

                String Sstatus = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_category_object = response_object.get("category");
                            if (check_category_object instanceof JSONArray) {
                                JSONArray location_array = response_object.getJSONArray("category");
                                if (location_array.length() > 0) {
                                    car_array.clear();
                                    car_itemList.clear();
                                    for (int i = 0; i < location_array.length(); i++) {
                                        JSONObject location_object = location_array.getJSONObject(i);
                                        RateCard_CarPojo car_pojo = new RateCard_CarPojo();
                                        car_pojo.setCar_id(location_object.getString("id"));
                                        car_pojo.setCat_type(location_object.getString("category"));

                                        car_array.add(location_object.getString("category"));
                                        car_itemList.add(car_pojo);
                                    }

                                    isCarAvailable = true;
                                } else {
                                    isCarAvailable = false;
                                }
                            }
                        } else {
                            isCarAvailable = false;
                        }
                    } else {
                        isCarAvailable = false;
                    }


                    if (Sstatus.equalsIgnoreCase("1") && isCarAvailable) {

                        carType_spinner.setEnabled(true);
                        carType_spinner.setClickable(true);

                        car_adapter = new ArrayAdapter<String>(RateCard.this,
                                R.layout.ratecard_city_spinner_dropdown, car_array);
                        carType_spinner.setAdapter(car_adapter);
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


    //-----------------------Rate Card Display Post Request-----------------
    private void rateCard_displayRequest(String Url, final String location_id, final String category_id) {
        dialog = new Dialog(RateCard.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------Rate Card Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("location_id", location_id);
        jsonParams.put("category_id", category_id);

        rateCard_mRequest = new ServiceRequest(RateCard.this);
        rateCard_mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Rate Card Response----------------" + response);

                String Sstatus = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_ratecard_object = response_object.get("ratecard");
                            if (check_ratecard_object instanceof JSONObject) {

                                JSONObject ratecard_object = response_object.getJSONObject("ratecard");
                                if (ratecard_object.length() > 0) {
                                 //   Currency currencycode = Currency.getInstance(getLocale(ratecard_object.getString("currency")));
                                    String   ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ratecard_object.getString("currency"));

                                    Object check_standard_rate_object = ratecard_object.get("standard_rate");
                                    if (check_standard_rate_object instanceof JSONArray) {
                                        JSONArray standard_array = ratecard_object.getJSONArray("standard_rate");
                                        if (standard_array.length() > 0) {
                                            stdrate_itemList.clear();
                                            for (int i = 0; i < standard_array.length(); i++) {
                                                JSONObject standard_object = standard_array.getJSONObject(i);
                                                RateCard_StdPojo stdrate_pojo = new RateCard_StdPojo();
                                                stdrate_pojo.setStdrate_title(standard_object.getString("title"));
                                                stdrate_pojo.setStdrate_sub_title(standard_object.getString("sub_title"));
                                                stdrate_pojo.setStdrate_fare(ScurrencySymbol+ standard_object.getString("fare"));
                                                stdrate_pojo.setStdrate_currencySymbol(ScurrencySymbol);

                                                stdrate_itemList.add(stdrate_pojo);
                                            }
                                        }
                                    }


                                    Object check_extra_charges_object = ratecard_object.get("extra_charges");
                                    if (check_extra_charges_object instanceof JSONArray) {

                                        JSONArray extra_array = ratecard_object.getJSONArray("extra_charges");
                                        if (extra_array.length() > 0) {
                                            rate_itemList.clear();
                                            for (int j = 0; j < extra_array.length(); j++) {
                                                JSONObject extra_object = extra_array.getJSONObject(j);
                                                RateCard_CardDisplayPojo rate_pojo = new RateCard_CardDisplayPojo();
                                                rate_pojo.setRate_title(extra_object.getString("title"));
                                                rate_pojo.setRate_sub_title(extra_object.getString("sub_title"));
                                                if(extra_object.getString("has_unit").length() > 0){
                                                    rate_pojo.setRate_fare(extra_object.getString("fare")+extra_object.getString("has_unit"));

                                                }else{

                                                    if (("".equalsIgnoreCase(extra_object.getString("fare"))) || (extra_object.getString("fare").length() <= 0)){
                                                        rate_pojo.setRate_fare("");
                                                    }else {
                                                        rate_pojo.setRate_fare(ScurrencySymbol + extra_object.getString("fare"));
                                                    }
                                                }
//                                                rate_pojo.setRate_fare(extra_object.getString("fare"));
                                                rate_pojo.setRate_currencySymbol(ScurrencySymbol);

                                                rate_itemList.add(rate_pojo);
                                            }
                                        }
                                    }

                                    isCarAvailable = true;
                                } else {
                                    isCarAvailable = false;
                                }
                            }
                        } else {
                            isCarAvailable = false;
                        }
                    } else {
                        isCarAvailable = false;
                    }




                    if (Sstatus.equalsIgnoreCase("1") && isCarAvailable) {

                        rate_display_layout.setVisibility(View.VISIBLE);

                        if (stdrate_itemList.size() > 0) {
                            standardAdapter = new RateCardStandardAdapter(RateCard.this, stdrate_itemList);
                            standard_listview.setAdapter(standardAdapter);
                        }

                       /* if (rate_itemList.size() > 0) {
                            adapter = new RateCardAdapter(RateCard.this, rate_itemList);
                            listview.setAdapter(adapter);
                        }*/
                    } else {
                        rate_itemList.clear();
                        String Sresponse = object.getString("response");
                        Alert(getResources().getString(R.string.alert_label_title), Sresponse);
                    }
                    adapter = new RateCardAdapter(RateCard.this, rate_itemList);
                    listview.setAdapter(adapter);



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
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }
}
