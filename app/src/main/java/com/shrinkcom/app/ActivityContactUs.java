package com.shrinkcom.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityContactUs extends AppCompatActivity implements View.OnClickListener {

    EditText edit_name,edit_email,edit_phone,edit_message;
    private String name, email, phone, message;
    TextView facebook,twitter;
    Button submit;
    Context mContext;

    RelativeLayout contact_layout_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        mContext = this;
        initViews();
        contact_layout_header = (RelativeLayout)findViewById(R.id.contact_layout_header);
        contact_layout_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
}

    private void initViews() {
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_phone = (EditText)findViewById(R.id.edit_phone);
        edit_message = (EditText)findViewById(R.id.edit_message);
        facebook = (TextView)findViewById(R.id.facebook);
        twitter = (TextView)findViewById(R.id.twitter);
        submit = (Button)findViewById(R.id.submit);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        submit.setOnClickListener(this);


    }

    private void validate() {
        name = edit_name.getText().toString().trim();
        email = edit_email.getText().toString().trim();
        phone = edit_phone.getText().toString().trim();
        message = edit_message.getText().toString().trim();
//        edit_name.getText().clear();
//        edit_email.getText().clear();
//        edit_phone.getText().clear();
//        edit_message.getText().clear();


        if (name.isEmpty()) {
            edit_name.setError("Name is required");
           edit_name.requestFocus();
        } else if (email.trim().length() == 00) {
            edit_email.setError(getResources().getString(R.string.action_alert_empty_email));
        } else if (!isValidEmail(email)) {
            edit_email.setError(getResources().getString(R.string.action_alert_invalid_email));
        } else if (phone.isEmpty()) {
           edit_phone.setError("Phone number is required");
           edit_phone.requestFocus();
       } else if (message.isEmpty()) {
            edit_message.setError("Message is required");
            edit_message.requestFocus();
        }else {
            Map<String, String> params = new HashMap<>();
            params.put("action", "contactus");
            params.put("name", "" + name);
            params.put("email", "" + email);
            params.put("mobile", "" + phone);
            params.put("message", "" + message);

            contactparams(params);
        }


       /* boolean checknetwork = CheckNetworkState.isOnline(mContext);
            if (checknetwork){
                Map<String, String> params = new HashMap<>();
            params.put("action", "contactus");
            params.put("name", "" + name);
            params.put("email", "" + email);
            params.put("mobile", "" + phone);
            params.put("message", "" + message);

            contactparams(params);

        } else {
            CheckNetworkState.showToast(mContext, getResources().getString(R.string.internetconnection));


        }*/


    }
  //  }

    private void contactparams(final Map<String, String> params) {

        final ProgressDialog pdialog = new ProgressDialog(mContext);
        pdialog.setTitle(getResources().getString(R.string.loading));
        pdialog.setCancelable(true);
        pdialog.show();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Iconstant.getcontactus,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("CouponResponse", "====>" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int resss = obj.getInt("status");
                            if (resss == 0) {
                                pdialog.dismiss();

                                AlertDialog alertDialog = new AlertDialog.Builder(ActivityContactUs.this).create(); //Read Update
                                alertDialog.setTitle("Thank you");
                                alertDialog.setMessage("Thank you for contact us");

                                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // here you can add functions
                                        edit_name.setText("");

                                        edit_email.setText("");
                                        edit_phone.setText("");
                                        edit_message.setText("");
                                    }
                                });

                                alertDialog.show();  //<-- See This!


//                               Intent intent = new Intent(ActivityContactUs.this, ActivityContactUs.class);
//                               startActivity(intent);
////                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
////                                        "mailto",null, null));
////                                startActivity(intent);
                               // finish();
                            } else {

                                // CheckNetworkState.showToast(RegistrationActivity.this,"Already exist");
                                pdialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //displaying the error in toast if occurrs
                        Log.e("TAG", "onErrorResponse: " + error.getMessage());
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                pdialog.dismiss();
                Log.e("SENDINGesponse", "--->" + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                validate();
                break;
                case R.id.facebook:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"));
                    startActivity(browserIntent);
                    break;
                    case R.id.twitter:
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"));
                         startActivity(twitterIntent);
                         break;

    }
//    facebook.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"));
//            startActivity(browserIntent);
//        }
//    });
//        twitter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"));
//                startActivity(browserIntent);
//            }
//        });
}
    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
