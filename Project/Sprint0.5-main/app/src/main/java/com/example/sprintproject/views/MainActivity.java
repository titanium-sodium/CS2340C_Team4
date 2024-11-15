package com.example.sprintproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.sprintproject.R;
import com.example.sprintproject.databinding.ActivityMainBinding;
import com.example.sprintproject.model.DBModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static String userId;
    private static String tripId;

    /* Instantiates the screen classes; might later switch to a Singleton Model, but for now
        this still encompasses it in essence. */
    private DestinationsPage destinationsPage;
    private DiningEstablishmentsPage diningEstablishmentsPage;
    protected static LogisticsPage logisticsPage;
    private AccommodationsPage accommodationsPage;
    private TravelCommunityPage travelCommunityPage;

    public static String getUserId() {
        return userId;
    }
    public static String getTripId() {
        return tripId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = getIntent().getStringExtra("userId");
        tripId = getIntent().getStringExtra("tripId");
        destinationsPage = new DestinationsPage(userId);
        diningEstablishmentsPage = new DiningEstablishmentsPage(userId);
        logisticsPage = new LogisticsPage(userId);
        accommodationsPage = new AccommodationsPage(userId, tripId);
        travelCommunityPage = new TravelCommunityPage(userId);


        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("tripId", tripId);
        logisticsPage.setArguments(args);



        //Sets the initial screen (landing page) on the navbar to the logistics page.
        changeFragment(logisticsPage);
        binding.navBar.getMenu().findItem(R.id.logisitics).setChecked(true);

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

    public DestinationsPage getDestinationsPage() {
        return destinationsPage;
    }

    public DiningEstablishmentsPage getDiningDEstablishmentsPage() {
        return diningEstablishmentsPage;
    }

    public AccommodationsPage getAccomodationsPage() {
        return accommodationsPage;
    }

    public TravelCommunityPage getTravelCommunityPage() {
        return travelCommunityPage;
    }
}