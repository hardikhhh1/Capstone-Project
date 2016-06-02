package com.harora.ceeride.model;

import android.content.Context;

import com.harora.ceeride.activity.CeeRideMapActivity;
import com.harora.ceeride.db.CeerideFavoriteDbUtils;
import com.harora.ceeride.menu.CeerideMenuItem;
import com.yalantis.contextmenu.lib.MenuObject;

/**
 * Created by harora on 3/22/16.
 * Class representing the favourite object/item/address
 * shown in the context menu.
 */
public class CeerideFavorite extends CeerideMenuItem {

    private Integer id;
    private CeeridePlace ceeridePlace;
    private String placeTag;
    private Integer counter;

    // TODO: Add last vistited, and number of times visited.


    public CeerideFavorite(Integer id, CeeridePlace ceeridePlace, String placeTag,
                           Integer counter) {
        this.id = id;
        this.ceeridePlace = ceeridePlace;
        this.placeTag = placeTag;
        this.counter = counter;
    }

    public CeerideFavorite(MenuObject menuObject, CeeridePlace ceeridePlace, String placeTag) {
        this.setMenuObject(menuObject);
        this.ceeridePlace = ceeridePlace;
        this.placeTag = placeTag;
    }

    public CeerideFavorite(CeeridePlace ceeridePlace) {
        this.ceeridePlace = ceeridePlace;
    }

    public CeeridePlace getCeeridePlace() {
        return ceeridePlace;
    }

    public String getPlaceTag() {
        return placeTag;
    }

    public Integer getCounter() {
        if (counter == null) {
            setCounter(0);
        }
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Integer getId() {
        if (id == null) {
            return -1;
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.ceeridePlace.getPlaceName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {

            return this.hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public void onClick(Context context) {
        if(context instanceof CeeRideMapActivity){
            CeeRideMapActivity activity = (CeeRideMapActivity) context;

            // Increment the counter when the favourite is clicked.
            CeerideFavoriteDbUtils.incrementCounter(context, this);
            activity.onPlaceSelected(this.ceeridePlace, 1);
        }
    }
}
