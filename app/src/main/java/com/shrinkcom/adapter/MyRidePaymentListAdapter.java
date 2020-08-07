package com.shrinkcom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shrinkcom.pojo.PaymentListPojo;
import com.shrinkcom.passenger.R;

import java.util.ArrayList;

/**
 * Created by Prem Kumar and Anitha on 11/2/2015.
 */
public class MyRidePaymentListAdapter extends BaseAdapter {

    private ArrayList<PaymentListPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyRidePaymentListAdapter(Context c, ArrayList<PaymentListPojo
            > d) {
        context = c;
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
    public int getViewTypeCount() {
        return 1;
    }


    public class ViewHolder {
        private TextView reason;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.myride_payment_list_single, parent, false);
            holder = new ViewHolder();
            holder.reason = (TextView) view.findViewById(R.id.myride_payment_list_textview);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.reason.setText(data.get(position).getPaymentName());
        return view;
    }
}

