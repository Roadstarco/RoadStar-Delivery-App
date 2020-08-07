package com.shrinkcom.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mylibrary.volley.ServiceRequest;
import com.shrinkcom.adapter.RecepitAdapter;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.walletrecipt.WalletReciept;
import com.shrinkcom.utils.SessionManager;

import java.util.HashMap;

public class RecepitList extends Activity implements RecepitAdapter.ItemClickListener {
    private RecepitAdapter adapter;
    private RecyclerView receipt_recyclerview;
    private String UserID;
    private SessionManager session;
    private RelativeLayout back;
    private WalletReciept[] walletReciepts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recepit_history);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        UserID = user.get(SessionManager.KEY_USERID);
        receipt_recyclerview = findViewById(R.id.receipt_recyclerview);
        back = findViewById(R.id.cabily_money_header_back_layout);
        receipt_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        postRequest_recipt_load(Iconstant.cabily_recepit_url_addamount);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {

    }


    private void postRequest_recipt_load(String Url) {
        final Dialog dialog = new Dialog(RecepitList.this);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.custom_loading_textview);
        dialog_title.setText(getResources().getString(R.string.action_processing));

        System.out.println("-------------reciept Url----------------" + Url);

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", UserID);


        ServiceRequest mRequest = new ServiceRequest(RecepitList.this);
        mRequest.makeServiceRequest(Url, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {
                System.out.println("-------------Cabily ADD Money Response----------------" + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                walletReciepts = gson.fromJson(response, WalletReciept[].class);


                dialog.dismiss();
                if (walletReciepts != null && walletReciepts.length > 0) {
                    adapter = new RecepitAdapter(RecepitList.this, walletReciepts);
                    adapter.setClickListener(RecepitList.this);
                    receipt_recyclerview.setAdapter(adapter);
                }
            }

            @Override
            public void onErrorListener() {
                dialog.dismiss();
            }
        });
    }

}
