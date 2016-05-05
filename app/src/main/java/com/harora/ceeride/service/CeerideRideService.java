package com.harora.ceeride.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.harora.ceeride.model.LyftRideDetail;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.model.UberRideDetail;
import com.harora.ceeride.utils.AbstractRideUtil;
import com.harora.ceeride.utils.CeeRideProperties;
import com.harora.ceeride.utils.RideDetailFragment;
import com.harora.ceeride.utils.RideUtil;
import com.victorsima.uber.UberClient;
import com.victorsima.uber.UberService;
import com.victorsima.uber.model.Price;
import com.victorsima.uber.model.Prices;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit2.Call;

/**
 * Created by harora on 5/4/16.
 */
public class CeerideRideService extends IntentService{

    private final String LOG_TAG = CeerideRideService.class.getSimpleName();

    private LyftClient.LyftService lyftService;
    private CostEstimates lyftPriceEstimates;
    private Prices uberPriceEstimates;
    private UberService uberService;


    public CeerideRideService(){
        super(CeerideRideService.class.getSimpleName());
    }

    public static Bundle getIntentBundle(double startLatitude, double startLongitude,
                                  double endLatitude, double endLongitude, String rideName){
        Bundle bundle = new Bundle();
        bundle.putDouble("startLatitude", startLatitude);
        bundle.putDouble("startLongitude", startLongitude);
        bundle.putDouble("endLatitude", endLatitude);
        bundle.putDouble("endLongitude", endLongitude);
        bundle.putString("rideName", rideName);
        return bundle;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String rideName = intent.getStringExtra("rideName");
        double startLatitude = intent.getDoubleExtra("startLatitude", -1);
        double startLongitude = intent.getDoubleExtra("startLongitude", -1);
        double endLatitude = intent.getDoubleExtra("endLatitude", -1);
        double endLongitude = intent.getDoubleExtra("endLongitude", -1);
        if(startLatitude == -1 || startLongitude == -1 || endLatitude == -1 ||
                endLongitude == -1 || rideName == null){
            throw new RuntimeException("Not all parameters passed");
        }
        ArrayList<RideDetail> rideDetails = new ArrayList<>();
        if(rideName.toLowerCase().equals("lyft")){
            lyftService = LyftClient.getLyftService("m5ASmQsgURu7","ivJbJ9DSvRQ2X8kDKIZdazUbNW1Pd0e7");
            try {
                Call<CostEstimates> estimates = lyftService.getCosts(startLatitude, startLongitude,
                        endLatitude, endLongitude);
                retrofit2.Response<CostEstimates> response = estimates.execute();
                lyftPriceEstimates = response.body();
                for(Cost ridePrice : lyftPriceEstimates.getCostEstimates()){
//            String currencyCode = ridePrice.getCurrency();
                    String rideType = ridePrice.getRideType();
                    String displayName = ridePrice.getDisplayName();
                    String priceEstimate = ridePrice.getCurrency();
                    String highEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMax());
                    String lowEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMin());
                    String surcharge = ridePrice.getPrimetimePercentage();
                    surcharge = surcharge.substring(0, surcharge.lastIndexOf("%"));
                    Float surchargeValue = Float.valueOf(surcharge);
                    LyftRideDetail rideDetail = new LyftRideDetail(rideType, displayName,
                            priceEstimate, lowEstimate, highEstimate, surchargeValue,
                            "", startLatitude, startLongitude, endLatitude, endLongitude);
                    rideDetails.add(rideDetail);
                }
            } catch (IOException e){
                Log.d(LOG_TAG, "Error while getting lyft price estimates.");
            }
        }else if(rideName.toLowerCase().equals("uber")){
            UberClient uberClient = new UberClient(CeeRideProperties.UBER_CLIENT_ID,
                    RestAdapter.LogLevel.BASIC);
            uberClient.setServerToken(CeeRideProperties.UBER_CLIENT_ID);
            this.uberService = uberClient.getApiService();
            uberPriceEstimates = uberService.getPriceEstimates(startLatitude, startLongitude,
                    endLatitude, endLongitude);
            if (uberPriceEstimates == null) return ;

            for(Price ridePrice : uberPriceEstimates.getPrices()){
                String rideId = ridePrice.getProductId();
                String currencyCode = ridePrice.getCurrencyCode();
                String displayName = ridePrice.getDisplayName();
                String priceEstimate = ridePrice.getEstimate();
                String highEstimate = Integer.toString(ridePrice.getHighEstimate());
                String lowEstimate = Integer.toString(ridePrice.getLowEstimate());
                Float surchargeValue = ridePrice.getSurgeMultiplier();
                UberRideDetail rideDetail = new UberRideDetail(rideId, displayName,
                        priceEstimate, lowEstimate, highEstimate, surchargeValue,
                        currencyCode,
                        "", startLatitude, startLongitude, endLatitude, endLongitude);
                rideDetails.add(rideDetail);
            }
        }

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(RideDetailFragment.BROADCASTRECIEVER_MSG);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putParcelableArrayListExtra("rideDetails", rideDetails);
        sendBroadcast(broadCastIntent);
    }

}
