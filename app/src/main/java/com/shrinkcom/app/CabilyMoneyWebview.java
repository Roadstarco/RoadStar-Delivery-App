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

import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;

import java.util.HashMap;

/**
 * Created by Prem Kumar and Anitha on 10/22/2015.
 */
public class CabilyMoneyWebview extends Activity {
    private RelativeLayout back;
    private Context context;
    private WebView webview;
    private ProgressBar progressBar;

    private String Str_recharge_amount = "";
    private String Str_currency_symbol = "";
    private String Str_currentBalance = "";
    private SessionManager session;
    private String UserID = "";
    private String language_code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabily_money_webview);
        context = getApplicationContext();
        initialize();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PkDialog mDialog = new PkDialog(CabilyMoneyWebview.this);
                mDialog.setDialogTitle(getResources().getString(R.string.cabily_webview_lable_cancel_transaction));
                mDialog.setDialogMessage(getResources().getString(R.string.cabily_webview_lable_cancel_transaction_proceed));
                mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        // close keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                        //----changing button of cabily money page----
                        CabilyMoney.changeButton();

                        onBackPressed();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
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
        session = new SessionManager(CabilyMoneyWebview.this);

        back = (RelativeLayout) findViewById(R.id.cabily_money_webview_header_back_layout);
        webview = (WebView) findViewById(R.id.cabily_money_webview);
        progressBar = (ProgressBar) findViewById(R.id.cabily_money_webview_progressbar);

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
        HashMap<String, String> language = session.getLanaguageCode();
        language_code = user.get(SessionManager.KEY_Language_code);
        if(language_code.equals(""))
        {
            language_code="en";
        }

        Intent i = getIntent();
        Str_recharge_amount = i.getStringExtra("cabilyMoney_recharge_amount");
        Str_currency_symbol = i.getStringExtra("cabilyMoney_currency_symbol");
        Str_currentBalance = i.getStringExtra("cabilyMoney_currentBalance");
        System.out.println("-----cabily_money_webview_url----"+Iconstant.cabily_money_webview_url + UserID + "&total_amount=" + Str_recharge_amount+"&lang="+language_code);

        startWebView(Iconstant.cabily_money_webview_url + UserID + "&total_amount=" + Str_recharge_amount+"&lang="+language_code);
    }

    private void startWebView(String url) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
/*

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                System.out.println("=====webview error code==="+error.getErrorCode());
                switch(error.getErrorCode()) {

                    case SslError.SSL_DATE_INVALID:
                        //   message = ResHelper.getString(R.string.ssl_cert_error_date_invalid);
                        break;
                    case SslError.SSL_EXPIRED:
                        //  message = ResHelper.getString(R.string.ssl_cert_error_expired);
                        break;
                    case SslError.SSL_IDMISMATCH:
                        // message = ResHelper.getString(R.string.ssl_cert_error_idmismatch);
                        break;
                    case SslError.SSL_INVALID:
                        // message = ResHelper.getString(R.string.ssl_cert_error_invalid);
                        break;
                    case SslError.SSL_NOTYETVALID:
                        //  message = ResHelper.getString(R.string.ssl_cert_error_not_yet_valid);
                        break;
                    case SslError.SSL_UNTRUSTED:
                        //   message = ResHelper.getString(R.string.ssl_cert_error_untrusted);
                        break;
                    default:
                        //  message = ResHelper.getString(R.string.ssl_cert_error_cert_invalid);
                }
            }
*/

            //Show loader on url load
            @Override
            public void onLoadResource(WebView view, String url) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                try {

                    System.out.println("---------cabily_money_webview_url----------------"+url);

                    if (url.contains("/wallet/failed")) {
                        finishMethod();
                        } else if (url.contains("/wallet/completed")) {
                        webview.clearHistory();
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.package.ACTION_CLASS_CABILY_MONEY_REFRESH");
                        sendBroadcast(broadcastIntent);
                        finishMethod();
                    } else if (url.contains("/wallet/cancel")) {
                        finishMethod();
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

        final PkDialog mDialog = new PkDialog(CabilyMoneyWebview.this);
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

    public void finishMethod() {
        //----changing button of cabily money page----
        CabilyMoney.changeButton();
        finish();
        onBackPressed();
        overridePendingTransition(R.anim.enter, R.anim.exit);
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


            final PkDialog mDialog = new PkDialog(CabilyMoneyWebview.this);
            mDialog.setDialogTitle(getResources().getString(R.string.cabily_webview_lable_cancel_transaction));
            mDialog.setDialogMessage(getResources().getString(R.string.cabily_webview_lable_cancel_transaction_proceed));
            mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    // close keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                    //----changing button of cabily money page----
                    CabilyMoney.changeButton();

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
