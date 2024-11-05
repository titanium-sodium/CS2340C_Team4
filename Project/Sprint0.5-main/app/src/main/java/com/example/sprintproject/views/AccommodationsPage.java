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
import com.example.sprintproject.model.AccomodationsModel;
import com.example.sprintproject.viewmodels.AccomodationsViewModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationsPage extends Fragment {

    private AccomodationsViewModel accomodationsViewModel;
    private static String userId;
    public AccommodationsPage(String userId) {
        accomodationsViewModel = new AccomodationsViewModel(userId);
        this.userId = userId;
    }

    public static AccommodationsPage newInstance() {
        AccommodationsPage fragment = new AccommodationsPage(userId);
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
        FilterViewModel filterButton = new FilterViewModel(true, "Accommodations");
        //Button
        view.findViewById(R.id.newResButton).setOnClickListener(v -> openAccomodationsForm());
        view.findViewById(R.id.filterButton).setOnClickListener(v -> filterButton.changeFilter(filterButton.getFilter(), filterButton.getType()));
        return view;
    }

    private void openAccomodationsForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.accomodation_res_dialog, null);
        EditText checkInInput = dialogView.findViewById(R.id.checkInEditText);
        EditText checkOutInput = dialogView.findViewById(R.id.checkOutInput);
        EditText addressInput = dialogView.findViewById(R.id.addressEditText);
        EditText numberRoomsInput = dialogView.findViewById(R.id.roomCountEditText);
        EditText roomTypeInput = dialogView.findViewById(R.id.roomTypeDropdown);

        builder.setView(dialogView)
                .setTitle("New Accomodation")
                .setPositiveButton("Add Accomodation", (dialog, which) -> {
                    String checkIn  = checkInInput.getText().toString();
                    String checkOut = checkOutInput.getText().toString();
                    String address = addressInput.getText().toString();
                    String numberRooms = numberRoomsInput.getText().toString();
                    String roomType = roomTypeInput.getText().toString();
                    if (!checkIn.isEmpty() && !checkOut.isEmpty() && !address.isEmpty()
                            && !numberRooms.isEmpty() && !roomType.isEmpty()) {
                        accomodationsViewModel.addAccommodations(new AccomodationsModel(checkIn, checkOut, numberRooms, roomType, address));
                    } else {
                        Toast.makeText(getContext(),
                                "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}