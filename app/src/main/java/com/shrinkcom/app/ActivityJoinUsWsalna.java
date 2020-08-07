package com.shrinkcom.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.shrinkcom.passenger.R;


public class ActivityJoinUsWsalna extends AppCompatActivity {

    private RelativeLayout back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us_wsalna);
        back = (RelativeLayout) findViewById(R.id.aboutus_header_back_layout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();

            }
        });


}

}
