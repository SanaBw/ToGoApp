package com.example.sanida.togoapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.example.sanida.togoapp.Models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;


public class NewTripFragment extends Fragment {

    private String tripId;
    private double cost;
    private int seats;
    private TextView tripNameTxt, startTxt, endTxt, dateTxt, timeTxt, carInfoTxt, seatsTxt, costTxt;
    private CheckBox drivingBox;
    private User user;
    private DatabaseReference dbRef;
    private Trip editableTrip;
    private Boolean editing;
    private SimpleDateFormat format;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_trip, container, false);
        TextView title = v.findViewById(R.id.title);
        Button saveBtn = v.findViewById(R.id.saveBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        tripNameTxt = v.findViewById(R.id.tripNameTxt);
        startTxt = v.findViewById(R.id.startTxt);
        endTxt = v.findViewById(R.id.endTxt);
        dateTxt = v.findViewById(R.id.dateTxt);
        timeTxt = v.findViewById(R.id.timeTxt);
        carInfoTxt = v.findViewById(R.id.carInfoTxt);
        seatsTxt = v.findViewById(R.id.seatsTxt);
        drivingBox = v.findViewById(R.id.drivingBox);
        costTxt = v.findViewById(R.id.costTxt);
        editing = false;
        dbRef = FirebaseDatabase.getInstance().getReference().child("/trips");
        format = new SimpleDateFormat("dd-MM-yyyy");

        if (editableTrip != null) {
            editing = true;
            tripId = editableTrip.getTripId();
            title.setText("Let's edit this trip!");
            tripNameTxt.setText(editableTrip.getTripName());
            startTxt.setText(editableTrip.getStartLocation());
            endTxt.setText(editableTrip.getEndLocation());
            dateTxt.setText(editableTrip.getDate());
            timeTxt.setText(editableTrip.getTime());
            carInfoTxt.setText(editableTrip.getCarInfo());
            seatsTxt.setText(String.valueOf(editableTrip.getSeats()));

            if (editableTrip.getDriving()) {
                drivingBox.setChecked(true);
            }
            costTxt.setText(String.format("%1$,.2f", editableTrip.getCost()));
        }

        FirebaseDatabase.getInstance().getReference().child("/users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrip();
            }


        });

        return v;
    }


    public void editMyTrip(Trip trip) {
        this.editableTrip = trip;
    }


    private void saveTrip() {
        String tripName = tripNameTxt.getText().toString();
        String startLocation = startTxt.getText().toString();
        String endLocation = endTxt.getText().toString();
        String date = dateTxt.getText().toString();
        String time = timeTxt.getText().toString();
        String carInfo = carInfoTxt.getText().toString();
        Boolean driving = drivingBox.isChecked();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        if (!editing) {
            tripId = UUID.randomUUID().toString();
        }

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

        if (tripName.equals("") || startLocation.equals("") || endLocation.equals("") || date.equals("") || time.equals("") || carInfo.equals("") || seats == 0) {
            Toast.makeText(getContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();
        } else if (!isValidDate(date)) {
            Toast.makeText(getContext(), "Date not valid!", Toast.LENGTH_LONG).show();
        } else if (!isValidTime(time)) {
            Toast.makeText(getContext(), "Time not valid!", Toast.LENGTH_LONG).show();
        } else {
            Trip trip = new Trip(tripId, tripName, startLocation, endLocation, date, time, carInfo, seats, driving, user, cost);
            dbRef.child(tripId).setValue(trip.toMap());

            if (editing) {
                Toast.makeText(getContext(), "Trip successfully edited!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Trip successfully saved and published!", Toast.LENGTH_LONG).show();
            }

            ft.replace(R.id.fragmentFrame, new HomeFragment());
            ft.commit();
        }
    }


    private boolean isValidTime(String time) {
        if (Pattern.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]", time)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isValidDate(String input) {
        try {
            format.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
