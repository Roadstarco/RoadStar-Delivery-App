package com.shrinkcom.fragment;

/**
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.mylibrary.InterFace.CallBack;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.googlemapdrawpolyline.GMapV2GetRouteDirection;
import com.mylibrary.gps.GPSTracker;
import com.mylibrary.materialprogresswheel.ProgressWheel;
import com.mylibrary.volley.ServiceRequest;
import com.shrinkcom.app.UserPickupInfoActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.shrinkcom.adapter.BookMyRide_Adapter;
import com.shrinkcom.adapter.SelectCarTypeAdapter;
import com.shrinkcom.adapter.SelectSeatAdapter;
import com.shrinkcom.app.AdsPage;
import com.shrinkcom.app.DropLocationSelect;
import com.shrinkcom.app.EstimateDetailPage;
import com.shrinkcom.app.FavoriteList;
import com.shrinkcom.app.MyRideDetailTrackRide;
import com.shrinkcom.app.NavigationDrawer;
import com.shrinkcom.app.TimerPage;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.EstimateDetailPojo;
import com.shrinkcom.pojo.HomePojo;
import com.shrinkcom.pojo.PoolRateCard;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.GeocoderHelper;
import com.shrinkcom.utils.HorizontalListView;
import com.shrinkcom.utils.MapAnimator;
import com.shrinkcom.utils.RouteEvaluator;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;


public class Fragment_HomePage extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final static int REQUEST_LOCATION = 299;
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static View rootview;
    LinearLayout bottom_layout;
    String time, unit;
    GPSTracker gps;
    String SselectedAddress = "";
    String Sselected_latitude = "", Sselected_longitude = "";
    ArrayList<HomePojo> driver_list = new ArrayList<HomePojo>();
    ArrayList<HomePojo> category_list = new ArrayList<HomePojo>();
    ArrayList<HomePojo> ratecard_list = new ArrayList<HomePojo>();
    Dialog dialog;
    BroadcastReceiver logoutReciver;
    String selectedCar = "";
    //-----Declaration For Enabling Gps-------
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    ArrayList<EstimateDetailPojo> ratecard_list1 = new ArrayList<EstimateDetailPojo>();
    ArrayList<PoolRateCard> poolRateCardList = new ArrayList<PoolRateCard>();
    Bitmap bmp;
    OnCameraChangeListener mOnCameraChangeListener;
    int count = 0;
    int seconds = 0;
    RelativeLayout R_share, R_normal;
    Animation slideUpAnimation, slideDownAnimation, fadeInAnimation, fadeOutAnimation;
    private int search_status = 0;
    private boolean sShare_ride, sShare_changed;
    private String SdestinationLatitude = "";
    private String SdestinationLongitude = "";
    private String SdestinationLocation = "";
    private TextView tv_apply, tv_cancel;
    private RelativeLayout drawer_layout;
    private RelativeLayout address_layout, favorite_layout;
    private RelativeLayout loading_layout;
    private RelativeLayout alert_layout;
    private TextView alert_textview, tv_estimate;
    private ImageView center_marker, currentLocation_image, center_icon;
    private TextView map_address,map_address_dropoff, destination_address, source_address;
    private  RelativeLayout map_address_dropofflayout;
    private RelativeLayout rideLater_layout, rideNow_layout, R_pickup;
    private TextView rideLater_textview, rideNow_textview;
    private RelativeLayout Rl_Confirm_Back;
    private Context context;
    private ProgressBar progressWheel;
    private ProgressWheel progressWheel1;
    private TextView Tv_walletAmount;
    private TextView Tv_marker_time, Tv_marker_min;
    private LinearLayout Ll_marker_time;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GoogleMap googleMap;
    private MarkerOptions marker;
    private CardView book_cardview_destination_address_layout;
    private ServiceRequest mRequest;
    private SessionManager session;
    private String UserID = "", CategoryID = "", sCurrencySymbolseat;
    private String CarAvailable = "";
    private String ScarType = "";
    private String selectedType = "";
    private boolean driver_status = false;
    private boolean category_status = false;
    private boolean ratecard_status = false;
    private boolean main_response_status = false;
    private double MyCurrent_lat = 0.0, MyCurrent_long = 0.0;
    private double Recent_lat = 0.0, Recent_long = 0.0;
    private BookMyRide_Adapter adapter;
    private HorizontalListView listview;
    private RelativeLayout ridenow_option_layout, estimate_layout_new;
    private RelativeLayout carType_layout, pickTime_layout;
    private LinearLayout coupon_layout;
    private TextView tv_carType, tv_pickuptime, tv_coupon_label, Tv_no_cabs;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMM/dd,hh:mm aa");
    private SimpleDateFormat coupon_mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat coupon_time_mFormatter = new SimpleDateFormat("hh:mm aa");
    private SimpleDateFormat mTime_Formatter = new SimpleDateFormat("HH");
    //------Declaration for Coupon code-----
    private RelativeLayout coupon_apply_layout, coupon_loading_layout, coupon_allowance_layout;
    private MaterialDialog coupon_dialog;
    private EditText coupon_edittext;
    private int destination_address_request_code = 800;
    private int waiting_request_booking_time= 10000;

    //----------------------Code for TextWatcher-------------------------
    private final TextWatcher EditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //clear error symbol after entering text
            if (coupon_edittext.getText().length() > 0) {
                coupon_edittext.setHint("");
            }
        }
    };
    private String coupon_selectedDate = "", language_code = "";
    private String coupon_selectedTime = "", sSurgeContent = "";
    private String Str_couponCode = "";
    private TextView coupon_allowance;
    private ImageView Iv_coupon_cancel_Icon;
    private String sCouponAllowanceText = "";
    //------Declaration for Confirm Ride-----
    private String response_time = "", riderId = "";
    private int timer_request_code = 100;
    private int placeSearch_request_code = 200;
    private int placeSearch_dest_request_code = 201;
    private int eta_placeSearch_request_code = 500;
    private int favoriteList_request_code = 300;
    private boolean ratecard_clicked = true;
    private ServiceRequest estimate_mRequest;
    private boolean isEstimateAvailable = false;
    private RelativeLayout Rl_CenterMarker;
    private boolean isLoading = false;
    private boolean backStatus = false, backListner = false;
    private Boolean isDataLoaded = false;
    private String time_out = "0", pool_option = "0", share_pool_status, pool_type = "0";
    private Handler mapHandler = new Handler();
    private LatLng destlatlng, startlatlng;
    private TextView Tv_surge, tv_CategoryDetail, tv_estimate_label, tv_normal_label, tv_normal_value, tv_share_label, tv_share_value, tv_share_spinner_count;
    private String sCategoryName, sCategoryETA;
    private Boolean isPathShowing;
    private String displayTime = "";
    private ArrayList<LatLng> wayPointList;
    private LatLngBounds.Builder wayPointBuilder;
    private List<Polyline> polyLines;
    private Polyline backgroundPolyline;
    private Polyline foregroundPolyline;
    private PolylineOptions optionsForeground;
    private AnimatorSet firstRunAnimSet;
    private AnimatorSet secondLoopRunAnimSet;
    RoutingListener listner = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {

            System.out.println("-----------jai onRoutingFailure-----------------" + e);

            GetRouteTask1 getRoute = new GetRouteTask1(startlatlng, destlatlng);
            getRoute.execute();
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
            if (foregroundPolyline != null) foregroundPolyline.remove();
            if (backgroundPolyline != null) backgroundPolyline.remove();

            PolylineOptions optionsBackground = new PolylineOptions().addAll(arrayList.get(0).getPoints()).color(Color.parseColor("#FFA7A6A6")).width(8);
            backgroundPolyline = googleMap.addPolyline(optionsBackground);

            PolylineOptions optionsForeground = new PolylineOptions().addAll(arrayList.get(0).getPoints()).color(Color.BLACK).width(8);
            foregroundPolyline = googleMap.addPolyline(optionsForeground);


            if (wayPointBuilder != null) {
                LatLngBounds bounds = wayPointBuilder.build();
                int padding = 310; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cu);
            }

            animateRoute(googleMap, arrayList.get(0).getPoints());
        }

        @Override
        public void onRoutingCancelled() {

        }
    };
    private Handler mHandler = new Handler();
    Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {

            System.out.println("-----------***********----jai-----RETRY-------************------------");
            RetryRideRequest(Iconstant.retry_ride_url, riderId);
            mapHandler.postDelayed(this, Long.parseLong(time_out) * 1000);

        }
    };
    Runnable mRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (count < seconds) {
                count++;
                System.out.println("-----prabu--user timer---------" + count);
                mHandler.postDelayed(this, 1000);
            } else {
                mHandler.removeCallbacks(this);
                mapHandler.removeCallbacks(mapRunnable);
                System.out.println("---prabu----user timer--finised-------" + count);
                if (mRequest != null) {
                    mRequest.cancelRequest();
                }
//                DeleteRideRequest(Iconstant.delete_ride_url);


            }
        }
    };
    private boolean selected_category;
    private TextView tv_share;
    private LinearLayout normal_ride, share_ride, share_count;
    private String rideType, eta_category_id, eta_share_category_id;
    private boolean isTrackRideAvailable = false;
    private boolean isRidePickUpAvailable = false;
    private boolean isRideDropAvailable = false;
    private boolean isLocationType = false;
    CallBack callBack = new CallBack() {
        @Override
        public void onComplete(String LocationName) {
            System.out.println("-------------------addreess----------------0" + LocationName);

            if (LocationName != null) {
                if (!isLocationType) {
                    map_address.setText(LocationName);
                    SselectedAddress = LocationName;
                }
                if (main_response_status && driver_status) {
                    rideNow_layout.setEnabled(true);
                    rideLater_layout.setEnabled(true);
                    Rl_CenterMarker.setEnabled(true);
                    Rl_CenterMarker.setClickable(true);
                   /* if (bottom_layout.getVisibility() != View.VISIBLE) {
                        bottom_layout.startAnimation(slideUpAnimation);
                        bottom_layout.setVisibility(View.VISIBLE);
                    }*/
                    bottomLayoutCliclableMethod(true);
                }
                isLocationType = false;
            } else {
                map_address.setText("");
                SselectedAddress = "";
               /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                    bottom_layout.startAnimation(slideDownAnimation);
                }*/
                bottomLayoutCliclableMethod(false);
                rideNow_layout.setEnabled(true);
                rideLater_layout.setEnabled(false);
                Rl_CenterMarker.setEnabled(false);
                Rl_CenterMarker.setClickable(false);
                isLocationType = false;
            }
        }

        @Override
        public void onError(String errorMsg) {

        }
    };
    private String catrgory_name = "";
    private String sha_catrgory_name = "";
    private boolean isClick = true;
    private String currentVersion = "";
    private boolean btnClickFlag = false;
    private String normal_est_amount = "";
    private boolean normalRideClick = true;
    private boolean normalRideStatus = false;
    private boolean shareRideClick = true;
    private ImageView sharerideactive_image, normalrideactive_image;

    //----------------DatePicker Listener------------
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            Date d = cal.getTime();

            String currentTime = mTime_Formatter.format(d);
            String selectedTime = mTime_Formatter.format(date);
            displayTime = mFormatter.format(date);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sTodayDate = sdf.format(new Date());
            String sSelectedDate = sdf.format(date);


            //     Tv_serviceNow.setTextColor(Color.parseColor("#ffffff "));

            if (selectedTime.equalsIgnoreCase("00")) {
                selectedTime = "24";
            }

            if (sTodayDate.equalsIgnoreCase(sSelectedDate)) {
                if (Integer.parseInt(currentTime) <= Integer.parseInt(selectedTime)) {

                    if (Integer.parseInt(selectedTime) - Integer.parseInt(currentTime) == 0) {
                        Calendar c = Calendar.getInstance();
                        int CurrentMinute = c.get(Calendar.MINUTE);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int SelectedMinutes = calendar.get(Calendar.MINUTE);

                        if (CurrentMinute <= SelectedMinutes) {
                            if (selectedType.equalsIgnoreCase("1")) {
                                rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                rideNow_layout.setClickable(true);
                            }

                            coupon_selectedDate = coupon_mFormatter.format(date);
                            coupon_selectedTime = coupon_time_mFormatter.format(date);


                            R_share.setVisibility(View.GONE);
                            tv_share.setVisibility(View.GONE);
                            tv_share.setVisibility(View.GONE);
                            R_normal.setVisibility(View.VISIBLE);
                            rideType = "normal";


                            //--------Disabling the map functionality---------
                            //          googleMap.getUiSettings().setAllGesturesEnabled(false);
                            currentLocation_image.setClickable(false);
                            currentLocation_image.setVisibility(View.GONE);

                            pickTime_layout.setEnabled(true);
                            drawer_layout.setEnabled(false);
                            address_layout.setEnabled(false);
                            //destination_address_layout.setVisibility(View.GONE);
                            //destination_address_layout.setEnabled(false);
                            favorite_layout.setEnabled(false);

                            source_address.setText(map_address.getText().toString());
                            destination_address.setText(getResources().getString(R.string.action_enter_drop_location));

                            /*Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                            ridenow_option_layout.startAnimation(animFadeIn);*/
                            ridenow_option_layout.setVisibility(View.VISIBLE);
                            rideNowOptionLayoutCliclableMethod(true);
                            center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                            Rl_CenterMarker.setVisibility(View.INVISIBLE);
                            center_marker.setEnabled(false);
                            //    Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                            //   tv_CategoryDetail.setText(ScarType+","+CarAvailable+" "+getResources().getString(R.string.away_label));
                            tv_CategoryDetail.setText(ScarType + "," + displayTime);
                            listview.setVisibility(View.INVISIBLE);
                           /* if (listview.getVisibility() == View.VISIBLE) {
                                listview.startAnimation(slideDownAnimation);
                                listview.setVisibility(View.INVISIBLE);
                            }*/
                            categoryListviewCliclableMethod(false);

                            rideLater_layout.setVisibility(View.GONE);
                            tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                            tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                            R_pickup.setVisibility(View.GONE);
                            center_icon.setVisibility(View.INVISIBLE);
                            rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                            rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));
                            rideNow_layout.setEnabled(true);

                            tv_carType.setText(ScarType);
                            tv_pickuptime.setText(displayTime);
                            NavigationDrawer.disableSwipeDrawer();
                            Str_couponCode = "";
                            session.setCouponCode("");
                            tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                            tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));
                            backStatus = true;
                        } else {
                            Alert(getActivity().getResources().getString(R.string.alert_label_ridelater_title), getActivity().getResources().getString(R.string.alert_label_ridelater_content));
                        }

                    } else {
                        if (selectedType.equalsIgnoreCase("1")) {
                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                            rideNow_layout.setClickable(true);
                        }

                        coupon_selectedDate = coupon_mFormatter.format(date);
                        coupon_selectedTime = coupon_time_mFormatter.format(date);


                        R_share.setVisibility(View.GONE);
                        tv_share.setVisibility(View.GONE);
                        R_normal.setVisibility(View.VISIBLE);
                        rideType = "normal";

                        //--------Disabling the map functionality---------
                        //             googleMap.getUiSettings().setAllGesturesEnabled(false);
                        currentLocation_image.setClickable(false);
                        currentLocation_image.setVisibility(View.GONE);

                        pickTime_layout.setEnabled(true);
                        drawer_layout.setEnabled(false);
                        address_layout.setEnabled(false);
                        //destination_address_layout.setVisibility(View.GONE);
                        //destination_address_layout.setEnabled(false);
                        favorite_layout.setEnabled(false);

                        source_address.setText(map_address.getText().toString());
                        destination_address.setText(getResources().getString(R.string.action_enter_drop_location));

                        /*Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                        ridenow_option_layout.startAnimation(animFadeIn);*/
                        ridenow_option_layout.setVisibility(View.VISIBLE);
                        rideNowOptionLayoutCliclableMethod(true);
                        center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                        Rl_CenterMarker.setVisibility(View.INVISIBLE);
                        center_marker.setEnabled(false);
                        //   Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                        listview.setVisibility(View.INVISIBLE);
                       /* if (listview.getVisibility() == View.VISIBLE) {
                            listview.startAnimation(slideDownAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(false);
                        rideLater_layout.setVisibility(View.GONE);
                        tv_CategoryDetail.setText(ScarType + "," + displayTime);
                        // tv_CategoryDetail.setText(ScarType+","+CarAvailable+" "+getResources().getString(R.string.away_label));
                        rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                        center_icon.setVisibility(View.INVISIBLE);
                        rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));
                        rideNow_layout.setEnabled(true);
                        R_pickup.setVisibility(View.GONE);
                        tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                        tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                        tv_carType.setText(ScarType);
                        tv_pickuptime.setText(displayTime);
                        NavigationDrawer.disableSwipeDrawer();
                        Str_couponCode = "";
                        session.setCouponCode("");
                        tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                        tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));
                        backStatus = true;
                    }

                } else {
                    Alert_cal(getActivity().getResources().getString(R.string.alert_label_ridelater_title), getActivity().getResources().getString(R.string.alert_label_ridelater_content));
                }
            } else {

                backStatus = true;
                if (selectedType.equalsIgnoreCase("1")) {
                    rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                    rideNow_layout.setClickable(true);
                }

                coupon_selectedDate = coupon_mFormatter.format(date);
                coupon_selectedTime = coupon_time_mFormatter.format(date);


                R_share.setVisibility(View.GONE);
                tv_share.setVisibility(View.GONE);
                tv_share.setVisibility(View.GONE);
                R_normal.setVisibility(View.VISIBLE);
                rideType = "normal";

                //--------Disabling the map functionality---------
                //           googleMap.getUiSettings().setAllGesturesEnabled(false);
                currentLocation_image.setClickable(false);
                currentLocation_image.setVisibility(View.GONE);

                pickTime_layout.setEnabled(true);
                drawer_layout.setEnabled(false);
                address_layout.setEnabled(false);
                //destination_address_layout.setVisibility(View.GONE);
                //destination_address_layout.setEnabled(false);
                favorite_layout.setEnabled(false);

                source_address.setText(map_address.getText().toString());
                destination_address.setText(getResources().getString(R.string.action_enter_drop_location));
                R_pickup.setVisibility(View.GONE);

               /* Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                ridenow_option_layout.startAnimation(animFadeIn);*/
                ridenow_option_layout.setVisibility(View.VISIBLE);
                rideNowOptionLayoutCliclableMethod(true);

                center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                Rl_CenterMarker.setVisibility(View.INVISIBLE);
                center_marker.setEnabled(false);
                tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                //   Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                listview.setVisibility(View.INVISIBLE);
               /* if (listview.getVisibility() == View.VISIBLE) {
                    listview.startAnimation(slideDownAnimation);
                    listview.setVisibility(View.INVISIBLE);
                }*/
                categoryListviewCliclableMethod(false);
                tv_CategoryDetail.setText(ScarType + "," + displayTime);
                //   tv_CategoryDetail.setText(ScarType+","+CarAvailable+" "+getResources().getString(R.string.away_label));
                rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                center_icon.setVisibility(View.INVISIBLE);
                rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));
                rideNow_layout.setEnabled(true);
                rideLater_layout.setVisibility(View.GONE);
                tv_carType.setText(ScarType);
                tv_pickuptime.setText(displayTime);
                NavigationDrawer.disableSwipeDrawer();
            }
            btnClickFlag = false;
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
            btnClickFlag = false;
            System.out.println("--------------------hsdksdfjsdfjsjfj-------------");
            //       Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.home_page_toast_cancel), Toast.LENGTH_SHORT).show();
        }
    };
    //----------------DatePicker Secondary Listener------------
    private SlideDateTimeListener Sublistener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            Date d = cal.getTime();
            String currenttime = mTime_Formatter.format(d);
            String selecedtime = mTime_Formatter.format(date);
            String displaytime = mFormatter.format(date);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sTodayDate = sdf.format(new Date());
            String sSelectedDate = sdf.format(date);


            if (selecedtime.equalsIgnoreCase("00")) {
                selecedtime = "24";
            }
            if (sTodayDate.equalsIgnoreCase(sSelectedDate)) {
                if (Integer.parseInt(currenttime) <= Integer.parseInt(selecedtime)) {

                    if (Integer.parseInt(selecedtime) - Integer.parseInt(currenttime) == 0) {
                        Calendar c = Calendar.getInstance();
                        int CurrentMinute = c.get(Calendar.MINUTE);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int SelectedMinutes = calendar.get(Calendar.MINUTE);

                        if (CurrentMinute <= SelectedMinutes) {
                            coupon_selectedDate = coupon_mFormatter.format(date);
                            coupon_selectedTime = coupon_time_mFormatter.format(date);

                            tv_pickuptime.setText(displaytime);
                        } else {
                            Alert(getActivity().getResources().getString(R.string.alert_label_ridelater_title), getActivity().getResources().getString(R.string.alert_label_ridelater_content));
                        }

                    } else {
                        coupon_selectedDate = coupon_mFormatter.format(date);
                        coupon_selectedTime = coupon_time_mFormatter.format(date);

                        tv_pickuptime.setText(displaytime);
                    }

                } else {
                    Alert(getActivity().getResources().getString(R.string.alert_label_ridelater_title), getActivity().getResources().getString(R.string.alert_label_ridelater_content));
                }
            } else {
                coupon_selectedDate = coupon_mFormatter.format(date);
                coupon_selectedTime = coupon_time_mFormatter.format(date);

                tv_pickuptime.setText(displaytime);
            }

           /* if (sTodayDate.equalsIgnoreCase(sSelectedDate)) {
                if (Integer.parseInt(currenttime) <= Integer.parseInt(selecedtime)) {
                    coupon_selectedDate = coupon_mFormatter.format(date);
                    coupon_selectedTime = coupon_time_mFormatter.format(date);

                    tv_pickuptime.setText(displaytime);
                } else {
                    Alert(getActivity().getResources().getString(R.string.alert_label_ridelater_title), getActivity().getResources().getString(R.string.alert_label_ridelater_content));
                }
            } else {
                coupon_selectedDate = coupon_mFormatter.format(date);
                coupon_selectedTime = coupon_time_mFormatter.format(date);

                tv_pickuptime.setText(displaytime);
            }*/

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
        }
    };

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.homepage, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        context = getActivity();
        initialize(rootview);
        initializeMap();
        //Start XMPP Chat Service
