package com.example.sanida.togoapp.Models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public Request(String id, User owner, User rider, Trip trip) {
        this.id = id;
        this.owner = owner;
        this.rider = rider;
        this.trip=trip;
    }

    public Request(){

    }

    User owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    User rider;
    Trip trip;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getRider() {
        return rider;
    }

    public void setRider(User rider) {
        this.rider = rider;
    }



    @Exclude
    public Map<Object, Object> toMap() {
        Map<Object, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("owner", owner);
        result.put("rider", rider);
        result.put("trip", trip);


        return result;
    }
}
