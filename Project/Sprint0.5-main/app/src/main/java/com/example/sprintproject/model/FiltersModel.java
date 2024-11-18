package com.example.sprintproject.model;

public interface FiltersModel {
    /**
     * Gets the current filter state
     * @return true if sorting ascending, false if descending
     */
    boolean getFilter();

    /**
     * Gets the type of filter (e.g., "Dining" or "Accommodations")
     * @return String representing the filter type
     */
    String getType();

    /**
     * Changes the filter state and returns the new state
     * @param filterType The type of filter being changed
     * @return new filter state after change
     */
    boolean changeFilter(String filterType);

    /**
     * Gets the field name to sort by in Firebase
     * @return String representing the field name to sort by
     */
    String getSortField();

    /**
     * Gets the associated ViewModel for the filter
     * @return Object representing the ViewModel (must be cast to appropriate type)
     */
    Object getViewModel();

    /**
     * Sets the associated ViewModel for the filter
     * @param viewModel The ViewModel to associate with this filter
     */
    void setViewModel(Object viewModel);

    /**
     * Applies the current filter to the dataset
     * @param currentFilter The current filter state
     * @param filterType The type of filter to apply
     */
    void applyFilter(boolean currentFilter, String filterType);
}