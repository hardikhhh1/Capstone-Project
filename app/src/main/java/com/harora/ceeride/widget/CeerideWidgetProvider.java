package com.harora.ceeride.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.harora.ceeride.R;
import com.harora.ceeride.model.RideDetail;

import java.util.ArrayList;

/**
 * Created by harora on 5/17/16.
 */
public class CeerideWidgetProvider extends AppWidgetProvider {

    private static ArrayList<RideDetail> rideDetails;

    public static ArrayList<RideDetail> getRideDetails() {
        if (rideDetails == null) {
            return new ArrayList<>();
        }
        return rideDetails;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            Intent widgetIntent = new Intent(context, WidgetService.class);
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            widgetIntent.setData(Uri.parse(widgetIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.ceeride_widget);
            remoteViews.setRemoteAdapter(R.id.widget_list, widgetIntent);
            appWidgetManager.updateAppWidget(widgetId, null);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        rideDetails = intent.getParcelableArrayListExtra("rideDetails");
        super.onReceive(context, intent);
    }

}
