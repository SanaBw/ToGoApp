package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private Context context;
    private FragmentTransaction ft;
    private RecyclerView tripsRecView;
    private DatabaseReference databaseReference;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> trips;
    private String currentUser;

    public HomeFragment(String currentUser) {
        this.currentUser = currentUser;
    }


    public HomeFragment() {
        this.currentUser = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SearchView searchView = view.findViewById(R.id.searchView);
        FloatingActionButton addTripBtn = view.findViewById(R.id.addTripBtn);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        tripsRecView = view.findViewById(R.id.tripsView);
        context = getContext();
        trips = new ArrayList<>();
        tripAdapter = new TripAdapter(Objects.requireNonNull(context), trips);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        tripsRecView.setLayoutManager(llm);
        tripsRecView.setAdapter(tripAdapter);

        llm.setOrientation(LinearLayoutManager.VERTICAL);

        getAllTrips();

        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentFrame, new NewTripFragment());
                ft.commit();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                tripAdapter.filter(text);
                return false;
            }
        });

        return view;
    }


    private void getAllTrips() {
        final ProgressDialog progressDialog = showProgressDialog(context);

        databaseReference.child("/trips").addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Trip trip = postSnapShot.getValue(Trip.class);

                        if (trip.getSeats() > 0) {
                            if (currentUser!=null){
                                if (trip.getUser().getId().equals(currentUser)){
                                    trips.add(trip);
                                }
                            } else {
                                trips.add(trip);
                            }
                        }
                    }
                }
                tripAdapter = new TripAdapter(context, trips);
                tripAdapter.notifyDataSetChanged();
                tripsRecView.setAdapter(tripAdapter);
                hideProgressDialog(progressDialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog(progressDialog);
            }
        }));
    }


    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        return progressDialog;
    }


    public static void hideProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
