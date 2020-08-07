package com.shrinkcom.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;

import java.util.HashMap;

/**
 * Created by user88 on 10/17/2016.
 */
public class AboutUsWebView extends Activity {
    private RelativeLayout back;
    private Context context;
    private WebView webview;
    private ProgressBar progressBar;
    private String Str_URL = "";
    private SessionManager session;
    private String UserID = "";
    private String SrideId_intent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_webview);
        context = getApplicationContext();

        initialize();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* final PkDialog mDialog = new PkDialog(AboutUsWebView.this);
                mDialog.setDialogTitle(getResources().getString(R.string.cabily_webview_lable_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.cabily_webview_lable_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        // close keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);
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
                mDialog.show();*/
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);

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
        session = new SessionManager(AboutUsWebView.this);
        back = (RelativeLayout) findViewById(R.id.cabily_about_us_webview_header_back_layout);
        webview = (WebView) findViewById(R.id.cabily_about_us_webview);
        progressBar = (ProgressBar) findViewById(R.id.cabily_about_us_webview_progressbar);

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
        Str_URL = i.getStringExtra("URL");
        System.out.println("About Us URL------------------------------------>"+Str_URL);
        startWebView(Str_URL);
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });

        //Load url in webView
        webview.loadUrl(url);
    }


    //--------------Alert Method-----------
    private void Alert(String title, String alert) {

        final PkDialog mDialog = new PkDialog(AboutUsWebView.this);
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


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }




}