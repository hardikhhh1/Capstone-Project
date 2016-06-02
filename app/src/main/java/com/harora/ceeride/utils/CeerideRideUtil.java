package com.harora.ceeride.utils;

import android.Manifest;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.harora.ceeride.activity.MainMapActivity;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.CeerideRideService;
import com.harora.ceeride.widget.CeerideWidgetProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by harora on 3/14/16.
 */
public class CeerideRideUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

//    CeerideRideService.Callback callback;
    private Fragment fragment;

    private CeeridePlace pickUpLocation;
    private CeeridePlace dropOffLocation;

    private GoogleApiClient mGoogleApiClient;
    private  Context mContext;
    private AppWidgetProvider mWidgetProvider;
    private static final String LOG_TAG = CeerideRideUtil.class.getSimpleName();


    public CeerideRideUtil(Fragment fragment, CeeridePlace pickUpLocation,
                           CeeridePlace dropOffLocation){
        this.fragment = fragment;
        mContext = fragment.getActivity().getApplicationContext();

        this.pickUpLocation = pickUpLocation;
        this.dropOffLocation = dropOffLocation;

    }

    public CeerideRideUtil(AppWidgetProvider provider, Context context){
        this.mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mWidgetProvider = provider;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "The connection have been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Error while connecting to google api.");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = getPresentLocation();
        mGoogleApiClient.disconnect();
        CeeridePlace presentPlace = getPresentPlace(location);
        this.pickUpLocation = presentPlace;
        if(mWidgetProvider instanceof CeerideWidgetProvider){
            ((CeerideWidgetProvider) mWidgetProvider).onLocationRecieved(presentPlace);
        }
    }


    @Nullable
    private CeeridePlace getPresentPlace(Location mLastLocation){
        Log.d(LOG_TAG, "Getting present location");

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    // In this sample, get just a single address.
                    1);
            if (addresses.size() > 0) {
                String addressLine = addresses.get(0).getAddressLine(0);
                return new CeeridePlace(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(),
                        addressLine);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error when getting location information");
        }
        return null;
    }

    @Nullable
    private Location getPresentLocation(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } else {
            // TODO: Show rationale and request permission.
        }
        return null;
    }

    private void showExceptionMessage(String message){
        // TODO : Add a parameter close, and close only if true.
        fragment.getActivity().getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
        if(fragment.getActivity() instanceof MainMapActivity){
            fragment.getActivity().onBackPressed();
        }
        Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void getRideDetails(){
        Set<String> rides = CeeridePreferences.getRides(mContext);
        if(rides.size() == 0){
            showExceptionMessage("You don't have any rides services activated in preferences.");
            return;
        }

        if(rides.contains(CeeridePreferences.UBER_RIDE)){
            startRideDetailService(CeeridePreferences.UBER_RIDE.toLowerCase());
        }
        if(rides.contains(CeeridePreferences.LYFT_RIDE)){
            startRideDetailService(CeeridePreferences.LYFT_RIDE.toLowerCase());
        }
    }

    private void startRideDetailService(String rideServiceName){
        Intent intent = new Intent(mContext, CeerideRideService.class);

        if(dropOffLocation == null){
            dropOffLocation = pickUpLocation;
        }
        Bundle extras = CeerideRideService.getIntentBundle(this.pickUpLocation.getLatitude(),
                this.pickUpLocation.getLongitude(),
                this.dropOffLocation.getLatitude(), this.dropOffLocation.getLongitude(),
                rideServiceName);
        intent.putExtras(extras);
        mContext.startService(intent);
    }
}
