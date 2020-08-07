package com.shrinkcom.app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.mylibrary.dialog.PkDialog;
import com.mylibrary.volley.AppController;
import com.mylibrary.volley.ServiceRequest;
import com.mylibrary.volley.VolleyMultipartRequest;
import com.mylibrary.widgets.CustomTextView;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.permissionhelper.PermissionHelper;
import com.shrinkcom.utils.ConnectionDetector;
import com.shrinkcom.utils.CurrencySymbolConverter;
import com.shrinkcom.utils.EmojiExcludeFilter;
import com.shrinkcom.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Prem Kumar and Anitha on 10/20/2015.
 */
public class CabilyMoney extends Activity {
    private static Context context;
    private static TextView Tv_cabilymoney_current_balnce;
    private static Button Bt_cabilymoney_minimum_amount;
    private static Button Bt_cabilymoney_maximum_amount;
    private static Button Bt_cabilymoney_between_amount;
    private static EditText Et_cabilymoney_enteramount;
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

            String strEnteredVal = Et_cabilymoney_enteramount.getText().toString();
            if (!strEnteredVal.equals("")) {
                if (Et_cabilymoney_enteramount.getText().toString().equals(Str_minimum_amt)) {
                    Bt_cabilymoney_minimum_amount.setBackgroundColor(0xFFe84c3d);
                    Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_minimum_amount.setTextColor(0xFFFFFFFF);
                    Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
                } else if (Et_cabilymoney_enteramount.getText().toString().equals(Str_midle_amt)) {
                    Bt_cabilymoney_between_amount.setBackgroundColor(0xFFe84c3d);
                    Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_between_amount.setTextColor(0xFFFFFFFF);
                    Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
                } else if (Et_cabilymoney_enteramount.getText().toString().equals(Str_maximum_amt)) {
                    Bt_cabilymoney_maximum_amount.setBackgroundColor(0xFFe84c3d);
                    Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_maximum_amount.setTextColor(0xFFFFFFFF);
                } else {
                    Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                    Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                    Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
                }
            }
        }
    };
    int PICK_IMAGE_CAMERA = 12;
    int PICK_IMAGE_GALLERY = 13;
    Dialog dialog;
    byte[] byteArray;
    private RelativeLayout back;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private SessionManager session;
    private String UserID = "";
    private Button Bt_add_cabilymoney;
    private RelativeLayout layout_current_transaction;
    private RadioGroup cabily_money_balance_select;
    private ServiceRequest mRequest;
    private boolean isRechargeAvailable = false;
    private String Sauto_charge_status = "";
    private String Str_currentbalance = "", Str_minimum_amt = "", Str_maximum_amt = "", Str_midle_amt = "", ScurrencySymbol = "";
    private String enteredValue = "";
    private RefreshReceiver refreshReceiver;
    private ImageView image_container_layout;
    private boolean isSelectImage = false;
    private CustomTextView receipt_history;

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

    public static void changeButton() {
        Et_cabilymoney_enteramount.setText("");
        Bt_cabilymoney_minimum_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
        Bt_cabilymoney_between_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
        Bt_cabilymoney_maximum_amount.setBackground(context.getResources().getDrawable(R.drawable.grey_border_background));
        Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
        Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
        Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabily_money);
        context = CabilyMoney.this;

        initialize();

        //Start XMPP Chat Service
//        ChatService.startUserAction(CabilyMoney.this);


        cabily_money_balance_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cabily_money_balance_select_ad_money:
                        // do operations specific to this selection
                        image_container_layout.setVisibility(View.GONE);
                        isSelectImage = false;
                        break;
                    case R.id.cabily_money_balance_select_bank_recipt:
                        // do operations specific to this selection
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            image_container_layout.setImageDrawable(getDrawable(R.drawable.ic_photo));
                        } else {
                            image_container_layout.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
                        }
                        byteArray = null;
                        image_container_layout.setVisibility(View.VISIBLE);
                        isSelectImage = true;
                        break;
                }
            }
        });
