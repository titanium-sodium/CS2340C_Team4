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
    public void changeFilter(String type) {
        System.out.println("Do stuff");
        this.filter = !filter;
    }

}
