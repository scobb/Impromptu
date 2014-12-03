package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;

import java.util.List;


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
    TextView vLocationPrompt;
    TextView vStreamPrompt;
    TextView vPushPrompt;
    TextView vTimePrompt;
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
        myEvent = myActivity.getComposeEvent();

        // get references to all the necessary GUI widgets
        vType = (ImageView) fragmentView.findViewById(R.id.fragComposeMain_imageView_type);
        vTitle = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_title);
        vDescription = (EditText) fragmentView.findViewById(R.id.fragComposeMain_editText_description);
        vStream = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_stream);
        vStreamPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeMain_textView_streamPrompt);
        vPush = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_push);
        vPushPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeMain_textView_pushPrompt);
        vTime = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_time);
        vTimePrompt = (TextView) fragmentView.findViewById(R.id.fragComposeMain_textView_timePrompt);
        vLocation = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_location);
        vLocationPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeMain_textView_locationPrompt);
        vCreate = (LinearLayout) fragmentView.findViewById(R.id.fragComposeMain_linearLayout_create);

        vStreamPrompt.setText("Stream");
        vPushPrompt.setText("Push");
        vTimePrompt.setText("Time");
        vLocationPrompt.setText("Location");

        if (myEvent != null) {
            String eventType = myEvent.getType();
            String eventTitle = myEvent.getTitle();
            String eventDescription = myEvent.getDescription();

            if (eventType != null) {
                switch (eventType) {
                    case "Sports":
                        vType.setImageResource(R.drawable.sport_icon);
                        break;
                    case "Drinking":
                        vType.setImageResource(R.drawable.drinking);
                        break;
                    case "Eating":
                        vType.setImageResource(R.drawable.food);
                        break;
                    case "TV":
                        vType.setImageResource(R.drawable.tv);
                        break;
                    case "Studying":
                        vType.setImageResource(R.drawable.studying);
                        break;
                    case "Working Out":
                        vType.setImageResource(R.drawable.working_out);
                        break;
                    default:
                        vType.setImageResource(R.drawable.ic_launcher);
                }
            }
            if (eventTitle != null) {
                vTitle.setText(eventTitle);
            }
            if (eventDescription != null) {
                vDescription.setText(eventDescription);
            }


        }
//how do I get it to go to the select type page?
        vType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Select type", Toast.LENGTH_SHORT).show();
                String attribute = "type";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);

            }
        });

        vStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String attribute = "stream";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);

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

                String attribute = "location";
                attributeSelectedListenerCallback.OnAttributeSelected(attribute);

            }
        });

        vCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean complete = true;

                if (myEvent.getTitle() == null) {
                    complete = false;
                }

                if (myEvent.getStreamFriends() == null || myEvent.getStreamFriends().isEmpty()) {
                    complete = false;
                }

                if (myEvent.getType() == null) {
                    complete = false;
                }

                if (myEvent.getDurationHour() == -1) {
                    complete = false;
                }
                //I set complete to true so I could make events with no location
                if (myEvent.getFormattedAddress() == null) {
                    myEvent.setLocationName("Kelly's house");
                    myEvent.setFormattedAddress("Kelly's house");
                    complete = true;
                }

                if (complete) {
                    Time time = new Time();
                    time.setToNow();

                    myEvent.setOwner((ImpromptuUser) ImpromptuUser.getCurrentUser());

                    myEvent.setCreationTime(time);
                    composeMainFinishedListenerCallback.onComposeMainFinished(true);
                }
                else {
                    Toast.makeText(getActivity(), "Fill in minimum number of required fields", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    public void onResume() {
        super.onResume();

        vStreamPrompt.setText("Stream");
        vPushPrompt.setText("Push");
        vTimePrompt.setText("Time");
        vLocationPrompt.setText("Location");

        if(myEvent != null) {
            List<ImpromptuUser> friends = myEvent.getStreamFriends();

            if(friends != null && friends.size() > 0) {

                String prompt = "Stream: ";

                String prefix = " ";
                String withComma = ", ";

                for(ImpromptuUser person : friends) {

                    prompt += prefix + person.getName();

                    prefix = withComma;
                }

                vStreamPrompt.setText(prompt);
            }

            friends = myEvent.getPushFriends();

            if(friends != null && friends.size() > 0) {

                String prompt = "Push: ";

                String prefix = " ";
                String withComma = ", ";

                for(ImpromptuUser person : friends) {
                    prompt += prefix + person.getName();
                    prefix = withComma;
                }

                vPushPrompt.setText(prompt);
            }

            //TODO: refactor this check? different way of asking "is location null"?? Add function to Event?
            if (myEvent.getLocationName() != null) {
                vLocationPrompt.setText("Location:  " + myEvent.getLocationName());
            }

            //TODO: do soemthing with time prompt???
            //vTimePrompt.setText("Time: ?????????");
        }
    }

}
