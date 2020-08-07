package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shrinkcom.pojo.MyRidesPojo;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 10/28/2015.
 */
public class MyRidesAdapter extends BaseAdapter
{

    private ArrayList<MyRidesPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyRidesAdapter(Context c,ArrayList<MyRidesPojo> d)
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
        private ImageView car_type_image;
        private TextView address;
        private TextView date,status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if(convertView == null)
        {
            view = mInflater.inflate(R.layout.myrides_single, parent, false);
            holder = new ViewHolder();
            holder.address = (TextView) view.findViewById(R.id.myride_single_address_textview);
            holder.car_type_image = (ImageView) view.findViewById(R.id.myride_single_car_type_imageView);
            holder.date = (TextView) view.findViewById(R.id.myride_single_date_textview);
            holder.status = (TextView) view.findViewById(R.id.myride_single_status_textview);

            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        if(data.get(position).getRide_status().equalsIgnoreCase("Booked"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_booked_label));
            holder.status.setTextColor(0xFF607D8B);
            holder.car_type_image.setImageResource(R.drawable.myride_booked_icon);
        }
        else if(data.get(position).getRide_status().equalsIgnoreCase("Completed"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_completed_label));
            holder.status.setTextColor(0xFF027536);
            holder.car_type_image.setImageResource(R.drawable.myride_completed_icon);
        }
        else if(data.get(position).getRide_status().equalsIgnoreCase("Cancelled"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_cancelled_label));
            holder.status.setTextColor(0xFFD21C2C);
            holder.car_type_image.setImageResource(R.drawable.myride_cancelled_icon);
        }
        else if(data.get(position).getRide_status().equalsIgnoreCase("Onride"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_onride_label));
            holder.status.setTextColor(0xFFf19e12);
            holder.car_type_image.setImageResource(R.drawable.myride_onride_icon);
        }
        else if(data.get(position).getRide_status().equalsIgnoreCase("Finished"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_finished_label));
            holder.status.setTextColor(0xFFE91E63);
            holder.car_type_image.setImageResource(R.drawable.myride_finished_icon);
        }
        else if(data.get(position).getRide_status().equalsIgnoreCase("Expired"))
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_finished_label));
            holder.status.setTextColor(0xFFE91E63);
            holder.car_type_image.setImageResource(R.drawable.myride_finished_icon);
        }
        else
        {
            holder.status.setText(context.getResources().getString(R.string.my_rides_status_accept_label));
            holder.status.setTextColor(0xFF2EACB3);
            holder.car_type_image.setImageResource(R.drawable.myride_accept_icon);
        }


        holder.address.setText(data.get(position).getRide_time()+" "+context.getResources().getString(R.string.my_rides_from_label)+" "+data.get(position).getPickup());
        holder.date.setText("Booking Date "+data.get(position).getRide_date());

        return view;
    }
}



