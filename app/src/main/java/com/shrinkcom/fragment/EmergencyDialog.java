package com.shrinkcom.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.GsonBuilder;
import com.recyclar.ClickListener;
import com.shrinkcom.adapter.EmergencyAdapter;
import com.shrinkcom.passenger.R;
import com.shrinkcom.permissionhelper.PermissionHelper;
import com.shrinkcom.pojo.EmergencyPojoNew;

import com.shrinkcom.utils.SessionManager;

import java.util.Arrays;
import java.util.List;

@SuppressLint("ValidFragment")
public class EmergencyDialog extends DialogFragment {
    String TAG = EmergencyDialog.class.getSimpleName();
    List<EmergencyPojoNew> emergency_number;
    private RecyclerView mRecyclerView;
    private EmergencyAdapter emerrgency_adapter;


    public EmergencyDialog(Context context) {
        emergency_number = Arrays.asList(new GsonBuilder().create().fromJson(new SessionManager(context).getEmergencyNumber(), EmergencyPojoNew[].class));
    }

    // this method create view for your Dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.recycle_dialog, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        this.getDialog().setCanceledOnTouchOutside(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emerrgency_adapter = new EmergencyAdapter(getActivity(), new ClickListener() {
            @Override
            public void onPositionClicked(int position, View view) {
                Log.e(TAG, "onPositionClicked: ");
                final EmergencyPojoNew planStation = emergency_number.get(position);
/*
                PermissionHelper.requestPhone(new PermissionHelper.OnPermissionGrantedListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + planStation.getNumber()));
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        getActivity().startActivity(intent);
                    }
                });
*/
            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        mRecyclerView.setAdapter(emerrgency_adapter);
        emerrgency_adapter.UpdateList(emergency_number);
        //get your recycler view and populate it.
        return v;
    }
}