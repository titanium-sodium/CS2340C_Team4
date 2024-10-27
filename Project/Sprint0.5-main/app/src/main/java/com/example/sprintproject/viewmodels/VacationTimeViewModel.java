package com.example.sprintproject.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sprintproject.model.VacationTime;
import com.example.sprintproject.model.VacationTimeRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VacationTimeViewModel extends AndroidViewModel {
    private final VacationTimeRepository repository;
    private final SimpleDateFormat dateFormat;

    // LiveData for UI updates
    private final MutableLiveData<String> calculatedValue = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<VacationTime> currentVacationTime = new MutableLiveData<>();

    public VacationTimeViewModel(Application application) {
        super(application);
        repository = VacationTimeRepository.getInstance(application);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    }

    // Getters for LiveData
    public LiveData<String> getCalculatedValue() {
        return calculatedValue;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public LiveData<VacationTime> getCurrentVacationTime() {
        return currentVacationTime;
    }

    /**
     * Calculates missing value based on two provided inputs
     * @return true if calculation was successful
     */
    public boolean calculateMissingValue(String duration, String startDate, String endDate) {
        try {
            // Case 1: Calculate end date from duration and start date
            if (!duration.isEmpty() && !startDate.isEmpty() && endDate.isEmpty()) {
                Date start = dateFormat.parse(startDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(start);
                cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(duration));
                calculatedValue.setValue(dateFormat.format(cal.getTime()));
                return true;
            }

            // Case 2: Calculate start date from duration and end date
            else if (!duration.isEmpty() && !endDate.isEmpty() && startDate.isEmpty()) {
                Date end = dateFormat.parse(endDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(duration));
                calculatedValue.setValue(dateFormat.format(cal.getTime()));
                return true;
            }

            // Case 3: Calculate duration from start and end date
            else if (!startDate.isEmpty() && !endDate.isEmpty() && duration.isEmpty()) {
                Date start = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);
                long diffInMillies = Math.abs(end.getTime() - start.getTime());
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                calculatedValue.setValue(String.valueOf(diffInDays));
                return true;
            }

            errorMessage.setValue("Please fill exactly two fields to calculate the third");
            return false;

        } catch (ParseException e) {
            errorMessage.setValue("Invalid date format. Please use MM/DD/YYYY");
            return false;
        } catch (NumberFormatException e) {
            errorMessage.setValue("Invalid duration. Please enter a number");
            return false;
        }
    }

    /**
     * Validates input and saves vacation time
     */
    public void saveVacationTime(String userId, String duration, String startDate, String endDate) {
        try {
            // Input validation
            if (duration.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                errorMessage.setValue("All fields must be filled");
                return;
            }

            // Parse and validate dates
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            int days = Integer.parseInt(duration);

            // Validate duration
            if (days <= 0) {
                errorMessage.setValue("Duration must be positive");
                return;
            }

            // Validate date order
            if (end.before(start)) {
                errorMessage.setValue("End date must be after start date");
                return;
            }

            // Create and save vacation time
            VacationTime vacationTime = new VacationTime(userId, days, startDate, endDate);
            repository.saveVacationTime(vacationTime);

            // Update LiveData
            currentVacationTime.setValue(vacationTime);
            saveSuccess.setValue(true);

        } catch (ParseException e) {
            errorMessage.setValue("Invalid date format. Please use MM/DD/YYYY");
        } catch (NumberFormatException e) {
            errorMessage.setValue("Invalid duration. Please enter a number");
        }
    }

    /**
     * Loads vacation time for a user
     */
    public void loadVacationTime(String userId) {
        VacationTime vacationTime = repository.getVacationTime(userId);
        currentVacationTime.setValue(vacationTime);
    }

    /**
     * Validates a date string format
     */
    public boolean isValidDateFormat(String date) {
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}