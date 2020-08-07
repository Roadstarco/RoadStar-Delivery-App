package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.FavoriteListPojo;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 11/12/2015.
 */
public class FavoriteListAdapter extends BaseAdapter
{

    private ArrayList<FavoriteListPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public FavoriteListAdapter(Context c,ArrayList<FavoriteListPojo> d)
    {
        context=c;
        mInflater = LayoutInflater.from(context);
        data = d;
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
        private TextView title;
        private TextView locationName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.favorite_list_single, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.favourite_list_single_title);
            holder.locationName = (TextView) view.findViewById(R.id.favourite_list_single_location_name);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder.title.setText(data.get(position).getTitle());
        holder.locationName.setText(data.get(position).getAddress());

        return view;
    }
}


