package com.example.sprintproject.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sprintproject.R;
import com.example.sprintproject.viewmodels.DestinationAdapter;

import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DestinationsPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String userId;


    public DestinationsPage(String userId) {
        this.userId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(userId);
        if (getArguments() != null) {
           //temp
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment. Aka rendering the value onto the destination screen
        View view = inflater.inflate(R.layout.destination_screen, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //just some sample data to put onto the rows
        List<String> destinations = Arrays.asList("Atlanta", "New York", "Tokyo", "Paris");
        List<Integer> daysPlanned = Arrays.asList(5, 3, 7, 4);

        //we set up the adaptor with the data
        DestinationAdapter destinationAdapter = new DestinationAdapter(destinations, daysPlanned);
        //where we attach our adaptor to recyclerView
        recyclerView.setAdapter(destinationAdapter);

        return view;
    }
}