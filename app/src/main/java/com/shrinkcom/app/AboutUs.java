package com.shrinkcom.app;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shrinkcom.utils.AppInfoSessionManager;
import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;

/**
 */
public class AboutUs extends Activity {
    private RelativeLayout back;
    private TextView Tv_more_info, version_code, Tv_About_us_content, tv_customer_phone, tv_customer_address,tv_terms_condition;
    AppInfoSessionManager appInfo_Session;
    private String currentVersion;
    SessionManager session;
    private String customerPhoneNo = "";
    final int PERMISSION_REQUEST_CODE = 111;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);
//        ChatService.startUserAction(AboutUs.this);
        back = (RelativeLayout) findViewById(R.id.aboutus_header_back_layout);
        Tv_more_info = (TextView) findViewById(R.id.more_info_baseurl);
        Tv_About_us_content = (TextView) findViewById(R.id.textView);
        version_code = (TextView) findViewById(R.id.aboutus_versioncode);

        tv_customer_phone = (TextView) findViewById(R.id.more_phone_baseurl);
        tv_customer_address = (TextView) findViewById(R.id.more_address_baseurl);
       // tv_terms_condition = (TextView) findViewById(R.id.aboutus_terms_condition);

        appInfo_Session = new AppInfoSessionManager(AboutUs.this);
        session = new SessionManager(AboutUs.this);
        try {
            currentVersion = AboutUs.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version_code.setText(getResources().getString(R.string.aboutus_lable_version_textview) + " " + currentVersion);

//        tv_terms_condition.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//             /*   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Iconstant.privacy_policy_url));
//                startActivity(browserIntent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
//            }
//        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter, R.anim.exit);
            return true;
        }
        return false;
    }
}


        /*
        HashMap<String, String> user1 = appInfo_Session.getAppInfo();
        final String Site_url = user1.get(AppInfoSessionManager.KEY_SITE_URL);

        HashMap<String, String> user2 = session.getAbout();
        final String about_us_content = user2.get(SessionManager.KEY_ABOUT_US);
        System.out.println("About us content" + about_us_content);

        HashMap<String, String> user3 = session.getcustomerdetail();
        customerPhoneNo = user3.get(SessionManager.KEY_CUS_PHO);
        final String customerAddress = user3.get(SessionManager.KEY_CUS_ADR);
        System.out.println("About us customer detail" + customerPhoneNo + " " + customerAddress);


       tv_customer_phone.setText(customerPhoneNo);
        tv_customer_address.setText(customerAddress);


        Tv_About_us_content.setText(about_us_content);
        Tv_more_info.setText(Site_url);
        */



        /*  tv_customer_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission() || !checkReadStatePermission()) {
                        requestPermission();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customerPhoneNo));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customerPhoneNo));
                    startActivity(intent);

                }
            }
        });

      Tv_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Site_url));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });



    }



    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + customerPhoneNo));
                    startActivity(callIntent);
                }
                break;

        }
    }*/


