package com.harora.ceeride.contextmenu;

import android.content.Context;
import android.util.Log;

import com.yalantis.contextmenu.lib.MenuObject;

/**
 * Created by harora on 3/27/16.
 */
public abstract class CeerideMenuItem  implements ContextMenu.ContextMenuItem{

    private MenuObject menuObject;

    public CeerideMenuItem() {
    }

    public CeerideMenuItem(MenuObject menuObject) {
        this.menuObject = menuObject;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public void setMenuObject(MenuObject menuObject) {
        this.menuObject = menuObject;
    }

    @Override
    public void onClick(Context context) {
        Log.d(CeerideMenuItem.class.getSimpleName(), "On click called");
    }
}
