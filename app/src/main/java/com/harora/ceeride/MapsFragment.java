package com.harora.ceeride;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MapsFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String LOG_TAG = MapsFragment.class.getSimpleName();

    MapView mapView;
    GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LatLng[] latLngMarkers;

    private GoogleApiClient mGoogleApiClient;

    private Double longitude;
    private Double latitude;
    private String locationProvider = LocationManager.GPS_PROVIDER;
    private LocationManager locationManager ;


    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            markAndZoomMap(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Error while connecting to google api.");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mGoogleApiClient.connect();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.d(LOG_TAG, "Got the last location.");

            markAndZoomMap(new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()), 0);
        } else {
            // Show rationale and request permission.
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

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager =
                (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
    }


    public void markAndZoomMap(LatLng latLng, int index){
        if(latLngMarkers == null){
            latLngMarkers = new LatLng[2];
        }
        latLngMarkers[index] = latLng;
        this.mMap.addMarker(new MarkerOptions().position(latLng));


        if(latLngMarkers[0] != null && latLngMarkers[1] != null){
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
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }

    }

    private void markAndZoomMap(Location location){
        if(mMap == null || location == null ) return;
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.d(LOG_TAG, "Got the location : Longitude : " + longitude + " Latitude : " + latitude);

    }


}
