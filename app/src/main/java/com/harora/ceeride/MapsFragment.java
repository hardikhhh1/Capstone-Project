package com.harora.ceeride;

import android.app.Fragment;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.fitness.data.MapValue;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MapsFragment extends Fragment {

    public static final String LOG_TAG = MapsFragment.class.getSimpleName();

    MapView mapView;
    GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ArrayList<LatLng> latLngMarkers;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
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
                mMap = googleMap;
                mMap.setBuildingsEnabled(true);
//                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                setPresentLocationOnMap();
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


    private void setPresentLocationOnMap(){
        // Retrieve a list of location providers that have fine accuracy, no monetary cost, etc
        //and then you can make location update request with selected best provider
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 , 10, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        markAndZoomMap(lastKnownLocation);
    }

    public void markAndZoomMap(LatLng latLng){
        if(latLngMarkers == null){
            latLngMarkers = new ArrayList<>();
        }
        latLngMarkers.add(latLng);
        this.mMap.addMarker(new MarkerOptions().position(latLng));
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        if(latLngMarkers.size() > 1){
            LatLngBounds bounds = new LatLngBounds(latLngMarkers.get(0), latLngMarkers.get(1));
            CameraUpdate boundUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            mMap.moveCamera(boundUpdate);
        }

    }

    private void markAndZoomMap(Location location){
        if(mMap == null || location == null ) return;
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.d(LOG_TAG, "Got the location : Longitude : " + longitude + " Latitude : " + latitude);

//        LatLng latLng = new LatLng(longitude, latitude);
//        mMap.addMarker(new MarkerOptions().position(latLng)
//                .title("Marker"));
//        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
//        mMap.moveCamera(center);
//        mMap.animateCamera(zoom);

    }


}
