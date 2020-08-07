package com.shrinkcom.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.shrinkcom.passenger.R;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    Button btn_next,btn_english,btn_arabic;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        btn_next = (Button)findViewById(R.id.btn_next);
        btn_english = (Button)findViewById(R.id.btn_english);
        btn_arabic = (Button)findViewById(R.id.btn_arabic);
        context = this;



        btn_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageToLoad  = "en";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
                 Intent intent = new Intent(ChangeLanguageActivity.this, SignUpBannerPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });


        btn_arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageToLoad  = "ar";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
                Intent intent = new Intent(ChangeLanguageActivity.this, SignUpBannerPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(ChangeLanguageActivity.this,SignUpBannerPage.class);
                startActivity(next);
            }
        });




}
    }