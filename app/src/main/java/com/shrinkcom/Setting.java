package com.shrinkcom;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.shrinkcom.app.NavigationDrawer;
import com.shrinkcom.passenger.R;

import java.util.Locale;

public class Setting extends AppCompatActivity {

    private RelativeLayout back;
    RadioButton radioButton1,radioButton2;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        back = (RelativeLayout)findViewById(R.id.setting_header_back_layout);
        radioButton1 = (RadioButton)findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);


        context = this;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();

            }
        });

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               // LocaleHelper.setLocale(Setting.this, "en");
                String languageToLoad  = "en";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());

                Intent intent = new Intent(Setting.this, NavigationDrawer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               // LocaleHelper.setLocale(Setting.this, "ar");
                String languageToLoad  = "ar";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
                Intent intent = new Intent(Setting.this, NavigationDrawer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                Intent refresh = new Intent(Setting.this,  DashBoardDriver.class);
//                Setting.this.startActivity(refresh);
            }
        });




}
}