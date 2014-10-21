package com.example.steve.impromptu.Main.Compose;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.steve.impromptu.R;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeMain extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_main, container, false);
    }

}
