package com.example.sanida.togoapp.Models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public Request(String owner, String rider, Boolean accepted, Trip trip) {
        this.owner = owner;
        this.rider = rider;
        this.trip=trip;
        this.accepted = accepted;
    }

    public Request(){

    }

    String owner;
    String rider;
    Trip trip;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    Boolean accepted;


    @Exclude
    public Map<Object, Object> toMap() {
        Map<Object, Object> result = new HashMap<>();
        result.put("owner", owner);
        result.put("rider", rider);
        result.put("accepted", accepted);
        result.put("trip", trip);



        return result;
    }
}
