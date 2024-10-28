package com.example.sprintproject;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sprintproject.model.AuthModel;
import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DestinationsRepository;
import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.viewmodels.DestinationViewModel;
import com.example.sprintproject.viewmodels.UserViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //----------------------------------------------AddDestination-----------------------------------------------------//
    @Test
    public void testAddDestination() {
        DestinationModel destination = new DestinationModel(123, 123, "Atlanta");
        assertEquals("Atlanta", destination.getLocation());
    }


    @Test
    public void testAddDestinationWithEmptyLocation() {
        try {
            DestinationModel destination = new DestinationModel(123, 123, "");
            fail("Cannot make a destination with no location");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid location", e.getMessage());
        }


    }
    @Test
    public void testAddDestinationEmptyStartDate() {
        try {
            DestinationModel destination = new DestinationModel(0, 10000, "Atlanta");
            fail("Cannot make a destination with no date");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid date", e.getMessage());
        }
    }
    @Test
    public void testAddDestinationEmptyEndDate() {
        try {
            DestinationModel destination = new DestinationModel(10000, 0, "Atlanta");
            fail("Cannot make a destination with no date");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid date", e.getMessage());
        }
    }
    @Test
    public void testAddDestinationEmptyAll() {
        try {
            DestinationModel destination = new DestinationModel(0, 0, "");
            fail("Cannot make a destination with no location");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid location", e.getMessage());
        }
    }
//
//    //----------------------------------------------getAllDestination-----------------------------------------------------//
//
    @Test
    public void testUserModelEmptyEmail() {
        try {
            UserModel userModel = new UserModel("abcdf", "");
            fail("Cannot make user email empty");
        } catch (IllegalArgumentException e) {
            assertEquals("email cannot be empty", e.getMessage());
        }

    }
    @Test
    public void testUserModelEmptyUserId() {

        try {
            UserModel userModel = new UserModel("", "test@test.com");
            fail("Cannot make userId empty");
        } catch (IllegalArgumentException e) {
            assertEquals("userId cannot be empty", e.getMessage());
        }
    }
//
//    //----------------------------------------------getTotalDays-----------------------------------------------------//
//
    @Test
    public void testGetTotalDays() {
        TravelStats travelStats = new TravelStats(12, 10);
        assertEquals(12, travelStats.getAllottedDays());
    }
//
//    //----------------------------------------------updateDestination-----------------------------------------------------//
//
    @Test
    public void testEmptyNote() {
        try {
            NotesModel notesModel = new NotesModel("");
            fail("Cannot make note empty");
        } catch (IllegalArgumentException e) {
            assertEquals("Note cannot be empty", e.getMessage());
        }

    }
    @Test
    public void testValidUserModel() {
        UserModel userModel = new UserModel("kjnsfejknfwkn", "test@test.com");
        assertEquals("kjnsfejknfwkn", userModel.getUserId());
    }

//    //----------------------------------------------deleteDestination-----------------------------------------------------//
//    // Joni's test cases
//
    @Test
    public void testPlannedDays() {

            TravelStats travelStats = new TravelStats();
            travelStats.setAllottedDays(100);
            travelStats.setPlannedDays(1);
            assertEquals(1, travelStats.getPlannedDays());
    }
    @Test
    public void testValidNotesModel() {
        NotesModel notesModel = new NotesModel(".");
        notesModel.setNotes("hello world");
        assertEquals("hello world", notesModel.getNotes());
    }

    @Test
    public void testUserViewModel() {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.setUserUID("abcdef");
        userViewModel.setUserEmail("test@test.com");

        UserModel userModel = new UserModel("abcdef", "test@test.com");
        assertEquals(userModel.getEmail(), userViewModel.getUserModel().getEmail());
    }


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}