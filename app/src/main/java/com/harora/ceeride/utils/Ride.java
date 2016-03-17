package com.harora.ceeride.utils;

/**
 * Created by harora on 3/14/16.
 */
public interface Ride {

    public void getMinDuration(double startLatitude, double startLongitude,
                               double endLatitude, double endLongitude);
    public void getSharedDuration(double startLatitude, double startLongitude,
                                  double endLatitude, double endLongitude);

    public void getSingleCost(double startLatitude, double startLongitude,
                              double endLatitude, double endLongitude);
    public void getSharedCost(double startLatitude, double startLongitude,
                              double endLatitude, double endLongitude);

    public void isSurcharge(double startLatitude, double startLongitude,
                            double endLatitude, double endLongitude);
    public void getSurchargeRemoveDistance(double startLatitude, double startLongitude,
                                           double endLatitude, double endLongitude);
    public void getSurcharge(double startLatitude, double startLongitude,
                             double endLatitude, double endLongitude);

}
