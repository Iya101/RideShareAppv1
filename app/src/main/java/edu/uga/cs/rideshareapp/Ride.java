package edu.uga.cs.rideshareapp;

public interface Ride {
    String getDate();
    String getFromLocation();
    String getToLocation();
    String getDriverId();
    String getUserId();
    int getPointsCost();
    void setPointsCost(int pointsCost); // Modified method signature
    boolean isOffer();
}
