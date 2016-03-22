package com.harora.ceeride;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by harora on 3/16/16.
 */
class CeeridePlaceSelectionListener implements PlaceSelectionListener{

    private final String LOG_TAG = CeeridePlaceSelectionListener.class.getSimpleName();

    int index;
    CeeRideActivity activity;

    public CeeridePlaceSelectionListener(CeeRideActivity activity, int index){
        this.index = index;
        this.activity = activity;
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.d(LOG_TAG, "Place selected : " + place.getName());
        activity.onPlaceSelected(place, index);
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "Error : " + status.toString());
    }

}
