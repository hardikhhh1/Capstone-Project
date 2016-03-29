package com.harora.ceeride.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.harora.ceeride.CeeridePlaceSelectionListener;
import com.harora.ceeride.R;
import com.harora.ceeride.contextmenu.ContextMenu;
import com.harora.ceeride.exceptions.CeerideException;
import com.harora.ceeride.exceptions.LocationEmptyException;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.utils.CeeridePreferenceFragment;
import com.harora.ceeride.utils.RideDetailFragment;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainMapActivity extends AppCompatActivity implements
        CeeRideMapActivity, RideDetailFragment.OnListFragmentInteractionListener,
        OnMenuItemClickListener{


    private CeeridePlace pickUpPlace;
    private CeeridePlace dropOffPlace;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private ContextMenu mContextMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment = new MapsFragment();
        mContextMenu = new ContextMenu(this, true, true);
        initToolbar();
        initMenuFragment();
        fragmentManager.beginTransaction()
                .add(R.id.layout_map_container, mapsFragment, LOG_TAG)
                .addToBackStack(MapsFragment.class.getName())
                .commit();
    }

    public CeeridePlace getPickUpPlace() {
        return pickUpPlace;
    }

    public void setPickUpPlace(CeeridePlace pickUpPlace) {
        this.pickUpPlace = pickUpPlace;
    }

    public CeeridePlace getDropOffPlace() {
        return dropOffPlace;
    }

    public void setDropOffPlace(CeeridePlace dropOffPlace) {
        this.dropOffPlace = dropOffPlace;
    }




    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;

            case R.id.settings:
                CeeridePreferenceFragment fragment = new CeeridePreferenceFragment();
                fragmentManager.beginTransaction()
                        .add(android.R.id.content,fragment, "settings")
                        .addToBackStack("settings")
                        .commit();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceSelected(CeeridePlace place, int index) {
        Log.d(LOG_TAG, "Place selected : " + place.getPlaceName());

        holder.mapsFragment.markAndZoomMap(new LatLng(place.getLatitude(), place.getLongitude()),
                index);

        if (index == 0) {
            onSourceChanged(place);
        } else {
            // TODO : Implement onDestination changed.
            dropOffPlace = place;
            holder.destinationLocation.setText(place.getPlaceName());
        }

        if(pickUpPlace != null && !pickUpPlace.getPlaceName().isEmpty()
                && dropOffPlace != null && !dropOffPlace.getPlaceName().isEmpty()){
            holder.mFindRidesButton.setVisibility(View.VISIBLE);
        } else{
            holder.mFindRidesButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSourceChanged(CeeridePlace place) {
        pickUpPlace = place;
        holder.pickUpLocation.setText(place.getPlaceName());
    }

    private final String LOG_TAG = MainMapActivity.class.getSimpleName();

    MainActivityHolder holder;

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(mContextMenu.getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
    }


    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 0 ){
                fragmentManager.popBackStack();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        mContextMenu.getMenuObject(position).onClick(this);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        holder = new MainActivityHolder(this);
        holder.pickUpLocation.setOnPlaceSelectedListener(new CeeridePlaceSelectionListener(this, 0));
        holder.destinationLocation.setOnPlaceSelectedListener(new CeeridePlaceSelectionListener(this, 1));
    }



    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }




    @Override
    public void onListFragmentInteraction(RideDetail rideDetail) {
        Log.d(LOG_TAG, "Interaction with the content: Ride clicked : " + rideDetail.getRideName());

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Do you want to call ")
                .append(rideDetail.getRideName())
                .append("?")
                .append("\n")
                .append("It will cost between " + rideDetail.getLowRideCost() + " - " +
                        rideDetail.getHighRideCost());

        new AlertDialog.Builder(this)
                .setTitle("Ceeride Confirmation")
                .setMessage(messageBuilder.toString())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MainMapActivity.this,
                                "Calling your ride.",
                                Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

        // TODO : Open the appropriate application with the ride details.
    }

    class MainActivityHolder{

        PlaceAutocompleteFragment pickUpLocation;
        PlaceAutocompleteFragment destinationLocation;
        MapsFragment mapsFragment;
        Activity mActivity;
        Bundle mBundle;

        @Bind(R.id.find_rides_button) Button mFindRidesButton;

        AutocompleteFilter typeFilter;

        public MainActivityHolder(Activity activity){
            pickUpLocation = getFragment(R.id.pickup_fragment);
            destinationLocation = getFragment( R.id.destination_fragment);
            typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(LOG_TAG);
            mActivity  = activity;
            ButterKnife.bind(this, activity);

            mFindRidesButton.setVisibility(View.INVISIBLE);
        }


        @OnClick(R.id.find_rides_button)
        public void onFindRidesButton(){
            Log.d(LOG_TAG, "On find rides button clicked.");

            holder.mFindRidesButton.setVisibility(View.INVISIBLE);
            try {
                setDetailsBundle();
                openDetailsFragment();
            } catch (CeerideException e){
                Log.e(LOG_TAG, "Error occured : " + e.getMessage());
            }
        }

        private void setDetailsBundle() throws LocationEmptyException{
            if(pickUpPlace == null){
                throw new LocationEmptyException(mActivity,
                        "Pick up location is null",
                        "Please fill the pick up address.");
            } else if(dropOffPlace == null){
                throw new LocationEmptyException(mActivity,
                        "Destination location is null",
                        "Please fill the destination address.");
            }

            mBundle = new Bundle();

            mBundle.putParcelable(RideDetailFragment.PICK_UP_LOCATION,
                    pickUpPlace);
            mBundle.putParcelable(RideDetailFragment.DROP_OFF_LOCATION,
                    dropOffPlace);
        }

        private void openDetailsFragment(){
            RideDetailFragment rideDetailFragment =
                    new RideDetailFragment();
            rideDetailFragment.setArguments(mBundle);
            Fragment previousFragment = fragmentManager.findFragmentByTag("detailsFragment");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(previousFragment != null){
                transaction.remove(previousFragment);
            }
            transaction.add(R.id.layout_map_container, rideDetailFragment, "detailsFragment")
                    .addToBackStack("detailsFragment")
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
