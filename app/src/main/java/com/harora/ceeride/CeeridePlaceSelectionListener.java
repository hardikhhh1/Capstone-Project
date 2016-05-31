package com.harora.ceeride;

import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.harora.ceeride.activity.CeeRideMapActivity;
import com.harora.ceeride.model.CeeridePlace;

/**
 * Created by harora on 3/16/16.
 */
public class CeeridePlaceSelectionListener implements PlaceSelectionListener{

    private final String LOG_TAG = CeeridePlaceSelectionListener.class.getSimpleName();

    private int index;
    private CeeRideMapActivity activity;

    public CeeridePlaceSelectionListener(CeeRideMapActivity activity, int index){
        this.index = index;
        this.activity = activity;
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.d(LOG_TAG, "Place selected : " + place.getName());
        CeeridePlace ceeridePlace = new CeeridePlace(place);
        activity.onPlaceSelected(ceeridePlace, index);
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "Error : " + status.toString());
    }

}
