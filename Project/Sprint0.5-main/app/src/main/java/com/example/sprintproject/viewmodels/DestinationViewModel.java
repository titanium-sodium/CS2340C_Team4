package com.example.sprintproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprintproject.model.DestinationsRepository;
import com.example.sprintproject.model.UserModel;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;


public class DestinationViewModel extends ViewModel {
    private final MutableLiveData<Long> startDate = new MutableLiveData<>();
    private final MutableLiveData<Long> endDate = new MutableLiveData<>();
//    private final DestinationsRepository repository; // For database operations

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

    private void calculateDuration() {
        Long start = startDate.getValue();
        Long end = endDate.getValue();

        if (start != null && end != null) {
            // Calculate duration in days
            long durationInMillis = end - start;
            int durationInDays = (int) (durationInMillis / (1000 * 60 * 60 * 24));
            // Update duration in repository or LiveData as needed
        }
    }

    public boolean validateDates() {
        Long start = startDate.getValue();
        Long end = endDate.getValue();

        if (start == null || end == null) {
            return false;
        }

        return start <= end;
    }
}