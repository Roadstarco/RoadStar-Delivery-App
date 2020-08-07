package com.shrinkcom.app;


import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.BuildConfig;
import com.shrinkcom.passenger.R;
import com.countrycodepicker.CountryPicker;
import com.countrycodepicker.CountryPickerListener;
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.facebook.Util;
import com.mylibrary.volley.AppController;
import com.mylibrary.volley.ServiceRequest;
import com.mylibrary.volley.VolleyMultipartRequest;
import com.mylibrary.widgets.CircularImageView;
//import com.mylibrary.xmpp.XmppService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Prem Kumar on 10/7/2015.
 */

public class ProfilePage extends FragmentActivity {

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Context context;
    private SessionManager session;
    private RelativeLayout layout_changePassword, back,change_language;
    private Button logout;
    private TextView tv_email;
    private EditText Et_name;
    public static EditText Et_mobileno;
    private static RelativeLayout Rl_country_code;
    private static TextView Tv_countryCode,tv_pass_label,tv_pass_value;
    private String  UserName = "", UserMobileno = "", UserCountyCode = "", UserEmail = "";
    private ServiceRequest mRequest;
    Dialog dialog;
    CountryPicker picker;
    private Spinner language_change;
    ImageView img_pass,img_name,img_mob;

    ArrayList<String> languages_spn=new ArrayList<String>();


    private static CircularImageView Img_profile_pic;
    private static String profile_pic="";

    final int PERMISSION_REQUEST_CODE = 111;

    private Dialog photo_dialog;
    Bitmap bitMapThumbnail;
    private byte[] byteArray;
    BroadcastReceiver logoutReciver;
    private int REQUEST_TAKE_PHOTO = 1;
    private int galleryRequestCode = 2;
    private Uri camera_FileUri;
    private String UserID = "", gcmID = "",language_code="",Agent_Name="",user_image="";
    private static final String TAG = "";
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Cabily_image";
    private String mSelectedFilePath = "";
    private Bitmap selectedBitmap;

    private boolean isAppInfoAvailable = false;

    String Language_code="";

    private String appDirectoryName, name, imagePath;
    private File captured_image, imageRoot;
    private Uri mImageCaptureUri, outputUri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        
        context = getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initialize();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.app.logout");
        logoutReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.app.logout")) {
                    finish();
                }
            }
        };
        registerReceiver(logoutReciver, filter);
        //Start XMPP Chat Service
//        ChatService.startUserAction(ProfilePage.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(ProfilePage.this);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    final PkDialog mDialog = new PkDialog(ProfilePage.this);
                    mDialog.setDialogTitle(getResources().getString(R.string.profile_lable_logout_title));
                    mDialog.setDialogMessage(getResources().getString(R.string.profile_lable_logout_message));
                    mDialog.setPositiveButton(getResources().getString(R.string.profile_lable_logout_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            postRequest_Logout(Iconstant.logout_url);
                        }
                    });
                    mDialog.setNegativeButton(getResources().getString(R.string.profile_lable_logout_no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();

                } else {
                    Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                }
            }
        });

        tv_pass_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        tv_pass_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        img_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
       /* img_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        img_mob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });*/

       /* layout_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });*/

        Et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                Log.e(TAG, "onEditorAction: "+Et_name.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    cd = new ConnectionDetector(ProfilePage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    CloseKeyboard(Et_name);

                    if (Et_name.getText().toString().trim().length() == 0) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_lable_error_name));
                    } else {
                        if (isInternetPresent) {
                            postRequest_editUserName(Iconstant.profile_edit_userName_url);


                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });

