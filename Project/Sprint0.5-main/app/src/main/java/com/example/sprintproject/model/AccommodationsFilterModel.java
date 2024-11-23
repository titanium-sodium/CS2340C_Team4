package com.example.sprintproject.model;


import com.example.sprintproject.viewmodels.AccommodationsViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccommodationsFilterModel implements FiltersModel {
    private boolean filter;
    private Object viewModel;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private static final String[] SORT_FIELDS = {"checkInDate", "checkOutDate", "duration"};
    private int currentSortFieldIndex = 0;

    public AccommodationsFilterModel() {
        this.filter = true;
    }

    @Override
    public boolean getFilter() {
        return filter;
    }

    @Override
    public String getType() {
        return "Accommodations";
    }

    @Override
    public boolean changeFilter(String filterType) {
        if (filterType.equals(getType())) {
            filter = !filter;
            // Cycle through sort fields when filter changes
            currentSortFieldIndex = (currentSortFieldIndex + 1) % SORT_FIELDS.length;
            applyFilter(filter, filterType);
        }
        return filter;
    }

    @Override
    public String getSortField() {
        return SORT_FIELDS[currentSortFieldIndex];
    }

    @Override
    public Object getViewModel() {
        return viewModel;
    }

    @Override
    public void setViewModel(Object viewModel) {
        if (viewModel instanceof AccommodationsViewModel) {
            this.viewModel = viewModel;
        } else {
            throw new IllegalArgumentException("ViewModel must be of type AccommodationsViewModel");
        }
    }

    public static int compareAccommodationDates(String date1, String date2) {
        try {
            Date d1 = SDF.parse(date1);
            Date d2 = SDF.parse(date2);
            if (d1 != null && d2 != null) {
                return d1.compareTo(d2);
            }
        } catch (ParseException e) {
            // If parsing fails, fall back to string comparison
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }

    @Override
    public void applyFilter(boolean currentFilter, String filterType) {
        if (viewModel instanceof AccommodationsViewModel && filterType.equals(getType())) {
            AccommodationsViewModel accommodationsViewModel = (AccommodationsViewModel) viewModel;

            // Update the sort field and direction in ViewModel
            accommodationsViewModel.setSortOrder(currentFilter, getSortField());

            // Trigger the sort
            accommodationsViewModel.sortAccommodations(currentFilter);
        }
    }

    // Get descriptive text for current sort state
    public String getSortDescription() {
        String direction = filter ? "Ascending" : "Descending";
        String field = SORT_FIELDS[currentSortFieldIndex];
        switch (field) {
        case "checkInDate":
            return "Check-in Date " + direction;
        case "checkOutDate":
            return "Check-out Date " + direction;
        case "duration":
            return "Duration " + direction;
        default:
            return "Sorted " + direction;
        }
    }
}