package com.harora.ceeride.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.harora.ceeride.exceptions.CeerideException;
import com.harora.ceeride.exceptions.LocationEmptyException;
import com.harora.ceeride.fragments.CeeridePreferenceFragment;
import com.harora.ceeride.fragments.RideDetailFragment;
import com.harora.ceeride.menu.ContextMenu;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainMapActivity extends AppCompatActivity implements
        CeeRideMapActivity, RideDetailFragment.OnListFragmentInteractionListener,
        OnMenuItemClickListener{

    public static final String PICK_UP_PLACE_KEY = "pickUpPlace";
    public static final String DROP_OFF_PLACE_KEY = "dropOffPlace";
    public static final String SETTINGS_FRAGMENT_TAG = "settings";
    private final int CEERIDE_PROPERTIES_RESULT = 1;
    private final String LOG_TAG = MainMapActivity.class.getSimpleName();
    private CeeridePlace pickUpPlace;
    private CeeridePlace dropOffPlace;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private ContextMenu mContextMenu;
    private RideDetailFragment rideDetailFragment;
    private MainActivityHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment = new MapsFragment();
        mContextMenu = new ContextMenu(this, true, true);
        initToolbar();
        initMenuFragment();
        askForPermission();
        rideDetailFragment = (RideDetailFragment)
                fragmentManager.findFragmentByTag(MainActivityHolder.DETAILS_FRAGMENT_TAG);
        if (rideDetailFragment == null) {

            fragmentManager.beginTransaction()
                    .add(R.id.layout_map_container, mapsFragment, LOG_TAG)
                    .addToBackStack(MapsFragment.class.getName())
                    .commit();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PICK_UP_PLACE_KEY, pickUpPlace);
        outState.putParcelable(DROP_OFF_PLACE_KEY, dropOffPlace);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pickUpPlace = savedInstanceState.getParcelable(PICK_UP_PLACE_KEY);
        dropOffPlace = savedInstanceState.getParcelable(DROP_OFF_PLACE_KEY);

        if (holder == null) {
            holder = new MainActivityHolder(this);
        }
        if (pickUpPlace != null && !pickUpPlace.getPlaceName().isEmpty()
                && dropOffPlace != null && !dropOffPlace.getPlaceName().isEmpty()) {
            holder.mFindRidesButton.setVisibility(View.VISIBLE);
        } else {
            holder.mFindRidesButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Helper method to ask for required permissions.
     */
    private void askForPermission() {

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
        };

        List<String> permissionsToBeAsked = new ArrayList<>();

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToBeAsked.add(permission);
            }
        }

        if (permissionsToBeAsked.size() > 0) {
            String[] permissionsTobeAskedArray = permissionsToBeAsked.toArray(new
                    String[permissionsToBeAsked.size()]);
            ActivityCompat.requestPermissions(this,
                    permissionsTobeAskedArray,
                    CEERIDE_PROPERTIES_RESULT);
        }

        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CEERIDE_PROPERTIES_RESULT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Permissions were granted");
                } else {
                    Log.d(LOG_TAG, "Permissions were denied");
                    // TODO : Throw not all permissions allowed, cannot proceed forward.
                }
                return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public CeeridePlace getDropOffPlace() {
        return dropOffPlace;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "Options item has been selected.");
        switch (item.getItemId()) {
            case R.id.context_menu:
                Log.d(LOG_TAG, "Context menu has been clicked");
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    // Show the context menu if the fragment is not present.
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;

            case R.id.settings:
                Log.d(LOG_TAG, "Settings menu has been opened.");
                CeeridePreferenceFragment fragment = new CeeridePreferenceFragment();
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, fragment, SETTINGS_FRAGMENT_TAG)
                        .addToBackStack(SETTINGS_FRAGMENT_TAG)
                        .commit();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceSelected(CeeridePlace place, int index) {
        Log.d(LOG_TAG, "Place selected : " + place.getPlaceName());

        if (place == null) {
            Log.e(LOG_TAG, "The place passed was null");
            return;
        }

        holder.mapsFragment.markAndZoomMap(new LatLng(place.getLatitude(), place.getLongitude()),
                index);

        if (index == 0) {
            onSourceChanged(place);
        } else {
            onDestinationChanged(place);
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
        Log.d(LOG_TAG, "Source location has been changed");
        pickUpPlace = place;
        holder.setPickUpLocationText();
    }

    @Override
    public void onDestinationChanged(CeeridePlace place) {
        Log.d(LOG_TAG, "Destination location has been changed");
        dropOffPlace = place;
        holder.setDestinationLocationText();
    }

    /**
     * Helper method to initialize the menu on the top right, which
     * has options to close and add to favourites.
     */
    public void initMenuFragment() {
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
            if (count > 1) {
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


    /**
     * Helper function to initialize the toolbar.
     */
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
    public void onListFragmentInteraction(final RideDetail rideDetail) {
        Log.d(LOG_TAG, "Interaction with the content: Ride clicked : " + rideDetail.getRideName());

        String messageBuilder = "Do you want to call " +
                rideDetail.getRideName() +
                "?" +
                "\n" +
                "It will cost between " +
                rideDetail.getLowRideCost() +
                " - " +
                rideDetail.getHighRideCost();


        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmation_title)
                .setMessage(messageBuilder)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Toast.makeText(MainMapActivity.this,
                                R.string.confirmation_toast_text,
                                Toast.LENGTH_SHORT).show();
                        try {
                            rideDetail.openApp(MainMapActivity.this,
                                    pickUpPlace.getPlaceName(),
                                    dropOffPlace.getPlaceName());
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    /**
     * Holder pattern for the main activity.
     */
    class MainActivityHolder{

        static final String NULL_PICKUP_LOCATION_ERROR_MSG = "Pick up location is null";
        static final String NULL_DESTINATION_LOCATION_MSG = "Destination location is null";
        static final String DETAILS_FRAGMENT_TAG = "detailsFragment";

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
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                    .build();
            pickUpLocation.setFilter(typeFilter);
            destinationLocation.setFilter(typeFilter);
            mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(LOG_TAG);
            mActivity  = activity;
            ButterKnife.bind(this, activity);

            // In the start the button should be invisible.
            mFindRidesButton.setVisibility(View.INVISIBLE);
        }

        public void setPickUpLocationText(){
            pickUpLocation.setText(pickUpPlace.getPlaceName());
            if(pickUpPlace != null && dropOffPlace != null){
                mFindRidesButton.setVisibility(View.VISIBLE);
            }
        }

        public void setDestinationLocationText(){
            destinationLocation.setText(dropOffPlace.getPlaceName());
            if(pickUpPlace != null && dropOffPlace != null){
                mFindRidesButton.setVisibility(View.VISIBLE);
            }
        }


        @OnClick(R.id.find_rides_button)
        public void onFindRidesButton(){
            Log.d(LOG_TAG, "On find rides button clicked.");

            holder.mFindRidesButton.setVisibility(View.INVISIBLE);
            try {
                setDetailsBundle();
                openDetailsFragment();
            } catch (CeerideException e){
                Log.e(LOG_TAG, "Error occurred : " + e.getMessage());
            }
        }

        private void setDetailsBundle() throws LocationEmptyException{
            if(pickUpPlace == null){
                throw new LocationEmptyException(mActivity,
                        NULL_PICKUP_LOCATION_ERROR_MSG,
                        getString(R.string.null_pick_up_location_text));
            } else if(dropOffPlace == null){
                throw new LocationEmptyException(mActivity,
                        NULL_DESTINATION_LOCATION_MSG,
                        getString(R.string.null_drop_off_location_text));
            }

            mBundle = new Bundle();

            mBundle.putParcelable(RideDetailFragment.PICK_UP_LOCATION,
                    pickUpPlace);
            mBundle.putParcelable(RideDetailFragment.DROP_OFF_LOCATION,
                    dropOffPlace);
        }

        private void openDetailsFragment(){
//            UberRideDetail.tempOpenApp(MainMapActivity.this);
//            return;
            rideDetailFragment =
                    new RideDetailFragment();
            rideDetailFragment.setArguments(mBundle);
            Fragment previousFragment = fragmentManager.findFragmentByTag(DETAILS_FRAGMENT_TAG);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(previousFragment != null){
                transaction.remove(previousFragment);
            }
            transaction.add(R.id.layout_map_container, rideDetailFragment, DETAILS_FRAGMENT_TAG)
                    .addToBackStack(DETAILS_FRAGMENT_TAG)
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