//        ChatService.startUserAction(getActivity());
        // Finishing the activity using broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.logout");
        filter.addAction("com.mylibrary.pushnotification.updateBottom_view");
        filter.addAction("com.handler.stop");

        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.app.logout")) {
                    getActivity().finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.updateBottom_view")) {
                    backStatus = false;

                    /* System.out.println("----jaipooja--------------home page refresh--------");*/
                    sShare_changed = false;
                    sShare_ride = false;
                    tv_share.setVisibility(View.GONE);
                    if (googleMap != null) {
                        googleMap.clear();
                    }
                    googleMap.getUiSettings().setAllGesturesEnabled(true);

                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                    // Enable / Disable zooming functionality
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);


                    googleMap.getUiSettings().setTiltGesturesEnabled(false);
                    googleMap.getUiSettings().setCompassEnabled(true);


                    ridenow_option_layout.setVisibility(View.GONE);

                    Rl_CenterMarker.setVisibility(View.GONE);

                    center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                    Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                    center_marker.setEnabled(true);
                    listview.setVisibility(View.INVISIBLE);
                  /*  if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                        listview.startAnimation(slideUpAnimation);
                        listview.setVisibility(View.INVISIBLE);
                    }*/
                    categoryListviewCliclableMethod(true);
                    R_pickup.setVisibility(View.VISIBLE);
                    isPathShowing = false;
                    if (sSurgeContent.trim().length() > 0) {
                        Tv_surge.setVisibility(View.VISIBLE);
                        Tv_surge.setText(sSurgeContent);
                    } else {
                        Tv_surge.setVisibility(View.GONE);
                    }
                    rideLater_layout.setVisibility(View.VISIBLE);

                    rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                    rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                    currentLocation_image.setClickable(true);
                    currentLocation_image.setVisibility(View.VISIBLE);
                    center_icon.setVisibility(View.VISIBLE);
                    pickTime_layout.setEnabled(true);
                    drawer_layout.setEnabled(true);
                    address_layout.setEnabled(true);
                    //destination_address_layout.setVisibility(View.VISIBLE);
                    //destination_address_layout.setEnabled(true);
                    favorite_layout.setEnabled(true);
                    NavigationDrawer.enableSwipeDrawer();


                    if (gps.canGetLocation() && gps.isgpsenabled()) {

                        if (gps.getLatitude() != 0.0 && gps.getLatitude() != 0.0) {

                            double Dlatitude = gps.getLatitude();
                            double Dlongitude = gps.getLongitude();
                            // Move the camera to last position with a zoom level
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            // Move the camera to last position with a zoom level
                        } else {
                            Alert("", "We cannot able to get your accurate location. Please try again. If you still facing this kind of problem try again in open sky instead of closed area.");
                        }

                    } else {
                        enableGpsService();
                        //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                    }
                } else if (intent.getAction().equals("com.handler.stop")) {
                    System.out.println("handler stops jai");
                    mapHandler.removeCallbacks(mapRunnable);
                    mHandler.removeCallbacks(mRunnable1);
                }

            }
        };
        getActivity().registerReceiver(logoutReciver, filter);



        map_address_dropofflayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), DropLocationSelect.class);
                startActivityForResult(intent, destination_address_request_code);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });



        drawer_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-------drawer_layout---------");
                if (btnClickFlag) {
                    return;
                }
                NavigationDrawer.drawerState = true;
                NavigationDrawer.openDrawer();
            }
        });


        tv_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // sShare_ride=true;

                sShare_changed = true;
                CategoryID = "c-pool";
                if (!destination_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_enter_drop_location))) {
                    sShare_changed = true;
                    R_normal.setVisibility(View.GONE);
                    R_share.setVisibility(View.VISIBLE);
                    tv_share.setVisibility(View.GONE);
                    //  R_normal.setVisibility(View.VISIBLE);
                    rideType = "share_change";
                    EstimatePriceRequest(Iconstant.estimate_price_url, "0");

                } else {
                    search_status = 1;
                    GPSTracker gps = new GPSTracker(getActivity());
                    if (gps.canGetLocation()) {
                        Intent intent = new Intent(getActivity(), DropLocationSelect.class);
                        startActivityForResult(intent, placeSearch_dest_request_code);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Toast.makeText(getActivity(), "Enable Gps", Toast.LENGTH_SHORT)
                                .show();
                    }
                }


            }
        });

        address_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    search_status = 0;
                    isLocationType = true;
                    if (isClick) {
                        isClick = false;
                        openAutocompleteActivity(Recent_lat, Recent_long);
                    }
                }
            }
        });

        book_cardview_destination_address_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                search_status = 1;
                GPSTracker gps = new GPSTracker(getActivity());
                if (gps.canGetLocation()) {
                    Intent intent = new Intent(getActivity(), DropLocationSelect.class);
                    startActivityForResult(intent, placeSearch_dest_request_code);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(getActivity(), "Enable Gps", Toast.LENGTH_SHORT).show();
                }


               /* search_status = 1;
                Intent intent = new Intent(getActivity(), LocationSearch.class);
                intent.putExtra("nearLatitude", String.valueOf(Recent_lat));
                intent.putExtra("nearLongitude",String.valueOf(Recent_long) );
                startActivityForResult(intent, placeSearch_request_code);
                getActivity().overridePendingTransition(R.anim.slideup, R.anim.slidedown);*/
            }
        });

        favorite_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    search_status = 0;
                    isLocationType = true;
                    if (map_address.getText().toString().length() > 0) {
                        Intent intent = new Intent(getActivity(), FavoriteList.class);
                        intent.putExtra("SelectedAddress", SselectedAddress);
                        intent.putExtra("SelectedLatitude", Sselected_latitude);
                        intent.putExtra("SelectedLongitude", Sselected_longitude);
                        startActivityForResult(intent, favoriteList_request_code);
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.favorite_list_label_select_location));
                        btnClickFlag = false;
                    }
                }

            }
        });

        carType_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                select_carType_Dialog();
            }
        });


        /*pickTime_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                Date seventhDay = calendar.getTime();

                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                        .setListener(Sublistener)
                        .setInitialDate(new Date())
                        .setMinDate(new Date())
                        .setMaxDate(seventhDay)
                        //.setIs24HourTime(true)
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .setIndicatorColor(Color.parseColor("#F83C6F"))
                        .build()
                        .show();
            }
        });*/


        normal_ride.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                normalRideStatus = true;
                shareRideClick = false;
                if (normalRideClick) {
                    EstimatePriceRequest(Iconstant.estimate_price_url, "1");

                } else {
                    sharerideactive_image.setVisibility(View.GONE);
                    normalrideactive_image.setVisibility(View.VISIBLE);
                    normalRideClick = true;
                    sShare_changed = false;
//                    normal_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));
//                    share_ride.setBackgroundColor(getResources().getColor(R.color.white));
                    share_count.setVisibility(View.GONE);
                    rideType = "normal";
                    System.out.println("category id" + eta_category_id);
                    CategoryID = eta_category_id;
                    tv_CategoryDetail.setText(catrgory_name + "," + CarAvailable + " " + getResources().getString(R.string.away_label));

                    googleMap.clear();
//                GetRouteTask getRoute = new GetRouteTask();
//                getRoute.execute();

                    GetRouteTask1 getRoute = new GetRouteTask1(startlatlng, destlatlng);
                    getRoute.execute();
                }


            }
        });

        share_ride.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                normalRideClick = false;
                if (shareRideClick) {
                    EstimatePriceRequest(Iconstant.estimate_price_url, "1");
                } else {
                    shareRideClick = true;
                    normalrideactive_image.setVisibility(View.GONE);
                    sharerideactive_image.setVisibility(View.VISIBLE);
//                    share_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));
//                    normal_ride.setBackgroundColor(getResources().getColor(R.color.white));
                    share_count.setVisibility(View.VISIBLE);
                    rideType = "share";
                    System.out.println("category id" + eta_share_category_id);
                    CategoryID = eta_share_category_id;
                    tv_CategoryDetail.setText(sha_catrgory_name + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                    sShare_changed = true;
                    drawpolyline(startlatlng, destlatlng);

//                    EstimatePriceRequest(Iconstant.estimate_price_url, "1");
                }


            }
        });
        share_count.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                select_Shareseat_Dialog();

                //    share_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));

            }
        });

        /*ratecard_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ratecard_clicked) {
                    ratecard_clicked = false;
                    showRateCard();
                }

            }
        });*/


        tv_estimate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!destination_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_enter_drop_location))) {
                    EstimatePriceRequest(Iconstant.estimate_price_url, "1");
                } else {
                    if (isClick) {
                        isClick = false;
                        try {

                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setBoundsBias(toBounds(new LatLng(Recent_lat, Recent_long), 50000)).build(getActivity());
                            startActivityForResult(intent, eta_placeSearch_request_code);
                        } catch (GooglePlayServicesRepairableException e) {

                            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                                    0 /* requestCode */).show();
                        } catch (GooglePlayServicesNotAvailableException e) {

                            String message = "Google Play Services is not available: " +
                                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
        estimate_layout_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!destination_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_enter_drop_location))) {
                    EstimatePriceRequest(Iconstant.estimate_price_url, "1");
                } else {
                    if (isClick) {
                        isClick = false;
                        try {

                            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setBoundsBias(toBounds(new LatLng(Recent_lat, Recent_long), 50000)).build(getActivity());
                            startActivityForResult(intent, eta_placeSearch_request_code);
                        } catch (GooglePlayServicesRepairableException e) {

                            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                                    0 /* requestCode */).show();
                        } catch (GooglePlayServicesNotAvailableException e) {

                            String message = "Google Play Services is not available: " +
                                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        Rl_Confirm_Back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (googleMap != null) {
                    googleMap.clear();
                }

                backStatus = false;
                //Enable and Disable RideNow Button
                if (!driver_status) {

                    Ll_marker_time.setVisibility(View.GONE);
                    Tv_marker_time.setVisibility(View.GONE);
                    Tv_marker_min.setVisibility(View.GONE);
                    Rl_CenterMarker.setVisibility(View.GONE);
                    center_marker.setImageResource(R.drawable.no_cars_available_new);
                    progressWheel.setVisibility(View.INVISIBLE);
                    Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                    // Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                    rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                    //    rideNow_layout.setClickable(false);
                } else {

                    Ll_marker_time.setVisibility(View.VISIBLE);
                    Tv_marker_time.setVisibility(View.VISIBLE);
                    Tv_marker_min.setVisibility(View.VISIBLE);
                    Rl_CenterMarker.setVisibility(View.GONE);
                    center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                    Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                    rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                    rideNow_layout.setClickable(true);
                    progressWheel.setVisibility(View.VISIBLE);
                    Tv_marker_time.setText(time);
                    Tv_marker_min.setText(unit);
                    /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                }

                rideType = "";

                sShare_changed = false;
                sShare_ride = false;
                tv_share.setVisibility(View.GONE);
                /*Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                ridenow_option_layout.startAnimation(animFadeOut);*/
                ridenow_option_layout.setVisibility(View.GONE);
                rideNowOptionLayoutCliclableMethod(false);
                center_marker.setEnabled(true);

                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                // Enable / Disable zooming functionality
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                R_pickup.setVisibility(View.VISIBLE);
                if (sSurgeContent.trim().length() > 0) {
                    Tv_surge.setVisibility(View.VISIBLE);
                    Tv_surge.setText(sSurgeContent);
                } else {
                    Tv_surge.setVisibility(View.GONE);
                }
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(true);
                isPathShowing = false;
                tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                listview.setVisibility(View.INVISIBLE);
              /*  if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                    listview.startAnimation(slideUpAnimation);
                    listview.setVisibility(View.INVISIBLE);
                }*/
                categoryListviewCliclableMethod(true);
                rideLater_layout.setVisibility(View.VISIBLE);
                center_icon.setVisibility(View.VISIBLE);
                rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                currentLocation_image.setClickable(true);
                currentLocation_image.setVisibility(View.VISIBLE);
                pickTime_layout.setEnabled(true);
                drawer_layout.setEnabled(true);
                address_layout.setEnabled(true);
                //destination_address_layout.setVisibility(View.VISIBLE);
                //destination_address_layout.setEnabled(true);
                favorite_layout.setEnabled(true);
                NavigationDrawer.enableSwipeDrawer();


                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    MyCurrent_lat = gps.getLatitude();
                    MyCurrent_long = gps.getLongitude();


                    if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                    } else {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    }
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }

                    // Move the camera to last position with a zoom level

                } else {
                    enableGpsService();
                    //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                }


            }
        });

        coupon_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoupon();
            }
        });

        rideLater_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("---rideLater_layout-----"+NavigationDrawer.drawerState);
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    Log.e("onClick: ", "onClick: " + rideLater_layout );


                    HashMap<String, String> wallet = session.getWalletAmount();
                    String sWalletAmount = wallet.get(SessionManager.KEY_WALLET_AMOUNT);
                    Tv_walletAmount.setText(sWalletAmount);

                    if (rideLater_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_ride_later))) {
                        if (destination_address.getText().toString().length() > 0) {
                            if (map_address.getText().toString().length() > 0) {

                                session.setCouponCode("");
                                tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                                tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));

                                selectedType = "1";
                                Str_couponCode = "";

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date());
                                calendar.add(Calendar.DAY_OF_YEAR, 7);
                                Date seventhDay = calendar.getTime();
                                System.out.println("---------------seventhDay-------------" + seventhDay);


                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.setTime(new Date());
                                calendar1.add(Calendar.HOUR, 1);
                                Date previous_time = calendar1.getTime();
                                System.out.println("------previous_time----" + previous_time);


                                new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
