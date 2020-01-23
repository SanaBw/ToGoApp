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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class LogReg extends AppCompatActivity {

    private EditText logEmail, logPassword, name, email, password;
    private String userEmail, newUserName, newUserEmail;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference dbRef;
    private ViewFlipper viewFlipper;
    private ProgressBar progressBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_reg);

        TextView resetPw = findViewById(R.id.resetPwTxt);
        TextView logreg = findViewById(R.id.logregText);
        TextView reglog = findViewById(R.id.reglogText);
        Button logIn = findViewById(R.id.logBtn);
        Button register = findViewById(R.id.regBtn);

        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        logEmail = findViewById(R.id.logEmailText);
        logPassword = findViewById(R.id.logPwText);
        name = findViewById(R.id.nameText);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.pwText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(LogReg.this, MainScreen.class);
            startActivity(i);
            finish();
        }

        reglog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                viewFlipper.showNext();
            }
        });

        logreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                viewFlipper.showNext();
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

        resetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetUserPw();
            }
        });
    }


    public void registerUser() {
        String newUserPw = password.getText().toString();
        newUserEmail = email.getText().toString();
        newUserName = name.getText().toString();

        if (newUserEmail.isEmpty() || newUserPw.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();
        } else if (!newUserEmail.contains("@")) {
            Toast.makeText(getApplicationContext(), "Please enter a valid e-mail address.", Toast.LENGTH_LONG).show();
        } else if (newUserPw.length() < 5) {
            Toast.makeText(getApplicationContext(), "Password needs be at least 5 characters long.", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(newUserEmail, newUserPw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                sendVerificationEmail();
                                Toast.makeText(getApplicationContext(), "Registration successful! Please check your inbox.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                viewFlipper.showNext();
                                logEmail.setText(newUserEmail);
                            } else {

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    Toast.makeText(getApplicationContext(), "Such user already exists! Choose another E-mail or reset your password.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Registration failed! Please check your connection and try again.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
        }
    }


    public void loginUser() {
        String userPw = logPassword.getText().toString();
        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        userEmail = logEmail.getText().toString();

        if (userEmail.isEmpty() || userPw.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill out every field.", Toast.LENGTH_LONG).show();
        } else {
            auth.signInWithEmailAndPassword(userEmail, userPw)
                    .addOnCompleteListener(LogReg.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Log.w("FAILURE: ", "Error logging in");
                                Toast.makeText(getApplicationContext(), "Wrong e-mail or password! Please try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else if (!auth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(getApplicationContext(), "Please verify your e-mail. Check your inbox!", Toast.LENGTH_LONG).show();
                                auth.getCurrentUser().sendEmailVerification();
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


    private void sendVerificationEmail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            writeNewUser(newUserName, newUserEmail, user.getUid());
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please check your connection and try again.", Toast.LENGTH_LONG).show();
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }


    private void writeNewUser(String name, String email, String id) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", name);
        newUser.put("email", email);
        newUser.put("id",id);

        dbRef.child(currentUser.getUid()).setValue(newUser).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "There was an error adding your data. Please go to the profile tab.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void resetUserPw() {
        userEmail = logEmail.getText().toString();

        if (userEmail.equals("") || !userEmail.contains("@")) {
            Toast.makeText(LogReg.this, "Enter your e-mail address first.", Toast.LENGTH_SHORT).show();
        } else {
            auth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(LogReg.this, "Check your e-mail.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LogReg.this, "No such user found.", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR", e.getMessage());
                }
            });
        }
    }


}




