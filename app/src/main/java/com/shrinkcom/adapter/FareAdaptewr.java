package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.FarePojo;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by user88 on 8/2/2017.
 */

public class FareAdaptewr extends BaseAdapter {

    Context context;
    ArrayList<FarePojo> data;
    private LayoutInflater mInflater;
    public FareAdaptewr(Context context, ArrayList<FarePojo> data)
    {
        this.context=context;
        this.data=data;
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        private TextView value;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {

            view = mInflater.inflate(R.layout.farelist, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.faretile);
            holder.value = (TextView) view.findViewById(R.id.farevalue);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        holder.title.setText(data.get(position).getTitle());
        holder.value.setText(data.get(position).getValue());

        return view;
    }
}
