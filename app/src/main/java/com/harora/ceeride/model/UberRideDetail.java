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

import com.harora.ceeride.service.CeerideReceiver;

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
    public CeerideReceiver.RideType getRideServiceType() {
        return CeerideReceiver.RideType.UBER;
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
    public void openApp(Activity activity, String pickUpLocation,
                        String dropOffLocation) throws PackageManager.NameNotFoundException {
        if (isPackageInstalled(activity, UBER_PACKAGE)) {
            //This intent will help you to launch if the package is already installed
            openLink(activity, getDeepLink(pickUpLocation, dropOffLocation));
            Log.d(TAG, "Uber is installed on the phone, opening the app. ");
        } else {
            openLink(activity, "https://play.google.com/store/apps/details?id=" + UBER_PACKAGE);
            Log.d(TAG, "Uber is not currently installed on your phone, opening Play Store.");
        }
    }

//    public static void tempOpenApp(Activity activity){
//        if (isPackageInstalled(activity, UBER_PACKAGE)) {
//            //This intent will help you to launch if the package is already installed
//            String deepLink =
//                    "uber://?client_id=ssjRHM-uc_GgGEo_i-uQPv_Eehord70xeaUYBMwF&product_id" +
//                            "=55c66225-fbe7-4fd5-9072-eab1ece5e23e&pickup[latitude]=42.331254300000005&pickup[longitude]" +
//                            "=-71.11877369999999&dropoff[latitude]=42.3642347&dropoff[longitude]=-71.0891";
//
//            openLink(activity,
//                   deepLink );
//            Log.d(TAG, "Uber is installed on the phone, opening the app. ");
//        } else {
//            openLink(activity, "https://play.google.com/store/apps/details?id=" + UBER_PACKAGE);
//            Log.d(TAG, "Uber is not currently installed on your phone, opening Play Store.");
//        }
//    }


    private String getDeepLink(String pickUpLocation, String dropOffLocation) {
        String builder =

                "uber://?" +
                        "action=setPickup" +
                        "&pickup[latitude]=" +
                        getPickUpLatitude() +
                        "&pickup[longitude]=" +
                        getPickUpLongitude() +
//                            "&pickup[nickname]=UberHQ" +
                        "&pickup[formatted_address]=" + pickUpLocation +
                        "&dropoff[latitude]=" +
                        getDestinationLatitude() +
                        "&dropoff[longitude]=" +
                        getDestinationLongitude() +
//                        "&dropoff[nickname]=Coit%20Tower" +
                        "&dropoff[formatted_address]=" + dropOffLocation +
                        "&product_id=" +
                        getRideId();
        builder.replace(" ", "%20");
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
