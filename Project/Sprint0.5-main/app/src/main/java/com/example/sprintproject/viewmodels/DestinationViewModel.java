package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.UserModel;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class DestinationViewModel extends ViewModel {
    private final MutableLiveData<Long> startDate = new MutableLiveData<>();
    private final MutableLiveData<Long> endDate = new MutableLiveData<>();
    private final MutableLiveData<Integer> duration = new MutableLiveData<>();

    public DestinationViewModel() {
         // Assuming Singleton pattern
    }

    public LiveData<Long> getStartDate() {
        return startDate;
    }

    public LiveData<Long> getEndDate() {
        return endDate;
    }

    public void setStartDate(Long date) {
        startDate.setValue(date);
        calculateDuration();
    }

    public void setEndDate(Long date) {
        endDate.setValue(date);
        calculateDuration();
    }

    public void calculateStartDate(Integer durationDays, Long endDate) {
        if (durationDays != null && endDate != null) {
            // Calculate start date by subtracting duration from end date
            long startMillis = endDate - TimeUnit.DAYS.toMillis(durationDays);
            startDate.setValue(startMillis);
        }
    }

    public void calculateEndDate(Integer durationDays, Long startDate) {
        if (durationDays != null && startDate != null) {
            // Calculate end date by adding duration to start date
            long endMillis = startDate + TimeUnit.DAYS.toMillis(durationDays);
            endDate.setValue(endMillis);
        }
    }


    public LiveData<Integer> getDuration() {
        return duration;
    }

    // Modify existing calculateDuration to store the value
    private void calculateDuration() {
        Long start = startDate.getValue();
        Long end = endDate.getValue();

        if (start != null && end != null) {
            // Calculate duration in days
            long durationInMillis = end - start;
            int durationInDays = (int) (durationInMillis / (1000 * 60 * 60 * 24));
            duration.setValue(durationInDays);
        }
    }

    // Add setter for duration
    public void setDuration(Integer days) {
        duration.setValue(days);
    }

    // Modify existing validation to include duration calculations
    public boolean validateDates() {
        Long start = startDate.getValue();
        Long end = endDate.getValue();
        Integer durationDays = duration.getValue();

        if (durationDays != null) {
            if (start == null && end != null) {
                calculateStartDate(durationDays, end);
                return true;
            }
            if (end == null && start != null) {
                calculateEndDate(durationDays, start);
                return true;
            }
        }

        if (start == null || end == null) {
            return false;
        }

        return start <= end;
    }
}