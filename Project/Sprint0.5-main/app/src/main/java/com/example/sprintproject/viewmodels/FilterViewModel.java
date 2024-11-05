package com.example.sprintproject.viewmodels;


public class FilterViewModel {
    private boolean filter;
    private String type;
    public FilterViewModel(boolean filter, String type) {
        this.filter = filter;
        this.type = type;
    }

    public void changeFilter(boolean filter, String type) {
        //TODO
    }
    public boolean getFilter()  {
        return filter;
    }
    public String getType() {
        return type;
    }
}
