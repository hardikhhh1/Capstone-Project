package com.harora.ceeride.contextmenu;

import android.content.Context;
import android.widget.Toast;

import com.harora.ceeride.MainMapActivity;
import com.harora.ceeride.R;
import com.harora.ceeride.db.CeerideFavoriteDbUtils;
import com.harora.ceeride.model.CeerideFavorite;
import com.harora.ceeride.model.CeeridePlace;
import com.yalantis.contextmenu.lib.MenuObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by harora on 3/23/16.
 */
public class ContextMenu {

    public List<CeerideMenuItem> ceerideMenuItems;
    public static final String ADD_FAVORITES = "Add favorites";

    private Context mContext;
    public ContextMenu(Context context, boolean close, boolean favorites){
        this.mContext = context;
        ceerideMenuItems = new ArrayList<>();
        MenuObject closeMenuObject = new MenuObject();
        closeMenuObject.setResource(R.drawable.icn_close);

        MenuObject favMenuObject = new MenuObject(ADD_FAVORITES);
        favMenuObject.setResource(R.drawable.icn_4);

        if(close){
            ceerideMenuItems.add(new CeerideMenuItem(closeMenuObject){});
        }
        addFavoriteTags();
        if(favorites) {
            ceerideMenuItems.add(new CeerideMenuItem(favMenuObject){
                @Override
                public void onClick(Context context) {
                    if(context instanceof MainMapActivity){
                        CeeridePlace dropOffPlace =
                                ((MainMapActivity) context).getDropOffPlace();
                        CeerideFavoriteDbUtils.save(mContext, new CeerideFavorite(
                                dropOffPlace, "SAMPLE"));
                    } else {
                        Toast.makeText(context, "Cannot add to favorites.").show();
                    }
                }
            });
        }
    }

    private void addFavoriteTags(){
        ArrayList<CeerideFavorite> favoriteList = CeerideFavoriteDbUtils.get(mContext, null);
        Set<CeerideFavorite> favoriteSet = new HashSet<>(favoriteList);
        for (CeerideFavorite fav : favoriteSet){
            MenuObject favMenuObject = new MenuObject(fav.getCeeridePlace().getPlaceName());
            favMenuObject.setResource(R.drawable.icn_4);
            ceerideMenuItems.add(new CeerideFavorite(favMenuObject, fav.getCeeridePlace(),
                    fav.getPlaceTag()));
        }
    }

    public List<MenuObject> getMenuObjects() {
        ArrayList<MenuObject> menuObjects = new ArrayList<>();
        for(CeerideMenuItem item : ceerideMenuItems ){
            menuObjects.add(item.getMenuObject());
        }
        return menuObjects;
    }

    public CeerideMenuItem getMenuObject(int position){
        return ceerideMenuItems.get(position);
    }


    public interface ContextMenuItem{
        public void onClick(Context context);
    }
}
