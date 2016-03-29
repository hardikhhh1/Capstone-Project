package com.harora.ceeride.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.harora.ceeride.model.CeeridePlace;


public  interface CeeRideMapActivity {

    public void onPlaceSelected(CeeridePlace place, int index);

    public void onSourceChanged(CeeridePlace place);

//    public void onDestinationChanged(CeeridePlace place);

}
