package com.mylibrary.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Prem Kumar and Anitha on 10/8/2015.
 */
public class CustomEdittext extends EditText {

    public CustomEdittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEdittext(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
            setTypeface(tf);
        }
    }

}
