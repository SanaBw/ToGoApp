package com.example.sanida.togoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MyTripsFragment extends Fragment {

    FloatingActionButton addTripBtn;
    Context context;
    FirebaseAuth auth;
    FragmentTransaction ft;
    RecyclerView allTrips;
    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TripAdapter adapter;
    ArrayList<Trip> trips = new ArrayList<>();
    FirebaseUser user;
    double cost;
    TextView costTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        addTripBtn = view.findViewById(R.id.addTripBtn);
        cost=0;
        costTxt = view.findViewById(R.id.costTxt);
        costTxt.setText("TOTAL COST:     " + cost + " BAM");

        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentFrame, new NewTripFragment());
                ft.commit();
            }
        });

        allTrips = view.findViewById(R.id.tripsView);
        context = getContext();

        adapter = new TripAdapter(context, trips);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        allTrips.setLayoutManager(llm);
        allTrips.setAdapter( adapter );

        getDataFromServer();

        return view;
    }

    public void getDataFromServer() {
        showProgressDialog();
        databaseReference.child("/trips").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren())
                    {
                        Trip trip = postSnapShot.getValue(Trip.class);
                        if (postSnapShot.child("userId").getValue().equals(user.getUid())){
                        trips.add(trip);
                        }

                        if (trip.getReservations()!=null){
                           if (trip.getReservations().containsValue(user.getUid())){
                                trips.add(trip);
                                System.out.println(trip.getTripName());
                                cost +=trip.getCost();
                               costTxt.setText("TOTAL COST:     " + cost + " BAM");

                            }
                        }


                    }
                }
                hideProgressDialog();
                adapter = new TripAdapter(context, trips);
                allTrips.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }


        }));

        databaseReference.child("/trips").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int repetition = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren())
                    {
                        int seat = Integer.parseInt(postSnapShot.child("maxSeats").getValue().toString())-repetition;
                        repetition++;
                        if (postSnapShot.child("seat"+seat).getValue()!=null){
                            if (postSnapShot.child("seat"+seat).getValue().equals(user.getUid())){
                                Trip trip = postSnapShot.getValue(Trip.class);
                                trips.add(trip);
                            }
                        }



                    }
                }
                hideProgressDialog();
                adapter = new TripAdapter(context, trips);
                allTrips.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }


        }));
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }



}
