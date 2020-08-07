package com.mylibrary.volley;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shrinkcom.app.SplashPage;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.utils.NetworkChangeReceiver;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.dialog.PkDialog;
//import com.mylibrary.xmpp.XmppService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prem Kumar and Anitha on 11/26/2015.
 */
public class ServiceRequest {
    private Context context;
    private ServiceListener mServiceListener;
    private StringRequest stringRequest;
    SessionManager session;
    private String UserID = "", gcmID = "",language_code="",Agent_Name="";

    private boolean isDemoEnabled = true;

    private String userID = "";

    public interface ServiceListener {
        void onCompleteListener(String response);
        void onErrorListener();
    }

    public ServiceRequest(Context context) {
        this.context = context;
        session=new SessionManager(context);
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> language = session.getLanaguageCode();
        userID = user.get(SessionManager.KEY_USERID);
        gcmID = user.get(SessionManager.KEY_GCM_ID);
        Agent_Name = user.get(SessionManager.KEY_ID_NAME);
        language_code = user.get(SessionManager.KEY_Language_code);
        if(language_code.equals(""))
        {
            language_code="en";
        }
        else
        {

        }
       /* try {
            if (!language.get(SessionManager.KEY_Language).equals("")) {
                language_code = language.get(SessionManager.KEY_Language_code);

            } else {
                language_code = "en";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            language_code="en";
        }*/
        System.out.println("----------language_code------------------" + language_code);
        System.out.println("topuserid2--------"+userID);
        System.out.println("topgcmID2--------"+gcmID);
    }

    public void cancelRequest()
    {
        if (stringRequest != null) {
            stringRequest.cancel();
        }
    }


    public void makeServiceRequest(final String url, int method, final HashMap<String, String> param,ServiceListener listener) {

        this.mServiceListener=listener;

        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mServiceListener.onCompleteListener(response);
                    JSONObject object = new JSONObject(response);

                    if (object.has("is_dead")) {
                        System.out.println("-----------is dead----------------");
                        final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                session.logoutUser();
//                                context.stopService(new Intent(context, XmppService.class));
                                Intent intent = new Intent(context, SplashPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();
                    }
                    if (object.has("is_out")) {
                        System.out.println("-------------is out---------------");
                        final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(object.getString("message"));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                session.logoutUser();
//                                context.stopService(new Intent(context, XmppService.class));
                                Intent intent = new Intent(context, SplashPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();


                       /* session.logoutUser();
                        Intent intent = new Intent(context, SplashPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);*/
                     //   Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

                        /*final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                session.logoutUser();
                                Intent intent = new Intent(context, SingUpAndSignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();*/

                    }

                /*    if (object.has("is_dead")) {
                        System.out.println("-----------is dead----------------");
                        final PkDialog mDialog = new PkDialog(context);
                        mDialog.setDialogTitle(context.getResources().getString(R.string.action_session_expired_title));
                        mDialog.setDialogMessage(context.getResources().getString(R.string.action_session_expired_message));
                        mDialog.setPositiveButton(context.getResources().getString(R.string.action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                                sessionManager.logoutUser();
                                Intent intent = new Intent(context, SingUpAndSignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                            }
                        });
                        mDialog.show();

                    }*/

                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, context.getResources().getString(R.string.service_request_net_slow_alert), Toast.LENGTH_SHORT).show();
                        NetworkChangeReceiver.firstTime = true;
                    } else if (error instanceof AuthFailureError) {
        //                Toast.makeText(context, context.getResources().getString(R.string.service_request_auth_failure), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
         //               Toast.makeText(context, context.getResources().getString(R.string.service_request_server_error), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        NetworkChangeReceiver.firstTime = true;
         //               Toast.makeText(context, context.getResources().getString(R.string.service_request_network_error), Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
         //               Toast.makeText(context, context.getResources().getString(R.string.service_request_parseerror), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
                Log.e("TAG", "onErrorResponse: "+error.getMessage() );
               /* if (isDemoEnabled){

                    if (Iconstant.app_facebook_post_url.equalsIgnoreCase(url)) {
                        String resp = context.getString(R.string.demo_json);
                        mServiceListener.onCompleteListener(resp);
                    }

                }*/

                mServiceListener.onErrorListener();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                System.out.println("------------Authkey------cabily---------" + Agent_Name);
                System.out.println("------------userid----------cabily-----" + userID);
                System.out.println("------------apptoken----------cabily-----" + gcmID);
                System.out.println("------------applanguage----------cabily-----" + language_code);
                System.out.println("------------isapplication----------cabily-----" + Iconstant.cabily_IsApplication);
                System.out.println("------------apptype----------cabily-----" + Iconstant.cabily_AppType);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authkey", Agent_Name);
                headers.put("isapplication",Iconstant.cabily_IsApplication);
                headers.put("applanguage",language_code);
                headers.put("apptype", Iconstant.cabily_AppType);
                headers.put("userid",userID);
                headers.put("apptoken",gcmID);
              /*  System.out.println("servicereques  apptype------------------"+Iconstant.cabily_AppType);
                System.out.println("servicereques apptoken------------------"+gcmID);
                System.out.println("servicereques userid------------------"+UserID);
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("User-agent",Iconstant.cabily_userAgent);
                headers.put("isapplication",Iconstant.cabily_IsApplication);
                headers.put("applanguage",Iconstant.cabily_AppLanguage);
                headers.put("apptype",Iconstant.cabily_AppType);
                headers.put("apptoken",gcmID);
                headers.put("userid",UserID);*/
                System.out.println("app header------------"+headers.toString());
                return headers;
            }
        };

        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }




    public void makegetRequest(final String url, int method,ServiceListener listener) {

        this.mServiceListener=listener;

        stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mServiceListener.onCompleteListener(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(context, context.getResources().getString(R.string.service_request_net_slow_alert), Toast.LENGTH_SHORT).show();
                        NetworkChangeReceiver.firstTime = true;
                    } else if (error instanceof AuthFailureError) {

                    } else if (error instanceof ServerError) {
                    } else if (error instanceof NetworkError) {
                        NetworkChangeReceiver.firstTime = true;

                    } else if (error instanceof ParseError) {


                    }
                } catch (Exception e) {
                }
                Log.e("TAG", "onErrorResponse: "+error.getMessage() );
                mServiceListener.onErrorListener();
            }
        });
        //to avoid repeat request Multiple Time
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
