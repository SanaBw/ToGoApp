package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashMap;


public class HomeFragment extends Fragment {

    FloatingActionButton addTripBtn;
    Context context;
    FragmentTransaction ft;
    RecyclerView allTrips;
    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TripAdapter adapter;
    ArrayList<Trip> trips = new ArrayList<>();
    SearchView searchView;
    View view;
    LayoutInflater inflater;
    static String user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater=inflater;
        view = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = (SearchView) view.findViewById(R.id.searchView);


        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        addTripBtn = view.findViewById(R.id.addTripBtn);

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
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        allTrips.setLayoutManager(llm);
        allTrips.setAdapter(adapter);

        getDataFromServer();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String text = newText;
                adapter.filter(text);

                return false;
            }
        });


        return view;
    }

    public void getDataFromServer() {
        showProgressDialog();
        databaseReference.child("/trips").addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Trip trip = postSnapShot.getValue(Trip.class);

                        if (trip.getSeats()>0){
                            trips.add(trip);
                        }

                    }
                }
                hideProgressDialog();
                adapter = new TripAdapter(context, trips);
                adapter.notifyDataSetChanged();
                allTrips.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }


        }));
    }


    static Boolean registered = false;
    public static boolean alreadyRegistered(final Trip trip) {

       HashMap<String, Object> reservations;
       if (trip.getReservations() == null){
           registered=false;
       } else {
           reservations = trip.getReservations();
           if (reservations.containsValue(user)){
               registered=true;
           } else {
               registered=false;
           }
       }

        return registered;
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
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
