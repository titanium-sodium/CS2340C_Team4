package com.example.sprintproject.views;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sprintproject.R;
import com.example.sprintproject.model.LodgingModel;
import com.example.sprintproject.model.ReservationModel;
import com.example.sprintproject.viewmodels.AccomodationsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationsPage extends Fragment {

    private AccomodationsViewModel accomodationsViewModel;
    public AccommodationsPage() {
        accomodationsViewModel = new AccomodationsViewModel();
    }

    public static AccommodationsPage newInstance() {
        AccommodationsPage fragment = new AccommodationsPage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.accomodation_screen, container, false);

        //Button
        view.findViewById(R.id.newResButton).setOnClickListener(v -> openAccomodationsForm());

        return view;
    }

    private void openAccomodationsForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.accomodation_res_dialog, null);
        EditText checkInInput = dialogView.findViewById(R.id.checkInEditText);
        EditText checkOutInput = dialogView.findViewById(R.id.checkOutInput);
        EditText addressInput = dialogView.findViewById(R.id.addressEditText);
        EditText numberRooms = dialogView.findViewById(R.id.roomCountEditText);
        EditText roomType = dialogView.findViewById(R.id.roomTypeDropdown);

        builder.setView(dialogView)
                .setTitle("New Accomodation")
                .setPositiveButton("Add Accomodation", (dialog, which) -> {

                    if (true) {
                        accomodationsViewModel.addAccomodations(new LodgingModel());
                    } else {
                        Toast.makeText(getContext(),
                                "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}