/*
        image_container_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionHelper.requestCamera(new PermissionHelper.OnPermissionGrantedListener() {
                    @Override
                    public void onPermissionGranted() {
                        PermissionHelper.requestStorage(new PermissionHelper.OnPermissionGrantedListener() {
                            @Override
                            public void onPermissionGranted() {
                                selectImage();
                            }
                        });

                    }
                });

            }
        });
*/
        receipt_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RecepitList.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

        layout_current_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CabilyMoney.this, CabilyMoneyTransaction.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        Et_cabilymoney_enteramount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(Et_cabilymoney_enteramount);
                }
                return false;
            }
        });

        Bt_cabilymoney_minimum_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_cabilymoney_enteramount.setText(Str_minimum_amt);
                Bt_cabilymoney_minimum_amount.setBackgroundColor(0xFFe84c3d);
                Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_minimum_amount.setTextColor(0xFFFFFFFF);
                Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
                Et_cabilymoney_enteramount.setSelection(Et_cabilymoney_enteramount.getText().length());
            }
        });

        Bt_cabilymoney_between_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_cabilymoney_enteramount.setText(Str_midle_amt);
                Bt_cabilymoney_between_amount.setBackgroundColor(0xFFe84c3d);
                Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                Bt_cabilymoney_between_amount.setTextColor(0xFFFFFFFF);
                Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);
                Et_cabilymoney_enteramount.setSelection(Et_cabilymoney_enteramount.getText().length());
            }
        });

        Bt_cabilymoney_maximum_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Et_cabilymoney_enteramount.setText(Str_maximum_amt);
                Bt_cabilymoney_maximum_amount.setBackgroundColor(0xFFe84c3d);
                Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                Bt_cabilymoney_maximum_amount.setTextColor(0xFFFFFFFF);
                Et_cabilymoney_enteramount.setSelection(Et_cabilymoney_enteramount.getText().length());
            }
        });

        Bt_add_cabilymoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enteredValue = Et_cabilymoney_enteramount.getText().toString();

                Str_minimum_amt = Str_minimum_amt.replace(",", "");
                enteredValue = enteredValue.replace(",", "");
                Str_maximum_amt = Str_maximum_amt.replace(",", "");

                try {
                    if (Str_minimum_amt != null && Str_minimum_amt.length() > 0) {
                        if (enteredValue.length() == 0) {
                            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.action_loading_cabily_money_empty_field));
                        } else if (Double.parseDouble(enteredValue) < Double.parseDouble(Str_minimum_amt) || Double.parseDouble(enteredValue) > Double.parseDouble(Str_maximum_amt)) {
                            Alert(getResources().getString(R.string.action_error), getResources().getString(R.string.cabilymoney_lable_rechargemoney_alert) + " " + ScurrencySymbol + Str_minimum_amt + " " + "-" + " " + ScurrencySymbol + Str_maximum_amt);
                        } else {
                            cd = new ConnectionDetector(CabilyMoney.this);
                            isInternetPresent = cd.isConnectingToInternet();

                            if (isInternetPresent) {
                                if (isSelectImage) {
                                    if (byteArray != null && byteArray.length > 0) {
                                        UploadDriverImage(Iconstant.cabily_add_money_url_addamount);
                                    } else {
                                        Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_select_image));
                                    }
                                } else {
                                    if (Sauto_charge_status.equalsIgnoreCase("1")) {
                                        postRequest_AddMoney(Iconstant.cabily_add_money_url);
                                    } else {
                                        Intent intent = new Intent(CabilyMoney.this, CabilyMoneyWebview.class);
                                        intent.putExtra("cabilyMoney_recharge_amount", enteredValue);
                                        intent.putExtra("cabilyMoney_currency_symbol", ScurrencySymbol);
                                        intent.putExtra("cabilyMoney_currentBalance", Str_currentbalance);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                    }
                                }
                            } else {
                                Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    private void initialize() {
        session = new SessionManager(CabilyMoney.this);
        cd = new ConnectionDetector(CabilyMoney.this);
        isInternetPresent = cd.isConnectingToInternet();

        receipt_history = findViewById(R.id.receipt_history);
        image_container_layout = findViewById(R.id.image_container_layout);
        cabily_money_balance_select = findViewById(R.id.cabily_money_balance_select);
        Bt_add_cabilymoney = (Button) findViewById(R.id.cabily_money_add_money_button);
        Et_cabilymoney_enteramount = (EditText) findViewById(R.id.cabily_money_enter_amount_edittext);
        Bt_cabilymoney_minimum_amount = (Button) findViewById(R.id.cabily_money_minimum_amt_button);
        Bt_cabilymoney_maximum_amount = (Button) findViewById(R.id.cabily_money_maximum_amt_button);
        Bt_cabilymoney_between_amount = (Button) findViewById(R.id.cabily_money_between_amt_button);
        Tv_cabilymoney_current_balnce = (TextView) findViewById(R.id.cabily_money_your_balance_textview);
        layout_current_transaction = (RelativeLayout) findViewById(R.id.cabily_money_current_balance_layout);
        back = (RelativeLayout) findViewById(R.id.cabily_money_header_back_layout);

        Et_cabilymoney_enteramount.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        Et_cabilymoney_enteramount.addTextChangedListener(EditorWatcher);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        // -----code to refresh drawer using broadcast receiver-----
        refreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_CLASS_CABILY_MONEY_REFRESH");
        registerReceiver(refreshReceiver, intentFilter);
        if (isInternetPresent) {
            postRequest_CabilyMoney(Iconstant.cabily_money_url);
        } else {
            Alert(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.alert_nointernet));
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final PkDialog mDialog = new PkDialog(CabilyMoney.this);
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

    //--------------Close KeyBoard Method-----------
    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //-----------------------Cabily Money Post Request-----------------
    private void postRequest_CabilyMoney(String Url) {
        dialog = new Dialog(CabilyMoney.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_loading));


        System.out.println("-------------CabilyMoney Url----------------" + Url);
        Log.e("UserID", ": " + UserID);
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        mRequest = new ServiceRequest(CabilyMoney.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("-------------CabilyMoney Response----------------" + response);
                Log.e("response", ": " + response);
                String Sstatus = "", Scurrency_code = "", Scurrentbalance = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    if (object.has("auto_charge_status"))
                        Sauto_charge_status = object.getString("auto_charge_status");
                    if (Sstatus.equalsIgnoreCase("1")) {
                        JSONObject response_object = object.getJSONObject("response");
                        if (response_object.length() > 0) {
                            Scurrency_code = response_object.getString("currency");
                            Scurrentbalance = response_object.getString("current_balance");
                            Str_currentbalance = response_object.getString("current_balance");
                            ScurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(Scurrency_code);

                            Object check_recharge_boundary_object = response_object.get("recharge_boundary");
                            if (check_recharge_boundary_object instanceof JSONObject) {
                                JSONObject recharge_object = response_object.getJSONObject("recharge_boundary");
                                if (recharge_object.length() > 0) {
                                    Str_minimum_amt = recharge_object.getString("min_amount");
                                    Str_maximum_amt = recharge_object.getString("max_amount");
                                    Str_midle_amt = recharge_object.getString("middle_amount");
                                    isRechargeAvailable = true;
                                } else {
                                    isRechargeAvailable = false;
                                }
                            }
                        }
                    } else {
                        isRechargeAvailable = false;
                    }


                    if (Sstatus.equalsIgnoreCase("1") && isRechargeAvailable) {
                        session.createWalletAmount(ScurrencySymbol + Str_currentbalance);
                        NavigationDrawer.navigationNotifyChange();

                        Bt_cabilymoney_minimum_amount.setText(ScurrencySymbol + Str_minimum_amt);
                        Bt_cabilymoney_maximum_amount.setText(ScurrencySymbol + Str_maximum_amt);
                        Bt_cabilymoney_between_amount.setText(ScurrencySymbol + Str_midle_amt);
                        Tv_cabilymoney_current_balnce.setText(ScurrencySymbol + Scurrentbalance);
                        Et_cabilymoney_enteramount.setHint(getResources().getString(R.string.cabilymoney_lable_rechargemoney_edittext_hint) + " " + ScurrencySymbol + Str_minimum_amt + " " + "-" + " " + ScurrencySymbol + Str_maximum_amt);
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

    //-----------------------Cabily Money Add Post Request-----------------
    private void postRequest_AddMoney(String Url) {
        dialog = new Dialog(CabilyMoney.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------Cabily ADD Money Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);
        jsonParams.put("total_amount", enteredValue);

        mRequest = new ServiceRequest(CabilyMoney.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------Cabily ADD Money Response----------------" + response);

                String Sstatus = "", Smessage = "", Swallet_money = "";

                try {
                    JSONObject object = new JSONObject(response);
                    Sstatus = object.getString("status");
                    Smessage = object.getString("msg");
                    Swallet_money = object.getString("wallet_amount");

                    if (Sstatus.equalsIgnoreCase("1")) {
                        session.createWalletAmount(ScurrencySymbol + Swallet_money);
                        NavigationDrawer.navigationNotifyChange();

                        Alert(getResources().getString(R.string.action_success), getResources().getString(R.string.action_loading_cabilymoney_transaction_wallet_success));
                        Et_cabilymoney_enteramount.setText("");
                        Tv_cabilymoney_current_balnce.setText(ScurrencySymbol + Swallet_money);
                        Bt_cabilymoney_minimum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                        Bt_cabilymoney_between_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));
                        Bt_cabilymoney_maximum_amount.setBackground(getResources().getDrawable(R.drawable.grey_border_background));

                        Bt_cabilymoney_minimum_amount.setTextColor(0xFF4E4E4E);
                        Bt_cabilymoney_between_amount.setTextColor(0xFF4E4E4E);
                        Bt_cabilymoney_maximum_amount.setTextColor(0xFF4E4E4E);

                    } else {
                        Alert(getResources().getString(R.string.action_error), Smessage);
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

    //-----------------------------------multipartcall-----------------imageupload---------------------
    private void UploadDriverImage(String url) {
        final HashMap<String, String> user = session.getUserDetails();
        final String gcmID = user.get(SessionManager.KEY_GCM_ID);
        final String Agent_Name = user.get(SessionManager.KEY_ID_NAME);
        final String language_code = user.get(SessionManager.KEY_Language_code);
        dialog = new Dialog(CabilyMoney.this);
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

                System.out.println("------------- image response-----------------" + response.data);
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    image_container_layout.setImageDrawable(getDrawable(R.drawable.ic_photo));
                } else {
                    image_container_layout.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
                }
                byteArray = null;
                image_container_layout.setVisibility(View.VISIBLE);
                isSelectImage = true;

                Alert(getResources().getString(R.string.action_success), getResources().getString(R.string.action_loading_cabilymoney_transaction_wallet_success_admin_confirm));
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
                headers.put("isapplication", Iconstant.cabily_IsApplication);
                headers.put("applanguage", language_code);
                headers.put("apptype", Iconstant.cabily_AppType);
                headers.put("userid", UserID);
                headers.put("apptoken", gcmID);
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
                params.put("user_id", UserID);
                System.out.println("user_id---------------" + UserID);
                params.put("total_amount", enteredValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("bank", new DataPart(System.currentTimeMillis() + "cabily_money.jpg", byteArray));

                System.out.println("user_image--------edit------" + byteArray);

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

    //-----------------------------------multipartcall-----------------imageupload---------------------
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
    public void onDestroy() {
        // Unregister the logout receiver
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CabilyMoney.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                image_container_layout.setImageBitmap(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byteArray = bytes.toByteArray();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            if(data!=null&&data.getData()!=null){
            Uri selectedImage = data.getData();
            InputStream iStream = null;
            try {
                if (selectedImage != null) {
                    image_container_layout.setImageURI(selectedImage);
                    iStream = getContentResolver().openInputStream(selectedImage);
                    try {
                        byteArray = getBytes(iStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }}
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.package.ACTION_CLASS_CABILY_MONEY_REFRESH")) {
                if (isInternetPresent) {
                    postRequest_CabilyMoney(Iconstant.cabily_money_url);
                }
            }
        }
    }
}
