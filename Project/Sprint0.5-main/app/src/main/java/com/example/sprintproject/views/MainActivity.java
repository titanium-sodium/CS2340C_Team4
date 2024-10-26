package com.example.sprintproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.sprintproject.R;
import com.example.sprintproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String userId = getIntent().getStringExtra("userId");

        DestinationsPage destinationsPage = new DestinationsPage(userId);
        DiningEstablishmentsPage diningEstablishmentsPage = new DiningEstablishmentsPage();

        LogisticsPage logisticsPage = new LogisticsPage();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        logisticsPage.setArguments(args);

        AccommodationsPage accommodationsPage = new AccommodationsPage();
        TravelCommunityPage travelCommunityPage = new TravelCommunityPage();

        //Sets the initial screen (landing page) on the navbar to the logistics page.
        changeFragment(logisticsPage);

        /* Depending on the item selected in the navbar, switches to the corresponding screen;
        by switching to previously established instances, this should hopefully maintain the state
        when switching back and forth between screens. */
        binding.navBar.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.destinations) {

                changeFragment(destinationsPage);

            } else if (itemId == R.id.dining) {

                changeFragment(diningEstablishmentsPage);

            } else if (itemId == R.id.logisitics) {

                changeFragment(logisticsPage);

            } else if (itemId == R.id.accommodations) {

                changeFragment(accommodationsPage);

            } else if (itemId == R.id.communities) {

                changeFragment(travelCommunityPage);

            }

            return true;
        });

    }

    //A method for changing from one screen on the navbar to another.
    private void changeFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}