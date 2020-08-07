package com.shrinkcom.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shrinkcom.passenger.R;
import com.shrinkcom.utils.SessionManager;

public class SelectCorrierServicesActivity extends AppCompatActivity {

    TextView tv_booknow;
    LinearLayout layoutInternational, layoutdomestic;
    String strtype="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_corrier_services);

        tv_booknow=findViewById(R.id.tv_booknow);
        layoutInternational = findViewById(R.id.layooutinternationl);
        layoutdomestic = findViewById(R.id.idlayoutdomestic);


        layoutInternational.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strtype = "INTERNATIONL";
            }
        });

        layoutdomestic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strtype = "DOMESTIC";
            }
        });



        tv_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (strtype.equals("")){
                    Toast.makeText(SelectCorrierServicesActivity.this, "Please Select Service type", Toast.LENGTH_SHORT).show();
                }else {

                    new SessionManager(SelectCorrierServicesActivity.this).setStringValue(SessionManager.KEY_SERVICETYPE,strtype);

                    Intent i = new Intent(SelectCorrierServicesActivity.this, NavigationDrawer.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
    }
}
