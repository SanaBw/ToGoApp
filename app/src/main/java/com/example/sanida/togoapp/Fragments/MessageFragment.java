package com.example.sanida.togoapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanida.togoapp.Adapters.AllMessagesAdapter;
import com.example.sanida.togoapp.Adapters.MessagesAdapter;
import com.example.sanida.togoapp.Adapters.TripAdapter;
import com.example.sanida.togoapp.Models.Message;
import com.example.sanida.togoapp.Models.User;
import com.example.sanida.togoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class MessageFragment extends Fragment {

    private Context context;
    private RecyclerView messagesView;
    private ArrayList<Message> messages;
    private DatabaseReference databaseReference;
    private MessagesAdapter messagesAdapter;
    private User currentUser, participant;
    private Button sendBtn;
    private EditText msgToSend;
    private TextView particName;
    private ImageView particImg;
    private long msgNumber;
    private LinearLayoutManager manager;

    public MessageFragment(Context context, User participant) {
        this.context = context;
        this.participant = participant;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);
        manager = new LinearLayoutManager(context);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(Objects.requireNonNull(context), messages);
        context = getContext();
        messagesView = view.findViewById(R.id.messagesView);
        messagesView.setAdapter(messagesAdapter);
        messagesView.setLayoutManager(manager);
        currentUser = TripAdapter.currentUserModel;
        sendBtn = view.findViewById(R.id.sendBtn);
        msgToSend = view.findViewById(R.id.msgToSend);
        particName = view.findViewById(R.id.particName);
        particImg = view.findViewById(R.id.particImg);
        TripAdapter.fetchUserData(context, participant.getId(), particName, particImg);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!msgToSend.getText().toString().equals("")) {
                    sendMessage(currentUser, participant, msgToSend.getText().toString());
                    msgToSend.setText("");
                }
            }
        });

        particImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RiderProfileFragment riderProfileFragment = new RiderProfileFragment(participant.getId());
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentFrame, riderProfileFragment)
                        .commit();
            }
        });

        getMyMessages();
        return view;
    }

    private void sendMessage(User currentUser, User participant, String message) {
        databaseReference.child("/messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                msgNumber = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c);
        String formattedTime = tf.format(c);

        Message messageToSend = new Message(msgNumber + 1, currentUser, participant, formattedDate, formattedTime, message);
        try {
            databaseReference.child("/messages").child(String.valueOf(msgNumber + 1)).setValue(messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            messages.clear();
            getMyMessages();
        }
    }

    private void getMyMessages() {

        databaseReference.child("/messages").addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                        Message message = postSnapShot.getValue(Message.class);

                        if ((message.getReceiver().getId().equals(currentUser.getId()) && message.getSender().getId().equals(participant.getId())) ||
                                message.getReceiver().getId().equals(participant.getId()) && message.getSender().getId().equals(currentUser.getId())) {
                            System.out.println(message);
                            messages.add(message);

                        }
                        msgNumber = dataSnapshot.getChildrenCount();
                    }

                }
                messagesAdapter = new MessagesAdapter(context, messages);
                messagesAdapter.notifyDataSetChanged();
                messagesView.setAdapter(messagesAdapter);
                if (messages.size()>0){
                    messagesView.smoothScrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));
    }

}
