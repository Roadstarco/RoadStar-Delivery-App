package com.shrinkcom.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by w on 12/4/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    String refreshedToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onTokenRefresh: " + refreshedToken);
        // Saving reg id to shared preferences


        //TokenDataApi(APIUtils.BASE_URL);
        // sending reg id to your server


    }

   /* private void TokenDataApi(String baseUrl) {
        //getting the progressbar

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion

                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        MediaSessionCompat.Token data = gson.fromJson(response, Token.class);
                        if (data != null) {
                            List<UserDatum> list = data.getUserData();
                            if ((list != null) && (list.size() > 0)) {
                                *//*id = list.get(0).getId();
                                date = list.get(0).getCreatedOn();
                                userId = list.get(0).getUserid();
                                VehicleId = list.get(0).getVehicleId();*//*


                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //displaying the error in toast if occurrs
                        Log.e("TAG", "onErrorResponse: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "tokan_user");
                params.put("id", String.valueOf(manager.getUserID()));
                params.put("tokan", FirebaseInstanceId.getInstance().getToken());
                Log.e("token.... ", params.toString());

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/


}
