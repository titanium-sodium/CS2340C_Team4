package com.example.sprintproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sprintproject.R;
import com.example.sprintproject.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static String userId;
    private static String tripId;  // This will now persist until changed
    private FirebaseAuth mAuth;
    private Button btnMyTrips;
    private Button btnLogout;

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get or update tripId and userId
        String newTripId = getIntent().getStringExtra("tripId");
        String newUserId = getIntent().getStringExtra("userId");

        // Only update if we got new values
        if (newTripId != null) {
            tripId = newTripId;
        }
        if (newUserId != null) {
            userId = newUserId;
        }

        // Setup toolbar and navigation buttons
        setupToolbar();

        // Initialize all pages with current tripId
        initializePages();

        // Set up logistics page arguments
        setupLogisticsArguments();

        // Set initial screen to logistics
        changeFragment(logisticsPage);
        binding.navBar.getMenu().findItem(R.id.logisitics).setChecked(true);

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupToolbar() {
        btnMyTrips = findViewById(R.id.btnMyTrips);
        btnLogout = findViewById(R.id.btnLogout);

        btnMyTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to trips list activity
                Intent intent = new Intent(MainActivity.this, TripsListActivity.class);
                intent.putExtra("userId", userId);
                // Don't clear tripId here - it will be updated if user selects a new trip
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase
                mAuth.signOut();
                // Clear tripId on logout
                tripId = null;

                // Navigate to login screen
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializePages() {
        // All pages now use the same tripId
        destinationsPage = new DestinationsPage(userId);
        diningEstablishmentsPage = new DiningEstablishmentsPage(tripId);
        logisticsPage = new LogisticsPage(userId);
        accommodationsPage = new AccommodationsPage(userId, tripId);
        travelCommunityPage = new TravelCommunityPage(userId);

        // Set tripId for any pages that need it
        destinationsPage.setArguments(createBundleWithIds());
        diningEstablishmentsPage.setArguments(createBundleWithIds());
        logisticsPage.setArguments(createBundleWithIds());
        // AccommodationsPage already gets tripId in constructor
        travelCommunityPage.setArguments(createBundleWithIds());
    }

    private Bundle createBundleWithIds() {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("tripId", tripId);
        return args;
    }


    private void setupLogisticsArguments() {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("tripId", tripId);
        logisticsPage.setArguments(args);
    }

    private void setupBottomNavigation() {
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

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}