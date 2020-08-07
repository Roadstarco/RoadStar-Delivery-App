package com.shrinkcom.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.recyclar.ClickListener;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.EmergencyPojoNew;


import java.lang.ref.WeakReference;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {

    private Activity context;

    ClickListener clickListener;
    private List<EmergencyPojoNew> emergencyPojo;


    public EmergencyAdapter(Activity context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;


    }

    public void UpdateList(List<EmergencyPojoNew> EmergencyPojo) {
        this.emergencyPojo = EmergencyPojo;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergencyadapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, clickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(emergencyPojo.get(position));

        EmergencyPojoNew pu = emergencyPojo.get(position);
        holder.full_name.setText(pu.getTitle());
        holder.phone_number.setText("" + pu.getNumber());

    }

    @Override
    public int getItemCount() {
        if (emergencyPojo == null) {
            return -1;
        } else {
            Log.e("TAG", "getItemCount: " + emergencyPojo.size());
            return emergencyPojo.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView full_name;
        public TextView phone_number;
        public ImageView phone_call;
        private WeakReference<ClickListener> listenerRef;

        public ViewHolder(final View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            full_name = (TextView) itemView.findViewById(R.id.full_name);
            phone_number = (TextView) itemView.findViewById(R.id.phone_number);
            phone_call = (ImageView) itemView.findViewById(R.id.phone_call);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EmergencyPojoNew planStation = (EmergencyPojoNew) view.getTag();

                }
            });
            phone_call.setOnClickListener(this);
//



        }

        @Override
        public void onClick(View v) {
            if (v.getId() == phone_call.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


}