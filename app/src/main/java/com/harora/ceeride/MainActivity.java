package com.harora.ceeride;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.harora.ceeride.utils.MyRideDetailRecyclerFragment;
import com.harora.ceeride.utils.MyRideDetailRecyclerViewAdapter;
import com.harora.ceeride.utils.RideDetailFragment;
import com.harora.ceeride.utils.dummy.DummyContent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class MainActivity extends ActionBarActivity implements
    CeeRideActivity, RideDetailFragment.OnListFragmentInteractionListener{

    Place pickUpPlace;
    Place dropOffPlace;

    @Override
    public void onPlaceSelected(Place place, int index) {
        Log.d(LOG_TAG, "Place selected : " + place.getName());
        holder.mapsFragment.markAndZoomMap(place.getLatLng(), index);

        if (index == 0) {
            pickUpPlace = place;
        } else {
            dropOffPlace = place;
        }
    }

    public void showExceptionMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show();
    }

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    MainActivityHolder holder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapsFragment mapsFragment = new MapsFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.layout_map_container, mapsFragment, LOG_TAG)
                .commit();

        holder = new MainActivityHolder(this);
        holder.pickUpLocation.setOnPlaceSelectedListener(new CeeridePlaceSelectionListener(this, 0));
        holder.destinationLocation.setOnPlaceSelectedListener(new CeeridePlaceSelectionListener(this, 1));

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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(LOG_TAG, "Interaction with the content.");
    }

    class MainActivityHolder{

        PlaceAutocompleteFragment pickUpLocation;
        PlaceAutocompleteFragment destinationLocation;
        MapsFragment mapsFragment;
        @Bind(R.id.find_rides_button) Button mFindRidesButton;

        AutocompleteFilter typeFilter;

        public MainActivityHolder(Activity activity){
            pickUpLocation = getFragment(R.id.pickup_fragment);
            destinationLocation = getFragment( R.id.destination_fragment);
            typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            mapsFragment = (MapsFragment) getFragmentManager().findFragmentByTag(LOG_TAG);
            ButterKnife.bind(this, activity);

        }

        @OnClick(R.id.find_rides_button)
        public void onFindRidesButton(){
            Log.d(LOG_TAG, "On find rides button clicked.");

//            if(pickUpPlace == null){
//                showExceptionMessage("Please select pick up location");
//                return;
//            } else if(dropOffPlace == null){
//                showExceptionMessage("Please select drop off location");
//                return;
//            }

            // We have both pick up and drop off location.
            // We can initiate another fragment.
            RideDetailFragment rideDetailFragment =
                    new RideDetailFragment();


            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_map_container, rideDetailFragment, "detailsFragment")
                    .commit();

        }


        private PlaceAutocompleteFragment getFragment(int id){
            PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)getFragmentManager()
                    .findFragmentById(id);
            fragment.setFilter(typeFilter);
            return fragment;
        }
    }

}
