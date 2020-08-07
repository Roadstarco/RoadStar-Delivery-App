package com.shrinkcom.app;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.shrinkcom.adapter.EmergencyContactAdapter;
import com.shrinkcom.adapter.FareAdaptewr;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.pojo.CancelTripPojo;
import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.pojo.FarePojo;
import com.shrinkcom.pojo.MyRideDetailPojo;
import com.shrinkcom.subclass.FragmentActivitySubclass;
import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.ServiceRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by Prem Kumar and Anitha on 10/29/2015.
 */
public class MyRidesDetail1 extends FragmentActivitySubclass {
    private ImageView Iv_panic;
    private MaterialDialog panic_dialog;
    private RelativeLayout back;
    private static Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";
    private String Contact_mail = "";
    private ServiceRequest mRequest;
    Dialog dialog;
    ArrayList<MyRideDetailPojo> itemlist;
    ArrayList<CancelTripPojo> itemlist_reason;
    ArrayList<EmergencyPojo> emergencyList;

    private TextView Tv_carType, Tv_carNo, Tv_rideStatus, Tv_pickup_loc, Tv_pickup_time, Tv_drop_loc, Tv_drop_time,
            Tv_rideDistance, Tv_timeTaken, Tv_waitTime, Tv_totalBill, Tv_totalPaid, Tv_couponDiscount, Tv_walletUsuage, Tv_PayStatus;
    private LinearLayout pickup_address_layout, drop_address_layout, pick_up_time_layout, drop_time_layout;
    private RelativeLayout Rl_favorite, Rl_priceBottom, Rl_button;
    private RelativeLayout Rl_address, Rl_pickup, RL_drop;
    private ImageView Iv_favorite;
    private LinearLayout Ll_cancelTrip, Ll_payment, Ll_mailInvoice, Ll_reportIssue, Ll_trackRide, Ll_share_Ride, Ll_wait_Time;

    private GoogleMap googleMap;
    private static String SrideId_intent = "";
    private boolean isPickUpAvailable = false;
    private boolean isSummaryAvailable = false;
    private boolean isReasonAvailable = false;
    private boolean isFareAvailable = false;
    private boolean isTrackRideAvailable = false;
    private MaterialDialog completejob_dialog;
    TextView code_;
    private EditText Et_share_trip_mobileno;
    public static MyRidesDetail1 myrideDetail_class;

    private String Str_LocationLatitude = "", Str_LocationLongitude = "";
    private String currencySymbol = "", dialCodeno;

    //------Invoice Dialog Declaration-----
    private EditText Et_dialog_InvoiceEmail;
    private MaterialDialog invoice_dialog;

    //------Favourite Dialog Declaration-----
    private EditText Et_dialog_FavouriteTitle;
    private MaterialDialog favourite_dialog;

    //------Tip Declaration-----
    private EditText Et_tip_Amount;
    private Button Bt_tip_Apply;
    private RelativeLayout Rl_tip, Rl_main_tip;
    private CheckBox Cb_tip;
    private TextView Tv_tipAmount;
    private LinearLayout Ll_deleteTip;

    private boolean isRidePickUpAvailable = false;
    private boolean isRideDropAvailable = false;
    CardView panic_cardView;
    AppInfoSessionManager appInfo_Session;
    final int PERMISSION_REQUEST_CODE = 111;
    private String sSelectedPanicNumber = "";

    CountryPicker picker;
    private ExpandableHeightListView payment_detail_list;


    ArrayList<FarePojo> farelist = new ArrayList<FarePojo>();
    private String ScurrencySymbol = "";
    private String sTripType = "";
    private String page = "";
    private String sType = "";
    private RelativeLayout Rl_mapView;
    private ImageView Iv_routeMap;
    private String sInvoiceSrc = "";


    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_REFRESH")) {
                if (isInternetPresent) {
                    postRequest_MyRides(Iconstant.myride_details_url);
                }
            }
            if (intent.getAction().equals("com.MyRidesDetail.MYRIDES_FINISH")) {
                if (isInternetPresent) {
                    finish();
                }
            }
        }
    }

    private RefreshReceiver refreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myrides_detail);
        myrideDetail_class = MyRidesDetail1.this;
        initialize();
        initializeMap();

        //Start XMPP Chat Service
