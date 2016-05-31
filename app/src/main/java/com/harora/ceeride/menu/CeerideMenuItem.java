package com.harora.ceeride.menu;

import android.content.Context;
import android.util.Log;

import com.yalantis.contextmenu.lib.MenuObject;

/**
 * Created by harora on 3/27/16.
 * Abstract class for any menu item in the context menu.
 */
public abstract class CeerideMenuItem  implements ContextMenu.ContextMenuItem{

    private MenuObject menuObject;

    protected CeerideMenuItem() {
    }

    public CeerideMenuItem(MenuObject menuObject) {
        this.menuObject = menuObject;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    protected void setMenuObject(MenuObject menuObject) {
        this.menuObject = menuObject;
    }

    @Override
    public void onClick(Context context) {
        Log.d(CeerideMenuItem.class.getSimpleName(), "On click called");
    }
}
