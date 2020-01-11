package com.example.sanida.togoapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanida.togoapp.R;
import com.example.sanida.togoapp.Models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewTripFragment extends Fragment {

    String tripName;
    String startLocation;
    String endLocation;
    String date;
    String time;
    String carInfo;
    String userId;
    String userName;
    String userPhoto;
    double cost;
    Boolean driving;
    int seats;
    static TextView tripNameTxt, startTxt, endTxt, dateTxt, timeTxt, carInfoTxt, seatsTxt, costTxt,title;
    CheckBox drivingBox;
    Button saveBtn;

    FirebaseUser user;
    FirebaseDatabase database;
    String userPath;
    FirebaseStorage storage;
    StorageReference storageReference;
    static FirebaseAuth auth;
    DatabaseReference dbRef;
    static FragmentTransaction ft;
    Trip editableTrip;
    String tripId;
    Pattern regex;
    Matcher matcher;

    Boolean editing;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_trip, container, false);



        tripNameTxt = v.findViewById(R.id.tripNameTxt);
        startTxt = v.findViewById(R.id.startTxt);
        endTxt = v.findViewById(R.id.endTxt);
        dateTxt = v.findViewById(R.id.dateTxt);
        timeTxt = v.findViewById(R.id.timeTxt);
        carInfoTxt = v.findViewById(R.id.carInfoTxt);
        seatsTxt = v.findViewById(R.id.seatsTxt);
        drivingBox = v.findViewById(R.id.drivingBox);
        costTxt = v.findViewById(R.id.costTxt);
        title = v.findViewById(R.id.title);
        editing=false;


        saveBtn = v.findViewById(R.id.saveBtn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        userPath = user.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("/trips");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrip();
            }


        });


       if (editableTrip!=null){
           editing=true;
           title.setText("Let's edit this trip!");
           tripId=editableTrip.getTripId();
           System.out.println(tripId);
           tripNameTxt.setText(editableTrip.getTripName());
           startTxt.setText(editableTrip.getStartLocation());
           endTxt.setText(editableTrip.getEndLocation());
           dateTxt.setText(editableTrip.getDate());
           timeTxt.setText(editableTrip.getTime());
           carInfoTxt.setText(editableTrip.getCarInfo());
           seatsTxt.setText(editableTrip.getSeats());
           if (editableTrip.getDriving()){
               drivingBox.setChecked(true);
           }
           costTxt.setText(String.format("%1$,.2f",editableTrip.getCost()));
       }
        return v;
    }

    public void editMyTrip(Trip trip) {
        this.editableTrip=trip;

    }



    private void saveTrip() {
        if (!editing){
            tripId = UUID.randomUUID().toString();
        }
        tripName = tripNameTxt.getText().toString();
        startLocation = startTxt.getText().toString();
        endLocation = endTxt.getText().toString();
        date = dateTxt.getText().toString();
        time = timeTxt.getText().toString();
        carInfo = carInfoTxt.getText().toString();

        if (seatsTxt.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();
        } else {
            seats = Integer.parseInt(seatsTxt.getText().toString());
        }
        if (costTxt.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();
        } else {
            cost = Double.parseDouble(costTxt.getText().toString());
        }
        driving = drivingBox.isChecked();


        if (tripName.equals("") || startLocation.equals("") || endLocation.equals("") || date.equals("") || time.equals("") || carInfo.equals("") || seats == 0) {
            Toast.makeText(getContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();

        } else if (!isValidDate(date)){
            Toast.makeText(getContext(), "Date not valid!", Toast.LENGTH_LONG).show();
        }else if(!isValidTime(time)) {
            Toast.makeText(getContext(), "Time not valid!", Toast.LENGTH_LONG).show();
        }else{
            Trip trip = new Trip(tripId, tripName, startLocation, endLocation, date, time, carInfo, seats, driving, user.getUid(), cost);
            dbRef.child(tripId).setValue(trip.toMap());


            if (editing){
                Toast.makeText(getContext(), "Trip successfully edited!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Trip successfully saved and published!", Toast.LENGTH_LONG).show();
            }

            ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentFrame, new HomeFragment());
            ft.commit();


        }
    }

    boolean isValidTime(String time) {
        if (Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time)){
            return true;
        } else {
            return  false;
        }
    }

    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    boolean isValidDate(String input) {
        try {
            format.parse(input);
            return true;
        }
        catch(ParseException e){
            return false;
        }
    }



}
