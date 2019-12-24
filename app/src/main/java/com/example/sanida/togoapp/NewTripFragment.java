package com.example.sanida.togoapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;


public class NewTripFragment extends Fragment {

    String tripName, startLocation, endLocation, date, time, carInfo;
    Boolean driving;
    int seats;
    TextView tripNameTxt, startTxt, endTxt, dateTxt, timeTxt, carInfoTxt, seatsTxt;
    CheckBox drivingBox;
    Button saveBtn;

    FirebaseUser user;
    FirebaseDatabase database;
    String userPath;
    FirebaseStorage storage;
    StorageReference storageReference;
    static FirebaseAuth auth;
    DatabaseReference dbRef;


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

        return v;
    }

    private void saveTrip() {
        final String tripId = UUID.randomUUID().toString();
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
        driving = drivingBox.isChecked();

        if (tripName.equals("") || startLocation.equals("") || endLocation.equals("") || date.equals("") || time.equals("") || carInfo.equals("") || seats == 0) {
            Toast.makeText(getContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();

        } else {
            Trip trip = new Trip(tripName, startLocation, endLocation, date, time, carInfo, seats, driving, user.getEmail());
            dbRef.child(tripId).setValue(trip.toMap());

            Toast.makeText(getContext(), "Trip successfully saved and published!", Toast.LENGTH_LONG).show();
        }
    }
}