//                                    .setLanguage(language_code)
                                        .setListener(listener)
                                        .setInitialDate(new Date())
                                        .setMinDate(new Date())
                                        .setMaxDate(seventhDay)
                                        //.setIs24HourTime(true)
                                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                                        .setIndicatorColor(Color.parseColor("#F83C6F"))
                                        .build()
                                        .show();

                            } else {
                                btnClickFlag = false;
                                Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_pickUp));
                            }
                        } else {
                            btnClickFlag = false;
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_drop));
                        }

                    } else if (rideLater_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_cancel))) {

                        //Enable and Disable RideNow Button
                        backStatus = false;
                        if (!driver_status) {

                            Ll_marker_time.setVisibility(View.GONE);
                            Tv_marker_time.setVisibility(View.GONE);
                            Tv_marker_min.setVisibility(View.GONE);
                            Rl_CenterMarker.setVisibility(View.GONE);
                            Rl_CenterMarker.setVisibility(View.GONE);
                            center_marker.setImageResource(R.drawable.no_cars_available_new);
                            Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                            progressWheel.setVisibility(View.INVISIBLE);
                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                            //         rideNow_layout.setClickable(false);
                        } else {

                            Ll_marker_time.setVisibility(View.VISIBLE);
                            Tv_marker_time.setVisibility(View.VISIBLE);
                            Tv_marker_min.setVisibility(View.VISIBLE);
                            Rl_CenterMarker.setVisibility(View.GONE);
                            center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                            Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                            rideNow_layout.setClickable(true);

                            Tv_marker_time.setText(time);
                            Tv_marker_min.setText(unit);
                            /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                        }

                       /* Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                        ridenow_option_layout.startAnimation(animFadeOut);*/
                        ridenow_option_layout.setVisibility(View.GONE);
                        rideNowOptionLayoutCliclableMethod(false);
                        center_marker.setEnabled(true);
                        tv_share.setVisibility(View.GONE);
                        googleMap.getUiSettings().setAllGesturesEnabled(true);
                        googleMap.getUiSettings().setRotateGesturesEnabled(false);
                        // Enable / Disable zooming functionality
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        R_pickup.setVisibility(View.VISIBLE);
                        if (sSurgeContent.trim().length() > 0) {
                            Tv_surge.setVisibility(View.VISIBLE);
                            Tv_surge.setText(sSurgeContent);
                        } else {
                            Tv_surge.setVisibility(View.GONE);
                        }
                        isPathShowing = false;
                        googleMap.getUiSettings().setTiltGesturesEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(true);
                        listview.setVisibility(View.INVISIBLE);
                       /* if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                            listview.startAnimation(slideUpAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(true);
                        center_icon.setVisibility(View.VISIBLE);
                        rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                        rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                        currentLocation_image.setClickable(true);
                        currentLocation_image.setVisibility(View.VISIBLE);
                        pickTime_layout.setEnabled(true);
                        drawer_layout.setEnabled(true);
                        address_layout.setEnabled(true);
                        //destination_address_layout.setVisibility(View.VISIBLE);
                        // destination_address_layout.setEnabled(true);
                        favorite_layout.setEnabled(true);
                        NavigationDrawer.enableSwipeDrawer();


                        if (gps.canGetLocation() && gps.isgpsenabled()) {

                            MyCurrent_lat = gps.getLatitude();
                            MyCurrent_long = gps.getLongitude();


                            if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                            } else {
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                            }
                            if (mRequest != null) {
                                mRequest.cancelRequest();
                            }

                            // Move the camera to last position with a zoom level

                        } else {
                            enableGpsService();
                            //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                        }

                    }
                }

            }
        });

        rideNow_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DRAWERSTATAA",">>>>"+NavigationDrawer.drawerState);
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    HashMap<String, String> wallet = session.getWalletAmount();
                    String sWalletAmount = wallet.get(SessionManager.KEY_WALLET_AMOUNT);
                    Tv_walletAmount.setText(sWalletAmount);


                    if (rideNow_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_ride_now))) {
                        selectedType = "0";
                        Str_couponCode = "";

                        if (map_address_dropoff.getText().toString().isEmpty()){
                            map_address_dropoff.setError("Select Drop Location");

                        }else {
                            SessionManager sessionManager = new SessionManager(getActivity());
                            sessionManager.setStringValue(SessionManager.KEY_PICKUP_LOCATION,map_address.getText().toString());
                            sessionManager.setStringValue(SessionManager.KEY_PICKUP_LOCATION_LAT,String.valueOf(Recent_lat));
                            sessionManager.setStringValue(SessionManager.KEY_PICKUP_LOCATION_LON,String.valueOf(Recent_long));
                            sessionManager.setStringValue(SessionManager.KEY_DROP_LOCATION,map_address_dropoff.getText().toString());
                            sessionManager.setStringValue(SessionManager.KEY_DROP_LOCATION_LAT,SdestinationLatitude);
                            sessionManager.setStringValue(SessionManager.KEY_DROP_LOCATION_LON,SdestinationLongitude);


                            Intent intent = new Intent(getActivity(), UserPickupInfoActivity.class);
                            startActivityForResult(intent,waiting_request_booking_time);
                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

                        /*if (!driver_status) {


                            btnClickFlag = false;
                        } else {
                            session.setCouponCode("");
                            if (destination_address.getText().toString().length() > 0) {
                                if (map_address.getText().toString().length() > 0) {

                                    session.setCouponCode("");
                                    tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                                    tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));

                                    //-------getting current date and time---------
                                    coupon_selectedDate = coupon_mFormatter.format(new Date());
                                    coupon_selectedTime = coupon_time_mFormatter.format(new Date());
                                    String displaytime = CarAvailable + " " + getResources().getString(R.string.home_label_fromNow);


                                    R_share.setVisibility(View.GONE);
                                    tv_share.setVisibility(View.GONE);
                                    tv_share.setVisibility(View.GONE);
                                    R_normal.setVisibility(View.VISIBLE);

                                    rideType = "normal";


                                    if (pool_option.equalsIgnoreCase("1")) {
                                        tv_share.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_share.setVisibility(View.GONE);
                                    }

                                    //--------Disabling the map functionality---------
                                    //        googleMap.getUiSettings().setAllGesturesEnabled(false);
                                    currentLocation_image.setClickable(false);
                                    currentLocation_image.setVisibility(View.GONE);
                                    R_pickup.setVisibility(View.GONE);
                                    if (sSurgeContent.trim().length() > 0) {
                                        Tv_surge.setVisibility(View.VISIBLE);
                                        Tv_surge.setText(sSurgeContent);
                                    } else {
                                        Tv_surge.setVisibility(View.GONE);
                                    }

                                    ridenow_option_layout.setVisibility(View.VISIBLE);
                                    rideNowOptionLayoutCliclableMethod(true);

                                    source_address.setText(map_address.getText().toString());
                                    destination_address.setText(getResources().getString(R.string.action_enter_drop_location));
                                    center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                                    Rl_CenterMarker.setVisibility(View.INVISIBLE);
                                    center_marker.setEnabled(false);
                                    progressWheel.setVisibility(View.INVISIBLE);
                                    System.out.println("category_detail--------------------------" + ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                                    tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));

                                    listview.setVisibility(View.INVISIBLE);
                               *//* if (listview.getVisibility() == View.VISIBLE) {
                                    listview.startAnimation(slideDownAnimation);
                                    listview.setVisibility(View.INVISIBLE);
                                }*//*
                                    categoryListviewCliclableMethod(false);
                                    center_icon.setVisibility(View.INVISIBLE);
                                    rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                                    rideLater_layout.setVisibility(View.GONE);
                                    rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));


                                    tv_carType.setText(ScarType);
                                    tv_pickuptime.setText(displaytime);

                                    //----Disabling onClick Listener-----
                                    pickTime_layout.setEnabled(false);
                                    drawer_layout.setEnabled(false);
                                    address_layout.setEnabled(false);
                                    //destination_address_layout.setVisibility(View.VISIBLE);
                                    // destination_address_layout.setEnabled(false);
                                    favorite_layout.setEnabled(false);
                                    NavigationDrawer.disableSwipeDrawer();
                                    backStatus = true;
                                    btnClickFlag = false;
                                } else {
                                    btnClickFlag = false;
                                    Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_pickUp));
                                }
                            } else {
                                btnClickFlag = false;
                                Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_drop));
                            }
                        }*/
                    } else if (rideNow_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_confirm))) {
                        Log.e("destination", ": " + destination_address.getText().toString() + "----------" + getString(R.string.action_enter_drop_location));
                        if (destination_address.getText().toString().length() > 0 && (!destination_address.getText().toString().equals(getString(R.string.action_enter_drop_location)))) {
                            cd = new ConnectionDetector(getActivity());
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent) {

                       /* HashMap<String, String> code = session.getCouponCode();
                        String coupon = code.get(SessionManager.KEY_COUPON_CODE);*/

                                riderId = "";
                                ConfirmRideRequest(Iconstant.confirm_ride_url, Str_couponCode, coupon_selectedDate, coupon_selectedTime, selectedType, CategoryID, map_address.getText().toString(), String.valueOf(Recent_lat), String.valueOf(Recent_long), "", destination_address.getText().toString(), SdestinationLatitude, SdestinationLongitude);
                            } else {
                                Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.alert_nointernet));
                            }
                        } else {
                            btnClickFlag = false;
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_drop));
                        }
                    } else if (rideNow_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_enter_drop_location))) {
                        session.setCouponCode("");
                        if (map_address.getText().toString().length() > 0) {
                            Str_couponCode = "";
                            selectedType = "0";
                            rideType = "share";
                            session.setCouponCode("");
                            tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                            tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));
                            sShare_ride = true;
                            search_status = 1;
                            GPSTracker gps = new GPSTracker(getActivity());
                            if (gps.canGetLocation()) {
                                Intent intent = new Intent(getActivity(), DropLocationSelect.class);
                                startActivityForResult(intent, placeSearch_dest_request_code);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else {
                                Toast.makeText(getActivity(), "Enable Gps", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            btnClickFlag = false;

                        } else {
                            btnClickFlag = false;
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_pickUp));
                        }

                    }
                }


            }
        });


        center_marker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isLoading && main_response_status) {
                    HashMap<String, String> wallet = session.getWalletAmount();
                    String sWalletAmount = wallet.get(SessionManager.KEY_WALLET_AMOUNT);
                    Tv_walletAmount.setText(sWalletAmount);

                    if (driver_status) {

                        backStatus = true;
                        if (map_address.getText().toString().length() > 0) {
                            selectedType = "0";

                            source_address.setText(map_address.getText().toString());
                            destination_address.setText(getResources().getString(R.string.action_enter_drop_location));

                            //-------getting current date and time---------
                            coupon_selectedDate = coupon_mFormatter.format(new Date());
                            coupon_selectedTime = coupon_time_mFormatter.format(new Date());
                            String displaytime = CarAvailable + " " + getResources().getString(R.string.home_label_fromNow);

                            //--------Disabling the map functionality---------
                            //          googleMap.getUiSettings().setAllGesturesEnabled(false);
                            currentLocation_image.setClickable(false);
                            currentLocation_image.setVisibility(View.GONE);

                            /*Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                            ridenow_option_layout.startAnimation(animFadeIn);*/
                            ridenow_option_layout.setVisibility(View.VISIBLE);
                            rideNowOptionLayoutCliclableMethod(true);

                            center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                            Rl_CenterMarker.setVisibility(View.INVISIBLE);
                            center_marker.setEnabled(false);
                            //  Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                            tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                            System.out.println("category_detail--------------------------" + ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                            listview.setVisibility(View.INVISIBLE);
                           /* if (listview.getVisibility() == View.VISIBLE) {
                                listview.startAnimation(slideDownAnimation);
                                listview.setVisibility(View.INVISIBLE);
                            }*/
                            categoryListviewCliclableMethod(false);
                            center_icon.setVisibility(View.INVISIBLE);
                            rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                            rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));
                            rideLater_layout.setVisibility(View.GONE);
                            tv_carType.setText(ScarType);
                            tv_pickuptime.setText(displaytime);

                            //----Disabling onClick Listener-----
                            pickTime_layout.setEnabled(false);
                            drawer_layout.setEnabled(false);
                            address_layout.setEnabled(false);
                            //destination_address_layout.setVisibility(View.GONE);
                            //destination_address_layout.setEnabled(false);
                            favorite_layout.setEnabled(false);
                            NavigationDrawer.disableSwipeDrawer();

                        } else {
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_pickUp));
                        }
                    }
                }


            }
        });


        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    if (category_list.size() > 0) {

                    /*System.out.println("---------cat id"+CategoryID);
                    System.out.println("---------selected_cat id"+selectedCar);*/


                        CarAvailable = category_list.get(position).getCat_time();
                        CategoryID = category_list.get(position).getCat_id();
                        ScarType = category_list.get(position).getCat_name();


                        System.out.println("---------cat id" + CategoryID);
                        System.out.println("---------selected_cat id" + selectedCar);

                        if (CategoryID.equalsIgnoreCase(selectedCar)) {
                            showRateCard();
                            btnClickFlag = false;

                        } else {
                         /*   if (bottom_layout.getVisibility() == View.VISIBLE) {
                                bottom_layout.startAnimation(slideDownAnimation);
                                bottom_layout.setVisibility(View.GONE);
                            }*/
                            bottomLayoutCliclableMethod(false);
                        }

                        cd = new ConnectionDetector(getActivity());
                        isInternetPresent = cd.isConnectingToInternet();


                        if (Recent_lat != 0.0) {
                            //     googleMap.clear();

                            if (isInternetPresent) {
                                if (mRequest != null) {
                                    mRequest.cancelRequest();
                                }
                               /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                                    bottom_layout.startAnimation(slideDownAnimation);
                                    bottom_layout.setVisibility(View.GONE);
                                }*/
                                bottomLayoutCliclableMethod(false);

                                rideNow_layout.setEnabled(true);
                                Rl_CenterMarker.setEnabled(false);
                                rideLater_layout.setEnabled(false);
                                Rl_CenterMarker.setClickable(false);

                                isLoading = true;
                                isLocationType = true;
                                PostRequest(Iconstant.BookMyRide_url, Recent_lat, Recent_long);
                                btnClickFlag = false;

                            } else {
                                btnClickFlag = false;
                                alert_layout.setVisibility(View.VISIBLE);
                                alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                            }
                        } else {
                            btnClickFlag = false;
                        }

                        adapter.notifyDataSetChanged();

                        /* }*/

                    }
                }
            }
        });

        currentLocation_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    cd = new ConnectionDetector(getActivity());
                    isInternetPresent = cd.isConnectingToInternet();
                    gps = new GPSTracker(getActivity());

                   /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                        bottom_layout.startAnimation(slideDownAnimation);
                        bottom_layout.setVisibility(View.GONE);
                    }*/
                    bottomLayoutCliclableMethod(false);
                   /* if (listview.getVisibility() == View.VISIBLE) {
                        listview.startAnimation(slideDownAnimation);
                        listview.setVisibility(View.INVISIBLE);
                    }*/
                    categoryListviewCliclableMethod(false);
                    if (gps.canGetLocation() && gps.isgpsenabled()) {

                        if (gps.getLatitude() != 0.0 && gps.getLatitude() != 0.0) {


                            MyCurrent_lat = gps.getLatitude();
                            MyCurrent_long = gps.getLongitude();


                            if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                            } else {
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                            if (mRequest != null) {
                                mRequest.cancelRequest();
                            }

                            // Move the camera to last position with a zoom level
                        } else {
                            btnClickFlag = false;
                            Alert("", "We cannot able to get your accurate location. Please try again. If you still facing this kind of problem try again in open sky instead of closed area.");
                        }

                    } else {
                        btnClickFlag = false;
                        enableGpsService();
                        //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        mOnCameraChangeListener = new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (!NavigationDrawer.drawerState) {
                    btnClickFlag = true;
                    if (!backStatus) {
                        if (!isPathShowing) {

                           /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                                bottom_layout.startAnimation(slideDownAnimation);
                                bottom_layout.setVisibility(View.GONE);
                            }*/
                            bottomLayoutCliclableMethod(false);
                           /* if (listview.getVisibility() == View.VISIBLE) {
                                listview.startAnimation(slideDownAnimation);
                                listview.setVisibility(View.INVISIBLE);
                            }*/
                            categoryListviewCliclableMethod(false);

                            double latitude = cameraPosition.target.latitude;
                            double longitude = cameraPosition.target.longitude;

                            cd = new ConnectionDetector(getActivity());
                            isInternetPresent = cd.isConnectingToInternet();

                            Log.e("camerachange lat-->", "" + latitude);
                            Log.e("on_camera_change lon-->", "" + longitude);

                            if (latitude != 0.0) {
                                //   googleMap.clear();

                                Recent_lat = latitude;
                                Recent_long = longitude;

                                if (isInternetPresent) {
                                    if (mRequest != null) {
                                        mRequest.cancelRequest();
                                    }

                                    normalRideStatus = false;
                                    shareRideClick = true;
                                    normalRideClick = true;

                                    rideNow_layout.setEnabled(true);
                                    Rl_CenterMarker.setEnabled(false);
                                    rideLater_layout.setEnabled(false);
                                    Rl_CenterMarker.setClickable(false);
                                    isLoading = true;
                                    Str_couponCode = "";
                                    session.setCouponCode("");
                                    tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                                    tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));
                                    PostRequest(Iconstant.BookMyRide_url, latitude, longitude);
                                } else {
                                    btnClickFlag = false;
                                    alert_layout.setVisibility(View.VISIBLE);
                                    alert_textview.setText(getResources().getString(R.string.alert_nointernet));
                                   /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                                        bottom_layout.startAnimation(slideDownAnimation);
                                        bottom_layout.setVisibility(View.GONE);
                                    }*/
                                    bottomLayoutCliclableMethod(false);
                                }
                            } else {
                                btnClickFlag = false;
                            }
                        } else {
                            btnClickFlag = false;
                        }
                    } else {
                        btnClickFlag = false;
                    }
                }
            }
        };

