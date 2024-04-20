package edu.uga.cs.rideshareapp;

public class RideRequest {
    private String requestId;
    private String destination;
    private String time;

    // Default constructor is required for Firebase
    public RideRequest() {
    }

    // Constructor with parameters
    public RideRequest(String requestId, String destination, String time) {
        this.requestId = requestId;
        this.destination = destination;
        this.time = time;
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

