package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.EmergencyPojo;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by jayachandran on 6/6/2017.
 */



public class EmergencyContactAdapter extends BaseAdapter {

    private ArrayList<EmergencyPojo> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public EmergencyContactAdapter(Context c, ArrayList<EmergencyPojo> d) {
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.emergency_contact_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.call_button);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(data.get(position).getTitle());

        return view;
    }
}


