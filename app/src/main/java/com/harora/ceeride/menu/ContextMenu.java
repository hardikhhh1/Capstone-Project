package com.harora.ceeride.menu;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.harora.ceeride.R;
import com.harora.ceeride.activity.MainMapActivity;
import com.harora.ceeride.db.CeerideFavoriteDbUtils;
import com.harora.ceeride.model.CeerideFavorite;
import com.harora.ceeride.model.CeeridePlace;
import com.yalantis.contextmenu.lib.MenuObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by harora on 3/23/16.
 * Context menu placed in the toolbar.
 */
public class ContextMenu {

    private static final String ADD_FAVORITES = "Add favorites";
    private static final String LOG_TAG = ContextMenu.class.getSimpleName();
    private List<CeerideMenuItem> ceerideMenuItems;
    private CeerideMenuItem favMenuItem;
    private Context mContext;
    private MenuObject closeMenuObject;
    private MenuObject favMenuObject;

    public ContextMenu(Context context, final boolean close, final boolean favorites) {
        this.mContext = context;
        ceerideMenuItems = new ArrayList<>();
        closeMenuObject = new MenuObject();
        closeMenuObject.setResource(R.drawable.icn_close);

        favMenuObject = new MenuObject(ADD_FAVORITES);
        favMenuObject.setResource(R.drawable.icn_4);

        favMenuItem = new CeerideMenuItem(favMenuObject) {
            @Override
            public void onClick(Context context) {
                if (context instanceof MainMapActivity) {
                    CeeridePlace dropOffPlace =
                            ((MainMapActivity) context).getDropOffPlace();
                    CeerideFavoriteDbUtils.save(mContext, new CeerideFavorite(dropOffPlace));
                    try {
                        initMenuItems(close, favorites);
                        ((MainMapActivity) mContext).initMenuFragment();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Error while initializing the menu fragment again");
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.cannot_add_fav_text),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        try {
            initMenuItems(close, favorites);
            ((MainMapActivity) mContext).initMenuFragment();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while initializing the menu fragment again");
        }

    }

    private void initMenuItems(boolean close, boolean favorites) {
        ceerideMenuItems = new ArrayList<>();
        ceerideMenuItems.add(favMenuItem);
        if (favorites) {
            addFavoriteTags();
        }
        if (close) {
            ceerideMenuItems.add(new CeerideMenuItem(closeMenuObject) {
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
        void onClick(Context context);
    }
}
