package com.example.sanida.togoapp.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import java.util.Calendar;
import java.util.Date;
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
    private FragmentTransaction ft;
    private SimpleDateFormat format;
    private ViewFlipper viewFlipper;
    private View.OnClickListener flip, back;
    private DatePicker datePicker;
    private TimePicker timePicker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_trip, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        TextView title = v.findViewById(R.id.title);
        Button saveBtn = v.findViewById(R.id.saveBtn);
        Button deleteBtn = v.findViewById(R.id.deleteBtn);
        Button next1 = v.findViewById(R.id.nextBtn1);
        Button next2 = v.findViewById(R.id.nextBtn2);
        Button next3 = v.findViewById(R.id.nextBtn3);
        Button back2 = v.findViewById(R.id.backBtn2);
        Button back3 = v.findViewById(R.id.backBtn3);
        Button back4 = v.findViewById(R.id.backBtn4);
        Calendar calendar = Calendar.getInstance();

        datePicker = v.findViewById(R.id.datePicker);
        timePicker = v.findViewById(R.id.timePicker);

        flip = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        };
        back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        };

        datePicker.setMinDate(calendar.getTimeInMillis());

        next1.setOnClickListener(flip);
        next2.setOnClickListener(flip);
        next3.setOnClickListener(flip);

        back2.setOnClickListener(back);
        back3.setOnClickListener(back);
        back4.setOnClickListener(back);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        viewFlipper = v.findViewById(R.id.viewFlipper);
        tripNameTxt = v.findViewById(R.id.tripNameTxt);
        startTxt = v.findViewById(R.id.startTxt);
        endTxt = v.findViewById(R.id.endTxt);
        carInfoTxt = v.findViewById(R.id.carInfoTxt);
        seatsTxt = v.findViewById(R.id.seatsTxt);
        drivingBox = v.findViewById(R.id.drivingBox);
        costTxt = v.findViewById(R.id.costTxt);
        editing = false;
        dbRef = FirebaseDatabase.getInstance().getReference().child("/trips");
        ft = getActivity().getSupportFragmentManager().beginTransaction();


        if (editableTrip != null) {
            editing = true;
            tripId = editableTrip.getTripId();
            title.setText("Let's edit this trip!");
            tripNameTxt.setText(editableTrip.getTripName());
            startTxt.setText(editableTrip.getStartLocation());
            endTxt.setText(editableTrip.getEndLocation());
            carInfoTxt.setText(editableTrip.getCarInfo());
            seatsTxt.setText(String.valueOf(editableTrip.getSeats()));

            if (editableTrip.getDriving()) {
                drivingBox.setChecked(true);
            }
            costTxt.setText(String.format("%1$,.2f", editableTrip.getCost()));
            deleteBtn.setVisibility(View.VISIBLE);
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

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrip();
            }
        });

        return v;
    }


    private void deleteTrip() {
        FirebaseDatabase.getInstance().getReference().child("/trips").child(editableTrip.getTripId()).removeValue();
        Toast.makeText(getContext(), "Trip deleted!", Toast.LENGTH_LONG).show();
        ft.replace(R.id.fragmentFrame, new HomeFragment());
        ft.commit();
    }


    public void editMyTrip(Trip trip) {
        this.editableTrip = trip;
    }


    private void saveTrip() {
        String tripName = tripNameTxt.getText().toString();
        String startLocation = startTxt.getText().toString();
        String endLocation = endTxt.getText().toString();
         String date = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getYear();
        String time;

        if (Build.VERSION.SDK_INT < 23) {
            time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

        } else {
            time = timePicker.getHour() + ":" + timePicker.getMinute();
        }

        String carInfo = carInfoTxt.getText().toString();
        Boolean driving = drivingBox.isChecked();

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


}
