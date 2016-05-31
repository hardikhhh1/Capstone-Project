package com.harora.ceeride.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.CeerideReceiver;

import java.util.ArrayList;

/**
 * Created by harora on 3/14/16.
 */
public abstract class AbstractRideUtil extends AsyncTask<Void, Void, Object> implements RideUtil{

    Callback callback;

    AbstractRideUtil() {
    }

    public AbstractRideUtil(Activity activity) {
        if(!(activity instanceof Callback)){
            throw new RuntimeException("The activity should implement callback");
        }
    }

    @Override
    public ArrayList<RideDetail> getRideDetails() {
        throw new RuntimeException("Implement get ride details : " + getClass().getSimpleName());
    }


    public interface Callback{
        void onRideDetails(ArrayList<RideDetail> rideDetails, CeerideReceiver.RideType rideType);
    }
}
