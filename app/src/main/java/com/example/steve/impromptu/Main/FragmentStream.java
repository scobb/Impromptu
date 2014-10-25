package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.steve.impromptu.R;

/**
 * Created by Stephen Arifin on 10/16/14.
 */

public class FragmentStream extends ListFragment {

    String[] people = new String[] {"Griffin", "Stevie Wonder", "Ben Marsh the Darsh",
        "Big Dog Billings", "Papa Holley", "Guilty Hilti", "Haydensity", "McCombs O`Neil",
        "Big Fag Taylor"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1, people);

        // Setting the list adapter for the ListFragment
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
