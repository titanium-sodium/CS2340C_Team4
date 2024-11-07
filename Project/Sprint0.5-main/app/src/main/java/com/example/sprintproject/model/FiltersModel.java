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
     * Changes the filter state and handles the sort order
     * @param type The type of filter being changed
     * @return new filter state after change
     */
    boolean changeFilter(String type);

    /**
     * Gets the field name to sort by in Firebase
     * @return String representing the field name to sort by
     */
    String getSortField();
}