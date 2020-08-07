package com.shrinkcom.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shrinkcom.passenger.R;
import com.squareup.picasso.Picasso;

/**
 * Created by user14 on 10/12/2017.
 */

public class CustomPagerAdapter extends PagerAdapter {
    private Activity myContext;
    private int[] myImages;
    private LayoutInflater myLayoutInflater;
    private String[] myText;

    public CustomPagerAdapter(Activity aContext, int[] aImageInt, String[] aText) {
        this.myContext = aContext;
        this.myImages = aImageInt;
        this.myText = aText;
        myLayoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = myLayoutInflater.inflate(R.layout.layout_inflate_pager_list_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.layout_inflate_pager_list_item_IMG);
        TextView aTxtVw = (TextView) itemView.findViewById(R.id.layout_inflate_pager_list_item_TXT);

        Picasso.with(myContext).load(myImages[position]).placeholder(R.drawable.banner1).into(imageView);
        aTxtVw.setText(myText[position]);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}