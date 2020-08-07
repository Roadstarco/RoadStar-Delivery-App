package com.shrinkcom.app;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;

import com.android.volley.VolleyError;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;
import com.shrinkcom.adapter.BookMyRide_Adapter;
import com.shrinkcom.adapter.GalleryAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.HomePojo;
import com.shrinkcom.utils.HorizontalListView;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.volley.VolleyMultipartRequest;
import com.shrinkcom.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPickupInfoActivity extends AppCompatActivity {

    LinearLayout layout_pickupaddress;
    LinearLayout layout_deliveryaddress;

    int FLAGESTAPER=0;


    TextView btn_update;
    ImageView back;

    Context mContext;
    private EditText _company_name;
    private EditText _User_name;
    private EditText countrycode_phone;
    private EditText _phone;
    private EditText _emailid;
    private EditText _address1;
    private EditText _address2;
    private Spinner _city;
    private Spinner _state;
    private EditText _postalcode;
    private Spinner _country;
    List<String> countryidlist;
    List<String> countrynamelist;
    List<String> countrycodelist;
    List<String> countryphonecodelist;
    int country_prompt_position;
    List<String> statenamelist;
    List<String> stateidlist;
    List<String> citynamelist;
    String strcountry,strstate,strcity,strphonecode;


    // TODO: 8/6/2020   declare delivery info variavble

    TextView btn_submit;
    Spinner spn_category, spn_subcategory;
    private int GALLERY = 1;
    EditText edt_company_name;
    EditText edt_username;
    EditText edt_phonecode;
    EditText edt_phone;
    EditText edt_deloveryaddress;
    EditText edt_deliverylocation;
    EditText edt_parcelheight;
    EditText edt_parcelwidth;
    EditText edt_parcelweight;
    TextView tv_add_images;
    HorizontalListView recyclerViewvehicletype;
    private Handler mapHandler = new Handler();
    SessionManager sessionManager;


    Map<String,String> params;

    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    Dialog dialog;
    private ServiceRequest mRequest;

    List<String> categorynamelist;
    List<String> categoryidlist;

    List<String> subcategorynamelist;
    List<String> subcategoryidlist;
    ArrayList<HomePojo> driver_list = new ArrayList<HomePojo>();
    ArrayList<HomePojo> category_list = new ArrayList<HomePojo>();

    SessionManager session;
    public  static   String CategoryID;
    String UserID;

    ArrayList<Uri> mArrayUri;
    String parcelcategory_id,parcelsubcategory_id;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pickup_info);
        session =  new SessionManager(this);
        mContext = this;
        initview();
        back = findViewById(R.id.back);
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getdata();

            }
        });

        countrySpinner();

        imagesEncodedList = new ArrayList<>();
        getcategory(Iconstant.GETCATEGORY);

        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        HashMap<String, String> cat = session.getCategoryID();
        String sCategoryId = cat.get(SessionManager.KEY_CATEGORY_ID);


        if (sCategoryId.length() > 0) {
            CategoryID = cat.get(SessionManager.KEY_CATEGORY_ID);
        } else {
            CategoryID = user.get(SessionManager.KEY_CATEGORY);
        }



        PostRequest(Iconstant.BookMyRide_url,session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LAT),session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LON));



    }


    void initview() {

        layout_pickupaddress = findViewById(R.id.id_layout_pick_info);
        layout_deliveryaddress = findViewById(R.id.idlayout_delivery_info);

        _company_name = findViewById(R.id.edt_companyname);
        _User_name = findViewById(R.id.edt_name);
        countrycode_phone = findViewById(R.id.edt_phcode);
        _phone = findViewById(R.id.edt_phone);
        _emailid = findViewById(R.id.edt_email);
        _address1 = findViewById(R.id.edt_addres1);
        _address2 = findViewById(R.id.edt_addres2);
        _country = findViewById(R.id.edt_country);
        _state = findViewById(R.id.edt_stat);
        _postalcode = findViewById(R.id.edt_postalcode);
        _city = findViewById(R.id.edt_city);

        countrycode_phone.setOnKeyListener(null);



        edt_company_name = findViewById(R.id.edt_companynamedelivery);
        edt_username = findViewById(R.id.edt_namedelivery);
        edt_phonecode = findViewById(R.id.edt_phcodedelivery);
        edt_phone = findViewById(R.id.edt_phonedelivery);
        edt_deloveryaddress = findViewById(R.id.edt_pickadddelivery);
        edt_deliverylocation = findViewById(R.id.edt_locationdelivery);
        edt_parcelheight = findViewById(R.id.edt_parcelheightdelivery);
        edt_parcelwidth = findViewById(R.id.edt_parcelwidthdelivery);
        edt_parcelweight = findViewById(R.id.edt_parcelweightdelivery);
        tv_add_images = findViewById(R.id.uploadImage);
        gvGallery = findViewById(R.id.recycleimage);
        recyclerViewvehicletype = findViewById(R.id.idrecyclevehicletype);
        spn_category = findViewById(R.id.spn_categorydelivery);
        spn_subcategory = findViewById(R.id.spn_subcategorydelivery);

        btn_submit = findViewById(R.id.btn_submit);
        back = findViewById(R.id.back);


        edt_phonecode.setOnKeyListener(null);
        edt_deliverylocation.setOnKeyListener(null);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 8/6/2020  submited data from for booking


            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( FLAGESTAPER ==0) {
                    onBackPressed();
                }else {
                    FLAGESTAPER = 0;
                    layout_pickupaddress.setVisibility(View.VISIBLE);
                    layout_deliveryaddress.setVisibility(View.GONE);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });


      //  edt_deliverylocation.setText(""+sessionManager.getStringValue(SessionManager.KEY_DROP_LOCATION));


        tv_add_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);

            }
        });


        // TODO: 7/13/2020  here use click event from list view

        recyclerViewvehicletype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("NANANAN",">>> "+category_list.get(i).getCat_name());
                Toast.makeText(mContext, "You Select  "+category_list.get(i).getCat_name(), Toast.LENGTH_SHORT).show();

                CategoryID = category_list.get(i).getCat_id();
            }
        });











    }


    boolean validation(){
        boolean valid = true;

        String strcompanyname = _company_name.getText().toString();
        String strusername = _User_name.getText().toString();
        String strphone = _phone.getText().toString();
        String stremail = _emailid.getText().toString();
        String straddress = _address1.getText().toString();

        if (strcompanyname.isEmpty()){
            _company_name.setError("Enter Company Name");
            valid = false;
        }else {
            _company_name.setError(null);
        }

        if (strusername.isEmpty()){
            _User_name.setError("Enter User Name");
            valid = false;
        }else {
            _User_name.setError(null);
        }

        if (strphone.length()<10){
            _phone.setError("Enter valid mobile no");
            valid = false;
        }else {
            _phone.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(stremail).matches()){
            _emailid.setError("Enter valid email address");
            valid = false;
        }else {
            _emailid.setError(null);
        }
        if (straddress.isEmpty()){
            _address1.setError("Enter address");
            valid = false;
        }else {
            _address1.setError(null);
        }

        return valid;
    }





    void getdata(){

       /* if (!validation()){
            return;
        }*/

        String strcompanyname = _company_name.getText().toString();
        String strusername = _User_name.getText().toString();
        String strphone = _phone.getText().toString();
        String stremail = _emailid.getText().toString();
        String straddress1 = _address1.getText().toString();
        String straddress2 = _address2.getText().toString();
        String strpostalcode = _postalcode.getText().toString();

        SessionManager sessionManager = new SessionManager(mContext);
        sessionManager.setStringValue(SessionManager.KEY_PICK_COMPANYNAME,strcompanyname);
        sessionManager.setStringValue(SessionManager.KEY_PICK_USERNAME,strusername);
        sessionManager.setStringValue(SessionManager.KEY_PICK_PHONECODE,strphonecode);
        sessionManager.setStringValue(SessionManager.KEY_PICK_PHONE,strphone);
        sessionManager.setStringValue(SessionManager.KEY_PICK_EMAILID,stremail);
        sessionManager.setStringValue(SessionManager.KEY_PICK_ADDRESS1,straddress1);
        sessionManager.setStringValue(SessionManager.KEY_PICK_ADDRESS2,straddress2);
        sessionManager.setStringValue(SessionManager.KEY_PICK_POSTCODE,strpostalcode);
        sessionManager.setStringValue(SessionManager.KEY_PICK_COUNTRY,strcountry);
        sessionManager.setStringValue(SessionManager.KEY_PICK_STATE,strstate);
        sessionManager.setStringValue(SessionManager.KEY_PICK_CITY,strcity);


        FLAGESTAPER = 1;
        layout_pickupaddress.setVisibility(View.GONE);
        layout_deliveryaddress.setVisibility(View.VISIBLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);

    }


    // TODO: 8/6/2020  validation delivery info


    boolean validation_deliveryinfo(){
        boolean valid = true;

        String strcompanyname = edt_company_name.getText().toString();
        String strusername = edt_username.getText().toString();
        String strphonecode = edt_phonecode.getText().toString();
        String strphone = edt_phone.getText().toString();
        String strdeliveryaddress = edt_deloveryaddress.getText().toString();
        String strdeliverylocation = edt_deliverylocation.getText().toString();
        String strheight = edt_parcelheight.getText().toString();
        String strwidth = edt_parcelwidth.getText().toString();
        String strweight = edt_parcelweight.getText().toString();

        if (strcompanyname.isEmpty()){
            edt_company_name.setError("Enter Company Name");
            valid = false;
        }else {
            edt_company_name.setError(null);
        }
        if (strusername.isEmpty()){
            edt_username.setError("Enter Name");
            valid = false;
        }else {
            edt_username.setError(null);
        }
        if (strphone.length()<10){
            edt_phone.setError("Enter Phone");
            valid = false;
        }else {
            edt_phone.setError(null);
        }

        if (strdeliveryaddress.isEmpty()){
            edt_deloveryaddress.setError("Enter Delivery Address");
            valid = false;
        }else {
            edt_deloveryaddress.setError(null);
        }
        if (strheight.isEmpty()){
            edt_parcelheight.setError("Enter Height");
            valid = false;
        }else {
            edt_parcelheight.setError(null);
        }
        if (strwidth.isEmpty()){
            edt_parcelwidth.setError("Enter Width");
            valid = false;
        }else {
            edt_parcelwidth.setError(null);
        }
        if (strweight.isEmpty()){
            edt_parcelweight.setError("Enter Weight");
            valid = false;
        }else {
            edt_parcelweight.setError(null);
        }


        return valid;

    }



    void getData_deliveryInfo() {
        Log.e("VALIDATIONNN",">>>> "+validation());
        if (!validation()){
            return;
        }
        String strcompanyname = edt_company_name.getText().toString();
        String strusername = edt_username.getText().toString();
        String strphonecode = edt_phonecode.getText().toString();
        String strphone = edt_phone.getText().toString();
        String strdeliveryaddress = edt_deloveryaddress.getText().toString();
        String strdeliverylocation = edt_deliverylocation.getText().toString();
        String strheight = edt_parcelheight.getText().toString();
        String strwidth = edt_parcelwidth.getText().toString();
        String strweight = edt_parcelweight.getText().toString();






        //image,
params = new HashMap<>();
        params.put("user_id",""+UserID);
        params.put("vehicle_type",""+CategoryID);
        params.put("type","1");
        params.put("booking_type",""+session.getStringValue(SessionManager.KEY_SERVICETYPE));
        params.put("pickup",""+session.getStringValue(SessionManager.KEY_PICKUP_LOCATION));
        params.put("pickup_lat",""+session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LAT));
        params.put("pickup_lon",""+session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LON));
        params.put("company_name",""+session.getStringValue(SessionManager.KEY_PICK_COMPANYNAME));
        params.put("name",""+session.getStringValue(SessionManager.KEY_PICK_USERNAME));
        params.put("phone",""+session.getStringValue(SessionManager.KEY_PICK_PHONE));
        params.put("email",""+session.getStringValue(SessionManager.KEY_PICK_EMAILID));
        params.put("address1",""+session.getStringValue(SessionManager.KEY_PICK_ADDRESS1));
        params.put("address2",""+session.getStringValue(SessionManager.KEY_PICK_ADDRESS2));
        params.put("city",""+session.getStringValue(SessionManager.KEY_PICK_CITY));
        params.put("state",""+session.getStringValue(SessionManager.KEY_PICK_STATE));
        params.put("postal_code",""+session.getStringValue(SessionManager.KEY_PICK_POSTCODE));
        params.put("country",""+session.getStringValue(SessionManager.KEY_PICK_COUNTRY));



        params.put("drop_loc",""+session.getStringValue(SessionManager.KEY_DROP_LOCATION));
        params.put("drop_lat",""+session.getStringValue(SessionManager.KEY_DROP_LOCATION_LAT));
        params.put("drop_lon",""+session.getStringValue(SessionManager.KEY_DROP_LOCATION_LON));
        params.put("drop_company_name",""+strcompanyname);
        params.put("drop_user_name",""+strusername);
        params.put("drop_user_phone",""+strphone);
        params.put("drop_address1",""+strdeliveryaddress);
        params.put("drop_address2",""+strdeliverylocation);
        params.put("category_id",""+parcelcategory_id);
        params.put("sub_category_id",""+parcelsubcategory_id);
        params.put("height",""+strheight);
        params.put("width",""+strwidth);
        params.put("weight",""+strweight);



        if (imagesEncodedList.size()>0) {
            EstimatePriceRequest(Iconstant.estimate_price_url);
        }else {
            Alert("Alert","Please Select Image first");
        }
    }







    // TODO: 7/3/2020  setCountryspinner

    void countrySpinner() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        countrycodelist = new ArrayList<>();
        countryidlist = new ArrayList<>();
        countrynamelist = new ArrayList<>();
        countryphonecodelist = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(readJSONFromAsset("countries.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                countryidlist.add(object.getString("id"));
                countrycodelist.add(object.getString("sortname"));
                countrynamelist.add(object.getString("name"));
                countryphonecodelist.add(object.getString("phoneCode"));
                if (countryCodeValue.equalsIgnoreCase(object.getString("sortname"))) {
                    country_prompt_position = i;
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, R.layout.layout_view_spinner, R.id.idtext, countrynamelist);
            _country.setAdapter(arrayAdapter);
            _country.setSelection(country_prompt_position);
            arrayAdapter.notifyDataSetChanged();

            _country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    countrycode_phone.setText("+"+countryphonecodelist.get(i));
                    String countryid = countryidlist.get(i);
                    strcountry = countrynamelist.get(i);
                    strphonecode = countryphonecodelist.get(i);
                    setStateSpinner(countryid);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: 7/3/2020  ststatespinner

    void setStateSpinner(String strcountryid) {
        statenamelist = new ArrayList<>();
        stateidlist = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(readJSONFromAsset("states.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                if (strcountryid.equals(object.getString("country_id"))) {
                    statenamelist.add(object.getString("name"));
                    stateidlist.add(object.getString("id"));
                }

            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, R.layout.layout_view_spinner, R.id.idtext, statenamelist);
            _state.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();

            _state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    strstate = statenamelist.get(i);
                    String stateid = stateidlist.get(i);
                    setCitySpinner(stateid);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    // TODO: 7/3/2020  setCitySpinner
    void setCitySpinner(String stateid) {
        citynamelist  = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(readJSONFromAsset("cities.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("cities");


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                if (stateid.equals(object.getString("state_id"))) {
                    citynamelist.add(object.getString("name"));
                }

            }


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, R.layout.layout_view_spinner, R.id.idtext, citynamelist);
            _city.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();

            _city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    strcity = citynamelist.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String readJSONFromAsset(String jsonname) {
        String json = null;
        try {
            InputStream is = getAssets().open(jsonname);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    // TODO: 8/6/2020  get category here


    private void getcategory(String Url) {

        dialog = new Dialog(mContext);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView loading = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        loading.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("--------------Confirm Ride url-------------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        System.out.println("--------------Confirm Ride jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(mContext);
        mRequest.makegetRequest(Url, Request.Method.GET, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------CAtegoryresponse-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);

                    categorynamelist = new ArrayList<>();
                    categoryidlist = new ArrayList<>();

                    if (object.getInt("status") == 1) {
                        JSONObject object1 = object.getJSONObject("response");

                        JSONArray jsonArray = object1.getJSONArray("category");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object2 = jsonArray.getJSONObject(i);
                                String catid = object2.getString("id");
                                String catename = object2.getString("category");
                                String categoryimage = object2.getString("image");
                                categorynamelist.add(catename);
                                categoryidlist.add(catid);
                            }
                        }
                    }

                    ArrayAdapter<String> categoryadaptor = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, categorynamelist);
                    spn_category.setAdapter(categoryadaptor);
                    categoryadaptor.notifyDataSetChanged();

                    spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            // TODO: 7/4/2020  call subcatrgory

                            parcelcategory_id  = categoryidlist.get(i);
                            getSubcategory(Iconstant.GETSUBCATEGORY, categoryidlist.get(i));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
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


    // TODO: 8/6/2020  get Subcategory here


    private void getSubcategory(String Url, String categoryid) {

        dialog = new Dialog(mContext);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView loading = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        loading.setText(getResources().getString(R.string.action_pleasewait));
        System.out.println("--------------Confirm Ride url-------------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("category_id", categoryid);
        System.out.println("--------------Confirm Ride jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("--------------CAtegoryresponse-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);

                    subcategorynamelist = new ArrayList<>();
                    subcategoryidlist = new ArrayList<>();

                    if (object.getInt("status") == 1) {
                        JSONObject object1 = object.getJSONObject("response");

                        JSONArray jsonArray = object1.getJSONArray("category");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object2 = jsonArray.getJSONObject(i);
                                String catid = object2.getString("id");
                                String catename = object2.getString("sub_category");
                                String categoryimage = object2.getString("image");
                                subcategorynamelist.add(catename);
                                subcategoryidlist.add(catid);
                            }
                        }
                    }

                    ArrayAdapter<String> categoryadaptor = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, subcategorynamelist);
                    spn_subcategory.setAdapter(categoryadaptor);
                    categoryadaptor.notifyDataSetChanged();

                    spn_subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            // TODO: 7/4/2020  call subcatrgory
                            parcelsubcategory_id= subcategoryidlist.get(i);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
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


    // TODO: 8/6/2020  gert services here

    private void PostRequest(String Url, final String latitude, final String longitude) {

        System.out.println("--------------Book My ride url-------------------" + Url);

        String Sselected_latitude = String.valueOf(latitude);
        String Sselected_longitude = String.valueOf(longitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat",latitude);
        jsonParams.put("lon", longitude);
        jsonParams.put("category", CategoryID);
        System.out.println("--------------Book My ride jsonParams-------------------" + jsonParams);
        mRequest = new ServiceRequest(mContext);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Book My ride reponse-------------------" + response);
                String fail_response = "", ScurrencyCode = "", SwalletAmount = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        if (object.getString("status").equalsIgnoreCase("1")) {

                            JSONObject jobject = object.getJSONObject("response");
                            if (jobject.length() > 0) {

                                ScurrencyCode = jobject.getString("currency");
                                SwalletAmount = jobject.getString("wallet_amount");
                                for (int i = 0; i < jobject.length(); i++) {

                                    Object check_driver_object = jobject.get("drivers");
                                    if (check_driver_object instanceof JSONArray) {

                                        JSONArray driver_array = jobject.getJSONArray("drivers");
                                        if (driver_array.length() > 0) {
                                            driver_list.clear();

                                            for (int j = 0; j < driver_array.length(); j++) {
                                                JSONObject driver_object = driver_array.getJSONObject(j);

                                                HomePojo pojo = new HomePojo();
                                                pojo.setDriver_lat(driver_object.getString("lat"));
                                                pojo.setDriver_long(driver_object.getString("lon"));

                                                driver_list.add(pojo);
                                            }

                                        } else {
                                            driver_list.clear();
                                        }
                                    } else {
                                    }


                                    Object check_ratecard_object = jobject.get("ratecard");
                                    if (check_ratecard_object instanceof JSONObject) {

                                        JSONObject ratecard_object = jobject.getJSONObject("ratecard");
                                        if (ratecard_object.length() > 0) {
                                            HomePojo pojo = new HomePojo();

                                            pojo.setRate_cartype(ratecard_object.getString("category"));
                                            pojo.setRate_note(ratecard_object.getString("note"));
                                            pojo.setCurrencyCode(jobject.getString("currency"));

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


                                        } else {

                                        }
                                    } else {

                                    }


                                    Object check_category_object = jobject.get("category");
                                    if (check_category_object instanceof JSONArray) {

                                        JSONArray cat_array = jobject.getJSONArray("category");
                                        if (cat_array.length() > 0) {
                                            category_list.clear();

                                            for (int k = 0; k < cat_array.length(); k++) {

                                                JSONObject cat_object = cat_array.getJSONObject(k);

                                                HomePojo pojo = new HomePojo();
                                                pojo.setCat_name(cat_object.getString("name"));
                                                pojo.setCat_time(cat_object.getString("eta"));
                                                pojo.setCat_id(cat_object.getString("id"));
                                                pojo.setIcon_normal(cat_object.getString("icon_normal"));
                                                pojo.setIcon_active(cat_object.getString("icon_active"));
                                                pojo.setCar_icon(cat_object.getString("icon_car_image"));
                                                pojo.setSelected_Cat(jobject.getString("selected_category"));
                                                pojo.setPoolOption(cat_object.getString("has_pool_option"));
                                                pojo.setPoolType(cat_object.getString("is_pool_type"));
                                                category_list.add(pojo);
                                            }


                                            BookMyRide_Adapter adapter = new BookMyRide_Adapter(UserPickupInfoActivity.this, category_list);

                                            recyclerViewvehicletype.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        } else {


                                        }
                                    } else {
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                }

            }

            @Override
            public void onErrorListener() {
                //   progressWheel.setVisibility(View.GONE);

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == GALLERY && resultCode == RESULT_OK

                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<Uri>();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);





                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private void EstimatePriceRequest(String Url) {
        System.out.println("--------------Estimate url-------------------" + Url);

        final Dialog mdialog = new Dialog(mContext);
        mdialog.getWindow();
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setContentView(R.layout.custom_loading);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();
        TextView dialog_title = (TextView) mdialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        final HashMap<String,String>    jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("pickup", session.getStringValue(SessionManager.KEY_PICKUP_LOCATION));
        jsonParams.put("drop",session.getStringValue(SessionManager.KEY_DROP_LOCATION));
        jsonParams.put("pickup_lat", session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LAT));
        jsonParams.put("pickup_lon", session.getStringValue(SessionManager.KEY_PICKUP_LOCATION_LON));
        jsonParams.put("drop_lat", session.getStringValue(SessionManager.KEY_DROP_LOCATION_LAT));
        jsonParams.put("drop_lon", session.getStringValue(SessionManager.KEY_DROP_LOCATION_LON));
        jsonParams.put("category", CategoryID);
        jsonParams.put("type", "0");
        //  jsonParams.put("pickup_date", coupon_selectedDate);
        //   jsonParams.put("pickup_time", coupon_selectedTime);
        System.out.println("--------------Estimate  jsonParams-------------------" + jsonParams);
        Log.e("RINKUREPONSE: ", "EstimatePriceRequest: " + jsonParams.toString());
        ServiceRequest  estimate_mRequest = new ServiceRequest(mContext);
        estimate_mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                Log.e("RINKURESPONSE",">>> "+response);
                System.out.println("--------------Estimate  reponse-------------------" + response);
                String sha_catrgory_id = "", sha_Spickup = "", sha_est_amount = "", sha_Sdrop = "", sha_Smin_amount = "", sha_Smax_amount = "", sha_SapproxTime = "", sha_SpeakTime = "", sha_SnightCharge = "", sha_approxTime = "", sha_Snote = "";
                String status = "", has_pool_service = "", SwalletAmount = "", is_pool_service = "", ScurrencyCode = "", Spickup = "", Sdrop = "", Smin_amount = "", Smax_amount = "", SapproxTime = "", SpeakTime = "", SnightCharge = "", Snote = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject response_object = object.getJSONObject("response");
                            if (response_object.length() > 0) {

                                ScurrencyCode = response_object.getString("currency");

                                if (response_object.has("has_pool_service")) {
                                    has_pool_service = response_object.getString("has_pool_service");
                                }

                                if (response_object.has("is_pool_service")) {
                                    is_pool_service = response_object.getString("is_pool_service");
                                }
                                if (response_object.has("eta")) {
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
                                        sha_est_amount = eta_object.getString("est_amount");
                                        String distance = eta_object.getString("distance");

                                        bottomSheetDialog(distance,sha_est_amount,SapproxTime);



                                    }
                                }

                            } else {

                            }
                        } else {

                        }
                    } else {
                    }

                    mdialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                mdialog.dismiss();
            }
        });
    }



    void bottomSheetDialog(String distance, String estimatefare,String approxtime){

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_summary, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(view);

        ImageView imageViewclose = dialog.findViewById(R.id.id_dismis_dialog);
        imageViewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tvpickLocation = dialog.findViewById(R.id.id_pickuplocatin);
        TextView tvdroplocation = dialog.findViewById(R.id.id_drop_location);
        TextView tvdistance  = dialog.findViewById(R.id.id_distance_couver);
        TextView tvestimatefare  = dialog.findViewById(R.id.id_estimate_fareid_estimate_fare);
        TextView btn_confirmbooking  = dialog.findViewById(R.id.btn_confirmbooking);



        tvpickLocation.setText(session.getStringValue(SessionManager.KEY_PICKUP_LOCATION));
        tvdroplocation.setText(session.getStringValue(SessionManager.KEY_DROP_LOCATION));
        tvdistance.setText(distance+" km");
        tvestimatefare.setText("Rs. "+estimatefare);

        btn_confirmbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                sendDataApi(params);

            }
        });

        dialog.show();



    }

    private void sendDataApi(final Map<String,String> params) {

        final Dialog mdialog = new Dialog(mContext);
        mdialog.getWindow();
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setContentView(R.layout.custom_loading);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();
        TextView dialog_title = (TextView) mdialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));



        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Iconstant.confirm_ride_url, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.e("RINKURESPONSE", "---> SUCCESS  " + resultResponse);
                mdialog.dismiss();
                try {
                    JSONObject object = new JSONObject(resultResponse);
                    if (object.getInt("status")==1){

                        JSONObject object1 = object.getJSONObject("response");
                        String response_time = object1.getString("response_time");
                        String try_value = object1.getString("retry_time");
                        String riderId = object1.getString("ride_id");

                        Intent intent  = new Intent();
                        intent.putExtra("response_time",response_time);
                        intent.putExtra("retry_time",try_value);
                        intent.putExtra("ride_id",riderId);
                       setResult(10000,intent);

                    }else {
                        Alert("Booking Status", "Your booking is not Submitted! try again...");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mdialog.dismiss();
                error.printStackTrace();
                Log.e("ERRORRRRR", "---->" + error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Log.e("RINKURESPONSE",">>> SENDVALUE "+params);
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();

                Log.e("RINKURESPONSE",">>> "+imagesEncodedList.size());
                for (int i =0; i<mArrayUri.size();i++) {
                    params.put("image["+i+"]", new VolleyMultipartRequest.DataPart(SystemClock.currentThreadTimeMillis()+"file_rinku["+i+"]"+".jpg",  convertImageToByte(mArrayUri.get(i)), "image/jpeg"));

                    Log.e("RINKURESPONSE",">>> image "+params);
                }
                return params;
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }



    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.e("ADADADA",">>> "+data);
        return data;
    }


    @Override
    public void onBackPressed() {
        if ( FLAGESTAPER ==0) {
            onBackPressed();
        }else {
            FLAGESTAPER = 0;
            layout_pickupaddress.setVisibility(View.VISIBLE);
            layout_deliveryaddress.setVisibility(View.GONE);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }









    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(mContext);
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

}
