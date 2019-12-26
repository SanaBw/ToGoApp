package com.example.sanida.togoapp;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Trip {

    String tripName;
    String startLocation;
    String endLocation;
    String date;
    String time;
    String carInfo;

    String userId;
    Boolean driving;
    int seats;


    public Trip() {

    }

    public Trip(String tripName,String startLocation,String endLocation,String date,String time,String carInfo, int seats, Boolean driving, String userId) {
        this.tripName = tripName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.date = date;
        this.time = time;
        this.carInfo = carInfo;
        this.seats=seats;
        this.driving = driving;
        this.userId = userId;
    }

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



    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }




    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tripName", tripName);
        result.put("startLocation", startLocation);
        result.put("endLocation", endLocation);
        result.put("date", date);
        result.put("time", time);
        result.put("carInfo", carInfo);
        result.put("seats", seats);
        result.put("driving", driving);
        result.put("userId", userId);


        return result;
    }
}
