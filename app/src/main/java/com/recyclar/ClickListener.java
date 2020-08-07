package com.recyclar;

import android.view.View;

public interface ClickListener {

    void onPositionClicked(int position, View view);

    void onLongClicked(int position);
}