package com.mylibrary.volley;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.shrinkcom.utils.MyActivityLifecycleCallbacks;
import com.shrinkcom.utils.SessionManager;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    SessionManager sessionManager;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        nukeSSL();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(AppController.this);
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void nukeSSL() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {


                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {

                    if(arg1.isValid()){
                        System.out.println("====Muruga IsValid=====true"+"==host name=="+arg0);
                        return true;
                    }else {
                        System.out.println("====Muruga IsValid=====false"+"==host name=="+arg0);
                        return false;
                    }
                    /*if (arg0.equalsIgnoreCase("http://cabily.zoplay.com/") ) {
                        System.out.println("====Muruga IsValid=====true"+"==host name=="+arg0);
                        return true;
                    } else {
                        System.out.println("====Muruga IsValid=====false"+"==host name=="+arg0);
                        return false;
                    }*/
                    //return true;
                }
            });
        } catch (Exception e) {
        }

    }
}