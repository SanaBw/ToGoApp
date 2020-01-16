package com.example.sanida.togoapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanida.togoapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class RiderProfileFragment extends Fragment {

    private String riderId;
    private ImageView photoImg;
    private TextView nameTxt;
    private Button sendMessageBtn, allTripsBtn;
    private DatabaseReference dbRef;
    private StorageReference storageReference;


    public RiderProfileFragment(String riderId) {
        this.riderId = riderId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_profile, container, false);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        photoImg = view.findViewById(R.id.profileImg);
        nameTxt = view.findViewById(R.id.nameTxt);
        sendMessageBtn = view.findViewById(R.id.messageBtn);
        allTripsBtn = view.findViewById(R.id.allTripsBtn);
        dbRef = FirebaseDatabase.getInstance().getReference().child("/users");
        storageReference = storage.getReference();

        getRiderData();

        allTripsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTripsFragment myTripsFragment = new MyTripsFragment(riderId);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentFrame, myTripsFragment)
                        .commit();

            }
        });

        return view;
    }


    private void getRiderData() {
        dbRef.child(riderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                nameTxt.setText(username);
                allTripsBtn.setText("all " + username + "'s trips");

                if (dataSnapshot.hasChild("photo")) {
                    String imagePath = dataSnapshot.child("photo").getValue().toString();
                    Glide.with(getContext())
                            .load(storageReference.child("/images").child(imagePath))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(RequestOptions.circleCropTransform())
                            .into(photoImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
