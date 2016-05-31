package com.harora.ceeride.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.harora.ceeride.R;
import com.harora.ceeride.model.CeeridePlace;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A placeholder fragment containing a simple view.
 */
public class MapsFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final String LOG_TAG = MapsFragment.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng[] latLngMarkers;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "The connection have been suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Error while connecting to google api.");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mGoogleApiClient.connect();
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Not all permission provided, cannot find the location. Returning.");
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        Location mLastLocation = getPresentLocation();
        markAndZoomMap(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), 0);

        if(getContext() instanceof MainMapActivity && mLastLocation != null){
            CeeridePlace presentLocation = getPresentPlace(mLastLocation);
            if(presentLocation != null){
                ((MainMapActivity) getContext()).onSourceChanged(presentLocation);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Getting google map to display");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(!mGoogleApiClient.isConnected()){
                    mGoogleApiClient.connect();
                }
                mMap = googleMap;
                mMap.setBuildingsEnabled(true);

                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        });

        return view;

    }

    public void markAndZoomMap(LatLng latLng, int index){
        if(latLngMarkers == null){
            latLngMarkers = new LatLng[2];
        }
        latLngMarkers[index] = latLng;
        this.mMap.addMarker(new MarkerOptions().position(latLng));


        if(latLngMarkers[0] != null && latLngMarkers[1] != null){
            // If only one location has been selected.
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng latLngMarker : latLngMarkers){
                builder.include(latLngMarker);
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate boundUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            CameraUpdate zoomUpdate = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 13);
            mMap.moveCamera(boundUpdate);
            mMap.animateCamera(zoomUpdate);
        } else{
            CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }

    @Nullable
    private Location getPresentLocation(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } else {
            // TODO: Show rationale and request permission.
        }
        return null;
    }

    @Nullable
    private CeeridePlace getPresentPlace(Location mLastLocation){
        Log.d(LOG_TAG, "Getting present location");

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(LOG_TAG, "On my location button has been clicked, getting and setting source address");
        if(getContext() instanceof MainMapActivity){
            Location mLastLocation = getPresentLocation();
            if(mLastLocation != null) {
                CeeridePlace presentLocation = getPresentPlace(mLastLocation);
                if (presentLocation != null) {
                    ((MainMapActivity) getContext()).onSourceChanged(presentLocation);
                }
            }
        }
        return true;
    }
}
