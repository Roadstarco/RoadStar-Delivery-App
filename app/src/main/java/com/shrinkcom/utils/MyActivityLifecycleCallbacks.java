package com.shrinkcom.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

//import com.mylibrary.xmpp.XmppService;

import java.util.List;


public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {


    private String checkLifeCycleStatus = "foreground";
    private SessionManager sessionManager;

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
        if (!isAppIsInBackground(activity)) {
            if (checkLifeCycleStatus.equalsIgnoreCase("foreground")) {
                available(activity);
                checkLifeCycleStatus = "background";

                Intent broadcastIntent1 = new Intent();
                broadcastIntent1.setAction("com.package.ACTION_CLASS_TrackYourRide_foreground_REFRESH_page");
                activity.sendBroadcast(broadcastIntent1);
            }
        }
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {

    }

    public void onActivityStopped(Activity activity) {
        if (isAppIsInBackground(activity)) {
            if (checkLifeCycleStatus.equalsIgnoreCase("background")) {
                unAvailable(activity);
                checkLifeCycleStatus = "foreground";
            }
        }
    }


    private void available(Activity activity) {

        sessionManager = new SessionManager(activity);

        sessionManager.setAppStatus("resume");

        ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(activity, "available");
        chatAvailability.postChatRequest();

        if (sessionManager.isLoggedIn()) {
         /*   System.out.println("----------xmpp MyActivityLifeCycleCallBack-----------------" + isMyServiceRunning(XmppService.class, activity));
            if (!isMyServiceRunning(XmppService.class, activity)) {
                activity.startService(new Intent(activity, XmppService.class));
                System.out.println("----------xmpp service restarted-----------------");
            }*/
        }


        if (!isMyServiceRunning(IdentifyAppKilled.class, activity)) {
            activity.startService(new Intent(activity, IdentifyAppKilled.class));
        }



    }


    private void unAvailable(Activity activity) {
        sessionManager = new SessionManager(activity);
        sessionManager.setAppStatus("pause");

        ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(activity, "unavailable");
        chatAvailability.postChatRequest();
    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if(runningProcesses!=null) {
                if (runningProcesses.size() > 0) {
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (String activeProcess : processInfo.pkgList) {
                                if (activeProcess.equals(context.getPackageName())) {
                                    isInBackground = false;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass, Activity activity) {
        boolean b = false;
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                b = true;
                break;
            } else {
                b = false;
            }
        }
        return b;
    }


}
