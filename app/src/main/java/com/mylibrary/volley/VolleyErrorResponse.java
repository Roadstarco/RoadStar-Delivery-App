package com.mylibrary.volley;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.shrinkcom.utils.NetworkChangeReceiver;
import com.shrinkcom.passenger.R;


/**
 * Created by Prem Kumar and Anitha on 10/8/2015.
 */
public class VolleyErrorResponse {

    public static void volleyError(Context context, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(context, context.getResources().getString(R.string.service_request_net_slow_alert), Toast.LENGTH_LONG).show();
            NetworkChangeReceiver.firstTime = true;
        } else if (error instanceof AuthFailureError) {
        //    Toast.makeText(context, "AuthFailureError", Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {
         //   Toast.makeText(context, "ServerError", Toast.LENGTH_LONG).show();
        } else if (error instanceof NetworkError) {
            NetworkChangeReceiver.firstTime = true;
         //   Toast.makeText(context, "NetworkError", Toast.LENGTH_LONG).show();
        } else if (error instanceof ParseError) {
        //    Toast.makeText(context, "ParseError", Toast.LENGTH_LONG).show();
        }
    }
}
