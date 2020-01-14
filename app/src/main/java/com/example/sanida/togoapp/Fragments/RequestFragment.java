package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanida.togoapp.Adapters.RequestAdapter;
import com.example.sanida.togoapp.Models.Request;
import com.example.sanida.togoapp.Models.Trip;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestFragment extends Fragment {

    private Context context;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private RecyclerView allRequests;
    private RequestAdapter reqAdapter;
    private ArrayList<Request> requests;
    private String user;
    private TextView noReqTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        LinearLayoutManager llm = new LinearLayoutManager(context);

        requests = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        allRequests = view.findViewById(R.id.requestView);
        noReqTxt = view.findViewById(R.id.noreq);
        context = getContext();
        user = auth.getCurrentUser().getUid();
        reqAdapter = new RequestAdapter(context, requests);

        allRequests.setLayoutManager(llm);
        allRequests.setAdapter(reqAdapter);

        getDataFromServer();

        return view;
    }


    public void getDataFromServer() {
        showProgressDialog();
        databaseReference.child("/requests").addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        try {
                            Request request = new Request();
                            Trip trip = postSnapShot.child("trip").getValue(Trip.class);

                            request.setId(postSnapShot.child("id").getValue().toString());
                            request.setOwner(postSnapShot.child("owner").getValue(User.class));
                            request.setRider(postSnapShot.child("rider").getValue(User.class));
                            request.setTrip(trip);

                            if (postSnapShot.child("owner").child("id").getValue().equals(user)) {
                                requests.add(request);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                reqAdapter = new RequestAdapter(context, requests);
                allRequests.setAdapter(reqAdapter);

                if (requests.size() > 0) {
                    noReqTxt.setVisibility(View.GONE);
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
            }
        }));
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }


    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
