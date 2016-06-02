package com.harora.ceeride.model;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.harora.ceeride.service.CeerideReceiver;

/**
 * Created by harora on 3/22/16.
 */
public abstract class RideDetail implements Parcelable {

    private String rideId;
    private String rideName;
    private String rideCost;
    private String lowRideCost;
    private String highRideCost;
    private String timeEstimate;
    private String currencyCode;

    private Float surchargeValue;

    private Double pickUpLatitude;
    private Double pickUpLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    RideDetail() {
    }

    RideDetail(Parcel in) {
        String data[] = new String[7];
        in.readStringArray(data);
        this.rideId = data[0];
        this.rideName = data[1];
        this.rideCost = data[2];
        this.lowRideCost = data[3];
        this.highRideCost = data[4];
        this.timeEstimate = data[5];
        this.currencyCode = data[6];

        this.surchargeValue = in.readFloat();

        this.pickUpLatitude = in.readDouble();
        this.pickUpLongitude = in.readDouble();
        this.destinationLatitude = in.readDouble();
        this.destinationLongitude = in.readDouble();
    }

    RideDetail(String rideId, String rideName, String rideCost, String lowRideCost,
               String highRideCost, Float surchargeValue, String currencyCode,
               String timeEstimate, Double pickUpLatitude, Double pickUpLongitude,
               Double destinationLatitude, Double destinationLongitude) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.rideCost = rideCost;
        this.currencyCode = currencyCode;
        this.lowRideCost = lowRideCost;
        this.highRideCost = highRideCost;
        this.surchargeValue = surchargeValue;
        this.timeEstimate = timeEstimate;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    static boolean isPackageInstalled(Context context, String packageId) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // ignored.
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.rideId,
                this.rideName,
                this.rideCost,
                this.lowRideCost,
                this.highRideCost,
                this.timeEstimate,
                this.currencyCode,
        });
        parcel.writeFloat(this.surchargeValue);

        parcel.writeDouble(this.pickUpLatitude);
        parcel.writeDouble(this.pickUpLongitude);
        parcel.writeDouble(this.destinationLatitude);
        parcel.writeDouble(this.destinationLongitude);
    }

    String getCurrencySymbol() {
        // https://en.wikipedia.org/wiki/ISO_4217
        if(getCurrencyCode() == null) return "";

        if(getCurrencyCode().equals("USD")){
            return "$";
        } else if(currencyCode.equals("INR")){
            return "Rs";
        }

        return "";
    }

    String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public abstract CeerideReceiver.RideType getRideServiceType();

    public abstract void openApp(Activity activity, String pickUpLocation,
                                 String dropOffLocation) throws PackageManager.NameNotFoundException;

    public abstract Drawable getAppIcon(Context context) throws PackageManager.NameNotFoundException;

    String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public abstract String getRideCostString();

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

    Double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(Double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    Double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(Double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    Double getDestinationLatitude() {
        return destinationLatitude;
    }

    Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
}
