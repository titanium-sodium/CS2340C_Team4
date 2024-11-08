package com.example.sprintproject.model;

public class AccommodationsFilterModel implements FiltersModel {
    private boolean filter;

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
    public boolean changeFilter(String type) {
        if (type.equals(getType())) {
            filter = !filter;
        }
        return filter;
    }

    @Override
    public String getSortField() {
        return "checkInDate";  // The field name in Firebase to sort by
    }
}