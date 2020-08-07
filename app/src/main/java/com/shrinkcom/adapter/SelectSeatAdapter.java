package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shrinkcom.pojo.PoolRateCard;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by jayachandran on 6/6/2017.
 */



public class SelectSeatAdapter extends BaseAdapter {

    private ArrayList<PoolRateCard> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Context context;

    public SelectSeatAdapter(Context c, ArrayList<PoolRateCard> d) {
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
        private TextView name,cost;
        private ImageView check_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.select_seat_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.select_seattype_single_textview);
            holder.cost = (TextView) view.findViewById(R.id.select_seattype_single_textview_cost);
            holder.check_image = (ImageView) view.findViewById(R.id.select_seattype_single_imageView);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(data.get(position).getSeat()+" "+context.getResources().getString(R.string.home_seat));
        holder.cost.setText(data.get(position).getCost());

        System.out.println("-----jai---------------"+data.get(position).getSelect());

       if (data.get(position).getSelect().equalsIgnoreCase("yes")) {
            holder.check_image.setVisibility(View.VISIBLE);
        } else {
            holder.check_image.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}


