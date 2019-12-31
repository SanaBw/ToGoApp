package com.example.sanida.togoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class MainScreen extends AppCompatActivity {


    Button signOut;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    BottomNavigationView bottomNavigationView;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentFrame, new HomeFragment());
        ft.commit();

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainScreen.this, LogReg.class));
                    finish();
                }
            }
        };


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                ft = getSupportFragmentManager().beginTransaction();
                switch (menuItem.getItemId()) {
                    case (R.id.homeItem):
                    default: {
                        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                        ft.replace(R.id.fragmentFrame, new HomeFragment());
                        break;
                    }
                    case (R.id.profileItem): {
                        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                        ft.replace(R.id.fragmentFrame, new ProfileFragment());
                        break;
                    }
                    case (R.id.myTrips): {
                        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                        ft.replace(R.id.fragmentFrame, new MyTripsFragment());
                        break;
                    }
                    case (R.id.chat): {
                        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                        ft.replace(R.id.fragmentFrame, new ChatFragment());
                        break;
                    }

                }
                ft.commit();
                return true;

            }
        });
    }
}
