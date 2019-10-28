package com.example.sanida.togoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainScreen extends AppCompatActivity {


    Button signOut;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    BottomNavigationView bottomNavigationView;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentFrame, new HomeFragment());
        ft.commit();

        /*signOut = findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                auth.signOut();
                setContentView(R.layout.log_reg);
            }
        });*/

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
                        ft.replace(R.id.fragmentFrame, new HomeFragment());
                        break;
                    }
                    case (R.id.searchItem): {
                        ft.replace(R.id.fragmentFrame, new SearchFragment());
                        break;
                    }
                    case (R.id.profileItem): {
                        System.out.println("profile");
                        break;
                    }
                    case (R.id.chat): {
                        System.out.println("chat");
                        break;
                    }

                }
                ft.commit();
                return true;

            }
        });
    }
}
