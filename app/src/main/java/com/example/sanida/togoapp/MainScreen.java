package com.example.sanida.togoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sanida.togoapp.Fragments.AllMessagesFragment;
import com.example.sanida.togoapp.Fragments.HomeFragment;
import com.example.sanida.togoapp.Fragments.MessageFragment;
import com.example.sanida.togoapp.Fragments.MyTripsFragment;
import com.example.sanida.togoapp.Fragments.NewTripFragment;
import com.example.sanida.togoapp.Fragments.ProfileFragment;
import com.example.sanida.togoapp.Fragments.RequestFragment;
import com.example.sanida.togoapp.Fragments.RiderProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreen extends AppCompatActivity {

    private FragmentTransaction ft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentFrame, new HomeFragment());
        ft.commit();

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
                        ft.replace(R.id.fragmentFrame, new AllMessagesFragment());
                        break;
                    }
                    case (R.id.requests): {
                        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                        ft.replace(R.id.fragmentFrame, new RequestFragment());
                        break;
                    }

                }

                ft.commit();

                return true;
            }
        });
    }

}