/*
        ride_assist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(getActivity());
                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    if (gps.getLatitude() != 0.0 && gps.getLatitude() != 0.0) {


                        MyCurrent_lat = gps.getLatitude();
                        MyCurrent_long = gps.getLongitude();
                        final EditText taskEditText = new EditText(getContext());
                        taskEditText.setHint("City Name");
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Ride Assist Enter City")
                                .setMessage("Please Enter City Name Where You want to go?")
                                .setView(taskEditText)
                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(taskEditText.getText());
                                        if (task != null && task.length() > 0) {
                                            dialog.dismiss();
                                            rideAssiste(Iconstant.rideassist, task);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                        dialog.show();


                        // Move the camera to last position with a zoom level
                    } else {
                        btnClickFlag = false;
                        Alert("", "We cannot able to get your accurate location. Please try again. If you still facing this kind of problem try again in open sky instead of closed area.");
                    }

                } else {
                    btnClickFlag = false;
                    enableGpsService();
                    //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                }
            }
        });
*/
        return rootview;
    }

    private void rideAssiste(final String Url, final String city) {


        final ProgressDialog progress;
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.action_pleasewait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.show();
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", String.valueOf(MyCurrent_lat));
        jsonParams.put("lon", String.valueOf(MyCurrent_long));
        jsonParams.put("city", city);
        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                if (progress != null) {
                    progress.dismiss();
                }
                Alert("Susses!!", "Thank-you for Ride Assist We Will Contact you Soon.. ");
            }

            @Override
            public void onErrorListener() {
                if (progress != null) {
                    progress.dismiss();
                }
            }
        });
    }

    private void drawpolyline(LatLng startlatlng, LatLng destlatlng) {
        Marker m[] = new Marker[2];
        if (googleMap != null) {
            googleMap.clear();
            m[0] = googleMap.addMarker(new MarkerOptions().position(startlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
            m[1] = googleMap.addMarker(new MarkerOptions().position(destlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));
            isPathShowing = true;

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : m) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 300; // offset from edges of the map in pixels
            CameraUpdate cu = newLatLngBounds(bounds, padding);

            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .add(startlatlng, destlatlng)
                    .width(5)
                    .color(Color.BLACK));

            googleMap.animateCamera(cu);

        }
    }

    private void initializeMap() {
        if (googleMap == null) {


            MapFragment mapFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(
                    R.id.book_my_ride_mapview));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap arg) {
                    loadMap(arg);


                }
            });
        }
    }

    public void loadMap(GoogleMap arg) {
        googleMap = arg;


        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle));

        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(false);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.setMyLocationEnabled(true);


        if (gps.canGetLocation() && gps.isgpsenabled()) {

            double Dlatitude = gps.getLatitude();
            double Dlongitude = gps.getLongitude();

            System.out.println("----------------map lat-------" + Dlatitude);
            System.out.println("----------------map lont-------" + Dlongitude);

            if (Dlatitude != 0.0 && Dlongitude != 0.0) {


                MyCurrent_lat = Dlatitude;
                MyCurrent_long = Dlongitude;

                Recent_lat = Dlatitude;
                Recent_long = Dlongitude;

                // Move the camera to last position with a zoom level
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Dlatitude, Dlongitude)).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                Alert(getActivity().getResources().getString(R.string.timer_label_alert_sorry), getActivity().getResources().getString(R.string.currect_location_fetching_issue));
            }

        } else {
            enableGpsService();
        }

        if (CheckPlayService()) {
            if (googleMap != null) {

                googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
                googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String tittle = marker.getTitle();
                        return true;
                    }
                });
            }
        } else {
            //Toast.makeText(getActivity(), "Install Google Play service To View Location !!!", Toast.LENGTH_LONG).show();
        }


    }

    private void initialize(View rooView) {
        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        session = new SessionManager(getActivity());
        gps = new GPSTracker(getActivity());
        wayPointList = new ArrayList<LatLng>();

        try {
            currentVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        isPathShowing = false;
        drawer_layout = (RelativeLayout) rooView.findViewById(R.id.book_navigation_layout);
        address_layout = (RelativeLayout) rooView.findViewById(R.id.book_navigation_address_layout);

        //destination_address_layout = (RelativeLayout) rooView.findViewById(R.id.book_navigation_destination_address_layout);

//        ride_assist = rooView.findViewById(R.id.ride_assist);
        favorite_layout = (RelativeLayout) rooView.findViewById(R.id.book_navigation_favorite_layout);
        bottom_layout = (LinearLayout) rooView.findViewById(R.id.book_my_ride_bottom_layout);
        map_address = (TextView) rooView.findViewById(R.id.book_navigation_search_address);
        map_address_dropoff = (TextView) rooView.findViewById(R.id.book_navigation_search_addressdeliver);
        map_address_dropofflayout = (RelativeLayout) rooView.findViewById(R.id.book_navigation_main_layoutdeliver);
        source_address = (TextView) rooView.findViewById(R.id.book_navigation_source_address_address_textView);
        destination_address = (TextView) rooView.findViewById(R.id.book_navigation_destination_address_search_address);
        tv_CategoryDetail = (TextView) rooView.findViewById(R.id.book_my_ride_confirm_header_textView);
        R_pickup = (RelativeLayout) rooView.findViewById(R.id.book_navigation_main_layout);
        center_icon = (ImageView) rooView.findViewById(R.id.center_icon);

        loading_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_loading_layout);
        center_marker = (ImageView) rooView.findViewById(R.id.book_my_ride_center_marker);
        alert_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_alert_layout);
        alert_textview = (TextView) rooView.findViewById(R.id.book_my_ride_alert_textView);
        currentLocation_image = (ImageView) rooView.findViewById(R.id.book_current_location_imageview);
        rideLater_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_rideLater_layout);
        rideNow_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_rideNow_layout);
        rideLater_textview = (TextView) rooView.findViewById(R.id.book_my_ride_rideLater_textView);
        rideNow_textview = (TextView) rooView.findViewById(R.id.book_my_ride_rideNow_textview);
        listview = (HorizontalListView) rooView.findViewById(R.id.book_my_ride_listview);
        ridenow_option_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_ridenow_option_layout);
        carType_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_cabtype_layout);
        pickTime_layout = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_pickup_layout);

        estimate_layout_new = (RelativeLayout) rooView.findViewById(R.id.rr);
        tv_estimate = (TextView) rooView.findViewById(R.id.book_my_ride_eta_amount_label);

        coupon_layout = (LinearLayout) rooView.findViewById(R.id.book_my_ride_applycoupon_layout);
        tv_carType = (TextView) rooView.findViewById(R.id.cartype_textview);
        tv_pickuptime = (TextView) rooView.findViewById(R.id.pickup_textview);
        tv_coupon_label = (TextView) rooView.findViewById(R.id.applycoupon_label);
        progressWheel = (ProgressBar) rooView.findViewById(R.id.book_my_ride_progress_wheel);
        progressWheel1 = (ProgressWheel) rooView.findViewById(R.id.book_my_ride_progress_wheel1);
        Tv_walletAmount = (TextView) rootview.findViewById(R.id.book_my_ride_wallet_amount_textView);
        Rl_Confirm_Back = (RelativeLayout) rootview.findViewById(R.id.book_my_ride_confirm_header_back_layout);

        Tv_marker_time = (TextView) rootview.findViewById(R.id.book_my_ride_confirm_header_car_time_textView);
        Tv_marker_min = (TextView) rootview.findViewById(R.id.book_my_ride_confirm_header_car_time_min_textView);
        Tv_no_cabs = (TextView) rootview.findViewById(R.id.book_my_ride_confirm_header_car_Pick_time);
        Ll_marker_time = (LinearLayout) rooView.findViewById(R.id.book_my_ride_marker_time_layout);
        book_cardview_destination_address_layout = (CardView) rooView.findViewById(R.id.book_cardview_destination_address_layout);

        Tv_surge = (TextView) rootview.findViewById(R.id.book_my_ride_surge_textView);
        tv_estimate_label = (TextView) rootview.findViewById(R.id.book_my_ride_enter_drop_label);

        Rl_CenterMarker = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_center_marker_RelativeLayout);


        tv_share = (TextView) rooView.findViewById(R.id.book_my_ride_share_textView);

        normal_ride = (LinearLayout) rooView.findViewById(R.id.book_my_ride_normal_ride_layout);
        share_ride = (LinearLayout) rooView.findViewById(R.id.book_my_ride_share_layout);
        share_count = (LinearLayout) rooView.findViewById(R.id.book_my_ride_share_count1);
        R_share = (RelativeLayout) rooView.findViewById(R.id.book_my_ride_share_main_layout);
        R_normal = (RelativeLayout) rooView.findViewById(R.id.rr);

        tv_normal_label = (TextView) rootview.findViewById(R.id.book_my_ride_normal_ride_text);
        tv_normal_value = (TextView) rootview.findViewById(R.id.book_my_ride_normal_ride_text_value);
        tv_share_label = (TextView) rootview.findViewById(R.id.book_my_ride_share_text);
        tv_share_value = (TextView) rootview.findViewById(R.id.book_my_ride_share_text_value);
        tv_share_spinner_count = (TextView) rooView.findViewById(R.id.book_my_ride_share_spinner_);

        normalrideactive_image = (ImageView) rooView.findViewById(R.id.normalride_active_imageview);
        sharerideactive_image = (ImageView) rooView.findViewById(R.id.shareride_active_imageview);


        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slideup);

        slideDownAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slidedown);

        fadeInAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_in);

        fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_out);

        rooView.findViewById(R.id.sos_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmergencyDialog emergencyDialog = new EmergencyDialog(getContext());
                emergencyDialog.show(getActivity().getFragmentManager(), "SOS");

            }
        });

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        share_pool_status = user.get(SessionManager.KEY_SHARE_POOL_STATUS);
        language_code = user.get(SessionManager.KEY_Language_code);

        if (language_code.equals("")) {
            language_code = "en";
        } else {

        }

        HashMap<String, String> wallet = session.getWalletAmount();
        String sWalletAmount = wallet.get(SessionManager.KEY_WALLET_AMOUNT);

        Tv_walletAmount.setText(sWalletAmount);

        HashMap<String, String> cat = session.getCategoryID();
        String sCategoryId = cat.get(SessionManager.KEY_CATEGORY_ID);

        if (sCategoryId.length() > 0) {
            CategoryID = cat.get(SessionManager.KEY_CATEGORY_ID);
        } else {
            CategoryID = user.get(SessionManager.KEY_CATEGORY);
        }


        rooView.setFocusableInTouchMode(true);
        // rooView.requestFocus();
        rooView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (backListner) {
                        backListner = false;
                        if (backStatus) {
                            backStatus = false;
                            if (!driver_status) {


                                Ll_marker_time.setVisibility(View.GONE);
                                Tv_marker_time.setVisibility(View.GONE);
                                Tv_marker_min.setVisibility(View.GONE);
                                Rl_CenterMarker.setVisibility(View.GONE);
                                center_marker.setImageResource(R.drawable.no_cars_available_new);
                                Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                                progressWheel.setVisibility(View.INVISIBLE);
                                rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                //             rideNow_layout.setClickable(false);
                            } else {

                                Ll_marker_time.setVisibility(View.VISIBLE);
                                Tv_marker_time.setVisibility(View.VISIBLE);
                                Tv_marker_min.setVisibility(View.VISIBLE);
                                Rl_CenterMarker.setVisibility(View.GONE);
                                center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                                Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                                rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                rideNow_layout.setClickable(true);

                                Tv_marker_time.setText(time);
                                Tv_marker_min.setText(unit);
                                /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                            }


                            sShare_changed = false;
                            sShare_ride = false;
                            rideType = "";
                            tv_share.setVisibility(View.GONE);

                            rideLater_layout.setVisibility(View.VISIBLE);
                            tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                            tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));

                          /*  Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                            ridenow_option_layout.startAnimation(animFadeOut);*/
                            ridenow_option_layout.setVisibility(View.GONE);
                            rideNowOptionLayoutCliclableMethod(false);

                            center_marker.setEnabled(true);

                            googleMap.getUiSettings().setAllGesturesEnabled(true);
                            googleMap.getUiSettings().setRotateGesturesEnabled(false);
                            // Enable / Disable zooming functionality
                            googleMap.getUiSettings().setZoomGesturesEnabled(true);
                            isPathShowing = false;
                            R_pickup.setVisibility(View.VISIBLE);
                            if (sSurgeContent.trim().length() > 0) {
                                Tv_surge.setVisibility(View.VISIBLE);
                                Tv_surge.setText(sSurgeContent);
                            } else {
                                Tv_surge.setVisibility(View.GONE);
                            }
                            googleMap.getUiSettings().setTiltGesturesEnabled(false);
                            googleMap.getUiSettings().setCompassEnabled(true);
                            listview.setVisibility(View.INVISIBLE);
                           /* if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                                listview.startAnimation(slideUpAnimation);
                                listview.setVisibility(View.INVISIBLE);
                            }*/
                            categoryListviewCliclableMethod(true);
                            center_icon.setVisibility(View.VISIBLE);
                            rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                            backStatus = false;
                            tv_share.setVisibility(View.GONE);
                            rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                            currentLocation_image.setClickable(true);
                            currentLocation_image.setVisibility(View.VISIBLE);
                            pickTime_layout.setEnabled(true);
                            drawer_layout.setEnabled(true);
                            address_layout.setEnabled(true);
                            //destination_address_layout.setVisibility(View.VISIBLE);
                            // destination_address_layout.setEnabled(true);
                            favorite_layout.setEnabled(true);
                            NavigationDrawer.enableSwipeDrawer();


                            if (gps.canGetLocation() && gps.isgpsenabled()) {

                                MyCurrent_lat = gps.getLatitude();
                                MyCurrent_long = gps.getLongitude();


                                if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                                } else {
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                }
                                if (mRequest != null) {
                                    mRequest.cancelRequest();
                                }

                                // Move the camera to last position with a zoom level

                            } else {
                                enableGpsService();
                                //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            showBackPressedDialog(true);
                        }
                    } else {
                        backListner = true;
                    }
                    System.out.println("back pressed");



                   /* getActivity().onBackPressed();
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/

                    return true;
                }
                return false;
            }
        });
    }

    //-------------------Show Coupon Code Method--------------------
    private void showCoupon() {
        coupon_dialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.coupon_code_dialog, null);

        tv_apply = (TextView) view.findViewById(R.id.couponcode_apply_textView);
        tv_cancel = (TextView) view.findViewById(R.id.couponcode_cancel_textView);
        final TextView tv_nointernet = (TextView) view.findViewById(R.id.couponcode_nointernet_textView);

        coupon_edittext = (EditText) view.findViewById(R.id.couponcode_editText);
        coupon_apply_layout = (RelativeLayout) view.findViewById(R.id.couponcode_apply_layout);
        coupon_loading_layout = (RelativeLayout) view.findViewById(R.id.couponcode_loading_layout);
        coupon_allowance_layout = (RelativeLayout) view.findViewById(R.id.couponcode_allowance_amount_layout);
        coupon_allowance = (TextView) view.findViewById(R.id.couponcode_allowance_textview);
        Iv_coupon_cancel_Icon = (ImageView) view.findViewById(R.id.couponcode_cancel_imageIcon);

        coupon_edittext.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        Iv_coupon_cancel_Icon.setVisibility(View.INVISIBLE);

        HashMap<String, String> code = session.getCouponCode();
        String coupon = code.get(SessionManager.KEY_COUPON_CODE);

        System.out.println("-----------coupon-------------" + coupon);

        if (!coupon.isEmpty()) {
            coupon_edittext.setText(coupon);
            tv_apply.setText(getResources().getString(R.string.couponcode_label_remove));
            coupon_allowance_layout.setVisibility(View.VISIBLE);
            coupon_allowance.setText(sCouponAllowanceText);
        }

        coupon_apply_layout.setVisibility(View.VISIBLE);
        coupon_loading_layout.setVisibility(View.GONE);
        coupon_edittext.addTextChangedListener(EditorWatcher);

        coupon_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(coupon_edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                coupon_dialog.dismiss();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        tv_apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (coupon_edittext.length() == 0) {
                    coupon_edittext.setHint(getResources().getString(R.string.couponcode_label_empty_code));
                    coupon_edittext.setHintTextColor(Color.RED);
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    coupon_edittext.startAnimation(shake);
                } else {
                    cd = new ConnectionDetector(getActivity());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        tv_nointernet.setVisibility(View.INVISIBLE);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        if (getResources().getString(R.string.couponcode_label_apply).equalsIgnoreCase(tv_apply.getText().toString())) {
                            CouponCodeRequest(Iconstant.couponCode_apply_url, coupon_edittext.getText().toString(), coupon_selectedDate);
                        } else {
                            Str_couponCode = "";
                            session.setCouponCode("");
                            coupon_edittext.setText("");
                            tv_apply.setText(getResources().getString(R.string.couponcode_label_apply));
                            coupon_allowance_layout.setVisibility(View.GONE);
                            tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                            tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));
                            tv_cancel.setText(getResources().getString(R.string.action_cancel_alert));
                        }
                    } else {
                        tv_nointernet.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        coupon_dialog.setView(view).show();
    }

    //-------------------Show RateCard Method--------------------
    private void showRateCard() {
        if (ratecard_status) {
            final MaterialDialog dialog = new MaterialDialog(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.rate_card_pop_up, null);
//            view.setAnimation(fadeInAnimation);
            TextView tv_cartype = (TextView) view.findViewById(R.id.ratecard_caretype_textview);
            TextView tv_firstprice = (TextView) view.findViewById(R.id.first_price_textView);
            TextView tv_firstKm = (TextView) view.findViewById(R.id.first_km_textView);
            TextView tv_afterprice = (TextView) view.findViewById(R.id.after_price_textView);
            TextView tv_afterKm = (TextView) view.findViewById(R.id.after_km_textView);
            TextView tv_otherprice = (TextView) view.findViewById(R.id.other_price_textView);
            TextView tv_otherKm = (TextView) view.findViewById(R.id.other_km_textView);
            TextView tv_note = (TextView) view.findViewById(R.id.ratecard_note_textview);
            //   TextView tv_ok = (TextView) view.findViewById(R.id.ratecard_ok_textview);
            TextView tv_emptynote = (TextView) view.findViewById(R.id.ratecard_emptylist_note_textview);
            RelativeLayout rl_emptylist = (RelativeLayout) view.findViewById(R.id.ratecard_display_empty_layout);
            RelativeLayout rl_list = (RelativeLayout) view.findViewById(R.id.ratecard_display_layout);

            if (ratecard_list.size() > 0) {
                rl_emptylist.setVisibility(View.GONE);
                tv_emptynote.setVisibility(View.GONE);
                rl_list.setVisibility(View.VISIBLE);
                tv_note.setVisibility(View.VISIBLE);

                //   Currency currencycode = Currency.getInstance(getLocale(ratecard_list.get(0).getCurrencyCode()));
                String ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ratecard_list.get(0).getCurrencyCode());
                tv_cartype.setText(ratecard_list.get(0).getRate_cartype());
                tv_firstprice.setText(ScurrencySymbol + ratecard_list.get(0).getMinfare_amt());
                tv_firstKm.setText(ratecard_list.get(0).getMinfare_km());
                tv_afterprice.setText(ScurrencySymbol + ratecard_list.get(0).getAfterfare_amt());
                tv_afterKm.setText(ratecard_list.get(0).getAfterfare_km());
                tv_otherprice.setText(ScurrencySymbol + ratecard_list.get(0).getOtherfare_amt());
                tv_otherKm.setText(ratecard_list.get(0).getOtherfare_km());
                tv_note.setText(ratecard_list.get(0).getRate_note());
            }

           /* tv_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    ratecard_clicked = true;
                }
            });*/
            dialog.setView(view).show();
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    //-------------------Show CarType Method--------------------
    private void select_carType_Dialog() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_cartype_dialog, null);

        ListView car_listview = (ListView) view.findViewById(R.id.car_type_dialog_listView);

        SelectCarTypeAdapter car_adapter = new SelectCarTypeAdapter(getActivity(), category_list);
        car_listview.setAdapter(car_adapter);
        car_adapter.notifyDataSetChanged();

        dialog.setTitle(getActivity().getResources().getString(R.string.car_type_select_dialog_label_carType));
        dialog.setPositiveButton(getActivity().getResources().getString(R.string.car_type_select_dialog_label_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                }
        );

        car_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
               /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                    bottom_layout.startAnimation(slideDownAnimation);
                }*/
                bottomLayoutCliclableMethod(false);
                CategoryID = category_list.get(position).getCat_id();
                SelectCar_Request(Iconstant.BookMyRide_url, Recent_lat, Recent_long);
                CarAvailable = category_list.get(position).getCat_time();
                //   String displaytime = CarAvailable + " " + getResources().getString(R.string.home_label_fromNow);
                String displaytime = CarAvailable;
                if (selectedType.equals("0")) {
                    tv_pickuptime.setText(displaytime);
                }
            }
        });
        dialog.setView(view).show();
    }

    //-------------------Show Share Seat Method--------------------
    private void select_Shareseat_Dialog() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.select_share_seat, null);

        ListView car_listview = (ListView) view.findViewById(R.id.seat_type_dialog_listView);
        RelativeLayout ok = (RelativeLayout) view.findViewById(R.id.select_seatype_single_Bottm_layout);


        final SelectSeatAdapter seat_adapter = new SelectSeatAdapter(getActivity(), poolRateCardList);
        car_listview.setAdapter(seat_adapter);
        seat_adapter.notifyDataSetChanged();

        dialog.setTitle(getActivity().getResources().getString(R.string.car_type_select_dialog_label_shareSeat));

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        car_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //         dialog.dismiss();
                System.out.println("poolRateCardList--------------------jai" + poolRateCardList.size());

                System.out.println("poolRateCardList--------------------jai" + poolRateCardList.size());


                for (int j = 0; j < poolRateCardList.size(); j++) {
                    System.out.println("poolRateCardList---------------j-----jai" + j);
                    System.out.println("poolRateCardList-------------pos-------jai" + position);

                    if (j == position) {
                        poolRateCardList.get(position).setSelect("yes");
                        tv_share_spinner_count.setText(poolRateCardList.get(position).getSeat());
//                        tv_share_value.setText(sCurrencySymbolseat+poolRateCardList.get(position).getCost());
                        tv_share_value.setText(poolRateCardList.get(position).getCost());
                    } else {
                        poolRateCardList.get(j).setSelect("no");
                    }
                }
                seat_adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        dialog.setView(view).show();
    }

    //-----------Check Google Play Service--------
    private boolean CheckPlayService() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, getActivity(), REQUEST_CODE_RECOVER_PLAY_SERVICES);
                if (dialog == null) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.home_page_toast_incompatible_version), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //-------------Method to get Complete Address------------
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        System.out.println("--------------lat and long" + LATITUDE + "dfdsf" + LONGITUDE);


        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                Log.e("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(getActivity());
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

    private void Alert_cal(String title, String alert) {

        final PkDialog mDialog = new PkDialog(getActivity());
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                HashMap<String, String> wallet = session.getWalletAmount();
                String sWalletAmount = wallet.get(SessionManager.KEY_WALLET_AMOUNT);
                Tv_walletAmount.setText(sWalletAmount);

                if (rideLater_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_ride_later))) {

                    if (map_address.getText().toString().length() > 0) {

                        session.setCouponCode("");
                        tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                        tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));

                        selectedType = "1";
                        Str_couponCode = "";

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_YEAR, 7);
                        Date seventhDay = calendar.getTime();

                        new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
