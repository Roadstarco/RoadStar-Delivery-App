package com.shrinkcom.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//import com.mylibrary.xmpp.XmppService;


/**
 * Created by Prem Kumar and Anitha on 1/25/2016.
 */
public class IdentifyAppKilled extends Service {
    SessionManager sessionManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = new SessionManager(IdentifyAppKilled.this);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sessionManager != null) {
            sessionManager.setAppStatus("dead");
        }

//        stopService(new Intent(getApplicationContext(), XmppService.class));
//        stopService(new Intent(getApplicationContext(), GEOService.class));
    }

    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }
}