package com.harora.ceeride.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.widget.CeerideWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harora on 5/4/16.
 */
public class CeerideReceiver extends BroadcastReceiver {


    public static final String RIDE_TYPE_KEY = "rideType";
    private Callback callback;

    public CeerideReceiver() {
    }

    public CeerideReceiver(Fragment fragment) {
        if (fragment instanceof Callback) {
            this.callback = (Callback) fragment;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<RideDetail> rideDetails = intent.
                getParcelableArrayListExtra(CeerideRideService.RIDE_DETAILS_KEY);
        RideType rideType = (RideType) intent.getSerializableExtra(RIDE_TYPE_KEY);
        if (callback != null) {
            callback.onRideDetails(rideDetails, rideType);
        }
        updateWidgets(context, rideDetails);
    }

    private void updateWidgets(Context context, ArrayList<RideDetail> rideDetails) {

        // Update the widget:

        Intent intent = new Intent(context, CeerideWidgetProvider.class);
        intent.putParcelableArrayListExtra(CeerideRideService.RIDE_DETAILS_KEY, rideDetails);
        intent.setAction(CeerideWidgetProvider.RIDE_DETAILS_UPDATED);
//         Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
//         since it seems the onUpdate() is only fired on that:
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);
    }

    public enum RideType {
        UBER,
        LYFT
    }

    public interface Callback {
        void onRideDetails(List<RideDetail> rideDetails, RideType rideType);
    }

}
