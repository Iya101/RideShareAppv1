package edu.uga.cs.rideshareapp;

public class RideOffer {
    private String offerId;
    private String userId;
    private String destination;
    private String date;
    private String time;

    // Default constructor is required for Firebase
    public RideOffer() {
    }

    public RideOffer(String offerId, String userId, String destination, String date, String time) {
        this.offerId = offerId;
        this.userId = userId;
        this.destination = destination;
        this.date = date;
        this.time = time;
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
