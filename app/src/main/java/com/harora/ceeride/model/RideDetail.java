package com.harora.ceeride.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by harora on 3/22/16.
 */
public abstract class RideDetail {

    private String rideName;
    private String rideCost;
    private String lowRideCost;
    private String highRideCost;
    private Float surchargeValue;
    private String timeEstimate;
    private String currencyCode;

    public RideDetail() {
    }

    public RideDetail(String rideName, String rideCost, String lowRideCost,
                      String highRideCost, Float surchargeValue, String currencyCode,
                      String timeEstimate) {
        this.rideName = rideName;
        this.rideCost = rideCost;
        this.currencyCode = currencyCode;
        this.lowRideCost = lowRideCost;
        this.highRideCost = highRideCost;
        this.surchargeValue = surchargeValue;
        this.timeEstimate = timeEstimate;
    }

    public String getCurrencySymbol(){
        // https://en.wikipedia.org/wiki/ISO_4217
        if(getCurrencyCode() == null) return "";

        if(getCurrencyCode().equals("USD")){
            return "$";
        } else if(currencyCode.equals("INR")){
            return "Rs";
        }

        return "";
    }

    public abstract Drawable getAppIcon(Context context) throws PackageManager.NameNotFoundException;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public abstract String getRideCostString();

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getRideName() {
        return rideName;
    }

    public void setRideName(String rideName) {
        this.rideName = rideName;
    }

    public String getRideCost() {
        return rideCost;
    }

    public void setRideCost(String rideCost) {
        this.rideCost = rideCost;
    }

    public String getLowRideCost() {
        return lowRideCost;
    }

    public void setLowRideCost(String lowRideCost) {
        this.lowRideCost = lowRideCost;
    }

    public String getHighRideCost() {
        return highRideCost;
    }

    public void setHighRideCost(String highRideCost) {
        this.highRideCost = highRideCost;
    }

    public Float getSurchargeValue() {
        return surchargeValue;
    }

    public void setSurchargeValue(Float surchargeValue) {
        this.surchargeValue = surchargeValue;
    }

    public String getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(String timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
}
