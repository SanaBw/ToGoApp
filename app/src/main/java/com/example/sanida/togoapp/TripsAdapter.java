package com.example.sanida.togoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

class TripsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Trip> trips;
    String userName, userPhoto;
    ArrayList<Trip> arraylist;

    public TripsAdapter(Context con,ArrayList<Trip> trips)
    {
        context=con;
        layoutInflater = LayoutInflater.from(context);
        this.trips=trips;
        arraylist = new ArrayList<>();
        this.arraylist.addAll(trips);
    }
    @Override
    public int getCount() {
        return trips.size();
    }
    ViewHolder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.trip, null, false);
            holder = new ViewHolder();
            holder.tripStart = (TextView) convertView.findViewById(R.id.startTxt);
            holder.tripEnd = (TextView) convertView.findViewById(R.id.endTxt);
            holder.tripDate = (TextView) convertView.findViewById(R.id.dateTxt);
            holder.tripSeats = (TextView) convertView.findViewById(R.id.seatsTxt);
            holder.tripDriving = (TextView) convertView.findViewById(R.id.driving);
            holder.tripUser = (TextView) convertView.findViewById(R.id.userTxt);
            holder.userImg = convertView.findViewById(R.id.userPhoto);
            holder.tripCost = convertView.findViewById(R.id.costTxt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Trip trip=trips.get(position);
        holder.tripCost.setText(trip.getCost() + " BAM");
        holder.tripStart.setText(trip.getStartLocation());
        holder.tripEnd.setText(trip.getEndLocation());
        holder.tripDate.setText(trip.getDate() + " at " + trip.getTime());
        holder.tripSeats.setText(trip.getSeats() + " seats available");
        if (trip.getDriving()){
            holder.tripDriving.setText(" is driving");
        } else {
            holder.tripDriving.setText(" is not driving");
        }

        fetchUserData(trip.getUserId(), holder);


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        trips.clear();
        if (charText.length() == 0) {
            trips.addAll(arraylist);
        } else {
            for (Trip tr : arraylist) {
                if (tr.getStartLocation().toLowerCase(Locale.getDefault()).contains(charText) || tr.getEndLocation().toLowerCase(Locale.getDefault()).contains(charText)) {
                    trips.add(tr);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView tripStart,tripEnd, tripDate, tripSeats, tripDriving, tripUser, tripCost;
        ImageView userImg;


    }
    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }



    private void fetchUserData(String user, ViewHolder holder) {

        final ViewHolder viewHolder = holder;

        FirebaseDatabase.getInstance().getReference().child("/users").child(user).addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (Objects.equals(postSnapShot.getKey(), "photo")) {
                            userPhoto = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            try{
                                Glide.with(context)
                                        .load(FirebaseStorage.getInstance().getReference().
                                                child("/images").
                                                child(userPhoto))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(viewHolder.userImg);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if (Objects.equals(postSnapShot.getKey(), "name")) {
                            userName = Objects.requireNonNull(postSnapShot.getValue()).toString();
                            viewHolder.tripUser.setText(userName);
                        }



                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        }));
    }
}