//                                .setLanguage(language_code)
                                .setListener(listener)
                                .setInitialDate(new Date())
                                .setMinDate(new Date())
                                .setMaxDate(seventhDay)
                                //.setIs24HourTime(true)
                                .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                                .setIndicatorColor(Color.parseColor("#F83C6F"))
                                .build()

                                .show();
                    } else {
                        Alert(getActivity().getResources().getString(R.string.alert_label_title), getActivity().getResources().getString(R.string.home_label_invalid_pickUp));
                    }

                } else if (rideLater_textview.getText().toString().equalsIgnoreCase(getResources().getString(R.string.home_label_cancel))) {

                    //Enable and Disable RideNow Button
                    if (!driver_status) {

                        Ll_marker_time.setVisibility(View.GONE);
                        Tv_marker_time.setVisibility(View.GONE);
                        Tv_marker_min.setVisibility(View.GONE);
                        Rl_CenterMarker.setVisibility(View.GONE);
                        Rl_CenterMarker.setVisibility(View.GONE);
                        center_marker.setImageResource(R.drawable.no_cars_available_new);
                        Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                        progressWheel.setVisibility(View.INVISIBLE);
                        rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                        //              rideNow_layout.setClickable(false);
                    } else {

                        Ll_marker_time.setVisibility(View.VISIBLE);
                        Tv_marker_time.setVisibility(View.VISIBLE);
                        Tv_marker_min.setVisibility(View.VISIBLE);
                        Rl_CenterMarker.setVisibility(View.GONE);
                        center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                        Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                        rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                        rideNow_layout.setClickable(true);

                        Tv_marker_time.setText(time);
                        Tv_marker_min.setText(unit);
                        /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                    }


                    R_share.setVisibility(View.GONE);
                    tv_share.setVisibility(View.GONE);
                    tv_share.setVisibility(View.GONE);
                    R_normal.setVisibility(View.VISIBLE);
                    rideType = "";

                   /* Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                    ridenow_option_layout.startAnimation(animFadeOut);*/
                    ridenow_option_layout.setVisibility(View.GONE);
                    rideNowOptionLayoutCliclableMethod(false);
                    center_marker.setEnabled(true);

                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                    // Enable / Disable zooming functionality
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
                    isPathShowing = false;
                    R_pickup.setVisibility(View.VISIBLE);
                    if (sSurgeContent.trim().length() > 0) {
                        Tv_surge.setVisibility(View.VISIBLE);
                        Tv_surge.setText(sSurgeContent);
                    } else {
                        Tv_surge.setVisibility(View.GONE);
                    }
                    tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                    tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                    googleMap.getUiSettings().setTiltGesturesEnabled(false);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    listview.setVisibility(View.INVISIBLE);
                   /* if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                        listview.startAnimation(slideUpAnimation);
                        listview.setVisibility(View.INVISIBLE);
                    }*/
                    categoryListviewCliclableMethod(true);
                    center_icon.setVisibility(View.VISIBLE);
                    rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                    backStatus = false;
                    tv_share.setVisibility(View.GONE);
                    rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                    currentLocation_image.setClickable(true);
                    currentLocation_image.setVisibility(View.VISIBLE);
                    pickTime_layout.setEnabled(true);
                    drawer_layout.setEnabled(true);
                    address_layout.setEnabled(true);
                    //destination_address_layout.setVisibility(View.VISIBLE);
                    // destination_address_layout.setEnabled(true);
                    favorite_layout.setEnabled(true);
                    NavigationDrawer.enableSwipeDrawer();


                    if (gps.canGetLocation() && gps.isgpsenabled()) {

                        MyCurrent_lat = gps.getLatitude();
                        MyCurrent_long = gps.getLongitude();


                        if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                        } else {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        }
                        if (mRequest != null) {
                            mRequest.cancelRequest();
                        }

                        // Move the camera to last position with a zoom level

                    } else {
                        enableGpsService();
                        //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
        mDialog.show();

    }

    private void rideNowOptionLayoutCliclableMethod(boolean b) {
        ridenow_option_layout.setFocusable(b);
        ridenow_option_layout.setClickable(b);
        ridenow_option_layout.setEnabled(b);
    }

    private void categoryListviewCliclableMethod(boolean b) {
//        listview.setVisibility(View.INVISIBLE);
        listview.setFocusable(b);
        listview.setClickable(b);
        listview.setEnabled(b);
    }

    private void bottomLayoutCliclableMethod(boolean b) {
        bottom_layout.setVisibility(View.VISIBLE);
        bottom_layout.setFocusable(b);
        bottom_layout.setClickable(b);
        bottom_layout.setEnabled(b);
        if (!b) {
            rideNow_textview.setTextColor(Color.parseColor("#CDCDCD"));
            rideLater_textview.setTextColor(Color.parseColor("#CDCDCD"));
        } else {
            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
            rideLater_textview.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    //-------------------Coupon Code Post Request----------------

    //-------------------AsynTask To get the current Address----------------
    private void PostRequest(String Url, final double latitude, final double longitude) {
        loading_layout.setVisibility(View.VISIBLE);
        progressWheel1.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.INVISIBLE);
        //center_marker.setVisibility(View.GONE);
        rideNow_layout.setEnabled(true);
        Rl_CenterMarker.setEnabled(false);
        rideLater_layout.setEnabled(false);
        Rl_CenterMarker.setClickable(false);
        selected_category = false;
        isLoading = true;

        System.out.println("--------------Book My ride url-------------------" + Url);

        Sselected_latitude = String.valueOf(latitude);
        Sselected_longitude = String.valueOf(longitude);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", String.valueOf(latitude));
        jsonParams.put("lon", String.valueOf(longitude));
        jsonParams.put("category", CategoryID);
        System.out.println("--------------Book My ride jsonParams-------------------" + jsonParams);
        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                loading_layout.setVisibility(View.GONE);

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
                                            driver_status = true;
                                        } else {
                                            driver_list.clear();
                                            driver_status = false;
                                        }
                                    } else {
                                        driver_status = false;
                                    }


                                    Object check_ratecard_object = jobject.get("ratecard");
                                    if (check_ratecard_object instanceof JSONObject) {

                                        JSONObject ratecard_object = jobject.getJSONObject("ratecard");
                                        if (ratecard_object.length() > 0) {
                                            ratecard_list.clear();
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

                                            ratecard_list.add(pojo);
                                            ratecard_status = true;
                                        } else {
                                            ratecard_list.clear();
                                            ratecard_status = false;
                                        }
                                    } else {
                                        ratecard_status = false;
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

                                                if (share_pool_status.equals("1")) {


                                                    pojo.setPoolOption(cat_object.getString("has_pool_option"));
                                                    pojo.setPoolType(cat_object.getString("is_pool_type"));
                                                }

                                                selectedCar = jobject.getString("selected_category");
                                                if (cat_object.getString("id").equals(jobject.getString("selected_category"))) {
                                                    CarAvailable = cat_object.getString("eta");
                                                    ScarType = cat_object.getString("name");
                                                    time = cat_object.getString("eta_time");
                                                    unit = cat_object.getString("eta_unit");
                                                    if (share_pool_status.equals("1")) {
                                                        pool_option = cat_object.getString("has_pool_option");
                                                        pool_type = cat_object.getString("is_pool_type");
                                                    }
                                                }
                                                category_list.add(pojo);
                                            }
                                            category_status = true;
                                        } else {
                                            category_list.clear();
                                            category_status = false;
                                        }
                                    } else {
                                        category_status = false;
                                    }
                                }

                                sSurgeContent = jobject.getString("surge");

                                if (driver_status) {
                                    if (sSurgeContent.trim().length() > 0) {
                                        Tv_surge.setVisibility(View.VISIBLE);
                                        Tv_surge.setText(sSurgeContent);
                                    } else {
                                        Tv_surge.setVisibility(View.GONE);
                                    }
                                } else {
                                    sSurgeContent = "";
                                }
                            }
                            main_response_status = true;
                        } else {
                            fail_response = object.getString("response");
                            main_response_status = false;
                            if (sSurgeContent.trim().length() > 0) {
                                Tv_surge.setVisibility(View.VISIBLE);
                                Tv_surge.setText(sSurgeContent);
                            } else {
                                Tv_surge.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    btnClickFlag = false;
                    e.printStackTrace();
                }


                if (main_response_status) {
                    String image_url = "";
                    alert_layout.setVisibility(View.GONE);

                    String sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                    session.createWalletAmount(sCurrencySymbol + SwalletAmount);
                    NavigationDrawer.navigationNotifyChange();

                    if (driver_status) {
                        System.out.println("1");
                        googleMap.clear();
                        for (int i = 0; i < driver_list.size(); i++) {
                            System.out.println("2");
                            for (int j = 0; j < category_list.size(); j++) {
                                System.out.println("3");
                                if (selectedCar.equals(category_list.get(j).getCat_id())) {
                                    System.out.println("4" + image_url);
                                    image_url = category_list.get(j).getCar_icon();
                                    System.out.println("4" + image_url);
                                    selected_category = true;

                                    if (share_pool_status.equals("1")) {
                                        if (category_list.get(j).getPool_type().equals("1")) {
                                            rideLater_layout.setVisibility(View.GONE);
                                            rideNow_textview.setText(getResources().getString(R.string.action_enter_drop_location));
                                        } else {
                                            rideLater_layout.setVisibility(View.VISIBLE);
                                            rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                                        }
                                    }
                                }
                            }
                            if (!selected_category) {
                                /*if (bottom_layout.getVisibility() == View.VISIBLE) {
                                    bottom_layout.startAnimation(slideDownAnimation);
                                }*/
                                bottomLayoutCliclableMethod(false);

                                System.out.println("---------------jai-----------first category select---------");
                                CategoryID = category_list.get(0).getCat_id();
                                PostRequest(Iconstant.BookMyRide_url, Recent_lat, Recent_long);
                            }
                            System.out.println("5");
                            final int finalI = i;
                            System.out.println("-------image_url---------" + image_url);
                            Picasso.with(getActivity())
                                    .load(image_url)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            String s = BitMapToString(bitmap);
                                            System.out.println("session bitmap" + s);
                                            session.setVehicle_BitmapImage(s);
                                            bmp = bitmap;
                                            double Dlatitude = Double.parseDouble(driver_list.get(finalI).getDriver_lat());
                                            double Dlongitude = Double.parseDouble(driver_list.get(finalI).getDriver_long());

                                            // create marker double Dlatitude = gps.getLatitude();
                                            MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                            marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                            googleMap.addMarker(marker);

                                            // adding marker
                                            //            googleMap.addMarker(marker);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                            System.out.println("6");
                        }
                    } else {
                        googleMap.clear();
                    }
                   /* try {
                        int number = 5; // number of buttons
                        MarkerOptions[] marker = new MarkerOptions[number];
                        for (int i = 0; i < number; i++) {
                            marker[i] = new MarkerOptions().position(new LatLng(MyCurrent_lat, MyCurrent_long));
                            marker[i].icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker));
                            googleMap.addMarker(marker[i]);
                            MyCurrent_lat = MyCurrent_lat + .0001;
                            MyCurrent_long = MyCurrent_long + .0001;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }*/


                    if (category_status) {

                        //Enable and Disable RideNow Button
                        if (!driver_status) {

                            Ll_marker_time.setVisibility(View.GONE);
                            Tv_marker_time.setVisibility(View.GONE);
                            Tv_marker_min.setVisibility(View.GONE);
                            Rl_CenterMarker.setVisibility(View.GONE);
                            center_marker.setImageResource(R.drawable.no_cars_available_new);
                            progressWheel.setVisibility(View.INVISIBLE);
                            Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));

                            /*if (bottom_layout.getVisibility() == View.GONE || bottom_layout.getVisibility() == View.INVISIBLE) {
                                bottom_layout.startAnimation(slideUpAnimation);
                                bottom_layout.setVisibility(View.VISIBLE);
                            }*/
                            bottomLayoutCliclableMethod(true);
                            rideNow_layout.setEnabled(true);
                            rideLater_layout.setEnabled(true);
                            Rl_CenterMarker.setEnabled(false);
                            Rl_CenterMarker.setClickable(false);
//                            rideNow_layout.setClickable(false);
                        } else {

                            Ll_marker_time.setVisibility(View.VISIBLE);
                            Tv_marker_time.setVisibility(View.VISIBLE);
                            progressWheel.setVisibility(View.VISIBLE);

                            Tv_marker_min.setVisibility(View.VISIBLE);
                            Rl_CenterMarker.setVisibility(View.GONE);
                            center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                            Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                            rideNow_layout.setClickable(true);

                            Tv_marker_time.setText(time);
                            Tv_marker_min.setText(unit);

                            rideNow_layout.setEnabled(true);
                            rideLater_layout.setEnabled(true);
                            Rl_CenterMarker.setEnabled(true);
                            Rl_CenterMarker.setClickable(true);
                            /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                        }

                        for (int j = 0; j < category_list.size(); j++) {
                            if (selectedCar.equals(category_list.get(j).getCat_id())) {
                                if (share_pool_status.equals("1")) {
                                    if (category_list.get(j).getPool_type().equals("1")) {

                                        rideLater_layout.setVisibility(View.GONE);
                                        rideNow_textview.setText(getResources().getString(R.string.action_enter_drop_location));
                                    } else {
                                        rideLater_layout.setVisibility(View.VISIBLE);
                                        rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                                    }
                                }
                            }
                        }


                       /* if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                            listview.startAnimation(slideUpAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(true);
                        if (isDataLoaded) {
                            listview.setVisibility(View.INVISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            isDataLoaded = true;
                            listview.setVisibility(View.INVISIBLE);
                            adapter = new BookMyRide_Adapter(getActivity(), category_list);
                            listview.setAdapter(adapter);
                        }
                        System.out.println("-------------------addreess----------------3");

                    } else {
                       /* if (listview.getVisibility() == View.VISIBLE) {
                            listview.startAnimation(slideDownAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(false);
                        rideNow_layout.setEnabled(true);
                        rideLater_layout.setEnabled(false);
                        Rl_CenterMarker.setEnabled(false);
                        Rl_CenterMarker.setClickable(false);
                    }

                } else {

                    System.out.println("-------------------addreess----------------2");
                    /*if (listview.getVisibility() == View.VISIBLE) {
                        listview.startAnimation(slideDownAnimation);
                        listview.setVisibility(View.INVISIBLE);
                    }*/
                    listview.setVisibility(View.GONE);
                    categoryListviewCliclableMethod(false);
                    Rl_CenterMarker.setVisibility(View.GONE);

                    alert_layout.setVisibility(View.VISIBLE);
                  /*  if (bottom_layout.getVisibility() == View.VISIBLE) {
                        bottom_layout.startAnimation(slideDownAnimation);
                    }*/
                    bottomLayoutCliclableMethod(false);
                    alert_textview.setText(fail_response);
                }

                String address = new GeocoderHelper().fetchCityName(context, latitude, longitude, callBack);

                progressWheel1.setVisibility(View.GONE);
                //loading_layout.setVisibility(View.GONE);
                //center_marker.setVisibility(View.VISIBLE);
                isLoading = false;
                btnClickFlag = false;


                if (session.isAds()) {
                    HashMap<String, String> ads = session.getAds();
                    String title = ads.get(SessionManager.KEY_AD_TITLE);
                    String msg = ads.get(SessionManager.KEY_AD_MSG);
                    String banner = ads.get(SessionManager.KEY_AD_BANNER);
                    System.out.println("--------jai----ads-title-----------" + title);
                    System.out.println("--------jai----ads-msg-----------" + msg);
                    System.out.println("--------jai----ads-banner-----------" + banner);

                    Intent i1 = new Intent(context, AdsPage.class);
                    i1.putExtra("AdsTitle", title);
                    i1.putExtra("AdsMessage", msg);
                    i1.putExtra("AdsBanner", banner);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i1);
                }
            }

            @Override
            public void onErrorListener() {
                //   progressWheel.setVisibility(View.GONE);
                loading_layout.setVisibility(View.GONE);
                //center_marker.setVisibility(View.VISIBLE);
                rideNow_layout.setEnabled(true);
                rideLater_layout.setEnabled(true);
                Rl_CenterMarker.setEnabled(true);
                Rl_CenterMarker.setClickable(true);

                alert_layout.setVisibility(View.VISIBLE);
                /*if (bottom_layout.getVisibility() == View.VISIBLE) {
                    bottom_layout.startAnimation(slideDownAnimation);
                }*/
                bottomLayoutCliclableMethod(false);
                btnClickFlag = false;
                isLoading = false;
                isLocationType = false;
            }
        });
    }


    //-------------------Confirm Ride Post Request----------------

    private void CouponCodeRequest(String Url, final String code, final String pickpudate) {
        System.out.println("--------------coupon code url-------------------" + Url);

        coupon_apply_layout.setVisibility(View.GONE);
        coupon_loading_layout.setVisibility(View.VISIBLE);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("code", code);
        jsonParams.put("pickup_date", pickpudate);
        System.out.println("--------------coupon code jsonParams-------------------" + jsonParams);
        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------coupon code reponse-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {

                            JSONObject result_object = object.getJSONObject("response");

                            coupon_apply_layout.setVisibility(View.VISIBLE);
                            coupon_loading_layout.setVisibility(View.GONE);


                            String code = result_object.getString("code");
                            String type = result_object.getString("discount_type");
                            String discount = result_object.getString("discount_amount");
                            String currency_code = result_object.getString("currency_code");
                            String ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(currency_code);

                            Str_couponCode = code;
                            session.setCouponCode(code);
                            coupon_allowance_layout.setVisibility(View.VISIBLE);
                            if ("Percent".equalsIgnoreCase(type)) {
                                coupon_allowance.setText(getResources().getString(R.string.couponcode_label_allowance_text1) + " " + discount + " " + getResources().getString(R.string.couponcode_label_allowance_text2));

                                sCouponAllowanceText = getResources().getString(R.string.couponcode_label_allowance_text1) + " " + discount + " " + getResources().getString(R.string.couponcode_label_allowance_text2);

                            } else {
                                coupon_allowance.setText(getResources().getString(R.string.couponcode_label_allowance_text1) + " " + discount + " " + ScurrencySymbol + " " + getResources().getString(R.string.couponcode_label_allowance_text3));

                                sCouponAllowanceText = getResources().getString(R.string.couponcode_label_allowance_text1) + " " + discount + " " + ScurrencySymbol + " " + getResources().getString(R.string.couponcode_label_allowance_text3);
                            }
                            tv_apply.setText(getResources().getString(R.string.couponcode_label_remove));
                            tv_apply.setTextColor(getResources().getColor(R.color.app_color));
                            tv_cancel.setText(getResources().getString(R.string.action_ok));
                            tv_cancel.setTextColor(getResources().getColor(R.color.darkgreen_color));
                            tv_coupon_label.setText(Str_couponCode);//getResources().getString(R.string.couponcode_label_verifed)
                            tv_coupon_label.setTextColor(getResources().getColor(R.color.darkgreen_color));

                        } else {

                            Str_couponCode = "";
                            session.setCouponCode(Str_couponCode);
                            tv_coupon_label.setText(getResources().getString(R.string.ridenow_label_coupon));
                            tv_coupon_label.setTextColor(Color.parseColor("#4e4e4e"));

                            coupon_apply_layout.setVisibility(View.VISIBLE);
                            coupon_loading_layout.setVisibility(View.GONE);
                            coupon_allowance_layout.setVisibility(View.GONE);

                            coupon_edittext.setText("");
                            coupon_edittext.setHint(getResources().getString(R.string.couponcode_label_invalid_code));
                            coupon_edittext.setHintTextColor(Color.RED);
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                            coupon_edittext.startAnimation(shake);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorListener() {
                coupon_apply_layout.setVisibility(View.VISIBLE);
                coupon_loading_layout.setVisibility(View.GONE);
                coupon_dialog.dismiss();
            }
        });
    }

    private void ConfirmRideRequest(String Url, final String code, final String pickUpDate, final String pickup_time, final String type, final String category, final String pickup_location, final String pickup_lat, final String pickup_lon, final String try_value, final String destination_location, final String destination_lat, final String destination_lon) {

        dialog = new Dialog(getActivity());
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView loading = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        loading.setText(getResources().getString(R.string.action_pleasewait));

        System.out.println("--------------Confirm Ride url-------------------" + Url);


        System.out.println("--------------Confirm Ride ride Type-------------------" + rideType);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("code", code);
        jsonParams.put("pickup_date", pickUpDate);
        jsonParams.put("pickup_time", pickup_time.toUpperCase().trim().replace(".", "").replace(" ", "").replace("பிற்பகல்", "PM").replace("முற்பகல்", "AM"));
        jsonParams.put("type", type);
        jsonParams.put("category", category);
        jsonParams.put("pickup", pickup_location);
        jsonParams.put("pickup_lat", pickup_lat);
        jsonParams.put("pickup_lon", pickup_lon);
        jsonParams.put("ride_id", riderId);
        jsonParams.put("platform", "android");
        jsonParams.put("version", currentVersion);

        if (destination_address.getText().toString().equalsIgnoreCase(getResources().getString(R.string.action_enter_drop_location))) {
            jsonParams.put("drop_loc", "");
            jsonParams.put("drop_lat", "");
            jsonParams.put("drop_lon", "");

        } else {
            jsonParams.put("drop_loc", destination_location);
            jsonParams.put("drop_lat", destination_lat);
            jsonParams.put("drop_lon", destination_lon);

        }

        if (rideType.equals("share_change") || (rideType.equals("share"))) {
            jsonParams.put("share", "Yes");
            jsonParams.put("no_of_seat", tv_share_spinner_count.getText().toString());
        } else {
            jsonParams.put("share", "No");
        }

        System.out.println("--------------Confirm Ride jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("--------------Confirm Ride reponse-------------------" + response);

                String selected_type = "", Sacceptance = "";
                String Str_driver_id = "", Str_driver_name = "", Str_driver_email = "", Str_driver_image = "", Str_driver_review = "",
                        Str_driver_lat = "", Str_driver_lon = "", Str_min_pickup_duration = "", Str_ride_id = "", Str_phone_number = "",
                        Str_vehicle_number = "", Str_vehicle_model = "";
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");
                        if (status.equalsIgnoreCase("1")) {
                            JSONObject response_object = object.getJSONObject("response");

                            selected_type = response_object.getString("type");
                            Sacceptance = object.getString("acceptance");
                            if (Sacceptance.equalsIgnoreCase("No")) {
                                response_time = response_object.getString("response_time");
                                seconds = Integer.parseInt(response_time);
                                time_out = response_object.getString("retry_time");

                            }

                            riderId = response_object.getString("ride_id");


                            if (Sacceptance.equalsIgnoreCase("Yes")) {
                                JSONObject driverObject = response_object.getJSONObject("driver_profile");

                                Str_driver_id = driverObject.getString("driver_id");
                                Str_driver_name = driverObject.getString("driver_name");
                                Str_driver_email = driverObject.getString("driver_email");
                                Str_driver_image = driverObject.getString("driver_image");
                                Str_driver_review = driverObject.getString("driver_review");
                                Str_driver_lat = driverObject.getString("driver_lat");
                                Str_driver_lon = driverObject.getString("driver_lon");
                                Str_min_pickup_duration = driverObject.getString("min_pickup_duration");
                                Str_ride_id = driverObject.getString("ride_id");
                                Str_phone_number = driverObject.getString("phone_number");
                                Str_vehicle_number = driverObject.getString("vehicle_number");
                                Str_vehicle_model = driverObject.getString("vehicle_model");
                            }


                            if (selected_type.equalsIgnoreCase("1")) {

                                final PkDialog mDialog = new PkDialog(getActivity());
                                mDialog.setDialogTitle(getActivity().getResources().getString(R.string.action_success));
                                mDialog.setDialogMessage(getActivity().getResources().getString(R.string.ridenow_label_confirm_success));
                                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        //Enable and Disable RideNow Button
                                        if (!driver_status) {

                                            Ll_marker_time.setVisibility(View.GONE);
                                            Tv_marker_time.setVisibility(View.GONE);
                                            Tv_marker_min.setVisibility(View.GONE);
                                            Rl_CenterMarker.setVisibility(View.GONE);
                                            center_marker.setImageResource(R.drawable.no_cars_available_new);
                                            progressWheel.setVisibility(View.INVISIBLE);
                                            Tv_no_cabs.setText(getString(R.string.home_label__no_cabs));
                                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                            //            rideNow_layout.setClickable(false);
                                        } else {

                                            Ll_marker_time.setVisibility(View.VISIBLE);
                                            Tv_marker_time.setVisibility(View.VISIBLE);
                                            Tv_marker_min.setVisibility(View.VISIBLE);
                                            Rl_CenterMarker.setVisibility(View.GONE);
                                            center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                                            Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                                            rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                            rideNow_layout.setClickable(true);
                                            Tv_marker_time.setText(time);
                                            Tv_marker_min.setText(unit);
                                            /* Tv_marker_time.setText(CarAvailable.replace("min", "").replace("mins", "").replace(getString(R.string.home_label_min), ""));*/
                                        }

                                        //---------Hiding the bottom layout after success request--------
                                        googleMap.getUiSettings().setAllGesturesEnabled(true);

                                        googleMap.getUiSettings().setRotateGesturesEnabled(false);
                                        // Enable / Disable zooming functionality
                                        googleMap.getUiSettings().setZoomGesturesEnabled(true);

                                        sShare_changed = false;
                                        sShare_ride = false;

                                        googleMap.getUiSettings().setTiltGesturesEnabled(false);
                                        googleMap.getUiSettings().setCompassEnabled(true);
                                        isPathShowing = false;

                                        /*Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                                        ridenow_option_layout.startAnimation(animFadeOut);*/
                                        ridenow_option_layout.setVisibility(View.GONE);
                                        rideNowOptionLayoutCliclableMethod(false);

                                        center_marker.setEnabled(true);
                                        R_pickup.setVisibility(View.VISIBLE);
                                        if (sSurgeContent.trim().length() > 0) {
                                            Tv_surge.setVisibility(View.VISIBLE);
                                            Tv_surge.setText(sSurgeContent);
                                        } else {
                                            Tv_surge.setVisibility(View.GONE);
                                        }
                                       /* if (listview.getVisibility() == View.VISIBLE) {
                                            listview.startAnimation(slideDownAnimation);
                                            listview.setVisibility(View.INVISIBLE);
                                        }*/
                                        categoryListviewCliclableMethod(false);
                                        rideLater_layout.setVisibility(View.VISIBLE);
                                        tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                                        tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                                        rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                                        center_icon.setVisibility(View.VISIBLE);
                                        backStatus = false;
                                        tv_share.setVisibility(View.GONE);
                                        rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                                        currentLocation_image.setClickable(true);
                                        currentLocation_image.setVisibility(View.VISIBLE);
                                        pickTime_layout.setEnabled(true);
                                        drawer_layout.setEnabled(true);
                                        address_layout.setEnabled(true);
                                        //destination_address_layout.setVisibility(View.VISIBLE);
                                        //destination_address_layout.setEnabled(true);
                                        favorite_layout.setEnabled(true);
                                        NavigationDrawer.enableSwipeDrawer();

                                        if (gps.canGetLocation() && gps.isgpsenabled()) {

                                            MyCurrent_lat = gps.getLatitude();
                                            MyCurrent_long = gps.getLongitude();


                                            if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                                            } else {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }
                                            if (mRequest != null) {
                                                mRequest.cancelRequest();
                                            }
                                            // Move the camera to last position with a zoom level

                                        } else {
                                            enableGpsService();
                                            //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                mDialog.show();

                            } else if (selected_type.equalsIgnoreCase("0")) {
                                if (Sacceptance.equalsIgnoreCase("Yes")) {
                                    //Move to ride Detail page
                                    Intent i = new Intent(getActivity(), MyRideDetailTrackRide.class);
                                    i.putExtra("rideID", Str_ride_id);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                } else {
                                    count = 0;
                                    mapHandler.postDelayed(mapRunnable, Integer.parseInt(time_out) * 1000);
                                    mHandler.post(mRunnable1);
                                    Intent intent = new Intent(getActivity(), TimerPage.class);
                                    intent.putExtra("Time", response_time);
                                    intent.putExtra("retry_count", try_value);
                                    intent.putExtra("ride_ID", riderId);
                                    intent.putExtra("userLat", pickup_lat);
                                    intent.putExtra("userLong", pickup_lon);
                                    startActivityForResult(intent, timer_request_code);
                                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                                }
                            }
                        } else {
                            String Sresponse = object.getString("response");
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), Sresponse);
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






    //-----------------------Track Ride Post Request-----------------
    private void postRequest_TrackRide1(String Url, String SrideId_intent) {
        dialog = new Dialog(getActivity());
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

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Track Ride Response----------------" + response);
                String Sstatus = "";
                String driverID = "", driverName = "", driverImage = "", driverRating = "",
                        driverLat = "", driverLong = "", driverTime = "", rideID = "", driverMobile = "",
                        driverCar_no = "", driverCar_model = "", userLat = "", userLong = "", sRideStatus = "";

                String sPickUpLocation = "", sPickUpLatitude = "", sPickUpLongitude = "";
                String sDropLocation = "", sDropLatitude = "", sDropLongitude = "", cab_type = "";
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
                        Intent i = new Intent(getActivity(), MyRideDetailTrackRide.class);
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
                        i.putExtra("cab_type", cab_type);
                        i.putExtra("userLat", userLat);
                        i.putExtra("userLong", userLong);
                        i.putExtra("rideStatus", sRideStatus);

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
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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


    //-------------------Delete Ride Post Request----------------

    private void RetryRideRequest(String Url, final String ride_id) {


        try {
            System.out.println("--------------Retry Ride Request url-------------------" + Url);

            HashMap<String, String> user = session.getUserDetails();
            UserID = user.get(SessionManager.KEY_USERID);

            HashMap<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("user_id", UserID);
            jsonParams.put("ride_id", ride_id);

            System.out.println("--------------Retry Ride Request jsonParams-------------------" + jsonParams);

            mRequest = new ServiceRequest(getActivity());
            mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
                @Override
                public void onCompleteListener(String response) {
                    System.out.println("--------------Retry Ride Request reponse-------------------" + response);

                    String Sacceptance = "";
                    String Str_driver_id = "", Str_driver_name = "", Str_driver_email = "", Str_driver_image = "", Str_driver_review = "",
                            Str_driver_lat = "", Str_driver_lon = "", Str_min_pickup_duration = "", Str_ride_id = "", Str_phone_number = "",
                            Str_vehicle_number = "", Str_vehicle_model = "";
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.length() > 0) {
                            String status = object.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                //  JSONObject response_object = object.getJSONObject("response");


                                Sacceptance = object.getString("acceptance");
                                if (Sacceptance.equalsIgnoreCase("No")) {


                                }
                                if (Sacceptance.equalsIgnoreCase("Yes")) {
                                    JSONObject response_object = object.getJSONObject("response");

                                    JSONObject driverObject = response_object.getJSONObject("driver_profile");

                                    Str_driver_id = driverObject.getString("driver_id");
                                    Str_driver_name = driverObject.getString("driver_name");
                                    Str_driver_email = driverObject.getString("driver_email");
                                    Str_driver_image = driverObject.getString("driver_image");
                                    Str_driver_review = driverObject.getString("driver_review");
                                    Str_driver_lat = driverObject.getString("driver_lat");
                                    Str_driver_lon = driverObject.getString("driver_lon");
                                    Str_min_pickup_duration = driverObject.getString("min_pickup_duration");
                                    Str_ride_id = driverObject.getString("ride_id");
                                    Str_phone_number = driverObject.getString("phone_number");
                                    Str_vehicle_number = driverObject.getString("vehicle_number");
                                    Str_vehicle_model = driverObject.getString("vehicle_model");
                                }

                                System.out.println("----------jai-------pickup lat and long" + String.valueOf(Recent_lat) + "   " + String.valueOf(Recent_long));

                                if (Sacceptance.equalsIgnoreCase("Yes")) {

                                    mapHandler.removeCallbacks(mapRunnable);
                                    mHandler.removeCallbacks(mRunnable1);

                                    Intent local = new Intent();
                                    local.setAction("com.timerhandler.stop");
                                    getActivity().sendBroadcast(local);


                                    Intent i = new Intent(getActivity(), MyRideDetailTrackRide.class);
                                    i.putExtra("rideID", Str_ride_id);
                                    startActivity(i);
                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                                } else {
                                }

                            } else {
                                String Sresponse = object.getString("response");
                                Alert(getActivity().getResources().getString(R.string.alert_label_title), Sresponse);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //  dialog.dismiss();
                }

                @Override
                public void onErrorListener() {
                    // dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DeleteRideRequest(String Url) {

        dialog = new Dialog(getActivity());
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView loading = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        loading.setText(getResources().getString(R.string.action_pleasewait));

        System.out.println("--------------Delete Ride url-------------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("ride_id", riderId);
        System.out.println("--------------Delete Ride jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {


                String Sacceptance = "";
                String Str_driver_id = "", response_value = "", Str_driver_name = "", Str_driver_email = "", Str_driver_image = "", Str_driver_review = "",
                        Str_driver_lat = "", Str_driver_lon = "", Str_min_pickup_duration = "", Str_ride_id = "", Str_phone_number = "",
                        Str_vehicle_number = "", Str_vehicle_model = "";


                System.out.println("--------------Delete Ride reponse-------------------" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.length() > 0) {
                        String status = object.getString("status");


                        if (status.equalsIgnoreCase("1")) {

                            Sacceptance = object.getString("acceptance");
                            if (Sacceptance.equalsIgnoreCase("No")) {
                                response_value = object.getString("response");

                            }
                            if (Sacceptance.equalsIgnoreCase("Yes")) {
                                JSONObject response_object = object.getJSONObject("response");

                                JSONObject driverObject = response_object.getJSONObject("driver_profile");

                                Str_driver_id = driverObject.getString("driver_id");
                                Str_driver_name = driverObject.getString("driver_name");
                                Str_driver_email = driverObject.getString("driver_email");
                                Str_driver_image = driverObject.getString("driver_image");
                                Str_driver_review = driverObject.getString("driver_review");
                                Str_driver_lat = driverObject.getString("driver_lat");
                                Str_driver_lon = driverObject.getString("driver_lon");
                                Str_min_pickup_duration = driverObject.getString("min_pickup_duration");
                                Str_ride_id = driverObject.getString("ride_id");
                                Str_phone_number = driverObject.getString("phone_number");
                                Str_vehicle_number = driverObject.getString("vehicle_number");
                                Str_vehicle_model = driverObject.getString("vehicle_model");
                            }

                            if (Sacceptance.equalsIgnoreCase("Yes")) {


                                Intent i = new Intent(getActivity(), MyRideDetailTrackRide.class);
                                i.putExtra("rideID", Str_ride_id);
                                startActivity(i);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            }
                            if (Sacceptance.equalsIgnoreCase("No")) {

                                final PkDialog mDialog = new PkDialog(getActivity());
                                mDialog.setDialogTitle(getResources().getString(R.string.timer_label_alert_sorry));
                                mDialog.setDialogMessage(getResources().getString(R.string.timer_label_alert_content));
                                mDialog.setPositiveButton(getResources().getString(R.string.estimate_detail_label_ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();

                                        googleMap.getUiSettings().setAllGesturesEnabled(true);

                                        googleMap.getUiSettings().setRotateGesturesEnabled(false);
                                        // Enable / Disable zooming functionality
                                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                        backStatus = false;
                                        tv_share.setVisibility(View.GONE);


                                        sShare_changed = false;
                                        sShare_ride = false;
                                        tv_share.setVisibility(View.GONE);
                                        googleMap.getUiSettings().setTiltGesturesEnabled(false);
                                        googleMap.getUiSettings().setCompassEnabled(true);
                                        isPathShowing = false;
                                        /*Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                                        ridenow_option_layout.startAnimation(animFadeOut);*/
                                        ridenow_option_layout.setVisibility(View.GONE);
                                        rideNowOptionLayoutCliclableMethod(false);

                                        Rl_CenterMarker.setVisibility(View.GONE);
                                        center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                                        Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                                        center_marker.setEnabled(true);
                                        if (sSurgeContent.trim().length() > 0) {
                                            Tv_surge.setVisibility(View.VISIBLE);
                                            Tv_surge.setText(sSurgeContent);
                                        } else {
                                            Tv_surge.setVisibility(View.GONE);
                                        }
                                        R_pickup.setVisibility(View.VISIBLE);
                                        listview.setVisibility(View.INVISIBLE);
                                      /*  if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                                            listview.startAnimation(slideUpAnimation);
                                            listview.setVisibility(View.INVISIBLE);
                                        }*/
                                        categoryListviewCliclableMethod(true);
                                        rideLater_layout.setVisibility(View.VISIBLE);
                                        tv_estimate.setText(getResources().getString(R.string.ridenow_label_estimate));
                                        tv_estimate_label.setText(getResources().getString(R.string.ridenow_label_enter_drop_loc));
                                        rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                                        center_icon.setVisibility(View.VISIBLE);
                                        rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                                        currentLocation_image.setClickable(true);
                                        currentLocation_image.setVisibility(View.VISIBLE);
                                        pickTime_layout.setEnabled(true);
                                        drawer_layout.setEnabled(true);
                                        address_layout.setEnabled(true);
                                        // destination_address_layout.setVisibility(View.VISIBLE);
                                        // destination_address_layout.setEnabled(true);
                                        favorite_layout.setEnabled(true);
                                        NavigationDrawer.enableSwipeDrawer();


                                        if (gps.canGetLocation() && gps.isgpsenabled()) {

                                            MyCurrent_lat = gps.getLatitude();
                                            MyCurrent_long = gps.getLongitude();


                                            if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                                            } else {
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                            }
                                            if (mRequest != null) {
                                                mRequest.cancelRequest();
                                            }

                                            // Move the camera to last position with a zoom level

                                        } else {
                                            enableGpsService();
                                            //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                });

                                mDialog.show();


                            }
                          /*  riderId = "";
                            Alert(getActivity().getResources().getString(R.string.action_success), response_value);*/


                        } else {
                            response_value = object.getString("response");
                            Alert(getActivity().getResources().getString(R.string.alert_label_title), response_value);
                        }


                        //---------Hiding the bottom layout after cancel request--------

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

    //-------------------Select Car Type Request---------------
    private void SelectCar_Request(String Url, final double latitude, final double longitude) {

        final Dialog mdialog = new Dialog(getActivity());
        mdialog.getWindow();
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setContentView(R.layout.custom_loading);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        TextView dialog_title = (TextView) mdialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_updating));

        System.out.println("--------------Select Car Type url-------------------" + Url);


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("lat", String.valueOf(latitude));
        jsonParams.put("lon", String.valueOf(longitude));
        jsonParams.put("category", CategoryID);
        System.out.println("--------------Select Car Type jsonParams-------------------" + jsonParams);

        mRequest = new ServiceRequest(getActivity());
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("--------------Select Car Type reponse-------------------" + response);
                String fail_response = "";

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.length() > 0) {
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject jobject = object.getJSONObject("response");
                            if (jobject.length() > 0) {
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
                                            driver_status = true;
                                        } else {
                                            driver_list.clear();
                                            driver_status = false;
                                        }
                                    } else {
                                        driver_status = false;
                                    }


                                    Object check_ratecard_object = jobject.get("ratecard");
                                    if (check_ratecard_object instanceof JSONObject) {

                                        JSONObject ratecard_object = jobject.getJSONObject("ratecard");
                                        if (ratecard_object.length() > 0) {
                                            ratecard_list.clear();
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

                                            ratecard_list.add(pojo);
                                            ratecard_status = true;
                                        } else {
                                            ratecard_list.clear();
                                            ratecard_status = false;
                                        }
                                    } else {
                                        ratecard_status = false;
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
                                                selectedCar = jobject.getString("selected_category");

                                                if (cat_object.getString("id").equals(jobject.getString("selected_category"))) {
                                                    CarAvailable = cat_object.getString("eta");
                                                    ScarType = cat_object.getString("name");
                                                }

                                                category_list.add(pojo);
                                            }

                                            category_status = true;
                                        } else {
                                            category_list.clear();
                                            category_status = false;
                                        }
                                    } else {
                                        category_status = false;
                                    }

                                }
                            }

                            main_response_status = true;
                        } else {
                            fail_response = object.getString("response");
                            main_response_status = false;
                        }

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if (main_response_status) {
                    String image_url = "";
                    tv_carType.setText(ScarType);
                    if (driver_status) {
                        System.out.println("1");
                        googleMap.clear();
                        for (int i = 0; i < driver_list.size(); i++) {
                            System.out.println("2");
                            for (int j = 0; j < category_list.size(); j++) {
                                System.out.println("3");
                                if (selectedCar.equals(category_list.get(j).getCat_id())) {
                                    image_url = category_list.get(j).getCar_icon();
                                }
                            }
                            final int finalI = i;
                            Picasso.with(getActivity())
                                    .load(image_url)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            String s = BitMapToString(bitmap);
                                            System.out.println("session bitmap" + s);
                                            session.setVehicle_BitmapImage(s);
                                            bmp = bitmap;
                                            double Dlatitude = Double.parseDouble(driver_list.get(finalI).getDriver_lat());
                                            double Dlongitude = Double.parseDouble(driver_list.get(finalI).getDriver_long());

                                            // create marker double Dlatitude = gps.getLatitude();
                                            MarkerOptions marker = new MarkerOptions().position(new LatLng(Dlatitude, Dlongitude));
                                            marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));

                                            // adding marker
                                            googleMap.addMarker(marker);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        }
                    } else {
                        googleMap.clear();
                    }


                    if (category_status) {

                        //Enable and Disable RideNow Button

                        if (selectedType.equalsIgnoreCase("0")) {
                            if (!driver_status) {
                                rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                //        rideNow_layout.setClickable(false);
                            } else {
                                rideNow_textview.setTextColor(Color.parseColor("#FFFFFF"));
                                rideNow_layout.setClickable(true);
                            }
                        }
                        listview.setVisibility(View.INVISIBLE);
                        /*if (listview.getVisibility() == View.VISIBLE) {
                            listview.startAnimation(slideDownAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(false);

                        if (isDataLoaded) {
                            adapter.notifyDataSetChanged();
                        } else {
                            isDataLoaded = true;
                            adapter = new BookMyRide_Adapter(getActivity(), category_list);
                            listview.setAdapter(adapter);
                        }
                    } else {
                        listview.setVisibility(View.INVISIBLE);
                      /*  if (listview.getVisibility() == View.VISIBLE) {
                            listview.startAnimation(slideDownAnimation);
                            listview.setVisibility(View.INVISIBLE);
                        }*/
                        categoryListviewCliclableMethod(false);
                    }

                    mdialog.dismiss();

                } else {
                    mdialog.dismiss();
                }

                if (map_address.getText().toString().trim().length() > 0) {
//                    bottom_layout.setVisibility(View.VISIBLE);
                } else {
                    /*if (bottom_layout.getVisibility() == View.VISIBLE) {
                        bottom_layout.startAnimation(slideDownAnimation);
                    }*/
                    bottomLayoutCliclableMethod(false);
                }
            }

            @Override
            public void onErrorListener() {
                mdialog.dismiss();
            }
        });

    }

    //------------------------------------------------------------------------------

    //-------------------Estimate Price Request----------------
    private void EstimatePriceRequest(String Url, final String type) {
        System.out.println("--------------Estimate url-------------------" + Url);

        final Dialog mdialog = new Dialog(getActivity());
        mdialog.getWindow();
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setContentView(R.layout.custom_loading);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();
        TextView dialog_title = (TextView) mdialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_pleasewait));


        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("pickup", map_address.getText().toString());
        jsonParams.put("drop", destination_address.getText().toString());
        jsonParams.put("pickup_lat", String.valueOf(Recent_lat));
        jsonParams.put("pickup_lon", String.valueOf(Recent_long));
        jsonParams.put("drop_lat", SdestinationLatitude);
        jsonParams.put("drop_lon", SdestinationLongitude);
        jsonParams.put("category", CategoryID);
        jsonParams.put("type", selectedType);
        jsonParams.put("pickup_date", coupon_selectedDate);
        jsonParams.put("pickup_time", coupon_selectedTime);
        System.out.println("--------------Estimate  jsonParams-------------------" + jsonParams);
        Log.e("EstimatePriceRequest: ", "EstimatePriceRequest: " + jsonParams.toString());
        estimate_mRequest = new ServiceRequest(getActivity());
        estimate_mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

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
                                        CarAvailable = eta_object.getString("nbdd");
                                        SapproxTime = eta_object.getString("att");
                                        SpeakTime = eta_object.getString("peak_time");
                                        SnightCharge = eta_object.getString("night_charge");
                                        Snote = eta_object.getString("note");
                                        normal_est_amount = eta_object.getString("est_amount");
                                        new SessionManager(getContext()).setEstimateAmount(normal_est_amount);
                                        eta_category_id = eta_object.getString("catrgory_id");
                                        catrgory_name = eta_object.getString("catrgory_name");

                                        if (CategoryID.equals(eta_category_id)) {
                                            ScarType = eta_object.getString("catrgory_name");
                                        }
                                    }
                                }

                                if (response_object.has("ratecard")) {


                                    JSONObject ratecard_object = response_object.getJSONObject("ratecard");
                                    if (ratecard_object.length() > 0) {
                                        ratecard_list1.clear();
                                        EstimateDetailPojo pojo = new EstimateDetailPojo();

                                        pojo.setRate_note(ratecard_object.getString("note"));
                                        pojo.setCurrencyCode(response_object.getString("currency"));
                                        pojo.setRate_cartype(catrgory_name);

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

                                        ratecard_list1.add(pojo);
                                    }
                                }


                                if (response_object.has("pool_eta")) {

                                    JSONObject eta_object = response_object.getJSONObject("pool_eta");
                                    if (eta_object.length() > 0) {
                                        sha_Spickup = eta_object.getString("pickup");
                                        sha_Sdrop = eta_object.getString("drop");
                                        sha_catrgory_id = eta_object.getString("catrgory_id");
                                        sha_Snote = eta_object.getString("note");
                                        CarAvailable = eta_object.getString("nbdd");
                                        sha_approxTime = eta_object.getString("att");
                                        eta_share_category_id = eta_object.getString("catrgory_id");
                                        sha_est_amount = eta_object.getString("est_amount");
                                        sha_catrgory_name = eta_object.getString("catrgory_name");
                                        if (CategoryID.equals(eta_share_category_id)) {
                                            ScarType = eta_object.getString("catrgory_name");
                                        }
                                    }
                                }
                                if (response_object.has("pool_ratecard")) {

                                    JSONArray pool_ratecard = response_object.getJSONArray("pool_ratecard");

                                    if (pool_ratecard.length() > 0) {
                                        poolRateCardList.clear();
                                        String sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);
                                        for (int k = 0; k < pool_ratecard.length(); k++) {

                                            JSONObject cat_object = pool_ratecard.getJSONObject(k);

                                            PoolRateCard pojo = new PoolRateCard();

                                            pojo.setCost(sCurrencySymbol + cat_object.getString("cost"));
                                            pojo.setSeat(cat_object.getString("seat"));
                                            pojo.setSelect("no");
                                            poolRateCardList.add(pojo);
                                        }


                                    }
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


                    if (status.equalsIgnoreCase("1")) {

                        System.out.println("------------------------pool_option------------has_pool_service----is_pool_service--sShare_changed---sShare_ride--" + pool_option + "has_pool_service" + has_pool_service + "is_pool_service" + is_pool_service + "sShare_changed" + sShare_changed + "sShare_ride" + sShare_ride);


                        String sCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(ScurrencyCode);

                        sCurrencySymbolseat = sCurrencySymbol;
                        //    tv_estimate.setText(sCurrencySymbol+Smin_amount+" "+"-"+" "+sCurrencySymbol+Smax_amount);
                        tv_estimate.setText(sCurrencySymbol + normal_est_amount);
                        tv_estimate_label.setText(getResources().getString(R.string.estimated_fare_label));


                        if (has_pool_service.equals("1")) {
                            tv_share_value.setText(sCurrencySymbol + sha_est_amount);
                            tv_normal_value.setText(sCurrencySymbol + normal_est_amount);
                            tv_normal_label.setText(catrgory_name);
                            tv_share_label.setText(sha_catrgory_name);
                            tv_share_spinner_count.setText(poolRateCardList.get(0).getSeat());
                            poolRateCardList.get(0).setSelect("yes");

                            if ("1".equalsIgnoreCase(selectedType)) {
                                tv_CategoryDetail.setText(ScarType + "," + displayTime);
                            } else {
                                tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                            }


                            if (sShare_changed) {

                                if (normalRideStatus) {
                                    normal_ride.setVisibility(View.VISIBLE);
                                } else {
                                    normal_ride.setVisibility(View.GONE);
                                }
                                R_normal.setVisibility(View.GONE);
                                R_share.setVisibility(View.VISIBLE);

                                tv_share.setVisibility(View.GONE);
//                                normal_ride.setBackgroundColor(getResources().getColor(R.color.white));
//                                share_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));
                                normalrideactive_image.setVisibility(View.GONE);
                                sharerideactive_image.setVisibility(View.VISIBLE);

                                share_count.setVisibility(View.VISIBLE);

                                rideType = "share_change";
                                CategoryID = sha_catrgory_id;

                                if ("1".equalsIgnoreCase(selectedType)) {
                                    tv_CategoryDetail.setText(ScarType + "," + displayTime);
                                } else {
                                    tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                                }

                                for (int i = 0; i < category_list.size(); i++) {
                                    if (category_list.get(i).getPool_type().equals("1")) {
                                        //CategoryID =category_list.get(i).getCat_id();
                                        System.out.println("-------------CategoryID---------------" + category_list.get(i).getCat_id());
                                    }
                                }


                                //  R_normal.setVisibility(View.VISIBLE);
                                //   EstimatePriceRequest(Iconstant.estimate_price_url, "0");
                            }


                        }
                        if (is_pool_service.equals("1")) {
                            tv_share_value.setText(sCurrencySymbol + sha_est_amount);
                            tv_share_label.setText(sha_catrgory_name);
                            poolRateCardList.get(0).setSelect("yes");
                            tv_share_spinner_count.setText(poolRateCardList.get(0).getSeat());

                            if (sShare_ride) {

                                if (pool_type.equals("1")) {
                                    coupon_selectedDate = coupon_mFormatter.format(new Date());
                                    coupon_selectedTime = coupon_time_mFormatter.format(new Date());


                                    currentLocation_image.setClickable(false);
                                    currentLocation_image.setVisibility(View.GONE);
                                    R_pickup.setVisibility(View.GONE);
                                    if (sSurgeContent.trim().length() > 0) {
                                        Tv_surge.setVisibility(View.VISIBLE);
                                        Tv_surge.setText(sSurgeContent);
                                    } else {
                                        Tv_surge.setVisibility(View.GONE);
                                    }

                                   /* Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                                    ridenow_option_layout.startAnimation(animFadeIn);*/
                                    ridenow_option_layout.setVisibility(View.VISIBLE);
                                    rideNowOptionLayoutCliclableMethod(true);


                                    source_address.setText(map_address.getText().toString());
                                    center_marker.setImageResource(R.drawable.pickup_map_pointer_pin);
                                    Rl_CenterMarker.setVisibility(View.INVISIBLE);
                                    center_marker.setEnabled(false);
                                    progressWheel.setVisibility(View.INVISIBLE);
                                    if ("1".equalsIgnoreCase(selectedType)) {
                                        tv_CategoryDetail.setText(ScarType + "," + displayTime);
                                    } else {
                                        tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                                    }
                                    listview.setVisibility(View.INVISIBLE);
                                  /*  if (listview.getVisibility() == View.VISIBLE) {
                                        listview.startAnimation(slideDownAnimation);
                                        listview.setVisibility(View.INVISIBLE);
                                    }*/
                                    categoryListviewCliclableMethod(false);
                                    center_icon.setVisibility(View.INVISIBLE);
                                    rideLater_textview.setText(getResources().getString(R.string.home_label_cancel));
                                    rideLater_layout.setVisibility(View.GONE);
                                    rideNow_textview.setText(getResources().getString(R.string.home_label_confirm));


                   /* tv_carType.setText(ScarType);
                    tv_pickuptime.setText(displaytime);*/

                                    //----Disabling onClick Listener-----confir
                                    pickTime_layout.setEnabled(false);
                                    drawer_layout.setEnabled(false);
                                    address_layout.setEnabled(false);
                                    //destination_address_layout.setVisibility(View.VISIBLE);
                                    // destination_address_layout.setEnabled(false);
                                    favorite_layout.setEnabled(false);
                                    NavigationDrawer.disableSwipeDrawer();
                                    backStatus = true;


                                    R_share.setVisibility(View.VISIBLE);
                                    normal_ride.setVisibility(View.GONE);
                                    tv_share.setVisibility(View.GONE);
                                    R_normal.setVisibility(View.GONE);
                                    share_count.setVisibility(View.VISIBLE);
                                    rideType = "share";
                                    //normal_ride.setBackgroundColor(getResources().getColor(R.color.white));
                                    //   share_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));
                                    //   share_count.setVisibility(View.VISIBLE);

                                    //       EstimatePriceRequest(Iconstant.estimate_price_url, "0");
                                }

                            }
                        } else {


                            System.out.println("------------------------pool_option------------has_pool_service-----------" + pool_option + "has_pool_service" + has_pool_service);
                            if (pool_option.equalsIgnoreCase("1") && has_pool_service.equals("1")) {
                                if (!sShare_changed) {
                                    if ("1".equalsIgnoreCase(selectedType)) {
                                        tv_CategoryDetail.setText(ScarType + "," + displayTime);
                                    } else {
                                        tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                                    }
                                    shareRideClick = false;
                                    normalRideStatus = true;
                                    rideType = "normal";
                                    R_normal.setVisibility(View.GONE);
                                    R_share.setVisibility(View.VISIBLE);
                                    normal_ride.setVisibility(View.VISIBLE);
                                    tv_share.setVisibility(View.GONE);
//                                    normal_ride.setBackgroundColor(getResources().getColor(R.color.darkgreen_color));
//                                    share_ride.setBackgroundColor(getResources().getColor(R.color.white));
                                    sharerideactive_image.setVisibility(View.GONE);
                                    normalrideactive_image.setVisibility(View.VISIBLE);

                                    share_count.setVisibility(View.GONE);
                                    CategoryID = eta_category_id;
                                }
                            } else {

                                if ("1".equalsIgnoreCase(selectedType)) {
                                    tv_CategoryDetail.setText(ScarType + "," + displayTime);
                                } else {
                                    tv_CategoryDetail.setText(ScarType + "," + CarAvailable + " " + getResources().getString(R.string.away_label));
                                }
                                shareRideClick = false;
                                normalRideStatus = true;
                                rideType = "normal";
                                R_normal.setVisibility(View.VISIBLE);
                                R_share.setVisibility(View.GONE);
                                tv_share.setVisibility(View.GONE);
                                //        EstimatePriceRequest(Iconstant.estimate_price_url, "0");

                            }
                        }


                        if (type.equalsIgnoreCase("1")) {
                            if (isEstimateAvailable) {
                                Intent intent = new Intent(getActivity(), EstimateDetailPage.class);
                                intent.putExtra("CurrencyCode", ScurrencyCode);

                                if ("c-pool".equalsIgnoreCase(CategoryID)) {
                                    intent.putExtra("PickUp", sha_Spickup);
                                    intent.putExtra("Drop", sha_Sdrop);
                                    intent.putExtra("ApproxPrice", sha_est_amount);
                                    intent.putExtra("ApproxTime", sha_approxTime);
                                    intent.putExtra("PeakTime", SpeakTime);
                                    intent.putExtra("NightCharge", SnightCharge);
                                    intent.putExtra("Note", sha_Snote);
                                    intent.putExtra("catrgory_name", sha_catrgory_name);
                                } else {
                                    intent.putExtra("PickUp", Spickup);
                                    intent.putExtra("Drop", Sdrop);
                                    intent.putExtra("MinPrice", Smin_amount);
                                    intent.putExtra("MaxPrice", Smax_amount);
                                    intent.putExtra("ApproxPrice", normal_est_amount);
                                    intent.putExtra("ApproxTime", SapproxTime);
                                    intent.putExtra("PeakTime", SpeakTime);
                                    intent.putExtra("NightCharge", SnightCharge);
                                    intent.putExtra("Note", Snote);
                                    intent.putExtra("RateCard", new EstimateDetailPojo("", ratecard_list1));
                                }
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            } else {
                                Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.estimate_price_label_not_found));
                            }
                        }
                    }

                    if (status.equalsIgnoreCase("2")) {
                        isPathShowing = false;
                        backStatus = false;
                        String msg = object.getString("response");
                        if (googleMap != null) {
                            googleMap.clear();
                        }
                        Alert(getResources().getString(R.string.alert_label_title), msg);

                    } else if (status.equalsIgnoreCase("0")) {
                        if (googleMap != null) {
                            googleMap.clear();
                        }
                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.estimate_price_label_not_found));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        System.out.println("--------------onActivityResult requestCode----------------" + requestCode);

        // code to get country name
        if (requestCode == timer_request_code && resultCode == RESULT_OK && data != null) {
            String ride_accepted = data.getStringExtra("Accepted_or_Not");
            String retry_count = data.getStringExtra("Retry_Count");

            mapHandler.removeCallbacks(mapRunnable);
            mHandler.removeCallbacks(mRunnable1);

            if (retry_count.equalsIgnoreCase("1") && ride_accepted.equalsIgnoreCase("not")) {


            } else if (retry_count.equalsIgnoreCase("2") && ride_accepted.equalsIgnoreCase("not")) {

                DeleteRideRequest(Iconstant.delete_ride_url);

            } else if ((retry_count.equalsIgnoreCase("1") || retry_count.equalsIgnoreCase("2")) && ride_accepted.equalsIgnoreCase("Cancelled")) {

                riderId = "";
                if (googleMap != null) {
                    googleMap.clear();
                }

                //---------Hiding the bottom layout after cancel request--------
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                // Enable / Disable zooming functionality
                googleMap.getUiSettings().setZoomGesturesEnabled(true);


                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(true);
                if (driver_status) {
                    progressWheel.setVisibility(View.VISIBLE);
                } else {
                    progressWheel.setVisibility(View.INVISIBLE);
                }
                isPathShowing = false;
                /*Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                ridenow_option_layout.startAnimation(animFadeOut);*/
                ridenow_option_layout.setVisibility(View.GONE);
                rideNowOptionLayoutCliclableMethod(false);
                Rl_CenterMarker.setVisibility(View.GONE);
                center_marker.setImageResource(R.drawable.marker_setpickup_location_new);
                Tv_no_cabs.setText(getString(R.string.home_label__pickUp));
                center_marker.setEnabled(true);
                R_pickup.setVisibility(View.VISIBLE);
                if (sSurgeContent.trim().length() > 0) {
                    Tv_surge.setVisibility(View.VISIBLE);
                    Tv_surge.setText(sSurgeContent);
                } else {
                    Tv_surge.setVisibility(View.GONE);
                }
                tv_share.setVisibility(View.GONE);
                sShare_changed = false;
                sShare_ride = false;
                listview.setVisibility(View.INVISIBLE);
               /* if (listview.getVisibility() == View.INVISIBLE || listview.getVisibility() == View.GONE) {
                    listview.startAnimation(slideUpAnimation);
                    listview.setVisibility(View.INVISIBLE);
                }*/
                categoryListviewCliclableMethod(true);
                rideLater_layout.setVisibility(View.VISIBLE);
                rideLater_textview.setText(getResources().getString(R.string.home_label_ride_later));
                center_icon.setVisibility(View.VISIBLE);
                backStatus = false;
                tv_share.setVisibility(View.GONE);
                rideNow_textview.setText(getResources().getString(R.string.home_label_ride_now));
                currentLocation_image.setClickable(true);
                currentLocation_image.setVisibility(View.VISIBLE);
                pickTime_layout.setEnabled(true);
                drawer_layout.setEnabled(true);
                address_layout.setEnabled(true);
                //destination_address_layout.setVisibility(View.VISIBLE);
                //destination_address_layout.setEnabled(true);
                favorite_layout.setEnabled(true);
                NavigationDrawer.enableSwipeDrawer();


                if (gps.canGetLocation() && gps.isgpsenabled()) {

                    MyCurrent_lat = gps.getLatitude();
                    MyCurrent_long = gps.getLongitude();


                    if ((MyCurrent_lat == 0.0) || (MyCurrent_long == 0.0)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                    } else {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(MyCurrent_lat, MyCurrent_long)).zoom(17).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    }
                    if (mRequest != null) {
                        mRequest.cancelRequest();
                    }

                    // Move the camera to last position with a zoom level

                } else {
                    enableGpsService();
                    //Toast.makeText(getActivity(), "GPS not Enabled !!!", Toast.LENGTH_LONG).show();
                }


            }

        } else if ((requestCode == placeSearch_request_code && resultCode == RESULT_OK && data != null)) {
            btnClickFlag = false;
            isClick = true;
            // Check if no view has focus:
            CloseKeyBoard();
            if (search_status == 0) {

                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                String SselectedLocation = "";
                if (String.valueOf(place.getAddress()).contains(String.valueOf(place.getName()))) {
                    SselectedLocation = place.getAddress() + "";
                } else {
                    SselectedLocation = place.getName() + " " + place.getAddress() + "";
                }

//                String SselectedLocation = place.getAddress() + "";
                String SselectedLatitude = place.getLatLng().latitude + "";
                String SselectedLongitude = place.getLatLng().longitude + "";

                if (!SselectedLatitude.equalsIgnoreCase("") && SselectedLatitude.length() > 0 && !SselectedLongitude.equalsIgnoreCase("") && SselectedLongitude.length() > 0) {
                    // Move the camera to last position with a zoom level
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(SselectedLatitude), Double.parseDouble(SselectedLongitude))).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if ((SselectedLocation != null) && (!"".equalsIgnoreCase(SselectedLocation.trim()))) {
                    map_address.setText(SselectedLocation);
                    SselectedAddress = SselectedLocation;
//                    bottom_layout.setVisibility(View.VISIBLE);
                } else {
                    map_address.setText("");
                    SselectedAddress = "";
                  /*  if (bottom_layout.getVisibility() == View.VISIBLE) {
                        bottom_layout.startAnimation(slideDownAnimation);
                    }*/
                    bottomLayoutCliclableMethod(false);
                }
            } else {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                if (String.valueOf(place.getAddress()).contains(String.valueOf(place.getName()))) {
                    SdestinationLocation = place.getAddress() + "";
                } else {
                    SdestinationLocation = place.getName() + " " + place.getAddress() + "";
                }

//                SdestinationLocation = place.getAddress() + "";
                SdestinationLatitude = place.getLatLng().latitude + "";
                SdestinationLongitude = place.getLatLng().longitude + "";

                if ((SdestinationLocation != null) && (!"".equalsIgnoreCase(SdestinationLocation.trim()))) {

                    destination_address.setText(SdestinationLocation);
                    map_address_dropoff.setText(SdestinationLocation);
                } else {
                    destination_address.setText("");
                }
            }
        } else if ((requestCode == placeSearch_dest_request_code && resultCode == RESULT_OK && data != null)) {

            if (search_status == 0) {
                String SselectedLatitude = data.getStringExtra("Selected_Latitude");
                String SselectedLongitude = data.getStringExtra("Selected_Longitude");
                String SselectedLocation = data.getStringExtra("Selected_Location");
                if (!SselectedLatitude.equalsIgnoreCase("") && SselectedLatitude.length() > 0 && !SselectedLongitude.equalsIgnoreCase("") && SselectedLongitude.length() > 0) {
                    // Move the camera to last position with a zoom level
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(SselectedLatitude), Double.parseDouble(SselectedLongitude))).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                map_address.setText(SselectedLocation);
            } else {
                SdestinationLatitude = data.getStringExtra("Selected_Latitude");
                SdestinationLongitude = data.getStringExtra("Selected_Longitude");
                SdestinationLocation = data.getStringExtra("Selected_Location");

                if ((SdestinationLocation != null) && (!"".equalsIgnoreCase(SdestinationLocation.trim()))) {

                    destination_address.setText(SdestinationLocation);
                    map_address_dropoff.setText(SdestinationLocation);
                    try {
                        double latitude = Double.parseDouble(SdestinationLatitude);
                        double longitude = Double.parseDouble(SdestinationLongitude);
                    /*double startlatitude = Double.parseDouble(Recent_lat);
                    double startlongitude = Double.parseDouble(Recent_long);*/
                        destlatlng = new LatLng(latitude, longitude);
                        startlatlng = new LatLng(Recent_lat, Recent_long);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (googleMap != null) {
                        if (startlatlng != null && destlatlng != null) {
                            googleMap.clear();

                            if ("share".equals(rideType)) {
                                drawpolyline(startlatlng, destlatlng);
                            } else {
//                                GetRouteTask getRoute = new GetRouteTask();
//                                getRoute.execute();
                                GetRouteTask1 getRoute = new GetRouteTask1(startlatlng, destlatlng);
                                getRoute.execute();

                            }
                        }
                    }
                } else {
                    destination_address.setText("");
                }
                EstimatePriceRequest(Iconstant.estimate_price_url, "0");
            }

        } else if ((requestCode == eta_placeSearch_request_code && resultCode == RESULT_OK && data != null)) {

            CloseKeyBoard();

            isClick = true;

            Place place = PlaceAutocomplete.getPlace(getActivity(), data);

            if (String.valueOf(place.getAddress()).contains(String.valueOf(place.getName()))) {
                SdestinationLocation = place.getAddress() + "";
            } else {
                SdestinationLocation = place.getName() + " " + place.getAddress() + "";
            }
//            SdestinationLocation = place.getAddress() + "";
            SdestinationLatitude = place.getLatLng().latitude + "";
            SdestinationLongitude = place.getLatLng().longitude + "";

            if ((SdestinationLocation != null) && (!"".equalsIgnoreCase(SdestinationLocation.trim()))) {

                destination_address.setText(SdestinationLocation);
                map_address_dropoff.setText(SdestinationLocation);
                try {
                    double latitude = Double.parseDouble(SdestinationLatitude);
                    double longitude = Double.parseDouble(SdestinationLongitude);
                    /*double startlatitude = Double.parseDouble(Recent_lat);
                    double startlongitude = Double.parseDouble(Recent_long);*/
                    destlatlng = new LatLng(latitude, longitude);
                    startlatlng = new LatLng(Recent_lat, Recent_long);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (googleMap != null) {
                    if (startlatlng != null && destlatlng != null) {
                        googleMap.clear();

                        if ("share".equals(rideType)) {
                            drawpolyline(startlatlng, destlatlng);
                        } else {
//                            GetRouteTask getRoute = new GetRouteTask();
//                            getRoute.execute();
                            GetRouteTask1 getRoute = new GetRouteTask1(startlatlng, destlatlng);
                            getRoute.execute();
                        }
                    }
                }
            } else {
                destination_address.setText("");
            }

            EstimatePriceRequest(Iconstant.estimate_price_url, "1");


        } else if (requestCode == favoriteList_request_code && resultCode == RESULT_OK && data != null) {
            btnClickFlag = false;
            String SselectedLocation = data.getStringExtra("Selected_Location");
            String SselectedLatitude = data.getStringExtra("Selected_Latitude");
            String SselectedLongitude = data.getStringExtra("Selected_Longitude");
            if (!SselectedLatitude.equalsIgnoreCase("") && SselectedLatitude.length() > 0 && !SselectedLongitude.equalsIgnoreCase("") && SselectedLongitude.length() > 0) {
                // Move the camera to last position with a zoom level
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(SselectedLatitude), Double.parseDouble(SselectedLongitude))).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if ((SselectedLocation != null) && (!"".equalsIgnoreCase(SselectedLocation.trim()))) {
                map_address.setText(SselectedLocation);
                SselectedAddress = SselectedLocation;
//                bottom_layout.setVisibility(View.VISIBLE);
            } else {
                map_address.setText("");
                SselectedAddress = "";
               /* if (bottom_layout.getVisibility() == View.VISIBLE) {
                    bottom_layout.startAnimation(slideDownAnimation);
                }*/
                bottomLayoutCliclableMethod(false);
            }
        } else if (requestCode == REQUEST_LOCATION) {
            System.out.println("----------inside request location------------------");

            switch (resultCode) {
                case RESULT_OK: {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.home_page_toast_loaction_enable), Toast.LENGTH_LONG).show();
                    break;
                }
                case RESULT_CANCELED: {
                    enableGpsService();
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(getActivity(), data);
//            Log.e(TAG, "Error: Status = " + status.toString());
            // Check if no view has focus:
            CloseKeyBoard();
            btnClickFlag = false;
            isLocationType = false;
            isClick = true;
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            CloseKeyBoard();
            btnClickFlag = false;
            isLocationType = false;
            isClick = true;
        }else if (requestCode== destination_address_request_code){

            try {
                SdestinationLatitude = data.getStringExtra("Selected_Latitude");
                SdestinationLongitude = data.getStringExtra("Selected_Longitude");
                SdestinationLocation = data.getStringExtra("Selected_Location");
                map_address_dropoff.setText(SdestinationLocation);
            } catch (Exception e) {
                e.printStackTrace();
                map_address_dropoff.setText("No Address Selected");
            }
        }else if (requestCode==waiting_request_booking_time){
            // TODO: 8/6/2020  set waiting timer


            try {

                response_time = data.getStringExtra("response_time");
                riderId = data.getStringExtra("ride_id");
                seconds = Integer.parseInt(response_time);
                time_out = data.getStringExtra("retry_time");


                count = 0;
                mapHandler.postDelayed(mapRunnable, Integer.parseInt(time_out) * 1000);
                mHandler.post(mRunnable1);
                Intent intent = new Intent(getActivity(), TimerPage.class);
                intent.putExtra("Time", data.getStringExtra("response_time"));
                intent.putExtra("retry_count", data.getStringExtra("retry_time"));
                intent.putExtra("ride_ID", data.getStringExtra("ride_id"));
                intent.putExtra("userLat", new SessionManager(getActivity()).getStringValue(SessionManager.KEY_PICKUP_LOCATION_LAT));
                intent.putExtra("userLong", new SessionManager(getActivity()).getStringValue(SessionManager.KEY_PICKUP_LOCATION_LON));
                startActivityForResult(intent, timer_request_code);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void CloseKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(logoutReciver);
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    //Enabling Gps Service
    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void showBackPressedDialog(final boolean isLogout) {
        System.gc();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_app_exiting)
                .setPositiveButton(getResources().getString(R.string.navigation_drawer_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getActivity().finishAffinity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //  getActivity().NavigationDrawer.this.finish();
                        System.exit(0);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                })
                .setNegativeButton(getResources().getString(R.string.navigation_drawer_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dialog.dismiss();

                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList<LatLng> addWayPointPoint(LatLng start, LatLng end) {

        try {
            if (googleMap != null) {
                googleMap.clear();
                wayPointList.clear();

                wayPointBuilder = new LatLngBounds.Builder();

                wayPointList.add(start);
                wayPointBuilder.include(start);
                wayPointList.add(end);
                wayPointBuilder.include(end);

                googleMap.addMarker(new MarkerOptions().position(startlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                googleMap.addMarker(new MarkerOptions().position(destlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wayPointList;
    }

    public void animateRoute(GoogleMap googleMap, List<LatLng> bangaloreRoute) {
        if (firstRunAnimSet == null) {
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }
        if (secondLoopRunAnimSet == null) {
            secondLoopRunAnimSet = new AnimatorSet();
        } else {
            secondLoopRunAnimSet.removeAllListeners();
            secondLoopRunAnimSet.end();
            secondLoopRunAnimSet.cancel();

            secondLoopRunAnimSet = new AnimatorSet();
        }
        //Reset the polylines


        System.out.println("----jai-----1------" + bangaloreRoute.toString());
        System.out.println("----jai-----2------" + bangaloreRoute.get(0).toString());


        final ValueAnimator percentageCompletion = ValueAnimator.ofInt(0, 100);
        percentageCompletion.setDuration(2000);
        percentageCompletion.setInterpolator(new DecelerateInterpolator());
        percentageCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = backgroundPolyline.getPoints();

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                subListTobeRemoved.clear();

                foregroundPolyline.setPoints(foregroundPoints);
            }
        });
        percentageCompletion.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                foregroundPolyline.setColor(Color.parseColor("#FFA7A6A6"));
                foregroundPolyline.setPoints(backgroundPolyline.getPoints());
                System.out.println("-------jai------3--------" + backgroundPolyline.getPoints());

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#FFA7A6A6"), Color.BLACK);
        colorAnimation.setInterpolator(new AccelerateInterpolator());
        colorAnimation.setDuration(1200); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                foregroundPolyline.setColor((int) animator.getAnimatedValue());
            }

        });

        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(), bangaloreRoute.toArray());
        foregroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundPolyline.setPoints(foregroundPolyline.getPoints());
                //   System.out.println("-------jai------4--------"+backgroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        foregroundRouteAnimator.setDuration(1600);
//        foregroundRouteAnimator.setStartDelay(2000);
//        foregroundRouteAnimator.start();

        firstRunAnimSet.playSequentially(foregroundRouteAnimator,
                percentageCompletion);
        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        secondLoopRunAnimSet.playSequentially(colorAnimation,
                percentageCompletion);
        secondLoopRunAnimSet.setStartDelay(200);

        secondLoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        firstRunAnimSet.start();
    }

    private void openAutocompleteActivity(double recent_lat, double recent_long) {
        try {

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(toBounds(new LatLng(recent_lat, recent_long), 50000)).build(getActivity());
            startActivityForResult(intent, placeSearch_request_code);
        } catch (GooglePlayServicesRepairableException e) {

            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {

            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    private class GetRouteTask extends AsyncTask<String, Void, String> {

        String response = "";
        GMapV2GetRouteDirection v2GetRouteDirection = new GMapV2GetRouteDirection();
        Document document;


        @Override
        protected void onPreExecute() {
            System.out.println("------------------route map-------------------");
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(startlatlng, destlatlng, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (googleMap != null) {
                googleMap.clear();
                try {
                    if (result.equalsIgnoreCase("Success")) {
                        ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);


                        //     PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.app_color));
                        /*for (int i = 0; i < directionPoint.size(); i++) {
                            rectLine.add(directionPoint.get(i));
                        }*/

                        Marker m[] = new Marker[2];
                        if (googleMap != null) {
                            m[0] = googleMap.addMarker(new MarkerOptions().position(startlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_marker)));
                            m[1] = googleMap.addMarker(new MarkerOptions().position(destlatlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)));
                            isPathShowing = true;

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : m) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 100; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            googleMap.animateCamera(cu);
                            // googleMap.animateCamera(cu);


                            MapAnimator.getInstance().animateRoute(googleMap, directionPoint);

                            // Adding route on the map
                            //       googleMap.addPolyline(rectLine);


                       /* markerOptions.position(destlatlng);
                        markerOptions.position(startlatlng);
                        markerOptions.draggable(true);*/
                            //  isPathShowing=true;
                            //       mHandler.removeCallbacks(mHandlerTask);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class GetRouteTask1 extends AsyncTask<String, Void, String> {

        String response = "";

        private ArrayList<LatLng> wayLatLng;
        private String dLat, dLong;

        GetRouteTask1(LatLng start, LatLng end) {
            wayLatLng = addWayPointPoint(start, end);
            if (wayLatLng.size() < 2) {
                wayLatLng.clear();
                wayLatLng = addWayPointPoint(start, end);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {


            try {
                System.out.println("-------jai wayLatLng---------" + wayLatLng);
                if (wayLatLng.size() >= 2) {
                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(listner)
                            .alternativeRoutes(true)
                            .waypoints(wayLatLng)
                            .key(Utils.GOOGLE_KEY)
                            .build();
                    routing.execute();
                }

            } catch (ConcurrentModificationException e) {

            }catch (Exception e) {

            }
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("result", "onPostExecute: "+result );
            if (result.equalsIgnoreCase("Success")) {

            }
        }

    }

}
