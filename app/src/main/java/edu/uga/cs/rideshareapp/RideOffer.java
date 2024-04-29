package edu.uga.cs.rideshareapp;

public class RideOffer implements Ride {
    private String offerId;
    private String userId;
    private String destination;
    private String fromLocation;
    private String date;
    private String time;
    private String key;           // Unique identifier for the ride request
    private boolean isAccepted;   // If the ride request has been accepted
    private boolean isOffer;      // Always true for RideOffer
    private int pointsCost = 0;

    // Default constructor is required for Firebase
    public RideOffer() {
        this.key = null;
        this.isAccepted = false;  // By default, a new request is not accepted
        this.isOffer = true;      // This is a ride offer
    }

    public RideOffer(String offerId, String userId, String fromLocation,  String destination, String date, String time) {
        this.key = null;
        this.offerId = offerId;
        this.userId = userId;
        this.fromLocation = fromLocation;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.isAccepted = false;  // By default, a new request is not accepted
        this.isOffer = true;      // This is a ride offer
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getters and setters
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(int pointsCost) {
        this.pointsCost = pointsCost;
    }

    public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean offer) {
        isOffer = offer;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFromLocation() {
        return fromLocation;
    }
    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    @Override
    public String getToLocation() {
        return destination;
    }

    @Override
    public String getDriverId() {
        return userId;
    }


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
