package com.harora.ceeride.model;

/**
 * Created by harora on 3/22/16.
 */
public abstract class RideDetail {

    private int logoId;
    private String rideName;
    private String rideCost;
    private String lowRideCost;
    private String highRideCost;
    private String surchargeValue;
    private String timeEstimate;

    public RideDetail() {
    }

    public RideDetail(int logoId, String rideName, String rideCost, String lowRideCost,
                      String highRideCost, String surchargeValue, String timeEstimate) {
        this.logoId = logoId;
        this.rideName = rideName;
        this.rideCost = rideCost;
        this.lowRideCost = lowRideCost;
        this.highRideCost = highRideCost;
        this.surchargeValue = surchargeValue;
        this.timeEstimate = timeEstimate;
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
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

    public String getSurchargeValue() {
        return surchargeValue;
    }

    public void setSurchargeValue(String surchargeValue) {
        this.surchargeValue = surchargeValue;
    }

    public String getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(String timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
}
