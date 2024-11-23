package com.example.sprintproject.viewmodels;

import com.example.sprintproject.model.AccommodationsFilterModel;
import com.example.sprintproject.model.DiningFilterModel;
import com.example.sprintproject.model.FiltersModel;

public class FilterViewModel {
    private boolean filter;
    private String type;
    private DiningViewModel diningViewModel;
    private AccommodationsViewModel accommodationsViewModel;
    private FiltersModel filterModel;

    public FilterViewModel(boolean filter, String type, Object viewModel) {
        this.filter = filter;
        this.type = type;

        if (type.equals("Dining")) {
            this.diningViewModel = (DiningViewModel) viewModel;
            this.filterModel = new DiningFilterModel();
            this.filterModel.setViewModel(this.diningViewModel);
        } else if (type.equals("Accommodations")) {
            this.accommodationsViewModel = (AccommodationsViewModel) viewModel;
            this.filterModel = new AccommodationsFilterModel();
            this.filterModel.setViewModel(this.accommodationsViewModel);
        }
    }

    public void changeFilter(boolean currentFilter, String filterType) {
        if (filterType.equals(this.type)) {
            this.filter = !currentFilter;
            this.filterModel.applyFilter(this.filter, filterType);
        }
    }

    // Add getter method for filter state
    public boolean getFilter() {
        return this.filter;
    }

    public String getType() {
        return type;
    }
}