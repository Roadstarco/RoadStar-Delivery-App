package com.shrinkcom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.shrinkcom.iconstant.Iconstant;
import com.shrinkcom.passenger.R;
import com.shrinkcom.pojo.walletrecipt.WalletReciept;

public class RecepitAdapter extends RecyclerView.Adapter<RecepitAdapter.ViewHolder> {

    private WalletReciept[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
private Context context;
    // data is passed into the constructor
    public RecepitAdapter(Context context, WalletReciept[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recepit_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.amount_reciept.setText("Total Amount : $"+mData[position].getTotalAmount());
        holder.status_recipt_history_adapter.setText("Status : "+mData[position].getPayStatus());
        holder.date_recipt_history_adapter.setText(mData[position].getPayDate());
        Picasso.with(context).load(Iconstant.cabily_recepit_url_addamount_image_pre_url+mData[position].getImage()).into(holder.reciepit_image);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView amount_reciept,status_recipt_history_adapter,date_recipt_history_adapter;
        ImageView reciepit_image;

        ViewHolder(View itemView) {
            super(itemView);
            reciepit_image = itemView.findViewById(R.id.reciepit_image);
            date_recipt_history_adapter = itemView.findViewById(R.id.date_recipt_history_adapter);
            amount_reciept = itemView.findViewById(R.id.amount_reciept);
            status_recipt_history_adapter = itemView.findViewById(R.id.status_recipt_history_adapter);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position


    // allows clicks events to be caught
   public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}