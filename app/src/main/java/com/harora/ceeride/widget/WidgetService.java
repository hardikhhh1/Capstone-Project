package com.harora.ceeride.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by harora on 5/17/16.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new CeerideRemoteViewsFactory(this.getApplicationContext()));
    }
}
