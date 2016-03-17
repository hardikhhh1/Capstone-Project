package com.harora.ceeride.utils;

import com.victorsima.uber.UberClient;
import com.victorsima.uber.UberService;
import com.victorsima.uber.model.Times;

import android.support.v7.appcompat.R;

import java.sql.Time;

import retrofit.RestAdapter;

/**
 * Created by harora on 3/14/16.
 */
public class UberRide extends AbstractRide {


    private UberClient uberClient = new UberClient(CeeRideProperties.UBER_CLIENT_ID,
            RestAdapter.LogLevel.BASIC);
    
    @Override
    public void getMinDuration(double startLatitude, double startLongitude,
                               double endLatitude, double endLongitude) {
        UberService service = uberClient.getApiService();
//        Times times = service.getTimeEstimates(startLongitude, startLongitude,
//                endLatitude, endLongitude);
        // TODO : Parse times, return a list.
    }

    @Override
    public void getSharedDuration(double startLatitude, double startLongitude,
                                  double endLatitude, double endLongitude) {
        UberService service = uberClient.getApiService();
//        Times times = service.getTimeEstimates(startLongitude, endLatitude);
        // TODO : Parse times, return a list.
    }

    @Override
    public void getSingleCost(double startLatitude, double startLongitude,
                              double endLatitude, double endLongitude) {
        UberService service = uberClient.getApiService();
//        service.getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    @Override
    public void getSharedCost(double startLatitude, double startLongitude,
                              double endLatitude, double endLongitude) {
        UberService service = uberClient.getApiService();
//        service.getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude);

    }

    @Override
    public void isSurcharge(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        super.isSurcharge(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    @Override
    public void getSurchargeRemoveDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        super.getSurchargeRemoveDistance(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    @Override
    public void getSurcharge(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        super.getSurcharge(startLatitude, startLongitude, endLatitude, endLongitude);
    }
}
