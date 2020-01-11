package com.example.sanida.togoapp.Models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Trip {



    String tripId;
    String tripName;
    String startLocation;
    String endLocation;
    String date;
    String time;
    String carInfo;


    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    Double cost;

    String userId;
    Boolean driving;
    int seats;

    public HashMap<String, Object> getReservations() {
        return reservations;
    }

    public void setReservations(HashMap<String, Object> reservations) {
        this.reservations = reservations;
    }

    HashMap<String, Object> reservations;

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    int maxSeats;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Trip() {

    }

    public Trip(String tripId, String tripName, String startLocation, String endLocation, String date, String time, String carInfo, int seats, Boolean driving, String userId, Double cost) {
        this.tripId=tripId;
        this.tripName = tripName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.date = date;
        this.time = time;
        this.carInfo = carInfo;
        this.seats=seats;
        this.driving = driving;
        this.userId = userId;
        this.cost=cost;
        this.maxSeats=seats;
        this.reservations=null;
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

    public int getSeats() {
        return seats;
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
        Map<String, Object> result = new HashMap<>();
        result.put("tripId", tripId);
        result.put("tripName", tripName);
        result.put("startLocation", startLocation);
        result.put("endLocation", endLocation);
        result.put("date", date);
        result.put("time", time);
        result.put("carInfo", carInfo);
        result.put("seats", seats);
        result.put("driving", driving);
        result.put("userId", userId);
        result.put("cost", cost);
        result.put("maxSeats", maxSeats);
        result.put("reservations",reservations);


        return result;
    }
}
