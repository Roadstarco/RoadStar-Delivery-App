package com.shrinkcom.adapter;

/**
 */
import android.app.Activity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinkcom.pojo.HomePojo;
import com.shrinkcom.utils.ImageLoader;
import com.shrinkcom.passenger.R;
import com.mylibrary.widgets.CircularImageView;

import java.util.ArrayList;


public class BookMyRide_Adapter extends BaseAdapter {

    private ArrayList<HomePojo> data;
    private ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private Activity context;

    public BookMyRide_Adapter(Activity c, ArrayList<HomePojo> d) {
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
        private CircularImageView image;
        private TextView name;
        private TextView time;
        private LinearLayout Ll_car;
        private View left_view, right_view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.bookmyride_single, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.bookmyride_single_carname);
            holder.time = (TextView) view.findViewById(R.id.bookmyride_single_time);
            holder.image = (CircularImageView) view.findViewById(R.id.bookmyride_single_car_image);
            holder.Ll_car = (LinearLayout) view.findViewById(R.id.bookmyride_single_car_layout);
            holder.left_view = (View) view.findViewById(R.id.book_my_ride_single_left_view);
            holder.right_view = (View) view.findViewById(R.id.book_my_ride_single_right_view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(data.get(position).getCat_name());
         holder.time.setText(data.get(position).getCat_time());

        if (data.get(position).getSelected_Cat().equalsIgnoreCase(data.get(position).getCat_id())) {
            imageLoader.DisplayImage(String.valueOf(data.get(position).getIcon_active()), holder.image);
        } else {
            imageLoader.DisplayImage(String.valueOf(data.get(position).getIcon_normal()), holder.image);
        }


        if (position == 0 && (getCount() - 1) == 0) {
            holder.left_view.setVisibility(View.GONE);
            holder.right_view.setVisibility(View.GONE);
        } else if (position == 0) {
            holder.left_view.setVisibility(View.GONE);
            holder.right_view.setVisibility(View.VISIBLE);
        } else if (position == (getCount() - 1)) {
            holder.left_view.setVisibility(View.VISIBLE);
            holder.right_view.setVisibility(View.GONE);
        } else {
            holder.left_view.setVisibility(View.VISIBLE);
            holder.right_view.setVisibility(View.VISIBLE);
        }



        //Code to adjust car at center
        Display display = context.getWindowManager().getDefaultDisplay();
        if (data.size() == 1) {
            System.out.println("--------------jai-----------category size"+data.size());
            ViewGroup.LayoutParams params = holder.Ll_car.getLayoutParams();
            params.width = (display.getWidth()) - 8;
            holder.Ll_car.setLayoutParams(params);
        } else if (data.size() == 2) {
            System.out.println("--------------jai-----------category size"+data.size());

            ViewGroup.LayoutParams params = holder.Ll_car.getLayoutParams();
            params.width = (display.getWidth() / 2) - 8;
            holder.Ll_car.setLayoutParams(params);
        }
       else if (data.size() == 3) {
            System.out.println("--------------jai-----------category size"+data.size());
            ViewGroup.LayoutParams params = holder.Ll_car.getLayoutParams();
            params.width = (display.getWidth() / 3) - 8;
            holder.Ll_car.setLayoutParams(params);
        }else {
            System.out.println("--------------jai-----------category size"+data.size());
            ViewGroup.LayoutParams params = holder.Ll_car.getLayoutParams();
            params.width = (display.getWidth() / 4) - 8;
            holder.Ll_car.setLayoutParams(params);
        }

        return view;
    }
}