package com.example.sanida.togoapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanida.togoapp.Fragments.MessageFragment;
import com.example.sanida.togoapp.Fragments.NewTripFragment;
import com.example.sanida.togoapp.Fragments.RiderProfileFragment;
import com.example.sanida.togoapp.Models.Request;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.example.sanida.togoapp.Models.Trip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Trip> trips, filteredTrips;
    private String currentUser;
    public static User currentUserModel;
    public static PopupWindow infoPopup, tripPopup;
    private Boolean isAlreadyRequested;



    public TripAdapter(Context context, ArrayList<Trip> trips) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        filteredTrips = new ArrayList<>();
        isAlreadyRequested = false;
        this.context = context;
        this.trips = trips;
        this.filteredTrips.addAll(trips);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.trip, parent, false);

        return new ViewHolder(rootView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tripStart, tripEnd, tripDate, tripSeats, tripDriving, tripUser, tripCost;
        ImageView userImg;
        User user;
        String tripId;
        Trip trip;

        ViewHolder(final View itemView) {
            super(itemView);
            tripStart = itemView.findViewById(R.id.startTxt);
            tripEnd = itemView.findViewById(R.id.endTxt);
            tripDate = itemView.findViewById(R.id.dateTxt);
            tripSeats = itemView.findViewById(R.id.seatsTxt);
            tripDriving = itemView.findViewById(R.id.driving);
            tripUser = itemView.findViewById(R.id.userTxt);
            userImg = itemView.findViewById(R.id.userPhoto);
            tripCost = itemView.findViewById(R.id.costTxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (trip.getUser().getId().equals(currentUserModel.getId())) {
                        NewTripFragment newTripFragment = new NewTripFragment();
                        ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentFrame, newTripFragment)
                                .commit();

                        newTripFragment.editMyTrip(trip);
                    } else {
                        openTrip(trip);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    seeTripInfo(trip, context);
                    return true;
                }
            });


        }}




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip trip = trips.get(position);

        holder.trip = trip;
        holder.tripId = trip.getTripId();
        holder.user = trip.getUser();
        holder.tripCost.setText(String.format("%1$,.2f", trip.getCost()) + " BAM");
        holder.tripStart.setText(trip.getStartLocation());
        holder.tripEnd.setText(trip.getEndLocation());
        holder.tripDate.setText(trip.getDate() + " at " + trip.getTime());
        holder.tripSeats.setText(trip.getSeats() + " seats available");


        if (trip.getDriving()) {
            holder.tripDriving.setText(" is driving");
        } else {
            holder.tripDriving.setText(" is not driving");
        }

        fetchUserData(context, trip.getUser().getId(), holder.tripUser,  holder.userImg);
        currentUserModel = getUserModel(currentUser);
    }


    @Override
    public int getItemCount() {
        return trips.size();
    }



    public static void fetchUserData(final Context context, String userId, final TextView textView, final ImageView imageView) {
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("/users");

        usersDatabase.child(userId).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (Objects.equals(postSnapShot.getKey(), "photo")) {
                            String userPhoto = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            try {
                                Glide.with(context)
                                        .load(FirebaseStorage.getInstance().getReference().
                                                child("/images").
                                                child(userPhoto))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (Objects.equals(postSnapShot.getKey(), "name")) {
                            textView.setText(Objects.requireNonNull(postSnapShot.getValue()).toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));
    }


    public static User getUserModel(String userId){
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference().child("/users");
        usersDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserModel = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return currentUserModel;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        trips.clear();

        if (charText.length() == 0) {
            trips.addAll(filteredTrips);
        } else {
            for (Trip tr : filteredTrips) {
                if (tr.getStartLocation().toLowerCase(Locale.getDefault()).contains(charText) || tr.getEndLocation().toLowerCase(Locale.getDefault()).contains(charText)) {
                    trips.add(tr);
                }
            }
        }

        notifyDataSetChanged();
    }


    public static void seeTripInfo(Trip trip, Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.trip_info, null);
        TextView carInfoTxt = layout.findViewById(R.id.carInfoTxt);
        RecyclerView ridersRecView = layout.findViewById(R.id.ridersProfile);
        String text = "Car info: " + trip.getCarInfo();
        ConstraintLayout background = layout.findViewById(R.id.backPopup);

        infoPopup = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        carInfoTxt.setText(text);

        if (trip.getRiders() != null) {
            HashMap<String, Object> riders = trip.getRiders();
            RidersAdapter ridersAdapter = new RidersAdapter(context, riders);
            LinearLayoutManager llm = new LinearLayoutManager(context);

            llm.setOrientation(LinearLayoutManager.VERTICAL);
            ridersRecView.setLayoutManager(llm);
            ridersRecView.setAdapter(ridersAdapter);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            infoPopup.setElevation(5.0f);
        }

        infoPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoPopup.dismiss();
            }
        });
    }


    private void openTrip(final Trip trip) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.trip_popup, null);
        final Button messageUser, reserve, profile;
        ConstraintLayout background = layout.findViewById(R.id.backPopup);


        tripPopup = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        messageUser = layout.findViewById(R.id.messageUserBtn);
        reserve = layout.findViewById(R.id.reserveBtn);
        profile = layout.findViewById(R.id.profileBtn);
        profile.setText(trip.getUser().getName() + "'s profile");

        if (!alreadyRequested(currentUserModel, trip)) {
            reserve.setVisibility(View.VISIBLE);
        }

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSeat(trip);
                tripPopup.dismiss();
                Toast.makeText(context, "Your request is sent!", Toast.LENGTH_LONG).show();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RiderProfileFragment riderProfileFragment = new RiderProfileFragment(trip.getUser().getId());
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentFrame, riderProfileFragment)
                        .commit();

                tripPopup.dismiss();
            }
        });

        messageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageFragment messageFragment = new MessageFragment(context, trip.getUser());
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentFrame, messageFragment)
                        .commit();

                tripPopup.dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            tripPopup.setElevation(5.0f);
        }

        tripPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);


        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripPopup.dismiss();
            }
        });
    }


    private boolean alreadyRequested(final User rider, final Trip trip) {
        FirebaseDatabase.getInstance().getReference().child("/requests").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Request request = postSnapShot.getValue(Request.class);
                        if (request != null) {
                            if (request.getRider().getId()
                                    .equals(rider.getId()) &&
                                    request.getTrip().getTripId().equals(trip.getTripId())) {
                                isAlreadyRequested = true;
                            } else {
                                isAlreadyRequested = false;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));

        return isAlreadyRequested;
    }


    private void requestSeat(Trip trip) {
        User owner = trip.getUser();
        User rider = currentUserModel;
        String requestId = UUID.randomUUID().toString();
        Request request = new Request(requestId, owner, rider, trip);
        FirebaseDatabase.getInstance().getReference().child("/requests").child(requestId).setValue(request);
    }
}
