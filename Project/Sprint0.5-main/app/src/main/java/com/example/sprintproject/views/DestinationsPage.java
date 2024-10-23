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
 * Use the {@link DestinationsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationsPage extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DestinationsPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DestinationsPage.
     */
    public static DestinationsPage newInstance(String param1, String param2) {
        DestinationsPage fragment = new DestinationsPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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