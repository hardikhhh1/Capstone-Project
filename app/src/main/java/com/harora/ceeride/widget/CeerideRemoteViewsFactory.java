package com.harora.ceeride.widget;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.harora.ceeride.R;
import com.harora.ceeride.model.RideDetail;

import java.util.List;

/**
 * Created by harora on 5/17/16.
 */
class CeerideRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    public static final String NO_SURCHARGE = "No surcharge";
    private Context context;

    private List<RideDetail> rideDetails;


    public CeerideRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        rideDetails = CeerideWidgetProvider.getRideDetails();
    }

    public int getCount() {
        return rideDetails.size();
    }

    public RemoteViews getLoadingView() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.widget_ride_detail);

        row = updateView(row, rideDetails.get(position));
        return row;
    }

    private RemoteViews updateView(RemoteViews row, RideDetail rideDetail) {
        Float surchargeValue = rideDetail.getSurchargeValue();
        String surcharge = Float.toString(surchargeValue);
        row.setTextViewText(R.id.ride_name, rideDetail.getRideServiceType().toString());

        if (surchargeValue > 1.0) {
            row.setViewVisibility(R.id.surcharge_icon, View.VISIBLE);
            row.setTextViewText(R.id.surcharge_value, surcharge);
        } else {
            row.setTextViewText(R.id.surcharge_value, NO_SURCHARGE);
        }

        return row;
    }

    @Override
    public void onDataSetChanged() {
        rideDetails = CeerideWidgetProvider.getRideDetails();
    }

    @Override
    public void onDestroy() {
    }
}