//        ChatService.startUserAction(MyRidesDetail.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("webview".equalsIgnoreCase(page)) {

                }
                if ("push".equalsIgnoreCase(sType)) {
                    Intent i = new Intent(MyRidesDetail1.this, NavigationDrawer.class);
                    startActivity(i);
                } else {
                    onBackPressed();
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        Ll_cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
                mDialog.setDialogTitle(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_title));
                mDialog.setDialogMessage(getResources().getString(R.string.my_rides_detail_cancel_ride_alert));
                mDialog.setPositiveButton(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        cd = new ConnectionDetector(MyRidesDetail1.this);
                        isInternetPresent = cd.isConnectingToInternet();

                        if (isInternetPresent) {
                            postRequest_CancelRides_Reason(Iconstant.cancel_myride_reason_url);
                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    }
                });
                mDialog.setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel_ride_alert_no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();

            }
        });

        Ll_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(MyRidesDetail1.this);
                isInternetPresent = cd.isConnectingToInternet();

                /*if (isInternetPresent) {
                    Intent passIntent = new Intent(MyRidesDetail.this, MyRidePaymentList.class);
                    passIntent.putExtra("RideID", SrideId_intent);
                    startActivity(passIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }*/

                if (isInternetPresent) {
                    finish();
                    Intent passIntent = new Intent(MyRidesDetail1.this, FareBreakUp.class);
                    passIntent.putExtra("RideID", SrideId_intent);
                    startActivity(passIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }


            }
        });

        Ll_mailInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailInvoice();
            }
        });

        Ll_reportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Ll_trackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(MyRidesDetail1.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    Intent i = new Intent(MyRidesDetail1.this, MyRideDetailTrackRide.class);
                    i.putExtra("rideID", SrideId_intent);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                    //       postRequest_TrackRide(Iconstant.myride_details_track_your_ride_url);
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });


        Ll_share_Ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareTrip();

            }
        });


        Iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemlist.get(0).getIsFavLocation().equalsIgnoreCase("0")) {
                    favouriteAddress();
                } else if (itemlist.get(0).getIsFavLocation().equalsIgnoreCase("1")) {
                    unfavouriteAddress();
                }
            }
        });


        Bt_tip_Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(MyRidesDetail1.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (Et_tip_Amount.getText().toString().length() > 0) {
                    if (isInternetPresent) {
                        postRequest_Tip(Iconstant.tip_add_url, "Apply");
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.my_rides_detail_tip_empty_label));
                }
            }
        });

        Cb_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Rl_tip.setVisibility(View.GONE);
                } else {
                    Rl_tip.setVisibility(View.GONE);
                }
            }
        });


        Ll_deleteTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cd = new ConnectionDetector(MyRidesDetail1.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {
                    postRequest_Tip(Iconstant.tip_remove_url, "Remove");
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });
        Iv_panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----------------panic onclick method-----------------");
                panic();
            }
        });

    }

    private void initializeMap() {
        if (googleMap == null) {

            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.my_rides_detail_mapview));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);


                }
            });
           /* googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.my_rides_detail_mapview)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(MyRidesDetail.this, getResources().getString(R.string.myrides_toast_sorry_unable), Toast.LENGTH_SHORT).show();
            }*/
        }

        // Changing map type

    }

    public void loadMap(GoogleMap arg) {
        googleMap = arg;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(false);
        //Enable / Disable Moving Function
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private void initialize() {
        session = new SessionManager(MyRidesDetail1.this);
        appInfo_Session = new AppInfoSessionManager(MyRidesDetail1.this);
        cd = new ConnectionDetector(MyRidesDetail1.this);
        isInternetPresent = cd.isConnectingToInternet();
        itemlist = new ArrayList<MyRideDetailPojo>();
        itemlist_reason = new ArrayList<CancelTripPojo>();
        emergencyList = new ArrayList<EmergencyPojo>();

        picker = CountryPicker.newInstance("Select Country");
        back = (RelativeLayout) findViewById(R.id.my_rides_detail_header_back_layout);
        Tv_carType = (TextView) findViewById(R.id.my_rides_detail_car_type);
        Tv_carNo = (TextView) findViewById(R.id.my_rides_detail_car_number);
        Tv_rideStatus = (TextView) findViewById(R.id.my_rides_detail_ride_status);
        Tv_PayStatus = (TextView) findViewById(R.id.tripdetail_status);
        Tv_pickup_loc = (TextView) findViewById(R.id.my_rides_detail_location_textview);
        Tv_drop_loc = (TextView) findViewById(R.id.my_rides_detail_Droplocation_textview);

        payment_detail_list = (ExpandableHeightListView) findViewById(R.id.my_rides_payment_detail_listView);
        Tv_pickup_time = (TextView) findViewById(R.id.my_rides_detail_pickup_time_textview);
        Tv_drop_time = (TextView) findViewById(R.id.my_rides_detail_drop_time_textview);
        pickup_address_layout = (LinearLayout) findViewById(R.id.pickup_address);
        drop_address_layout = (LinearLayout) findViewById(R.id.drop_address);

        pick_up_time_layout = (LinearLayout) findViewById(R.id.pickup_time_layout);
        drop_time_layout = (LinearLayout) findViewById(R.id.drop_time_layout);

        Tv_rideDistance = (TextView) findViewById(R.id.my_rides_detail_ride_distance_textview);
        Tv_timeTaken = (TextView) findViewById(R.id.my_rides_detail_time_taken_textview);
        Tv_waitTime = (TextView) findViewById(R.id.my_rides_detail_wait_time_textview);
        Tv_totalBill = (TextView) findViewById(R.id.my_rides_detail_total_bill_textview);
        Tv_totalPaid = (TextView) findViewById(R.id.my_rides_detail_total_paid_textview);
        Tv_couponDiscount = (TextView) findViewById(R.id.my_rides_detail_coupon_discount_textview);
        Tv_walletUsuage = (TextView) findViewById(R.id.my_rides_detail_wallet_usuage_textview);
        Ll_share_Ride = (LinearLayout) findViewById(R.id.my_rides_detail_share_layout);
        Ll_wait_Time = (LinearLayout) findViewById(R.id.my_rides_detail_wait_time_layout);


        Rl_favorite = (RelativeLayout) findViewById(R.id.my_rides_detail_favorite_layout);
        RL_drop = (RelativeLayout) findViewById(R.id.layout_trip_summery_details_status);
        Rl_priceBottom = (RelativeLayout) findViewById(R.id.my_rides_detail_price_layout);
        Rl_button = (RelativeLayout) findViewById(R.id.my_rides_detail_button_layout);
        Iv_favorite = (ImageView) findViewById(R.id.my_rides_detail_favorite_imageView);
        Rl_address = (RelativeLayout) findViewById(R.id.my_rides_detail_address_layout);
        Rl_pickup = (RelativeLayout) findViewById(R.id.my_rides_detail_time_layout);

        Ll_cancelTrip = (LinearLayout) findViewById(R.id.my_rides_detail_cancel_trip_layout);
        Ll_payment = (LinearLayout) findViewById(R.id.my_rides_detail_payment_layout);
        Ll_mailInvoice = (LinearLayout) findViewById(R.id.my_rides_detail_mail_invoice_layout);
        Ll_reportIssue = (LinearLayout) findViewById(R.id.my_rides_detail_report_issue_layout);
        Ll_trackRide = (LinearLayout) findViewById(R.id.my_rides_detail_track_ride_layout);

        Et_tip_Amount = (EditText) findViewById(R.id.my_rides_detail_tip_editText);
        Bt_tip_Apply = (Button) findViewById(R.id.my_rides_detail_tip_apply_button);
        Rl_main_tip = (RelativeLayout) findViewById(R.id.my_rides_detail_tip_top_layout);
        Rl_tip = (RelativeLayout) findViewById(R.id.my_rides_detail_tip_layout);
        Cb_tip = (CheckBox) findViewById(R.id.my_rides_detail_tip_checkBox);
        Tv_tipAmount = (TextView) findViewById(R.id.my_rides_detail_tip_amount_textView);
        Ll_deleteTip = (LinearLayout) findViewById(R.id.my_rides_detail_tip_amount_remove_layout);

        panic_cardView = (CardView) findViewById(R.id.my_rides_detail_panic_cardview_layout);
        Iv_panic = (ImageView) findViewById(R.id.panic_image);

        Rl_mapView = (RelativeLayout) findViewById(R.id.my_rides_detail_mapview_layout);
        Iv_routeMap = (ImageView) findViewById(R.id.my_rides_detail_route_map_imageview);

        int maxLength = 3;
        Et_tip_Amount.setFilters(new InputFilter[]{new EmojiExcludeFilter(), new InputFilter.LengthFilter(maxLength)});

        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_REFRESH");
        intentFilter.addAction("com.MyRidesDetail.MYRIDES_FINISH");
        registerReceiver(refreshReceiver, intentFilter);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        HashMap<String, String> user1 = appInfo_Session.getAppInfo();
        Contact_mail = user1.get(AppInfoSessionManager.KEY_CONTACT_EMAIL);

        Intent intent = getIntent();
        SrideId_intent = intent.getStringExtra("RideID");
        if (intent.hasExtra("page")) {
            page = intent.getStringExtra("page");
        }
        if (intent.hasExtra("type")) {
            sType = intent.getStringExtra("type");
        }


        emergencyList = session.getEmergencyContactDetails();
        for (int i = 0; i < emergencyList.size(); i++) {
            System.out.println("---emergencyList-----" + emergencyList.get(i).getTitle() + " " + emergencyList.get(i).getNumber());
        }

        if (isInternetPresent) {
            postRequest_MyRides(Iconstant.myride_details_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                /*if(completejob_dialog!=null)
                {
                    completejob_dialog.dismiss();
                }*/
            }
        });
        mDialog.show();

    }

    private void AlertCloseShare(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                if (completejob_dialog != null) {
                    completejob_dialog.dismiss();
                }
            }
        });
        mDialog.show();

    }

    private void AlertCancel(String title, String alert) {

        final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                Intent finish_fareBreakUp = new Intent();
                finish_fareBreakUp.setAction("com.mylibrary.pushnotification.finish.FareBreakUp");
                sendBroadcast(finish_fareBreakUp);

                Intent finish_timerPage = new Intent();
                finish_timerPage.setAction("com.mylibrary.pushnotification.finish.TimerPage");
                sendBroadcast(finish_timerPage);

                Intent finish_pushAlert = new Intent();
                finish_pushAlert.setAction("com.mylibrary.pushnotification.finish.PushNotificationAlert");
                sendBroadcast(finish_pushAlert);

                Intent finish_MyRideDetails = new Intent();
                finish_MyRideDetails.setAction("com.mylibrary.pushnotification.finish.MyRideDetails");
                sendBroadcast(finish_MyRideDetails);

                Intent local = new Intent();
                local.setAction("com.mylibrary.pushnotification.finish.trackyourRide");
                sendBroadcast(local);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                sendBroadcast(broadcastIntent);

                Intent broadcastIntent1 = new Intent();
                broadcastIntent1.setAction("com.package.MYRIDES_FINISH");
                sendBroadcast(broadcastIntent1);

                if (completejob_dialog != null) {
                    completejob_dialog.dismiss();
                }
            }
        });
        mDialog.show();

    }

    private void shareTrip() {
        completejob_dialog = new MaterialDialog(MyRidesDetail1.this);
        View view = LayoutInflater.from(MyRidesDetail1.this).inflate(R.layout.share_trip_popup, null);
        Et_share_trip_mobileno = (EditText) view.findViewById(R.id.sharetrip_mobilenoEt);
        Button Bt_Submit = (Button) view.findViewById(R.id.jsharetrip_popup_submit);
        Button Bt_Cancel = (Button) view.findViewById(R.id.sharetrip_popup_cancel);
        code_ = (TextView) view.findViewById(R.id.sharetrip_country_codeEt);
        Et_share_trip_mobileno.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        completejob_dialog.setView(view).show();

        code_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                code_.setText(dialCode);
                dialCodeno = dialCode;
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(code_.getWindowToken(), 0);
            }
        });

        Bt_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidPhoneNumber(Et_share_trip_mobileno.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.profile_lable_error_mobile));
                } else if (!isValidCodeNumber(code_.getText().toString())) {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.register_label_alert_country_code));
                } else {
                    if (isInternetPresent) {
                        share_trip_postRequest_MyRides(MyRidesDetail1.this, Iconstant.share_trip_url, "jobcomplete");
                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                    }
                }
            }
        });

        Bt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                completejob_dialog.dismiss();
            }
        });

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


    //code to Check Email Validation
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //--------------------Code to set error for EditText-----------------------
    private void erroredit(EditText editname, String msg) {
        Animation shake = AnimationUtils.loadAnimation(MyRidesDetail1.this, R.anim.shake);
        editname.startAnimation(shake);


        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(msg);
        ssbuilder.setSpan(fgcspan, 0, msg.length(), 0);
        editname.setError(ssbuilder);
    }


    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher mailInvoice_EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Et_dialog_InvoiceEmail.getText().length() > 0) {
                Et_dialog_InvoiceEmail.setError(null);
            }
        }
    };

    private final TextWatcher favourite_EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (Et_dialog_FavouriteTitle.getText().length() > 0) {
                Et_dialog_FavouriteTitle.setError(null);
            }
        }
    };

    //----------Method to Send Email--------
    protected void sendEmail() {
        System.out.println(" jai contact email" + Contact_mail);
        String[] TO = {Contact_mail};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Message");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyRidesDetail1.this, getResources().getString(R.string.myrides_toast_there_is_no), Toast.LENGTH_SHORT).show();
        }
    }

    //----------Method for Invoice Email--------
    private void mailInvoice() {
        final View view = View.inflate(MyRidesDetail1.this, R.layout.mail_invoice_dialog, null);
        invoice_dialog = new MaterialDialog(MyRidesDetail1.this);
        invoice_dialog.setContentView(view)
                .setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                                if (imm.isAcceptingText()) {
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                invoice_dialog.dismiss();


                            }
                        }
                )
                .setPositiveButton(getResources().getString(R.string.my_rides_detail_send), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cd = new ConnectionDetector(MyRidesDetail1.this);
                                isInternetPresent = cd.isConnectingToInternet();
                                if (Et_dialog_InvoiceEmail.getText().toString().trim().length() > 0) {

                                    if (!isValidEmail(Et_dialog_InvoiceEmail.getText().toString())) {
                                        erroredit(Et_dialog_InvoiceEmail, getResources().getString(R.string.register_label_alert_email));
                                    } else {
                                        if (isInternetPresent) {
                                            postRequest_EmailInvoice(Iconstant.myride_details_inVoiceEmail_url, Et_dialog_InvoiceEmail.getText().toString(), SrideId_intent);
                                        } else {
                                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                                        }
                                    }
                                } else {
                                    erroredit(Et_dialog_InvoiceEmail, getResources().getString(R.string.register_label_alert_email_empty));
                                }
                            }
                        }
                )
                .show();

        Et_dialog_InvoiceEmail = (EditText) view.findViewById(R.id.mail_invoice_email_edittext);
        Et_dialog_InvoiceEmail.addTextChangedListener(mailInvoice_EditorWatcher);
        Et_dialog_InvoiceEmail.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Et_dialog_InvoiceEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(Et_dialog_InvoiceEmail.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }

    private void panic() {
        System.out.println("----------------panic method-----------------");
        View view = View.inflate(MyRidesDetail1.this, R.layout.panic_page, null);
        final MaterialDialog dialog = new MaterialDialog(MyRidesDetail1.this);
        dialog.setContentView(view).setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }
        ).show();
        ListView emergencyListview = (ListView) view.findViewById(R.id.panic_listView);

        EmergencyContactAdapter emergencyContactAdapter = new EmergencyContactAdapter(MyRidesDetail1.this, emergencyList);
        emergencyListview.setAdapter(emergencyContactAdapter);

        emergencyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();

                sSelectedPanicNumber = emergencyList.get(position).getNumber();
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                        requestPermission();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                        startActivity(callIntent);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                    startActivity(callIntent);
                }

            }
        });

    }

    //----------Method for Favourite Address--------
    private void favouriteAddress() {
        final View view = View.inflate(MyRidesDetail1.this, R.layout.myride_detail_favourite_dialog, null);
        favourite_dialog = new MaterialDialog(MyRidesDetail1.this);
        favourite_dialog.setContentView(view)
                .setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                                if (imm.isAcceptingText()) {
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                favourite_dialog.dismiss();

                            }
                        }
                )
                .setPositiveButton(getResources().getString(R.string.my_rides_detail_favourite_apply), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cd = new ConnectionDetector(MyRidesDetail1.this);
                                isInternetPresent = cd.isConnectingToInternet();
                                Et_dialog_FavouriteTitle.setText(Et_dialog_FavouriteTitle.getText().toString().trim());
                                if (Et_dialog_FavouriteTitle.getText().toString().length() == 0) {
                                    erroredit(Et_dialog_FavouriteTitle, getResources().getString(R.string.my_rides_detail_favourite_alert_title));
                                } else {
                                    if (isInternetPresent) {
                                        postRequest_FavoriteSave(Iconstant.favoritelist_add_url);
                                    } else {
                                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                                    }
                                }
                            }
                        }
                )
                .show();

        Et_dialog_FavouriteTitle = (EditText) view.findViewById(R.id.myride_detail_favourite_title_edittext);
        Et_dialog_FavouriteTitle.addTextChangedListener(favourite_EditorWatcher);
        Et_dialog_FavouriteTitle.setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        Et_dialog_FavouriteTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(Et_dialog_FavouriteTitle.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

    }

    //----------Method for UnFavourite Address--------
    private void unfavouriteAddress() {

        final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
        mDialog.setDialogMessage(getResources().getString(R.string.my_rides_detail_un_fav_alert_message));
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (isInternetPresent) {
                    postRequest_FavoriteDelete(Iconstant.favoritelist_delete_url, itemlist.get(0).getLocation_key());
                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });
        mDialog.setNegativeButton(getResources().getString(R.string.my_rides_detail_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    //-----------------------MyRide Detail Post Request-----------------
    private void postRequest_MyRides(String Url) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("-------------MyRide Detail Url----------------" + Url);
        System.out.println("-------------MyRide Detail user_id----------------" + UserID);
        System.out.println("-------------MyRide Detail ride_id----------------" + SrideId_intent);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", SrideId_intent);

        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MyRide Detail Response----------------" + response);

                String Sstatus = "";
                //    Currency currencycode;

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_details_object = response_object.get("details");
                            if (check_details_object instanceof JSONObject) {

                                JSONObject detail_object = response_object.getJSONObject("details");
                                if (detail_object.length() > 0) {
                                    itemlist.clear();
                                    MyRideDetailPojo pojo = new MyRideDetailPojo();
                                    ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(detail_object.getString("currency"));
                                    // currencycode = Currency.getInstance(getLocale(detail_object.getString("currency")));
                                    //  currencySymbol = currencycode.getSymbol();

                                    pojo.setCurrrencySymbol(ScurrencySymbol);
                                    pojo.setCarType(detail_object.getString("cab_type"));
                                    pojo.setRideId(detail_object.getString("ride_id"));
                                    pojo.setRideStatus(detail_object.getString("ride_status"));
                                    pojo.setRideDisplayStatus(detail_object.getString("disp_status"));
                                    pojo.setDoCancelAction(detail_object.getString("do_cancel_action"));
                                    pojo.setDoTrackAction(detail_object.getString("do_track_action"));
                                    pojo.setIsFavLocation(detail_object.getString("is_fav_location"));
                                    pojo.setDoFavLocation(detail_object.getString("do_fav"));
                                    pojo.setPay_status(detail_object.getString("pay_status"));
                                    pojo.setPayDisplayStatus(detail_object.getString("disp_pay_status"));
                                    pojo.setPickup(detail_object.getString("pickup_date"));
                                    pojo.setDistanceUnit(detail_object.getString("distance_unit"));
                                    sTripType = detail_object.getString("trip_type");
                                    pojo.setLocation_key(detail_object.getString("fav_location_id"));
                                    sInvoiceSrc = detail_object.getString("invoice_src");


                                    Object check_pickup_object = detail_object.get("pickup");
                                    if (check_pickup_object instanceof JSONObject) {

                                        JSONObject pickup_object = detail_object.getJSONObject("pickup");
                                        if (pickup_object.length() > 0) {
                                            pojo.setAddress(pickup_object.getString("location"));
                                            JSONObject latlong_object = pickup_object.getJSONObject("latlong");
                                            if (latlong_object.length() > 0) {
                                                pojo.setLocationLat(latlong_object.getString("lat"));
                                                pojo.setLocationLong(latlong_object.getString("lon"));
                                            }

                                            isPickUpAvailable = true;
                                        } else {
                                            isPickUpAvailable = false;
                                        }
                                    }


                                    if (detail_object.getString("ride_status").equalsIgnoreCase("Completed") || detail_object.getString("ride_status").equalsIgnoreCase("Finished") || detail_object.getString("ride_status").equalsIgnoreCase("Onride")) {
                                        pojo.setDrop(detail_object.getString("drop_date"));


                                        Object check_drop_object = detail_object.get("drop");
                                        if (check_drop_object instanceof JSONObject) {

                                            JSONObject pickup_object1 = detail_object.getJSONObject("drop");
                                            if (pickup_object1.length() > 0) {
                                                pojo.setDropAddress(pickup_object1.getString("location"));
                                                JSONObject latlong_object = pickup_object1.getJSONObject("latlong");
                                                if (latlong_object.length() > 0) {
                                                   /* pojo.setLocationLat(latlong_object.getString("lat"));
                                                    pojo.setLocationLong(latlong_object.getString("lon"));*/
                                                }

                                                //   isPickUpAvailable = true;
                                            } else {
                                                ///     isPickUpAvailable = false;
                                            }
                                        }


                                        Object check_summary_object = detail_object.get("summary");
                                        if (check_summary_object instanceof JSONObject) {

                                            JSONObject summary_object = detail_object.getJSONObject("summary");
                                            if (summary_object.length() > 0) {
                                                pojo.setRideDistance(summary_object.getString("ride_distance"));
                                                pojo.setTimeTaken(summary_object.getString("ride_duration"));
                                                pojo.setWaitTime(summary_object.getString("waiting_duration"));

                                                isSummaryAvailable = true;
                                            } else {
                                                isSummaryAvailable = false;
                                            }
                                        }


                                       /* Object check_fare_object = detail_object.get("fare");
                                        if (check_fare_object instanceof JSONObject) {

                                            JSONObject fare_object = detail_object.getJSONObject("fare");
                                            if (fare_object.length() > 0) {
                                                pojo.setTotalBill(fare_object.getString("grand_bill"));
                                                pojo.setTotalPaid(fare_object.getString("total_paid"));
                                                pojo.setCouponDiscount(fare_object.getString("coupon_discount"));
                                                pojo.setWalletUsuage(fare_object.getString("wallet_usage"));
                                                pojo.setTip_amount(fare_object.getString("tips_amount"));

                                                isFareAvailable = true;
                                            } else {
                                                isFareAvailable = false;
                                            }
                                        }*/

                                        Object check_fare_detail_object = detail_object.get("fare_summary");
                                        if (check_fare_detail_object instanceof JSONArray) {
                                            JSONArray jarry = detail_object.getJSONArray("fare_summary");
                                            farelist.clear();
                                            if (jarry.length() > 0) {
                                                for (int i = 0; i < jarry.length(); i++) {
                                                    JSONObject job = jarry.getJSONObject(i);
                                                    FarePojo farepojo = new FarePojo();
                                                    farepojo.setTitle(job.getString("title"));
                                                    farepojo.setValue(ScurrencySymbol + job.getString("value"));
                                                    farelist.add(farepojo);
                                                }
                                                isFareAvailable = true;
                                            } else {
                                                isFareAvailable = false;
                                            }
                                        }


                                    }

                                    itemlist.add(pojo);
                                }
                            }

                        }
                    }


                    //------------OnPost Execute------------
                    if (Sstatus.equalsIgnoreCase("1")) {

                        if (itemlist.size() > 0) {
                            Rl_address.setVisibility(View.VISIBLE);
                            Rl_pickup.setVisibility(View.VISIBLE);

                            Tv_carType.setText(itemlist.get(0).getCarType() + " " + getResources().getString(R.string.my_rides_detail_ride_textview));
                            Tv_carNo.setText(getResources().getString(R.string.my_rides_detail_crn_textview) + " " + itemlist.get(0).getRideId());
                            Tv_rideStatus.setText(getResources().getString(R.string.tripsummery_add_Ride_label) + " " + itemlist.get(0).getDisplayStatus());


                            if (isPickUpAvailable) {
                                Tv_pickup_loc.setText(itemlist.get(0).getAddress());
                                if (!itemlist.get(0).getPickup().equalsIgnoreCase("")) {
                                    Tv_pickup_time.setText(itemlist.get(0).getPickup());
                                } else {
                                    pickup_address_layout.setVisibility(View.GONE);

                                }

                                Str_LocationLatitude = itemlist.get(0).getLocationLat();
                                Str_LocationLongitude = itemlist.get(0).getLocationLong();

                                Display display = getWindowManager().getDefaultDisplay();
                                int width = display.getWidth();

                                System.out.println("---------------screen width-----------------"+width);


                                if (600 <= width && 800 >= width){
                                    Iv_routeMap.getLayoutParams().height = 380;
                                }else if (801 <= width && 1500 >= width){
                                    Iv_routeMap.getLayoutParams().height = 500  ;
                                }else if (1501 <= width){
                                    Iv_routeMap.getLayoutParams().height = 640;
                                }



                                if (!"".equalsIgnoreCase(sInvoiceSrc) && sInvoiceSrc != null) {
                                    Rl_mapView.setVisibility(View.GONE);
                                    Iv_routeMap.setVisibility(View.VISIBLE);
                                    Picasso.with(MyRidesDetail1.this).load(sInvoiceSrc).placeholder(R.drawable.no_user_image).into(Iv_routeMap);
                                } else {
                                    Iv_routeMap.setVisibility(View.GONE);
                                    Rl_mapView.setVisibility(View.VISIBLE);
                                    //set marker for User location.
                                    if (itemlist.get(0).getLocationLat() != null && itemlist.get(0).getLocationLong() != null) {


                                        googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(itemlist.get(0).getLocationLat()), Double.parseDouble(itemlist.get(0).getLocationLong())))
                                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker_icon2)));
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                                        // Move the camera to last position with a zoom level

                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(itemlist.get(0).getLocationLat()), Double.parseDouble(itemlist.get(0).getLocationLong()))).zoom(17).build();
                                        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                                        googleMap.moveCamera(camUpdate);
                                    }
                                }
                            }


                            if (itemlist.get(0).getRideStatus().equalsIgnoreCase("Completed") || itemlist.get(0).getRideStatus().equalsIgnoreCase("Finished")) {
                                Tv_drop_time.setVisibility(View.VISIBLE);
                                if (!itemlist.get(0).getDrop().equalsIgnoreCase("")) {
                                    drop_time_layout.setVisibility(View.VISIBLE);
                                    Tv_drop_time.setText(itemlist.get(0).getDrop());
                                } else {
                                    drop_time_layout.setVisibility(View.GONE);
                                    drop_address_layout.setVisibility(View.GONE);

                                }

                                drop_address_layout.setVisibility(View.VISIBLE);
                                Tv_drop_loc.setVisibility(View.VISIBLE);
                                Tv_drop_loc.setText(itemlist.get(0).getDropAddress());
                                RL_drop.setVisibility(View.VISIBLE);
                                Tv_PayStatus.setVisibility(View.VISIBLE);
                                Tv_PayStatus.setText(getResources().getString(R.string.tripsummery_add_Payment_label) + " " + itemlist.get(0).getPayDisplayStatus());
                                if (isSummaryAvailable) {
                                   /* Tv_rideDistance.setText(itemlist.get(0).getRideDistance() + " " + getResources().getString(R.string.my_rides_detail_kms_textview));
                                    Tv_timeTaken.setText(itemlist.get(0).getTimeTaken() + " " + getResources().getString(R.string.my_rides_detail_mins_textview));
                                    Tv_waitTime.setText(itemlist.get(0).getWaitTime() + " " + getResources().getString(R.string.my_rides_detail_mins_textview));*/

                                    Tv_rideDistance.setText(itemlist.get(0).getRideDistance() + " " + itemlist.get(0).getDistanceUnit());
                                    Tv_timeTaken.setText(itemlist.get(0).getTimeTaken());
                                    if ("Share".equalsIgnoreCase(sTripType)) {
                                        Ll_wait_Time.setVisibility(View.GONE);

                                    } else {
                                        Ll_wait_Time.setVisibility(View.VISIBLE);
                                        Tv_waitTime.setText(itemlist.get(0).getWaitTime());
                                    }

                                }

                                if (isFareAvailable) {

                                    FareAdaptewr adaptewr = new FareAdaptewr(MyRidesDetail1.this, farelist);
                                    payment_detail_list.setAdapter(adaptewr);
                                    payment_detail_list.setExpanded(true);

                                }

                                Rl_priceBottom.setVisibility(View.VISIBLE);
                            } else {
                                Rl_priceBottom.setVisibility(View.GONE);
                                drop_address_layout.setVisibility(View.GONE);
                                drop_time_layout.setVisibility(View.GONE);
                                Tv_PayStatus.setVisibility(View.GONE);
                                RL_drop.setVisibility(View.GONE);
                            }

                            //------------Panic Button Change Function-------
                            if (itemlist.get(0).getRideStatus().equalsIgnoreCase("Onride")) {
                                panic_cardView.setVisibility(View.VISIBLE);

                                drop_address_layout.setVisibility(View.VISIBLE);
                                if (!itemlist.get(0).getDrop().equalsIgnoreCase("")) {
                                    Tv_drop_time.setVisibility(View.GONE);
                                } else {
                                    Tv_drop_time.setVisibility(View.GONE);
                                }

                                Tv_drop_loc.setVisibility(View.VISIBLE);
                                drop_address_layout.setVisibility(View.VISIBLE);
                                Tv_drop_loc.setText(itemlist.get(0).getDropAddress());
                                RL_drop.setVisibility(View.VISIBLE);


                            } else {
                                panic_cardView.setVisibility(View.GONE);
                            }


                            //------------Button Change Function-------
                            if (itemlist.get(0).getPay_status().equalsIgnoreCase("Pending") || itemlist.get(0).getPay_status().equalsIgnoreCase("Processing")) {
                                Ll_cancelTrip.setVisibility(View.GONE);
                                Ll_payment.setVisibility(View.VISIBLE);
                                Ll_mailInvoice.setVisibility(View.GONE);
                                Ll_reportIssue.setVisibility(View.GONE);
                                Rl_favorite.setVisibility(View.GONE);
                            }

                            if (itemlist.get(0).getDoCancelAction().equalsIgnoreCase("1")) {
                                Ll_cancelTrip.setVisibility(View.VISIBLE);
                                Ll_payment.setVisibility(View.GONE);
                                Ll_mailInvoice.setVisibility(View.GONE);
                                Ll_reportIssue.setVisibility(View.GONE);
                                Rl_favorite.setVisibility(View.GONE);
                                Rl_main_tip.setVisibility(View.GONE);
                                Rl_tip.setVisibility(View.GONE);
                                Ll_deleteTip.setVisibility(View.GONE);
                                Cb_tip.setChecked(false);
                            }

                            if (itemlist.get(0).getRideStatus().equalsIgnoreCase("Completed")) {
                                Ll_cancelTrip.setVisibility(View.GONE);
                                Ll_payment.setVisibility(View.GONE);
                                Ll_mailInvoice.setVisibility(View.VISIBLE);
                                Ll_reportIssue.setVisibility(View.VISIBLE);
                                Rl_favorite.setVisibility(View.VISIBLE);
                                Rl_main_tip.setVisibility(View.GONE);
                                Rl_tip.setVisibility(View.GONE);
                                Ll_deleteTip.setVisibility(View.GONE);
                                Cb_tip.setChecked(false);

                                //Programmatically making textView to left of layout
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.LEFT_OF, R.id.my_rides_detail_favorite_layout);
                                params.addRule(RelativeLayout.CENTER_VERTICAL, R.id.my_rides_detail_favorite_layout);
                                Tv_rideStatus.setLayoutParams(params);
                            }


                            //------Show and Hide the Button Layout------
                            if (itemlist.get(0).getPay_status().equalsIgnoreCase("Pending") || itemlist.get(0).getPay_status().equalsIgnoreCase("Processing") || itemlist.get(0).getDoCancelAction().equalsIgnoreCase("1") || itemlist.get(0).getRideStatus().equalsIgnoreCase("Completed") || itemlist.get(0).getRideStatus().equalsIgnoreCase("Onride")) {
                                Rl_button.setVisibility(View.VISIBLE);
                            } else {
                                Rl_button.setVisibility(View.GONE);
                            }

                            //---------Changing Favourite Color-----
                            if (itemlist.get(0).getDoFavLocation().equalsIgnoreCase("1")) {
                                Rl_favorite.setVisibility(View.VISIBLE);
                                if (itemlist.get(0).getIsFavLocation().equalsIgnoreCase("1")) {
                                    Iv_favorite.setImageResource(R.drawable.heart_red_icon);
                                    Iv_favorite.setEnabled(true);
                                } else {
                                    Iv_favorite.setImageResource(R.drawable.heart_grey_icon);
                                    Iv_favorite.setEnabled(true);
                                }
                            } else {
                                Rl_favorite.setVisibility(View.GONE);
                            }
                            //------Show and Hide Track Ride Button Layout------
                            if (itemlist.get(0).getDoTrackAction().equalsIgnoreCase("1")) {
                                Ll_trackRide.setVisibility(View.VISIBLE);
                                Ll_share_Ride.setVisibility(View.VISIBLE);
                            } else {
                                Ll_trackRide.setVisibility(View.GONE);
                                Ll_share_Ride.setVisibility(View.GONE);
                            }
                        }
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


    //-----------------------MyRide Cancel Reason Post Request-----------------
    private void postRequest_CancelRides_Reason(String Url) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        System.out.println("-------------MyRide Cancel Reason Url1----------------" + Url);
        System.out.println("user_id-------------" + UserID);
        System.out.println("ride_id-------------" + SrideId_intent);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", UserID);
        jsonParams.put("ride_id", SrideId_intent);
        jsonParams.put("user_type", "user");
        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------MyRide Cancel Reason Response1----------------" + response);

                String Sstatus = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {

                            Object check_reason_object = response_object.get("reason");
                            if (check_reason_object instanceof JSONArray) {

                                JSONArray reason_array = response_object.getJSONArray("reason");
                                if (reason_array.length() > 0) {
                                    itemlist_reason.clear();
                                    for (int i = 0; i < reason_array.length(); i++) {
                                        JSONObject reason_object = reason_array.getJSONObject(i);
                                        CancelTripPojo pojo = new CancelTripPojo();
                                        pojo.setReason(reason_object.getString("reason"));
                                        pojo.setReasonId(reason_object.getString("id"));

                                        itemlist_reason.add(pojo);
                                    }

                                    isReasonAvailable = true;
                                } else {
                                    isReasonAvailable = false;
                                }
                            }
                        }
                    } else {
                        String Sresponse = object.getString("response");
                        AlertCancel(getResources().getString(R.string.alert_label_title), Sresponse);
                    }

                    if (Sstatus.equalsIgnoreCase("1") && isReasonAvailable) {
                        Intent passIntent = new Intent(MyRidesDetail1.this, MyRideCancelTrip.class);
                        Bundle bundleObject = new Bundle();
                        bundleObject.putSerializable("Reason", itemlist_reason);
                        passIntent.putExtras(bundleObject);
                        passIntent.putExtra("RideID", SrideId_intent);
                        startActivity(passIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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


    //-----------------------MyRide Email Invoice Post Request-----------------
    private void postRequest_EmailInvoice(String Url, final String Semail, final String SrideId) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_sending_invoice));

        System.out.println("-------------MyRide Email Invoice Url----------------" + Url);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("email", Semail);
        jsonParams.put("ride_id", SrideId);

        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                String Sstatus = "", Sresponse = "";
                try {
                    System.out.println("-------------mail_invoice response---------------" + response);
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Sresponse = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        invoice_dialog.dismiss();
                        Alert(getResources().getString(R.string.action_success), Sresponse);
                    } else {
                        Sresponse = object.getString("response");
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


    //-----------------------Favourite Save Post Request-----------------
    private void postRequest_FavoriteSave(String Url) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_saving));


        System.out.println("-------------Favourite Save Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("title", Et_dialog_FavouriteTitle.getText().toString());
        jsonParams.put("latitude", Str_LocationLatitude);
        jsonParams.put("longitude", Str_LocationLongitude);
        jsonParams.put("address", itemlist.get(0).getAddress());

        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Favourite Save Response----------------" + response);
                String Sstatus = "", Smessage = "",Sloc_key = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("message");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Sloc_key = object.getString("loc_key");
                    }

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(Et_dialog_FavouriteTitle.getWindowToken(), 0);

                    if (Sstatus.equalsIgnoreCase("1")) {

                        final PkDialog mDialog = new PkDialog(MyRidesDetail1.this);
                        mDialog.setDialogTitle(getResources().getString(R.string.action_success));
                        mDialog.setDialogMessage(Smessage);
                        final String finalSloc_key = Sloc_key;
                        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                favourite_dialog.dismiss();
                                Iv_favorite.setImageResource(R.drawable.heart_red_icon);
                                itemlist.get(0).setIsFavLocation("1");
                                itemlist.get(0).setLocation_key(finalSloc_key);
                            }
                        });
                        mDialog.show();

                    } else {
                        Alert(getResources().getString(R.string.alert_label_title), Smessage);
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

    //-----------------------Favourite List Delete Post Request-----------------
    private void postRequest_FavoriteDelete(String Url, final String locationKey) {
        dialog = new Dialog(MyRidesDetail1.this);
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
        mRequest = new ServiceRequest(MyRidesDetail1.this);
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
                       /* MyRideDetailPojo myRideDetailPojo = new MyRideDetailPojo();
                        myRideDetailPojo.setIsFavLocation("0");
                        itemlist.set(0,myRideDetailPojo);*/
                        itemlist.get(0).setIsFavLocation("0");

                        Iv_favorite.setImageResource(R.drawable.heart_grey_icon);
                        Iv_favorite.setEnabled(true);

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


    //-----------------------Track Ride Post Request-----------------
    private void postRequest_TrackRide(String Url) {
        dialog = new Dialog(MyRidesDetail1.this);
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

        mRequest = new ServiceRequest(MyRidesDetail1.this);
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
                        Intent i = new Intent(MyRidesDetail1.this, MyRideDetailTrackRide.class);
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


                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
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


    //----------------------------------Share Trip post reques------------------------
    private void share_trip_postRequest_MyRides(Context mContext, String url, String key) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        System.out.println("------------- ride_id----------------" + SrideId_intent);
        System.out.println("------------- mobile_no----------------" + dialCodeno + Et_share_trip_mobileno.getText().toString());

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);
        jsonParams.put("mobile_no", dialCodeno + Et_share_trip_mobileno.getText().toString());

        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {
                Log.e("share trip", response);

                String Str_status = "", Str_response = "";
                System.out.println("sharetrip response-------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");

                    if (Str_status.equalsIgnoreCase("1")) {

                        AlertCloseShare(getResources().getString(R.string.action_success), Str_response);

                    } else {
                        AlertCloseShare(getResources().getString(R.string.action_error), Str_response);

                    }

                } catch (Exception e) {
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

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static final boolean isValidCodeNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return true;
        }
    }

    //-----------------------Tip Post Request-----------------
    private void postRequest_Tip(String Url, final String tipStatus) {
        dialog = new Dialog(MyRidesDetail1.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        System.out.println("-------------tip Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("ride_id", SrideId_intent);
        if (tipStatus.equalsIgnoreCase("Apply")) {
            jsonParams.put("tips_amount", Et_tip_Amount.getText().toString());
        }

        mRequest = new ServiceRequest(MyRidesDetail1.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------tip Response----------------" + response);
                String sStatus = "", sResponse = "", sTipAmount = "", sTotalBill = "";
                try {

                    JSONObject object = new JSONObject(response);
                    sStatus = object.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {

                        JSONObject response_Object = object.getJSONObject("response");
                        sTipAmount = response_Object.getString("tips_amount");
                        sTotalBill = response_Object.getString("total");

                        if (tipStatus.equalsIgnoreCase("Apply")) {
                            Tv_tipAmount.setText(currencySymbol + sTipAmount);
                            Tv_totalBill.setText(currencySymbol + sTotalBill);
                            Rl_main_tip.setVisibility(View.GONE);
                            Rl_tip.setVisibility(View.GONE);
                            Ll_deleteTip.setVisibility(View.VISIBLE);
                        } else {
                            Tv_totalBill.setText(currencySymbol + sTotalBill);
                            Tv_tipAmount.setText(currencySymbol + sTipAmount);
                            Cb_tip.setChecked(false);
                            Et_tip_Amount.setText("");
                            Rl_main_tip.setVisibility(View.GONE);
                            Ll_deleteTip.setVisibility(View.GONE);
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


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            if ("push".equalsIgnoreCase(sType)) {
                Intent i = new Intent(MyRidesDetail1.this, NavigationDrawer.class);
                startActivity(i);
            } else {
                onBackPressed();
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }


    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + sSelectedPanicNumber));
                    startActivity(callIntent);
                }
                break;
        }
    }
}
