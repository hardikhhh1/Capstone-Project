package com.harora.ceeride.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by harora on 3/22/16.
 */
public class LyftRideDetail extends RideDetail{

    private String currencyCode;

    public LyftRideDetail(String rideName, String rideCost,
                          String lowRideCost, String highRideCost,
                          Float surchargeValue,  String timeEstimate) {
        // TODO : THe low ride cost and high ride cost can be null
        // IN CASE OF TAXI
        super(rideName, rideCost, lowRideCost, highRideCost, surchargeValue,
                "USD", timeEstimate);
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
                context.getPackageManager().getInstalledApplications(PackageManager.GET_ACTIVITIES);
        for(ApplicationInfo info : applicationInfos){
            if(info.taskAffinity == null) continue;
            if(info.taskAffinity.toLowerCase().toString().toLowerCase().indexOf("lyft") != -1){
                return context.getPackageManager().getApplicationIcon(info);
            }
        }
        return null;
    }

    @Override
    public String getRideCostString() {

        if(getLowRideCost().equals("0") && getHighRideCost().equals("0")){
            return "Metered";
        }

        String symbol = "";
        if(this.getCurrencyCode() != null){
            symbol = getCurrencySymbol();
        }
        return symbol + getLowRideCost() + " - " + symbol + getHighRideCost();

    }



}
