package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shrinkcom.pojo.HomePojo;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 10/12/2015.
 */
public class SelectCarTypeAdapter extends BaseAdapter {

    private ArrayList<HomePojo> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public SelectCarTypeAdapter(Context c, ArrayList<HomePojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    public class ViewHolder {
        private TextView name;
        private ImageView dot_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.home_select_cartype_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.select_cartype_single_textview);
            holder.dot_image = (ImageView) view.findViewById(R.id.select_cartype_single_imageView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(data.get(position).getCat_name());

        if (data.get(position).getSelected_Cat().equalsIgnoreCase(data.get(position).getCat_id())) {
            holder.dot_image.setVisibility(View.VISIBLE);
        } else {
            holder.dot_image.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}

