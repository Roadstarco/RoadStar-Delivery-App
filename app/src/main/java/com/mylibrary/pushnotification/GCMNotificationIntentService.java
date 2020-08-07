package com.mylibrary.pushnotification;

//import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;


import com.shrinkcom.app.SplashPage;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anitha
 */

public class GCMNotificationIntentService extends JobIntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GcmNotificationService";
    static final int JOB_ID = 1000;
    String CHANNEL_ID = "my_channel_01";
    String CHANNEL_NAME = "my_channel_name";
    int importance = NotificationManager.IMPORTANCE_HIGH;

    NotificationCompat.Builder builder;
    Context context = GCMNotificationIntentService.this;

    private String key1 = "", key2 = "", key3 = "", key4 = "", key5 = "",
            key6 = "", key7 = "", key8 = "", key9 = "", key10 = "",
            key11 = "", key12 = "", message = "";
    String action = "", msg1, title, banner;
    private static SessionManager session;

    //public GCMNotificationIntentService() {
    //    super("GcmIntentService");
   // }

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GCMNotificationIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        session = new SessionManager(context);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
               /* for (int i = 0; i < 3; i++) {
                    Log.d("Messsage Coming... ", "" + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }*/
//                Log.e("Message Completed work @ ", "" + SystemClock.elapsedRealtime());

                Log.e("Received: ", "" + extras.toString());
                Log.e("Received: ", "" + extras.toString());

                if (extras != null) {

                    try {
                        action = (String) extras.get(Iconstant.Push_Action);

                        if (action.equalsIgnoreCase(Iconstant.pushNotificationDriverLoc)) {

                        } else {

                            if (extras.containsKey("key1")) {
                                key1 = extras.get("key1").toString();
                            }
                            if (extras.containsKey("key2")) {
                                key2 = extras.get("key2").toString();
                            }
                            if (extras.containsKey("key3")) {
                                key3 = extras.get("key3").toString();
                            }
                            if (extras.containsKey("key4")) {
                                key4 = extras.get("key4").toString();
                            }
                            if (extras.containsKey("key5")) {
                                key5 = extras.get("key5").toString();
                            }
                            if (extras.containsKey("key6")) {
                                key6 = extras.get("key6").toString();
                            }
                            if (extras.containsKey("key7")) {
                                key7 = extras.get("key7").toString();
                            }
                            if (extras.containsKey("key8")) {
                                key8 = extras.get("key8").toString();
                            }
                            if (extras.containsKey("key9")) {
                                key9 = extras.get("key9").toString();
                            }
                            if (extras.containsKey("key10")) {
                                key10 = extras.get("key10").toString();
                            }
                            if (extras.containsKey("key11")) {
                                key11 = extras.get("key11").toString();
                            }
                            if (extras.containsKey("key12")) {
                                key12 = extras.get("key12").toString();
                            }
                            if (extras.containsKey(Iconstant.Push_Message)) {
                                message = extras.get(Iconstant.Push_Message).toString();
                            }

                            sendNotification(message.toString());


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
      // GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    @SuppressWarnings("deprecation")
    private void sendNotification(String msg) {
        Intent notificationIntent = null;
        int id = createID();
        notificationIntent = new Intent(GCMNotificationIntentService.this, SplashPage.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("type", "push");

        if (action.equalsIgnoreCase(Iconstant.PushNotification_AcceptRide_Key)) {
            notificationIntent.putExtra("page", "track");
            notificationIntent.putExtra("rideId", key9);

        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_CabArrived_Key)) {
            notificationIntent.putExtra("page", "track");
            notificationIntent.putExtra("rideId", key1);

        } else if (action.equalsIgnoreCase(Iconstant.pushNotificationBeginTrip)) {
            notificationIntent.putExtra("page", "track");
            notificationIntent.putExtra("rideId", key1);

        } else if (action.equalsIgnoreCase(Iconstant.pushNotification_ReloadTrackingPage_Key)) {

            notificationIntent.putExtra("page", "track");
            notificationIntent.putExtra("rideId", key1);

        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_RequestPayment_Key)) {

            notificationIntent.putExtra("page", "farebreakup");
            notificationIntent.putExtra("rideId", key6);


        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_PaymentPaid_Key)) {

            notificationIntent.putExtra("page", "rating");
            notificationIntent.putExtra("rideId", key1);


        } else if (action.equalsIgnoreCase(Iconstant.pushNotification_Ads)) {

            notificationIntent.putExtra("page", "Ads");
            notificationIntent.putExtra("title", key1);
            notificationIntent.putExtra("msg", key2);
            notificationIntent.putExtra("banner", key3);

        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_RideCompleted_Key)) {
            notificationIntent.putExtra("page", "details");
            notificationIntent.putExtra("rideId", key1);

        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_RequestPayment_makepayment_Stripe_Key)) {
            notificationIntent.putExtra("page", "details");
            notificationIntent.putExtra("rideId", key1);

        } else if (action.equalsIgnoreCase(Iconstant.PushNotification_RideCancelled_Key)) {
            notificationIntent.putExtra("page", "details");
            notificationIntent.putExtra("rideId", key1);

        }


        PendingIntent contentIntent = PendingIntent.getActivity(GCMNotificationIntentService.this, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        final NotificationManager nm = (NotificationManager) GCMNotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = GCMNotificationIntentService.this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GCMNotificationIntentService.this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.pushlogo)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_logo))
                .setTicker(msg)
                .setColor(Color.RED)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setLights(0xffff0000, 100, 2000)
                .setPriority(Notification.DEFAULT_SOUND)
                .setContentText(msg);

        Notification n = builder.getNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

            if (smallIconViewId != 0) {
                if (n.contentView != null)
                    n.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (n.headsUpContentView != null)
                    n.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                if (n.bigContentView != null)
                    n.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            // Create a notification and set the notification channel.
            Notification notification = new Notification.Builder(GCMNotificationIntentService.this)
                    .setContentTitle("New Message")
                    .setContentText("You've received new messages.")
                    .setSmallIcon(R.drawable.pushlogo)
                    .setChannelId(CHANNEL_ID)
                    .build();

            n.defaults |= Notification.DEFAULT_ALL;
            nm.notify(NOTIFICATION_ID, n);
            removeNotification(NOTIFICATION_ID, nm);
        }
    }


    private void removeNotification(final int id, final NotificationManager notificationManager) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(id);
            }
        }, 10000 );
    }


    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }
}