package com.example.sanida.togoapp;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Trip {
    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
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

    public String getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }

    public Boolean getDriving() {
        return driving;
    }

    public void setDriving(Boolean driving) {
        this.driving = driving;
    }

    public String getSeats() {
        return String.valueOf(seats);
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    String tripName;
    String startLocation;
    String endLocation;
    String date;
    String time;
    String carInfo;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String user;
    Boolean driving;
    int seats;

    public Map<String, Boolean> tripMap = new HashMap<>();

    public Trip() {

    }

    public Trip(String tripName,String startLocation,String endLocation,String date,String time,String carInfo, int seats, Boolean driving, String user) {
        this.tripName = tripName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.date = date;
        this.time = time;
        this.carInfo = carInfo;
        this.seats=seats;
        this.driving = driving;
        this.user = user;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", tripName);
        result.put("start", startLocation);
        result.put("end", endLocation);
        result.put("date", date + " " + time);
        result.put("car info", carInfo);
        result.put("seats", seats);
        result.put("driving", driving);
        result.put("user", user);

        return result;
    }
}
