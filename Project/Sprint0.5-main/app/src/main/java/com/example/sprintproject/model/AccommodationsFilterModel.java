package com.example.sprintproject.model;

import com.example.sprintproject.viewmodels.AccommodationsViewModel;

public class AccommodationsFilterModel implements FiltersModel {
    private boolean filter;
    private Object viewModel;
    private static final String SORT_FIELD = "checkInDate";

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
        if (viewModel instanceof AccommodationsViewModel) {
            this.viewModel = viewModel;
        } else {
            throw new IllegalArgumentException("ViewModel must be of type AccommodationsViewModel");
        }
    }

    @Override
    public void applyFilter(boolean currentFilter, String filterType) {
        if (viewModel instanceof AccommodationsViewModel && filterType.equals(getType())) {
            AccommodationsViewModel accommodationsViewModel = (AccommodationsViewModel) viewModel;
            accommodationsViewModel.sortAccommodations(currentFilter);
        }
    }
}