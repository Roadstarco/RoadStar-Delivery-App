package com.shrinkcom.subclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;

/**
 * Created by user88 on 4/28/2017.
 */

public class FragmentActivitySubclass extends FragmentActivity {
    BroadcastReceiver receiver;
    protected PowerManager.WakeLock mWakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mylibrary.pushnotification.finish.trackyourRide");
        filter.addAction("com.mylibrary.pushnotification.finish.PushNotificationAlert");
        filter.addAction("com.mylibrary.pushnotification.finish.TimerPage");
        filter.addAction("com.mylibrary.pushnotification.finish.FareBreakUp");
        filter.addAction("com.mylibrary.pushnotification.finish.FareBreakUpPaymentList");
        filter.addAction("com.mylibrary.pushnotification.finish.MyRidePaymentList");
        filter.addAction("com.mylibrary.pushnotification.finish.MyRideDetails");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.mylibrary.pushnotification.finish.trackyourRide")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.PushNotificationAlert")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.TimerPage")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.FareBreakUp")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.FareBreakUpPaymentList")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.MyRidePaymentList")) {
                    finish();
                } else if (intent.getAction().equals("com.mylibrary.pushnotification.finish.MyRideDetails")) {
                    finish();
                }
            }
        };
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        this.mWakeLock.release();
        super.onDestroy();
    }

}
