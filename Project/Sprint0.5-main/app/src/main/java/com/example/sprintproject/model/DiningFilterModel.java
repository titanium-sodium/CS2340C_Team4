package com.example.sprintproject.model;

public class DiningFilterModel implements FiltersModel {
    private boolean filter;

    public DiningFilterModel() {
        this.filter = true;
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
    public boolean changeFilter(String type) {
        if (type.equals(getType())) {
            filter = !filter;
        }
        return filter;
    }

    @Override
    public String getSortField() {
        return "date";  // The field name in Firebase to sort by
    }
}