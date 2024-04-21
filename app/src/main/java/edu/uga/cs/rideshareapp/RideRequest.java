package edu.uga.cs.rideshareapp;

public class RideRequest {

    private String key;           // Unique identifier for the ride request
    private String fromLocation;  // Pick-up location
    private String toLocation;    // Destination
    private String date;          // Date of the ride
    private String comments;      // Additional comments or requirements

    public RideRequest() {
        this.key = null;
        this.fromLocation = null;
        this.toLocation = null;
        this.date = null;
        this.comments = null;
    }

    public RideRequest(String fromLocation, String toLocation, String date, String comments) {
        this.key = null;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.date = date;
        this.comments = comments;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Ride from " + fromLocation + " to " + toLocation + " on " + date + " | Comments: " + comments;
    }
}
