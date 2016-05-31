package com.harora.ceeride.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.harora.ceeride.utils.CeeridePreferences;

import java.util.List;

/**
 * Created by harora on 3/22/16.
 */
public class LyftRideDetail extends RideDetail{

    public static final String ZERO_STRING = "0";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RideDetail createFromParcel(Parcel in) {
            return new LyftRideDetail(in);
        }

        public RideDetail[] newArray(int size) {
            return new LyftRideDetail[size];
        }
    };
    private static final String LYFT_PACKAGE = "me.lyft.android";
    private final String TAG = LyftRideDetail.class.getSimpleName();
    private String currencyCode;

    private LyftRideDetail(String rideId, String rideName, String rideCost,
                           String lowRideCost, String highRideCost,
                           Float surchargeValue, String timeEstimate,
                           Double pickUpLatitude, Double pickUpLongitude,
                           Double destinationLatitude, Double destinationLongitude) {
        // TODO : THe low ride cost and high ride cost can be null
        // IN CASE OF TAXI
        super(rideId, rideName, rideCost, lowRideCost, highRideCost, surchargeValue,
                "USD", timeEstimate, pickUpLatitude, pickUpLongitude, destinationLatitude,
                destinationLongitude);
    }

    private LyftRideDetail(Parcel in) {
        super(in);
    }

    private static void openLink(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    @Override
    public String getHighRideCost() {
        return String.valueOf(Double.valueOf(super.getHighRideCost()) / 100);
    }

    @Override
    public String getLowRideCost() {
        return String.valueOf(Double.valueOf(super.getLowRideCost()) / 100);
    }

    @Override
    public Float getSurchargeValue() {
        return super.getSurchargeValue() / 100f;
    }

    @Override
    public Drawable getAppIcon(Context context) throws PackageManager.NameNotFoundException {
        List<ApplicationInfo> applicationInfos =
                context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo info : applicationInfos){
            if(info.taskAffinity == null) continue;
            if (info.taskAffinity.toLowerCase().contains(CeeridePreferences.LYFT_RIDE.toLowerCase())) {
                return context.getPackageManager().getApplicationIcon(info);
            }
        }
        return null;
    }

    @Override
    public String getRideCostString() {

        if (getLowRideCost().equals(ZERO_STRING) && getHighRideCost().equals(ZERO_STRING)) {
            return "Metered";
        }

        String symbol = "";
        if(this.getCurrencyCode() != null){
            symbol = getCurrencySymbol();
        }
        return symbol + getLowRideCost() + " - " + symbol + getHighRideCost();

    }

    @Override
    public void openApp(Activity activity) throws PackageManager.NameNotFoundException {
        if (isPackageInstalled(activity, LYFT_PACKAGE)) {
            //This intent will help you to launch if the package is already installed
            openLink(activity, getDeepLink());
            Log.d(TAG, "Lyft is installed on the phone, opening the app. ");
        } else {
            openLink(activity, "https://play.google.com/store/apps/details?id=" + LYFT_PACKAGE);
            Log.d(TAG, "Lyft is not currently installed on your phone, opening Play Store.");
        }
    }

    private  String getDeepLink(){
        String builder = "lyft://?ridetype=" +
                getRideName() +
                "&pickup[latitude]=" + Double.toString(getPickUpLatitude()) +
                "&pickup[longitude]=" + Double.toString(getPickUpLongitude()) +
                "&destination[latitude]=" + Double.toString(getDestinationLatitude()) +
                "&destination[longitude]=" + Double.toString(getDestinationLongitude());
        return builder;
    }



}
