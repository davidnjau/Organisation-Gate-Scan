package com.dave.organisationgatepass.helperClass;

public class DbScanned {

    private String arrivalTime;
    private String departureTime;
    private String userName;

    public DbScanned() {
    }

    public DbScanned(String arrivalTime, String departureTime, String userName) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.userName = userName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
