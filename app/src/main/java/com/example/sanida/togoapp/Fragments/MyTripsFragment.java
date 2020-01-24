package com.example.sanida.togoapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanida.togoapp.Models.SwipeCallback;
import com.example.sanida.togoapp.R;
import com.example.sanida.togoapp.Models.Trip;
import com.example.sanida.togoapp.Adapters.TripAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    private CoordinatorLayout coordinatorLayout;
    private String riderPosition;
    int position;
    private Drawable drawable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FloatingActionButton addTripBtn = view.findViewById(R.id.addTripBtn);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);


        if (currentUser == null) {
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
        enableSwipeToDelete();

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

        databaseReference.child("/trips").addListenerForSingleValueEvent((new ValueEventListener() {
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



    private void enableSwipeToDelete() {

        SwipeCallback swipeCallback = new SwipeCallback(adapter, context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.RIGHT) {

                        drawable = ContextCompat.getDrawable(context, R.drawable.delete);
                        position = viewHolder.getAdapterPosition();

                        if (trips.get(position).getUser().getId().equals(currentUser)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to cancel your trip?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelMyTrip(trips.get(position));
                                    trips.remove(position);
                                    adapter.notifyDataSetChanged();

                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Your trip is canceled.", Snackbar.LENGTH_LONG);
                                    snackbar.setActionTextColor(Color.YELLOW);
                                    snackbar.show();
                                    return;
                                }
                            }).setNegativeButton("BACK", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemRemoved(position + 1);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    return;
                                }
                            }).show();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to cancel your reservation?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelReservation(trips.get(position));
                                    trips.remove(position);
                                    adapter.notifyDataSetChanged();

                                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Your reservation is canceled.", Snackbar.LENGTH_LONG);
                                    snackbar.setActionTextColor(Color.YELLOW);
                                    snackbar.show();
                                    return;
                                }
                            }).setNegativeButton("BACK", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemRemoved(position + 1);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    return;
                                }
                            }).show();

                        }

                    } else if (direction == ItemTouchHelper.LEFT) {
                        drawable = ContextCompat.getDrawable(context, R.drawable.info);
                        TripAdapter.seeTripInfo(trips.get(position),context);


                    }
                }
        };


        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(myTripsRecView);

    }


    private void cancelMyTrip(Trip trip) {
        databaseReference.child("/trips").child(trip.getTripId()).removeValue();
    }


    private void cancelReservation(Trip trip) {
        int seats = trip.getSeats();

        HashMap<String, Object> riders = trip.getRiders();
        for (Map.Entry<String, Object> rider : riders.entrySet()) {
            if (rider.getValue().equals(currentUser)) {
                riderPosition = rider.getKey();
            }
        }
        databaseReference.child("/trips").child(trip.getTripId()).child("riders").child(riderPosition).removeValue();
        databaseReference.child("/trips").child(trip.getTripId()).child("seats").setValue(seats + 1);


        HashMap<String, Object> ridersNew = trip.getRiders();
        HashMap<String, Object> ridersUpdated = trip.getRiders();

        for (Map.Entry<String, Object> riderNew : ridersNew.entrySet()) {
            ridersUpdated.put("seat" + seats, riderNew.getValue());
            seats--;
        }

        adapter.notifyDataSetChanged();
    }




}

