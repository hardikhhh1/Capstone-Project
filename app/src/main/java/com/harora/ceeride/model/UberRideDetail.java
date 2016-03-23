package com.harora.ceeride.model;

/**
 * Created by harora on 3/22/16.
 */
public class UberRideDetail extends RideDetail{

    public UberRideDetail(int logoId, String rideName, String rideCost,
                          String lowRideCost, String highRideCost,
                          String surchargeValue, String timeEstimate) {
        // TODO : THe low ride cost and high ride cost can be null
        // IN CASE OF TAXI
        super(logoId, rideName, rideCost, lowRideCost, highRideCost, surchargeValue,
                timeEstimate);
    }

}
