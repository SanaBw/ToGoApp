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

import com.example.sanida.togoapp.Adapters.AllMessagesAdapter;
import com.example.sanida.togoapp.Models.Message;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class AllMessagesFragment extends Fragment {

    private TextView noReqTxt;
    private Context context;
    private RecyclerView allMessagesRecView;
    private ArrayList<User> allChatters;
    private DatabaseReference databaseReference;
    private AllMessagesAdapter allMessagesAdapter;
    private String currentUser;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        LinearLayoutManager llm = new LinearLayoutManager(context);

        allChatters = new ArrayList<>();
        context = getContext();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        allMessagesAdapter = new AllMessagesAdapter(Objects.requireNonNull(context), allChatters);
        allMessagesRecView = view.findViewById(R.id.chatView);
        noReqTxt = view.findViewById(R.id.noreq3);
        allMessagesRecView.setLayoutManager(llm);
        allMessagesRecView.setAdapter(allMessagesAdapter);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getMyMessages();

        return view;
    }

    private void getMyMessages() {
        final ProgressDialog progressDialog = HomeFragment.showProgressDialog(context);

        databaseReference.child("/messages").addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        if (postSnapShot.child("receiver").child("id").getValue().equals(currentUser)) {
                            user = postSnapShot.child("sender").getValue(User.class);
                        } else if (postSnapShot.child("sender").child("id").getValue().equals(currentUser)) {
                            user = postSnapShot.child("receiver").getValue(User.class);
                        }

                        if (postSnapShot.child("receiver").child("id").getValue().equals(currentUser) || postSnapShot.child("sender").child("id").getValue().equals(currentUser)) {
                            if (!alreadyAdded(user)){
                                allChatters.add(user);
                                System.out.println("Added + " + user.getEmail());
                            }
                        }
                    }
                }
                allMessagesAdapter = new AllMessagesAdapter(context, allChatters);
                allMessagesAdapter.notifyDataSetChanged();
                allMessagesRecView.setAdapter(allMessagesAdapter);
                HomeFragment.hideProgressDialog(progressDialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                HomeFragment.hideProgressDialog(progressDialog);
            }
        }));
    }

    private boolean alreadyAdded(User user){
        boolean added=false;
            for (User chatter:allChatters){
                if (chatter.getEmail().equals(user.getEmail())){
                    added = true;
                    break;
                } else {
                    added = false;
                }
            }

        return added;
    }


}
