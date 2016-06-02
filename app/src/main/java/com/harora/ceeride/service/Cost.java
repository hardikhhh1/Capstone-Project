package com.harora.ceeride.service;

/**
 * Created by harora on 4/6/16.
 */
public class Cost {

    private String rideType;
    private String displayName;
    private String currency;
    private int estimatedCostCentsMin;
    private int estimatedCostCentsMax;
    private int estimatedDurationSeconds;
    private String primetimePercentage;

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getEstimatedCostCentsMin() {
        return estimatedCostCentsMin;
    }

    public void setEstimatedCostCentsMin(int estimatedCostCentsMin) {
        this.estimatedCostCentsMin = estimatedCostCentsMin;
    }

    public int getEstimatedCostCentsMax() {
        return estimatedCostCentsMax;
    }

    public void setEstimatedCostCentsMax(int estimatedCostCentsMax) {
        this.estimatedCostCentsMax = estimatedCostCentsMax;
    }

    public int getEstimatedDurationSeconds() {
        return estimatedDurationSeconds;
    }

    public void setEstimatedDurationSeconds(int estimatedDurationSeconds) {
        this.estimatedDurationSeconds = estimatedDurationSeconds;
    }

    public String getPrimetimePercentage() {
        return primetimePercentage;
    }

    public void setPrimetimePercentage(String primetimePercentage) {
        this.primetimePercentage = primetimePercentage;
    }
}