//        Et_country_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_NEXT ) {
//                    Et_mobileno.requestFocus();
//                    handled = true;
//                }
//                return handled;
//            }
//        });


        Rl_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode) {
                picker.dismiss();
                Tv_countryCode.setText(dialCode.replace("+", ""));
                Et_mobileno.requestFocus();
            }
        });

        ((EditText) findViewById(R.id.myprofile_edit_phoneno_editText)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                Log.e(TAG, "onEditorAction: "+Et_mobileno.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    cd = new ConnectionDetector(ProfilePage.this);
                    isInternetPresent = cd.isConnectingToInternet();
                    CloseKeyboard(Et_name);

                    if (!isValidPhoneNumber(Et_mobileno.getText().toString())) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_lable_error_mobile));
                    } else if (Tv_countryCode.getText().toString().length() == 0) {
                        Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.profile_lable_error_mobilecode));
                    } else {
                        if (isInternetPresent) {
                            postRequest_editMobileNumber(Iconstant.profile_edit_mobileNo_url);

                        } else {
                            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                        }
                    }
                    handled = true;
                }
                return handled;
            }
        });




        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale locale = null;

                switch (language_change.getSelectedItemPosition()){

                    case 0:
                        locale = new Locale("en");
                        session.setlamguage("en","en");
                        System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                       changeLanguage_PostRequest(Iconstant.change_Language,"en");
                        break;
                    case 1:
                        locale = new Locale("es");
                        session.setlamguage("es","es");
                        System.out.println("========Tamil Language========"+language_change.getSelectedItem().toString()+"\t\tes");
                        changeLanguage_PostRequest(Iconstant.change_Language,"es");
                        break;
                    case 2:
                        locale = new Locale("ta");
                        session.setlamguage("ta","ta");
                        System.out.println("========Tamil Language========"+language_change.getSelectedItem().toString()+"\t\tta");
                        changeLanguage_PostRequest(Iconstant.change_Language,"ta");
                        break;
                    default:
                        locale = new Locale("en");
                        session.setlamguage("en","en");
                        System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                        changeLanguage_PostRequest(Iconstant.change_Language,"en");
                        break;
                }

                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

            }
        });

        Img_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkAccessFineLocationPermission() || !checkAccessCoarseLocationPermission() || !checkWriteExternalStoragePermission()) {
                        requestPermission();
                    } else {
                        chooseimage();
                    }
                } else {
                    chooseimage();
                }

            }
        });

    }
    private void changeLanguage_PostRequest(String Url, final String type1) {
        showDialog(getResources().getString(R.string.action_updating));

        System.out.println("-------------changeLAnguage--------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("id", UserID);
        jsonParams.put("lang_code",type1);
        jsonParams.put("user_type","user");

        System.out.println("--------------user_id-------------------" +UserID);
        System.out.println("--------------type-------------------" +type1);


        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {


            @Override
            public void onCompleteListener(String response) {

                Log.e("changelanguage", response);

                System.out.println("changelngresponse---------" + response);

                String Str_status = "", Str_response = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    Str_response = object.getString("response");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
                if (Str_status.equalsIgnoreCase("1")){
                    Alert_change(getResources().getString(R.string.action_success),Str_response);

                    Locale myLocale = new Locale(type1);
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);


                    //  Alert(getResources().getString(R.string.label_pushnotification_cashreceived),Str_response);
                }else{

                    Alert_sorry(getResources().getString(R.string.action_error),Str_response);


                }

              //  dialog.dismiss();
            }

            @Override
            public void onErrorListener() {

                dialog.dismiss();

            }

        });

    }
    private void initialize() {
        session = new SessionManager(ProfilePage.this);
        picker = CountryPicker.newInstance("Select Country");
        cd = new ConnectionDetector(ProfilePage.this);
        isInternetPresent = cd.isConnectingToInternet();

        tv_pass_label = (TextView) findViewById(R.id.myprofile_changepassword_textview);
        tv_pass_value = (TextView) findViewById(R.id.myprofile_changepassword_star_textview);
        img_pass= (ImageView) findViewById(R.id.arrow_changepasword);
        img_mob= (ImageView) findViewById(R.id.mydetailsimg);
        img_name= (ImageView) findViewById(R.id.myprofile_username_editimage);
        tv_email = (TextView) findViewById(R.id.myprofile_emailid_textview);
        Rl_country_code = (RelativeLayout) findViewById(R.id.myprofile_textView_country_code_layout);
        Tv_countryCode = (TextView) findViewById(R.id.myprofile_country_code_textview);
        back = (RelativeLayout) findViewById(R.id.myprofile_header_back_layout);
        Et_mobileno = (EditText) findViewById(R.id.myprofile_edit_phoneno_editText);
        Et_name = (EditText) findViewById(R.id.myprofile_username_editText);
        layout_changePassword = (RelativeLayout) findViewById(R.id.myprofile_changepassword_layout);
        logout = (Button) findViewById(R.id.myprofile_logout_button);
        language_change=(Spinner)findViewById(R.id.language_spinner);
        change_language=(RelativeLayout) findViewById(R.id.change_layout);
        Img_profile_pic = (CircularImageView)findViewById(R.id.profile_profileimg);
        language_change.getBackground().setColorFilter(Color.parseColor("#4e4e4e"), PorterDuff.Mode.SRC_ATOP);

        Et_mobileno.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_name.setFilters(new InputFilter[]{new EmojiExcludeFilter()});





        appDirectoryName = "cabily";
        imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
        if (!imageRoot.exists()) {
            imageRoot.mkdir();
        } else if (!imageRoot.isDirectory()) {
            imageRoot.delete();
            imageRoot.mkdir();
        }

        name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        captured_image = new File(imageRoot, name + ".jpg");




        // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            gcmID = user.get(SessionManager.KEY_GCM_ID);
            Agent_Name = user.get(SessionManager.KEY_ID_NAME);
            language_code = user.get(SessionManager.KEY_Language_code);
            user_image= user.get(SessionManager.KEY_USER_IMAGE);
            if(language_code.equals(""))
            {
                language_code="en";
            }

        UserID = user.get(SessionManager.KEY_USERID);
        UserName = user.get(SessionManager.KEY_USERNAME);
        UserMobileno = user.get(SessionManager.KEY_PHONENO);
        UserEmail = user.get(SessionManager.KEY_EMAIL);
        UserCountyCode = user.get(SessionManager.KEY_COUNTRYCODE);

        //Et_name.setImeActionLabel("Send", KeyEvent.);

        languages_spn.clear();

        languages_spn.add("English");
        languages_spn.add("Spanish");
        languages_spn.add("தமிழ்");


        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(ProfilePage.this, R.layout.spinner_text, languages_spn);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_change.setAdapter(dataAdapter);

        if(isInternetPresent){
            postRequest(Iconstant.profile_page_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }



    }


    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


    //-----------------------get user details-----------------
    private void postRequest(String Url) {
        showDialog(getResources().getString(R.string.action_loading));
        System.out.println("---------------get user details Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        System.out.println("---------------get user details Url-----------------" + Url);

        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------get user details Url---------------" + response);
                String Sstatus = "", Smessage = "",sName = "",sCountrycode = "",sPhonenumber = "",sEmail = "",sImage = "",sLang= "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                                sName = response_object.getString("name");
                                sCountrycode = response_object.getString("country_code");
                                sPhonenumber = response_object.getString("phone_number");
                                sEmail = response_object.getString("email");
                                sImage = response_object.getString("image");
                                sLang= response_object.getString("lang");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();

                if (Sstatus.equalsIgnoreCase("1")) {
                    UserName = sName;
                    UserMobileno = sPhonenumber;
                    UserEmail = sEmail;
                    UserCountyCode =sCountrycode;
                    language_code =sLang;
                    user_image= sImage;

                    tv_email.setText(UserEmail);
                    Et_name.setText(UserName);
                    Et_mobileno.setText(UserMobileno);
                    Tv_countryCode.setText(UserCountyCode.replace("+", ""));
                    Picasso.with(ProfilePage.this).load(user_image).placeholder(R.drawable.no_user_image).into(Img_profile_pic);

                    session=new SessionManager(ProfilePage.this);

                    HashMap<String, String> language = session.getLanaguage();

                    if(!language_code.equals("")) {

                        if(language_code.equals("en")){

                            language_change.setSelection(0);

                        }else if(language_code.equals("es")){
                            language_change.setSelection(1);
                        }
                        else{
                            language_change.setSelection(2);
                        }

                    }

                    Et_name.setSelection(Et_name.getText().length());
                    Et_mobileno.setSelection(Et_mobileno.getText().length());

                } else {
                    tv_email.setText(UserEmail);
                    Et_name.setText(UserName);
                    Et_mobileno.setText(UserMobileno);
                    Tv_countryCode.setText(UserCountyCode.replace("+", ""));
                    Picasso.with(ProfilePage.this).load(user_image).placeholder(R.drawable.no_user_image).into(Img_profile_pic);

                    session=new SessionManager(ProfilePage.this);

                    HashMap<String, String> language = session.getLanaguage();

                    if(!language.get(SessionManager.KEY_Language).equals("")) {


                        if(language.get(SessionManager.KEY_Language).equals("en")){

                            language_change.setSelection(0);

                        }else
                        if(language.get(SessionManager.KEY_Language).equals("es")){
                            language_change.setSelection(1);
                        }
                        else{

                            language_change.setSelection(2);
                        }

                    }
                    Et_name.setSelection(Et_name.getText().length());
                    Et_mobileno.setSelection(Et_mobileno.getText().length());
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(ProfilePage.this);
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
    private void Alert_sorry(String title, String message) {
        final PkDialog mDialog = new PkDialog(ProfilePage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                // finish();
            }
        });
        mDialog.show();
    }
    private void Alert_change(String title, String message) {
        final PkDialog mDialog = new PkDialog(ProfilePage.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.alert_label_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent in=new Intent(ProfilePage.this,UpdateUserLocation.class);
                finish();
                startActivity(in);
                // finish();
            }
        });
        mDialog.show();
        mDialog.setCancelOnTouchOutside(false);

    }
    // validating Phone Number
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target) || target.length() <= 5 || target.length() >= 16) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Show Dialog Method-----------
    private void showDialog(String data) {
        dialog = new Dialog(ProfilePage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(data);
    }

    //--------------Update Mobile Number From Profile OTP Page Method-----------
    public static void updateMobileDialog(String code, String phone) {
        Et_mobileno.setText(phone);
        Tv_countryCode.setText(code.replace("+", ""));
    }

    //-----------------------Edit UserName Request-----------------
    private void postRequest_editUserName(String Url) {
        showDialog(getResources().getString(R.string.action_updating));
        System.out.println("---------------Edit Username Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("user_name", Et_name.getText().toString());

        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------Edit Username Response-----------------" + response);
                String Sstatus = "", Smessage = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();

                if (Sstatus.equalsIgnoreCase("1")) {
                    session.setUserNameUpdate(Et_name.getText().toString());
                   NavigationDrawer.navigationNotifyChange();
                    Alert(getResources().getString(R.string.action_success), getResources().getString(R.string.profile_lable_username_success));
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    //-----------------------Edit MobileNumber Request-----------------
    private void postRequest_editMobileNumber(String Url) {
        showDialog(getResources().getString(R.string.action_updating));
        System.out.println("---------------Edit MobileNumber Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("country_code", "+" + Tv_countryCode.getText().toString());
        jsonParams.put("phone_number", Et_mobileno.getText().toString());
        jsonParams.put("otp", "");

        Log.e(TAG, "postRequest_editMobileNumber: " +jsonParams.toString()  );

        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("---------------Edit MobileNumber Response-----------------" + response);
                String Sstatus = "", Smessage = "", Sotp = "", Sotp_status = "", Scountry_code = "", Sphone_number = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("response");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        Sotp = object.getString("otp");
                        Sotp_status = object.getString("otp_status");
                        Scountry_code = object.getString("country_code");
                        Sphone_number = object.getString("phone_number");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
                if (Sstatus.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(ProfilePage.this, ProfileOtpPage.class);
                    intent.putExtra("Otp", Sotp);
                    intent.putExtra("Otp_Status", Sotp_status);
                    intent.putExtra("CountryCode", Scountry_code);
                    intent.putExtra("Phone", Sphone_number);
                    intent.putExtra("UserID", UserID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Alert(getResources().getString(R.string.action_error), Smessage);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }


    public void logoutFromFacebook() {
        Util.clearCookies(ProfilePage.this);
        // your sharedPrefrence
        SharedPreferences.Editor editor = context.getSharedPreferences("CASPreferences",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    //-----------------------Logout Request-----------------
    private void postRequest_Logout(String Url) {
        showDialog(getResources().getString(R.string.action_logging_out));
        System.out.println("---------------LogOut Url-----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("device", "ANDROID");

        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                Locale locale = null;
                System.out.println("---------------LogOut Response-----------------" + response);
                String Sstatus = "", Sresponse = "";
                try {

                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Sresponse = object.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
                if (Sstatus.equalsIgnoreCase("1")) {
                    logoutFromFacebook();
                    /*session.logoutUser();*/
                    postRequest_applaunch(Iconstant.app_info_url);
                } else {
                    Alert(getResources().getString(R.string.action_error), Sresponse);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

    private void postRequest_applaunch(String Url) {

        System.out.println("-------------Splash App Information Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_type", "user");
        jsonParams.put("id", "");
        mRequest = new ServiceRequest(ProfilePage.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------Splash App Information Response----------------" + response);

                String Str_status = "", sContact_mail = "", sCustomerServiceNumber = "", sSiteUrl = "", sXmppHostUrl = "", sHostName = "", sFacebookId = "", sGooglePlusId = "", sPhoneMasking = "";
                try {
                    JSONObject object = new JSONObject(response);
                    Str_status = object.getString("status");
                    if (Str_status.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            JSONObject info_object = response_object.getJSONObject("info");
                            if (info_object.length() > 0) {
                                sContact_mail = info_object.getString("site_contact_mail");
                                sCustomerServiceNumber = info_object.getString("customer_service_number");
                                sSiteUrl = info_object.getString("site_url");
                                sXmppHostUrl = info_object.getString("xmpp_host_url");
                                sHostName = info_object.getString("xmpp_host_name");
                              /*  sFacebookId = info_object.getString("facebook_app_id");
                                sGooglePlusId = info_object.getString("google_plus_app_id");
                                sPhoneMasking = info_object.getString("phone_masking_status");*/

                                /*server_mode = info_object.getString("server_mode");
                                site_mode = info_object.getString("site_mode");
                                site_string = info_object.getString("site_mode_string");
                                site_url = info_object.getString("site_url");*/
                                Language_code= info_object.getString("lang_code");
                                isAppInfoAvailable = true;
                            } else {
                                isAppInfoAvailable = false;
                            }

                           /* sPendingRideId= response_object.getString("pending_rideid");
                            sRatingStatus= response_object.getString("ride_status");*/

                        } else {
                            isAppInfoAvailable = false;
                        }
                    } else {
                        isAppInfoAvailable = false;
                    }
                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {

                            Locale locale = null;

                            switch (Language_code) {

                                case "en":
                                    locale = new Locale("en");
                                    session.setlamguage("en", "en");
                                    //  System.out.println("========English Language========"+language_change.getSelectedItem().toString()+"\t\ten");
                                    //  Intent in=new Intent(ProfilePage.this,NavigationDrawer.class);
                                    //   finish();
                                    //  startActivity(in);

//                        Intent bi = new Intent();
//                        bi.setAction("homepage");
//                        sendBroadcast(bi);
//                        finish();

                                    break;
                                case "es":
                                    locale = new Locale("es");
                                    session.setlamguage("es", "es");
                                    //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                    //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                    //     finish();
                                    //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                    break;
                                case "ta":
                                    locale = new Locale("ta");
                                    session.setlamguage("ta","ta");
                                    //      session.setlamguage("Ar",language_change.getSelectedItem().toString());
                                    //     System.out.println("========Arabic Language========"+language_change.getSelectedItem().toString()+"\t\tar");
                                    //     Intent i=new Intent(ProfilePage.this,NavigationDrawer.class);
                                    //     finish();
                                    //     startActivity(i);

//                        Intent bii = new Intent();
//                        bii.setAction("homepage");
//                        sendBroadcast(bii);
//                        finish();
                                    break;
                                default:
                                    locale = new Locale("en");
                                    session.setlamguage("en", "en");
                                    break;
                            }

                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
                        session.logoutUser();
//                        stopService(new Intent(ProfilePage.this, XmppService.class));

                        Intent local = new Intent();
                        local.setAction("com.app.logout");
                        ProfilePage.this.sendBroadcast(local);

                        onBackPressed();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                       /* if(site_mode.equalsIgnoreCase("dev"))
                        {
                            mInfoDialog = new PkDialogWithoutButton(Splash.this);
                            mInfoDialog.setDialogTitle("ALERT");
                            mInfoDialog.setDialogMessage(site_string);
                            mInfoDialog.show();
                        }
                        else
                        {
                            Intent intent = new Intent(Splash.this,HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                        if(server_mode.equalsIgnoreCase("0"))
                        {
                            Toast.makeText(context, site_url, Toast.LENGTH_SHORT).show();
                        }*/
                    } else {
                        Toast.makeText(ProfilePage.this, "BAD URL", Toast.LENGTH_SHORT).show();

                        /*mInfoDialog = new PkDialogWithoutButton(Splash.this);
                        mInfoDialog.setDialogTitle("");
                        mInfoDialog.setDialogMessage("");
                        mInfoDialog.show();*/
                    }
/*                    if (Str_status.equalsIgnoreCase("1") && isAppInfoAvailable) {

                        session = new SessionManager(Splash.this);
                        if (session.isLoggedIn()) {
                            Intent i = new Intent(Splash.this, DashBoardDriver.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Intent i = new Intent(Splash.this, HomePage.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    } else {

                        Alert(getResources().getString(R.string.alert_sorry_label_title), getResources().getString(R.string.fetchdatatoast));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onErrorListener() {

            }
        });
    }



    private void takePicture() {

       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            camera_FileUri = getOutputMediaFileUri(1);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_FileUri);
        } else {
            try {
                camera_FileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_FileUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }*/


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_FileUri = getOutputMediaFileUri(1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_FileUri);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = "";
        if (Build.VERSION.SDK_INT >= 24) {
            mCurrentPhotoPath = String.valueOf(FileProvider.getUriForFile(ProfilePage.this,
                    BuildConfig.APPLICATION_ID + ".provider", image));
        } else {
            mCurrentPhotoPath = String.valueOf(Uri.fromFile(image));
        }
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, galleryRequestCode);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));

        /*return FileProvider.getUriForFile(ProfilePage.this,
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));*/
    }


    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", camera_FileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        camera_FileUri = savedInstanceState.getParcelable("file_uri");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO ||  requestCode == UCrop.REQUEST_CROP) {
                try {
                    if (requestCode == REQUEST_TAKE_PHOTO) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        System.out.println("-----------camera_FileUri-----------"+camera_FileUri.getPath());
                        final Bitmap bitmap = BitmapFactory.decodeFile(camera_FileUri.getPath(), options);
                        Bitmap thumbnail = bitmap;
                        final String picturePath = camera_FileUri.getPath();
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        File curFile = new File(picturePath);
                        try {
                            ExifInterface exif = new ExifInterface(curFile.getPath());
                            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            int rotationInDegrees = exifToDegrees(rotation);

                            Matrix matrix = new Matrix();
                            if (rotation != 0f) {
                                matrix.preRotate(rotationInDegrees);
                            }
                        //    thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                        } catch (IOException ex) {
                            Log.e("TAG", "Failed to get Exif data", ex);
                        }
                        System.out.println("edit-----" + Iconstant.Edit_profile_image_url);

                        HashMap<String, String> language = session.getLanaguage();
                        String lang = language.get(SessionManager.KEY_Language);

                        Uri picUri = Uri.fromFile(curFile);
                        UCrop.Options Uoptions = new UCrop.Options();
                        Uoptions.setStatusBarColor(getResources().getColor(R.color.app_color));
                        Uoptions.setToolbarColor(getResources().getColor(R.color.app_color));
                        Uoptions.setActiveWidgetColor(ContextCompat.getColor(this, R.color.app_color));
                        Uoptions.setToolbarTitle(getResources().getString(R.string.app_name),lang);

                        UCrop.of(picUri, picUri)
                                .withAspectRatio(4, 4)
                                .withMaxResultSize(8000, 8000)
                                .start(ProfilePage.this);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == galleryRequestCode) {

                Uri selectedImage = data.getData();
                if (selectedImage.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    final String picturePath = c.getString(columnIndex);
                    c.close();
                    File curFile = new File(picturePath);

                    Picasso.with(ProfilePage.this).load(picturePath).resize(100, 100).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap thumbnail = bitmap;
                            mSelectedFilePath = picturePath;
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });


//                    performCrop(selectedImage);



                    HashMap<String, String> language = session.getLanaguage();
                    String lang = language.get(SessionManager.KEY_Language);

                    Uri picUri = Uri.fromFile(curFile);
                    UCrop.Options Uoptions = new UCrop.Options();
                    Uoptions.setStatusBarColor(getResources().getColor(R.color.app_color));
                    Uoptions.setToolbarColor(getResources().getColor(R.color.app_color));
                    Uoptions.setActiveWidgetColor(ContextCompat.getColor(this, R.color.app_color));
                    Uoptions.setToolbarTitle(getResources().getString(R.string.app_name),lang);

                    UCrop.of(picUri, picUri)
                            .withAspectRatio(4, 4)
                            .withMaxResultSize(8000, 8000)
                            .start(ProfilePage.this);

                } else {
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();

                    int columnIndex = c.getColumnIndex(filePath[0]);
                    final String picturePath = c.getString(columnIndex);
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    Bitmap thumbnail = bitmap; //getResizedBitmap(bitmap, 600);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    File curFile = new File(picturePath);

                    try {
                        ExifInterface exif = new ExifInterface(curFile.getPath());
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = exifToDegrees(rotation);

                        Matrix matrix = new Matrix();
                        if (rotation != 0f) {
                            matrix.preRotate(rotationInDegrees);
                        }
                        thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                    } catch (IOException ex) {
                        Log.e("TAG", "Failed to get Exif data", ex);
                    }
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    c.close();


                    Uri picUri = Uri.fromFile(curFile);


                    //---new-----------new image--DIR
                    if (!imageRoot.exists()) {
                        imageRoot.mkdir();
                    } else if (!imageRoot.isDirectory()) {
                        imageRoot.delete();
                        imageRoot.mkdir();
                    }

                    final File image = new File(imageRoot, System.currentTimeMillis() + ".jpg");
                    outputUri = Uri.fromFile(image);


                    HashMap<String, String> language = session.getLanaguage();
                    String lang = language.get(SessionManager.KEY_Language);

                    UCrop.Options Uoptions = new UCrop.Options();
                    Uoptions.setStatusBarColor(getResources().getColor(R.color.app_color));
                    Uoptions.setToolbarColor(getResources().getColor(R.color.app_color));
                    Uoptions.setActiveWidgetColor(ContextCompat.getColor(this, R.color.app_color));
                    Uoptions.setToolbarTitle(getResources().getString(R.string.app_name),lang);

                    //UCrop.of(picUri, picUri)
                    UCrop.of(selectedImage, outputUri)
                            .withAspectRatio(4, 4)
                            .withMaxResultSize(8000, 8000)
                            .start(ProfilePage.this);

                }
            }


            if ( requestCode == UCrop.REQUEST_CROP) {

                final Uri resultUri = UCrop.getOutput(data);
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                Img_profile_pic.setImageBitmap(selectedBitmap);
                Img_profile_pic.setImageURI(resultUri);
                UploadDriverImage(Iconstant.Edit_profile_image_url);

            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                System.out.println("========muruga cropError==========="+cropError);
            }


        }


    }


    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void UploadDriverImage(String url) {

        dialog = new Dialog(ProfilePage.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                System.out.println("------------- image response-----------------"+response.data);


                String resultResponse = new String(response.data);
                System.out.println("-------------  response-----------------"+resultResponse);
                String sStatus = "", sResponse = "",SUser_image="",Smsg="";
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    sStatus = jsonObject.getString("status");
                    if (sStatus.equalsIgnoreCase("1")) {
                //        JSONObject responseObject = jsonObject.getJSONObject("response");
                    //    SUser_image = jsonObject.getString("image");
                        Smsg = jsonObject.getString("image_url");

                 //       Img_profile_pic.setImageBitmap(bitMapThumbnail);
                        session.setuser_image(Smsg);
                        Picasso.with(ProfilePage.this).load(Smsg).placeholder(R.drawable.no_user_image).into(Img_profile_pic);

                        NavigationDrawer.navigationNotifyChange();
                        Locale locale = null;
                        switch (language_code){

                            case "en":
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                break;
                            case "es":
                                locale = new Locale("es");
                                session.setlamguage("es","es");
                                break;
                            case "ta":
                                locale = new Locale("ta");
                                session.setlamguage("ta","ta");
                                break;
                            default:
                                locale = new Locale("en");
                                session.setlamguage("en","en");
                                break;
                        }

                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());


                        Alert(getResources().getString(R.string.action_success),getResources().getString(R.string.edit_profile_success_label));

                    } else {
                        sResponse = jsonObject.getString("response");
                        Alert(getResources().getString(R.string.my_rides_rating_header_sorry_textview), sResponse);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    Toast.makeText(ProfilePage.this,"Something happened , try again",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                System.out.println("------------Authkey------cabily---------" + Agent_Name);
                System.out.println("------------userid----------cabily-----" + UserID);
                System.out.println("------------apptoken----------cabily-----" + gcmID);
                System.out.println("------------applanguage----------cabily-----" + language_code);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authkey", Agent_Name);
                headers.put("isapplication",Iconstant.cabily_IsApplication);
                headers.put("applanguage",language_code);
                headers.put("apptype", Iconstant.cabily_AppType);
                headers.put("userid",UserID);
                headers.put("apptoken",gcmID);
              /*  System.out.println("servicereques  apptype------------------"+Iconstant.cabily_AppType);
                System.out.println("servicereques apptoken------------------"+gcmID);
                System.out.println("servicereques userid------------------"+UserID);
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("User-agent",Iconstant.cabily_userAgent);
                headers.put("isapplication",Iconstant.cabily_IsApplication);
                headers.put("applanguage",Iconstant.cabily_AppLanguage);
                headers.put("apptype",Iconstant.cabily_AppType);
                headers.put("apptoken",gcmID);
                headers.put("userid",UserID);*/

                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",UserID);
                System.out.println("user_id---------------"+UserID);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("user_image",new DataPart("cabily_user.jpg", byteArray));

                System.out.println("user_image--------edit------"+byteArray);

                return params;
            }
        };

        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(retryPolicy);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        multipartRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(multipartRequest);

    }


    // --------------------Method for choose image to edit profileimage--------------------
    private void chooseimage() {
        photo_dialog = new Dialog(ProfilePage.this);
        photo_dialog.getWindow();
        photo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        photo_dialog.setContentView(R.layout.image_upload_dialog);
        photo_dialog.setCanceledOnTouchOutside(true);
        photo_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        photo_dialog.show();
        photo_dialog.getWindow().setGravity(Gravity.CENTER);

        RelativeLayout camera = (RelativeLayout) photo_dialog
                .findViewById(R.id.profilelayout_takephotofromcamera);
        RelativeLayout gallery = (RelativeLayout) photo_dialog
                .findViewById(R.id.profilelayout_takephotofromgallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                photo_dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                photo_dialog.dismiss();
            }
        });
    }



    private boolean checkAccessFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAccessCoarseLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseimage();
                } else {
                    finish();
                }
                break;
        }
    }
    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

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
        unregisterReceiver(logoutReciver);
    }
}

