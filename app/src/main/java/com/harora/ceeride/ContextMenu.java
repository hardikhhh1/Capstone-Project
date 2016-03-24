package com.harora.ceeride;

import com.yalantis.contextmenu.lib.MenuObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harora on 3/23/16.
 */
public class ContextMenu {

    public List<MenuObject> menuObjects;
    public static final String ADD_FAVORITES = "Add favorites";

    public ContextMenu(boolean close, boolean favourites){
        menuObjects = new ArrayList<>();
        MenuObject closeMenuObject = new MenuObject();
        closeMenuObject.setResource(R.drawable.icn_close);

        MenuObject favMenuObject = new MenuObject(ADD_FAVORITES);
        favMenuObject.setResource(R.drawable.icn_4);

        if(close){
            menuObjects.add(closeMenuObject);
        }
        if(favourites) {
            menuObjects.add(favMenuObject);
        }
    }

    public List<MenuObject> getMenuObjects() {
        return menuObjects;
    }

    public MenuObject getMenuObject(int position){
        return menuObjects.get(position);
    }
}
