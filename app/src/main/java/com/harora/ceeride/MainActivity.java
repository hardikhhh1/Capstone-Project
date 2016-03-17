package com.harora.ceeride;

import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity implements PlaceSelectionListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    MainActivityHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holder = new MainActivityHolder();
        holder.destinationLocation.setOnPlaceSelectedListener(this);
        holder.pickUpLocation.setOnPlaceSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPlaceSelected(Place place) {
        Log.d(LOG_TAG, "Place selected : " + place.getName());
        holder.mapsFragment.markAndZoomMap(place.getLatLng());
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "Error : " + status.toString());
    }

    class MainActivityHolder{

        PlaceAutocompleteFragment pickUpLocation;
        PlaceAutocompleteFragment destinationLocation;
        MapsFragment mapsFragment;

        AutocompleteFilter typeFilter;

        public MainActivityHolder(){
            pickUpLocation = getFragment(R.id.pickup_fragment);
            destinationLocation = getFragment( R.id.destination_fragment);
            typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            mapsFragment = (MapsFragment) getFragmentManager().findFragmentById(R.id.layout_map);
        }

        private PlaceAutocompleteFragment getFragment(int id){
            PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)getFragmentManager()
                    .findFragmentById(id);
            fragment.setFilter(typeFilter);
            return fragment;
        }
    }

}
