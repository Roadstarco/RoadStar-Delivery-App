package com.shrinkcom.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.subclass.ActivitySubClass;
import com.shrinkcom.passenger.R;

/**
 * Created by Prem Kumar and Anitha on 11/6/2015.
 */
public class PushNotificationAlert extends ActivitySubClass {
    TextView Tv_ok,Tv_title;
    RelativeLayout Rl_ok;
    TextView message;
    private String Str_message, Str_action, SrideId_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushnotification_alert);
        initialize();

        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Str_action.equalsIgnoreCase(Iconstant.PushNotification_RideCancelled_Key) || Str_action.equalsIgnoreCase(Iconstant.PushNotification_RideCompleted_Key)) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("com.mylibrary.pushnotification.updateBottom_view");
                    sendBroadcast(broadcastIntent);
                }

                if (Str_action.equalsIgnoreCase(Iconstant.PushNotification_PaymentPaid_Key)) {
                    Intent intent = new Intent(PushNotificationAlert.this, MyRideRating.class);
                    intent.putExtra("RideID", SrideId_intent);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                if (Str_action.equalsIgnoreCase(Iconstant.PushNotification_AcceptRideLater_Key)) {
                    Intent intent = new Intent(PushNotificationAlert.this, MyRidesDetail.class);
                    intent.putExtra("RideID", SrideId_intent);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                 //   overridePendingTransition(R.anim.enter, R.anim.exit);


                }else {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            }
        });
    }

    private void initialize() {
        Tv_ok = (TextView) findViewById(R.id.pushnotification_alert_ok_textview);
        Tv_title = (TextView) findViewById(R.id.pushnotification_alert_messge_label);
        message = (TextView) findViewById(R.id.pushnotification_alert_messge_textview);
        Rl_ok = (RelativeLayout) findViewById(R.id.pushnotification_alert_ok_layout);

        Intent intent = getIntent();
        Str_message = intent.getStringExtra("message");
        Str_action = intent.getStringExtra("Action");
        if (getIntent().getExtras().containsKey("RideID")) {
            SrideId_intent = intent.getStringExtra("RideID");
        }
        message.setText(Str_message);

        if(Str_action.equalsIgnoreCase(Iconstant.PushNotification_RideCancelled_Key))
        {
            Tv_title.setText(getResources().getString(R.string.pushnotification_alert_label_ride_cancelled_sorry));
        }
        else if(Str_action.equalsIgnoreCase(Iconstant.PushNotification_CabArrived_Key))
        {
            Tv_title.setText(getResources().getString(R.string.pushnotification_alert_label));
        }
        else if(Str_action.equalsIgnoreCase(Iconstant.PushNotification_RideCompleted_Key))
        {
            Tv_title.setText(getResources().getString(R.string.pushnotification_alert_label_ride_completed_thanks));
        }
        else if(Str_action.equalsIgnoreCase(Iconstant.PushNotification_PaymentPaid_Key))
        {
            Tv_title.setText(getResources().getString(R.string.pushnotification_alert_label_ride_arrived_success));
        }

       /* try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;

        }
        return false;
    }
}
