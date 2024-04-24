package edu.uga.cs.rideshareapp;

public class RideOffer {
    private String offerId;
    private String userId;
    private String destination;
    private String fromLocation;
    private String date;
    private String time;
    private String key;           // Unique identifier for the ride request

    // Default constructor is required for Firebase
    public RideOffer() {

        this.key = null;
    }

    public RideOffer(String offerId, String userId,String fromLocation,  String destination, String date, String time) {
        this.key = null;
        this.offerId = offerId;
        this.userId = userId;
        this.fromLocation = fromLocation;
        this.destination = destination;
        this.date = date;
        this.time = time;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
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
}
