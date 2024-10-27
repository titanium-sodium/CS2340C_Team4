package com.example.sprintproject;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sprintproject.model.DestinationModel;
import com.example.sprintproject.model.DestinationsRepository;
import com.google.android.gms.tasks.Task;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public void testAddDestination() {
        DestinationsRepository repository = DestinationsRepository.getInstance(); //simply create a new instance of DestinationsRepository
        //create a instance of DestinationModel with start date and end date along with some location
        DestinationModel destination = new DestinationModel("2024-10-01", "2024-10-07","North Korea");

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






    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}