package com.example.sprintproject;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sprintproject.model.AccommodationsFilterModel;
import com.example.sprintproject.model.AccommodationsModel;
import com.example.sprintproject.model.CommunityPost;
import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DiningFilterModel;
import com.example.sprintproject.model.DiningReservation;
import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.model.TravelStats;
import com.example.sprintproject.model.UserModel;
import com.example.sprintproject.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;

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

    //------------------------------------SPRINT 3-------------------------------------//

    //------------------------------------AddDiningReservation-------------------------------------//
    @Test
    public void testAddDiningReservation() {

        DiningReservation diningReservation = new DiningReservation("SwawS", "SuS",
                "www.meem.com", "SaaS", "4:20", 100L);

        assertEquals("SwawS", diningReservation.getUserId());
        assertEquals("SuS", diningReservation.getTripId());
        assertEquals("www.meem.com", diningReservation.getWebsite());
        assertEquals("SaaS", diningReservation.getLocation());
        assertEquals(100L, diningReservation.getDate());

    }

    //---------------------------------------ChangeFilter------------------------------------------//

    @Test
    public void testChangeFilterCorrectType() {

        AccommodationsFilterModel accommodationsFilterModel = new AccommodationsFilterModel();
        assertFalse(accommodationsFilterModel.changeFilter("Accommodations"));

    }

    @Test
    public void testChangeFilterTypeMismatch() {

        DiningFilterModel diningFilterModel = new DiningFilterModel();
        assertTrue(diningFilterModel.changeFilter("Accommodations"));

    }

    //---------------------------------------DiningReservation------------------------------------------//

    @Test
    public void testEmptyLocation() {
        try {
            DiningReservation res = new DiningReservation("123", "abc",
                    "coolmath.com", "","5", 5L);
            fail("Reservation must include a location.");
        } catch (IllegalArgumentException e) {
            assertEquals("Reservation must include a location.", e.getMessage());
        }

    }

    @Test
    public void testEmptyTime() {
        try {
            DiningReservation res = new DiningReservation("123", "abc",
                    "coolmath.com", "Papa John's", "", 0);
            fail("Reservation must include a time.");
        } catch (IllegalArgumentException e) {
            assertEquals("Reservation must include a time.", e.getMessage());
        }

    }

    @Test
    public void testValidDiningReservation() {
        DiningReservation res = new DiningReservation("123", "abc",
                "coolmath.com", "Papa John's", "5", 5L);
        assertEquals("123", res.getUserId());
        assertEquals("coolmath.com", res.getWebsite());
        assertEquals("Papa John's", res.getLocation());
        assertEquals(5L, res.getDate());
    }

    @Test
    public void testAllFieldsDiningReservation() {
        ArrayList<String> notes = new ArrayList<>();
        notes.add("Vegan Friendly");
        ArrayList<String> contributors = new ArrayList<>();
        contributors.add("joni");
        DiningReservation res = new DiningReservation("123", "abc",
                "coolmath.com", "Papa John's","5", 110824);
        assertEquals(110824, res.getDate());
    }

    //---------------------------------------addAccommodations------------------------------------------//

    @Test
    public void testAddAccommodation() {
        AccommodationsModel accommodationsModel = new AccommodationsModel("345", "678",
                2, "Double", "Atlanta");
        accommodationsModel.setWebsite("website.com");

        assertEquals("345", accommodationsModel.getCheckInDate());
        assertEquals("678", accommodationsModel.getCheckOutDate());
        assertEquals(2, accommodationsModel.getNumberOfRooms());
        assertEquals("Double", accommodationsModel.getRoomType());
        assertEquals("Atlanta", accommodationsModel.getLocation());
        assertEquals("website.com", accommodationsModel.getWebsite());
    }

    //---------------------------------------AccommodationsModel------------------------------------------//

    @Test
    public void testEmptyLocationAccommodation() {
        try {
            AccommodationsModel accommodationsModel = new AccommodationsModel("123", "456", 1, "Single", "");
            fail("Accommodation must include a location.");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid location", e.getMessage());
        }
    }

    @Test
    public void testEmptyCheckInDate() {
        try {
            AccommodationsModel accommodationsModel = new AccommodationsModel("", "0000", 2, "Double", "Somewhere");
            fail("Accommodation must include a check-in date.");
        } catch (IllegalArgumentException e) {
            assertEquals("Dates cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testEmptyCheckOutDate() {
        try {
            AccommodationsModel res = new AccommodationsModel("9999", "", 3, "Suite", "Somewhere");
            fail("Accommodation must include a check-out date.");
        } catch (IllegalArgumentException e) {
            assertEquals("Dates cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testValidAccommodation() {
        AccommodationsModel accommodationsModel = new AccommodationsModel("123", "456", 1, "Single", "Somewhere");
        assertEquals("123", accommodationsModel.getCheckInDate());
        assertEquals("456", accommodationsModel.getCheckOutDate());
        assertEquals(1, accommodationsModel.getNumberOfRooms());
        assertEquals("Single", accommodationsModel.getRoomType());
        assertEquals("Somewhere", accommodationsModel.getLocation());
    }







    //---------------------------- TravelStats-------------------------------------------------//
    @Test
    public void testSetAndGetAllottedDays() {//DONE
        TravelStats stats = new TravelStats();
        stats.setAllottedDays(10);
        assertEquals(10, stats.getAllottedDays());
    }

    @Test
    public void testSetAndGetPlannedDays() {//DONE
        TravelStats stats = new TravelStats();
        stats.setPlannedDays(5);
        assertEquals(5, stats.getPlannedDays());
    }

    @Test
    public void testSetAndGetRemainingDays() {//DONE
        TravelStats stats = new TravelStats();
        stats.setRemainingDays(2);
        assertEquals(2, stats.getRemainingDays());
    }

    @Test
    public void testTravelStats() { //DONE
        TravelStats stats = new TravelStats(3, 2);
        assertEquals(3, stats.getAllottedDays());
        assertEquals(2, stats.getPlannedDays());
    }


    @Test
    public void testPlannedDaysExceedAllottedDays() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TravelStats(3, 5); // plannedDays > allottedDays
        });
        assertEquals("Cannot have more planned days than total trip days", exception.getMessage());
    }

    //---------------------------------CommunityPost---------------------------------------//
    @Test
    public void testEmptyPost() {

        CommunityPost emptyPost = new CommunityPost();

        assertNull(emptyPost.getPostId());
        assertNull(emptyPost.getAccommodations());
        assertNull(emptyPost.getDestinations());
        assertNull(emptyPost.getDuration());
        assertNull(emptyPost.getDiningReservations());
        assertNull(emptyPost.getNotes());
        assertNull(emptyPost.getTripId());
        assertNull(emptyPost.getUserId());

    }

    @Test
    public void testSingleFieldPost(){

        CommunityPost singleFieldPost = new CommunityPost();

        singleFieldPost.setPostId("SwawS");

        assertEquals("SwawS",singleFieldPost.getPostId());
        assertNull(singleFieldPost.getAccommodations());
        assertNull(singleFieldPost.getDestinations());
        assertNull(singleFieldPost.getDuration());
        assertNull(singleFieldPost.getDiningReservations());
        assertNull(singleFieldPost.getNotes());
        assertNull(singleFieldPost.getTripId());
        assertNull(singleFieldPost.getUserId());

    }

    @Test
    public void setGetAccommodations() {

        CommunityPost accommodationsPost = new CommunityPost();

        HashMap<String, String> accommodations = new HashMap<>();
        accommodations.put("MeeM", "SooS");
        accommodationsPost.setAccommodations(accommodations);

        assertTrue(accommodationsPost.getAccommodations().containsKey("MeeM")
                && accommodationsPost.getAccommodations().containsValue("SooS"));

    }

    @Test
    public void testFullPost() {

        CommunityPost fullPost = new CommunityPost();

        fullPost.setPostId("SwawS");
        fullPost.setDuration("SuS");
        fullPost.setUserId("SeeS");
        fullPost.setTripId("SaaS");

        HashMap<String, String> accommodations = new HashMap<>();
        accommodations.put("MeeM", "SooS");
        fullPost.setAccommodations(accommodations);

        ArrayList<String> destinations = new ArrayList<String>();
        destinations.add("SiiS");
        fullPost.setDestinations(destinations);

        HashMap<String,String> diningReservations = new HashMap<>();
        diningReservations.put("KeK", "Swooce");
        fullPost.setDiningReservations(diningReservations);

        assertEquals("SwawS", fullPost.getPostId());
        assertEquals("SuS", fullPost.getDuration());
        assertEquals("SeeS", fullPost.getUserId());
        assertEquals("SaaS", fullPost.getTripId());
        assertTrue(fullPost.getAccommodations().containsKey("MeeM")
                && fullPost.getAccommodations().containsValue("SooS"));
        assertTrue(fullPost.getDestinations().contains("SiiS"));
        assertTrue(fullPost.getDiningReservations().containsKey("KeK")
                && fullPost.getDiningReservations().containsValue("Swooce"));
        
    }

}