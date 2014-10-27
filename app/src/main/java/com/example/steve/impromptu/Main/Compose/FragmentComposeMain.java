package com.example.steve.impromptu.Main.Compose;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.R;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeMain extends Fragment {

    ImageView vType;
    EditText vTitle;
    EditText vDescription;
    LinearLayout vStream;
    LinearLayout vPush;
    LinearLayout vTime;
    LinearLayout vLocation;
    LinearLayout vCreate;
    Event newEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_main, container, false);


        // get references to all the necessary GUI widgets
        vType = (ImageView) fragmentView.findViewById(R.id.fragComposeMain_imageView_type);
        vTitle = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_title);
        vDescription = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_description);
        vStream = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_stream);
        vPush = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_push);
        vTime = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_time);
        vLocation = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_location);
        vCreate = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_create);

        vType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select type", Toast.LENGTH_SHORT).show();

            }
        });

        vStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select stream", Toast.LENGTH_SHORT).show();

            }
        });

        vPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select push", Toast.LENGTH_SHORT).show();

            }
        });

        vTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select time", Toast.LENGTH_SHORT).show();

            }
        });

        vLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();

            }
        });

        vCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO make sure enough info is filled out

                Toast.makeText(getActivity(), "Select create", Toast.LENGTH_SHORT).show();

            }
        });

        return fragmentView;
    }

}
