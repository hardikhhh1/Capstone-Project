package com.harora.ceeride.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.harora.ceeride.R;
import com.harora.ceeride.model.CeeridePlace;
import com.harora.ceeride.model.RideDetail;
import com.harora.ceeride.service.CeerideReceiver;
import com.harora.ceeride.utils.CeerideRideUtil;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by harora on 5/17/16.
 */
public class CeerideWidgetProvider extends AppWidgetProvider {

    public static final String RIDE_DETAILS = "rideDetails";
    public static final String RIDE_DETAILS_UPDATED = "rideDetailsUpdated";
    private static ArrayList<RideDetail> rideDetails;
    private HashMap<CeerideReceiver.RideType, RideDetail> rideDetailHashMap;
    private CeerideRideUtil util;

    public static ArrayList<RideDetail> getRideDetails() {
        if (rideDetails == null) {
            return new ArrayList<>();
        }
        return rideDetails;
    }

    @Override
    public void onEnabled(Context context) {
        util = new CeerideRideUtil(this, context);
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        util = new CeerideRideUtil(this, context);
    }

    private void updateAppWidgets(Context mContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext.getApplicationContext());
        ComponentName ceerideWidget = new ComponentName(mContext.getApplicationContext(),
                CeerideWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(ceerideWidget);

        for (int widgetId : appWidgetIds) {
            Intent widgetIntent = new Intent(mContext, WidgetService.class);
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            widgetIntent.setData(Uri.parse(widgetIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.ceeride_widget);
            remoteViews.setRemoteAdapter(R.id.widget_list, widgetIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RIDE_DETAILS_UPDATED)) {
            ArrayList<RideDetail> details = intent.getParcelableArrayListExtra(RIDE_DETAILS);
            if (details == null) return;
            if (rideDetailHashMap == null) {
                rideDetailHashMap = new HashMap<>();
            }

            for (RideDetail detail : details) {
                rideDetailHashMap.put(detail.getRideServiceType(), detail);
            }

            for (RideDetail detail : getRideDetails()) {
                if (!rideDetailHashMap.containsKey(detail.getRideServiceType())) {
                    rideDetailHashMap.put(detail.getRideServiceType(), detail);
                }
            }

            rideDetails = new ArrayList<>(rideDetailHashMap.values());
            updateAppWidgets(context);
        }
        super.onReceive(context, intent);
    }

    public void onLocationRecieved(CeeridePlace presentPlace) {
        util.getRideDetails();
    }
}
