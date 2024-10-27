package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.DBModel;
import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.viewmodels.DBViewModel;
import com.example.sprintproject.viewmodels.DestinationAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DestinationsPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String userId = MainActivity.getUserId();


    public DestinationsPage() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = MainActivity.getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment. Aka rendering the value onto the destination screen
        View view = inflater.inflate(R.layout.destination_screen, container, false);

        view.findViewById(R.id.travelLogButton).setOnClickListener(v -> opentravelLogForm());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //just some sample data to put onto the rows
        List<String> destinations = Arrays.asList("Atlanta", "New York", "Tokyo", "Paris");
        List<Integer> daysPlanned = Arrays.asList(5, 3, 7, 4);

        //we set up the adaptor with the data
        DestinationAdapter destinationAdapter = new DestinationAdapter(destinations, daysPlanned);
        //where we attach our adaptor to recyclerView
        recyclerView.setAdapter(destinationAdapter);

        return view;
    }

    private void opentravelLogForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_log_form, null);
        EditText locationInput = dialogView.findViewById(R.id.locationText);
        EditText startTimeInput = dialogView.findViewById(R.id.startTime);
        EditText endTimeInput = dialogView.findViewById(R.id.endTime);

        DatabaseReference DB = DBModel.getInstance().child("users");

        builder.setView(dialogView).setTitle("Invite User")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String location = locationInput.getText().toString();
                    String startTime = startTimeInput.getText().toString();
                    String endTime = endTimeInput.getText().toString();
                    if (!location.isEmpty() & !startTime.isEmpty() && !endTime.isEmpty()) {
                        DB.child(userId).child("destinations").child(location+startTime).setValue(new DestinationModel(startTime, endTime, location));
                    } else {
                        //toast
                        System.out.println("Not valid");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}