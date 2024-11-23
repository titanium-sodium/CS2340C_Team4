package com.example.sprintproject.model;

import com.example.sprintproject.viewmodels.DiningViewModel;

public class DiningFilterModel implements FiltersModel {
    private boolean filter;
    private Object viewModel;
    private static final String SORT_FIELD = "reservationTimestamp";
    // Updated to match new field name

    public DiningFilterModel() {
        this.filter = true;
        // true means ascending (oldest first), false means descending (newest first)
    }

    @Override
    public boolean getFilter() {
        return filter;
    }

    @Override
    public String getType() {
        return "Dining";
    }

    @Override
    public boolean changeFilter(String filterType) {
        if (filterType.equals(getType())) {
            filter = !filter;
            applyFilter(filter, filterType);
        }
        return filter;
    }

    @Override
    public String getSortField() {
        return SORT_FIELD;
    }

    @Override
    public Object getViewModel() {
        return viewModel;
    }

    @Override
    public void setViewModel(Object viewModel) {
        if (viewModel instanceof DiningViewModel) {
            this.viewModel = viewModel;
        } else {
            throw new IllegalArgumentException("ViewModel must be of type DiningViewModel");
        }
    }

    @Override
    public void applyFilter(boolean currentFilter, String filterType) {
        if (viewModel instanceof DiningViewModel && filterType.equals(getType())) {
            DiningViewModel diningViewModel = (DiningViewModel) viewModel;
            // When filter is false, sort descending (newest first)
            // When filter is true, sort ascending (oldest first)
            diningViewModel.sortReservations(currentFilter);
        }
    }
}