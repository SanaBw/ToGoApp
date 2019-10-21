package com.example.sanida.togoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogReg extends AppCompatActivity {

    EditText logEmail, logPassword, name, surname, email, password;
    TextView logreg, reglog;
    Button logIn, register;
    String userEmail, userPw, newUserEmail, newUserPw, newUserName, newUserSurname;
    Boolean userExists;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    ViewFlipper vf;
    ProgressBar progressBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_reg);

        vf = (ViewFlipper) findViewById(R.id.viewFlipper);

        logEmail = findViewById(R.id.logEmailText);
        logPassword = findViewById(R.id.logPwText);
        logreg = findViewById(R.id.logregText);
        name = findViewById(R.id.nameText);
        surname = findViewById(R.id.surnameText);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.pwText);
        reglog = findViewById(R.id.reglogText);
        logIn = findViewById(R.id.logBtn);
        register = findViewById(R.id.regBtn);
        userExists = false;
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        reglog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                vf.showNext();
            }
        });

        logreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                vf.showNext();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    public void registerUser() {
        newUserEmail = email.getText().toString();
        newUserPw = password.getText().toString();
        newUserName = name.getText().toString();
        newUserSurname = surname.getText().toString();

        if (newUserEmail.isEmpty() || newUserPw.isEmpty() || newUserName.isEmpty() || newUserSurname.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill out every field", Toast.LENGTH_LONG).show();
        } else if (!newUserEmail.contains("@")){
            Toast.makeText(getApplicationContext(), "Please enter a valid e-mail address", Toast.LENGTH_LONG).show();
        } else if (newUserPw.length()<5){
            Toast.makeText(getApplicationContext(), "Password needs be at least 5 characters long", Toast.LENGTH_LONG).show();
        }else if (!userExists(newUserEmail)) {
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(newUserEmail, newUserPw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                vf.showNext();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Registration failed! Please check your connection and try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public void loginUser() {
        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        userEmail = logEmail.getText().toString();
        userPw = logPassword.getText().toString();

        if (userEmail.isEmpty() || userPw.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill out every field", Toast.LENGTH_LONG).show();
        } else {
            auth.signInWithEmailAndPassword(userEmail, userPw)
                    .addOnCompleteListener(LogReg.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FAILURE: ", "Error logging in");
                                Toast.makeText(getApplicationContext(), "Wrong e-mail or password! Please try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Connecting. Please wait...", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(LogReg.this, MainScreen.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
        }
    }

    private boolean userExists(final String userEmail) {

        dbRef.child("users").orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "E-mail already exists", Toast.LENGTH_SHORT).show();
                            userExists = true;
                        } else {
                            userExists = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        return userExists;
    }
}
