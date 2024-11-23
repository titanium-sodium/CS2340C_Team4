package com.example.sprintproject.model;

import com.example.sprintproject.viewmodels.DiningViewModel;

public class DiningFilterModel implements FiltersModel {
    private boolean filter;
    private DiningViewModel viewModel;
    private static final String SORT_FIELD = "reservationTimestamp";

    public DiningFilterModel() {
        this.filter = true;
    }

    @Override
    public void applyFilter(boolean currentFilter, String filterType) {
        if (viewModel != null && filterType.equals(getType())) {
            // Apply the sort directly to the ViewModel
            viewModel.sortReservations(currentFilter);
        }
    }

    @Override
    public void setViewModel(Object viewModel) {
        if (viewModel instanceof DiningViewModel) {
            this.viewModel = (DiningViewModel) viewModel;
        } else {
            throw new IllegalArgumentException("ViewModel must be of type DiningViewModel");
        }
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
}