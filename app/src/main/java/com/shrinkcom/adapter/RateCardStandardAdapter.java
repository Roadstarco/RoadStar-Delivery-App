package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.RateCard_StdPojo;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 10/20/2015.
 */
public class RateCardStandardAdapter extends BaseAdapter
{

    private ArrayList<RateCard_StdPojo> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public RateCardStandardAdapter(Context c,ArrayList<RateCard_StdPojo> d)
    {
        context=c;
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
    public int getViewTypeCount()
    {
        return 1;
    }


    public class ViewHolder
    {
        private TextView price;
        public TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.ratecard_standard_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.ratecard_standard_single_title);
            holder.price = (TextView) view.findViewById(R.id.ratecard_standard_single_price);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        holder.title.setText(data.get(position).getStdrate_title());
        holder.price.setText(data.get(position).getStdrate_fare());
        return view;
    }
}


