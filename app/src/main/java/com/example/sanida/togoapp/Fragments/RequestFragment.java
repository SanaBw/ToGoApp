package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanida.togoapp.Adapters.RequestAdapter;
import com.example.sanida.togoapp.Adapters.TripAdapter;
import com.example.sanida.togoapp.Models.Request;
import com.example.sanida.togoapp.Models.Trip;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class RequestFragment extends Fragment {

    Context context;
    FirebaseAuth auth;
    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    RecyclerView  allRequests;
    RequestAdapter reqAdapter;
    ArrayList<Request> requests = new ArrayList<>();
    String user;
    TextView noReqTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        auth = FirebaseAuth.getInstance();

        allRequests = view.findViewById(R.id.requestView);
        noReqTxt = view.findViewById(R.id.noreq);
        context = getContext();
        user=auth.getCurrentUser().getUid();

        reqAdapter = new RequestAdapter(context, requests);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        allRequests.setLayoutManager(llm);
        allRequests.setAdapter( reqAdapter );

        getDataFromServer();




        return view;
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

    public void getDataFromServer() {
        showProgressDialog();
        databaseReference.child("/requests").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren())
                    {
                        try {
                            System.out.println(postSnapShot);
                            System.out.println(postSnapShot.child("owner"));
                            System.out.println(user);
                            System.out.println(postSnapShot.child("id").getValue().toString());
                            Request request = new Request();
                            request.setId(postSnapShot.child("id").getValue().toString());
                            request.setOwner(postSnapShot.child("owner").getValue(User.class));
                            request.setRider(postSnapShot.child("rider").getValue(User.class));
                            Trip trip = postSnapShot.child("trip").getValue(Trip.class);
                            request.setTrip(trip);
                            if (postSnapShot.child("owner").child("id").getValue().equals(user)){
                                requests.add(request);
                            }
                        }catch (Exception e){
                            e.printStackTrace();

                        }




                    }}

                hideProgressDialog();
                reqAdapter = new RequestAdapter(context, requests);
                allRequests.setAdapter(reqAdapter);

                if (requests.size()>0){
                    noReqTxt.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }


        }));

    }






}
