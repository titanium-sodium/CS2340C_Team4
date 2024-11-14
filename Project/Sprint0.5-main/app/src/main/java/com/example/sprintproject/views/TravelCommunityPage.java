package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.AccommodationsModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TravelCommunityPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelCommunityPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String userId;

    public TravelCommunityPage() {
        // Required empty public constructor
    }
    public TravelCommunityPage(String userId) {
        this.userId = userId;
    }


    public static TravelCommunityPage newInstance(String param1, String param2) {
        TravelCommunityPage fragment = new TravelCommunityPage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.travel_community_screen, container, false);
        view.findViewById(R.id.addPost).setOnClickListener(v -> openAddPostForm());
        return view;
    }

    private void openAddPostForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.travel_post_dialog, null);
        EditText startInput = dialogView.findViewById(R.id.startEditText);
        EditText endInput = dialogView.findViewById(R.id.endEditText);
        EditText destinationInput = dialogView.findViewById(R.id.destinationEditText);
        EditText accommodationsInput = dialogView.findViewById(R.id.accomodationsEditText);
        EditText reservationInput = dialogView.findViewById(R.id.diningReservationEditText);
        EditText noteInput = dialogView.findViewById(R.id.notesEditText);

        builder.setView(dialogView)
                .setTitle("New Travel Post")
                .setPositiveButton("Add Accommodation", (dialog, which) -> {
                    String start  = startInput.getText().toString();
                    String end = endInput.getText().toString();
                    String destination = destinationInput.getText().toString();
                    String accomodations = accommodationsInput.getText().toString();
                    String reservation = reservationInput.getText().toString();
                    String notes = noteInput.getText().toString();
                    if (true) {
                        //TODO need to implement
                    } else {
                        Toast.makeText(getContext(),
                                "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}