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
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.viewmodels.DiningReservationViewModel;
import com.example.sprintproject.viewmodels.FilterViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiningEstablishmentsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiningEstablishmentsPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private DiningReservationViewModel diningReservationViewModel;
    public DiningEstablishmentsPage() {
        diningReservationViewModel = new DiningReservationViewModel();
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiningEstablishmentsPage.
     */

    public static DiningEstablishmentsPage newInstance(String param1, String param2) {
        DiningEstablishmentsPage fragment = new DiningEstablishmentsPage();
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
        View view = inflater.inflate(R.layout.dining_reservations_screen, container, false);
        FilterViewModel filterButton = new FilterViewModel(true, "Dining");
        //Button
        view.findViewById(R.id.addReservationButton).setOnClickListener(v -> openReservationForm());
        view.findViewById(R.id.filterButton).setOnClickListener(v -> filterButton.changeFilter(filterButton.getFilter(), filterButton.getType()));
        return view;
    }

    private void openReservationForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dining_res_dialog, null);
        EditText locationInput = dialogView.findViewById(R.id.locationEditText);
        EditText timeInput = dialogView.findViewById(R.id.timeEditText);
        EditText websiteInput = dialogView.findViewById(R.id.websiteText);

        builder.setView(dialogView)
                .setTitle("New Reservation")
                .setPositiveButton("Add Reservation", (dialog, which) -> {
                    String website = websiteInput.getText().toString().trim();
                    String time = timeInput.getText().toString().trim();;
                    String location = locationInput.getText().toString().trim();;
                    if (!website.isEmpty() && !time.isEmpty() && !location.isEmpty()) {
                        diningReservationViewModel.addReservation(new DiningReservation(
                                MainActivity.getUserId(), website, location, time
                        ));
                    } else {
                        Toast.makeText(getContext(),
                                "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
