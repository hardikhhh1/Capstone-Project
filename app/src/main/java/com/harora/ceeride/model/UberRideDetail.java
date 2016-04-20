package com.harora.ceeride.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harora on 3/22/16.
 */
public class UberRideDetail extends RideDetail{

    public UberRideDetail(String rideName, String rideCost,
                          String lowRideCost, String highRideCost,
                          Float surchargeValue, String currencyCode,
                          String timeEstimate) {
        // TODO : THe low ride cost and high ride cost can be null
        // IN CASE OF TAXI
        super(rideName, rideCost, lowRideCost, highRideCost, surchargeValue,
                currencyCode, timeEstimate);
    }

    @Override
    public Drawable getAppIcon(Context context) throws PackageManager.NameNotFoundException {
        List<ApplicationInfo> applicationInfos =
                context.getPackageManager().getInstalledApplications(PackageManager.GET_ACTIVITIES);
        for(ApplicationInfo info : applicationInfos){
            if(info.taskAffinity == null) continue;

            if(info.taskAffinity.toLowerCase().toString().toLowerCase().indexOf("uber") != -1){
                return  context.getPackageManager().getApplicationIcon(info);
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

        if(getLowRideCost().trim().equals(getHighRideCost().trim())){
            return "Min. " + symbol + getLowRideCost();
        }

        return symbol + getLowRideCost() + " - " + symbol + getHighRideCost();

    }

}
