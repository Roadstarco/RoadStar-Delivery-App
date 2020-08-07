package com.shrinkcom.adapter;

/**
 * Created by Prem Kumar on 10/1/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shrinkcom.utils.SessionManager;
import com.shrinkcom.passenger.R;
import com.mylibrary.widgets.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * Created by Prem Kumar on 9/21/2015.
 */
public class HomeMenuListAdapter extends BaseAdapter
{
    Context context;
    String[] mTitle;
    int[] mIcon;
    LayoutInflater inflater;
    View itemView;
    public SessionManager session;

    public HomeMenuListAdapter(Context context, String[] title, int[] icon)
    {
        this.context = context;
        this.mTitle = title;
        this.mIcon = icon;
    }

    @Override
    public int getCount()
    {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Declare Variables
        TextView txtTitle,profile_name,profile_mobile,walletMoney;
        RoundedImageView profile_icon;
        ImageView imgIcon;
        RelativeLayout general_layout,profile_layout;
        View drawer_view;

        session = new SessionManager(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        txtTitle = (TextView) itemView.findViewById(R.id.title);
        walletMoney= (TextView) itemView.findViewById(R.id.drawer_item_list_wallet_money);
        profile_mobile= (TextView) itemView.findViewById(R.id.profile_mobile_number);
        imgIcon = (ImageView) itemView.findViewById(R.id.icon);
        profile_name = (TextView) itemView.findViewById(R.id.profile_name);
        profile_icon = (RoundedImageView) itemView.findViewById(R.id.profile_icon);
        general_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_normal_layout);
        profile_layout = (RelativeLayout) itemView.findViewById(R.id.drawer_list_item_profile_layout);
        drawer_view=(View)itemView.findViewById(R.id.drawer_list_view);


        if(position==0)
        {
            HashMap<String, String> user = session.getUserDetails();
            String UserprofileImage=user.get(SessionManager.KEY_USER_IMAGE);
            String User_phone=user.get(SessionManager.KEY_PHONENO);
            String User_phoneCode=user.get(SessionManager.KEY_COUNTRYCODE);
            String User_fullname=user.get(SessionManager.KEY_USERNAME);

            profile_layout.setVisibility(View.VISIBLE);
            general_layout.setVisibility(View.GONE);
            drawer_view.setVisibility(View.GONE);

          //  profile_icon.setImageResource(R.drawable.no_profile_image_avatar_icon);
         if(UserprofileImage!=null&&UserprofileImage.length()>0)
            Picasso.with(context).load(String.valueOf(UserprofileImage)).placeholder(R.drawable.no_profile_image_avatar_icon).into(profile_icon);
            profile_name.setText(User_fullname);
            profile_mobile.setText(User_phoneCode+" "+User_phone);
        }
        else
        {

            if(position==3)
            {
                drawer_view.setVisibility(View.VISIBLE);
                walletMoney.setVisibility(View.VISIBLE);
            }
            else
            {
                drawer_view.setVisibility(View.GONE);
                walletMoney.setVisibility(View.GONE);
            }

            profile_layout.setVisibility(View.GONE);
            general_layout.setVisibility(View.VISIBLE);

            HashMap<String, String> amount = session.getWalletAmount();
            String wallet_money=amount.get(SessionManager.KEY_WALLET_AMOUNT);

            imgIcon.setImageResource(mIcon[position]);
            txtTitle.setText(mTitle[position]);
            walletMoney.setText(wallet_money);
        }

        return itemView;
    }
}

