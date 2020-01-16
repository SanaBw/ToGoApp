package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanida.togoapp.R;
import com.example.sanida.togoapp.Models.Trip;
import com.example.sanida.togoapp.Adapters.TripAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyTripsFragment extends Fragment {

    private Context context;
    private FragmentTransaction ft;
    private RecyclerView myTripsRecView;
    private DatabaseReference databaseReference;
    private TripAdapter adapter;
    private String currentUser;
    private ArrayList<Trip> trips;
    private double cost;
    private TextView costTxt;
    private TextView noReqTxt;


    public MyTripsFragment(String currentUser){
        this.currentUser=currentUser;
    }


    public MyTripsFragment(){
        this.currentUser=null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FloatingActionButton addTripBtn = view.findViewById(R.id.addTripBtn);
        LinearLayoutManager llm = new LinearLayoutManager(context);

        if (currentUser==null){
            currentUser = auth.getCurrentUser().getUid();
        } else {
            addTripBtn.hide();
        }

        trips = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        cost = 0;
        costTxt = view.findViewById(R.id.costTxt);
        myTripsRecView = view.findViewById(R.id.tripsView);
        context = getContext();
        adapter = new TripAdapter(context, trips);
        noReqTxt = view.findViewById(R.id.noreq2);

        costTxt.setText("TOTAL COST:     " + cost + " BAM");
        myTripsRecView.setLayoutManager(llm);
        myTripsRecView.setAdapter(adapter);

        getAllMyTrips();

        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentFrame, new NewTripFragment());
                ft.commit();
            }
        });

        return view;
    }


    private void getAllMyTrips() {
        final ProgressDialog progressDialog = HomeFragment.showProgressDialog(context);

        databaseReference.child("/trips").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Trip trip = postSnapShot.getValue(Trip.class);
                        if (postSnapShot.child("user").child("id").getValue().equals(currentUser)) {
                            trips.add(trip);
                        }

                        if (trip.getRiders() != null) {
                            if (trip.getRiders().containsValue(currentUser)) {
                                trips.add(trip);
                                cost += trip.getCost();
                                costTxt.setText("TOTAL COST:     " + cost + " BAM");
                            }
                        }
                    }
                }
                adapter = new TripAdapter(context, trips);
                myTripsRecView.setAdapter(adapter);

                if (trips.size() > 0) {
                    noReqTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                HomeFragment.hideProgressDialog(progressDialog);
            }
        }));

        databaseReference.child("/trips").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int repetition = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        int seat = Integer.parseInt(postSnapShot.child("maxSeats").getValue().toString()) - repetition;
                        repetition++;

                        if (postSnapShot.child("seat" + seat).getValue() != null) {
                            if (postSnapShot.child("seat" + seat).getValue().equals(currentUser)) {
                                Trip trip = postSnapShot.getValue(Trip.class);
                                trips.add(trip);
                            }
                        }
                    }
                }
                adapter = new TripAdapter(context, trips);
                myTripsRecView.setAdapter(adapter);
                HomeFragment.hideProgressDialog(progressDialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                HomeFragment.hideProgressDialog(progressDialog);
            }
        }));
    }
}
