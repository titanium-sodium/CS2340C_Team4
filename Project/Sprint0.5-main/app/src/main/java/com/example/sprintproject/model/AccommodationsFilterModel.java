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
    public void changeFilter(String type) {
        System.out.println("Do stuff");
        this.filter = !filter;
    }
}
