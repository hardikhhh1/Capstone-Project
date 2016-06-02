package com.harora.ceeride.service;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.harora.ceeride.fragments.RideDetailFragment;
import com.harora.ceeride.model.LyftRideDetail;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.model.UberRideDetail;
import com.harora.ceeride.utils.CeeRideProperties;
import com.harora.ceeride.utils.CeeridePreferences;
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
public class CeerideRideService extends IntentService {

    public static final String START_LATITUDE_KEY = "startLatitude";
    public static final String START_LONGITUDE_KEY = "startLongitude";
    public static final String END_LATITUDE_KEY = "endLatitude";
    public static final String END_LONGITUDE_KEY = "endLongitude";
    public static final String RIDE_NAME_KEY = "rideName";
    public static final String RIDE_DETAILS_KEY = "rideDetails";

    private final String LOG_TAG = CeerideRideService.class.getSimpleName();

    private LyftClient.LyftService lyftService;
    private CostEstimates lyftPriceEstimates;
    private Prices uberPriceEstimates;
    private UberService uberService;
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mReceiver;

    public CeerideRideService() {
        super(CeerideRideService.class.getSimpleName());
    }

    public static Bundle getIntentBundle(double startLatitude, double startLongitude,
                                         double endLatitude, double endLongitude, String rideName) {
        Bundle bundle = new Bundle();
        bundle.putDouble(START_LATITUDE_KEY, startLatitude);
        bundle.putDouble(START_LONGITUDE_KEY, startLongitude);
        bundle.putDouble(END_LATITUDE_KEY, endLatitude);
        bundle.putDouble(END_LONGITUDE_KEY, endLongitude);
        bundle.putString(RIDE_NAME_KEY, rideName);
        return bundle;
    }

    public static Bundle getIntentBundle(String rideName) {
        Bundle bundle = new Bundle();
        bundle.putString(RIDE_NAME_KEY, rideName);
        return bundle;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
        }
        IntentFilter filter = new IntentFilter(RideDetailFragment.BROADCASTRECEIVER_MSG);
        mReceiver = new CeerideReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        super.onStart(intent, startId);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String rideName = intent.getStringExtra(RIDE_NAME_KEY);
        double startLatitude = intent.getDoubleExtra(START_LATITUDE_KEY, -1);
        double startLongitude = intent.getDoubleExtra(START_LONGITUDE_KEY, -1);
        double endLatitude = intent.getDoubleExtra(END_LATITUDE_KEY, -1);
        double endLongitude = intent.getDoubleExtra(END_LONGITUDE_KEY, -1);
        if (startLatitude == -1 || startLongitude == -1) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "THe required permissions have not been provided, cannot find the" +
                        "location");
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                Log.e(LOG_TAG, "Location is null.");
                return;
            }
            startLatitude = location.getLatitude();
            startLongitude = location.getLongitude();
        }
        if (endLatitude == -1 && endLongitude == -1) {
            endLatitude = startLatitude;
            endLongitude = startLongitude;
        }
        if(startLatitude == -1 || startLongitude == -1 || endLatitude == -1 ||
                endLongitude == -1 || rideName == null){
            throw new RuntimeException("Not all parameters passed");
        }
        ArrayList<RideDetail> rideDetails = new ArrayList<>();
        CeerideReceiver.RideType rideServiceName = null;
//        CeerideReceiver.RideType rideServiceName = CeerideReceiver.RideType.UBER;

        if (rideName.toLowerCase().equals(CeeridePreferences.LYFT_RIDE.toLowerCase())) {
            rideServiceName = CeerideReceiver.RideType.LYFT;
            lyftService = LyftClient.getLyftService(CeeRideProperties.LYFT_CLIENT_ID,
                    CeeRideProperties.LYFT_CLIENT_SECRET);
            try {
                Call<CostEstimates> estimates = lyftService.getCosts(startLatitude, startLongitude,
                        endLatitude, endLongitude);
                retrofit2.Response<CostEstimates> response = estimates.execute();
                lyftPriceEstimates = response.body();
                for (Cost ridePrice : lyftPriceEstimates.getCostEstimates()) {
                    String currencyCode = ridePrice.getCurrency();
                    String rideType = ridePrice.getRideType();
                    String displayName = ridePrice.getDisplayName();
                    String priceEstimate = ridePrice.getCurrency();
                    String highEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMax());
                    String lowEstimate = Integer.toString(ridePrice.getEstimatedCostCentsMin());
                    String surcharge = ridePrice.getPrimetimePercentage();
                    String timeEstimate = String.valueOf(ridePrice.getEstimatedDurationSeconds());
                    surcharge = surcharge.substring(0, surcharge.lastIndexOf("%"));
                    Float surchargeValue = Float.valueOf(surcharge);
                    LyftRideDetail rideDetail = new LyftRideDetail(null, displayName,
                            priceEstimate, lowEstimate, highEstimate, surchargeValue,
                            "", startLatitude, startLongitude, endLatitude, endLongitude);
                    rideDetails.add(rideDetail);
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error while getting lyft price estimates.");
            }
        } else if (rideName.toLowerCase().equals(CeeridePreferences.UBER_RIDE.toLowerCase())) {
            rideServiceName = CeerideReceiver.RideType.UBER;
            UberClient uberClient = new UberClient(CeeRideProperties.UBER_CLIENT_ID,
                    RestAdapter.LogLevel.BASIC);
            uberClient.setServerToken(CeeRideProperties.UBER_CLIENT_ID);
            this.uberService = uberClient.getApiService();
            uberPriceEstimates = uberService.getPriceEstimates(startLatitude, startLongitude,
                    endLatitude, endLongitude);
            if (uberPriceEstimates == null) return;

            for (Price ridePrice : uberPriceEstimates.getPrices()) {
                String rideId = ridePrice.getProductId();
                String currencyCode = ridePrice.getCurrencyCode();
                String displayName = ridePrice.getDisplayName();
                String timeEstimate = ridePrice.getEstimate();
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

//        rideDetails.add(new UberRideDetail(
//                "1", "rideName", "10", "10", "20", 1.00f, "$",
//                "12", 1.0, 2.0, 1.0, 2.0));

        Intent broadCastIntent = new Intent(RideDetailFragment.BROADCASTRECEIVER_MSG);
        broadCastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadCastIntent.putParcelableArrayListExtra(RIDE_DETAILS_KEY, rideDetails);
        broadCastIntent.putExtra(CeerideReceiver.RIDE_TYPE_KEY, rideServiceName);
        sendBroadcast(broadCastIntent);
    }

}
