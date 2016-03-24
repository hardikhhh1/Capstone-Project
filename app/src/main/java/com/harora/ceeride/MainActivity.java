package com.harora.ceeride;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.utils.RideDetailFragment;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements
        CeeRideActivity, RideDetailFragment.OnListFragmentInteractionListener,
        OnMenuItemClickListener{

    Place pickUpPlace;
    Place dropOffPlace;
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private ContextMenu mContextMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        MapsFragment mapsFragment = new MapsFragment();
        mContextMenu = new ContextMenu(true, true);


        initToolbar();
        initMenuFragment();


        fragmentManager.beginTransaction()
                .add(R.id.layout_map_container, mapsFragment, LOG_TAG)
                .commit();
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
        }
        return super.onOptionsItemSelected(item);
    }

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
            finish();
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if(mContextMenu.getMenuObject(position)
                .getTitle().equals(ContextMenu.ADD_FAVORITES)){
            Toast.makeText(this, "Add favourites was clicked", Toast.LENGTH_SHORT).show();
        }
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
                        Toast.makeText(MainActivity.this,
                                "Calling your ride.",
                                Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

        // TODO : Open the appropriate application with
        // the ride details.
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
            mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(LOG_TAG);
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
            Bundle bundle = new Bundle();

            // TODO : Remove this , just for testing easily
            CeeridePlace tempPickUpPlace = new CeeridePlace(42.345183500000005,
                    -71.08505099999999, "48 Clearway St");
            CeeridePlace tempDropOffPlace = new CeeridePlace(42.3713157,
                    -71.0965647, "185 Elm St");
            bundle.putParcelable(RideDetailFragment.PICK_UP_LOCATION,
                    tempPickUpPlace);
            bundle.putParcelable(RideDetailFragment.DROP_OFF_LOCATION,
                    tempDropOffPlace);

            rideDetailFragment.setArguments(bundle);
            Fragment previousFragment = fragmentManager.findFragmentByTag("detailsFragment");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(previousFragment != null){
                transaction.remove(previousFragment);
            }
            transaction.add(R.id.layout_map_container, rideDetailFragment, "detailsFragment")
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
