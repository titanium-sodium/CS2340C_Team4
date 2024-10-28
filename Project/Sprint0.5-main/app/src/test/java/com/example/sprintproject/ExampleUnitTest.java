package com.example.sprintproject;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DestinationsRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //----------------------------------------------AddDestination-----------------------------------------------------//
    public void testAddDestination() {
        DestinationsRepository repository = DestinationsRepository.getInstance(); //simply create a new instance of DestinationsRepository
        //create a instance of DestinationModel with start date and end date along with some location
        DestinationModel destination = new DestinationModel("2024-10-01", "2024-10-07","New York");

        //adds a new destination along with a personal user ID. Is a task object bc addDestination returns a Task object
        Task<String> task = repository.addDestination(destination, "user1");
        //this is a listener that waits for the task to finish. When the task is done, the code inside the brackets
        //will process and determine if the code was successful or not.
        task.addOnCompleteListener(result -> {
            //If the result is true, then that means that destination and userid was added to the database without error
            assertTrue(result.isSuccessful());
            //result.getResult() gets the result of the task. If the result is null, then something went wrong and no
            //ID was made for the destination.
            assertNotNull(result.getResult());//check if the generated id is not null
        });
    }



    public void testAddDestinationWithEmptyLocation() {
        DestinationsRepository repository = DestinationsRepository.getInstance();
        DestinationModel destination = new DestinationModel("2024-10-01", "2024-10-07","   ");

        Task<String> task = repository.addDestination(destination, "user1");
        task.addOnCompleteListener(result -> {
            assertFalse(result.isSuccessful());//if the task is successful when there is whitespace in the location then
            //the test case will say that this is false as the task IS NOT supposed to work if there are any whitespaces
            //btw these have to be check BEFORE firebase handles the test case as firebase itself does not check for whitespace
            //parameters
        });
    }

    public void testAddDestinationEmptyStartDate() {
        DestinationsRepository repository = DestinationsRepository.getInstance();
        DestinationModel destination = new DestinationModel("   ", "2024-10-07","New York");

        Task<String> task = repository.addDestination(destination, "user1");
        task.addOnCompleteListener(result -> {
            assertFalse(result.isSuccessful());//if the task is successful when there is whitespace in the start date then
            //the test case will say that this is false as the task IS NOT supposed to work if there are any whitespaces
            //btw these have to be check BEFORE firebase handles the test case as firebase itself does not check for whitespace
            //parameters
        });
    }

    public void testAddDestinationEmptyEndDate() {
        DestinationsRepository repository = DestinationsRepository.getInstance();
        DestinationModel destination = new DestinationModel("2024-10-01", "   ","New York");

        Task<String> task = repository.addDestination(destination, "user1");
        task.addOnCompleteListener(result -> {
            assertFalse(result.isSuccessful());//if the task is successful when there is whitespace in the end date then
            //the test case will say that this is false as the task IS NOT supposed to work if there are any whitespaces
            //btw these have to be check BEFORE firebase handles the test case as firebase itself does not check for whitespace
            //parameters
        });
    }

    public void testAddDestinationEmptyAll() {
        DestinationsRepository repository = DestinationsRepository.getInstance();
        DestinationModel destination = new DestinationModel("   ", "   ","   ");

        Task<String> task = repository.addDestination(destination, "user1");
        task.addOnCompleteListener(result -> {
            assertFalse(result.isSuccessful());//if the task is successful when there is whitespace in the all the parameters then
            //the test case will say that this is false as the task IS NOT supposed to work if there are any whitespaces
        });
    }

    //----------------------------------------------getAllDestination-----------------------------------------------------//

    public void testGetAllDestination() {
        //create new instance of respository
        String userID = "user1";
        DestinationsRepository repository = DestinationsRepository.getInstance();
        DestinationModel destination1 = new DestinationModel("2024-07-01", "2024-07-14", "Kyoto");
        repository.addDestination(destination1, userID).addOnCompleteListener(adding -> {
            //make sure destinations is added to userID
            assertTrue(adding.isSuccessful());
            //get all destinations and put it within task list
            Task<List<DestinationModel>> task = repository.getAllDestinations(userID);
            //wait for task to finish
            task.addOnCompleteListener(result -> {
                //true if task is successful
                assertTrue(result.isSuccessful());
                //get results and put it into list of DestinationModel
                List<DestinationModel> allDestinations = result.getResult();
                //checks if list is null or not
                assertNotNull(allDestinations);
                //checks if list if empty
                assertFalse(allDestinations.isEmpty());

                //make sure items in allDestinations match what is supposed to be in location, start date, and end date
                boolean found = false;
                for (DestinationModel destinations : allDestinations) {
                    if(destinations.getLocation().equals("Kyoto") && (destinations.getStartDate().equals("2024-07-01") &&
                            destinations.getEndDate().equals("2024-07-14")) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            });
        });


    }

    //----------------------------------------------getTotalDays-----------------------------------------------------//

    public void testGetTotalDays() {
        //create new instance of respository
        DestinationsRepository repository = DestinationsRepository.getInstance();
        //create new instance of userID
        String userID = "user1";
        //create a destination with date and location
        DestinationModel destination = new DestinationModel("2024-10-01", "2024-10-07", "Paris"); // 6 days

        repository.addDestination(destination, userID).addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful());//puts out true if addDestination works
            //we then test out totalTripDays
            repository.getTotalTripDays(userID).addOnCompleteListener(result -> {
                //if task is successful, then TotalTripDays works
                assertTrue(result.isSuccessful());
                //we put the total days into totalDays
                Integer totalDays = result.getResult();
                //make sure totalDays is not null
                assertNotNull(totalDays);
                //make sure the actual total days is the same as the expected
                assertEquals(6, totalDays.intValue());
            });
        });
    }

    //----------------------------------------------updateDestination-----------------------------------------------------//

    public void testUpdateDestination() {
        //creates a new instance of DestinationsRepository
        DestinationsRepository repository = DestinationsRepository.getInstance();
        //creates a DestinationModel instance with a valid start date, end date, and location
        DestinationModel destination = new DestinationModel(20240205, 20240212, "Los Angeles");

        //adds destination to the repository for a specific user ID to ensure it exists before updating
        repository.addDestination(destination, "user1").addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful()); // ensures the destination was added successfully

            //updates the destination with new values
            destination.setLocation("San Francisco");
            destination.setStartDate(20240208);
            destination.setEndDate(20240215);

            //calls the updateDestination method to update the existing destination
            Task<String> updateTask = repository.updateDestination(destination, "user1");
            updateTask.addOnCompleteListener(result -> {
                //asserts that the update task completed successfully
                assertTrue(result.isSuccessful());
            });
        });
    }

    public void testUpdateDestinationWithEmptyLocation() {
        //creates a new instance of the DestinationsRepository
        DestinationsRepository repository = DestinationsRepository.getInstance();
        //creates a DestinationModel instance with a valid start date, end date, and location
        DestinationModel destination = new DestinationModel(20240801, 20240807, "Seattle");

        //adds the destination to ensure it exists
        repository.addDestination(destination, "user1").addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful()); // ensures the destination was added successfully

            //tries updating the destination with an empty location
            destination.setLocation("   ");
            Task<String> updateTask = repository.updateDestination(destination, "user1");
            updateTask.addOnCompleteListener(result -> {
                //assert that the update should fail
                assertFalse(result.isSuccessful());
            });
        });
    }


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
