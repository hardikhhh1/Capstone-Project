package com.harora.ceeride.utils;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.harora.ceeride.model.LyftRideDetail;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.Cost;
import com.harora.ceeride.service.CostEstimates;
import com.harora.ceeride.service.LyftClient;
import com.victorsima.uber.model.Times;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by harora on 3/14/16.
 */
public class LyftRideUtil extends AbstractRideUtil {

    private final String LOG_TAG = LyftRideUtil.class.getSimpleName();

    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private LyftClient.LyftService lyftService;
    private CostEstimates priceEstimates;
    private Times timeEstimates;
    private Callback callback;


    public LyftRideUtil(double startLatitude, double startLongitude, double endLatitude,
                        double endLongitude, Fragment fragment){
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;

        lyftService = LyftClient.getLyftService("m5ASmQsgURu7","ivJbJ9DSvRQ2X8kDKIZdazUbNW1Pd0e7");
        attach(fragment);
    }

    private void attach(Fragment fragment){
        if(fragment instanceof Callback){
            callback = (Callback) fragment;
        } else{
            throw new RuntimeException("Implement callback for LYFT RIDE UTIL");
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {

        try {
            Call<CostEstimates> estimates = lyftService.getCosts(this.startLatitude, this.startLongitude,
                    this.endLatitude, this.endLongitude);
            retrofit2.Response<CostEstimates> response = estimates.execute();
            priceEstimates = response.body();
        } catch (IOException e){
            Log.d(LOG_TAG, "Error while getting lyft price estimates.");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (priceEstimates == null) return ;

        ArrayList<RideDetail> rideDetails = new ArrayList<>();

        for(Cost ridePrice : priceEstimates.getCostEstimates()){
//            String currencyCode = ridePrice.getCurrency();
            String rideType = ridePrice.getRideType();
            String displayName = ridePrice.getDisplayName();
            String priceEstimate = ridePrice.getCurrency();
            String highEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMax());
            String lowEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMin());
            String surcharge = ridePrice.getPrimetimePercentage();
            surcharge = surcharge.substring(0, surcharge.lastIndexOf("%"));
            Float surchargeValue = Float.valueOf(surcharge);
//            LyftRideDetail rideDetail = new LyftRideDetail(rideType, displayName,
//                    priceEstimate, lowEstimate, highEstimate, surchargeValue,
//                    "");
//            rideDetails.add(rideDetail);
        }
        callback.onRideDetails(rideDetails);
    }

}
