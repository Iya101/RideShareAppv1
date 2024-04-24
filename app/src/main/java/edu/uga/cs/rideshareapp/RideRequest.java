package edu.uga.cs.rideshareapp;

public class RideRequest {

    private String key;           // Unique identifier for the ride request
    private String userId;        // ID of the user who posted the ride request
    private String driverId;      // ID of the driver who accepts the ride request
    private String fromLocation;  // Pick-up location
    private String toLocation;    // Destination
    private String date;          // Date of the ride
    private String time;          // Time or additional requirements
    private boolean isAccepted;   // If the ride request has been accepted
    private int pointsCost;       // Points cost associated with the ride

    public RideRequest() {
        // Initialize all fields to default values
        this.key = null;
        this.userId = null;
        this.driverId = null;
        this.fromLocation = null;
        this.toLocation = null;
        this.date = null;
        this.time = null;
        this.isAccepted = false;
        this.pointsCost = 0;
    }

    public RideRequest(String userId, String fromLocation, String toLocation, String date, String time, int pointsCost) {
        this.userId = userId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.date = date;
        this.time = time;
        this.isAccepted = false;  // By default, a new request is not accepted
        this.pointsCost = pointsCost;
    }

    // Getters and Setters for all fields

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
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

    public int getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(int pointsCost) {
        this.pointsCost = pointsCost;
    }

    @Override
    public String toString() {
        return "Ride from " + fromLocation + " to " + toLocation + " on " + date + (isAccepted ? " accepted by driver ID: " + driverId : "") + " | Comments: " + time;
    }
}

