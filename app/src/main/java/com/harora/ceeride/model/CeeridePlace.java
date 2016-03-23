package com.harora.ceeride.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;

/**
 * Created by harora on 3/22/16.
 */
public class CeeridePlace implements Parcelable{

    private double latitude;
    private double longitude;
    private String placeName;

    public CeeridePlace(double latitude, double longitude, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    public CeeridePlace(Place place){
        new CeeridePlace(place.getLatLng().latitude, place.getLatLng().longitude,
                place.getName().toString());
    }

    public CeeridePlace(Parcel input){
        new CeeridePlace(input.readDouble(), input.readDouble(), input.readString());
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(placeName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CeeridePlace createFromParcel(Parcel in) {
            return new CeeridePlace(in);
        }

        @Override
        public Object[] newArray(int i) {
            return new CeeridePlace[0];
        }
    };
}
