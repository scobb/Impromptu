package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.ActivityMain;
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
    public Event myEvent;

    OnComposeMainFinishedListener composeMainFinishedListenerCallback;
    OnAttributeSelectedListener attributeSelectedListenerCallback;

    // Container Activity must implement this interface
    public interface OnComposeMainFinishedListener {
        public void onComposeMainFinished(Boolean create);
    }

    public interface OnAttributeSelectedListener {
        public void OnAttributeSelected(String attribute);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_main, container, false);

        ActivityMain myActivity = (ActivityMain) getActivity();
        myEvent = myActivity.getNewEvent();

        // get references to all the necessary GUI widgets
        vType = (ImageView) fragmentView.findViewById(R.id.fragComposeMain_imageView_type);
        vTitle = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_title);
        vDescription = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_description);
        vStream = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_stream);
        vPush = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_push);
        vTime = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_time);
        vLocation = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_location);
        vCreate = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_create);

        if (myEvent != null) {
            String eventType = myEvent.getType();
            String eventTitle = myEvent.getTitle();
            String eventDescription = myEvent.getDescription();

            if (eventType != null) {
                // vType.setImageResource(blah);
            }
            if (eventTitle != null) {
                vTitle.setText(eventTitle);
            }
            if (eventDescription != null) {
                vDescription.setText(eventDescription);
            }
        }

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

                String attribute = "push";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);

            }
        });

        vTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attribute = "time";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);
            }
        });

        vLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                String attribute = "location";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);

            }
        });

        vCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO make sure enough info is filled out
                // change event's creation time
                // call onComposeMainFinishedCallback

                Toast.makeText(getActivity(), "Select create", Toast.LENGTH_SHORT).show();

            }
        });

        vDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                myEvent.setDescription(editable.toString());

            }
        });

        vTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                myEvent.setTitle(editable.toString());

            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            composeMainFinishedListenerCallback = (OnComposeMainFinishedListener) activity;
            attributeSelectedListenerCallback = (OnAttributeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement all listeners");
        }
    }

}
