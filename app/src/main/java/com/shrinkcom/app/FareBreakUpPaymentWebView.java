package com.shrinkcom.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;

import java.util.HashMap;


/**
 * Created by Prem Kumar and Anitha on 11/7/2015.
 */
public class FareBreakUpPaymentWebView extends Activity {
    private RelativeLayout back;
    private Context context;
    private WebView webview;
    private ProgressBar progressBar;

    private String Str_mobileID = "";
    private SessionManager session;
    private String UserID = "";
    private String SrideId_intent = "";
    private String sType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myride_payment_webview);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PkDialog mDialog = new PkDialog(FareBreakUpPaymentWebView.this);
                mDialog.setDialogTitle(getResources().getString(R.string.cabily_webview_lable_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.cabily_webview_lable_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        // close keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                        Intent intent = new Intent(FareBreakUpPaymentWebView.this, MyRidesDetail.class);
                        intent.putExtra("RideID", SrideId_intent);
                        intent.putExtra("page", "webview");
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        finish();
                        FareBreakUp.farebreakup_class.finish();
                        FareBreakUpPaymentList.myride_paymentList_class.finish();






                    }
                });
                mDialog.setNegativeButton(getResources().getString(R.string.action_cancel_alert), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();

            }
        });

        // Show the progress bar
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);

                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

    }

    private void initialize() {
        session = new SessionManager(FareBreakUpPaymentWebView.this);

        back = (RelativeLayout) findViewById(R.id.myride_payment_webview_header_back_layout);
        webview = (WebView) findViewById(R.id.myride_payment_webview);
        progressBar = (ProgressBar) findViewById(R.id.myride_payment_webview_progressbar);

        // Enable Javascript to run in WebView
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);

        // Allow Zoom in/out controls
        webview.getSettings().setBuiltInZoomControls(true);

        // Zoom out the best fit your screen
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);

        Intent i = getIntent();
        if (i.hasExtra("type")) {
            sType = i.getStringExtra("type");
        }
        Str_mobileID = i.getStringExtra("MobileID");
        SrideId_intent = i.getStringExtra("RideID");

        startWebView(Iconstant.makepayment_webview_url + Str_mobileID);
    }


    private void startWebView(String url) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            @Override
            public void onLoadResource(WebView view, String url) {
                System.out.println("url---load---------->"+url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("url---onPageFinished---------->"+url);
                try {
                    if (url.contains("webview/trip/cancelled")) {
                        Toast.makeText(FareBreakUpPaymentWebView.this,getResources().getString(R.string.cabily_webview_lable_transaction_failed),Toast.LENGTH_LONG).show();
                       // AlertPay(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.cabily_webview_lable_transaction_failed));
                        finish();
                        onBackPressed();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    } else if (url.contains("pay-completed")) {

                        Toast.makeText(FareBreakUpPaymentWebView.this,getResources().getString(R.string.cabily_webview_lable_transaction_success),Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(FareBreakUpPaymentWebView.this, MyRideRating.class);
                        if ("push".equalsIgnoreCase(sType)){
                            intent.putExtra("type", sType);
                            intent.putExtra("RideID", SrideId_intent);
                        }else {
                            intent.putExtra("RideID", SrideId_intent);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        finish();
                        FareBreakUp.farebreakup_class.finish();
                        FareBreakUpPaymentList.myride_paymentList_class.finish();


                   //     AlertPaySuccess(getResources().getString(R.string.action_success), getResources().getString(R.string.cabily_webview_lable_transaction_success));
                    } else if (url.contains("pay-cancel")) {


                        Toast.makeText(FareBreakUpPaymentWebView.this,getResources().getString(R.string.cabily_webview_lable_transaction_failed),Toast.LENGTH_LONG).show();


                        finish();
                        onBackPressed();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                     //   AlertPay(getResources().getString(R.string.alert_label_title), getResources().getString(R.string.cabily_webview_lable_transaction_cancel));
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        //Load url in webView
        webview.loadUrl(url);
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentWebView.this);
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

    //--------------Alert Pay Transaction Method-----------
    private void AlertPay(String title, String alert) {

        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentWebView.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        mDialog.show();

    }

    //--------------Alert Pay Transaction Success Method-----------
    private void AlertPaySuccess(String title, String alert) {


        final PkDialog mDialog = new PkDialog(FareBreakUpPaymentWebView.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(FareBreakUpPaymentWebView.this, MyRideRating.class);
                intent.putExtra("RideID", SrideId_intent);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                finish();
                FareBreakUp.farebreakup_class.finish();
                FareBreakUpPaymentList.myride_paymentList_class.finish();
            }
        });
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            final PkDialog mDialog = new PkDialog(FareBreakUpPaymentWebView.this);
            mDialog.setDialogTitle(getResources().getString(R.string.cabily_webview_lable_cancel_transaction));
            mDialog.setDialogMessage(getResources().getString(R.string.cabily_webview_lable_cancel_transaction_proceed));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
            mDialog.setNegativeButton(getResources().getString(R.string.action_cancel_alert), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.show();

            return true;
        }
        return false;
    }

}

