package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.RateCard_CardDisplayPojo;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 10/14/2015.
 */
public class RateCardAdapter extends BaseAdapter
{

    private ArrayList<RateCard_CardDisplayPojo> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public RateCardAdapter(Context c,ArrayList<RateCard_CardDisplayPojo> d)
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
        private TextView title,price,description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.ratecard_display_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.ratecard_display_single_title);
            holder.price = (TextView) view.findViewById(R.id.ratecard_display_single_price);
            holder.description = (TextView) view.findViewById(R.id.ratecard_display_single_description);

            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        System.out.println("title card----------------jai---------------"+data.get(position).getRate_title());
        System.out.println("description card----------------jai---------------"+data.get(position).getRate_sub_title());

        holder.title.setText(data.get(position).getRate_title());
        holder.description.setText(data.get(position).getRate_sub_title());

        if(data.get(position).getRate_fare().length()>0)
        {
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(data.get(position).getRate_fare());
        }
        else
        {
            holder.price.setVisibility(View.GONE);
        }


        return view;
    }
}


