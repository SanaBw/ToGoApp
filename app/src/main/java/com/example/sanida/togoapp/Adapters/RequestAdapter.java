package com.example.sanida.togoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanida.togoapp.Fragments.RequestFragment;
import com.example.sanida.togoapp.Models.Request;
import com.example.sanida.togoapp.Models.Trip;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Request> requests;
    private String userPhoto, userName;


    public RequestAdapter(Context context, ArrayList<Request> requests) {
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.requests = requests;
    }


    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.request, parent, false);
        return new RequestAdapter.ViewHolder(rootView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtView, userName;
        ImageView userImg;
        Button accept, decline;
        Request request;

        ViewHolder(final View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.txtView);
            userName = itemView.findViewById(R.id.userName);
            userImg = itemView.findViewById(R.id.photo);
            accept = itemView.findViewById(R.id.acceptBtn);
            decline = itemView.findViewById(R.id.declineBtn);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRider(request, v);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    declineRider(request, v);
                }
            });
        }


        @Override
        public void onClick(View view) {
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.request = request;

        String txt = "From " + request.getTrip().getStartLocation() + " To " + request.getTrip().getEndLocation();
        holder.txtView.setText(txt);

        fetchUserData(request.getRider(), holder);
    }


    @Override
    public int getItemCount() {
        return requests.size();
    }


    private void fetchUserData(User user, final RequestAdapter.ViewHolder holder) {
        final RequestAdapter.ViewHolder viewHolder = holder;

        FirebaseDatabase.getInstance().getReference().child("/users").child(user.getId()).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        if (Objects.equals(postSnapShot.getKey(), "photo")) {
                            userPhoto = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            try {
                                Glide.with(context)
                                        .load(FirebaseStorage.getInstance().getReference().
                                                child("/images").
                                                child(userPhoto))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(viewHolder.userImg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (Objects.equals(postSnapShot.getKey(), "name")) {
                            userName = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            viewHolder.userName.setText(userName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));
    }


    private void acceptRider(Request request, View v) {
        Trip trip = request.getTrip();
        User rider = request.getRider();
        int seats = trip.getSeats();
        DatabaseReference databasePath = FirebaseDatabase.getInstance().getReference().child("/trips").child(trip.getTripId());

        if (request.getTrip().getRiders() == null) {
            HashMap<String, Object> riders = new HashMap<>();

            riders.put("seat" + seats, rider.getId());
            databasePath.child("/riders").setValue(riders);
        } else {
            HashMap<String, Object> riders = trip.getRiders();

            riders.put("seat" + seats, rider.getId());
            databasePath.child("/riders").updateChildren(riders);
        }

        databasePath.child("/seats").setValue(seats - 1);
        FirebaseDatabase.getInstance().getReference().child("/requests").child(request.getId()).removeValue();

        refreshData(v);
    }


    private void declineRider(Request request, View v) {
        FirebaseDatabase.getInstance().getReference().child("/requests").child(request.getId()).removeValue();
        refreshData(v);
    }


    private void refreshData(View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        RequestFragment myFragment = new RequestFragment();

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, myFragment).commit();
    }
}
