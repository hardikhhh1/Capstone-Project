package com.harora.ceeride.model;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.harora.ceeride.CeeRideActivity;
import com.harora.ceeride.MainActivity;
import com.harora.ceeride.contextmenu.CeerideMenuItem;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

/**
 * Created by harora on 3/22/16.
 */
public class CeerideFavorite extends CeerideMenuItem {

    private CeeridePlace ceeridePlace;
    private String placeTag;

    // TODO: Add last vistited, and number of times visited.


    public CeeridePlace getCeeridePlace() {
        return ceeridePlace;
    }

    public CeerideFavorite(CeeridePlace ceeridePlace, String placeTag) {
        this.ceeridePlace = ceeridePlace;
        this.placeTag = placeTag;
    }

    public CeerideFavorite(MenuObject menuObject, CeeridePlace ceeridePlace, String placeTag) {
        this.setMenuObject(menuObject);
        this.ceeridePlace = ceeridePlace;
        this.placeTag = placeTag;
    }

    public void setCeeridePlace(CeeridePlace ceeridePlace) {
        this.ceeridePlace = ceeridePlace;
    }

    public String getPlaceTag() {
        return placeTag;
    }

    public void setPlaceTag(String placeTag) {
        this.placeTag = placeTag;
    }

    @Override
    public int hashCode() {
        return this.ceeridePlace.getPlaceName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public void onClick(Context context) {
        if(context instanceof CeeRideActivity){
            CeeRideActivity activity = (CeeRideActivity) context;
            activity.onPlaceSelected(this.ceeridePlace, 1);
        }
    }
}
