package com.harora.ceeride.utils;

import android.support.v4.app.Fragment;

import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.model.UberRideDetail;
import com.victorsima.uber.UberClient;
import com.victorsima.uber.UberService;
import com.victorsima.uber.model.Price;
import com.victorsima.uber.model.Prices;
import com.victorsima.uber.model.Times;
import java.util.ArrayList;

import retrofit.RestAdapter;

/**
 * Created by harora on 3/14/16.
 */
public class UberRideUtil extends AbstractRideUtil {


    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private UberService uberService;
    private Prices priceEstimates;
    private Times timeEstimates;
    private Callback callback;


    public UberRideUtil(double startLatitude, double startLongitude, double endLatitude,
                        double endLongitude, Fragment fragment){
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        UberClient uberClient = new UberClient(CeeRideProperties.UBER_CLIENT_ID,
                RestAdapter.LogLevel.BASIC);
        uberClient.setServerToken(CeeRideProperties.UBER_CLIENT_ID);
        this.uberService = uberClient.getApiService();
        attach(fragment);
    }

    private void attach(Fragment fragment){
        if(fragment instanceof Callback){
            callback = (Callback) fragment;
        } else{
            throw new RuntimeException("Implement callback for uberrideutil");
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {
        priceEstimates = uberService.getPriceEstimates(this.startLatitude, this.startLongitude,
                this.endLatitude, this.endLongitude);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (priceEstimates == null) return ;

        ArrayList<RideDetail> rideDetails = new ArrayList<>();

        for(Price ridePrice : priceEstimates.getPrices()){
            String currencyCode = ridePrice.getCurrencyCode();
            String displayName = ridePrice.getDisplayName();
            String priceEstimate = ridePrice.getEstimate();
            String highEstimate = Integer.toString(ridePrice.getHighEstimate());
            String lowEstimate = Integer.toString(ridePrice.getLowEstimate());
            String surchargeValue = Float.toString(ridePrice.getSurgeMultiplier());
            UberRideDetail rideDetail = new UberRideDetail(0, displayName,
                    priceEstimate, lowEstimate, highEstimate, surchargeValue,
                    "ts");
            rideDetails.add(rideDetail);
        }
        callback.onRideDetails(rideDetails);
    }

}
