package com.harora.ceeride.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.utils.AbstractRideUtil;

import java.util.ArrayList;

/**
 * Created by harora on 5/4/16.
 */
public class CeerideReciever extends BroadcastReceiver {

    private Callback callback;

    public CeerideReciever(Fragment fragment) {
        if(fragment instanceof Callback){
            this.callback = (Callback) fragment;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<RideDetail> rideDetails = intent.getParcelableArrayListExtra("rideDetails");
        if(callback != null){
            callback.onRideDetails(rideDetails);
        }

    }

    public interface Callback{
         void onRideDetails(ArrayList<RideDetail> rideDetails);
    }

}
