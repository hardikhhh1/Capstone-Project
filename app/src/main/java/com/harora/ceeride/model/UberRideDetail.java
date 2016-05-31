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

import java.util.List;

/**
 * Created by harora on 3/22/16.
 */
public class UberRideDetail extends RideDetail{

    public static final String ZERO_STRING = "0";
    public static final String METERED = "Metered";
    public static final String MIN_STR = "Min. ";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RideDetail createFromParcel(Parcel in) {
            return new UberRideDetail(in);
        }

        public RideDetail[] newArray(int size) {
            return new UberRideDetail[size];
        }
    };
    private static final String TAG = UberRideDetail.class.getSimpleName();
    private static final String UBER_PACKAGE= "com.ubercab";

    public UberRideDetail(String rideId, String rideName, String rideCost,
                          String lowRideCost, String highRideCost,
                          Float surchargeValue, String currencyCode,
                          String timeEstimate, Double pickUpLatitude, Double pickUpLongitude,
                          Double destinationLatitude, Double destinationLongitude){
        // TODO : THe low ride cost and high ride cost can be null
        // IN CASE OF TAXI
        super(rideId, rideName, rideCost, lowRideCost, highRideCost, surchargeValue,
                currencyCode, timeEstimate, pickUpLatitude, pickUpLongitude,
                destinationLatitude, destinationLongitude);
    }

    private UberRideDetail(Parcel in) {
        super(in);
    }

    private static void openLink(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    @Override
    public Drawable getAppIcon(Context context) throws PackageManager.NameNotFoundException {
        List<ApplicationInfo> applicationInfos =
                context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo info : applicationInfos){
            if(info.taskAffinity == null) continue;

            if (info.taskAffinity.toLowerCase().contains("uber")) {
                return  context.getPackageManager().getApplicationIcon(info);
            }
        }
        return null;
    }

    @Override
    public void openApp(Activity activity) throws PackageManager.NameNotFoundException {
        if (isPackageInstalled(activity, UBER_PACKAGE)) {
            //This intent will help you to launch if the package is already installed
            openLink(activity, getDeepLink());
            Log.d(TAG, "Uber is installed on the phone, opening the app. ");
        } else {
            openLink(activity, "https://play.google.com/store/apps/details?id=" + UBER_PACKAGE);
            Log.d(TAG, "Uber is not currently installed on your phone, opening Play Store.");
        }
    }

    private  String getDeepLink(){

//        uber://dropoff[latitude]=37.802374&dropoff[longitude]=-122.405818&dropoff[nickname]=Coit%20Tower&dropoff[formatted_address]=1%20Telegraph%20Hill%20Blvd%2C%20San%20Francisco%2C%20CA%2094133&product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View%20team%20roster&partner_deeplink=partner%3A%2F%2Fteam%2F9383
        String builder = "uber://action=setPickup" +
                "?product_id[nickname]=" +
                getRideId() +
                "&pickup[latitude]=" +
                Double.toString(getPickUpLatitude()) +
                "&pickup[longitude]=" +
                Double.toString(getPickUpLongitude()) +
                "&dropoff[latitude]=" +
                Double.toString(getDestinationLatitude()) +
                "&dropoff[longitude]=" +
                Double.toString(getDestinationLongitude());
        return builder;
    }

    @Override
    public String getRideCostString() {
        if (getLowRideCost().equals(ZERO_STRING) && getHighRideCost().equals(ZERO_STRING)) {
            return METERED;
        }

        String symbol = "";

        if(this.getCurrencyCode() != null){
            symbol = getCurrencySymbol();
        }

        if(getLowRideCost().trim().equals(getHighRideCost().trim())){
            return MIN_STR + symbol + getLowRideCost();
        }

        return symbol + getLowRideCost() + " - " + symbol + getHighRideCost();

    }

}
