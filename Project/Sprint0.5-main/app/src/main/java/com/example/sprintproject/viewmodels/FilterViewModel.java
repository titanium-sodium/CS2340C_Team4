package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.FiltersModel;
import com.example.sprintproject.model.DiningFilterModel;
import com.example.sprintproject.model.AccommodationsFilterModel;

public class FilterViewModel {
    private boolean filter;
    private String type;
    private DiningViewModel diningViewModel;
    private AccommodationsViewModel accommodationsViewModel;
    private FiltersModel filterModel;

    public FilterViewModel(boolean filter, String type, Object viewModel) {
        this.filter = filter;
        this.type = type;

        // Initialize the appropriate filter model and viewModel based on type
        if (type.equals("Dining")) {
            this.diningViewModel = (DiningViewModel) viewModel;
            this.filterModel = new DiningFilterModel();
        } else if (type.equals("Accommodations")) {
            this.accommodationsViewModel = (AccommodationsViewModel) viewModel;
            this.filterModel = new AccommodationsFilterModel();
        }
    }

    public void changeFilter(boolean currentFilter, String filterType) {
        if (filterType.equals(this.type)) {
            // Get the new filter state from the model
            this.filter = filterModel.changeFilter(filterType);

            // Apply sorting based on the type
            if (type.equals("Dining") && diningViewModel != null) {
                applySort(diningViewModel);
            } else if (type.equals("Accommodations") && accommodationsViewModel != null) {
                applySort(accommodationsViewModel);
            }
        }
    }

    private void applySort(DiningViewModel viewModel) {
        viewModel.sortReservations(this.filter);
    }

    private void applySort(AccommodationsViewModel viewModel) {
        viewModel.sortAccommodations(this.filter);
    }

    public boolean getFilter() {
        return filter;
    }

    public String getType() {
        return type;
    }
}